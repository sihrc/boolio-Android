package io.boolio.android.fragments.search;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SearchView;

import com.google.gson.internal.LinkedTreeMap;

import org.lucasr.smoothie.AsyncGridView;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnItemClick;
import io.boolio.android.MainActivity;
import io.boolio.android.R;
import io.boolio.android.custom.BoolioSearchView;
import io.boolio.android.helpers.Glider;
import io.boolio.android.helpers.Utils;
import io.boolio.android.network.BoolioData;
import io.boolio.android.network.clients.ExternalClient;
import io.boolio.android.network.helpers.BoolioCallback;

/**
 * Created by Chris on 5/5/15.
 */
public class SearchImageFragment extends DialogFragment {
    BoolioCallback<Uri> callback;
    Activity activity;
    GalleryAdapter galleryAdapter;

    // Image Loading
    @Bind(R.id.progress_bar_saving) View progress;
    @Bind(R.id.search_bar) BoolioSearchView searchView;
    @Bind(R.id.search_grid_view) AsyncGridView asyncGridView;


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

    @OnItemClick(R.id.search_grid_view) void onItemSelected(int position) {
        showLargerImage(galleryAdapter.getItem(position).original);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dialog_search_image, container, false);
        ButterKnife.bind(this, rootView);

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
        asyncGridView.setAdapter(galleryAdapter);
        asyncGridView.setNumColumns(3);

        return rootView;
    }

    private void showLargerImage(final String original) {
        progress.setVisibility(View.VISIBLE);
        Glider.getBitmap(original, MainActivity.SCREEN_WIDTH, MainActivity.SCREEN_HEIGHT, new BoolioCallback<Bitmap>() {
            @Override
            public void handle(Bitmap resObj) {
                progress.setVisibility(View.GONE);
                final ImageView preview = new ImageView(activity);
                preview.setImageBitmap(resObj);
                new AlertDialog.Builder(activity)
                    .setView(preview)
                    .setPositiveButton("CROP", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Bitmap bm = ((BitmapDrawable) preview.getDrawable()).getBitmap();
                            if (bm == null) {
                                return;
                            }
                            Utils.saveBitmapToUri(activity, bm, new Runnable() {
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
        });
    }
}
