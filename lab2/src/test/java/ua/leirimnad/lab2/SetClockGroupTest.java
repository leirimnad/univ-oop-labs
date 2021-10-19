package ua.leirimnad.lab2;

import javafx.application.Platform;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.Array;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SetClockGroupTest {
    @BeforeAll
    static void setUp() {
        Platform.startup(()->{});
    }

    @Test
    void indexOf() {
        SetClockGroup testGroup = new SetClockGroup("Test group");
        Alarm testAlarm = new Alarm(12, 20, testGroup);
        Alarm testAlarm1 = new Alarm(12, 20, testGroup);
        assertEquals(0, testGroup.indexOf(testAlarm));
        assertEquals(1, testGroup.indexOf(testAlarm1));
    }

    @Test
    void getSetClocks() {
        SetClockGroup testGroup = new SetClockGroup("Test group");
        Alarm testAlarm = new Alarm(2, 20, testGroup);
        Alarm testAlarm1 = new Alarm(3, 20, testGroup);
        Alarm testAlarm2 = new Alarm(4, 20, testGroup);
        List<SetClock> list = new ArrayList<>(testGroup.getSetClocks());

        assertEquals(testAlarm, list.get(0));
        assertEquals(testAlarm1, list.get(1));
        assertEquals(testAlarm2, list.get(2));
    }

    @Test
    void size() {
        SetClockGroup testGroup = new SetClockGroup("Test group");
        new Alarm(2, 20, testGroup);
        assertEquals(1, testGroup.size());

        new Alarm(3, 20, testGroup);
        assertEquals(2, testGroup.size());

        new Alarm(4, 20, testGroup);
        assertEquals(3, testGroup.size());
    }
}