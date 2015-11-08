package com.example.ndk_opencv_androidstudio.user_test_001;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.widget.EditText;

import com.example.ndk_opencv_androidstudio.R;

/**
 * Created by jonbr on 08.11.2015.
 */
public class UserIdInputDialog {

    private final Context context;
    private AlertDialog dialog;

    private EditText userIdInput;

    public UserIdInputDialog(Context context) {
        this.context = context;
    }

    public void setup(DialogInterface.OnClickListener listener) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            dialog = new AlertDialog.Builder(context)
                    .setPositiveButton("Ok", listener)
                    .setView(R.layout.user_id_input_dialog)
                    .setCancelable(false)
                    .create();
        }
    }

    public void show() {
        dialog.show();

        userIdInput = (EditText) dialog.findViewById(R.id.user_id_input);
    }

    public int getUserId() {
        String s = userIdInput.getText().toString();
        try {
            return Integer.parseInt(s);
        } catch (Exception e) {
            return -1;
        }
    }

}
