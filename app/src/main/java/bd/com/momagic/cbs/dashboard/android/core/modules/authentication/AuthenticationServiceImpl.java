package bd.com.momagic.cbs.dashboard.android.core.modules.authentication;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import bd.com.momagic.cbs.dashboard.android.core.concurrency.ThreadSafeExecutor;
import bd.com.momagic.cbs.dashboard.android.core.configurations.Configuration;
import bd.com.momagic.cbs.dashboard.android.core.configurations.ConfigurationProvider;
import bd.com.momagic.cbs.dashboard.android.core.dependencyinjection.ServiceProvider;
import bd.com.momagic.cbs.dashboard.android.core.dependencyinjection.SingletonServiceProvider;
import bd.com.momagic.cbs.dashboard.android.core.networking.MediaType;
import bd.com.momagic.cbs.dashboard.android.core.networking.http.HttpClient;
import bd.com.momagic.cbs.dashboard.android.core.networking.http.HttpHeader;
import bd.com.momagic.cbs.dashboard.android.core.networking.http.HttpMethod;
import bd.com.momagic.cbs.dashboard.android.core.networking.http.HttpRequest;
import bd.com.momagic.cbs.dashboard.android.core.networking.http.HttpResponse;
import bd.com.momagic.cbs.dashboard.android.core.networking.http.HttpStatusCode;
import bd.com.momagic.cbs.dashboard.android.core.text.JsonSerializer;
import bd.com.momagic.cbs.dashboard.android.core.threading.AsyncTask;
import bd.com.momagic.cbs.dashboard.android.core.utilities.FileSystemUtilities;
import bd.com.momagic.cbs.dashboard.android.core.utilities.JsonWebTokenUtilities;
import bd.com.momagic.cbs.dashboard.android.core.utilities.NumberUtilities;
import bd.com.momagic.cbs.dashboard.android.core.utilities.StreamUtilities;
import bd.com.momagic.cbs.dashboard.android.core.utilities.StringUtilities;
import bd.com.momagic.cbs.dashboard.android.core.utilities.WriterUtilities;

public class AuthenticationServiceImpl implements AuthenticationService {

    private final Logger logger = LoggerFactory.getLogger(AuthenticationServiceImpl.class);
    private final ServiceProvider serviceProvider = SingletonServiceProvider.getInstance();
    private final HttpClient httpClient = serviceProvider.get(HttpClient.class);

    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock(false);
    private final Lock readLock = readWriteLock.readLock();
    private final Lock writeLock = readWriteLock.writeLock();
    private final Lock fileLock = new ReentrantLock(false);
    private AuthenticationToken token = new AuthenticationToken();

    private static final long ONE_MINUTE_IN_MILLISECONDS = 60 * 1000;

    private void storeAuthenticationToken(final AuthenticationToken token) {
        final String filePath = prepareTokenFilePath();
        final boolean written = ThreadSafeExecutor.execute(fileLock,
                () -> WriterUtilities.writeString(token.toJson(true), filePath, false));

        if (written) { return; }

        logger.warn("Failed to store authentication token in device storage. For more details, please check previous logs.");
    }

    private void setAuthenticationToken(final AuthenticationToken token) {
        writeLock.lock();

        try {
            this.token = token;
        } finally {
            writeLock.unlock();
        }
    }

    private AuthenticationServiceImpl() { }

    @Override
    public AuthenticationToken getAuthenticationToken() {
        AuthenticationToken token;

        readLock.lock();

        try {
            token = this.token;
        } finally {
            readLock.unlock();
        }

        return token;
    }

    @Override
    public AsyncTask<AuthenticationToken> loadAuthenticationTokenAsync() {
        // preparing token file path...
        final String filePath = prepareTokenFilePath();
        final String tokenAsJson = StringUtilities.getDefaultIfNullOrWhiteSpace(
                StreamUtilities.readString(filePath),
                StringUtilities.getEmptyString(),
                true);

        if (StringUtilities.isEmpty(tokenAsJson)) { return AsyncTask.from(getAuthenticationToken()); }

        final AuthenticationToken token = JsonSerializer.deserialize(tokenAsJson, AuthenticationToken.class);

        if (token == null) { return AsyncTask.from(getAuthenticationToken()); }

        // saving the token in memory...
        setAuthenticationToken(token);

        return AsyncTask.from(token);
    }

