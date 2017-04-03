/*
 * Copyright (c) 2011, Conor McDermottroe
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * - Redistributions of source code must retain the above copyright notice,
 *   this list of conditions and the following disclaimer.
 * - Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package pt.davidafsilva.ghn.service.options.osx;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/** An interface to the OS X Keychain. The names of functions and parameters
 *	will mostly match the functions listed in the <a href="http://developer.apple.com/library/mac/#documentation/Security/Reference/keychainservices/Reference/reference.html">Keychain Services Reference</a>.
 *
 *	@author Conor McDermottroe
 */
public class OSXKeychain {
	/** The singleton instance of the keychain. Lazily loaded in
	 *	{@link #getInstance()}.
	 */
	private static OSXKeychain instance;

	/** Prevent this class from being instantiated directly. */
	private OSXKeychain() {
	}

	/** Get an instance of the keychain.
	 *
	 *	@return	An instance of this class.
	 *	@throws OSXKeychainException        If it's not possible to connect to the
	 *									keychain.
	 */
	public static OSXKeychain getInstance()
            throws OSXKeychainException
	{
		if (instance == null) {
			try {
				loadSharedObject();
			} catch (IOException e) {
				throw new OSXKeychainException("Failed to load osxkeychain.so", e);
			}
			instance = new OSXKeychain();
		}
		return instance;
	}

	/** Add a non-internet password to the keychain.
	 *
	 *	@param	serviceName				The name of the service the password is
	 *									for.
	 *	@param	accountName				The account name/username for the
	 *									service.
	 *	@param	password				The password for the service.
	 *	@throws OSXKeychainException        If an error occurs when communicating
	 *									with the OS X keychain.
	 */
	public void addGenericPassword(String serviceName, String accountName, String password)
            throws OSXKeychainException
	{
		_addGenericPassword(serviceName, accountName, password);
	}

	/** Update an existing non-internet password to the keychain.
	 *
	 *	@param	serviceName				The name of the service the password is
	 *									for.
	 *	@param	accountName				The account name/username for the
	 *									service.
	 *	@param	password				The password for the service.
	 *	@throws OSXKeychainException        If an error occurs when communicating
	 *									with the OS X keychain.
	 */
	public void modifyGenericPassword(String serviceName, String accountName, String password)
            throws OSXKeychainException
	{
		_modifyGenericPassword(serviceName, accountName, password);
	}

	public Optional<String> findGenericPassword(String serviceName, String accountName)
            throws OSXKeychainException
	{
		return Optional.ofNullable(_findGenericPassword(serviceName, accountName));
	}

	/** Delete a generic password from the keychain.
	 *
	 *	@param	serviceName				The name of the service the password is
	 *									for.
	 *	@param	accountName				The account name/username for the
	 *									service.
	 *	@throws OSXKeychainException        If an error occurs when communicating
	 *									with the OS X keychain.
	 */
	public void deleteGenericPassword(String serviceName, String accountName)
            throws OSXKeychainException
	{
		_deleteGenericPassword(serviceName, accountName);
	}

	/* ************************* */
	/* JNI stuff from here down. */
	/* ************************* */

	/** See Java_pt_davidafsilva_ghn_service_options_osx_OSXKeychain__1addGenericPassword for
	 *	the implementation of this and use {@link #addGenericPassword(String,
	 *	String, String)} to call this.
	 *
	 *	@param	serviceName				The value which should be passed as the
	 *									serviceName parameter to
	 *									SecKeychainAddGenericPassword.
	 *	@param	accountName				The value which should be passed as the
	 *									accountName parameter to
	 *									SecKeychainAddGenericPassword.
	 *	@param	password				The value which should be passed as the
	 *									password parameter to
	 *									SecKeychainAddGenericPassword.
	 *	@throws OSXKeychainException        If an error occurs when communicating
	 *									with the OS X keychain.
	 */
	private native void _addGenericPassword(String serviceName, String accountName, String password)
            throws OSXKeychainException;

	/** See Java_pt_davidafsilva_ghn_service_options_osx_OSXKeychain__1modifyGenericPassword for
	 *	the implementation of this and use {@link #modifyGenericPassword(String,
	 *	String, String)} to call this.
	 *
	 *	@param	serviceName				The value which should be passed as the
	 *									serviceName parameter to
	 *									SecKeychainAddGenericPassword.
	 *	@param	accountName				The value which should be passed as the
	 *									accountName parameter to
	 *									SecKeychainAddGenericPassword.
	 *	@param	password				The value which should be passed as the
	 *									password parameter to
	 *									SecKeychainAddGenericPassword.
	 *	@throws OSXKeychainException        If an error occurs when communicating
	 *									with the OS X keychain.
	 */
	private native void _modifyGenericPassword(String serviceName, String accountName, String password)
            throws OSXKeychainException;

