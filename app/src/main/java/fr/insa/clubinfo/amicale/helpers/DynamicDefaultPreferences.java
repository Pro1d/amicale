package fr.insa.clubinfo.amicale.helpers;

import android.content.Context;
import android.preference.PreferenceManager;
import android.provider.Settings;

import fr.insa.clubinfo.amicale.R;

/**
 * Created by Proïd on 27/08/2016.
 */

public class DynamicDefaultPreferences {
    public static String getUserNameFromPreferences(Context context) {
        String key = context.getResources().getString(R.string.prefs_chat_nickname_key);
        String defaultNickname = context.getResources().getString(R.string.prefs_chat_nickname_default_value);
        String nicknameFromPrefs = PreferenceManager.getDefaultSharedPreferences(context).getString(key, defaultNickname);
        if(defaultNickname.equals(nicknameFromPrefs)) {
            String generatedNickname = defaultNickname + System.currentTimeMillis()%10000;
            saveUserNameInPreferences(context, generatedNickname);
            return generatedNickname;
        }
        else {
            return nicknameFromPrefs;
        }
    }

    public static void saveUserNameInPreferences(Context context, String name) {
        String key = context.getResources().getString(R.string.prefs_chat_nickname_key);
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(key, name).commit();
    }
}
