package bd.com.momagic.cbs.dashboard.android;

import bd.com.momagic.cbs.dashboard.android.core.configurations.Configuration;
import bd.com.momagic.cbs.dashboard.android.core.configurations.ConfigurationProvider;
import bd.com.momagic.cbs.dashboard.android.core.dependencyinjection.ServiceProvider;
import bd.com.momagic.cbs.dashboard.android.core.dependencyinjection.SingletonServiceProvider;
import bd.com.momagic.cbs.dashboard.android.core.networking.http.HttpClient;

import android.content.Context;

public class Application extends android.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();

        final Context context = getApplicationContext();
        final Configuration configuration = ConfigurationProvider.tryLoadConfiguration(context);

        try {
            HttpClient.initialize();
        } catch (final Exception exception) {
            exception.printStackTrace();
        }

        final ServiceProvider serviceProvider = SingletonServiceProvider.getInstance();
        serviceProvider.get(HttpClient.class, () -> HttpClient.create(configuration.getHttpClient()));
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}
