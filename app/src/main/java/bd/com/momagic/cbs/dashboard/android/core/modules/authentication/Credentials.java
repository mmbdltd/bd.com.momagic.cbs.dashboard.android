package bd.com.momagic.cbs.dashboard.android.core.modules.authentication;

import androidx.annotation.NonNull;

import bd.com.momagic.cbs.dashboard.android.core.text.JsonSerializer;
import lombok.Getter;

@Getter
public class Credentials {

    private String email;
    private String password;

    public Credentials setEmail(final String email) {
        this.email = email;

        return this;
    }

    public Credentials setPassword(final String password) {
        this.password = password;

        return this;
    }

    public String toJson(final boolean prettyPrint) {
        return JsonSerializer.serialize(this, prettyPrint);
    }

    @NonNull
    @Override
    public String toString() {
        return toJson(true);
    }
}
