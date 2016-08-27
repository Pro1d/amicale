package fr.insa.clubinfo.amicale.dialogs;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import fr.insa.clubinfo.amicale.R;
import fr.insa.clubinfo.amicale.helpers.WashINSAAlarm;
import fr.insa.clubinfo.amicale.models.LaundryMachine;

/**
 * Created by Proïd on 08/06/2016.
 */

public class WashINSAAlarmCancelDialog {
    private static AlertDialog dialog = null;

    public static void initialize() {
        dialog = null;
    }

    public static void showDialog(final Context context) {
        if (dialog == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(R.string.washinsa_alarm_cancel_dialog_title);
            builder.setIcon(R.drawable.ic_alarm_black_36dp);

            builder.setNegativeButton(R.string.washinsa_alarm_cancel_dialog_negative, null);
            builder.setPositiveButton(R.string.washinsa_alarm_cancel_dialog_positive, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    WashINSAAlarm.cancelDelayedAlarm(context);
                }
            });
            builder.setMessage(R.string.washinsa_alarm_cancel_dialog_content);

            dialog = builder.create();
        }

        dialog.show();
    }

}
