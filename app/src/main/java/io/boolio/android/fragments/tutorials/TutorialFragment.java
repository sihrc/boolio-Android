package io.boolio.android.fragments.tutorials;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.res.ResourcesCompat;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.boolio.android.R;
import io.boolio.android.fragments.BoolioFragment;

/**
 * Created by Chris on 4/15/15.
 */
public class TutorialFragment extends BoolioFragment {
    @Bind(R.id.fragment_tutorial_image) ImageView tutorialImage;
    @Bind(R.id.fragment_tutorial_text) TextView tutorialText;

    // Create Custom Tutorial Fragment
    public static TutorialFragment newInstance(int pageImage, int pageText) {
        // Create empty fragment
        TutorialFragment fragment = new TutorialFragment();

        // Packaging arguments as a bundle
        Bundle argument = new Bundle();
        argument.putInt("pageImage", pageImage);
        argument.putInt("pageText", pageText);

        // Set Arguments
        fragment.setArguments(argument);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_tutorial, container, false);
        ButterKnife.bind(this, rootView);

        // Set Image and Text
        tutorialText.setText(Html.fromHtml(getString(getArguments().getInt("pageText"))));
        tutorialImage.setBackground(ResourcesCompat.getDrawable(getResources(), getArguments().getInt("pageImage"), activity.getTheme()));

        return rootView;
    }
}
