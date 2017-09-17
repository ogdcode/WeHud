package com.wehud.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.wehud.R;
import com.wehud.model.Auth;
import com.wehud.model.Payload;
import com.wehud.network.APICall;
import com.wehud.util.Constants;
import com.wehud.util.GsonUtils;
import com.wehud.util.PreferencesUtils;
import com.wehud.util.Utils;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String PARAM_USERNAME_OR_EMAIL = "usernameOrEmail";
    private static final String PARAM_PASSWORD = "password";

    private EditText mUsernameOrEmail;
    private EditText mPassword;

    private boolean mPaused;
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (!mPaused) {
                final String response = intent.getStringExtra(Constants.EXTRA_BROADCAST);
                final Payload payload = GsonUtils.getInstance().fromJson(response, Payload.class);

                final String code = payload.getCode();
                final String content = payload.getContent();

                if (intent.getAction().equals(Constants.INTENT_LOGIN)) {
                    if (Integer.valueOf(code) == Constants.HTTP_OK) {
                        final Auth auth = GsonUtils.getInstance().fromJson(content, Auth.class);

                        PreferencesUtils.put(context,
                                Constants.PREF_USER_ID, auth.getId()
                        );
                        PreferencesUtils.put(context,
                                Constants.PREF_TOKEN, auth.getToken()
                        );

                        context.startActivity(new Intent(context, MainActivity.class));
                    } else {
                        int messageId;
                        switch (Integer.valueOf(code)) {
                            case Constants.HTTP_FORBIDDEN:
                                messageId = R.string.error_invalidCredentials;
                                break;
                            case Constants.HTTP_INTERNAL_SERVER_ERROR:
                                messageId = R.string.error_server;
                                break;
                            default:
                                Utils.toast(context, R.string.error_unknown, code);
                                return;
                        }

                        Utils.toast(context, messageId);
                    }
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mUsernameOrEmail = (EditText) findViewById(R.id.usernameOrEmail);
        mPassword = (EditText) findViewById(R.id.password);

        Button forgotPasswordButton = (Button) findViewById(R.id.btnForgotPassword);
        Button signInButton = (Button) findViewById(R.id.btnSend);
        Button signUpButton = (Button) findViewById(R.id.btnSignUp);
        forgotPasswordButton.setOnClickListener(this);
        signInButton.setOnClickListener(this);
        signUpButton.setOnClickListener(this);

        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.INTENT_LOGIN);

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
        PreferencesUtils.clear(this);
        unregisterReceiver(mReceiver);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnForgotPassword:
                break;
            case R.id.btnSend:
                this.signIn();
                break;
            case R.id.btnSignUp:
                startActivity(new Intent(this, RegisterActivity.class));
                break;
            default:
                break;
        }
    }

    private void signIn() {
        TextView firstInvalidField = Utils.getFirstInvalidField(mUsernameOrEmail, mPassword);

        if (firstInvalidField != null)
            firstInvalidField.setError(getString(R.string.error_fieldRequired));
        else {
            Map<String, String> headers = new HashMap<>();
            headers.put(Constants.HEADER_CONTENT_TYPE, Constants.APPLICATION_JSON);
            headers.put(Constants.HEADER_ACCEPT, Constants.APPLICATION_JSON);

            final String usernameOrEmail = mUsernameOrEmail.getText().toString();
            final String password = mPassword.getText().toString();

            Map<String, String> login = new HashMap<>();
            login.put(PARAM_USERNAME_OR_EMAIL, usernameOrEmail);
            login.put(PARAM_PASSWORD, password);

            final String body = GsonUtils.getInstance().toJson(login);

            final APICall call = new APICall(
                    this,
                    Constants.INTENT_LOGIN,
                    Constants.POST,
                    Constants.API_LOGIN,
                    body,
                    headers
            );
            if (!call.isLoading()) call.execute();
        }
    }
}
