package ua.leirimnad.lab2;

import javafx.application.Platform;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.format.FormatStyle;

import static org.junit.jupiter.api.Assertions.*;

class TimerTest {
    @BeforeAll
    static void setUp() {
        Platform.startup(()->{});
    }

    @Test
    void set() {
        SetClockGroup testGroup = new SetClockGroup("Test group");
        Timer testTimer = new Timer(123, testGroup);
        testTimer.set(true);
        assertTrue(testTimer.isSet());
        testTimer.set(true);
        assertTrue(testTimer.isSet());
        testTimer.set(false);
        assertFalse(testTimer.isSet());
        testTimer.set(false);
        assertFalse(testTimer.isSet());
        testTimer.set(true);
        assertTrue(testTimer.isSet());
        testTimer.set(false);
        assertFalse(testTimer.isSet());
    }

    @Test
    void getTimeLeft() {
        SetClockGroup testGroup = new SetClockGroup("Test group");
        Timer testAlarm = new Timer(30, testGroup);
        assertEquals(30 * 1000, testAlarm.getTimeLeft());
        testAlarm.doTick();
        assertEquals(29 * 1000, testAlarm.getTimeLeft());
    }

    @Test
    void getTotalDuration() {
        SetClockGroup testGroup = new SetClockGroup("Test group");
        Timer testAlarm = new Timer(30, testGroup);
        assertEquals(30 * 1000, testAlarm.getTotalDuration());
        testAlarm.doTick();
        assertEquals(30 * 1000, testAlarm.getTotalDuration());
    }

    @Test
    void getDurationString() {
        assertEquals("0:00", Timer.getDurationString(0));
        assertEquals("0:01", Timer.getDurationString(1));
        assertEquals("0:59", Timer.getDurationString(59));
        assertEquals("1:00", Timer.getDurationString(60));
        assertEquals("1:00:01", Timer.getDurationString(3601));
    }

}