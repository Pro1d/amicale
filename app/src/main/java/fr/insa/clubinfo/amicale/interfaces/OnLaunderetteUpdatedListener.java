package fr.insa.clubinfo.amicale.interfaces;

import fr.insa.clubinfo.amicale.models.Launderette;

/**
 * Created by Proïd on 05/06/2016.
 */

public interface OnLaunderetteUpdatedListener {
    void onLaunderetteLoaded(Launderette launderette);
    void onLaunderetteSyncFailed();
}
