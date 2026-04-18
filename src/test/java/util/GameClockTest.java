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


    @Test
    void timerCallbackLoopingWorks() throws InterruptedException {
        AtomicInteger tickCount = new AtomicInteger(0);
        GameClock clock = GameClock.get();
        long intervalMS = clock.getMillisecondInterval() * 5L;  // increase the interval to ensure it's called at least once

        clock.addObserver(delta -> {
            tickCount.incrementAndGet();
        });

        clock.startThread();
        Thread.sleep(intervalMS);
        clock.stopThread();

        assertTrue(tickCount.get() > 0, "No clock callbacks were called within " + intervalMS + "ms");
    }
}