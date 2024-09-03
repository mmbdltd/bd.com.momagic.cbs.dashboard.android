package bd.com.momagic.cbs.dashboard.android.ui.login;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import bd.com.momagic.cbs.dashboard.android.core.dependencyinjection.ServiceProvider;
import bd.com.momagic.cbs.dashboard.android.core.dependencyinjection.SingletonServiceProvider;
import bd.com.momagic.cbs.dashboard.android.core.modules.authentication.AuthenticationService;
import bd.com.momagic.cbs.dashboard.android.core.modules.authentication.AuthenticationServiceImpl;
import bd.com.momagic.cbs.dashboard.android.core.modules.authentication.AuthenticationToken;
import bd.com.momagic.cbs.dashboard.android.core.modules.authentication.Credentials;
import bd.com.momagic.cbs.dashboard.android.core.threading.AsyncTask;

public class LoginViewModel extends ViewModel {

    private final ServiceProvider serviceProvider = SingletonServiceProvider.getInstance();
    private final AuthenticationService authenticationService
            = serviceProvider.get(AuthenticationServiceImpl.class);

    private final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> authenticated = new MutableLiveData<>(false);
    private final MutableLiveData<String> email = new MutableLiveData<>("jamil@momagicbd.com");
    private final MutableLiveData<String> password = new MutableLiveData<>("momagic123");

    public MutableLiveData<Boolean> getLoading() {
        return loading;
    }

    public MutableLiveData<Boolean> getAuthenticated() {
        return authenticated;
    }

    public MutableLiveData<String> getEmail() {
        return email;
    }

    public MutableLiveData<String> getPassword() {
        return password;
    }

    public void login() {
        loading.setValue(true);

        AsyncTask.run(() -> {
            final AuthenticationToken token = authenticationService.authenticateAsync(new Credentials()
                    .setEmail(getEmail().getValue())
                    .setPassword(getPassword().getValue())).tryAwait();

            // NOTE: WE CANNOT USE setValue() method because we are inside async task/background thread...
            authenticated.postValue(token.isAuthenticated());
            loading.postValue(false);
        });
    }
}
