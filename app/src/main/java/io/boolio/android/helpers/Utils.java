package io.boolio.android.helpers;

import android.content.Context;

/**
 * Created by Chris on 4/17/15.
 */
public class Utils {

    public static String formatTimeDifferences(String value) {
        long diff = (System.currentTimeMillis() - Long.parseLong(value)) / 1000;
        StringBuilder buf = new StringBuilder();

        buf.append("");
        if (diff < 0L) {
            diff = Math.abs(diff);
        }
        if (diff == 0) {
            return "1s";
        } else if (diff < 60L) {
            buf.append(diff).append("s");
        } else if (diff < 3600L) {
            buf.append(diff / 60L).append("m");
        } else if (diff < 86400L) {
            buf.append(diff / 3600L).append("h");
        } else if (diff < 604800L) {
            buf.append(diff / 86400L).append("d");
        } else if (diff > 604800L) {
            buf.append(diff / 604800L).append("w");
        }
        return buf.toString();
    }

    public static int convertDpToPixel(Context context, int dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }
}
