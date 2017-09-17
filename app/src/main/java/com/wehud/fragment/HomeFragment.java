package com.wehud.fragment;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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

import com.google.gson.reflect.TypeToken;
import com.wehud.R;
import com.wehud.adapter.VPAdapter;
import com.wehud.dialog.EditDialogFragment;
import com.wehud.dialog.TextDialogFragment;
import com.wehud.model.Page;
import com.wehud.model.Payload;
import com.wehud.model.Post;
import com.wehud.network.APICall;
import com.wehud.util.Constants;
import com.wehud.util.GsonUtils;
import com.wehud.util.PreferencesUtils;
import com.wehud.util.Utils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeFragment extends Fragment implements ViewPager.OnPageChangeListener,
        EditDialogFragment.OnEditDialogDismissOkListener, TextDialogFragment.OnTextDialogDismissOkListener {

    private static final String KEY_CURRENT_PAGE = "key_current_page";
    private static final String KEY_PAGE_POSTS = "key_page_posts";

    private static final String PARAM_TITLE = "title";

    private VPAdapter mAdapter;
    private ViewPager mPager;

    private int mCurrentPage;
    private Context mContext;

    private List<Page> mPages;

    private boolean mPaused;
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (!mPaused) {
                final String response = intent.getStringExtra(Constants.EXTRA_BROADCAST);
                final Payload payload = GsonUtils.getInstance().fromJson(response, Payload.class);

                final String code = payload.getCode();

                if (Integer.valueOf(code) == Constants.HTTP_OK ||
                        Integer.valueOf(code) == Constants.HTTP_NO_CONTENT) {
                    final String content = payload.getContent();

                    switch (intent.getAction()) {
                        case Constants.INTENT_PAGES_LIST:
                            final Type pageListType = new TypeToken<List<Page>>(){}.getType();
                            mPages = GsonUtils.getInstance().fromJson(content, pageListType);

                            if (!mPages.isEmpty()) {
                                for (Page page : mPages) {
                                    final List<Post> pagePosts = page.getPosts();
                                    PostsFragment fragment = PostsFragment.newInstance();
                                    if (pagePosts != null && !pagePosts.isEmpty()) {
                                        Bundle args = new Bundle();
                                        args.putParcelableArrayList(KEY_PAGE_POSTS,
                                                (ArrayList<Post>) page.getPosts()
                                        );
                                        fragment.setArguments(args);
                                    }

                                    mAdapter.add(fragment, page.getTitle());
                                }

                                mAdapter.notifyDataSetChanged();
                                mPager.invalidate();
                            }
                            break;
                        case Constants.INTENT_PAGES_ADD:
                            final Page page = GsonUtils.getInstance().fromJson(content, Page.class);

                            mPages.add(page);
                            mAdapter.add(PostsFragment.newInstance(), page.getTitle());
                            mAdapter.notifyDataSetChanged();
                            mPager.invalidate();
                            break;
                        case Constants.INTENT_PAGES_DELETE:
                            final Fragment item = mAdapter.getItem(mCurrentPage);
                            final String title = mAdapter.getPageTitle(mCurrentPage);

                            mPages.remove(mCurrentPage);
                            mAdapter.remove(
                                    item,
                                    title
                            );
                            mAdapter.notifyDataSetChanged();
                            mPager.invalidate();

                            Utils.toast(mContext, R.string.message_pageRemoved, title);
                            break;
                        default:
                            break;
                    }
                } else if (Integer.valueOf(code) == Constants.HTTP_INTERNAL_SERVER_ERROR)
                    Utils.toast(mContext, R.string.error_server);
                else Utils.toast(mContext, R.string.error_general, code);
            }
        }
    };

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        setHasOptionsMenu(true);

        final View view = inflater.inflate(R.layout.fragment_home, container, false);
        mContext = view.getContext();

        if (savedInstanceState != null) mCurrentPage = savedInstanceState.getInt(KEY_CURRENT_PAGE);
        else mCurrentPage = 0;

        TabLayout tabs = (TabLayout) view.findViewById(android.R.id.tabs);
        mPager = (ViewPager) view.findViewById(R.id.pager);

        mAdapter = new VPAdapter(getChildFragmentManager());
        mAdapter.add(PostsFragment.newInstance(), getString(R.string.tab_myFeeds));

        mPager.setAdapter(mAdapter);
        mPager.setCurrentItem(mCurrentPage);
        mPager.addOnPageChangeListener(this);
        tabs.setupWithViewPager(mPager);

        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.INTENT_PAGES_ADD);
        filter.addAction(Constants.INTENT_PAGES_LIST);
        filter.addAction(Constants.INTENT_PAGES_DELETE);

        mContext.registerReceiver(mReceiver, filter);

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        mPaused = true;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!mPaused) this.getPages();

        mPaused = false;
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
        switch (item.getItemId()) {
            case R.id.menu_post_add:
                EditDialogFragment.generate(
                        getFragmentManager(), this, 0,
                        getString(R.string.dialogTitle_addPage), null,
                        getString(R.string.hint_name), false
                );
                break;
            case R.id.menu_delete:
                final String pageTitle = mPages.get(mCurrentPage - 1).getTitle();
                TextDialogFragment.generate(
                        getFragmentManager(), this,
                        pageTitle, getString(R.string.dialogMessage_removePage),
                        0
                );
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        if (mCurrentPage == 0 || position == 0) getActivity().invalidateOptionsMenu();
        mCurrentPage = position;
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_CURRENT_PAGE, mCurrentPage);
    }

    @Override
    public void onEditDialogDismissOk(Object id, String text) {
        if (mReceiver != null) this.createPage(text);
    }

    @Override
    public void onTextDialogDismissOk(Object id) {
        if (mReceiver != null) this.deletePage();
    }

    private void getPages() {
        Map<String, String> headers = new HashMap<>();
        headers.put(Constants.HEADER_CONTENT_TYPE, Constants.APPLICATION_JSON);
        headers.put(Constants.HEADER_ACCEPT, Constants.APPLICATION_JSON);

        final APICall call = new APICall(
                mContext,
                Constants.INTENT_PAGES_LIST,
                Constants.GET,
                Constants.API_PAGES,
                headers
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

    private void createPage(String title) {
        Map<String, String> headers = new HashMap<>();
        headers.put(Constants.HEADER_CONTENT_TYPE, Constants.APPLICATION_JSON);
        headers.put(Constants.HEADER_ACCEPT, Constants.APPLICATION_JSON);

        Map<String, String> parameters = new HashMap<>();
        parameters.put(Constants.PARAM_TOKEN, PreferencesUtils.get(mContext, Constants.PREF_TOKEN));

        Map<String, String> page = new HashMap<>();
        page.put(PARAM_TITLE, title);

        String body = GsonUtils.getInstance().toJson(page);

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
}
