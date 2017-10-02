package com.wehud.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * This {@link FragmentStatePagerAdapter} subclass
 * handles {@link Fragment} instances and is used
 * with a {@link android.support.v4.view.ViewPager} object.
 *
 * @author Olivier Gonçalves, 2017
 */
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

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    /**
     * This method was overridden to force the
     * adapter to redraw {@link Fragment} objects
     * that changed places after a call to the adapter's
     * notifySetDataChanged() method.
     *
     * @param item an item in the adapter
     * @return the position of the item
     */
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
