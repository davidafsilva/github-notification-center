package pt.davidafsilva.ghn.model.mutable;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Optional;

import pt.davidafsilva.ghn.model.AbstractModel;
import pt.davidafsilva.ghn.model.Persisted;

/**
 * @author david
 */
public class SecuredConfiguration extends AbstractModel implements Persisted {

  public static final String ID = "GHNC-SCFG";
  private String token;

  @Override
  @JsonIgnore
  public String getId() {
    return ID;
  }

  public Optional<String> getToken() {
    return Optional.ofNullable(token);
  }

  public SecuredConfiguration setToken(final String token) {
    this.token = token;
    return this;
  }
}
