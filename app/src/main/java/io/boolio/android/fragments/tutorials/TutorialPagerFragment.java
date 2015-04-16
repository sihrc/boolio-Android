package io.boolio.android.fragments.tutorials;

import java.util.ArrayList;
import java.util.List;

import io.boolio.android.R;
import io.boolio.android.fragments.BoolioFragment;

/**
 * Created by Chris on 4/15/15.
 */
public class TutorialPagerFragment extends BoolioFragment {
    List<TutorialFragment> fragmentList;

    public static TutorialPagerFragment newInstance() {
        TutorialPagerFragment tutorialPagerFragment = new TutorialPagerFragment();
        tutorialPagerFragment.setTutorials(
                // Tutorial I
                R.drawable.logo, R.string.tutorial_1,

                // Tutorial II
                R.drawable.logo, R.string.tutorial_1,

                // Tutorial III
                R.drawable.logo, R.string.tutorial_1,

                // Tutorial IV
                R.drawable.logo, R.string.tutorial_1
        );
        return tutorialPagerFragment;
    }

    private void setTutorials(Integer... args) {
        fragmentList = new ArrayList<>();
        int total = args.length;
        int index= 0,
           order = 0;
        while (index < total)
            fragmentList.add(TutorialFragment.newInstance(args[index++], args[index++], total, order++));
    }
}
