package bd.com.momagic.cbs.dashboard.android.core.concurrency;

import bd.com.momagic.cbs.dashboard.android.Tuple;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public final class ThreadSafeLong {

    private volatile long value = 0L;

    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock(false);
    private final Lock readLock = readWriteLock.readLock();
    private final Lock writeLock = readWriteLock.writeLock();

    public ThreadSafeLong() { }

    public ThreadSafeLong(final long initialValue) {
        value = initialValue;
    }

    public long getUnsafe() {
        return value;
    }

    public long get() {
        return ThreadSafeExecutor.execute(readLock, this::getUnsafe);
    }

    public void setUnsafe(final long value) {
        this.value = value;
    }

    public void set(final long value) {
        setAndGet(value);
    }

    public long getAndSet(final long value) {
        return ThreadSafeExecutor.execute(writeLock, () -> {
            // storing the previous value into a temporary variable...
            final long previousValue = this.value;
            // assigning the new value...
            setUnsafe(value);

            return previousValue;
        });
    }

    public long setAndGet(final long value) {
        return ThreadSafeExecutor.execute(writeLock, () -> {
            // assigning the new value...
            setUnsafe(value);

            return this.value;
        });
    }

    public long addAndGet(final long valueToAdd) {
        return ThreadSafeExecutor.execute(writeLock, () -> {
            // adding and assigning the new value...
            setUnsafe(value + valueToAdd);

            return value;
        });
    }

    public long subtractAndGet(final long valueToSubtract) {
        return ThreadSafeExecutor.execute(writeLock, () -> {
            // subtracting and assigning the new value...
            setUnsafe(value - valueToSubtract);

            return value;
        });
    }

    public long multiplyAndGet(final long multiplyBy) {
        return ThreadSafeExecutor.execute(writeLock, () -> {
            // multiplying and assigning the new value...
            setUnsafe(value * multiplyBy);

            return value;
        });
    }

    public long divideAndGet(final long divideBy) {
        return ThreadSafeExecutor.execute(writeLock, () -> {
            // dividing and assigning the new value...
            setUnsafe(value / divideBy);

            return value;
        });
    }

    public boolean isLessThan(final long value) {
        return ThreadSafeExecutor.execute(readLock, () -> this.value < value);
    }

    public boolean isLessThanOrEqualTo(final long value) {
        return ThreadSafeExecutor.execute(readLock, () -> this.value <= value);
    }

    public boolean isGreaterThan(final long value) {
        return ThreadSafeExecutor.execute(readLock, () -> this.value > value);
    }

    public boolean isGreaterThanOrEqualTo(final long value) {
        return ThreadSafeExecutor.execute(readLock, () -> this.value >= value);
    }

    @Override
    public boolean equals(Object valueAsObject) {
        if (!(valueAsObject instanceof Long)) { return false; }

        final long valueAsNumber = (long) valueAsObject;

        return ThreadSafeExecutor.execute(readLock, () -> value == valueAsNumber);
    }

    /**
     * This method first adds two values (global value and the value to add)
     * and checks if the result is less than the value to check.
     * If the result is less than the given value to check, it updates the global value
     * and returns true. Otherwise, it does not update the global value and returns false.
     * @implNote This method is synchronized.
     * @param valueToAdd Value to add to the global value.
     * @param valueToCheck Value to compare with.
     * @return A tuple containing a 'boolean' indicating if the value is less than
     * the value to compare and a 'long' containing the current value (after performing the operation).
     */
    public Tuple addIfAfterAdditionLessThan(final long valueToAdd, final long valueToCheck) {
        // acquiring write lock...
        return ThreadSafeExecutor.execute(writeLock, () -> {
            // first we shall add the two values...
            final long result = value + valueToAdd;

            // if after addition, the result is less than
            // the value to check...
            if (result < valueToCheck) {
                // we shall set the result to the value...
                setUnsafe(result);

                // we shall return a tuple containing 'true'...
                return Tuple.of(true, value);
            }

            // otherwise, we shall return a tuple containing 'false'...
            return Tuple.of(false, value);
        });
    }

    /**
     * This method first subtracts the given value from the global value
     * and checks if the result is greater than the value to check.
     * If the result is greater than the given value to check, it updates the global value
     * and returns true. Otherwise, it does not update the global value and returns false.
     * @implNote This method is synchronized.
     * @param valueToSubtract Value to subtract from the global value.
     * @param valueToCheck Value to compare with.
     * @return A tuple containing a 'boolean' indicating if the value is greater than
     * the value to compare and a 'long' containing the current value (after performing the operation).
     */
    public Tuple subtractIfAfterSubtractionGreaterThan(final long valueToSubtract, final long valueToCheck) {
        // acquiring write lock...
        return ThreadSafeExecutor.execute(writeLock, () -> {
            // first we shall subtract the given value from our current global value...
            final long result = value - valueToSubtract;

            // if after subtraction, the result is greater than
            // the value to check...
            if (result > valueToCheck) {
                // we shall set the result to the value...
                setUnsafe(result);

                // we shall return a tuple containing 'true'...
                return Tuple.of(true, value);
            }

            // otherwise, we shall return a tuple containing 'false'...
            return Tuple.of(false, value);
        });
    }
}
