package fr.insa.clubinfo.amicale;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseInstallation;

/**
 * Created by Proïd on 24/08/2016.
 */

public class AmicaleApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Parse.initialize(new Parse.Configuration.Builder(this)
                .build()
        );

        ParseInstallation.getCurrentInstallation().saveInBackground();
    }
}
