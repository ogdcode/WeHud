package com.wehud.activity;

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

public class MainActivity extends AppCompatActivity
        implements BottomNavigationView.OnNavigationItemSelectedListener, TextDialogFragment.OnTextDialogDismissOkListener {

    private static final String KEY_MENU_ID = "key_menu_id";

    private int mMenuId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        BottomNavigationView navBottom = (BottomNavigationView) findViewById(R.id.nav_bottom);
        navBottom.setOnNavigationItemSelectedListener(this);

        if (savedInstanceState != null)
            mMenuId = savedInstanceState.getInt(KEY_MENU_ID);
        else mMenuId = R.id.menu_home;

        this.setMenu(mMenuId);
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
        finish();
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
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
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
