package com.wehud.fragment;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
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
import com.wehud.model.Payload;
import com.wehud.model.User;
import com.wehud.network.APICall;
import com.wehud.util.Constants;
import com.wehud.util.GsonUtils;
import com.wehud.util.PreferencesUtils;
import com.wehud.util.Utils;

import java.util.HashMap;
import java.util.Map;

public class ProfileFragment extends Fragment
        implements View.OnClickListener, TextDialogFragment.OnTextDialogDismissOkListener {

    private Context mContext;
    private ImageView mProfileUserAvatar;
    private TextView mProfileUsername;

    private boolean mPaused;
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Constants.INTENT_USER_GET) && !mPaused) {
                final String response = intent.getStringExtra(Constants.EXTRA_BROADCAST);
                final Payload payload = GsonUtils.getInstance().fromJson(response, Payload.class);

                final String code = payload.getCode();

                if (Integer.valueOf(code) == Constants.HTTP_OK) {
                    final String content = payload.getContent();
                    final User currentUser = GsonUtils.getInstance().fromJson(content, User.class);

                    final String avatar = currentUser.getAvatar();
                    final String username = currentUser.getUsername();

                    if (!TextUtils.isEmpty(avatar))
                        Utils.loadImage(mContext, avatar, mProfileUserAvatar);
                    else mProfileUserAvatar.setImageResource(R.mipmap.ic_launcher_round);
                    mProfileUsername.setText(username);
                } else {
                    int messageId;
                    switch (Integer.valueOf(code)) {
                        case Constants.HTTP_METHOD_NOT_ALLOWED:
                            messageId = R.string.error_sessionExpired;
                            getActivity().finish();
                            break;
                        case Constants.HTTP_INTERNAL_SERVER_ERROR:
                            messageId = R.string.error_server;
                            break;
                        default:
                            Utils.toast(mContext, R.string.error_general, code);
                            return;
                    }

                    Utils.toast(mContext, messageId);
                }
            }
        }
    };

    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_profile, container, false);
        mContext = view.getContext();

        mProfileUserAvatar = (ImageView) view.findViewById(R.id.profile_icUser);
        mProfileUsername = (TextView) view.findViewById(R.id.profile_username);

        TextView profileMessages = (TextView) view.findViewById(R.id.profile_messages);
        TextView profileContacts = (TextView) view.findViewById(R.id.profile_contacts);
        TextView profileSettings = (TextView) view.findViewById(R.id.profile_settings);
        TextView profileSignOut = (TextView) view.findViewById(R.id.profile_signOut);

        profileMessages.setOnClickListener(this);
        profileContacts.setOnClickListener(this);
        profileSettings.setOnClickListener(this);
        profileSignOut.setOnClickListener(this);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.INTENT_USER_GET);

        mContext.registerReceiver(mReceiver, filter);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!mPaused) this.getCurrentUserInfo();

        mPaused = false;
    }

    @Override
    public void onPause() {
        super.onPause();
        mPaused = true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mContext.unregisterReceiver(mReceiver);
    }

    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.profile_messages:
                intent = new Intent(mContext, MessagesActivity.class);
                break;
            case R.id.profile_contacts:
                intent = new Intent(mContext, ContactsActivity.class);
                break;
            case R.id.profile_settings:
                intent = new Intent(mContext, SettingsActivity.class);
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

    private void getCurrentUserInfo() {
        Map<String, String> headers = new HashMap<>();
        headers.put(Constants.HEADER_CONTENT_TYPE, Constants.APPLICATION_JSON);
        headers.put(Constants.HEADER_ACCEPT, Constants.APPLICATION_JSON);

        final String currentUserId = PreferencesUtils.get(mContext, Constants.PREF_USER_ID);

        final APICall call = new APICall(
                mContext,
                Constants.INTENT_USER_GET,
                Constants.GET,
                Constants.API_USERS + '/' + currentUserId,
                headers
        );
        if (!call.isLoading()) call.execute();
    }
}
