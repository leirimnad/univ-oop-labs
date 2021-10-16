package ua.leirimnad.lab2;

import javafx.scene.layout.AnchorPane;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

public abstract class SetClock {
    protected Instant timeCreated;
    protected String soundPath;
    protected SetClockGroup group;
    protected AnchorPane widget;

    public SetClock(){
        this.timeCreated = Instant.now();
    }

    public abstract AnchorPane getWidget();
    public abstract void set(boolean to);

}
