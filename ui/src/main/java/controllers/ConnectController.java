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
      host.setText("192.168.43.230");
      port.setText("2221");
      host.setEditable(false);
      port.setEditable(false);
    } else {
      host.setEditable(true);
      port.setEditable(true);
    }
  }
}