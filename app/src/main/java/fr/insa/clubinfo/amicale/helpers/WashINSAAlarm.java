package fr.insa.clubinfo.amicale.helpers;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Message;
import android.util.Log;

import java.util.GregorianCalendar;
import java.util.Timer;
import java.util.TimerTask;

import fr.insa.clubinfo.amicale.MainActivity;
import fr.insa.clubinfo.amicale.R;
import fr.insa.clubinfo.amicale.dialogs.WashINSAAlarmCancelDialog;
import fr.insa.clubinfo.amicale.dialogs.WashINSAAlarmStopDialog;
import fr.insa.clubinfo.amicale.models.LaundryMachine;

/**
 * Created by Proïd on 12/06/2016.
 */

public class WashINSAAlarm extends BroadcastReceiver {
    private static final String ALARM_EXTRA = "fr.insa.clubinfo.amicale.WashINSAlarmEXTRA";
    private static final int ALARM_WHAT = 0;
    private static final int ALARM_ACTION_CANCEL_ALARM_CONFIRMATION = 0xAACAC;
    private static final int ALARM_ACTION_ALARM_RINGING = 0xAAA12;
    private static final int NOTIFICATION_REQUEST_CODE = 4;
    private static final int ALARM_REQUEST_CODE = 3;
    private static final int ALARM_NOTIFICATION_ID = 42;

    private static LaundryMachine machine;

    /*** Delayed alarm (Notification et pending intent) ***/

    public static void createDelayedAlarm(Context context, Date time, LaundryMachine machine) {
        WashINSAAlarm.machine = machine;

        // Cancel currently existing alarm
        cancelDelayedAlarm(context);

        // Create an alarm pending intent
        createAlarmIntent(context, time);

        // Build and Show a notification
        createNotification(context, time);
    }

    public static void cancelDelayedAlarm(Context context) {
        cancelAlarmIntent(context);
        clearNotification(context);
    }


    /*** Alarm pending intent ***/

    private static PendingIntent currentPendingIntent;

    private static void createAlarmIntent(Context context, Date time) {
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(context.getApplicationContext(), WashINSAAlarm.class);
        intent.putExtra(ALARM_EXTRA, ALARM_ACTION_ALARM_RINGING);
        currentPendingIntent = PendingIntent.getBroadcast(context, ALARM_REQUEST_CODE, intent, 0);

        manager.set(AlarmManager.RTC_WAKEUP, time.getTimeInMillis(), currentPendingIntent);
    }

    private static void cancelAlarmIntent(Context context) {
        if(currentPendingIntent != null) {
            AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            manager.cancel(currentPendingIntent);
            currentPendingIntent = null;
        }
    }


    /*** Notification ***/

    private static void createNotification(Context context, Date time) {
        Resources res = context.getResources();
        String content = res.getString(R.string.washinsa_alarm_notif_content, machine.getNumber(),
                time.getDate().get(GregorianCalendar.HOUR_OF_DAY),
                time.getDate().get(GregorianCalendar.MINUTE));

        // Pending intent on click
        Intent intent = new Intent(context.getApplicationContext(), WashINSAAlarm.class);
        intent.putExtra(ALARM_EXTRA, ALARM_ACTION_CANCEL_ALARM_CONFIRMATION);
        PendingIntent resultPendingIntent = PendingIntent.getBroadcast(context, NOTIFICATION_REQUEST_CODE, intent, 0);

        Notification notification = new Notification.Builder(context)
                .setContentTitle(res.getString(R.string.washinsa_alarm_notif_title))
                .setSmallIcon(R.drawable.ic_alarm_on_black_24dp)
                .setContentText(content)
                .setTicker(content)
                .setShowWhen(false)
                .setOngoing(true)
                .setContentIntent(resultPendingIntent)
                .build();

        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(ALARM_NOTIFICATION_ID, notification);
    }

    private static void clearNotification(Context context) {
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(ALARM_NOTIFICATION_ID);
    }


    /*** Ringtone ***/

    private static void playRingtone(final Context context) {
        AlarmPlayer.playSound(context);

        // Auto stop the alarm after 60 sec
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                stopRingtone();
            }
        }, 1000 * 60);
    }

    public static void stopRingtone() {
        AlarmPlayer.stopSound();
    }


    /** Called when intent alarm is fired **/
    @Override
    public void onReceive(Context context, Intent intent) {
        // Transfer action request to the gui
        Log.i("###", "onReceive action:"+intent.getIntExtra(ALARM_EXTRA, -1)+" "+intent.getExtras().getInt(ALARM_EXTRA, -1)+" "+intent.hasExtra(ALARM_EXTRA));
        int action = intent.getExtras().getInt(ALARM_EXTRA, -1);
        if(action == -1)
            return;


        if (MainActivity.handler == null) {
            Intent activityIntent = new Intent(context, MainActivity.class);
            activityIntent.putExtra(ALARM_EXTRA, action);
            activityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            context.startActivity(activityIntent);
        } else {
            Message msg = new Message();
            msg.what = ALARM_WHAT;
            msg.arg1 = action;
            MainActivity.handler.sendMessage(msg);
        }
    }


    /** Called when the activity has been started by this receiver **/
    public static void onActivityCreated(Intent intent, Context context) {
        if(intent.hasExtra(ALARM_EXTRA)) {
            int action = intent.getIntExtra(ALARM_EXTRA, -1);
            executeAlarmActionGUI(action, context);
        }
    }

    public static void handleMessage(Message msg, Context context) {
        if(msg.what == ALARM_WHAT)
            executeAlarmActionGUI(msg.arg1, context);
    }

    private static void executeAlarmActionGUI(int action, Context context) {
        if(action == ALARM_ACTION_CANCEL_ALARM_CONFIRMATION) {
            WashINSAAlarmCancelDialog.showDialog(context, machine);
        }
        else if(action == ALARM_ACTION_ALARM_RINGING) {
            cancelDelayedAlarm(context);
            playRingtone(context);
            WashINSAAlarmStopDialog.showDialog(context, machine);
        }
    }
}
