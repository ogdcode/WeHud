package com.wehud.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Parcelable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.wehud.R;
import com.wehud.adapter.ImagesAdapter;
import com.wehud.dialog.EditDialogFragment;
import com.wehud.dialog.ListDialogFragment;
import com.wehud.dialog.TextDialogFragment;
import com.wehud.model.Image;
import com.wehud.model.Payload;
import com.wehud.model.User;
import com.wehud.network.APICall;
import com.wehud.util.Constants;
import com.wehud.util.GsonUtils;
import com.wehud.util.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SettingsActivity extends AppCompatActivity
        implements View.OnClickListener, EditDialogFragment.OnEditDialogDismissOkListener,
        ListDialogFragment.OnListDialogDismissOkListener,
        TextDialogFragment.OnTextDialogDismissOkListener {

    private static final String PARAM_AVATAR = "avatar";
    private static final String PARAM_USERNAME = "username";
    private static final String PARAM_EMAIL = "email";
    private static final String PARAM_PASSWORD = "password";

    private Image mImage;

    private ImageView mAvatar;
    private TextView mUsername;
    private TextView mEmail;
    private TextView mPassword;

    private User mCurrentUser;

    private boolean mPaused;
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (!mPaused) {
                final String response = intent.getStringExtra(Constants.EXTRA_BROADCAST);
                final Payload payload = GsonUtils.getInstance().fromJson(response, Payload.class);

                final String code = payload.getCode();

                if (Integer.valueOf(code) == Constants.HTTP_OK ||
                        Integer.valueOf(code) == Constants.HTTP_NO_CONTENT) {
                    final String content = payload.getContent();

                    switch (intent.getAction()) {
                        case Constants.INTENT_USER_GET:
                            mCurrentUser = GsonUtils.getInstance().fromJson(content, User.class);

                            final String avatar = mCurrentUser.getAvatar();
                            final String currentPassword = mCurrentUser.getPassword();

                            mImage = new Image(avatar, 0);

                            Utils.loadImage(SettingsActivity.this, avatar, mAvatar, 256);
                            mUsername.setText(mCurrentUser.getUsername());
                            mEmail.setText(mCurrentUser.getEmail());

                            mPassword.setTag(currentPassword);
                            mPassword.setText(getString(R.string.sample_password));
                            break;
                        case Constants.INTENT_USER_UPDATE:
                            Utils.toast(
                                    SettingsActivity.this,
                                    R.string.message_updateSuccess
                            );
                            SettingsActivity.this.finish();
                            break;
                        case Constants.INTENT_USER_DELETE:
                            Utils.toast(
                                    SettingsActivity.this,
                                    R.string.message_deleteSuccess
                            );
                            break;
                        default:
                            break;
                    }
                } else if (Integer.valueOf(code) == Constants.HTTP_INTERNAL_SERVER_ERROR)
                    Utils.toast(SettingsActivity.this, R.string.error_server);
                else Utils.toast(SettingsActivity.this, R.string.error_general, code);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }

        mAvatar = (ImageView) findViewById(R.id.settings_avatar);
        mUsername = (TextView) findViewById(R.id.settings_username);
        mEmail = (TextView) findViewById(R.id.settings_email);
        mPassword = (TextView) findViewById(R.id.settings_password);

        Button changeAvatarButton = (Button) findViewById(R.id.settings_btnChangeAvatar);
        Button updateButton = (Button) findViewById(R.id.settings_btnUpdate);
        Button deleteButton = (Button) findViewById(R.id.settings_btnDelete);

        mUsername.setOnClickListener(this);
        mEmail.setOnClickListener(this);
        mPassword.setOnClickListener(this);
        changeAvatarButton.setOnClickListener(this);
        updateButton.setOnClickListener(this);
        deleteButton.setOnClickListener(this);

        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.INTENT_USER_GET);
        filter.addAction(Constants.INTENT_USER_UPDATE);
        filter.addAction(Constants.INTENT_USER_DELETE);

        registerReceiver(mReceiver, filter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!mPaused) this.getSettings();

        mPaused = false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPaused = true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }

    @Override
    public void onClick(View view) {
        int textId;
        String title;
        String text;
        String hint;
        boolean isPassword = false;

        switch (view.getId()) {
            case R.id.settings_email:
                textId = R.id.settings_email;
                title = getString(R.string.settings_titleEmail);
                text = mEmail.getText().toString();
                hint = getString(R.string.hint_email);
                break;
            case R.id.settings_username:
                textId = R.id.settings_username;
                title = getString(R.string.settings_titleUsername);
                text = mUsername.getText().toString();
                hint = getString(R.string.hint_username);
                break;
            case R.id.settings_password:
                textId = R.id.settings_password;
                title = getString(R.string.settings_titlePassword);
                text = mPassword.getText().toString();
                hint = getString(R.string.hint_password);
                isPassword = true;
                break;
            case R.id.settings_btnChangeAvatar:
                this.changeAvatar();
                return;
            case R.id.settings_btnUpdate:
                this.updateAccount();
                return;
            case R.id.settings_btnDelete:
                this.deleteAccount();
                return;
            default:
                return;
        }

        EditDialogFragment.generate(
                getSupportFragmentManager(),
                this,
                textId,
                title,
                text,
                hint,
                isPassword
        );
    }

    @Override
    public void onEditDialogDismissOk(Object textId, String text) {
        TextView textView = (TextView) findViewById((int) textId);
        if (textView != null) {
            if ((int) textId == R.id.settings_password) {
                String dotPassword = text.replaceAll("(?s).", "*");
                textView.setTag(text);
                textView.setText(dotPassword);
            } else {
                textView.setText(text);
            }
        }
    }

    @Override
    public void onListDialogDismissOk(Parcelable p) {
        if (p instanceof Image) {
            mImage = (Image) p;

            final String url = mImage.getUrl();
            if (!TextUtils.isEmpty(url)) Utils.loadImage(this, url, mAvatar, 256);
            else mAvatar.setImageResource(mImage.getResId());
        }
    }

    @Override
    public void onTextDialogDismissOk(Object id) {
        Map<String, String> headers = new HashMap<>();
        headers.put(Constants.HEADER_CONTENT_TYPE, Constants.APPLICATION_JSON);
        headers.put(Constants.HEADER_ACCEPT, Constants.APPLICATION_JSON);

        Map<String, String> parameters = new HashMap<>();
        parameters.put(Constants.PARAM_TOKEN, Constants.SAMPLE_TOKEN);

        final APICall call = new APICall(
                this,
                Constants.INTENT_USER_DELETE,
                Constants.DELETE,
                Constants.API_USERS,
                headers,
                parameters
        );
        if (!call.isLoading()) call.execute();
    }

    private void getSettings() {
        Map<String, String> headers = new HashMap<>();
        headers.put(Constants.HEADER_CONTENT_TYPE, Constants.APPLICATION_JSON);
        headers.put(Constants.HEADER_ACCEPT, Constants.APPLICATION_JSON);

        Map<String, String> parameters = new HashMap<>();
        parameters.put(Constants.PARAM_TOKEN, Constants.SAMPLE_TOKEN);

        final APICall call = new APICall(
                this,
                Constants.INTENT_USER_GET,
                Constants.GET,
                Constants.API_USERS + "/598eccbc74459e02f455e4d9",
                headers,
                parameters
        );
        if (!call.isLoading()) call.execute();
    }

    private void changeAvatar() {
        final FragmentManager manager = getSupportFragmentManager();
        final String title = getString(R.string.dialogTitle_chooseAvatar);

        final ArrayList<Image> images = Utils.getDefaultAvatars();

        final RecyclerView.Adapter adapter = new ImagesAdapter(images);
        final RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 2);

        ListDialogFragment.generate(
                manager,
                this,
                title,
                images,
                adapter,
                layoutManager,
                null
        );
    }

    private void updateAccount() {
        final String avatar = mImage.getUrl();
        final String username = mUsername.getText().toString();
        final String email = mEmail.getText().toString();

        // Text is replaced with dots, so use tag instead of text.
        final String password = mPassword.getTag().toString();

        if (!avatar.equals(mCurrentUser.getAvatar()) ||
                !username.equals(mCurrentUser.getUsername()) ||
                !email.equals(mCurrentUser.getEmail())) {
            Map<String, String> headers = new HashMap<>();
            headers.put(Constants.HEADER_CONTENT_TYPE, Constants.APPLICATION_JSON);
            headers.put(Constants.HEADER_ACCEPT, Constants.APPLICATION_JSON);

            Map<String, String> parameters = new HashMap<>();
            parameters.put(Constants.PARAM_TOKEN, Constants.SAMPLE_TOKEN);

            Map<String, String> newSettings = new HashMap<>();
            newSettings.put(PARAM_AVATAR, avatar);
            newSettings.put(PARAM_USERNAME, username);
            newSettings.put(PARAM_EMAIL, email);
            if (!password.equals(mCurrentUser.getPassword()))
                newSettings.put(PARAM_PASSWORD, password);

            final String body = GsonUtils.getInstance().toJson(newSettings);

            final APICall call = new APICall(
                    this,
                    Constants.INTENT_USER_UPDATE,
                    Constants.PUT,
                    Constants.API_USERS,
                    body,
                    headers,
                    parameters
            );
            if (!call.isLoading()) call.execute();
        } else {
            Utils.toast(this, R.string.message_noChangesToUpdate);
        }
    }

    private void deleteAccount() {
        TextDialogFragment.generate(
                getSupportFragmentManager(),
                this,
                getString(R.string.dialogTitle_deleteAccount),
                getString(R.string.message_deleteAccount),
                0
        );
    }
}
