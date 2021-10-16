package ua.leirimnad.lab2;

import java.net.URL;
import java.util.*;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.IntegerBinding;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;

public class TimerController {
    Timer a;
    List<Alarm> alarms = new ArrayList<>();

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button addButton;

    @FXML
    private Button sortButton;

    @FXML
    private FlowPane firstTimerRow;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    void initialize() {
        Timeline timeline = new Timeline( new KeyFrame(Duration.millis(500), event -> {
            for (Alarm alarm : alarms){
                alarm.tick();
            }
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
        addButton.setOnMouseClicked(event -> {
            try{
                Parent addTimerRoot = new FXMLLoader(TimerApplication.class.getResource("addTimer-view.fxml")).load();
                Stage addTimerStage = new Stage();
                addTimerStage.setTitle("Створити таймер");
                addTimerStage.setScene(new Scene(addTimerRoot));
                addTimerStage.setResizable(false);
                addTimerStage.setOnCloseRequest(e->{
                    if(addTimerStage.getUserData() != null){
                        SetClock newSetClock = (SetClock) addTimerStage.getUserData();
                        if (addTimerStage.getUserData().getClass().equals(Alarm.class)){
                            alarms.add((Alarm) addTimerStage.getUserData());
                        }
                        firstTimerRow.getChildren().add(newSetClock.getWidget());
                    }
                });
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
