package fr.insa.clubinfo.amicale.sync;

import android.content.Context;
import android.content.res.Resources;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.Future;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.Locale;

import fr.insa.clubinfo.amicale.R;
import fr.insa.clubinfo.amicale.interfaces.OnLaundryRoomUpdatedListener;
import fr.insa.clubinfo.amicale.models.LaundryMachine;
import fr.insa.clubinfo.amicale.models.LaundryRoom;

/**
 * Created by Proïd on 05/06/2016.
 */

public class LaundryRoomLoader {
    private final Context context;
    private final OnLaundryRoomUpdatedListener listener;
    private Future currentTask;

    public LaundryRoomLoader(Context context, OnLaundryRoomUpdatedListener listener) {
        this.listener = listener;
        this.context = context;
    }

    public void loadAsync(boolean loadDefault) {
        if(loadDefault) {
            LaundryRoom laundryRoom = createDefaultLaundry();
            listener.onLaundryRoomLoaded(laundryRoom);
        }
        else {
            currentTask = Ion.with(context).load("http://92.222.86.168/washinsa/json")
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {
                            if (e != null || result == null) {
                                e.printStackTrace();
                                LaundryRoom laundryRoom = createDefaultLaundry();
                                listener.onLaundryRoomSyncFailed(laundryRoom);
                            } else {
                                LaundryRoom laundryRoom = createLaundryRoomFromJson(result);
                                listener.onLaundryRoomLoaded(laundryRoom);
                            }
                        }
                    });
        }
    }

    private LaundryRoom createLaundryRoomFromJson(JsonObject json) {
        LaundryRoom l = new LaundryRoom();

        for(JsonElement item : json.getAsJsonArray("json")) {
            // Create and add each machine
            LaundryMachine machine = createLaundryMachineFromJson(item.getAsJsonObject());
            l.addMachine(machine);
        }

        return l;
    }

    private LaundryMachine createLaundryMachineFromJson(JsonObject json) {
        LaundryMachine m = new LaundryMachine();

        // Machine number
        m.setNumber(json.get("machine").getAsInt());

        // Machine description
        m.setDescription(json.get("type").getAsString().replaceAll("^\\s+", "").replaceAll("\\s+$", ""));

        // Machine type
        if(m.getDescription().toLowerCase(Locale.FRENCH).contains("lave")) {
            m.setType(LaundryMachine.Type.WASHING);
        } else {
            m.setType(LaundryMachine.Type.DRYER);
        }

        // Remaining time
        m.setMinutesRemaining(json.get("remainingTime").getAsInt());

        // State
        String stateDescription = json.get("available").getAsString();
        if(stateDescription.contains("Disponible"))
            m.setState(LaundryMachine.State.FREE);
        else if(stateDescription.contains("Terminé"))
            m.setState(LaundryMachine.State.FINISHED);
        else if(stateDescription.contains("En cours d'utilisation"))
            m.setState(LaundryMachine.State.BUSY);
        else if(stateDescription.contains("Hors service"))
            m.setState(LaundryMachine.State.DISUSED);
        else
            m.setState(LaundryMachine.State.UNKNOWN);

        return m;
    }

    public LaundryRoom createDefaultLaundry() {
        // Getting default values
        Resources res = context.getResources();
        int dryersCount = res.getInteger(R.integer.default_dryers_count);
        int washingMachinesCount = res.getInteger(R.integer.default_washing_machine_count);
        String dryerDescription = res.getString(R.string.default_dryers_description);
        String washingDescription = res.getString(R.string.default_washing_machine_description);

        // Create launderette
        // Create launderette
        LaundryRoom l = new LaundryRoom();

        for(int i = 1; i <= dryersCount; i++) {
            LaundryMachine m = new LaundryMachine();
            m.setState(LaundryMachine.State.UNKNOWN);
            m.setType(LaundryMachine.Type.DRYER);
            m.setNumber(i);
            m.setDescription(dryerDescription);
            l.addMachine(m);
        }
        for(int i = dryersCount+1; i <= dryersCount+washingMachinesCount; i++) {
            LaundryMachine m = new LaundryMachine();
            m.setState(LaundryMachine.State.UNKNOWN);
            m.setType(LaundryMachine.Type.WASHING);
            m.setNumber(i);
            m.setDescription(washingDescription);
            l.addMachine(m);
        }
        return l;
    }

    public void cancel() {
        if(currentTask != null)
            currentTask.cancel(true);
    }
}
