package de.ahci.memewebview;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

/**
 * Created by jonbr on 07.11.2015.
 */
public class WebChromeClientWithAlert extends WebChromeClient {

    private Context context;

    public WebChromeClientWithAlert(Context context) {
        super();
        this.context = context;
    }

    @Override
    public boolean onJsConfirm(final WebView view, final String url, String message, final JsResult result) {
        JSONObject obj = null;
        try {
            obj = new JSONObject(message);
        } catch (JSONException e) {
            e.printStackTrace();
            return true;
        }

        try {
            final JSONObject finalObj = obj;
            buildDialog(finalObj, view.getContext(), result)
                    .show();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return true;
    }

    private AlertDialog buildDialog(final JSONObject jsonObject, final Context context, final JsResult result) throws JSONException {
        return new AlertDialog.Builder(context)
                .setTitle("Im Browser öffnen?")
                .setMessage(jsonObject.getString("text"))
                .setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    context.startActivity(
                                            new Intent(Intent.ACTION_VIEW, Uri.parse(jsonObject.getString("url"))));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                result.confirm();
                            }
                        })
                .setNegativeButton(android.R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                result.cancel();
                            }
                        })
                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        result.cancel();
                    }
                })
                .create();
    }


}
