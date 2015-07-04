package io.boolio.android.helpers;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import io.boolio.android.R;

/**
 * Created by Chris on 5/6/15.
 */
public class Dialogs {
    public static void messageDialog(Context context, int title, int message, final Runnable runnable) {
        new AlertDialog.Builder(context).setTitle(title).setMessage(message)
            .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (runnable != null)
                        runnable.run();
                    dialog.dismiss();
                }
            }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();
    }
}
