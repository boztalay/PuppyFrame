package com.boztalay.puppyframe.configuration.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import com.boztalay.puppyframe.R;

public class SelectableImageView extends ImageView {
    private boolean checked;

    public SelectableImageView(Context context) {
        this(context, null);
    }

    public SelectableImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SelectableImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        checked = false;
    }

    public void toggleChecked() {
        setChecked(!checked);
    }

    public void setChecked(boolean checked) {
        this.checked = checked;

        if(checked) {
            setColorFilter(getResources().getColor(R.color.selected_image_tint));
        } else {
            setColorFilter(0);
        }
    }
}
