package com.wehud.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.wehud.R;

public final class TextDialogFragment extends DialogFragment {

    private static final String KEY_ID = "key_id";
    private static final String KEY_TITLE = "key_title";
    private static final String KEY_MESSAGE = "key_message";

    private static OnTextDialogDismissOkListener mListener;

    private static TextDialogFragment newInstance() {
        return new TextDialogFragment();
    }

    private void setOnTextDialogDismissOkListener(OnTextDialogDismissOkListener listener) {
        mListener = listener;
    }

    public static void generate(FragmentManager manager, OnTextDialogDismissOkListener listener,
                                String title, String message, int id) {
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_ID, id);
        bundle.putString(KEY_TITLE, title);
        bundle.putString(KEY_MESSAGE, message);

        TextDialogFragment textDialog = TextDialogFragment.newInstance();
        textDialog.setArguments(bundle);
        textDialog.setOnTextDialogDismissOkListener(listener);
        textDialog.show(manager, title);
    }

    @SuppressLint("InflateParams")
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Context context = getActivity();
        final Bundle bundle = getArguments();
        final int dialogId = bundle.getInt(KEY_ID);
        final String title = bundle.getString(KEY_TITLE);
        final String message = bundle.getString(KEY_MESSAGE);

        final View headerView = LayoutInflater.from(context).inflate(R.layout.dialog_header, null);
        final View bodyView = LayoutInflater.from(context).inflate(R.layout.dialog_text, null);

        final TextView titleView = (TextView) headerView.findViewById(R.id.dialog_title);
        if (!TextUtils.isEmpty(title)) titleView.setText(title);

        final TextView messageView = (TextView) bodyView.findViewById(R.id.dialog_message);
        if (!TextUtils.isEmpty(message)) messageView.setText(message);

        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCustomTitle(headerView);
        builder.setView(bodyView);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                if (mListener != null) mListener.onTextDialogDismissOk(dialogId);
                dismiss();
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

    public interface OnTextDialogDismissOkListener {
        void onTextDialogDismissOk(int i);
    }

}
