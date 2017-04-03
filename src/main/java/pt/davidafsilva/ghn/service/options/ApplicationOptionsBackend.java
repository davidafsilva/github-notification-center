package pt.davidafsilva.ghn.service.options;

import pt.davidafsilva.ghn.ApplicationOptions;

/**
 * @author david
 */
interface ApplicationOptionsBackend {

  ApplicationOptions load();

  void save(final ApplicationOptions options);
}
