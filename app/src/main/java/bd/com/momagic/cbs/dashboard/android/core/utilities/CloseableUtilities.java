package bd.com.momagic.cbs.dashboard.android.core.utilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;

public final class CloseableUtilities {

    private static final Logger logger = LoggerFactory.getLogger(CloseableUtilities.class);

    /**
     * Tries to close the closeable.
     * @param closeable Closeable to be closed.
     */
    public static void tryClose(final Closeable closeable) {
        if (closeable == null) { return; }

        try {
            closeable.close();
        } catch (Exception exception) {
            logger.warn("An exception occurred while closing the closeable.", exception);
        }
    }
}
