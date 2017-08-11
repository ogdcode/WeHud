package com.wehud.activity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.wehud.R;
import com.wehud.fragment.HomeFragment;

public class MainActivity extends AppCompatActivity {

    private static final String KEY_MENUID = "key_menuId";

    private int mMenuId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (savedInstanceState != null)
            mMenuId = savedInstanceState.getInt(KEY_MENUID);
        else mMenuId = R.id.menu_home;

        this.setMenu(mMenuId);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_MENUID, mMenuId);
    }

    private void setMenu(int menuId) {
        Fragment fragment;
        int titleResourceId;

        switch (menuId) {
            case R.id.menu_home:
                fragment = HomeFragment.newInstance();
                titleResourceId = R.string.menu_home;
                break;
            default:
                fragment = new Fragment();
                titleResourceId = R.string.app_name;
                break;
        }

        setTitle(getString(titleResourceId));
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.commit();
    }
}
