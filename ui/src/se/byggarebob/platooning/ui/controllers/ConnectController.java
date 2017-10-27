package se.byggarebob.platooning.ui.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

// TODO: Auto-generated Javadoc
/**
 * The Class ConnectController.
 */
public class ConnectController {

  /** The host. */
  @FXML
  private TextField host;

  /** The port. */
  @FXML
  private TextField port;

  /** The connect. */
  @FXML
  private Button connect;

  /** The use default. */
  @FXML
  private CheckBox useDefault;

  /** The default host. */
  private static String DEFAULT_HOST = "localhost";

  /** The default port. */
  private static String DEFAULT_PORT = "2221";

  /**
   * Connect on action.
   *
   * @param event the event
   */
  @FXML
  void connectOnAction(ActionEvent event) {
    String hostStr = host.getText();
    int portInt = Integer.parseInt(port.getText());
    if (MainController.mopedConnection.connect(hostStr, portInt)) {
      System.out.println("Connected");
    } else {
      System.out.println("Connection failed");
    }

    ((Stage) connect.getScene().getWindow()).close();
  }

  /**
   * Use default on action.
   *
   * @param event the event
   */
  @FXML
  void useDefaultOnAction(ActionEvent event) {
    if (useDefault.isSelected()) {
      host.setText(DEFAULT_HOST);
      port.setText(DEFAULT_PORT);
    }
    host.disableProperty().setValue(useDefault.isSelected());
    port.disableProperty().setValue(useDefault.isSelected());
  }
}