package ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;

public class Main extends Application {

    @Override
    public void start(Stage mainStage) throws Exception {
        URL url = new File("platooning/src/main/java/ui/fxmls/MainStage.fxml").toURI().toURL();
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
