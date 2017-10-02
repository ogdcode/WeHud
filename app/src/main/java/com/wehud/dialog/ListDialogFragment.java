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

    private static final String KEY_ID = "key_id";
    private static final String KEY_TITLE = "key_title";
    private static final String KEY_LIST = "key_list";

    private static OnListDialogDismissOkListener mListener;

    private static RecyclerView.Adapter mAdapter;

    private static RecyclerView.LayoutManager mLayoutManager;

    private static DividerItemDecoration mDivider;

    private static ListDialogFragment newInstance() {
        return new ListDialogFragment();
    }

    private void setOnListDialogDismissOkListener(OnListDialogDismissOkListener listener) {
        mListener = listener;
    }

    private void setListAdapter(RecyclerView.Adapter adapter) {
        mAdapter = adapter;
    }

    private void setListLayoutManager(RecyclerView.LayoutManager layoutManager) {
        mLayoutManager = layoutManager;
    }

    private void setListDividerItemDecoration(DividerItemDecoration divider) {
        mDivider = divider;
    }

    /**
     * Creates the dialog window using the provided parameters.
     *
     * @param manager       a {@link FragmentManager} object
     * @param listener      a {@link OnListDialogDismissOkListener} interface instance
     * @param title         a unique {@link String} serving as header for the dialog
     * @param list          an ensemble of {@link Parcelable} objects
     * @param adapter       a {@link android.support.v7.widget.RecyclerView.Adapter} subclass object
     * @param layoutManager a {@link android.support.v7.widget.RecyclerView.LayoutManager} object
     * @param divider       a {@link DividerItemDecoration} object
     * @param id            a unique identifier for an instance of this class
     */
    public static void generate(FragmentManager manager, OnListDialogDismissOkListener listener,
                                String title, ArrayList<? extends Parcelable> list,
                                RecyclerView.Adapter adapter,
                                RecyclerView.LayoutManager layoutManager,
                                DividerItemDecoration divider, Object id)
    {
        Bundle args = new Bundle();
        if (id instanceof Long)
            args.putLong(KEY_ID, (long) id);
        if (id instanceof Integer)
            args.putInt(KEY_ID, (int) id);
        if (id instanceof Short)
            args.putShort(KEY_ID, (short) id);
        if (id instanceof Double)
            args.putDouble(KEY_ID, (double) id);
        if (id instanceof Float)
            args.putFloat(KEY_ID, (float) id);
        if (id instanceof Byte)
            args.putByte(KEY_ID, (byte) id);
        if (id instanceof String)
            args.putString(KEY_ID, id.toString());
        if (id instanceof Character)
            args.putChar(KEY_ID, (char) id);
        if (id instanceof Boolean)
            args.putBoolean(KEY_ID, (boolean) id);

        args.putString(KEY_TITLE, title);
        args.putParcelableArrayList(KEY_LIST, list);

        ListDialogFragment dialog = ListDialogFragment.newInstance();
        dialog.setArguments(args);
        dialog.setOnListDialogDismissOkListener(listener);
        dialog.setListAdapter(adapter);
        dialog.setListLayoutManager(layoutManager);
        dialog.setListDividerItemDecoration(divider);
        dialog.show(manager, title);
    }

    @SuppressLint("InflateParams")
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Context context = getActivity();
        final Bundle args = getArguments();
        final Object dialogId = args.get(KEY_ID);
        final String title = args.getString(KEY_TITLE);
        final List<? extends Parcelable> list = args.getParcelableArrayList(KEY_LIST);

        final View headerView = LayoutInflater.from(context).inflate(R.layout.dialog_header, null);
        final View bodyView = LayoutInflater.from(context).inflate(R.layout.dialog_list, null);

        final TextView titleView = (TextView) headerView.findViewById(R.id.dialog_title);
        if (!TextUtils.isEmpty(title)) titleView.setText(title);

        final RecyclerView listView = (RecyclerView) bodyView.findViewById(android.R.id.list);

        if (mDivider != null) listView.addItemDecoration(mDivider);
        listView.setLayoutManager(mLayoutManager);
        listView.setAdapter(mAdapter);

        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCustomTitle(headerView);
        builder.setView(bodyView);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                if (list != null && !list.isEmpty()) {
                    int selectedParcelable = Long.valueOf(mAdapter.getItemId(-1)).intValue();
                    if (selectedParcelable != -1) {
                        Parcelable p = list.get(selectedParcelable);
                        mListener.onListDialogDismissOk(dialogId, p);
                        dismiss();
                    } else
                        Utils.toast(context, R.string.message_chooseElementInList);
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

    /**
     * This interface is used to get data from this class to another that implements it.
     */
    public interface OnListDialogDismissOkListener {
        void onListDialogDismissOk(Object o, Parcelable p);
    }
}
