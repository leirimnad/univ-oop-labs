package ua.leirimnad.lab2;

import java.util.List;

public class SetClockGroup {
    List<SetClock> setClocks;

    public SetClockGroup(SetClock ...clocks){
        for (SetClock clock : clocks){
            add(clock);
        }
    }

    public void add(SetClock setClock){
        setClocks.add(setClock);
    }

    public void remove(SetClock setClock){
        setClocks.remove(setClock);
    }
}
