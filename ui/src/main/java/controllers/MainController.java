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
    updateDisabledControls();

    connection.selectedProperty().addListener(c -> {
      if (connection.isSelected()) {

        Parent root;
        URL fxml;

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

        boolean connected = Main.broadcaster.isConnected();

        connection.selectedProperty().setValue(connected);

      } else {
        Main.broadcaster.disconnect();
        platooning.setSelected(false);
        acc.setSelected(false);
        alc.setSelected(false);
      }
      updateDisabledControls();
    });

    platooning.selectedProperty().addListener(p -> {
      acc.setSelected(platooning.isSelected());
      alc.setSelected(platooning.isSelected());
      updateDisabledControls();
    });

    acc.selectedProperty().addListener(a -> {
      updateDisabledControls();
    });

    alc.selectedProperty().addListener(a -> {
      updateDisabledControls();
    });
  }

  private void updateDisabledControls() {
    if (!connection.isSelected()) {
      setConnectedControlsDisabled(true);
    } else {
      setConnectedControlsDisabled(false);
      if (platooning.isSelected()) {
        acc.disableProperty().setValue(true);
        alc.disableProperty().setValue(true);
        setAlcControlsDisabled(true);
        setAccControlsDisabled(true);
      } else {
        acc.disableProperty().setValue(false);
        alc.disableProperty().setValue(false);
        setAccControlsDisabled(acc.isSelected());
        setAlcControlsDisabled(alc.isSelected());
      }
    }
  }

  private void setConnectedControlsDisabled(boolean value) {
    platooning.disableProperty().setValue(value);
    acc.disableProperty().setValue(value);
    alc.disableProperty().setValue(value);
    setAccControlsDisabled(value);
    setAlcControlsDisabled(value);
  }

  public void setAccControlsDisabled(boolean value) {
    accelerate.disableProperty().setValue(value);
    brake.disableProperty().setValue(value);
    speed.disableProperty().setValue(value);
  }

  public void setAlcControlsDisabled(boolean value) {
    left.disableProperty().setValue(value);
    right.disableProperty().setValue(value);
    steer.disableProperty().setValue(value);
  }
}
