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
import com.wehud.dialog.TextDialogFragment;
import com.wehud.model.Payload;
import com.wehud.network.APICall;
import com.wehud.util.Constants;
import com.wehud.util.GsonUtils;
import com.wehud.util.Utils;

import java.util.HashMap;
import java.util.Map;

public class ForgotPasswordActivity extends AppCompatActivity
        implements View.OnClickListener, TextDialogFragment.OnTextDialogDismissOkListener {

    private static final String PARAM_USERNAME_OR_EMAIL = "usernameOrEmail";

    private EditText mUsernameOrEmail;

    private boolean mPaused;
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String response = intent.getStringExtra(Constants.EXTRA_BROADCAST);
            final Payload payload = GsonUtils.getInstance().fromJson(response, Payload.class);

            if (!mPaused) {
                final String code = payload.getCode();

                if (Integer.valueOf(code) == Constants.HTTP_OK)
                    TextDialogFragment.generate(
                            getSupportFragmentManager(),
                            ForgotPasswordActivity.this,
                            getString(R.string.dialogTitle_emailSent),
                            getString(R.string.message_sendSuccess),
                            0
                    );
                else {
                    int messageId;
                    switch (Integer.valueOf(code)) {
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
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar ab = getSupportActionBar();
        if (ab != null) ab.setDisplayHomeAsUpEnabled(true);

        mUsernameOrEmail = (EditText) findViewById(R.id.usernameOrEmail);
        Button sendButton = (Button) findViewById(R.id.btnSend);
        sendButton.setOnClickListener(this);

        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.INTENT_FORGOT_PASSWORD);

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
                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btnSend) this.send();
    }

    @Override
    public void onTextDialogDismissOk(Object o) {
        NavUtils.navigateUpFromSameTask(this);
    }

    private void send() {
        TextView firstInvalidField = Utils.getFirstInvalidField(mUsernameOrEmail);

        if (firstInvalidField != null)
            firstInvalidField.setError(getString(R.string.error_fieldRequired));
        else {
            final String usernameOrEmail = mUsernameOrEmail.getText().toString();

            Map<String, String> headers = new HashMap<>();
            headers.put(Constants.HEADER_CONTENT_TYPE, Constants.APPLICATION_JSON);
            headers.put(Constants.HEADER_ACCEPT, Constants.APPLICATION_JSON);

            Map<String, String> info = new HashMap<>();
            info.put(PARAM_USERNAME_OR_EMAIL, usernameOrEmail);

            final String body = GsonUtils.getInstance().toJson(info);

            final APICall call = new APICall(
                    this,
                    Constants.INTENT_FORGOT_PASSWORD,
                    Constants.POST,
                    Constants.API_FORGOT_PASSWORD,
                    body,
                    headers
            );
            if (!call.isLoading()) call.execute();
        }
    }
}
