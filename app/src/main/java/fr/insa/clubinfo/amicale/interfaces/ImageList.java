package fr.insa.clubinfo.amicale.interfaces;

import android.graphics.Bitmap;

/**
 * Created by Proïd on 10/06/2016.
 */

public interface ImageList {
    Bitmap getImage(int position);
    int getCount();
}