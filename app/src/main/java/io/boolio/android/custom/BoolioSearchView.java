package io.boolio.android.custom;

import android.content.Context;
import android.text.InputType;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import io.boolio.android.R;


/**
 * Created by Chris on 5/5/15.
 */
public class BoolioSearchView extends SearchView {
    Context context;

    public BoolioSearchView(Context context) {
        super(context);
        this.context = context;
        init();
    }

    private void init() {
        setIconified(false);
        setIconifiedByDefault(false);

        /** Setting the search icon **/
        int searchButton = context.getResources().getIdentifier("android:id/search_mag_icon", null, null);
        //Getting the 'search_plate' LinearLayout.
        ImageView searchPlate = (ImageView) findViewById(searchButton);
        //Setting background of 'search_plate' to earlier defined drawable.
        searchPlate.setImageResource(R.drawable.search);

        /** Setting the Text Color **/
        int id = context.getResources().getIdentifier("android:id/search_src_text", null,
            null);
        TextView textView = (TextView) findViewById(id);
        textView.setSingleLine();
        textView.setTextColor(context.getResources().getColor(R.color.text_white));
        textView.setHintTextColor(context.getResources().getColor(R.color.text_white));

        /** Other Attributes **/
        setQueryHint(context.getString(R.string.search_bar_text));
        setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
    }

    public BoolioSearchView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

}
