package ca.benwu.fingerflinger.view;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.TextView;

import ca.benwu.fingerflinger.R;

/**
 * Created by Ben Wu on 12/28/2015.
 */
public class CustomRegularTextView extends TextView {

    private Context mContext;

    public CustomRegularTextView(Context context) {
        super(context, null);
        mContext = context;
        init();
    }

    public CustomRegularTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs, 0);
        mContext = context;
        init();
    }

    private void init() {
        setTypeface(Typeface.createFromAsset(mContext.getAssets(), "fonts/Quicksand-Regular.otf"));
        setTextColor(mContext.getResources().getColor(R.color.defaultText));
    }
}
