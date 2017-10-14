package ui;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class Main extends Application {

  public static ActionBroadcaster broadcaster;

  @Override
  public void start(Stage mainStage) throws Exception {
    URL url = new File("ui/src/main/java/fxmls/MainStage.fxml").toURI().toURL();
    Parent root = FXMLLoader.load(url);
    mainStage.setTitle("Platooning");
    mainStage.setScene(new Scene(root));
    mainStage.show();

    broadcaster = new ActionBroadcaster();

    while (!broadcaster.connected) {
      Stage connectStage = new Stage();
      connectStage.initOwner(mainStage);
      connectStage.initModality(Modality.APPLICATION_MODAL);

      url = new File("ui/src/main/java/fxmls/Connect.fxml").toURI().toURL();
      root = FXMLLoader.load(url);

      connectStage.setScene(new Scene(root));
      connectStage.showAndWait();
    }
  }

  public static void main(String[] args) {
    launch(args);
  }

  public class ActionBroadcaster {

    private Socket socket;
    private boolean connected;

    public ActionBroadcaster() {
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
  }
}
