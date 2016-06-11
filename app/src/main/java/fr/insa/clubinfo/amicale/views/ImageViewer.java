package fr.insa.clubinfo.amicale.views;

import android.content.Context;
import android.view.View;

import fr.insa.clubinfo.amicale.R;
import fr.insa.clubinfo.amicale.adapters.DraweePagerAdapter;
import fr.insa.clubinfo.amicale.interfaces.ImageList;
import fr.insa.clubinfo.amicale.interfaces.OnImageClickedListener;
import me.relex.circleindicator.CircleIndicator;

/**
 * Created by Proïd on 10/06/2016.
 */

public class ImageViewer {

    private static ImageViewer staticImageViewer;

    public static void instantiateImageViewer(View viewer, View defaultView) {
        staticImageViewer = new ImageViewer(viewer, defaultView);
    }
    public static ImageViewer getImageViewer() {
        return staticImageViewer;
    }

    private DraweePagerAdapter adapter;
    MultiTouchViewPager viewPager;
    private View fullscreenViewer;
    private View defaultView;
    private boolean isVisible = false;

    private ImageViewer(View viewer, View defaultView) {
        CircleIndicator indicator = (CircleIndicator) viewer.findViewById(R.id.image_viewer_circle_indicator);
        viewPager = (MultiTouchViewPager) viewer.findViewById(R.id.image_viewer_multi_touch_view_pager);
        adapter = new DraweePagerAdapter(viewPager);
        viewPager.setAdapter(adapter);
        indicator.setViewPager(viewPager);
        fullscreenViewer = viewer;
        this.defaultView = defaultView;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void hide() {
        fullscreenViewer.setVisibility(View.GONE);
        defaultView.setVisibility(View.VISIBLE);
        isVisible = false;
    }

    public void show(ImageList images, int position) {
        fullscreenViewer.setVisibility(View.VISIBLE);
        defaultView.setVisibility(View.GONE);
        isVisible = true;
        adapter.update(images);
        viewPager.setCurrentItem(position);
    }
}
