package util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TimeTest {
    @Test
    void timeSingleInstanceIsSame() {
        Time instance1 = Time.get();
        Time instance2 = Time.get();
        assertEquals(instance1, instance2, "Time instances are not the same");
    }

    @Test
    void timeMeasuredIsAccurate() throws InterruptedException {
        Time time = Time.get();
        long waitIntervalMS = 100;
        float waitTimeMS;

        time.beginFrame();
        Thread.sleep(waitIntervalMS);
        waitTimeMS = time.endFrame() * 1000;

        assertTrue(waitTimeMS >= waitIntervalMS * 0.95, "Measured wait time is less than expected");
        assertTrue(waitTimeMS <= waitIntervalMS * 1.05, "Measured wait time is greater than expected");
    }

    @Test
    void timeIncreasesOverTimeWithEndFrame() throws InterruptedException {
        Time time = Time.get();
        long waitIntervalMS = 100;
        float waitTimeBefore, waitTimeAfter;

        waitTimeBefore = time.endFrame();
        Thread.sleep(waitIntervalMS);
        waitTimeAfter = time.endFrame();

        assertTrue(waitTimeBefore < waitTimeAfter, "Between frames, time did not increase"+ waitTimeBefore + " " + waitTimeAfter);
    }
}