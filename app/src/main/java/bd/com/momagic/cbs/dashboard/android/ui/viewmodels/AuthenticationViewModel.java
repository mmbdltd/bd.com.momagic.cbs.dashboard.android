package bd.com.momagic.cbs.dashboard.android.ui.viewmodels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import bd.com.momagic.cbs.dashboard.android.core.dependencyinjection.ServiceProvider;
import bd.com.momagic.cbs.dashboard.android.core.dependencyinjection.SingletonServiceProvider;
import bd.com.momagic.cbs.dashboard.android.core.modules.authentication.AuthenticationService;
import bd.com.momagic.cbs.dashboard.android.core.modules.authentication.AuthenticationServiceImpl;
import bd.com.momagic.cbs.dashboard.android.core.modules.authentication.AuthenticationToken;
import bd.com.momagic.cbs.dashboard.android.core.modules.authentication.Credentials;
import bd.com.momagic.cbs.dashboard.android.core.threading.AsyncTask;
import lombok.Getter;

public class AuthenticationViewModel extends ViewModel {

    private final ServiceProvider serviceProvider = SingletonServiceProvider.getInstance();
    private final AuthenticationService authenticationService
            = serviceProvider.get(AuthenticationServiceImpl.class);

    @Getter
    private final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);
    @Getter
    private final MutableLiveData<String> email = new MutableLiveData<>("jamil@momagicbd.com");
    @Getter
    private final MutableLiveData<String> password = new MutableLiveData<>("momagic123");
    private final MutableLiveData<AuthenticationToken> token = new MutableLiveData<>();

    public MutableLiveData<AuthenticationToken> getToken() {
        final AuthenticationToken token = authenticationService.getAuthenticationToken();

        this.token.setValue(token);

        return this.token;
    }

    public void authenticate() {
        loading.setValue(true);

        AsyncTask.run(() -> {
            final AuthenticationToken token = authenticationService.authenticateAsync(new Credentials()
                    .setEmail(getEmail().getValue())
                    .setPassword(getPassword().getValue())).tryAwait();

            // NOTE: WE CANNOT USE setValue() method because we are inside async task/background thread...
            this.token.postValue(token);
            loading.postValue(false);
        });
    }
}
