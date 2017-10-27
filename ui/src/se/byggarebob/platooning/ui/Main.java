package se.byggarebob.platooning.ui;

import java.io.File;
import java.net.URL;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

// TODO: Auto-generated Javadoc
/**
 * The Class Main.
 */
public class Main extends Application {

  /** The main stage. */
  public static volatile Stage mainStage = null;

  /* (non-Javadoc)
   * @see javafx.application.Application#start(javafx.stage.Stage)
   */
  @Override
  public void start(Stage mainStage) throws Exception {
    URL url = new File(
        "ui/src/se/byggarebob/platooning/ui/fxmls/MainStage.fxml").toURI()
        .toURL();
    Parent root = FXMLLoader.load(url);
    mainStage.setTitle("Platooning");
    mainStage.setScene(new Scene(root));
    mainStage.show();

    if (Main.mainStage == null) {
      Main.mainStage = mainStage;
    }
  }

  /**
   * The main method.
   *
   * @param args the arguments
   */
  public static void main(String[] args) {
    launch(args);
  }
}
