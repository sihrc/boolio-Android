package io.boolio.android.helpers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

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

    public static File getTempFile(Context context) {
        File tempFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separatorChar + "Android/data/" + context.getPackageName() + "/files");
        if (!tempFile.exists() && !tempFile.mkdirs()) {
            Log.e("PictureHelper", "Unable to make Temp File");
            return null;
        }

        return new File(tempFile, System.currentTimeMillis() + ".jpg");
    }

    public static List<String> parseStringArray(String input) {
        List<String> result = new ArrayList<>();
        if (input.isEmpty())
            return result;

        Collections.addAll(result, input.replace(" ", "").split(","));

        return result;
    }

    /**
     * ACCESS PRIVATE METHOD *
     */
    public static Object callPrivateMethod(Object T, String methodName, Class returnVal, Object... args) {
        try {
            Method privateStringMethod = T.getClass().
                    getDeclaredMethod(methodName, returnVal);
            privateStringMethod.setAccessible(true);

            return privateStringMethod.invoke(T, args);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        Log.e("Private Method Accessor", "Something went wrong");
        return null;
    }

    /**
     * SCALE DOWN A BITMAP BEFORE LOADING
     */
    public static Bitmap loadScaledBitmap(Context context, Uri uri, int width, int height) {
        if (width == 0 || height == 0)
            Log.e("BitmapHelper", "Height or Width is 0! " + width + "x" + height);
        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(streamFromUri(context, uri), null, bmOptions);

        // Determine how much to scale down the image
        int scaleFactor = calculateInSampleSize(bmOptions, width, height);
        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        return BitmapFactory.decodeStream(streamFromUri(context, uri), null, bmOptions);
    }

    public static void saveBitmapToUri(final Context context, final Bitmap bitmap, final Runnable asyncRunning, final BoolioCallback<Uri> callback) {
        new AsyncTask<Void, Void, Void>() {
            Uri uri;

            @Override
            protected void onPreExecute() {
                if (asyncRunning != null)
                    asyncRunning.run();
                uri = Uri.fromFile(getTempFile(context));
            }

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    bitmap.compress(Bitmap.CompressFormat.PNG, 90, context.getContentResolver().openOutputStream(uri));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                callback.handle(uri);
            }
        }.execute();
    }

    private static java.io.InputStream streamFromUri(Context context, Uri uri) {
        try {
            return context.getContentResolver().openInputStream(uri);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Bitmap Loading Helpers
    private static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        if (reqWidth == 0 || reqHeight == 0)
            Log.e("BitmapHelpers", "calculateInSampleSize was given 0 width or height");
        final int width = options.outWidth;
        final int height = options.outHeight;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            if (width > height) {
                inSampleSize = (int) Math.ceil((float) height / (float) reqHeight);
            } else {
                inSampleSize = (int) Math.ceil((float) width / (float) reqWidth);
            }
        }

        return inSampleSize;
    }

    public static Bitmap rotateBitmap(Bitmap source, Uri file) {
        int orientation = 0;
        try {
            switch (new ExifInterface(file.getPath()).getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    orientation = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    orientation = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    orientation = 270;
                    break;
                // etc.
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Matrix matrix = new Matrix();
        matrix.postRotate(orientation);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    public static ArrayToStringBuilder<String> stringArrayToString = new ArrayToStringBuilder<String>() {
        @Override
        public String getItem(String object) {
            return object;
        }
    };

    public static abstract class ArrayToStringBuilder<T> {
        public String build(Collection<T> objects) {
            if (objects == null) {
                return "";
            }

            StringBuilder builder = new StringBuilder();
            for (T object : objects) {
                builder.append(getItem(object));
                builder.append(",");
            }

            return builder.length() > 1 ? builder.substring(0, builder.length() - 1) : builder.toString();
        }

        public abstract String getItem(T object);
    }
}
