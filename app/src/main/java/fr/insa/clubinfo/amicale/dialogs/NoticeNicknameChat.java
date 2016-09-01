package fr.insa.clubinfo.amicale.dialogs;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import fr.insa.clubinfo.amicale.MainActivity;
import fr.insa.clubinfo.amicale.R;
import fr.insa.clubinfo.amicale.helpers.DynamicDefaultPreferences;

/**
 * Created by Proïd on 01/09/2016.
 */

public class NoticeNicknameChat {
    private static AlertDialog dialog = null;
    private static MainActivity main;

    public static void initialize(MainActivity main) {
        dialog = null;
        NoticeNicknameChat.main = main;
    }

    public static void show() {
        if (dialog == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(main);
            builder.setTitle(R.string.nickname_dialog_title);
            builder.setIcon(R.drawable.ic_account_box_black_24dp);
            String message = main.getResources().getString(R.string.nickname_dialog_message, DynamicDefaultPreferences.getUserNameFromPreferences(main));
            builder.setMessage(message);
            builder.setNegativeButton(R.string.nickname_cancel_button, null);
            builder.setPositiveButton(R.string.nickname_go_button, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    main.switchToSettingsFragment();
                }
            });

            dialog = builder.create();
        }

        dialog.show();
    }
}
