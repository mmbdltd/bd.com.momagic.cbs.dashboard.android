package bd.com.momagic.cbs.dashboard.android;

import bd.com.momagic.cbs.dashboard.android.core.configurations.ConfigurationProvider;
import android.content.Context;

public class Application extends android.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();

        final Context context = getApplicationContext();

        ConfigurationProvider.tryLoadConfiguration(context);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}
