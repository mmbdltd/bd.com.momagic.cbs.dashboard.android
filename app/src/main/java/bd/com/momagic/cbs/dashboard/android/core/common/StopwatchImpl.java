package bd.com.momagic.cbs.dashboard.android.core.common;

import bd.com.momagic.cbs.dashboard.android.core.utilities.DateTimeFormatter;
import lombok.Getter;

class StopwatchImpl implements Stopwatch {

    @Getter
    private long startTime = 0L;

    @Getter
    private long endTime = 0L;

    private long elapsedTimeInMilliseconds = 0L;

    @Override
    public Stopwatch reset() {
        startTime = 0L;
        endTime = 0L;
        elapsedTimeInMilliseconds = 0L;

        return this;
    }

    @Override
    public Stopwatch start() {
        // if the stopwatch is already started or
        // the stopwatch is already stopped...
        if (startTime != 0L || endTime != 0L) { return this; }

        // gets the current timestamp...
        startTime = System.currentTimeMillis();

        return this;
    }

    @Override
    public Stopwatch startNew() {
        reset();

        return start();
    }

    @Override
    public Stopwatch stop() {
        // if the stopwatch is not started, we'll return...
        if (startTime == 0L) { return this; }

        // gets the current timestamp...
        endTime = System.currentTimeMillis();
        // measures the elapsed time...
        elapsedTimeInMilliseconds = endTime - startTime;

        return this;
    }

    @Override
    public long getElapsedTime() {
        return elapsedTimeInMilliseconds;
    }

    @Override
    public String getHumanReadableElapsedTime() {
        // gets the elapsed time in milliseconds...
        final long elapsedTimeInMilliseconds = getElapsedTime();

        // formatting the elapsed time (in milliseconds) to make it
        // more human-readable and returning it...
        return DateTimeFormatter.formatTime(elapsedTimeInMilliseconds);
    }
}
