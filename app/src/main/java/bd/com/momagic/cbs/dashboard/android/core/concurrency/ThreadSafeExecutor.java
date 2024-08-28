package bd.com.momagic.cbs.dashboard.android.core.concurrency;

import lombok.SneakyThrows;

import java.util.concurrent.locks.Lock;

public final class ThreadSafeExecutor {

    @SneakyThrows
    public static <ReturnType> ReturnType execute(
            Lock lock,
            ThreadSafeAction<ReturnType> action) {
        ReturnType value;

        lock.lock();                // <-- acquiring the lock...

        try {
            value = action.execute();
        } finally {
            lock.unlock();          // <-- releasing the lock...
        }

        return value;
    }
}
