package io.boolio.android.fragments.search;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.gson.internal.LinkedTreeMap;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import org.lucasr.smoothie.AsyncGridView;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import io.boolio.android.MainActivity;
import io.boolio.android.R;
import io.boolio.android.custom.BoolioSearchView;
import io.boolio.android.helpers.Utils;
import io.boolio.android.network.clients.ExternalClient;
import io.boolio.android.network.helpers.BoolioCallback;
import io.boolio.android.network.BoolioData;

/**
 * Created by Chris on 5/5/15.
 */
public class SearchImageFragment extends DialogFragment {
    BoolioCallback<Uri> callback;
    Activity activity;
    GalleryAdapter galleryAdapter;

    // Image Loading
    View progress;


    public static SearchImageFragment newInstance(BoolioCallback<Uri> savedUri) {
        SearchImageFragment fragment = new SearchImageFragment();
        fragment.callback = savedUri;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_TITLE, android.R.style.Theme_Holo_Light);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dialog_search_image, container, false);
        progress = rootView.findViewById(R.id.progress_bar_saving);
        final BoolioSearchView searchView = (BoolioSearchView) rootView.findViewById(R.id.search_bar);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                galleryAdapter.clear();
                progress.setVisibility(View.VISIBLE);
                try {
                    query = URLEncoder.encode(query, "utf-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                for (int i = 0; i < 2; i++) {
                    ExternalClient.api().getImages(
                            i * 10 + 1,
                            getString(R.string.google_cx_key),
                            getString(R.string.google_api_key),
                            query,
                            new BoolioCallback<BoolioData>() {
                                @Override
                                public void handle(BoolioData objects) {
                                    @SuppressWarnings({"unchecked"})
                                    ArrayList<LinkedTreeMap> items = (ArrayList<LinkedTreeMap>) objects.get("items");
                                    if (items != null) {
                                        for (LinkedTreeMap obj : items) {
                                            LinkedTreeMap image = (LinkedTreeMap) obj.get("image");
                                            if (galleryAdapter != null) {
                                                galleryAdapter.add(new SearchImage((String) image.get("thumbnailLink"), (String) obj.get("link")));
                                            }
                                        }
                                    }

                                    galleryAdapter.notifyDataSetChanged();
                                    progress.setVisibility(View.GONE);
                                }
                            });
                }

                searchView.clearFocus();
                Utils.hideKeyboard(activity, searchView);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        // Setup GridView
        galleryAdapter = new GalleryAdapter(activity);
        AsyncGridView asyncGridView = (AsyncGridView) rootView.findViewById(R.id.search_grid_view);
        asyncGridView.setAdapter(galleryAdapter);
        asyncGridView.setNumColumns(3);
        asyncGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showLargerImage(galleryAdapter.getItem(position).original);
            }
        });

        return rootView;
    }

    private void showImagePreviewDialog(final Bitmap currBitmap) {
        ImageView preview = new ImageView(activity);
        preview.setImageBitmap(currBitmap);
        new AlertDialog.Builder(activity)
                .setView(preview)
                .setPositiveButton("CROP", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Utils.saveBitmapToUri(activity, currBitmap, new Runnable() {
                            @Override
                            public void run() {
                                progress.setVisibility(View.VISIBLE);
                            }
                        }, new BoolioCallback<Uri>() {
                            @Override
                            public void handle(Uri object) {
                                callback.handle(object);
                                progress.setVisibility(View.GONE);
                                SearchImageFragment.this.dismiss();
                            }
                        });
                        dialog.dismiss();
                    }
                }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();
    }


    private void showLargerImage(String original) {
        ImageSize imageSize = new ImageSize(MainActivity.SCREEN_WIDTH, MainActivity.SCREEN_WIDTH);
        ImageLoader.getInstance().loadImage(original, imageSize, new SimpleImageLoadingListener() {
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                showImagePreviewDialog(loadedImage);
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                Toast.makeText(activity, "Sorry, this image is unavailable", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
