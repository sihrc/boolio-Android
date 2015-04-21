package io.boolio.android.helpers;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;

import com.soundcloud.android.crop.Crop;

import java.io.File;
import java.io.IOException;

import io.boolio.android.MainActivity;

/**
 * Created by chris on 2/19/15.
 */
public class PictureHelper {
    final static int REQUEST_TAKE_PHOTO = 0;
    final static int REQUEST_PICK_PHOTO = 1;

    Context context;

    Uri savedUri;

    public void dispatchTakePictureIntent(Activity activity, Fragment fragment) {
        context = activity;
        savedUri = Uri.fromFile(Utils.getTempFile(activity));

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, savedUri);

        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(activity.getPackageManager()) != null) {
            fragment.startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
        }
    }

    public void dispatchChoosePictureIntent(Activity activity, Fragment fragment) {
        context = activity;
        Intent loadPictureIntent = new Intent();
        loadPictureIntent.setType("image/*");
        loadPictureIntent.setAction(Intent.ACTION_GET_CONTENT);
        loadPictureIntent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        // Ensure that there's a camera activity to handle the intent
        if (loadPictureIntent.resolveActivity(activity.getPackageManager()) != null) {
            fragment.startActivityForResult(loadPictureIntent, REQUEST_PICK_PHOTO);
        }
    }

    public boolean onActivityResult(Fragment fragment, int requestCode, int resultCode, Intent data, View view, BitmapCallback callback) {
        if (resultCode != Activity.RESULT_OK) {
            Log.e("PictureHelper", "Intent did not come back successfully");
            return false;
        }

        // Handling Picking Photo
        if (requestCode == REQUEST_PICK_PHOTO || requestCode == REQUEST_TAKE_PHOTO) {
            if (data != null && data.getData() != null) {
                savedUri = data.getData();
            }

            Uri croppedUri = Uri.fromFile(new File(context.getFilesDir() + String.valueOf(System.currentTimeMillis()) + ".png"));
            Crop crop = new Crop(savedUri).output(croppedUri);
            if (view.getWidth() != 0)
                crop.withAspect(view.getWidth(), view.getHeight());
            Intent cropIntent = (Intent) Utils.callPrivateMethod(crop, "getIntent", Context.class, context);
            fragment.startActivityForResult(cropIntent, Crop.REQUEST_CROP);

            savedUri = croppedUri;
            return true;
        }

        if (requestCode == Crop.REQUEST_CROP) {
            Bitmap scaledBitmap = Utils.loadScaledBitmap(context, savedUri, MainActivity.SCREEN_WIDTH, MainActivity.SCREEN_HEIGHT);
            callback.onBitmap(Utils.rotateBitmap(Bitmap.createBitmap(
                    scaledBitmap, 0, 0,
                    scaledBitmap.getWidth(),
                    scaledBitmap.getHeight()), savedUri));
        }

        return false;
    }

    public static interface BitmapCallback {
        public void onBitmap(Bitmap bitmap);
    }
}