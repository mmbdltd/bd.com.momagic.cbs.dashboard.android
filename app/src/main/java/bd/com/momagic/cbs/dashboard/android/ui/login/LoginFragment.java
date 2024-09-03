package bd.com.momagic.cbs.dashboard.android.ui.login;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;

import bd.com.momagic.cbs.dashboard.android.databinding.FragmentLoginBinding;
import bd.com.momagic.cbs.dashboard.android.ui.viewmodels.AuthenticationViewModel;

public class LoginFragment extends Fragment {

    private FragmentLoginBinding binding;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        binding = FragmentLoginBinding.inflate(inflater, container, false);

        final View root = binding.getRoot();
        final AuthenticationViewModel authenticationViewModel = new ViewModelProvider(requireActivity()).get(AuthenticationViewModel.class);
        binding.setAuthenticationViewModel(authenticationViewModel);
        binding.setLifecycleOwner(getViewLifecycleOwner());

        // we don't want to show the hamburger menu options for login screen...
        hindMenuOptions(getActivity());

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        binding = null;
    }

    private static void hindMenuOptions(final FragmentActivity fragmentActivity) {
        final AppCompatActivity activity = (AppCompatActivity) fragmentActivity;

        if (activity == null) { return; }

        final ActionBar actionBar = activity.getSupportActionBar();

        if (actionBar == null) { return; }

        actionBar.setDisplayHomeAsUpEnabled(false);
    }
}
