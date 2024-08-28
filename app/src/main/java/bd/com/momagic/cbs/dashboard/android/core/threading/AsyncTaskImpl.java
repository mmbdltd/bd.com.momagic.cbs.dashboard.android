package bd.com.momagic.cbs.dashboard.android.core.threading;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Future;

class AsyncTaskImpl<Type> implements AsyncTask<Type> {

    private final Logger logger = LoggerFactory.getLogger(AsyncTaskImpl.class);
    private final Type data;
    private final Future<Type> future;
    private final Exception exception;

    private AsyncTaskImpl() {
        this(null, null, null);
    }

    private AsyncTaskImpl(Type data) {
        this(data, null, null);
    }

    private AsyncTaskImpl(Future<Type> future) {
        this(null, future, null);
    }

    private AsyncTaskImpl(Exception exception) {
        this(null, null, exception);
    }

    private AsyncTaskImpl(Type data, Future<Type> future, Exception exception) {
        this.data = data;
        this.future = future;
        this.exception = exception;
    }

    @Override
    public Type getData() {
        return data;
    }

    @Override
    public Future<Type> getFuture() {
        return future;
    }

    @Override
    public Exception getException() {
        return exception;
    }

    @Override
    public Type await() throws Exception {
        // returns the data...
        if (data != null) { return data; }
        // throws exception if the exception is not null...
        if (exception != null) { throw exception; }
        // if future is null, we shall return null...
        if (future == null) { return null; }

        Type result;

        try {
            // waits for the future to complete and
            // retrieves the result...
            result = future.get();
        } catch (Exception exception) {
            logger.error("An exception occurred while awaiting the task.", exception);

            throw exception;
        }

        return result;
    }

    @Override
    public Type tryAwait() {
        Type result;

        try {
            result = await();
        } catch (Exception exception) {
            // if exception is encountered, returns null...
            return null;
        }

        return result;
    }

    static <Type> AsyncTask<Type> from(Type data) {
        return new AsyncTaskImpl<>(data);
    }

    static <Type> AsyncTask<Type> from(Future<Type> future) {
        return new AsyncTaskImpl<>(future);
    }

    static <Type> AsyncTask<Type> from(Exception exception) {
        return new AsyncTaskImpl<>(exception);
    }

    static <Type> AsyncTask<Type> empty() { return new AsyncTaskImpl<>(); }
}
