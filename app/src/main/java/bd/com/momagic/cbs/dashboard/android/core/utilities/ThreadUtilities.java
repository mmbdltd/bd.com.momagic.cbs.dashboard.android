package bd.com.momagic.cbs.dashboard.android.core.utilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

public final class ThreadUtilities {

    private static final Logger logger = LoggerFactory.getLogger(ThreadUtilities.class);

    public static void trySleep(final long timeoutInMilliseconds) {
        try {
            Thread.sleep(timeoutInMilliseconds);
        } catch (final Exception exception) { }
    }

    /**
     * Blocks until all tasks have completed execution after a shutdown request,
     * or the timeout occurs, or the current thread is interrupted, whichever happens first.
     * @param timeoutInMilliseconds The maximum time to wait (in milliseconds).
     * @param executorService Executor service that shall await termination.
     * @return true if the executor terminated or interrupted while waiting
     * and false if the timeout elapsed before termination.
     */
    public static boolean awaitExecutorServiceTermination(
            final long timeoutInMilliseconds,
            final ExecutorService executorService) {
        boolean terminated;

        try {
            terminated = executorService.awaitTermination(
                    timeoutInMilliseconds,
                    TimeUnit.MILLISECONDS);
        } catch (final Exception exception) {
            // in case of exception, returns true...
            return true;
        }

        return terminated;
    }

    public static void tryInterrupt(final Thread thread) {
        // if the thread is 'null', we shall return...
        if (thread == null) { return; }

        try {
            // tries to interrupt the thread...
            thread.interrupt();
        } catch (final Exception exception) {
            logger.warn("An exception occurred while interrupting thread.", exception);
        }
    }

    public static void tryJoin(final Thread thread) {
        // if the thread is 'null', we shall return...
        if (thread == null) { return; }

        try {
            // tries to wait for the thread to finish...
            thread.join();
        } catch (final Exception exception) {
            logger.warn("An exception occurred while joining thread.", exception);
        }
    }
}
