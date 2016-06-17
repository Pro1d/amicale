package fr.insa.clubinfo.amicale.interfaces;

import fr.insa.clubinfo.amicale.models.LaundryMachine;

/**
 * Created by Proïd on 14/06/2016.
 */

public interface OnCreateAlarmListener {
    void onCreateAlarm(LaundryMachine machine, int minutesInAdvance);
}
