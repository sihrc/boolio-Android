package io.boolio.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;

/**
 * Created by Chris on 3/13/15.
 */
public class FTUEFragment extends Fragment {
    /**
     * Allows for quick and easy generation of variable FTUEFragments
     * @param bgDrawable - resource id for drawable
     * @param message - resource id for message
     * @return FTUEFragment with specific drawable and message
     */
    public static FTUEFragment getFTUEFragment(int bgDrawable, int message) {
        Bundle arguments = new Bundle();
        arguments.putInt("bg", bgDrawable);
        arguments.putInt("message", message);

        FTUEFragment ftueFragment = new FTUEFragment();
        ftueFragment.setArguments(arguments);
        return ftueFragment;
    }
}
