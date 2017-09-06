package com.wehud.dialog;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import com.wehud.R;

import java.util.Calendar;

public final class DatePickerDialogFragment extends DialogFragment {

    private static final String KEY_ID = "key_id";
    private static final String KEY_TITLE = "key_title";

    private static OnDatePickListener mListener;

    private static DatePickerDialogFragment newInstance() {
        return new DatePickerDialogFragment();
    }

    public void setOnDatePickListener(OnDatePickListener listener) {
        mListener = listener;
    }

    public static void generate(FragmentManager manager, OnDatePickListener listener,
                                String title, int id
    ) {
        Bundle args = new Bundle();
        args.putInt(KEY_ID, id);
        args.putString(KEY_TITLE, title);

        DatePickerDialogFragment datePickerDialog = DatePickerDialogFragment.newInstance();
        datePickerDialog.setArguments(args);
        datePickerDialog.setOnDatePickListener(listener);
        datePickerDialog.show(manager, title);
    }

    @SuppressLint("InflateParams")
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar cal = Calendar.getInstance();
        final int year = cal.get(Calendar.YEAR);
        final int month = cal.get(Calendar.MONTH);
        final int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);

        final Context context = getActivity();
        final Bundle args = getArguments();
        final int viewId = args.getInt(KEY_ID);
        final String title = args.getString(KEY_TITLE);

        final View headerView = LayoutInflater.from(context).inflate(R.layout.dialog_header, null);
        final View bodyView = LayoutInflater.from(context).inflate(R.layout.dialog_date_picker, null);

        final TextView titleView = (TextView) headerView.findViewById(R.id.dialog_title);
        if (!TextUtils.isEmpty(title)) titleView.setText(title);

        final DatePicker picker = (DatePicker) bodyView.findViewById(R.id.picker);
        picker.init(year, month, dayOfMonth, null);

        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCustomTitle(headerView);
        builder.setView(bodyView);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                if (mListener != null) {
                    final int year = picker.getYear();
                    final int month = picker.getMonth();
                    final int dayOfMonth = picker.getDayOfMonth();

                    mListener.onDatePick(viewId, year, month, dayOfMonth);
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

    public interface OnDatePickListener {
        void onDatePick(final int id, int i, int i1, int i2);
    }
}
