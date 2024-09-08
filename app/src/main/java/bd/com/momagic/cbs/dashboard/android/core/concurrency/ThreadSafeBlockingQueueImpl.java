package bd.com.momagic.cbs.dashboard.android.core.concurrency;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ThreadSafeBlockingQueueImpl<Type> implements ThreadSafeBlockingQueue<Type> {

    private final Logger logger = LoggerFactory.getLogger(ThreadSafeBlockingQueueImpl.class);
    private final BlockingQueue<Type> blockingQueue;

    public ThreadSafeBlockingQueueImpl() {
        blockingQueue = new LinkedBlockingQueue<>();
    }

    public ThreadSafeBlockingQueueImpl(final int capacity) {
        blockingQueue = new LinkedBlockingQueue<>(capacity);
    }

    @Override
    public int size() {
        return blockingQueue.size();
    }

    @Override
    public void enqueue(final Type element) {
        try {
            blockingQueue.put(element);
        } catch (final Exception exception) {
            logger.error("An exception occurred while placing the element to the queue.", exception);
        }
    }

    @Override
    public Type dequeue() {
        Type element;

        try {
            element = blockingQueue.take();
        } catch (final Exception exception) {
            logger.error("An exception occurred while placing the element to the queue.", exception);

            return null;
        }

        return element;
    }

    @Override
    public void clear() {
        blockingQueue.clear();
    }
}
