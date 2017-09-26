package com.wehud.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.wehud.R;
import com.wehud.model.Payload;
import com.wehud.network.APICall;
import com.wehud.util.Constants;
import com.wehud.util.GsonUtils;
import com.wehud.util.Utils;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String PARAM_EMAIL = "email";
    private static final String PARAM_USERNAME = "username";
    private static final String PARAM_PASSWORD = "password";

    private EditText mEmail;
    private EditText mUsername;
    private EditText mPassword;
    private EditText mConfirmPassword;

    private boolean mPaused;
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Constants.INTENT_USER_CREATE) && !mPaused) {
                final String response = intent.getStringExtra(Constants.EXTRA_BROADCAST);
                final Payload payload = GsonUtils.getInstance().fromJson(response, Payload.class);

                final String code = payload.getCode();

                if (Integer.valueOf(code) == Constants.HTTP_CREATED) {
                    Utils.toast(RegisterActivity.this, R.string.message_accountCreated);
                    NavUtils.navigateUpFromSameTask(RegisterActivity.this);
                } else {
                    int messageId;
                    switch (Integer.valueOf(code)) {
                        case Constants.HTTP_INTERNAL_SERVER_ERROR:
                            messageId = R.string.error_server;
                            break;
                        default:
                            Utils.toast(RegisterActivity.this, R.string.error_unknown, code);
                            return;
                    }

                    Utils.toast(RegisterActivity.this, messageId);
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar ab = getSupportActionBar();
        if (ab != null) ab.setDisplayHomeAsUpEnabled(true);

        mEmail = (EditText) findViewById(R.id.email);
        mUsername = (EditText) findViewById(R.id.username);
        mPassword = (EditText) findViewById(R.id.password);
        mConfirmPassword = (EditText) findViewById(R.id.confirmPassword);

        Button createAccountButton = (Button) findViewById(R.id.btnCreateAccount);
        createAccountButton.setOnClickListener(this);

        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.INTENT_USER_CREATE);

        registerReceiver(mReceiver, filter);
    }

    @Override
    protected void onResume() {
        super.onResume();
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
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View view) {
        TextView firstInvalidField = Utils.getFirstInvalidField(
                mEmail, mUsername, mPassword, mConfirmPassword
        );

        if (firstInvalidField != null)
            firstInvalidField.setError(getString(R.string.error_fieldRequired));
        else {
            final String email = mEmail.getText().toString();
            final String username = mUsername.getText().toString();
            final String password = mPassword.getText().toString();
            final String confirmPassword = mConfirmPassword.getText().toString();

            Utils.clearText(mEmail, mUsername, mPassword, mConfirmPassword);

            if (!password.equals(confirmPassword)) {
                mPassword.setError(getString(R.string.error_passwords_no_match));
                mConfirmPassword.setError(getString(R.string.error_passwords_no_match));
            } else this.createAccount(email, username, password);
        }
    }

    private void createAccount(String... params) {
        Map<String, String> headers = new HashMap<>();
        headers.put(Constants.HEADER_CONTENT_TYPE, Constants.APPLICATION_JSON);
        headers.put(Constants.HEADER_ACCEPT, Constants.APPLICATION_JSON);

        Map<String, String> accountInfo = new HashMap<>();
        accountInfo.put(PARAM_EMAIL, params[0]);
        accountInfo.put(PARAM_USERNAME, params[1]);
        accountInfo.put(PARAM_PASSWORD, params[2]);

        final String body = GsonUtils.getInstance().toJson(accountInfo);

        final APICall call = new APICall(
                this,
                Constants.INTENT_USER_CREATE,
                Constants.POST,
                Constants.API_USERS,
                body,
                headers
        );
        if (!call.isLoading()) call.execute();
    }
}
