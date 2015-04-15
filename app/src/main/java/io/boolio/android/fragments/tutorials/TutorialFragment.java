package io.boolio.android.fragments.tutorials;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import io.boolio.android.R;
import io.boolio.android.fragments.BoolioFragment;

/**
 * Created by Chris on 4/15/15.
 */
public class TutorialFragment extends BoolioFragment {
    Context context;

    // Indicator Views
    Drawable indicator;

    // Create Custom Tutorial Fragment
    public static TutorialFragment newInstance(int pageImage, int pageText, int numPages, int currentPage) {
        // Create empty fragment
        TutorialFragment fragment = new TutorialFragment();

        // Packaging arguments as a bundle
        Bundle argument = new Bundle();
        argument.putInt("pageImage", pageImage);
        argument.putInt("pageText", pageText);
        argument.putInt("numPages", numPages);
        argument.putInt("currentPage", currentPage);

        // Set Arguments
        fragment.setArguments(argument);
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        context = activity;

        // Preload indicator drawable
        indicator = getResources().getDrawable(R.drawable.shape_page_indicator);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_tutorial, container, false);

        // Find Views
        ImageView tutorialImage = (ImageView) rootView.findViewById(R.id.fragment_tutorial_image);
        TextView tutorialText = (TextView) rootView.findViewById(R.id.fragment_tutorial_text);
        LinearLayout pageIndicators = (LinearLayout) rootView.findViewById(R.id.fragment_tutorial_slides);


        // Set Image and Text
        tutorialText.setText(getArguments().getInt("pageText"));
        tutorialImage.setBackground(getResources().getDrawable(getArguments().getInt("pageImage")));

        // Add Page Indicators
        int currentPage = getArguments().getInt("currentPage");
        View singleIndicator;
        for (int i = 0; i < getArguments().getInt("numPages"); i++) {
            singleIndicator = new View(context);
            singleIndicator.setBackground(indicator);
            if (currentPage == i) {
                singleIndicator.setBackgroundColor(getResources().getColor(R.color.black));
            }

            // Add Indicator to Linear Layout
            pageIndicators.addView(singleIndicator);
        }

        return rootView;
    }
}
