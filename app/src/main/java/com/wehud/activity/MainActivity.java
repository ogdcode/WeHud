package com.wehud.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.wehud.R;
import com.wehud.dialog.TextDialogFragment;
import com.wehud.fragment.GamesFragment;
import com.wehud.fragment.HomeFragment;
import com.wehud.fragment.ProfileFragment;
import com.wehud.fragment.SendFragment;
import com.wehud.model.Payload;
import com.wehud.network.APICall;
import com.wehud.util.Constants;
import com.wehud.util.GsonUtils;
import com.wehud.util.PreferencesUtils;
import com.wehud.util.Utils;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity
        implements BottomNavigationView.OnNavigationItemSelectedListener, TextDialogFragment.OnTextDialogDismissOkListener {

    private static final String KEY_MENU_ID = "key_menu_id";

    private int mMenuId;

    private boolean mPaused;
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            if (!mPaused && intent.getAction().equals(Constants.INTENT_LOGOUT)) {
                final String response = intent.getStringExtra(Constants.EXTRA_BROADCAST);
                final Payload payload = GsonUtils.getInstance().fromJson(response, Payload.class);

                final String code = payload.getCode();

                if (Integer.valueOf(code) == Constants.HTTP_NO_CONTENT) {
                    PreferencesUtils.clear(MainActivity.this);
                    finish();
                } else if (Integer.valueOf(code) == Constants.HTTP_INTERNAL_SERVER_ERROR)
                    Utils.toast(MainActivity.this, R.string.error_server);
                else Utils.toast(MainActivity.this, R.string.error_general, code);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        BottomNavigationView navBottom = (BottomNavigationView) findViewById(R.id.nav_bottom);
        navBottom.setOnNavigationItemSelectedListener(this);

        if (savedInstanceState != null)
            mMenuId = savedInstanceState.getInt(KEY_MENU_ID);
        else mMenuId = R.id.menu_home;

        this.setMenu(mMenuId);

        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.INTENT_LOGOUT);

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
    public void onBackPressed() {
        this.attemptSignOut();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_MENU_ID, mMenuId);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        this.setMenu(item.getItemId());
        return true;
    }

    @Override
    public void onTextDialogDismissOk(Object id) {
        Map<String, String> headers = new HashMap<>();
        headers.put(Constants.HEADER_CONTENT_TYPE, Constants.APPLICATION_JSON);
        headers.put(Constants.HEADER_ACCEPT, Constants.APPLICATION_JSON);

        Map<String, String> parameters = new HashMap<>();
        parameters.put(Constants.PARAM_TOKEN, PreferencesUtils.get(this, Constants.PREF_TOKEN));

        final APICall call = new APICall(
                this,
                Constants.INTENT_LOGOUT,
                Constants.GET,
                Constants.API_LOGOUT,
                headers,
                parameters
        );
        if (!call.isLoading()) call.execute();
    }

    private void setMenu(int menuId) {
        Fragment fragment;
        int titleResourceId;

        switch (menuId) {
            case R.id.menu_home:
                fragment = HomeFragment.newInstance();
                titleResourceId = R.string.menu_home;
                break;
            case R.id.menu_send:
                fragment = SendFragment.newInstance();
                titleResourceId = R.string.title_send;
                break;
            case R.id.menu_games:
                fragment = GamesFragment.newInstance();
                titleResourceId = R.string.title_games;
                break;
            case R.id.menu_profile:
                fragment = ProfileFragment.newInstance();
                titleResourceId = R.string.title_profile;
                break;
            default:
                return;
        }

        setTitle(getString(titleResourceId));

        final FragmentManager manager = getSupportFragmentManager();
        final FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.commit();
    }

    private void attemptSignOut() {
        TextDialogFragment.generate(
                getSupportFragmentManager(),
                this,
                getString(R.string.dialogTitle_signingOut),
                getString(R.string.message_exitApp),
                0
        );
    }
}
