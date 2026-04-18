package util;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GameClockCallbackTest {
    private GameClock clock;
    private long intervalMS;
    private long intervalBigMS; // bigger interval to ensure called at least once

    @BeforeEach
    void setupClock() {
        clock = GameClock.get();
        intervalMS = clock.getMillisecondInterval();
        intervalBigMS = intervalMS * 5L;
        clock.startThread();
    }

    @AfterEach
    void stopClock() {
        clock.stopThread();
    }


    @Test
    void timerCallbackLoopingWorks() throws InterruptedException {
        AtomicInteger tickCount = new AtomicInteger(0);

        clock.addObserver(delta -> {
            tickCount.incrementAndGet();
        });

        Thread.sleep(intervalBigMS);
        assertTrue(tickCount.get() > 0, "No clock callbacks were called within " + intervalBigMS + "ms");
    }

    @Test
    void timerCallbackLoopingTimingIntervalsAreConsistent() throws InterruptedException {
        AtomicInteger tickCount = new AtomicInteger(0);
        AtomicInteger timeTotal = new AtomicInteger(0);
        float timeAvg;

        clock.addObserver(delta -> {
            timeTotal.addAndGet((int) (delta));
            tickCount.incrementAndGet();
        });

        Thread.sleep(intervalBigMS);
        timeAvg = (float) timeTotal.get() / tickCount.get();

        // 5% tolerance
        assertTrue(timeAvg >= intervalMS * 0.95 && timeAvg <= intervalMS * 1.05f,
                "Expected ~16.6ms but got " + timeAvg + "ms"
        );
    }
}