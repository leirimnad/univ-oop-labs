package ua.leirimnad.lab2;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class TimerController {
    Timer a;
    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button addButton;

    @FXML
    private Button sortButton;

    @FXML
    private HBox firstTimerRow;

    @FXML
    void initialize() {

        addButton.setOnMouseClicked(event -> {
            try{
                Parent addTimerRoot = new FXMLLoader(TimerApplication.class.getResource("addTimer-view.fxml")).load();
                Stage addTimerStage = new Stage();
                addTimerStage.setTitle("Створити таймер");
                addTimerStage.setScene(new Scene(addTimerRoot));
                addTimerStage.setResizable(false);
                addTimerStage.show();
            } catch (java.io.IOException e) {
                e.printStackTrace();
            }
        });
        sortButton.setOnMouseClicked(event -> {
            System.out.println(a.getTimeLeft());
        });

    }

}
