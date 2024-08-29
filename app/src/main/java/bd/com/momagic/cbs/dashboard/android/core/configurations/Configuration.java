package bd.com.momagic.cbs.dashboard.android.core.configurations;

import android.content.Context;

import com.fasterxml.jackson.annotation.JsonIgnore;

import bd.com.momagic.cbs.dashboard.android.core.networking.http.HttpClientConfiguration;
import bd.com.momagic.cbs.dashboard.android.core.text.JsonSerializer;
import lombok.Data;

@Data
public final class Configuration {

    private double version;
    private String instanceId;
    private String applicationName;
    private String profile;
    @JsonIgnore
    private Context context;
    private HttpClientConfiguration httpClient;

    public String toJson(final boolean prettyPrint) {
        return JsonSerializer.serialize(this, prettyPrint);
    }

    @Override
    public String toString() {
        return toJson(true);
    }
}
