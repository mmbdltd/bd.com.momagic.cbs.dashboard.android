package bd.com.momagic.cbs.dashboard.android.core.configurations;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bd.com.momagic.cbs.dashboard.android.BuildConfig;
import bd.com.momagic.cbs.dashboard.android.R;
import bd.com.momagic.cbs.dashboard.android.core.text.JsonSerializer;
import bd.com.momagic.cbs.dashboard.android.core.utilities.StreamUtilities;

import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import android.content.Context;

public final class ConfigurationProvider {

    private static final Logger logger = LoggerFactory.getLogger(ConfigurationProvider.class);
    private static final ReadWriteLock readWriteLock = new ReentrantReadWriteLock(false);
    private static final Lock readLock = readWriteLock.readLock();
    private static final Lock writeLock = readWriteLock.writeLock();
    private static Configuration configuration;

    private ConfigurationProvider() { }

    /**
     * Retrieves input stream (based on profile) to read configuration.
     * @return Input stream to read configuration.
     */
    private static InputStream getConfigurationAsStream(final String profile, final Context context) {
        final String configurationFileName = "configuration_" + profile + ".json";

        logger.info("Loading configuration, '" + configurationFileName + "'.");

        return "production".equals(profile)
                ? context.getResources().openRawResource(R.raw.configuration_production)
                : context.getResources().openRawResource(R.raw.configuration_development);
    }

    /**
     * Sets system properties from the configuration.
     * @param configuration Configuration from which the system properties shall be set.
     * @return The configuration.
     */
    private static Configuration setSystemProperties(Configuration configuration) {
        final Properties properties = System.getProperties();

        // gets the http client configuration...
        /*var httpClientConfiguration = configuration.getHttpClient();

        // if the http client configuration is not null...
        if (httpClientConfiguration != null) {
            // sets http client properties...
            // disables hostname verification for HTTP client if security is disabled...
            properties.setProperty("jdk.internal.httpclient.disableHostnameVerification",
                    "" + httpClientConfiguration.isHostnameVerificationDisabled());
            // sets the number of times the basic authentication filter will attempt
            // to retry a failed authentication...
            properties.setProperty("jdk.httpclient.auth.retrylimit",
                    "" + httpClientConfiguration.getBasicAuthenticationRetryLimit());
        }*/

        return configuration;
    }

    public static Configuration loadConfiguration(final Context context) throws Exception {
        logger.info("Loading configuration.");

        // retrieves configuration as stream from resources...
        final InputStream inputStream = getConfigurationAsStream(BuildConfig.PROFILE, context);
        // reads JSON content from the input stream...
        final String content = StreamUtilities.readString(inputStream);
        // parses the JSON content as configuration...
        Configuration configuration = JsonSerializer.deserialize(content, Configuration.class);

        // if configuration is null, we shall throw exception...
        if (configuration == null) {
            throw new Exception("Configuration could not be loaded. Please check previous logs for more details.");
        }

        // setting application context to configuration...
        configuration.setContext(context);

        // this method shall set configuration values
        // to system properties...
        configuration = setSystemProperties(configuration);

        // if configuration is loaded successfully,
        // we'll set that to our static variable...
        setConfiguration(configuration);

        logger.info("Successfully loaded configuration for \"" + configuration.getProfile() + "\" profile.");

        return ConfigurationProvider.configuration;
    }

    public static Configuration tryLoadConfiguration(final Context context) {
        try {
            return loadConfiguration(context);
        } catch (Exception exception) {
            logger.error("An exception occurred while loading configuration.", exception);
        }

        return null;
    }

    public static Configuration getConfiguration() {
        Configuration configuration;

        readLock.lock();        // <-- thread synchronization starts here...

        configuration = ConfigurationProvider.configuration;

        readLock.unlock();      // <-- thread synchronization ends here...

        return configuration;
    }

    private static void setConfiguration(Configuration configuration) {
        writeLock.lock();        // <-- thread synchronization starts here...

        ConfigurationProvider.configuration = configuration;

        writeLock.unlock();      // <-- thread synchronization ends here...
    }
}
