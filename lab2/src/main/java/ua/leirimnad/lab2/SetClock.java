package ua.leirimnad.lab2;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class SetClock {
    protected Instant timeSet;
    protected Instant setTo;

    public SetClock(Instant setTo){
        this.timeSet = Instant.now();
        this.setTo = setTo;
    }

    public long getTimeLeft(){
        return Instant.now().until(this.setTo, ChronoUnit.SECONDS);
    }

    public boolean hasGoneOff(){
        return Instant.now().isAfter(this.setTo);
    }
}
