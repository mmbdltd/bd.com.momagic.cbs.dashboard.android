package bd.com.momagic.cbs.dashboard.android.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import bd.com.momagic.cbs.dashboard.android.core.threading.AsyncTask;
import bd.com.momagic.cbs.dashboard.android.core.utilities.ThreadUtilities;
import bd.com.momagic.cbs.dashboard.android.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        binding.swipeRefreshLayout.setOnRefreshListener(this);

        AsyncTask.run(() -> {
            ThreadUtilities.trySleep(2_000);

            binding.banglalinkRevenue.showDangerAlert();

            binding.banglalinkRevenue.setCustomCardViewCenterTextNumericValue(9999);
        });

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        binding = null;
    }

    @Override
    public void onRefresh() {
        // cardView.setTextCenter("10000");

            /*final ServiceProvider serviceProvider = SingletonServiceProvider.getInstance();
            final HttpClient httpClient = serviceProvider.get(HttpClient.class);
            final HttpResponse<String> response = httpClient.sendRequestAsync(HttpRequest.createForString()
                            .setMethod(HttpMethod.POST)
                            .setUrl("http://192.168.33.109/api/controlCenter/v1.0/control")
                            .setBody("{ \"packetType\": \"SERVER_STATISTICS\" }")
                    // .setUrl("https://www.google.com")
            ).tryAwait();

            System.out.println(response.getBodyAsString());
            System.out.println(response.getMessage());*/

        // System.out.println("Refreshed");

        binding.swipeRefreshLayout.setRefreshing(false);
    }
}
