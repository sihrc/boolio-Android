package io.boolio.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import io.boolio.auth.BoolioAuth;
import io.boolio.auth.FacebookAuth;

/**
 * Created by Chris on 3/13/15.
 * Handles the Login View
 * TODO - layout for boolio / facebook login
 */
public class LoginFragment extends Fragment {

    /**
     * Creates new instance of LoginFragment with arguments.
     * Constructors should be public and take no arguments.
     * @param message - Login message to show
     * @return LoginFragment - Fragment to show
     */
    public static LoginFragment newInstance(int message) {
        Bundle arguments = new Bundle();
        arguments.putInt("message", message);

        LoginFragment fragment = new LoginFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    private void setupBoolioAuth() {
        BoolioAuth.getInstance();
    }

    private void setupFacebookAuth() {
        FacebookAuth.getInstance();
    }
}
