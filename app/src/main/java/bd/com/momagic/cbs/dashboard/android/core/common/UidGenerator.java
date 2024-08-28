package bd.com.momagic.cbs.dashboard.android.core.common;

import bd.com.momagic.cbs.dashboard.android.core.dependencyinjection.ServiceProvider;
import bd.com.momagic.cbs.dashboard.android.core.dependencyinjection.SingletonServiceProvider;

public interface UidGenerator {

    String generate();

    static UidGenerator getInstance() {
        final ServiceProvider serviceProvider = SingletonServiceProvider.getInstance();
        final UidGenerator uidGenerator = serviceProvider.get(UidGenerator.class, UidGeneratorImpl::new);

        return uidGenerator;
    }
}
