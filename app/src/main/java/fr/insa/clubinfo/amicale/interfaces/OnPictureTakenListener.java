package fr.insa.clubinfo.amicale.interfaces;

import android.graphics.Bitmap;

/**
 * Created by Proïd on 06/06/2016.
 */

public interface OnPictureTakenListener {
    void onPictureTaken();
    /** @param drawable can be null if loading failed */
    void onPictureLoaded(Bitmap drawable);
}
