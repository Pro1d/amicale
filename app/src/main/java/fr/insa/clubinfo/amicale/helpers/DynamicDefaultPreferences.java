package fr.insa.clubinfo.amicale.helpers;

import android.content.Context;
import android.preference.PreferenceManager;

import fr.insa.clubinfo.amicale.R;

/**
 * Created by Proïd on 27/08/2016.
 */

public class DynamicDefaultPreferences {
    public static String getUserNameFromPreferences(Context context) {
        String key = context.getResources().getString(R.string.prefs_chat_nickname_key);
        String defaultNickname = context.getResources().getString(R.string.prefs_chat_nickname_default_value);
        String nicknameFromPrefs = PreferenceManager.getDefaultSharedPreferences(context).getString(key, defaultNickname);
        nicknameFromPrefs = nicknameFromPrefs.replaceAll("^\\s+|\\s+$", "");
        if(defaultNickname.equals(nicknameFromPrefs) || nicknameFromPrefs.isEmpty()) {
            // generate a pseudo with a random odd number inferior to 10000
            String generatedNickname = defaultNickname + ((System.currentTimeMillis()%10000)|1);
            saveUserNameInPreferences(context, generatedNickname);
            return generatedNickname;
        }
        else {
            return nicknameFromPrefs;
        }
    }

    public static void saveUserNameInPreferences(Context context, String name) {
        String key = context.getResources().getString(R.string.prefs_chat_nickname_key);
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(key, name).apply();
    }
}
