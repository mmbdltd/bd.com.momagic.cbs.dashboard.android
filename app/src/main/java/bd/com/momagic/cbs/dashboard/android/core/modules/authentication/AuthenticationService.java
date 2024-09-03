package bd.com.momagic.cbs.dashboard.android.core.modules.authentication;

import bd.com.momagic.cbs.dashboard.android.core.threading.AsyncTask;

public interface AuthenticationService {
    AuthenticationToken getAuthenticationToken();
    AsyncTask<AuthenticationToken> loadAuthenticationTokenAsync();
    AsyncTask<AuthenticationToken> authenticateAsync(final Credentials credentials);
    AsyncTask<AuthenticationToken> renewTokenAsync(final AuthenticationToken token);
    AsyncTask<AuthenticationToken> logoutAsync();
}
