package se.byggarebob.platooning.ui.controllers;

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
import se.byggarebob.platooning.ui.Main;

// TODO: Auto-generated Javadoc
/**
 * Initializable interfacing between UI and lower-level controllers.
 *
 * @author Johan Svennungsson
 */
public class MainController implements Initializable {

  /** The speed. */
  @FXML
  private Slider speed;

  /** The steer. */
  @FXML
  private Slider steer;

  /** The accelerate. */
  @FXML
  private Button accelerate;

  /** The left. */
  @FXML
  private Button left;

  /** The right. */
  @FXML
  private Button right;

  /** The brake. */
  @FXML
  private Button brake;

  /** The platooning. */
  @FXML
  private ToggleSwitch platooning;

  /** The acc. */
  @FXML
  private ToggleSwitch acc;

  /** The alc. */
  @FXML
  private ToggleSwitch alc;

  /** The connection. */
  @FXML
  private ToggleSwitch connection;

  /**
   * The Enum COMMAND.
   */
  public enum COMMAND {

    /** The enable acc. */
    ENABLE_ACC,

    /** The enable alc. */
    ENABLE_ALC,

    /** The disable acc. */
    DISABLE_ACC,

    /** The disable alc. */
    DISABLE_ALC,

    /** The set speed. */
    SET_SPEED,

    /** The set steer. */
    SET_STEER,
  }

  /** The moped connection. */
  static MopedConnection mopedConnection = null;

  /* (non-Javadoc)
   * @see javafx.fxml.Initializable#initialize(java.net.URL, java.util.ResourceBundle)
   */
  public void initialize(URL url, ResourceBundle resourceBundle) {
    if (mopedConnection == null) {
      mopedConnection = new MopedConnection();
    }
    updateDisabledControls();

    connection.selectedProperty().addListener(c -> {
      if (connection.isSelected()) {

        Parent root;
        URL fxml;

        try {
          fxml = new File(
              "ui/src/se/byggarebob/platooning/ui/fxmls/Connect.fxml")
              .toURI()
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

  /**
   * Update disabled controls.
   */
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

  /**
   * Sets the connected controls disabled.
   *
   * @param value the new connected controls disabled
   */
  private void setConnectedControlsDisabled(boolean value) {
    platooning.disableProperty().setValue(value);
    acc.disableProperty().setValue(value);
    alc.disableProperty().setValue(value);
    setAccControlsDisabled(value);
    setAlcControlsDisabled(value);
  }

  /**
   * Sets the acc controls disabled.
   *
   * @param value the new acc controls disabled
   */
  public void setAccControlsDisabled(boolean value) {
    accelerate.disableProperty().setValue(value);
    brake.disableProperty().setValue(value);
    speed.disableProperty().setValue(value);
  }

  /**
   * Sets the alc controls disabled.
   *
   * @param value the new alc controls disabled
   */
  public void setAlcControlsDisabled(boolean value) {
    left.disableProperty().setValue(value);
    right.disableProperty().setValue(value);
    steer.disableProperty().setValue(value);
  }

  /**
   * The Class MopedConnection.
   */
  public class MopedConnection {

    /** The socket. */
    private Socket socket;

    /** The connected. */
    private boolean connected;

    /** The update receiver. */
    private UIUpdateReceiver updateReceiver;

    /** The update receiver thread. */
    private Thread updateReceiverThread;

    /**
     * Instantiates a new moped connection.
     */
    public MopedConnection() {
      connected = false;
    }

    /**
     * Connect.
     *
     * @param host the host
     * @param port the port
     * @return true, if successful
     */
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

    /**
     * Send data.
     *
     * @param data the data
     */
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

    /**
     * Sets the speed.
     *
     * @param speed the new speed
     */
    public void setSpeed(byte speed) {
      byte[] payload = new byte[2];
      payload[0] = (byte) COMMAND.SET_SPEED.ordinal();
      payload[1] = speed;
      sendData(payload);
    }

    /**
     * Sets the steer.
     *
     * @param steer the new steer
     */
    public void setSteer(byte steer) {
      byte[] payload = new byte[2];
      payload[0] = (byte) COMMAND.SET_STEER.ordinal();
      payload[1] = steer;
      sendData(payload);
    }

    /**
     * Sets the acc on.
     *
     * @param on the new acc on
     */
    public void setAccOn(boolean on) {
      byte[] payload = new byte[1];
      COMMAND command = on ? COMMAND.ENABLE_ACC : COMMAND.DISABLE_ACC;
      payload[0] = (byte) command.ordinal();
      sendData(payload);
    }

    /**
     * Sets the alc on.
     *
     * @param on the new alc on
     */
    public void setAlcOn(boolean on) {

      byte[] payload = new byte[1];
      COMMAND command = on ? COMMAND.ENABLE_ALC : COMMAND.DISABLE_ALC;
      payload[0] = (byte) command.ordinal();
      sendData(payload);
    }

    /**
     * Checks if is connected.
     *
     * @return true, if is connected
     */
    public boolean isConnected() {
      return connected;
    }

    /**
     * Disconnect.
     */
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

    /**
     * The Class UIUpdateReceiver.
     */
    private class UIUpdateReceiver implements Runnable {

      /** The stop flagged. */
      public AtomicBoolean stopFlagged;

      /**
       * Instantiates a new UI update receiver.
       */
      public UIUpdateReceiver() {
        stopFlagged = new AtomicBoolean(false);
      }

      /* (non-Javadoc)
       * @see java.lang.Runnable#run()
       */
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
