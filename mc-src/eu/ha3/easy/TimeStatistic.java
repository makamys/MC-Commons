package eu.ha3.easy;

import java.util.Locale;

/**
 * Basic timer that measures the total milliseconds since it's creation.
 * 
 */
public class TimeStatistic {
    private final Locale locale;

    protected long startTime;

    /**
     * Creates a new timer.
     * 
     * @param locale Format used when converting duration to a string.
     */
    public TimeStatistic(Locale locale) {
        this.locale = locale;
        startTime = System.currentTimeMillis();
    }

    /**
     * Creates a new timer.
     */
    public TimeStatistic() {
        this(null);
    }

    /**
     * Gets the total milliseconds that have elapsed.
     */
    public long getMilliseconds() {
        return System.currentTimeMillis() - startTime;
    }

    /**
     * Gets a formatted string representing the amount of time that has elapsed.
     */
    public String getSecondsAsString(int precision) {
        if (locale == null) {
            return String.format("%." + precision + "f", getMilliseconds() / 1000f);
        }
        return String.format(locale, "%." + precision + "f", getMilliseconds() / 1000f);
    }

}
