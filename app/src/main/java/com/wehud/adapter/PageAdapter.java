package com.wehud.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public final class PageAdapter extends FragmentStatePagerAdapter {
    private List<Fragment> mFragments;
    private List<String> mTitles;

    public PageAdapter(FragmentManager fm) {
        super(fm);
        mFragments = new ArrayList<>();
        mTitles = new ArrayList<>();
    }

    public void add(Fragment fragment, String title, boolean dupes) {
        if (dupes) {
            mFragments.add(fragment);
            mTitles.add(title);
        } else {
            if (mFragments.contains(fragment)) mFragments.remove(mFragments.indexOf(fragment));
            mFragments.add(fragment);

            if (mTitles.contains(title)) mTitles.remove(mTitles.indexOf(title));
            mTitles.add(title);
        }
    }

    public void remove(Fragment fragment, String title) {
        mFragments.remove(fragment);
        mTitles.remove(title);
    }

    public void clear() {
        for (int i = 0; i < mFragments.size(); ++i) mFragments.remove(i);
        for (int i = 0; i < mTitles.size(); ++i) mTitles.remove(i);
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getItemPosition(Object item) {
        final Fragment fragment = (Fragment) item;
        final int position = mFragments.indexOf(fragment);

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
