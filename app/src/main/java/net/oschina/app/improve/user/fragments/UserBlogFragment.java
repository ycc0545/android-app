package net.oschina.app.improve.user.fragments;


import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;

import net.oschina.app.R;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.improve.base.adapter.BaseListAdapter;
import net.oschina.app.improve.base.fragments.BaseListFragment;
import net.oschina.app.improve.bean.Blog;
import net.oschina.app.improve.bean.base.PageBean;
import net.oschina.app.improve.bean.base.ResultBean;
import net.oschina.app.improve.detail.activities.BlogDetailActivity;
import net.oschina.app.improve.general.adapter.BlogAdapter;

import java.lang.reflect.Type;

/**
 * 博客界面
 */
public class UserBlogFragment extends BaseListFragment<Blog> {

    public static final String HISTORY_BLOG = "history_my_blog";
    public static final String USER_ID = "user_id";
    private int userId;

    @Override
    protected void initBundle(Bundle bundle) {
        super.initBundle(bundle);
        userId = bundle.getInt(USER_ID, 0);
        // mIsRefresh = false;
    }


    @Override
    protected void requestData() {
        super.requestData();

        OSChinaApi.getUserBlogList(OSChinaApi.CATALOG_BLOG_NORMAL,
                (mIsRefresh ? (mBean != null ? mBean.getPrevPageToken() : null) : (mBean != null ? mBean.getNextPageToken() : null))
                , userId, mHandler);

    }

    @Override
    protected BaseListAdapter<Blog> getListAdapter() {
        BlogAdapter blogAdapter = new BlogAdapter(this);
        blogAdapter.setUserBlog(true);
        return blogAdapter;
    }

    @Override
    protected Type getType() {
        return new TypeToken<ResultBean<PageBean<Blog>>>() {
        }.getType();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Blog blog = mAdapter.getItem(position);
        if (blog != null) {
            BlogDetailActivity.show(getActivity(), blog.getId());
            TextView title = (TextView) view.findViewById(R.id.tv_item_blog_title);
            TextView content = (TextView) view.findViewById(R.id.tv_item_blog_body);

            updateTextColor(title, content);
            saveToReadedList(UserBlogFragment.HISTORY_BLOG, blog.getId() + "");
        }
    }

}
