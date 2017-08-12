package com.wehud.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

public final class VPAdapter extends FragmentStatePagerAdapter {
    private List<Fragment> mFragments;
    private List<String> mTitles;
    private Bundle mBundle;

    public VPAdapter(FragmentManager fm) {
        super(fm);
        mFragments = new ArrayList<>();
        mTitles = new ArrayList<>();
    }

    public void add(Fragment fragment, String title) {
        mFragments.add(fragment);
        mTitles.add(title);
    }

    public void remove(Fragment fragment, String title) {
        mFragments.remove(fragment);
        mTitles.remove(title);
    }

    public void setBundle(Bundle bundle) {
        mBundle = bundle;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = mFragments.get(position);
        if (mBundle != null) {
            fragment.setArguments(mBundle);
        }
        return mFragments.get(position);
    }

    @Override
    public int getItemPosition(Object item) {
        Fragment fragment = (Fragment) item;
        int position = mFragments.indexOf(fragment);

        if (position >=  0) return position;
        else return POSITION_NONE;
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }

    @Override
    public String getPageTitle(int position) {
        return mTitles.get(position);
    }
}
