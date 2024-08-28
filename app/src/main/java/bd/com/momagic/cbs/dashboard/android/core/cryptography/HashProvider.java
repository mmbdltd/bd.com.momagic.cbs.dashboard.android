package bd.com.momagic.cbs.dashboard.android.core.cryptography;

import bd.com.momagic.cbs.dashboard.android.core.dependencyinjection.ServiceProvider;
import bd.com.momagic.cbs.dashboard.android.core.dependencyinjection.SingletonServiceProvider;
import bd.com.momagic.cbs.dashboard.android.core.text.Encoding;

public interface HashProvider {

    byte[] computeHash(byte[] bytes, HashAlgorithm algorithm) throws Exception;

    byte[] computeHash(String message, HashAlgorithm algorithm) throws Exception;

    String computeHash(String message, HashAlgorithm algorithm, Encoding encoding) throws Exception;

    boolean isMatched(byte[] bytes, byte[] preComputedHashAsBytes, HashAlgorithm algorithm) throws Exception;

    boolean isMatched(String message, byte[] preComputedHashAsBytes, HashAlgorithm algorithm) throws Exception;

    boolean isMatched(String message, String preComputedHash, HashAlgorithm algorithm, Encoding preComputedHashEncoding) throws Exception;

    static HashProvider getInstance() {
        final ServiceProvider serviceProvider = SingletonServiceProvider.getInstance();
        final HashProvider hashProvider = serviceProvider.get(HashProvider.class, HashProviderImpl::new);

        return hashProvider;
    }
}
