package fr.insa.clubinfo.amicale.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

/**
 * First child must be ProgressBar, second child must be ImageView.
 * The progressBar is added if it is not initially present
 *
 * Created by Proïd on 06/06/2016.
 */

public class SwitchImageViewAsyncLayout extends FrameLayout {
    private static final int imageViewIndex = 1;
    private static final int progressBarIndex = 0;

    public SwitchImageViewAsyncLayout(Context context) {
        super(context);
        addProgressBar();
    }

    public SwitchImageViewAsyncLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        addProgressBar();
    }

    public SwitchImageViewAsyncLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        addProgressBar();
    }
    private void addProgressBar() {
        // Create and add a layout to this view
        RelativeLayout rl = new RelativeLayout(getContext());
        rl.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        this.addView(rl);

        // Create a progress bar
        ProgressBar pb = new ProgressBar(getContext());
        pb.setVisibility(View.GONE);

        // Add the progress bar in the center of the layout
        RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        rlp.addRule(RelativeLayout.CENTER_IN_PARENT);
        rl.addView(pb, rlp);
    }

    public void hideAll() {
        getChildAt(imageViewIndex).setVisibility(View.GONE);
        getChildAt(progressBarIndex).setVisibility(View.GONE);
        ((ImageView)getChildAt(imageViewIndex)).setImageDrawable(null);
    }

    public void showProgressView() {
        getChildAt(imageViewIndex).setVisibility(View.GONE);
        getChildAt(progressBarIndex).setVisibility(View.VISIBLE);
        ((ImageView)getChildAt(imageViewIndex)).setImageDrawable(null);
    }

    public void showImageView(Bitmap drawable) {
        getChildAt(progressBarIndex).setVisibility(View.GONE);
        getChildAt(imageViewIndex).setVisibility(View.VISIBLE);
        if(drawable != null) {
            ImageView view = ((ImageView) getChildAt(imageViewIndex));
            view.setDrawingCacheEnabled(false);
            view.setImageBitmap(drawable);
            view.setDrawingCacheEnabled(true);
        }
    }
}
