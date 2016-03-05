package com.ahci.meme_recommender.activity.help;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.support.annotation.NonNull;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.SimpleExpandableListAdapter;

import com.ahci.meme_recommender.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HelpDialogHelper {

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public void showHelpDialog(Context context, DialogInterface.OnDismissListener onDismissListener) {
        AlertDialog dialog = buildDialog(context);
        dialog.show();
        dialog.setOnDismissListener(onDismissListener);
    }

    public void showHelpDialog(Context context) {
        AlertDialog dialog = buildDialog(context);
        dialog.show();
    }

    private AlertDialog buildDialog(Context context) {
        AlertDialog.Builder helpDialogBuilder = new AlertDialog.Builder(context);
        helpDialogBuilder.setTitle(R.string.help_dialog_title);

        View content = ((LayoutInflater) (context.getSystemService(Context.LAYOUT_INFLATER_SERVICE))).inflate(R.layout.help_dialog, null);
        ExpandableListView list = (ExpandableListView) content.findViewById(R.id.help_dialog_list);

        BaseExpandableListAdapter adapter = setupHelpItems(context);

        list.setAdapter(adapter);

        helpDialogBuilder.setView(content);
        return helpDialogBuilder.create();
    }

    @NonNull
    private BaseExpandableListAdapter setupHelpItems(Context context) {
        List<String> listDataHeader = new ArrayList<String>();
        Map<String, List<String>> listDataChild = new HashMap<String, List<String>>();

        listDataHeader.add(context.getString(R.string.help_dialog_recognition_problems_topic));
        listDataChild.put(listDataHeader.get(0), Collections.singletonList(context.getString(R.string.help_dialog_recognition_problems_explain)));

        listDataHeader.add(context.getString(R.string.help_dialog_server_problems_topic));
        listDataChild.put(listDataHeader.get(1), Collections.singletonList(context.getString(R.string.help_dialog_server_problems_explain)));

        listDataHeader.add(context.getString(R.string.help_dialog_film_topic));
        listDataChild.put(listDataHeader.get(2), Collections.singletonList(context.getString(R.string.help_dialog_film_explain)));

        listDataHeader.add(context.getString(R.string.help_dialog_correct_topic));
        listDataChild.put(listDataHeader.get(3), Collections.singletonList(context.getString(R.string.help_dialog_correct_explain)));


        return new ExpandableListAdapter(context, listDataHeader, listDataChild);
    }

}
