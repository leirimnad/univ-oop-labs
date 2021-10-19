package ua.leirimnad.lab2;

import javafx.application.Platform;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SetClockTest {
    @BeforeAll
    static void setUp() {
        Platform.startup(()->{});
    }

    @Test
    void getGroup() {
        SetClockGroup testGroup = new SetClockGroup("Test group");
        Alarm testAlarm = new Alarm(12, 20, testGroup);
        assertEquals(testGroup, testAlarm.getGroup());
    }

    @Test
    void changeGroup() {
        SetClockGroup testGroup = new SetClockGroup("Test group");
        SetClockGroup testGroup1 = new SetClockGroup("Test group1");
        Alarm testAlarm = new Alarm(12, 20, testGroup);
        testAlarm.changeGroup(testGroup1);
        assertNotEquals(testGroup, testAlarm.getGroup());
        assertEquals(testGroup1, testAlarm.getGroup());
    }

    @Test
    void isSet() {
        SetClockGroup testGroup = new SetClockGroup("Test group");
        Alarm testAlarm = new Alarm(12, 20, testGroup);
        testAlarm.set(true);
        assertTrue(testAlarm.isSet());
        testAlarm.set(true);
        assertTrue(testAlarm.isSet());
        testAlarm.set(false);
        assertFalse(testAlarm.isSet());
        testAlarm.set(false);
        assertFalse(testAlarm.isSet());
        testAlarm.set(true);
        assertTrue(testAlarm.isSet());
        testAlarm.set(false);
        assertFalse(testAlarm.isSet());
    }
}