    @Override
    public AsyncTask<AuthenticationToken> authenticateAsync(final Credentials credentials) {
        // retrieving service configuration...
        final AuthenticationServiceConfiguration configuration
                = ConfigurationProvider.getConfiguration().getAuthenticationService();
        final AuthenticationToken token = new AuthenticationToken();
        final AsyncTask<AuthenticationToken> asyncTask = AsyncTask.from(token);
        // sending authentication request...
        final HttpResponse<String> httpResponse = httpClient.sendRequestAsync(HttpRequest.createForString()
                .setMethod(HttpMethod.POST)
                .setUrl(configuration.getLoginRequestUrl())
                .addHeader(HttpHeader.ACCEPT, MediaType.APPLICATION_JSON)
                .addHeader(HttpHeader.CONTENT_TYPE, MediaType.APPLICATION_JSON_UTF8)
                .setBody(credentials.toJson(false))).tryAwait();

        // if an exception occurred...
        if (httpResponse.getException() != null) {
            token.setMessage(httpResponse.getException().getMessage());

            // we shall return the exception...
            return asyncTask;
        }

        // setting status code...
        token.setStatusCode(httpResponse.getStatusCode());
        // setting message...
        token.setMessage(httpResponse.getMessage());

        final String responseBody = StringUtilities.getDefaultIfNullOrWhiteSpace(
                httpResponse.getBodyAsString(), StringUtilities.getEmptyString(), true);

        if (StringUtilities.isEmpty(responseBody)) {
            token.setMessage("No response body received from the server.");

            return asyncTask;
        }

        if (token.getStatusCode() != HttpStatusCode.OK.getValue()) { return asyncTask; }

        final InternalAuthenticationResponse internalAuthenticationResponse
                = JsonSerializer.deserialize(responseBody, InternalAuthenticationResponse.class);

        if (internalAuthenticationResponse == null) {
            token.setStatusCode(HttpStatusCode.INTERNAL_SERVER_ERROR.getValue());
            token.setMessage("Invalid response body received from the server.");

            return asyncTask;
        }

        if (!internalAuthenticationResponse.isSuccess()
                || internalAuthenticationResponse.getStatus_code() != HttpStatusCode.OK.getValue()) {
            token.setStatusCode(internalAuthenticationResponse.getStatus_code());
            token.setMessage("Authentication failed.");

            return asyncTask;
        }

        token
                .setAuthenticated(true)
                .setEmail(credentials.getEmail())
                .setPassword(credentials.getPassword())
                .setName(internalAuthenticationResponse.getUser().getName())
                .setMobile(internalAuthenticationResponse.getUser().getMobile())
                .setTokenType(internalAuthenticationResponse.getUser().getToken_type())
                .setAccessToken(internalAuthenticationResponse.getUser().getAccess_token())
                .setRefreshToken(internalAuthenticationResponse.getUser().getRefresh_token())
                .setAccessTokenExpiresAtInMilliseconds(extractTokenExpiresAtInMilliseconds(token.getAccessToken()))
                .setRefreshTokenExpiresAtInMilliseconds(extractTokenExpiresAtInMilliseconds(token.getRefreshToken()));

        // setting the token in memory...
        setAuthenticationToken(token);
        // saving the token for later use...
        storeAuthenticationToken(token);

        return asyncTask;
    }

