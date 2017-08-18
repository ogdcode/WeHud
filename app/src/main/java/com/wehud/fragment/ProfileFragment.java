package com.wehud.fragment;


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
import com.wehud.dialog.TextDialogFragment;

public class ProfileFragment extends Fragment
        implements View.OnClickListener, TextDialogFragment.OnDismissOkListener {

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

        mProfileUserAvatar = (ImageView) view.findViewById(R.id.profile_userAvatar);
        mProfileUsername = (TextView) view.findViewById(R.id.profile_username);
        mProfileMessages = (TextView) view.findViewById(R.id.profile_messages);
        mProfileContacts = (TextView) view.findViewById(R.id.profile_contacts);
        mProfileSettings = (TextView) view.findViewById(R.id.profile_settings);
        mProfileSignOut = (TextView) view.findViewById(R.id.profile_signOut);

        mProfileUsername.setOnClickListener(this);
        mProfileMessages.setOnClickListener(this);
        mProfileContacts.setOnClickListener(this);
        mProfileSettings.setOnClickListener(this);
        mProfileSignOut.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.profile_contacts:
                intent = new Intent(getContext(), ContactsActivity.class);
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
    public void onDismissOk() {
        getActivity().finish();
    }

    private void attemptSignOut() {
        TextDialogFragment.generate(
                getFragmentManager(),
                this,
                getString(R.string.dialogTitle_signingOut),
                getString(R.string.message_exitApp)
        );
    }
}
