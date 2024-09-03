package bd.com.momagic.cbs.dashboard.android.core.modules.authentication;

import androidx.annotation.NonNull;

import bd.com.momagic.cbs.dashboard.android.core.text.JsonSerializer;
import lombok.Data;

@Data
class InternalAuthenticationResponse {

    private int errorCode;
    private boolean success;
    private int status_code;
    private String message;
    private UserData user;

    public String toJson(final boolean prettyPrint) {
        return JsonSerializer.serialize(this, prettyPrint);
    }

    @NonNull
    @Override
    public String toString() {
        return toJson(true);
    }

    @Data
    static final class UserData {
        private long id;
        private String email;
        private String mobile;
        private String name;
        private String token_type;
        private String access_token;
        private String refresh_token;

        public String toJson(final boolean prettyPrint) {
            return JsonSerializer.serialize(this, prettyPrint);
        }

        @NonNull
        @Override
        public String toString() {
            return toJson(true);
        }
    }
}
