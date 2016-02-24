package com.ahci.meme_recommender.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;

import com.ahci.meme_recommender.R;
import com.ahci.meme_recommender.server_connection.ServerCorrespondence;

/**
 * Handles all server connection errors (by displaying various dialogs and offering solutions to
 * resolve the errors or explanations for the errors to the user).
 */
public class NetworkErrorHelper implements ServerCorrespondence.ServerErrorHandler {

    private Activity activity;

    public NetworkErrorHelper(Activity context) {
        this.activity = context;
    }


    /**
     * Displays a dialog that allows the user to open the network connectivity settings or
     * close the app.
     */
    @Override
    public void onNoNetworkAvailable() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);

        dialogBuilder.setTitle(activity.getString(R.string.no_network_enabled_title))
                .setMessage(R.string.no_network_enabled_body)
                .setPositiveButton(R.string.open_network, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        activity.startActivity(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS));
                    }
                })
                .setNegativeButton(R.string.close_app, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        activity.finish();
                    }
                });

        dialogBuilder.create().show();
    }


    /**
     * Displays a dialog that informs the user about the error and allows them to close the app.
     */
    @Override
    public void onNoConnectionToServerPossible() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);

        dialogBuilder.setTitle(activity.getString(R.string.server_offline))
                .setMessage(R.string.server_offline_body)
                .setPositiveButton(R.string.close_app, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        activity.finish();
                    }
                });

        dialogBuilder.create().show();
    }

    /**
     * Displays a dialog that informs the user about the error and allows them to close the app.
     */
    @Override
    public void onNoConnectionAtAllPossible() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);

        dialogBuilder.setTitle(activity.getString(R.string.all_servers_offline))
                .setMessage(R.string.all_servers_offline_body)
                .setPositiveButton(R.string.close_app, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        activity.finish();
                    }
                });

        dialogBuilder.create().show();
    }

}
