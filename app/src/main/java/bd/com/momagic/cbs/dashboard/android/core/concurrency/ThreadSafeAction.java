package bd.com.momagic.cbs.dashboard.android.core.concurrency;

public interface ThreadSafeAction<ReturnType> {
    ReturnType execute() throws Exception;
}
