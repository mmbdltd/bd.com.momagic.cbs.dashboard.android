package bd.com.momagic.cbs.dashboard.android.core.utilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Semaphore;

public final class SemaphoreUtilities {

    private static final Logger logger = LoggerFactory.getLogger(SemaphoreUtilities.class);

    public static boolean tryAcquireSemaphore(final Semaphore semaphore) {
        if (semaphore == null) {
            logger.warn("Could not acquire the semaphore because it is null.");

            return false;
        }

        try {
            semaphore.acquire();
        } catch (Exception exception) {
            logger.warn("An exception occurred while acquiring the semaphore.", exception);

            return false;
        }

        // logger.log(Level.DEBUG, "Successfully acquired the semaphore.");

        return true;
    }

    public static void releaseSemaphore(final Semaphore semaphore) {
        if (semaphore == null) {
            logger.warn("Could not release the semaphore because it is null.");

            return;
        }

        semaphore.release();

        // logger.log(Level.DEBUG, "Successfully released the semaphore.");
    }
}
