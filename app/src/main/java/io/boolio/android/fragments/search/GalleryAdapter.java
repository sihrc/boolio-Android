package io.boolio.android.fragments.search;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import io.boolio.android.MainActivity;
import io.boolio.android.R;
import io.boolio.android.network.ServerGoogle;

/**
 * Created by chris on 2/16/15.
 */
public class GalleryAdapter extends ArrayAdapter<SearchImage> {
    // Constructed members
    Context context;
    ImageLoader imageLoader;
    AbsListView.LayoutParams layoutParams;

    public GalleryAdapter(Context context) {
        super(context, -1);
        this.context = context;
        imageLoader = ServerGoogle.getInstance(context).getImageLoader();
        layoutParams = new AbsListView.LayoutParams(MainActivity.SCREEN_WIDTH/3, MainActivity.SCREEN_WIDTH/3);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_search_image, parent, false);
            convertView.setLayoutParams(layoutParams);
        }

        ((NetworkImageView) convertView).setImageUrl(getItem(position).thumbnail, imageLoader);

        return convertView;
    }
}