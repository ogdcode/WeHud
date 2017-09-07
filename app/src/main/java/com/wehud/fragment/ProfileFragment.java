package com.wehud.fragment;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.wehud.R;
import com.wehud.activity.ContactsActivity;
import com.wehud.activity.MessagesActivity;
import com.wehud.activity.SettingsActivity;
import com.wehud.dialog.TextDialogFragment;

public class ProfileFragment extends Fragment
        implements View.OnClickListener, TextDialogFragment.OnTextDialogDismissOkListener {

    private ImageView mProfileUserAvatar;
    private TextView mProfileUsername;
    private TextView mProfileMessages;
    private TextView mProfileContacts;
    private TextView mProfileSettings;
    private TextView mProfileSignOut;

    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        mProfileMessages = (TextView) view.findViewById(R.id.profile_messages);
        mProfileContacts = (TextView) view.findViewById(R.id.profile_contacts);
        mProfileSettings = (TextView) view.findViewById(R.id.profile_settings);
        mProfileSignOut = (TextView) view.findViewById(R.id.profile_signOut);

        mProfileMessages.setOnClickListener(this);
        mProfileContacts.setOnClickListener(this);
        mProfileSettings.setOnClickListener(this);
        mProfileSignOut.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View view) {
        Intent intent;
        Context context = getContext();
        switch (view.getId()) {
            case R.id.profile_messages:
                intent = new Intent(context, MessagesActivity.class);
                break;
            case R.id.profile_contacts:
                intent = new Intent(context, ContactsActivity.class);
                break;
            case R.id.profile_settings:
                intent = new Intent(context, SettingsActivity.class);
                break;
            case R.id.profile_signOut:
                this.attemptSignOut();
                return;
            default:
                return;
        }

        startActivity(intent);
    }

    @Override
    public void onTextDialogDismissOk(Object id) {
        getActivity().finish();
    }

    private void attemptSignOut() {
        TextDialogFragment.generate(
                getFragmentManager(),
                this,
                getString(R.string.dialogTitle_signingOut),
                getString(R.string.message_exitApp),
                0
        );
    }
}
