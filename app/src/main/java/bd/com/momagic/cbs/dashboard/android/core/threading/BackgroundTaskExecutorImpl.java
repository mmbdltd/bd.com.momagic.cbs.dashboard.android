package bd.com.momagic.cbs.dashboard.android.core.threading;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bd.com.momagic.cbs.dashboard.android.core.concurrency.ThreadSafeBlockingQueue;
import bd.com.momagic.cbs.dashboard.android.core.concurrency.ThreadSafeBlockingQueueImpl;
import bd.com.momagic.cbs.dashboard.android.core.concurrency.ThreadSafeBoolean;
import bd.com.momagic.cbs.dashboard.android.core.utilities.ThreadUtilities;

class BackgroundTaskExecutorImpl implements BackgroundTaskExecutor, Runnable {

    private final boolean loop;
    private final long backgroundThreadSleepTimeoutInMilliseconds;
    private final ThreadSafeBoolean running = new ThreadSafeBoolean(false);
    private final Logger logger = LoggerFactory.getLogger(BackgroundTaskExecutorImpl.class);
    private final ThreadSafeBlockingQueue<Runnable> taskQueue
            = new ThreadSafeBlockingQueueImpl<>();
    private final Thread backgroundThread;

    private static final long DEFAULT_THREAD_SLEEP_TIMEOUT_IN_MILLISECONDS = 500L;

    BackgroundTaskExecutorImpl() {
        this(false);
    }

    BackgroundTaskExecutorImpl(final boolean loop) {
        this(loop, -1L);
    }

    BackgroundTaskExecutorImpl(
            final boolean loop,
            final long backgroundThreadSleepTimeoutInMilliseconds) {
        this.loop = loop;
        this.backgroundThreadSleepTimeoutInMilliseconds = backgroundThreadSleepTimeoutInMilliseconds < 0L
                ? DEFAULT_THREAD_SLEEP_TIMEOUT_IN_MILLISECONDS
                : backgroundThreadSleepTimeoutInMilliseconds;

        backgroundThread = new Thread(this);
        backgroundThread.setDaemon(false);
        backgroundThread.setPriority(Thread.MIN_PRIORITY);
        backgroundThread.setName("background-task");
    }

    @Override
    public void execute(final Runnable task) {
        // placing the task at the end of the queue...
        taskQueue.enqueue(task);
        // if background thread is not already started,
        // we shall start the background task...
        start();
    }

    @Override
    public void start() {
        final boolean alreadyRunning = this.running.getAndSet(true);

        // if the background task executor is already running,
        // we shall not proceed any further...
        if (alreadyRunning) { return; }

        // otherwise, we shall start the background thread...
        backgroundThread.start();
    }

    @Override
    public void stop() {
        running.set(false);

        ThreadUtilities.tryInterrupt(backgroundThread);
    }

    @Override
    public void join() {
        ThreadUtilities.tryJoin(backgroundThread);
    }

    @Override
    public void run() {
        logger.info("Background task executor has started.");

        while (running.get()) {
            final Runnable task = taskQueue.dequeue();

            if (task == null) { continue; }

            try {
                task.run();
            } catch (final Exception exception) {
                logger.error("An exception occurred while performing background task.", exception);
            }

            // if loop flag is false, we shall skip this iteration...
            if (!loop) { continue; }

            // we shall wait for a while...
            ThreadUtilities.trySleep(backgroundThreadSleepTimeoutInMilliseconds);
            // we shall place the task at the end of the queue...
            execute(task);
        }

        logger.info("Background task executor has stopped.");
    }
}
