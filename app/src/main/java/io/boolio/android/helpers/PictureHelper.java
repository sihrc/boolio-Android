package io.boolio.android.helpers;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.soundcloud.android.crop.Crop;

import java.io.File;

import io.boolio.android.R;

/**
 * Created by chris on 2/19/15.
 */
public class PictureHelper {
    public final static int REQUEST_TAKE_PHOTO = 0;
    public final static int REQUEST_PICK_PHOTO = 1;

    Context context;

    Uri savedUri;

    public PictureHelper(Context context) {
        this.context = context;
    }

    public static boolean isRequest(int code) {
        return code == PictureHelper.REQUEST_PICK_PHOTO || code == PictureHelper.REQUEST_TAKE_PHOTO || code == Crop.REQUEST_CROP;
    }

    public void dispatchTakePictureIntent(Activity activity, Fragment fragment) {
        savedUri = Uri.fromFile(Utils.getTempFile(activity));

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, savedUri);

        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(activity.getPackageManager()) != null) {
            fragment.startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
        }
    }

    public void dispatchChoosePictureIntent(Activity activity, Fragment fragment) {
        Intent loadPictureIntent = new Intent();
        loadPictureIntent.setType("image/*");
        loadPictureIntent.setAction(Intent.ACTION_GET_CONTENT);
        loadPictureIntent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        // Ensure that there's a camera activity to handle the intent
        if (loadPictureIntent.resolveActivity(activity.getPackageManager()) != null) {
            fragment.startActivityForResult(loadPictureIntent, REQUEST_PICK_PHOTO);
        }
    }

    public boolean onActivityResult(Fragment fragment, int requestCode, int resultCode, Intent data, final View view, final BitmapCallback callback) {
        if (resultCode != Activity.RESULT_OK) {
            Log.e("PictureHelper", "Intent did not come back successfully");
            return false;
        }

        // Handling Picking Photo
        if (requestCode == REQUEST_PICK_PHOTO || requestCode == REQUEST_TAKE_PHOTO) {
            if (data != null && data.getData() != null) {
                savedUri = data.getData();
            }

            dispatchCropPictureIntent(fragment, view, savedUri);
            return true;
        }

        if (requestCode == Crop.REQUEST_CROP) {
            new AsyncTask<Void, Void, Bitmap>() {
                @Override
                protected Bitmap doInBackground(Void... params) {

                    return Utils.rotateBitmap(
                        Utils.loadScaledBitmap(context, savedUri, view.getWidth(), view.getHeight()), savedUri);
                }

                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    Toast.makeText(context, R.string.loading_picture, Toast.LENGTH_SHORT).show();
                }

                @Override
                protected void onPostExecute(Bitmap aVoid) {
                    callback.onBitmap(aVoid);
                }
            }.execute();
        }

        return false;
    }

    public void dispatchCropPictureIntent(Fragment fragment, View view, Uri inputUri) {
        Uri croppedUri = Uri.fromFile(new File(context.getFilesDir() + String.valueOf(System.currentTimeMillis()) + ".png"));
        Crop crop = new Crop(inputUri).output(croppedUri);
        if (view.getWidth() != 0)
            crop.withAspect(view.getWidth(), view.getHeight());
        Intent cropIntent = (Intent) Utils.callPrivateMethod(crop, "getIntent", Context.class, context);
        fragment.startActivityForResult(cropIntent, Crop.REQUEST_CROP);

        savedUri = croppedUri;

    }

    public interface BitmapCallback {
        void onBitmap(Bitmap bitmap);
    }
}