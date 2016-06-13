package fr.insa.clubinfo.amicale.helpers;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;

import fr.insa.clubinfo.amicale.R;

/**
 * Created by Proïd on 12/06/2016.
 */

public class Alarm {
    private static final int ALARM_REQUEST_CODE = 2;

    public void createAlarm(Context context, String key, java.util.Date time) {
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent();
        PendingIntent pending = PendingIntent.getActivity(context, ALARM_REQUEST_CODE, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        manager.set(AlarmManager.RTC, time.getTime(), pending);

    }
    public static void cancelAlarm(String key) {

    }
    public static void onActivityResult(int requestCode, int resultCode) {
        if(requestCode == ALARM_REQUEST_CODE) {
            // interface
        }
    }

    public static void executeAlarm(Context context) {
        Resources res = context.getResources();
        Notification notif = new Notification.Builder(context)
                .setContentTitle(res.getString(R.string.washinsa_alarm_notif_title))
                .setContentText(res.getString(R.string.washinsa_alarm_notif_content))
                .setSmallIcon(R.drawable.ic_alarm_black_24dp)
                //.setContentIntent(resultPendingIntent);
                .build();

        int mId = 42;
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
// mId allows you to update the notification later on.
        mNotificationManager.notify(mId, notif);
    }
}
