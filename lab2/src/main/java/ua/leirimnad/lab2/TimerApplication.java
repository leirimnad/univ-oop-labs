package ua.leirimnad.lab2;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

/**
 * Base class for the timer application.
 * Starts the app.
 */
public class TimerApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(TimerApplication.class.getResource("timer-view.fxml"));
        Parent root = fxmlLoader.load();

        Scene scene = new Scene(root);
        stage.setTitle("Таймер");
        stage.setScene(scene);
        stage.setResizable(true);
        stage.setMinWidth(300);
        stage.setMinHeight(285);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}