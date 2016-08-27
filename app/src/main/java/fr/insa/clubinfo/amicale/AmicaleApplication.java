package fr.insa.clubinfo.amicale;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.ParsePush;

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

        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
        installation.put("GCMSenderId", "301365828536");
        installation.saveInBackground();
        ParsePush.subscribeInBackground("test");
    }
}
