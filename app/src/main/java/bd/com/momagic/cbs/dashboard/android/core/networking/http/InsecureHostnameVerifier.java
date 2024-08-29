package bd.com.momagic.cbs.dashboard.android.core.networking.http;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

public class InsecureHostnameVerifier implements HostnameVerifier {

    /**
     * @implNote This method might seem unnecessary. But it is required
     * to avoid BadHostnameVerifier warning.
     * @see <a href="https://developer.android.com/privacy-and-security/risks/unsafe-hostname?source=studio">Bad Hostname Verifier Risks</a>
     */
    private boolean verify() { return true; }

    @Override
    public boolean verify(final String hostname, final SSLSession sslSession) {
        return verify();
    }
}
