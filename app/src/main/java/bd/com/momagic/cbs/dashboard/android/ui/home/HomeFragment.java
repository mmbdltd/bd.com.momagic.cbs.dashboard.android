package bd.com.momagic.cbs.dashboard.android.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import bd.com.momagic.cbs.dashboard.android.R;
import bd.com.momagic.cbs.dashboard.android.core.dependencyinjection.ServiceProvider;
import bd.com.momagic.cbs.dashboard.android.core.dependencyinjection.SingletonServiceProvider;
import bd.com.momagic.cbs.dashboard.android.core.networking.http.HttpClient;
import bd.com.momagic.cbs.dashboard.android.core.networking.http.HttpMethod;
import bd.com.momagic.cbs.dashboard.android.core.networking.http.HttpRequest;
import bd.com.momagic.cbs.dashboard.android.core.networking.http.HttpResponse;
import bd.com.momagic.cbs.dashboard.android.core.threading.AsyncTask;
import bd.com.momagic.cbs.dashboard.android.core.utilities.ThreadUtilities;
import bd.com.momagic.cbs.dashboard.android.databinding.FragmentHomeBinding;
import bd.com.momagic.cbs.dashboard.android.ui.customcardview.CustomCardView;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final CustomCardView cardView = root.findViewById(R.id.custom_card_view);

        binding.refresh.setOnRefreshListener(() -> {
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

            binding.refresh.setRefreshing(false);
        });


        AsyncTask.run(() -> {
            ThreadUtilities.trySleep(3_000);

            System.out.println("I AM INSIDE ASYNC");

            cardView.setTextCenter("10000");
        });

        // final TextView textView = binding.textHome;
        // homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}