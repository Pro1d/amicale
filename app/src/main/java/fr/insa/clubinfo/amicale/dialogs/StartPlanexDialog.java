package fr.insa.clubinfo.amicale.dialogs;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;

import fr.insa.clubinfo.amicale.R;

/**
 * Created by Proïd on 08/06/2016.
 */

public class StartPlanexDialog {
    private static AlertDialog dialog = null;

    public static void startPlanex(Context context) {
        Intent planexIntent = getPlanexIntent(context);
        if(planexIntent == null) {
            showGettingPlanexDialog(context);
        } else {
            startPlanex(planexIntent, context);
        }
    }

    public static void showGettingPlanexDialog(final Context context) {
        if (dialog == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(R.string.planex_dialog_title);
            builder.setIcon(R.mipmap.planex);
            builder.setMessage(R.string.planex_dialog_message);
            builder.setNegativeButton(R.string.planex_cancel_button, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            builder.setPositiveButton(R.string.planex_install_button, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startMarket(context);
                }
            });

            dialog = builder.create();
        }

        dialog.show();
    }

    private static Intent getPlanexIntent(Context context) {
        String planexPackageName = context.getResources().getString(R.string.planex_package_name);
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(planexPackageName);
        return intent;
    }

    private static void startPlanex(Intent planexIntent, Context context) {
        planexIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(planexIntent);
    }

    private static void startMarket(Context context) {
        String planexPackageName = context.getResources().getString(R.string.planex_package_name);
        // Bring user to the market
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setData(Uri.parse("market://details?id=" + planexPackageName));
        context.startActivity(intent);
    }
}
