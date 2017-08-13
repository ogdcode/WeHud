package com.wehud.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.wehud.R;
import com.wehud.util.Utils;

import java.util.ArrayList;
import java.util.List;

public final class ListDialogFragment extends DialogFragment {

    private static final String KEY_TITLE = "key_title";
    private static final String KEY_LIST = "key_list";

    private static OnDismissOkListener mListener;

    private static RecyclerView.Adapter mAdapter;

    private static ListDialogFragment newInstance() {
        return new ListDialogFragment();
    }

    private void setOnDismissOkListener(OnDismissOkListener listener) {
        mListener = listener;
    }

    private void setListAdapter(RecyclerView.Adapter adapter) {
        mAdapter = adapter;
    }

    public static void generate(FragmentManager manager, OnDismissOkListener listener, String title,
                                List<Parcelable> list, RecyclerView.Adapter adapter) {
        Bundle bundle = new Bundle();
        bundle.putString(KEY_TITLE, title);
        bundle.putParcelableArrayList(KEY_LIST, (ArrayList<Parcelable>) list);

        ListDialogFragment dialog = ListDialogFragment.newInstance();
        dialog.setArguments(bundle);
        dialog.setOnDismissOkListener(listener);
        dialog.setListAdapter(adapter);
        dialog.show(manager, title);
    }

    @SuppressLint("InflateParams")
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Context context = getActivity();
        final Bundle bundle = getArguments();
        final String title = bundle.getString(KEY_TITLE);
        final List<Parcelable> list = bundle.getParcelableArrayList(KEY_LIST);

        final View headerView = LayoutInflater.from(context).inflate(R.layout.dialog_header, null);
        final View bodyView = LayoutInflater.from(context).inflate(R.layout.dialog_list, null);

        final TextView titleView = (TextView) headerView.findViewById(R.id.dialog_title);
        if (!TextUtils.isEmpty(title)) titleView.setText(title);

        final RecyclerView listView = (RecyclerView) bodyView.findViewById(android.R.id.list);

        listView.setLayoutManager(new LinearLayoutManager(context));
        listView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.HORIZONTAL));
        listView.setAdapter(mAdapter);

        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCustomTitle(headerView);
        builder.setView(bodyView);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                if (list != null && !list.isEmpty()) {
                    Parcelable p = list.get((int) mAdapter.getItemId(-1));
                    if (p != null) {
                        mListener.onDismissOk(p);
                        dismiss();
                    } else
                        Utils.toast(context, "Please choose an element in the list.");
                }
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dismiss();
            }
        });

        return builder.create();
    }

    public interface OnDismissOkListener {
        void onDismissOk(Parcelable p);
    }
}