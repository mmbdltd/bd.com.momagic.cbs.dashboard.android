package bd.com.momagic.cbs.dashboard.android;

import bd.com.momagic.cbs.dashboard.android.core.configurations.Configuration;
import bd.com.momagic.cbs.dashboard.android.core.configurations.ConfigurationProvider;
import bd.com.momagic.cbs.dashboard.android.core.dependencyinjection.ServiceProvider;
import bd.com.momagic.cbs.dashboard.android.core.dependencyinjection.SingletonServiceProvider;
import bd.com.momagic.cbs.dashboard.android.core.networking.http.HttpClient;

import android.content.Context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Application extends android.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();

        final Logger logger = LoggerFactory.getLogger(Application.class);
        final Context context = getApplicationContext();
        final Configuration configuration = ConfigurationProvider.tryLoadConfiguration(context);

        if (configuration == null) {
            logger.warn("Configuration was not loaded. For more details, please check previous logs.");

            return;
        }

        try {
            HttpClient.initialize();
        } catch (final Exception exception) {
            logger.error("An exception occurred while initializing the HTTP client.", exception);

            return;
        }

        final ServiceProvider serviceProvider = SingletonServiceProvider.getInstance();
        serviceProvider.get(HttpClient.class, () -> HttpClient.create(configuration.getHttpClient()));
    }
}
