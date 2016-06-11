package fr.insa.clubinfo.amicale;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;

/**
 * Created by Proïd on 10/06/2016.
 */

public class AmicaleApplication extends Application {
    @Override public void onCreate() {
        super.onCreate();
        Fresco.initialize(getApplicationContext());
    }
}
