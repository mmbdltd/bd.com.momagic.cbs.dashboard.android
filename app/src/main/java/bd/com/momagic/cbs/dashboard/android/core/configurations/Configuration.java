package bd.com.momagic.cbs.dashboard.android.core.configurations;

import androidx.annotation.NonNull;

import bd.com.momagic.cbs.dashboard.android.core.modules.authentication.AuthenticationServiceConfiguration;
import bd.com.momagic.cbs.dashboard.android.core.networking.http.HttpClientConfiguration;
import bd.com.momagic.cbs.dashboard.android.core.text.JsonSerializer;
import bd.com.momagic.cbs.dashboard.android.core.threading.AsyncTaskConfiguration;
import lombok.Data;

@Data
public final class Configuration {

    private double version;
    private String instanceId;
    private String applicationName;
    private String profile;
    private String cacheDirectory;
    private String filesDirectory;
    private HttpClientConfiguration httpClient;
    private AuthenticationServiceConfiguration authenticationService;
    private AsyncTaskConfiguration asyncTask;

    public String toJson(final boolean prettyPrint) {
        return JsonSerializer.serialize(this, prettyPrint);
    }

    @NonNull
    @Override
    public String toString() {
        return toJson(true);
    }
}
