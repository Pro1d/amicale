package fr.insa.clubinfo.amicale.sync;

import android.os.AsyncTask;

import fr.insa.clubinfo.amicale.interfaces.OnLaunderetteUpdatedListener;
import fr.insa.clubinfo.amicale.models.Launderette;
import fr.insa.clubinfo.amicale.models.LaundryMachine;

/**
 * Created by Proïd on 05/06/2016.
 */

public class LaunderetteLoader {
    private final OnLaunderetteUpdatedListener listener;
    private AsyncTask<Void, Void, Launderette> currentTask;

    public LaunderetteLoader(OnLaunderetteUpdatedListener listener) {
        this.listener = listener;
    }

    public void loadAsync() {
        currentTask = new AsyncTask<Void, Void, Launderette>() {
            @Override
            protected Launderette doInBackground(Void... params) {
                Launderette l = new Launderette();
                for(int i = 0; i < 3; i++) {
                    LaundryMachine m = new LaundryMachine();
                    m.setDescription("SECHE LINGE 14 KG");
                    m.setMinutesRemaining(i-1);
                    m.setNumber(i);
                    m.setType(LaundryMachine.Type.DRYER);
                    l.addDryingMachine(m);
                }
                for(int i = 0; i < 5; i++) {
                    LaundryMachine m = new LaundryMachine();
                    m.setDescription("LAVE LINGE 6 KG");
                    m.setMinutesRemaining(43*i/4 - 1);
                    m.setNumber(i+3);
                    m.setType(LaundryMachine.Type.WASHING);
                    l.addWashingMachine(m);
                }
                return l;
            }

            @Override
            protected void onPostExecute(Launderette launderette) {
                listener.onLaunderetteLoaded(launderette);
            }

            @Override
            protected void onCancelled(Launderette launderette) {
                listener.onLaunderetteSyncFailed();
            }
        }.execute();
    }

    public void cancel() {
        currentTask.cancel(true);
    }
}
