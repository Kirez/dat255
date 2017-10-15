package controllers;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.controlsfx.control.ToggleSwitch;
import ui.Main;

/**
 * Created by Johan on 2017-10-12.
 */
public class MainController implements Initializable {

  @FXML
  private Slider speed;

  @FXML
  private Slider steer;

  @FXML
  private Button accelerate;

  @FXML
  private Button left;

  @FXML
  private Button right;

  @FXML
  private Button brake;

  @FXML
  private ToggleSwitch platooning;

  @FXML
  private ToggleSwitch acc;

  @FXML
  private ToggleSwitch alc;

  @FXML
  private ToggleSwitch connection;

  public void initialize(URL url, ResourceBundle resourceBundle) {
    setConnectedControlsDisabled(true);

    connection.selectedProperty().addListener(c -> {
      if (connection.isSelected()) {

        Parent root;
        URL fxml;

        boolean connected = false;

        try {
          fxml = new File("ui/src/main/java/fxmls/Connect.fxml").toURI()
              .toURL();
          root = FXMLLoader.load(fxml);
          Stage connectStage = new Stage();
          connectStage.initOwner(Main.mainStage);
          connectStage.initModality(Modality.APPLICATION_MODAL);

          connectStage.setScene(new Scene(root));
          connectStage.showAndWait();
        } catch (IOException e) {
          e.printStackTrace();
        }

        connected = Main.broadcaster.isConnected();

        connection.selectedProperty().setValue(connected);

        setConnectedControlsDisabled(!connected);
      } else {
        Main.broadcaster.disconnect();
      }
    });

  }

  private void setConnectedControlsDisabled(boolean value) {
    speed.disableProperty().setValue(value);
    steer.disableProperty().setValue(value);
    accelerate.disableProperty().setValue(value);
    brake.disableProperty().setValue(value);
    left.disableProperty().setValue(value);
    right.disableProperty().setValue(value);
    platooning.disableProperty().setValue(value);
    acc.disableProperty().setValue(value);
    alc.disableProperty().setValue(value);
  }

}
