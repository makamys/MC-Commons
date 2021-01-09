package eu.ha3.easy;

/**
 * Basic timer that may be stopped and restarted.
 *
 */
public class StopWatchStatistic extends TimeStatistic {
    private long stopTime;

    @Override
    public long getMilliseconds() {
        return stopTime - startTime;
    }

    /**
     * Resets this timer to 0.
     */
    public void reset() {
        startTime = System.currentTimeMillis();
    }

    /**
     * Stops this timer.
     */
    public void stop() {
        stopTime = System.currentTimeMillis();
    }
}
