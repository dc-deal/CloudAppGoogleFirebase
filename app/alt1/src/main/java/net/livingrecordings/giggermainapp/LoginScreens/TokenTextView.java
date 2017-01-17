package net.livingrecordings.giggermainapp.LoginScreens;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import net.livingrecordings.giggermainapp.R;

/**
 * Created by Kraetzig Neu on 21.12.2016.
 */

public class TokenTextView extends TextView{
    public TokenTextView(Context context) {
        super(context);
    }

    public TokenTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    public void setSelected(boolean selected) {
        super.setSelected(selected);
        setCompoundDrawablesWithIntrinsicBounds(0, 0, selected ? R.drawable.ic_addascount : 0, 0);
    }
}
