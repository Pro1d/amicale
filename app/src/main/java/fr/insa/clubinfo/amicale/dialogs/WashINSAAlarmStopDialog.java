package fr.insa.clubinfo.amicale.dialogs;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;

import fr.insa.clubinfo.amicale.R;
import fr.insa.clubinfo.amicale.helpers.WashINSAAlarm;
import fr.insa.clubinfo.amicale.models.LaundryMachine;

/**
 * Created by Proïd on 08/06/2016.
 */

public class WashINSAAlarmStopDialog {
    private static AlertDialog dialog = null;

    public static void initialize() {
        dialog = null;
    }

    public static void showDialog(final Context context, LaundryMachine machine) {
        if (dialog == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(R.string.washinsa_alarm_stop_dialog_title);
            builder.setIcon(R.drawable.ic_alarm_black_36dp);

            builder.setNeutralButton(R.string.washinsa_alarm_stop_dialog_neutral, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    WashINSAAlarm.stopRingtone();
                }
            });
            builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    WashINSAAlarm.stopRingtone();
                }
            });

            dialog = builder.create();
        }
        int minutesBefore = WashINSAAlarmCreateDialog.getDefaultMinutePickerValue(context);
        String content = context.getResources().getString(R.string.washinsa_alarm_stop_dialog_content_sentence_start, machine.getNumber())
        +" "+context.getResources().getQuantityString(R.plurals.washinsa_alarm_stop_dialog_content_sentence_end, minutesBefore, minutesBefore);
        dialog.setMessage(content);

        dialog.show();
    }

}
