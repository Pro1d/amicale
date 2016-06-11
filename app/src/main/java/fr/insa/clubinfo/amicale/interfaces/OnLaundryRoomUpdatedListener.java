package fr.insa.clubinfo.amicale.interfaces;

import fr.insa.clubinfo.amicale.models.LaundryRoom;

/**
 * Created by Proïd on 05/06/2016.
 */

public interface OnLaundryRoomUpdatedListener {
    void onLaundryRoomLoaded(LaundryRoom laundryRoom);
    void onLaundryRoomSyncFailed(LaundryRoom defaultLaundryRoom);
    void onLaundryRoomSyncCanceled();
}
