package com.wehud.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.wehud.R;
import com.wehud.adapter.VPAdapter;

public class HomeFragment extends Fragment implements ViewPager.OnPageChangeListener {

    private static final String KEY_CURRENTPAGE = "key_currentPage";

    private int mCurrentPage;
    private Context mContext;

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        setHasOptionsMenu(true);

        View view = inflater.inflate(R.layout.fragment_home, container, false);
        mContext = view.getContext();

        if (savedInstanceState != null)
            mCurrentPage = savedInstanceState.getInt(KEY_CURRENTPAGE);
        else mCurrentPage = 0;


        TabLayout tabs = (TabLayout) view.findViewById(android.R.id.tabs);
        ViewPager pager = (ViewPager) view.findViewById(R.id.pager);


        VPAdapter adapter = new VPAdapter(getChildFragmentManager());
        adapter.add(PostsFragment.newInstance(), getString(R.string.tab_myFeeds));

        pager.setAdapter(adapter);
        pager.setCurrentItem(mCurrentPage);
        pager.addOnPageChangeListener(this);
        tabs.setupWithViewPager(pager);

        return view;
    }

    /*
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (mCurrentPage == 0)
            menu.findItem(R.id.item_delete).setVisible(false);
        else
            menu.findItem(R.id.item_delete).setVisible(true);
        inflater.inflate(R.menu.menu_page, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
    */

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        mCurrentPage = position;
        if (mCurrentPage == 0) getActivity().invalidateOptionsMenu();
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_CURRENTPAGE, mCurrentPage);
    }
}
