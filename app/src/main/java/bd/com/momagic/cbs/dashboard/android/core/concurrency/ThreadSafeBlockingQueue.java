package bd.com.momagic.cbs.dashboard.android.core.concurrency;

public interface ThreadSafeBlockingQueue<Type> {
    int size();
    void enqueue(final Type element);
    Type dequeue();
    void clear();
}
