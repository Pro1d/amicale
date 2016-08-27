package fr.insa.clubinfo.amicale.receivers;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.preference.PreferenceManager;

import com.parse.ParsePushBroadcastReceiver;

import fr.insa.clubinfo.amicale.R;

/**
 * Created by Proïd on 27/08/2016.
 */

public class PushReceiver extends ParsePushBroadcastReceiver {
    @Override
    protected void onPushReceive(Context context, Intent intent) {
        Resources r = context.getResources();
        boolean notificationEnabled = PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(r.getString(R.string.prefs_notification_key),
                        r.getBoolean(R.bool.prefs_notification_default_value));

        // Call super only if the notifications are enabled in preferences
        if(notificationEnabled) {
            // super will build and show the notification
            super.onPushReceive(context, intent);
        }
    }
}
