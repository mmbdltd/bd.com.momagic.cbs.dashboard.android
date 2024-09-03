package bd.com.momagic.cbs.dashboard.android;

import android.os.Bundle;
import android.view.Menu;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;

import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bd.com.momagic.cbs.dashboard.android.core.dependencyinjection.ServiceProvider;
import bd.com.momagic.cbs.dashboard.android.core.dependencyinjection.SingletonServiceProvider;
import bd.com.momagic.cbs.dashboard.android.core.modules.authentication.AuthenticationService;
import bd.com.momagic.cbs.dashboard.android.core.modules.authentication.AuthenticationServiceImpl;
import bd.com.momagic.cbs.dashboard.android.core.modules.authentication.AuthenticationToken;
import bd.com.momagic.cbs.dashboard.android.databinding.ActivityMainBinding;
import bd.com.momagic.cbs.dashboard.android.ui.viewmodels.AuthenticationViewModel;

public class MainActivity extends AppCompatActivity {

    private final Logger logger = LoggerFactory.getLogger(MainActivity.class);
    private final ServiceProvider serviceProvider = SingletonServiceProvider.getInstance();
    private final AuthenticationService authenticationService
            = serviceProvider.get(AuthenticationServiceImpl.class);
    private AppBarConfiguration mAppBarConfiguration;
    private TextView name;
    private TextView email;
    private TextView mobile;
    private AuthenticationViewModel authenticationViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        authenticationViewModel = new ViewModelProvider(this)
                .get(AuthenticationViewModel.class);

        final ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        binding.setAuthenticationViewModel(authenticationViewModel);
        binding.setLifecycleOwner(this);
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);
        binding.appBarMain.fab.setOnClickListener(view -> { });

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        name = navigationView.getHeaderView(0).findViewById(R.id.name);
        email = navigationView.getHeaderView(0).findViewById(R.id.email);
        mobile = navigationView.getHeaderView(0).findViewById(R.id.mobile);

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_login,
                R.id.nav_home,
                R.id.nav_gallery,
                R.id.nav_slideshow
                )
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        final Button logoutButton = navigationView.findViewById(R.id.logout_button);

        authenticationViewModel.getToken().observe(this, token -> {
            final boolean authenticated = token.isAuthenticated();

            name.setText(token.getName());
            email.setText(token.getEmail());
            mobile.setText(token.getMobile());

            drawer.setDrawerLockMode(authenticated
                    ? DrawerLayout.LOCK_MODE_UNLOCKED
                    : DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

            navController.popBackStack(authenticated ? R.id.nav_login : R.id.nav_home, true);
            navController.navigate(authenticated ? R.id.nav_home : R.id.nav_login);
        });

        // adding logout button action...
        logoutButton.setOnClickListener(view -> {
            final AuthenticationToken token = authenticationService.logoutAsync().tryAwait();

            authenticationViewModel.getToken().setValue(token);
        });

        // this will make sure to navigate to home if a user is already authenticated...
        authenticationViewModel.getToken().setValue(
                authenticationService.getAuthenticationToken());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}
