package ua.leirimnad.lab2;

import javafx.application.Platform;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;

class AlarmTest {
    @BeforeAll
    static void setUp() {
        Platform.startup(()->{});
    }

    @Test
    void set() {
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

    @Test
    void nextInstant() {
        assertTrue(Instant.now().isBefore(Alarm.nextInstant(0, 0, null)));
        assertTrue(Instant.now().isBefore(Alarm.nextInstant(23, 59, null)));
        assertTrue(Instant.now().isBefore(Alarm.nextInstant(12, 0, null)));
        assertTrue(Instant.now().isBefore(Alarm.nextInstant(0, 12, null)));

        assertTrue(Instant.now().until(Alarm.nextInstant(0, 0, null), ChronoUnit.HOURS) < 24);
        assertTrue(Instant.now().until(Alarm.nextInstant(23, 59, null), ChronoUnit.HOURS) < 24);
        assertTrue(Instant.now().until(Alarm.nextInstant(12, 0, null), ChronoUnit.HOURS) < 24);
        assertTrue(Instant.now().until(Alarm.nextInstant(0, 12, null), ChronoUnit.HOURS) < 24);

    }

    @Test
    void secondsUntil() {
        assertTrue(Alarm.secondsUntil(0, 0, null) / 3600 < 24);
        assertTrue(Alarm.secondsUntil(23, 59, null) / 3600 < 24);
        assertTrue(Alarm.secondsUntil(12, 0, null) / 3600 < 24);
        assertTrue(Alarm.secondsUntil(0, 12, null) / 3600 < 24);

    }

    @Test
    void getTimeString() {
        assertEquals("00:00", Alarm.getTimeString(0, 0));
        assertEquals("23:59", Alarm.getTimeString(23, 59));
        assertEquals("01:01", Alarm.getTimeString(1, 1));
        assertEquals("10:00", Alarm.getTimeString(10, 0));
        assertEquals("00:10", Alarm.getTimeString(0, 10));
    }

    @Test
    void getEndDurationString() {
        assertEquals("0 сек", Alarm.getEndDurationString(0));
        assertEquals("1 сек", Alarm.getEndDurationString(1));
        assertEquals("59 сек", Alarm.getEndDurationString(59));
        assertEquals("1 хв", Alarm.getEndDurationString(60));
        assertEquals("1 хв", Alarm.getEndDurationString(61));
        assertEquals("1 хв", Alarm.getEndDurationString(119));
        assertEquals("2 хв", Alarm.getEndDurationString(120));
        assertEquals("2 хв", Alarm.getEndDurationString(123));
        assertEquals("59 хв", Alarm.getEndDurationString(3599));
        assertEquals("1 год", Alarm.getEndDurationString(3600));
        assertEquals("2 год", Alarm.getEndDurationString(8000));
    }

    @Test
    void getTotalDuration() {
        SetClockGroup testGroup = new SetClockGroup("Test group");
        Alarm testAlarm = new Alarm(12, 20, testGroup);
        assertTrue(testAlarm.getTotalDuration() < 24 * 60 * 60 * 1000);
        assertTrue(testAlarm.getTotalDuration() >= 0);

    }

    @Test
    void getTimeLeft() {
        SetClockGroup testGroup = new SetClockGroup("Test group");
        Alarm testAlarm = new Alarm(12, 20, testGroup);
        testAlarm.set(false);
        assertEquals(-1, testAlarm.getTimeLeft());
        testAlarm.set(true);
        assertEquals(testAlarm.getTimeLeft(), testAlarm.getTotalDuration());
    }


}