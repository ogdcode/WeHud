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

    /**
     * Creates the dialog window using the provided parameters.
     *
     * @param manager   a {@link FragmentManager} object
     * @param listener  a {@link OnTextDialogDismissOkListener} interface instance
     * @param title     a unique {@link String} serving as header for the dialog
     * @param message   a {@link String} serving as body text for the dialog
     * @param id        a unique identifier for an instance of this class
     */
    public static void generate(FragmentManager manager, OnTextDialogDismissOkListener listener,
                                String title, String message, Object id)
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
        args.putString(KEY_MESSAGE, message);

        TextDialogFragment textDialog = TextDialogFragment.newInstance();
        textDialog.setArguments(args);
        textDialog.setOnTextDialogDismissOkListener(listener);
        textDialog.show(manager, title);
    }

    @SuppressLint("InflateParams")
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Context context = getActivity();
        final Bundle args = getArguments();
        final Object dialogId = args.get(KEY_ID);
        final String title = args.getString(KEY_TITLE);
        final String message = args.getString(KEY_MESSAGE);

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

    /**
     * This interface is used to get data from this class to another that implements it.
     */
    public interface OnTextDialogDismissOkListener {
        void onTextDialogDismissOk(Object o);
    }

}
