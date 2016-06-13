package fr.insa.clubinfo.amicale.dialogs;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;

import fr.insa.clubinfo.amicale.R;
import fr.insa.clubinfo.amicale.helpers.Alarm;

/**
 * Created by Proïd on 08/06/2016.
 */

public class WashINSAAlarmDialog {
    private static AlertDialog dialog = null;
    private static NumberPicker minutePicker;

    public static void showSettingWashINSAAlarmDialog(final Context context) {
        if (dialog == null)
            createDialog(context);

        dialog.show();
    }

    private static void createDialog(final Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.washinsa_alarm_dialog_title);
        builder.setIcon(R.drawable.ic_alarm_on_black_24dp);
        builder.setView(getView(context));

        builder.setNegativeButton(R.string.washinsa_alarm_dialog_cancel, null);
        builder.setPositiveButton(R.string.washinsa_alarm_dialog_create, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int minutes = minutePicker.getValue();
                Alarm.executeAlarm(context);
            }
        });

        dialog = builder.create();
    }

    private static View getView(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_washinsa_alarm, null);
        minutePicker = (NumberPicker) view.findViewById(R.id.dialog_washinsa_alarm_np_minutes);
        minutePicker.setMinValue(0);
        minutePicker.setMaxValue(context.getResources().getInteger(R.integer.washinsa_alarm_maximum_time_before_machine_end));
        return view;
    }
}
