package bd.com.momagic.cbs.dashboard.android.core.modules.authentication;

import androidx.annotation.NonNull;

import bd.com.momagic.cbs.dashboard.android.core.text.JsonSerializer;
import bd.com.momagic.cbs.dashboard.android.core.utilities.StringUtilities;
import lombok.Getter;

@Getter
public class AuthenticationToken {

    private boolean authenticated = false;
    private int statusCode = -1;
    private String message = StringUtilities.getEmptyString();
    private String name = StringUtilities.getEmptyString();
    private String email = StringUtilities.getEmptyString();
    private String password = StringUtilities.getEmptyString();
    private String mobile = StringUtilities.getEmptyString();
    private String tokenType = StringUtilities.getEmptyString();
    private String accessToken = StringUtilities.getEmptyString();
    private String refreshToken = StringUtilities.getEmptyString();
    private long accessTokenExpiresAtInMilliseconds = 0L;
    private long refreshTokenExpiresAtInMilliseconds = 0L;

    public boolean hasAccessTokenExpired() {
        if (accessTokenExpiresAtInMilliseconds == 0L) { return false; }

        final long currentTimeInMilliseconds = System.currentTimeMillis();

        return accessTokenExpiresAtInMilliseconds < currentTimeInMilliseconds;
    }

    public boolean hasRefreshTokenExpired() {
        if (refreshTokenExpiresAtInMilliseconds == 0L) { return false; }

        final long currentTimeInMilliseconds = System.currentTimeMillis();

        return refreshTokenExpiresAtInMilliseconds < currentTimeInMilliseconds;
    }

    public AuthenticationToken setAuthenticated(final boolean authenticated) {
        this.authenticated = authenticated;

        return this;
    }

    public AuthenticationToken setStatusCode(final int statusCode) {
        this.statusCode = statusCode;

        return this;
    }

    public AuthenticationToken setMessage(final String message) {
        this.message = message;

        return this;
    }

    public AuthenticationToken setName(final String name) {
        this.name = name;

        return this;
    }

    public AuthenticationToken setEmail(final String email) {
        this.email = email;

        return this;
    }

    public AuthenticationToken setPassword(final String password) {
        this.password = password;

        return this;
    }

    public AuthenticationToken setMobile(final String mobile) {
        this.mobile = mobile;

        return this;
    }

    public AuthenticationToken setTokenType(final String tokenType) {
        this.tokenType = tokenType;

        return this;
    }

    public AuthenticationToken setAccessToken(final String accessToken) {
        this.accessToken = accessToken;

        return this;
    }

    public AuthenticationToken setRefreshToken(final String refreshToken) {
        this.refreshToken = refreshToken;

        return this;
    }

    public AuthenticationToken setAccessTokenExpiresAtInMilliseconds(final long accessTokenExpiresAtInMilliseconds) {
        this.accessTokenExpiresAtInMilliseconds = accessTokenExpiresAtInMilliseconds;

        return this;
    }

    public AuthenticationToken setRefreshTokenExpiresAtInMilliseconds(final long refreshTokenExpiresAtInMilliseconds) {
        this.refreshTokenExpiresAtInMilliseconds = refreshTokenExpiresAtInMilliseconds;

        return this;
    }

    public AuthenticationToken cloneAuthenticationToken() {
        return new AuthenticationToken()
                .setAuthenticated(authenticated)
                .setStatusCode(statusCode)
                .setMessage(message)
                .setName(name)
                .setEmail(email)
                .setPassword(password)
                .setMobile(mobile)
                .setTokenType(tokenType)
                .setAccessToken(accessToken)
                .setRefreshToken(refreshToken)
                .setAccessTokenExpiresAtInMilliseconds(accessTokenExpiresAtInMilliseconds)
                .setRefreshTokenExpiresAtInMilliseconds(refreshTokenExpiresAtInMilliseconds);
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
