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

    private static final String KEY_VIEW_ID = "dialog_id";
    private static final String KEY_HINT = "dialog_hint";
    private static final String KEY_TITLE = "dialog_title";
    private static final String KEY_TEXT = "dialog_text";
    private static final String KEY_PASSWORD = "dialog_password";

    private static final int PASSWORD_INVISIBLE = InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD;
    private static final int PASSWORD_VISIBLE = InputType.TYPE_CLASS_TEXT;

    private static OnEditDialogDismissOkListener mListener;

    private static EditDialogFragment newInstance() {
        return new EditDialogFragment();
    }

    private void setOnEditDialogDismissOkListener(OnEditDialogDismissOkListener listener) {
        mListener = listener;
    }

    public static void generate(FragmentManager manager, OnEditDialogDismissOkListener listener,
                                int id, String title, String text, String hint, boolean password) {
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_VIEW_ID, id);
        bundle.putString(KEY_TITLE, title);
        bundle.putString(KEY_TEXT, text);
        bundle.putString(KEY_HINT, hint);
        bundle.putBoolean(KEY_PASSWORD, password);

        EditDialogFragment editDialog = EditDialogFragment.newInstance();
        editDialog.setArguments(bundle);
        editDialog.setOnEditDialogDismissOkListener(listener);
        editDialog.show(manager, title);
    }

    @SuppressLint("InflateParams")
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Context context = getActivity();
        final Bundle bundle = getArguments();
        final int viewId = bundle.getInt(KEY_VIEW_ID);
        final String[] text = {bundle.getString(KEY_TEXT)};
        final String title = bundle.getString(KEY_TITLE);
        final String hint = bundle.getString(KEY_HINT);
        final boolean isPassword = bundle.getBoolean(KEY_PASSWORD);

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
                            return;
                        }
                        text[0] = newPassword.getText().toString();
                    }
                    mListener.onEditDialogDismissOk(viewId, text[0]);
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
        void onEditDialogDismissOk(int i, String s);
    }
}
