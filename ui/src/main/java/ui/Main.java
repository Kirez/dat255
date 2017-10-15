package ui;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

  public static MopedConnection mopedConnection;
  public static Stage mainStage;

  public enum COMMAND {
    ENABLE_ACC,
    ENABLE_ALC,
    DISABLE_ACC,
    DISABLE_ALC,
    SET_SPEED,
    SET_STEER,
  }

  @Override
  public void start(Stage mainStage) throws Exception {
    URL url = new File("ui/src/main/java/fxmls/MainStage.fxml").toURI().toURL();
    Parent root = FXMLLoader.load(url);
    mainStage.setTitle("Platooning");
    mainStage.setScene(new Scene(root));
    mainStage.show();

    Main.mopedConnection = new MopedConnection();
    Main.mainStage = mainStage;
  }

  public static void main(String[] args) {
    launch(args);
  }

  public class MopedConnection {

    private Socket socket;
    private boolean connected;

    public MopedConnection() {
      connected = false;
    }

    public boolean connect(String host, int port) {
      connected = false;
      try {
        socket = new Socket(InetAddress.getByName(host), port);
        connected = true;
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
        socket.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
}
