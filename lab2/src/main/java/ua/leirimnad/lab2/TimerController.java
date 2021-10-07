package ua.leirimnad.lab2;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Button;
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
    void initialize() {
        assert addButton != null : "fx:id=\"addButton\" was not injected: check your FXML file 'timer-view.fxml'.";
        assert sortButton != null : "fx:id=\"sortButton\" was not injected: check your FXML file 'timer-view.fxml'.";

        addButton.setOnMouseClicked(event -> {
            a = new Timer(100);
        });
        sortButton.setOnMouseClicked(event -> {
            System.out.println(a.getTimeLeft());
        });

    }

}