	/** See Java_pt_davidafsilva_ghn_service_options_osx_OSXKeychain__1findGenericPassword for
	 *	the implementation of this and use {@link #findGenericPassword(String,
	 *	String)} to call this.
	 *
	 *	@param	serviceName				The value which should be passed as the
	 *									serviceName parameter to
	 *									SecKeychainFindGenericPassword.
	 *	@param	accountName				The value for the accountName parameter
	 *									to SecKeychainFindGenericPassword.
	 *	@return							The first password which matches the
	 *									details supplied.
	 *	@throws OSXKeychainException        If an error occurs when communicating
	 *									with the OS X keychain.
	 */
	private native String _findGenericPassword(String serviceName, String accountName)
            throws OSXKeychainException;

	/** See Java_pt_davidafsilva_ghn_service_options_osx_OSXKeychain__1deleteGenericPassword for
	 *	the implementation of this and use {@link #deleteGenericPassword(String,
	 *	String)} to call this.
	 *
	 *	@param	serviceName				The value which should be passed as the
	 *									serviceName parameter to
	 *									SecKeychainFindGenericPassword in order
	 *									to find the password to delete it.
	 *	@param	accountName				The value for the accountName parameter
	 *									to SecKeychainFindGenericPassword in
	 *									order to find the password to delete it.
	 *	@throws OSXKeychainException        If an error occurs when communicating
	 *									with the OS X keychain.
	 */
	private native void _deleteGenericPassword(String serviceName, String accountName)
            throws OSXKeychainException;

	/** Load the shared object which contains the implementations for the native
	 *	methods in this class.
	 *
	 *	@throws	IOException	If the shared object could not be loaded.
	 */
	private static void loadSharedObject()
	throws IOException
	{
		// Stream the library out of the JAR
		InputStream soInJarStream = OSXKeychain.class.getResourceAsStream("/osxkeychain.so");

		// Put the library in a temp file.
		File soInTmp = File.createTempFile("osxkeychain", ".so");
		soInTmp.deleteOnExit();
		OutputStream soInTmpStream = new FileOutputStream(soInTmp);

		// Copy the .so
		byte[] buffer = new byte[4096];
		int bytesRead;
		while ((bytesRead = soInJarStream.read(buffer)) > 0) {
			soInTmpStream.write(buffer, 0, bytesRead);
		}

		// Clean up
		soInJarStream.close();
		soInTmpStream.close();

		// Now load the library
		System.load(soInTmp.getAbsolutePath());
	}

	/* ********************************* */
	/* Private utilities from here down. */
	/* ********************************* */

	/** A fixed mapping of ports to known protocols. */
	private static final Map<Integer, OSXKeychainProtocolType> PROTOCOLS;
	static {
		PROTOCOLS = new HashMap<Integer, OSXKeychainProtocolType>(32);
		PROTOCOLS.put(548, OSXKeychainProtocolType.AFP);
		PROTOCOLS.put(3020, OSXKeychainProtocolType.CIFS);
		PROTOCOLS.put(2401, OSXKeychainProtocolType.CVSpserver);
		PROTOCOLS.put(3689, OSXKeychainProtocolType.DAAP);
		PROTOCOLS.put(3031, OSXKeychainProtocolType.EPPC);
		PROTOCOLS.put(21, OSXKeychainProtocolType.FTP);
		PROTOCOLS.put(990, OSXKeychainProtocolType.FTPS);
		PROTOCOLS.put(80, OSXKeychainProtocolType.HTTP);
		PROTOCOLS.put(443, OSXKeychainProtocolType.HTTPS);
		PROTOCOLS.put(143, OSXKeychainProtocolType.IMAP);
		PROTOCOLS.put(993, OSXKeychainProtocolType.IMAPS);
		PROTOCOLS.put(631, OSXKeychainProtocolType.IPP);
		PROTOCOLS.put(6667, OSXKeychainProtocolType.IRC);
		PROTOCOLS.put(994, OSXKeychainProtocolType.IRCS);
		PROTOCOLS.put(389, OSXKeychainProtocolType.LDAP);
		PROTOCOLS.put(636, OSXKeychainProtocolType.LDAPS);
		PROTOCOLS.put(119, OSXKeychainProtocolType.NNTP);
		PROTOCOLS.put(563, OSXKeychainProtocolType.NNTPS);
		PROTOCOLS.put(110, OSXKeychainProtocolType.POP3);
		PROTOCOLS.put(995, OSXKeychainProtocolType.POP3S);
		PROTOCOLS.put(554, OSXKeychainProtocolType.RTSP);
		PROTOCOLS.put(25, OSXKeychainProtocolType.SMTP);
		PROTOCOLS.put(1080, OSXKeychainProtocolType.SOCKS);
		PROTOCOLS.put(22, OSXKeychainProtocolType.SSH);
		PROTOCOLS.put(3690, OSXKeychainProtocolType.SVN);
		PROTOCOLS.put(23, OSXKeychainProtocolType.Telnet);
		PROTOCOLS.put(992, OSXKeychainProtocolType.TelnetS);
	}
}
