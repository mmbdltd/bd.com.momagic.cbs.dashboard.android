package bd.com.momagic.cbs.dashboard.android.core.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import bd.com.momagic.cbs.dashboard.android.core.concurrency.ThreadSafeExecutor;
import bd.com.momagic.cbs.dashboard.android.core.utilities.StringUtilities;

class UidGeneratorImpl implements UidGenerator {

    private volatile long count = INITIAL_COUNT;
    private volatile long previousTimeInMilliseconds = -1L;

    private final Lock lock = new ReentrantLock(false);

    private static final Logger logger = LoggerFactory.getLogger(UidGeneratorImpl.class);
    private static final int MINIMUM_RANDOM_VALUE = 1000;
    private static final int MAXIMUM_RANDOM_VALUE = 9999;
    private static final long INITIAL_COUNT = 1L;

    UidGeneratorImpl() { }

    private long getNextCount(final long currentTimeInMilliseconds) {
        return ThreadSafeExecutor.execute(lock, () -> {
            // computing next count...
            // if previous time is equal to the current time...
            final long count = previousTimeInMilliseconds == currentTimeInMilliseconds
                    ? this.count + 1            // <-- we shall increment the count...
                    : INITIAL_COUNT;            // <-- otherwise, we shall reset the count with the initial value...

            // then we shall re-assign the global count...
            this.count = count;
            // we'll then assign the current time to the previous time (global)...
            previousTimeInMilliseconds = currentTimeInMilliseconds;

            // lastly, we shall return the count...
            return count;
        });
    }

    @Override
    public String generate() {
        // retrieving the unique value of this application instance from configuration...
        // NOTE: THIS UNIQUE VALUE ENSURES THAT NO TWO APPLICATION INSTANCES CAN GENERATE THE SAME UNIQUE ID...
        final String uniqueValue = StringUtilities.getEmptyString();
        // taking the enhanced current system time (in milliseconds)...
        final long currentTimeInMilliseconds = EnhancedTimeProvider.getCurrentTimeInMilliseconds();
        // getting the next count value...
        final long count = getNextCount(currentTimeInMilliseconds);
        // getting a thread local random generator...
        final Random random = ThreadLocalRandom.current();
        // we shall generate a random value within the pre-defined range...
        final int randomValue = 1 + MINIMUM_RANDOM_VALUE + random.nextInt(MAXIMUM_RANDOM_VALUE);

        // appending all the values to prepare a unique ID...
        // lastly, we shall return the unique ID...
        return uniqueValue + count + currentTimeInMilliseconds + randomValue;
    }
}
