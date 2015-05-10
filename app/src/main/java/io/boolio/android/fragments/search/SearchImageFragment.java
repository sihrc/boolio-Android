package io.boolio.android.fragments.search;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;

import org.lucasr.smoothie.AsyncGridView;

import java.util.List;

import io.boolio.android.MainActivity;
import io.boolio.android.R;
import io.boolio.android.custom.BoolioSearchView;
import io.boolio.android.helpers.BoolioCallback;
import io.boolio.android.helpers.Utils;
import io.boolio.android.network.NetworkCallback;
import io.boolio.android.network.ServerGoogle;

/**
 * Created by Chris on 5/5/15.
 */
public class SearchImageFragment extends DialogFragment {
    BoolioCallback<Uri> callback;
    Activity activity;
    GalleryAdapter galleryAdapter;

    // Image Loading
    ImageLoader imageLoader;
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
        this.imageLoader = ServerGoogle.getInstance(activity).getImageLoader();
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
                ServerGoogle.getInstance(activity).getImages(query, new NetworkCallback<List<SearchImage>>() {
                    @Override
                    public void handle(List<SearchImage> object) {
                        if (galleryAdapter != null) {
                            galleryAdapter.addAll(object);
                            galleryAdapter.notifyDataSetChanged();
                        }

                        progress.setVisibility(View.GONE);
                    }
                });
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
        imageLoader.get(original, new ImageLoader.ImageListener() {
            @Override
            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                if (response.getBitmap() != null)
                    showImagePreviewDialog(response.getBitmap());
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(activity, "Sorry, this image is unavailable", Toast.LENGTH_SHORT).show();
            }
        }, MainActivity.SCREEN_WIDTH, MainActivity.SCREEN_WIDTH);
    }
}
