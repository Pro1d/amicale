package fr.insa.clubinfo.amicale.dialogs;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;

import java.util.GregorianCalendar;

import fr.insa.clubinfo.amicale.R;
import fr.insa.clubinfo.amicale.helpers.Date;
import fr.insa.clubinfo.amicale.interfaces.OnCreateAlarmListener;
import fr.insa.clubinfo.amicale.models.LaundryMachine;

/**
 * Created by Proïd on 08/06/2016.
 */

public class WashINSAAlarmCreateDialog {
    private static final String prefs_name = "washinsa_alarm";
    private static final String minutesBeforeKey = "minutes_before";
    private static final int defaultMinutesBefore = 3;// put it in res

    private static AlertDialog dialog = null;
    private static NumberPicker minutePicker;
    private static OnCreateAlarmListener paramListener;
    private static LaundryMachine paramMachine;

    public static void initialize() {
        dialog = null;
    }

    public static void showDialog(final Context context, LaundryMachine machine, OnCreateAlarmListener listener) {
        if (dialog == null)
            createDialog(context);

        paramListener = listener;
        paramMachine = machine;

        dialog.show();
    }

    private static void createDialog(final Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.washinsa_alarm_create_dialog_title);
        builder.setIcon(R.drawable.ic_alarm_black_36dp);
        builder.setView(getView(context));

        builder.setNegativeButton(R.string.washinsa_alarm_create_dialog_cancel, null);
        builder.setPositiveButton(R.string.washinsa_alarm_create_dialog_create, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int minutes = minutePicker.getValue();
                saveDefaultMinutePickerValue(context, minutes);

                paramListener.onCreateAlarm(paramMachine, minutes);
            }
        });

        dialog = builder.create();
    }

    private static View getView(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_washinsa_alarm, null);
        minutePicker = (NumberPicker) view.findViewById(R.id.dialog_washinsa_alarm_np_minutes);
        minutePicker.setMinValue(0);
        minutePicker.setMaxValue(context.getResources().getInteger(R.integer.washinsa_alarm_maximum_time_before_machine_end));
        minutePicker.setValue(getDefaultMinutePickerValue(context));
        return view;
    }

    public static int getDefaultMinutePickerValue(Context context) {
        return context.getSharedPreferences(prefs_name, Context.MODE_PRIVATE).getInt(minutesBeforeKey, defaultMinutesBefore);
    }

    private static void saveDefaultMinutePickerValue(Context context, int value) {
        context.getSharedPreferences(prefs_name, Context.MODE_PRIVATE).edit().putInt(minutesBeforeKey, value).apply();
    }
}
