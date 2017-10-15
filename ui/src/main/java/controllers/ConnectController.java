package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import ui.Main;

public class ConnectController {

  @FXML
  private TextField host;
  @FXML
  private TextField port;
  @FXML
  private Button connect;
  @FXML
  private CheckBox useDefault;

  private static String DEFAULT_HOST = "localhost";
  private static String DEFAULT_PORT = "2221";

  @FXML
  void connectOnAction(ActionEvent event) {
    String hostStr = host.getText();
    int portInt = Integer.parseInt(port.getText());
    if (Main.broadcaster.connect(hostStr, portInt)) {
      System.out.println("Connected");
    } else {
      System.out.println("Connection failed");
    }

    ((Stage) connect.getScene().getWindow()).close();
  }

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