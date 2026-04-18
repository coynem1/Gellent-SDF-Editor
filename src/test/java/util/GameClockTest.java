package util;

import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class GameClockTest {
    @Test
    void getSingletonInstanceIsSame() {
        GameClock instance1 = GameClock.get();
        GameClock instance2 = GameClock.get();
        assertEquals(instance1, instance2, "GameClock instances are not the same");
    }

    @Test
    void clockIntervalGreaterThanZero() {
        GameClock clock = GameClock.get();
        assertTrue(clock.getMillisecondInterval() > 0, "Clock interval is not greater than zero");
    }
}