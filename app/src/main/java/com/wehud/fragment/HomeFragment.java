package com.wehud.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.reflect.TypeToken;
import com.wehud.R;
import com.wehud.adapter.PageAdapter;
import com.wehud.dialog.EditDialogFragment;
import com.wehud.dialog.TextDialogFragment;
import com.wehud.model.Page;
import com.wehud.model.Payload;
import com.wehud.model.Reward;
import com.wehud.network.APICall;
import com.wehud.util.Constants;
import com.wehud.util.GsonUtils;
import com.wehud.util.PreferencesUtils;
import com.wehud.util.Utils;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class HomeFragment extends Fragment implements TabLayout.OnTabSelectedListener,
        TextDialogFragment.OnTextDialogDismissOkListener,
        EditDialogFragment.OnEditDialogDismissOkListener {

    private static final String KEY_CURRENT_PAGE = "key_current_page";
    private static final String PARAM_TITLE = "title";

    private Context mContext;
    private int mCurrentPage;
    private List<Page> mPages;

    private PageAdapter mAdapter;
    private TabLayout mTabs;
    private ViewPager mPager;

    private boolean mPaused;
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            if (!mPaused) {
                if (!TextUtils.isEmpty(intent.getStringExtra(Constants.EXTRA_REFRESH_PAGE)) &&
                        intent.getAction().equals(Constants.INTENT_REFRESH_PAGE)) {
                    final String pageId = intent.getStringExtra(Constants.EXTRA_REFRESH_PAGE);
                    getPages(pageId);
                }

                if (!TextUtils.isEmpty(intent.getStringExtra(Constants.EXTRA_BROADCAST))) {
                    final String response = intent.getStringExtra(Constants.EXTRA_BROADCAST);
                    final Payload payload = GsonUtils.getInstance().fromJson(
                            response,
                            Payload.class
                    );

                    final String code = payload.getCode();

                    if (Integer.valueOf(code) == Constants.HTTP_OK ||
                            Integer.valueOf(code) == Constants.HTTP_CREATED ||
                            Integer.valueOf(code) == Constants.HTTP_NO_CONTENT) {
                        final String content = payload.getContent();

                        switch (intent.getAction()) {
                            case Constants.INTENT_PAGES_LIST:
                                final Type pageListType = new TypeToken<List<Page>>(){}.getType();
                                mPages = GsonUtils.getInstance().fromJson(content, pageListType);

                                if (!mPages.isEmpty()) {
                                    int i = 1;
                                    for (Page page : mPages)
                                        mAdapter.add(
                                                PageFragment.newInstance(
                                                        i++,
                                                        page.getId(),
                                                        page.getPosts()
                                                ),
                                                page.getTitle(),
                                                false
                                        );

                                    mAdapter.notifyDataSetChanged();
                                    mPager.setOffscreenPageLimit(mPages.size());
                                    mPager.invalidate();
                                } else mTabs.setVisibility(View.GONE);

                                break;
                            case Constants.INTENT_PAGES_ADD:
                                final Page page = GsonUtils.getInstance().fromJson(
                                        content,
                                        Page.class
                                );

                                mPages.add(page);
                                mAdapter.add(
                                        PageFragment.newInstance(
                                                mPages.size(),
                                                page.getId(),
                                                page.getPosts()
                                        ),
                                        page.getTitle(),
                                        false
                                );

                                mAdapter.notifyDataSetChanged();
                                mPager.setOffscreenPageLimit(mPages.size());
                                mPager.invalidate();

                                final Reward reward = Utils.getNestedReward(content);
                                if (Utils.isNotEmpty(reward.getEntities()))
                                    Utils.generateRewardDialog(
                                            mContext,
                                            getFragmentManager(),
                                            HomeFragment.this,
                                            reward,
                                            0
                                    );
                                else if (mPages.size() > 0) mTabs.setVisibility(View.VISIBLE);

                                break;
                            case Constants.INTENT_PAGES_DELETE:
                                final String title = mAdapter.getPageTitle(mCurrentPage);

                                mPages.remove(mCurrentPage - 1);
                                mAdapter.remove(
                                        mAdapter.getItem(mCurrentPage),
                                        title
                                );
                                mAdapter.notifyDataSetChanged();
                                mPager.setOffscreenPageLimit(mPages.size());
                                mPager.invalidate();

                                if (mPages.isEmpty()) mTabs.setVisibility(View.GONE);

                                Utils.toast(mContext, R.string.message_pageRemoved, title);
                                break;
                            case Constants.INTENT_REFRESH_PAGE:
                                if (Integer.valueOf(code) == Constants.HTTP_OK) {
                                    Intent refreshIntent = new Intent(Constants.INTENT_REFRESH_POSTS);
                                    refreshIntent.putExtra(Constants.EXTRA_REFRESH_POSTS, content);
                                    mContext.sendBroadcast(refreshIntent);
                                }
                                break;
                            default:
                                break;
                        }
                    } else {
                        int messageId;
                        switch (Integer.valueOf(code)) {
                            case Constants.HTTP_UNAUTHORIZED:
                                messageId = R.string.error_sessionExpired;
                                getActivity().finish();
                                break;
                            case Constants.HTTP_INTERNAL_SERVER_ERROR:
                                messageId = R.string.error_server;
                                break;
                            default:
                                Utils.toast(mContext, R.string.error_general, code);
                                return;
                        }

                        Utils.toast(mContext, messageId);
                    }
                }
            }
        }
    };

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        final View view = inflater.inflate(R.layout.fragment_home, container, false);
        mContext = view.getContext();

        if (savedInstanceState != null) mCurrentPage = savedInstanceState.getInt(KEY_CURRENT_PAGE);

        mTabs = (TabLayout) view.findViewById(android.R.id.tabs);
        mPager = (ViewPager) view.findViewById(R.id.pager);

        mAdapter = new PageAdapter(getChildFragmentManager());
        mAdapter.add(
                PageFragment.newInstance(0, Utils.generateId(new Random()), null),
                getString(R.string.tab_myFeeds),
                false
        );

        mTabs.addOnTabSelectedListener(this);
        mPager.setAdapter(mAdapter);
        mPager.setCurrentItem(mCurrentPage);
        mPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabs));
        mTabs.setupWithViewPager(mPager);

        return view;

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.INTENT_PAGES_LIST);
        filter.addAction(Constants.INTENT_PAGES_ADD);
        filter.addAction(Constants.INTENT_PAGES_DELETE);
        filter.addAction(Constants.INTENT_REFRESH_PAGE);

        mContext.registerReceiver(mReceiver, filter);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!mPaused) this.getPages(null);

        mPaused = false;
    }

    @Override
    public void onPause() {
        super.onPause();
        mPaused = true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mContext.unregisterReceiver(mReceiver);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_posts, menu);
        if (mCurrentPage == 0)
            menu.findItem(R.id.menu_delete).setVisible(false);
        else
            menu.findItem(R.id.menu_delete).setVisible(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        FragmentManager manager = getFragmentManager();
        switch (item.getItemId()) {
            case R.id.menu_post_add:
                EditDialogFragment.generate(
                        manager, this, 0,
                        getString(R.string.dialogTitle_addPage), null,
                        getString(R.string.hint_name), false
                );
                break;
            case R.id.menu_delete:
                final String pageTitle = mPages.get(mCurrentPage - 1).getTitle();
                TextDialogFragment.generate(
                        manager, this, pageTitle,
                        getString(R.string.dialogMessage_removePage), 1
                );
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_CURRENT_PAGE, mCurrentPage);
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        final int position = tab.getPosition();
        if (mCurrentPage == 0 || position == 0) getActivity().invalidateOptionsMenu();
        mCurrentPage = position;
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTextDialogDismissOk(Object id) {
        switch ((int) id) {
            case 1:
                this.deletePage();
                break;
            default:
                if (mPages.size() > 1) mTabs.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public void onEditDialogDismissOk(Object id, String text) {
        this.createPage(text);
    }

    private void getPages(String pageId) {
        Map<String, String> headers = new HashMap<>();
        headers.put(Constants.HEADER_CONTENT_TYPE, Constants.APPLICATION_JSON);
        headers.put(Constants.HEADER_ACCEPT, Constants.APPLICATION_JSON);

        final String connectedUserId = PreferencesUtils.get(mContext, Constants.PREF_USER_ID);
        StringBuilder url = new StringBuilder();
        StringBuilder atn = new StringBuilder();
        if (pageId != null) {
            String toAppend = Constants.API_PAGES + '/' + pageId + Constants.API_PAGE_POSTS;
            url.append(toAppend);
            atn.append(Constants.INTENT_REFRESH_PAGE);
        } else {
            String toAppend = Constants.API_USERS_PAGES + '/' + connectedUserId;
            url.append(toAppend);
            atn.append(Constants.INTENT_PAGES_LIST);
        }

        final APICall call = new APICall(
                mContext,
                atn.toString(),
                Constants.GET,
                url.toString(),
                headers
        );
        if (!call.isLoading()) call.execute();
    }

    private void createPage(String title) {
        Map<String, String> headers = new HashMap<>();
        headers.put(Constants.HEADER_CONTENT_TYPE, Constants.APPLICATION_JSON);
        headers.put(Constants.HEADER_ACCEPT, Constants.APPLICATION_JSON);

        Map<String, String> parameters = new HashMap<>();
        parameters.put(Constants.PARAM_TOKEN, PreferencesUtils.get(mContext, Constants.PREF_TOKEN));

        Map<String, String> page = new HashMap<>();
        page.put(PARAM_TITLE, title);

        final String body = GsonUtils.getInstance().toJson(page);

        APICall call = new APICall(
                mContext,
                Constants.INTENT_PAGES_ADD,
                Constants.POST,
                Constants.API_PAGES,
                body,
                headers,
                parameters
        );
        if (!call.isLoading()) call.execute();
    }

    private void deletePage() {
        Map<String, String> headers = new HashMap<>();
        headers.put(Constants.HEADER_CONTENT_TYPE, Constants.APPLICATION_JSON);
        headers.put(Constants.HEADER_ACCEPT, Constants.APPLICATION_JSON);

        Map<String, String> parameters = new HashMap<>();
        parameters.put(Constants.PARAM_TOKEN, PreferencesUtils.get(mContext, Constants.PREF_TOKEN));

        final String pageId = mPages.get(mCurrentPage - 1).getId();

        final APICall call = new APICall(
                mContext,
                Constants.INTENT_PAGES_DELETE,
                Constants.DELETE,
                Constants.API_PAGES + '/' + pageId,
                headers,
                parameters
        );
        if (!call.isLoading()) call.execute();
    }
}
