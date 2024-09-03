package bd.com.momagic.cbs.dashboard.android.core.modules.authentication;

import androidx.annotation.NonNull;

import bd.com.momagic.cbs.dashboard.android.core.text.JsonSerializer;
import lombok.Data;

@Data
public class AuthenticationServiceConfiguration {

    private String loginRequestUrl;
    private String tokenRenewalRequestUrl;
    private String logoutRequestUrl;
    private String tokenFilePathFormat;

    public String toJson(final boolean prettyPrint) {
        return JsonSerializer.serialize(this, prettyPrint);
    }

    @NonNull
    @Override
    public String toString() {
        return toJson(true);
    }
}
