package pt.davidafsilva.ghn;

import javafx.application.Application;
import javafx.stage.Stage;

/**
 * @author david
 */
public class ApplicationRunner extends Application {

  /**
   * Let's go!
   *
   * @param args the command line arguments
   */
  public static void main(final String[] args) {
    launch(args);
  }

  @Override
  public void start(final Stage primaryStage) throws Exception {
    ApplicationController.start(this, primaryStage);
  }
}
