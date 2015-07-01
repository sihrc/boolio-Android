package io.boolio.android.fragments.search;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;



import io.boolio.android.MainActivity;
import io.boolio.android.R;
import io.boolio.android.helpers.Glider;

/**
 * Created by chris on 2/16/15.
 */
public class GalleryAdapter extends ArrayAdapter<SearchImage> {
    // Constructed members
    Context context;
    AbsListView.LayoutParams layoutParams;

    public GalleryAdapter(Context context) {
        super(context, -1);
        this.context = context;
        layoutParams = new AbsListView.LayoutParams(MainActivity.SCREEN_WIDTH/3, MainActivity.SCREEN_WIDTH/3);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_search_image, parent, false);
            convertView.setLayoutParams(layoutParams);
        }

        Glider.image((ImageView) convertView, getItem(position).thumbnail);

        return convertView;
    }
}