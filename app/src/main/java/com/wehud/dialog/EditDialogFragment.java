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
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.wehud.R;

public final class EditDialogFragment extends DialogFragment {

    private static final String KEY_ID = "key_view_id";
    private static final String KEY_HINT = "key_hint";
    private static final String KEY_TITLE = "key_title";
    private static final String KEY_TEXT = "key_text";
    private static final String KEY_PASSWORD = "key_password";

    private static final int PASSWORD_VISIBLE = InputType.TYPE_CLASS_TEXT;
    private static final int PASSWORD_INVISIBLE = InputType.TYPE_CLASS_TEXT |
            InputType.TYPE_TEXT_VARIATION_PASSWORD;

    private static OnEditDialogDismissOkListener mListener;

    private static EditDialogFragment newInstance() {
        return new EditDialogFragment();
    }

    private void setOnEditDialogDismissOkListener(OnEditDialogDismissOkListener listener) {
        mListener = listener;
    }

    public static void generate(FragmentManager manager, OnEditDialogDismissOkListener listener,
                                String title, String text, String hint, boolean password,
                                Object id)
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
        args.putString(KEY_TEXT, text);
        args.putString(KEY_HINT, hint);
        args.putBoolean(KEY_PASSWORD, password);

        EditDialogFragment editDialog = EditDialogFragment.newInstance();
        editDialog.setArguments(args);
        editDialog.setOnEditDialogDismissOkListener(listener);
        editDialog.show(manager, title);
    }

    @SuppressLint("InflateParams")
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Context context = getActivity();
        final Bundle args = getArguments();
        final Object dialogId = args.get(KEY_ID);
        final String[] text = {args.getString(KEY_TEXT)};
        final String title = args.getString(KEY_TITLE);
        final String hint = args.getString(KEY_HINT);
        final boolean isPassword = args.getBoolean(KEY_PASSWORD);

        final View headerView = LayoutInflater.from(context).inflate(R.layout.dialog_header, null);
        final View bodyView = LayoutInflater.from(context).inflate(R.layout.dialog_edit, null);

        final TextView titleView = (TextView) headerView.findViewById(R.id.dialog_title);
        if (!TextUtils.isEmpty(title)) titleView.setText(title);


        final EditText bodyField = (EditText) bodyView.findViewById(R.id.dialog_editField);
        if (!TextUtils.isEmpty(text[0]) && !isPassword) bodyField.setText(text[0]);
        if (!TextUtils.isEmpty(hint)) bodyField.setHint(hint);
        bodyField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String newText = bodyField.getText().toString();
                if (!TextUtils.isEmpty(newText)) text[0] = newText;
            }
        });

        final EditText newPassword = (EditText) bodyView.findViewById(R.id.dialog_newPassword);
        final EditText confirmNewPassword = (EditText) bodyView.findViewById(R.id.dialog_confirmNewPassword);
        final CheckBox showPasswordBox = (CheckBox) bodyView.findViewById(R.id.dialog_boxShowPassword);
        if (isPassword) {
            bodyField.setInputType(PASSWORD_INVISIBLE);
            showPasswordBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                    if (checked) {
                        bodyField.setInputType(PASSWORD_VISIBLE);
                    } else {
                        bodyField.setInputType(PASSWORD_INVISIBLE);
                    }
                }
            });
        } else {
            newPassword.setVisibility(View.GONE);
            confirmNewPassword.setVisibility(View.GONE);
            showPasswordBox.setVisibility(View.GONE);
        }

        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCustomTitle(headerView);
        builder.setView(bodyView);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                if (mListener != null && !TextUtils.isEmpty(bodyField.getText().toString())) {
                    if (isPassword) {
                        if (!newPassword.getText().toString().equals(confirmNewPassword.getText().toString())) {
                            newPassword.setError(getString(R.string.error_passwords_no_match));
                            confirmNewPassword.setError(getString(R.string.error_passwords_no_match));
                        } else text[0] = newPassword.getText().toString();
                    }
                    mListener.onEditDialogDismissOk(dialogId, text[0]);
                }
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

    public interface OnEditDialogDismissOkListener {
        void onEditDialogDismissOk(Object o, String s);
    }
}
