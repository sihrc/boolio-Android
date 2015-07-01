package io.boolio.android.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.boolio.android.R;

/**
 * Created by Chris on 4/16/15.
 */
public class LoginFragment extends BoolioFragment {
    @Bind(R.id.fb_login) Button fbButton;
    @Bind(R.id.login_button) Button loginButton;

    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_login, container, false);
        ButterKnife.bind(this, rootView);

        // Setup Custom Facebook Logo
        fbButton.invalidate();
        loginButton.setCompoundDrawablesWithIntrinsicBounds(
            fbButton.getCompoundDrawables()[0], null, null, null);
        loginButton.invalidate();

        return rootView;
    }

    @OnClick(R.id.login_button)
    void onClick() {
        fbButton.performClick();
    }
}
