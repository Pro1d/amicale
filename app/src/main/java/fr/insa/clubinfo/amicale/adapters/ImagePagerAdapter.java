
package fr.insa.clubinfo.amicale.adapters;

import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import fr.insa.clubinfo.amicale.interfaces.ImageList;
import uk.co.senab.photoview.PhotoView;

/**
 * Created by Proïd on 10/06/2016.
 */

public class ImagePagerAdapter extends PagerAdapter {
    private final ArrayList<PhotoView> views = new ArrayList<>();
    private final ViewPager attachedViewPager;

    public ImagePagerAdapter(ViewPager pager) {
        attachedViewPager = pager;
    }

    public void update(ImageList images) {
        int count = images.getImageCount();

        // Delete last views if too many
        while(views.size() > count)
            removeView(views.size()-1);

        // Replace image
        for(int i = 0; i < views.size(); i++) {
            createView(views.get(i), images.getImage(i));
        }

        // Add views and set image if necessary
        while(views.size() < count) {
            PhotoView v = createView(null, images.getImage(views.size()));
            addView(v, views.size());
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemPosition(Object object)
    {
        int index = views.indexOf(object);
        if(index == -1)
            return POSITION_NONE;
        else
            return index;
    }

    @Override
    public int getCount() {
        return views.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public Object instantiateItem(ViewGroup viewGroup, int position) {
        View view = views.get(position);
        viewGroup.addView(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        return view;
    }

    private PhotoView createView(PhotoView recycleView, Bitmap image) {
        final PhotoView photoView = (recycleView == null ? new PhotoView(attachedViewPager.getContext()) : recycleView);
        photoView.setImageBitmap(image);
        photoView.setScale(1.0f);
        return photoView;
    }

    private void addView(PhotoView v, int position) {
        views.add(position, v);
    }

    private void removeView(int position) {
        attachedViewPager.setAdapter(null);
        views.remove(position);
        attachedViewPager.setAdapter(this);
    }
}
