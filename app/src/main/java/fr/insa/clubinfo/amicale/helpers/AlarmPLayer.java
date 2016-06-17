package fr.insa.clubinfo.amicale.helpers;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.widget.Toast;

import java.io.IOException;

import fr.insa.clubinfo.amicale.R;

/**
 * Created by Proïd on 16/06/2016.
 */

public class AlarmPlayer {
    private static MediaPlayer mp;

    public static void playSound(Context context){
        stopSound();

        Uri ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);

        try {
            mp = new MediaPlayer();
            mp.setDataSource(context, ringtoneUri);
            mp.setAudioStreamType(AudioManager.STREAM_ALARM);
            mp.setLooping(true);
            mp.prepare();
            mp.start();
        } catch (IOException e) {
            Toast.makeText(context, R.string.alarm_player_toast_media_player_error, Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    public static void stopSound() {
        if(mp != null) {
            mp.stop();
            mp.reset();
            mp.release();
            mp = null;
        }
    }
}
