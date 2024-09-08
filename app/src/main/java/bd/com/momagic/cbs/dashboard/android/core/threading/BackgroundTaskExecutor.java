package bd.com.momagic.cbs.dashboard.android.core.threading;

public interface BackgroundTaskExecutor {
    void execute(final Runnable task);
    void start();
    void stop();
    void join();

    static BackgroundTaskExecutor createInstance() {
        return new BackgroundTaskExecutorImpl();
    }

    static BackgroundTaskExecutor createInstance(final boolean loop) {
        return new BackgroundTaskExecutorImpl(loop);
    }

    static BackgroundTaskExecutor createInstance(
            final boolean loop,
            final long backgroundThreadSleepTimeoutInMilliseconds) {
        return new BackgroundTaskExecutorImpl(loop, backgroundThreadSleepTimeoutInMilliseconds);
    }
}
