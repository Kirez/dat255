import java.io.File;
import java.net.URL;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

  @Override
  public void start(Stage mainStage) throws Exception {
    URL url = new File("ui/src/main/java/fxmls/MainStage.fxml").toURI().toURL();
    Parent root = FXMLLoader.load(url);
    mainStage.setTitle("Platooning");
    mainStage.setScene(new Scene(root, 600, 400));
    mainStage.setMinWidth(600);
    mainStage.setMinHeight(400);
    mainStage.show();
  }

  public static void main(String[] args) {
    launch(args);
  }
}
