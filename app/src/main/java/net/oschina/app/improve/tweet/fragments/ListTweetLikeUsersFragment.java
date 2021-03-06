package net.oschina.app.improve.tweet.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.TextHttpResponseHandler;

import net.oschina.app.AppContext;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.bean.User;
import net.oschina.app.improve.base.adapter.BaseRecyclerAdapter;
import net.oschina.app.improve.base.fragments.BaseRecyclerViewFragment;
import net.oschina.app.improve.bean.base.PageBean;
import net.oschina.app.improve.bean.base.ResultBean;
import net.oschina.app.improve.bean.simple.TweetLike;
import net.oschina.app.improve.tweet.adapter.TweetLikeUsersAdapter;
import net.oschina.app.improve.tweet.contract.TweetDetailContract;
import net.oschina.app.util.UIHelper;

import java.lang.reflect.Type;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * 动弹详情, 点赞列表
 * Created by thanatos
 * on 16/6/13.
 */
public class ListTweetLikeUsersFragment extends BaseRecyclerViewFragment<TweetLike> implements TweetDetailContract.IThumbupView {

    private TweetDetailContract.Operator mOperator;
    private TweetDetailContract.IAgencyView mAgencyView;

    public static ListTweetLikeUsersFragment instantiate(TweetDetailContract.Operator operator, TweetDetailContract.IAgencyView mAgencyView) {
        ListTweetLikeUsersFragment fragment = new ListTweetLikeUsersFragment();
        fragment.mOperator = operator;
        fragment.mAgencyView = mAgencyView;
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mOperator = (TweetDetailContract.Operator) activity;
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    mOperator.onScroll();
                }
            }
        });
    }

    @Override
    protected BaseRecyclerAdapter<TweetLike> getRecyclerAdapter() {
        return new TweetLikeUsersAdapter(getContext());
    }

    @Override
    protected Type getType() {
        return new TypeToken<ResultBean<PageBean<TweetLike>>>(){}.getType();
    }

    @Override
    public void onLoadMore() {
        OSChinaApi.getTweetLikeList(
                mOperator.getTweetDetail().getId(),
                mIsRefresh ? mBean.getPrevPageToken() : mBean.getNextPageToken(),
                mHandler
        );
    }

    @Override
    protected void requestData() {
        OSChinaApi.getTweetLikeList(mOperator.getTweetDetail().getId(), null, mHandler);
    }

    @Override
    protected void onRequestSuccess(int code) {
        super.onRequestSuccess(code);
        if (mAdapter.getCount() < 20 && mAgencyView != null)
            mAgencyView.resetLikeCount(mAdapter.getCount());
    }

    @Override
    public void onItemClick(int position, long itemId) {
        super.onItemClick(position, itemId);
        TweetLike liker = mAdapter.getItem(position);
        UIHelper.showUserCenter(getContext(), liker.getAuthor().getId(), liker.getAuthor().getName());
    }

    @Override
    public void onLikeSuccess(boolean isUp, User user) {
        onRefreshing();
    }
}
