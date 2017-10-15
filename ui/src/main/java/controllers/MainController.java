package controllers;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicBoolean;
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

  public enum COMMAND {
    ENABLE_ACC,
    ENABLE_ALC,
    DISABLE_ACC,
    DISABLE_ALC,
    SET_SPEED,
    SET_STEER,
  }

  public static MopedConnection mopedConnection;

  public void initialize(URL url, ResourceBundle resourceBundle) {
    mopedConnection = new MopedConnection();
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

        boolean connected = mopedConnection.isConnected();

        connection.selectedProperty().setValue(connected);

      } else {
        mopedConnection.disconnect();
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
      if (connection.isSelected()) {
        mopedConnection.setAccOn(acc.isSelected());
      }
    });

    alc.selectedProperty().addListener(a -> {
      updateDisabledControls();
      if (connection.isSelected()) {
        mopedConnection.setAlcOn(alc.isSelected());
      }
    });

    speed.valueProperty().addListener(s -> {
      if (!acc.isSelected()) {
        mopedConnection.setSpeed((byte) speed.getValue());
      }
    });

    steer.valueProperty().addListener(s -> {
      if (!alc.isSelected()) {
        mopedConnection.setSteer((byte) steer.getValue());
      }
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

  public class MopedConnection {

    private Socket socket;
    private boolean connected;
    private UIUpdateReceiver updateReceiver;
    private Thread updateReceiverThread;

    public MopedConnection() {
      connected = false;
    }

    public boolean connect(String host, int port) {
      connected = false;
      try {
        socket = new Socket(InetAddress.getByName(host), port);
        connected = true;
        updateReceiver = new UIUpdateReceiver();
        if (updateReceiverThread != null && updateReceiverThread.isAlive()) {
          updateReceiverThread.interrupt();
        }
        updateReceiverThread = new Thread(updateReceiver);
        updateReceiverThread.start();
      } catch (IOException e) {
        e.printStackTrace();
      }
      return connected;
    }

    public void sendData(byte[] data) {
      try {
        DataOutputStream writer = new DataOutputStream(
            socket.getOutputStream());
        writer.write(data);
      } catch (IOException e) {
        e.printStackTrace();
        connected = false;
      }
    }

    public void setSpeed(byte speed) {
      byte[] payload = new byte[2];
      payload[0] = (byte) COMMAND.SET_SPEED.ordinal();
      payload[1] = speed;
      sendData(payload);
    }

    public void setSteer(byte steer) {
      byte[] payload = new byte[2];
      payload[0] = (byte) COMMAND.SET_STEER.ordinal();
      payload[1] = steer;
      sendData(payload);
    }

    public void setAccOn(boolean on) {
      byte[] payload = new byte[1];
      COMMAND command = on ? COMMAND.ENABLE_ACC : COMMAND.DISABLE_ACC;
      payload[0] = (byte) command.ordinal();
      sendData(payload);
    }

    public void setAlcOn(boolean on) {

      byte[] payload = new byte[1];
      COMMAND command = on ? COMMAND.ENABLE_ALC : COMMAND.DISABLE_ALC;
      payload[0] = (byte) command.ordinal();
      sendData(payload);
    }

    public boolean isConnected() {
      return connected;
    }

    public void disconnect() {
      connected = false;
      try {
        if (updateReceiverThread != null && updateReceiverThread.isAlive()) {
          updateReceiverThread.interrupt();
        }
        socket.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    private class UIUpdateReceiver implements Runnable {

      public AtomicBoolean stopFlagged;

      public UIUpdateReceiver() {
        stopFlagged = new AtomicBoolean(false);
      }

      @Override
      public void run() {
        try {
          DataInputStream inputStream = new DataInputStream(
              socket.getInputStream());
          while (!stopFlagged.get()) {
            byte motorValue = inputStream.readByte();
            byte steerValue = inputStream.readByte();
            speed.setValue(motorValue);
            steer.setValue(steerValue);
          }
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
  }
}