    @Override
    public AsyncTask<AuthenticationToken> renewTokenAsync(final AuthenticationToken token) {
        // if provided token is null, we shall return an empty async task...
        if (token == null) { return AsyncTask.from(getAuthenticationToken()); }

        // otherwise, we shall clone the token...
        final AuthenticationToken _token = ThreadSafeExecutor.execute(
                writeLock, token::cloneAuthenticationToken);

        // if refresh token has expired...
        if (_token.hasRefreshTokenExpired()) {
            // we shall authenticate...
            return authenticateAsync(new Credentials()
                    .setEmail(_token.getEmail())
                    .setPassword(_token.getPassword()));
        }

        // retrieving configuration...
        final AuthenticationServiceConfiguration configuration
                = ConfigurationProvider.getConfiguration().getAuthenticationService();
        final AsyncTask<AuthenticationToken> asyncTask = AsyncTask.from(_token);
        final Map<String, String> requestBody = new HashMap<>();
        requestBody.put("refresh_token", _token.getRefreshToken());
        // sending authentication request...
        final HttpResponse<String> httpResponse = httpClient.sendRequestAsync(HttpRequest.createForString()
                .setMethod(HttpMethod.POST)
                .setUrl(configuration.getTokenRenewalRequestUrl())
                .addHeader(HttpHeader.ACCEPT, MediaType.APPLICATION_JSON)
                .addHeader(HttpHeader.CONTENT_TYPE, MediaType.APPLICATION_JSON_UTF8)
                .setBody(JsonSerializer.serialize(requestBody, false))).tryAwait();

        // if an exception occurred...
        if (httpResponse.getException() != null) {
            _token.setMessage(httpResponse.getException().getMessage());

            // we shall return the exception...
            return asyncTask;
        }

        // setting status code...
        _token.setStatusCode(httpResponse.getStatusCode());
        // setting message...
        _token.setMessage(httpResponse.getMessage());

        final String responseBody = StringUtilities.getDefaultIfNullOrWhiteSpace(
                httpResponse.getBodyAsString(), StringUtilities.getEmptyString(), true);

        if (StringUtilities.isEmpty(responseBody)) {
            _token.setMessage("No response body received from the server.");

            return asyncTask;
        }

        if (_token.getStatusCode() != HttpStatusCode.OK.getValue()) { return asyncTask; }

        final Map<String, Object> response = JsonSerializer.deserializeAsMap(responseBody);

        if (response == null) {
            _token.setStatusCode(HttpStatusCode.INTERNAL_SERVER_ERROR.getValue());
            _token.setMessage("Invalid response body received from the server.");

            return asyncTask;
        }

        final Object statusCode = response.get("status_code");

        if (statusCode == null || !statusCode.equals(HttpStatusCode.OK.getValue())) {
            _token.setStatusCode(HttpStatusCode.BAD_REQUEST.getValue());
            _token.setMessage("Token renewal failed.");

            return asyncTask;
        }

        final String tokenType = (String) response.get("token_type");
        final String accessToken = (String) response.get("access_token");
        final String refreshToken = (String) response.get("refresh_token");

        _token
                .setAuthenticated(true)
                .setTokenType(tokenType)
                .setAccessToken(accessToken)
                .setRefreshToken(refreshToken)
                .setAccessTokenExpiresAtInMilliseconds(extractTokenExpiresAtInMilliseconds(_token.getAccessToken()))
                .setRefreshTokenExpiresAtInMilliseconds(extractTokenExpiresAtInMilliseconds(_token.getRefreshToken()));

        // saving the token in memory...
        setAuthenticationToken(_token);
        // saving the token for later use...
        storeAuthenticationToken(_token);

        return asyncTask;
    }

    @Override
    public AsyncTask<AuthenticationToken> logoutAsync() {
        final AuthenticationToken token = new AuthenticationToken();

        // resetting the token in-memory...
        setAuthenticationToken(token);

        final String filePath = prepareTokenFilePath();

        // deleting the file...
        ThreadSafeExecutor.execute(fileLock, () -> FileSystemUtilities.deleteFile(filePath));

        return AsyncTask.from(token);
    }

    private static long extractTokenExpiresAtInMilliseconds(final String token) {
        final Object tokenExpiresAtInSecondsAsObject = JsonWebTokenUtilities.extractValue("exp", token);

        if (tokenExpiresAtInSecondsAsObject == null) { return 0L; }

        final long tokenExpiresAtInSeconds = NumberUtilities.tryParseLong(tokenExpiresAtInSecondsAsObject.toString(), 0L);
        // NOTE: SUBTRACTING ONE MINUTE DELIBERATELY TO ENSURE EARLY TOKEN EXPIRATION...!!!
        final long tokenExpiresAtInMilliseconds = (tokenExpiresAtInSeconds * 1000L) - ONE_MINUTE_IN_MILLISECONDS;

        return tokenExpiresAtInMilliseconds < 1L ? 0L : tokenExpiresAtInMilliseconds;
    }

    private static String prepareTokenFilePath() {
        // retrieving configuration...
        final Configuration configuration = ConfigurationProvider.getConfiguration();
        // retrieving service configuration...
        final AuthenticationServiceConfiguration serviceConfiguration = configuration.getAuthenticationService();
        // preparing file path...
        String filePath = serviceConfiguration.getTokenFilePathFormat()
                .replace("{{applicationDataDirectory}}", configuration.getFilesDirectory())
                .replace("{{profile}}", configuration.getProfile());
        // sanitizing the path...
        filePath = FileSystemUtilities.sanitizePath(filePath);

        return filePath;
    }
}
