package io.boolio.android.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import io.boolio.android.R;

/**
 * Created by Chris on 4/16/15.
 */
public class LoginFragment extends BoolioFragment {
    public static LoginFragment newInstance(){
        LoginFragment fragment= new LoginFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_login, container, false);

        // Find Views and Buttons
        EditText loginEmail = (EditText) rootView.findViewById(R.id.login_email);
        EditText loginPassword = (EditText) rootView.findViewById(R.id.login_password);
        Button loginButton = (Button) rootView.findViewById(R.id.login_button);

        return rootView;
    }
}
