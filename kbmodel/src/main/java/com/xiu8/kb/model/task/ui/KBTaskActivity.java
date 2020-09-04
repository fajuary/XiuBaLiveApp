package com.xiu8.kb.model.task.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.android.frame.third.library.share.IShareInfo;
import com.android.frame.third.library.share.ShareUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;
import com.seek.library.activity.SeekBaseMVPActivity;
import com.seek.library.bean.ShareUrlBean;
import com.seek.library.widget.dialog.ShareDefaultListener;
import com.seek.library.widget.dialog.ShareDialog;
import com.seek.library.widget.toast.Toast;
import com.seek.library.widget.toolbar.SeekToolBar;
import com.xiu8.base.mvp.presenter.CreatePresenter;
import com.xiu8.kb.model.R;
import com.xiu8.kb.model.task.KBTaskBean;
import com.xiu8.kb.model.task.KBTaskPresenter;
import com.xiu8.kb.model.task.KBTaskView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

@CreatePresenter(KBTaskPresenter.class)
public class KBTaskActivity extends SeekBaseMVPActivity<KBTaskPresenter> implements KBTaskView, OnRefreshLoadMoreListener, BaseQuickAdapter.OnItemClickListener, ShareDialog.OnShareClickListener {

    private List<KBTaskBean> mData = new ArrayList<>();
    private SmartRefreshLayout mRefreshLayout;
    private RecyclerView mRecyclerView;
    private TaskAdapter mAdapter;
    private TaskAmountView mAmountView;
    private TaskFootView mFootView;
    private SeekToolBar mToolbar;
    private ShareDefaultListener mDefaultListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kb_activity_task);
    }


    @Override
    public void initViews() {
        mToolbar = (SeekToolBar) findViewById(R.id.prom_create_tool_bar);
        mToolbar.setCenterTitle("获取K币");
        mRefreshLayout = findView(R.id.kb_task_refresh);
        mRecyclerView = findView(R.id.kb_task_recycler_view);
        mRefreshLayout.setEnableLoadMore(false);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new TaskAdapter(mData);
        mRecyclerView.setAdapter(mAdapter);
        mAmountView = new TaskAmountView(this);
        mAmountView.setAmount("0");
        mAdapter.addHeaderView(mAmountView);
    }

    @Override
    public void initEvent() {
        mRefreshLayout.setOnRefreshListener(this);
        mAdapter.setOnItemClickListener(this);
        mToolbar.setOnItemClickListener(new SeekToolBar.OnItemClickListener() {
            @Override
            public void onItemClick(int id) {
                if (id == SeekToolBar.ID_LEFT_ICON) {
                    finish();
                }
            }
        });
    }

    @Override
    public void onPresenterFinished() {
        mPresenter.getTaskList();
    }

    @Override
    public void onRefresh(RefreshLayout refreshLayout) {
        mPresenter.getTaskList();
    }

    @Override
    public void onLoadMore(RefreshLayout refreshLayout) {
        //此功能,不需要实现，没有加载更多需求
    }

    @Override
    public void onAccountResult(long amount) {
        DecimalFormat format = new DecimalFormat("#,###.00");
        mAmountView.setAmount(format.format(amount));
    }

    @Override
    public void onTaskResult(List<KBTaskBean> result) {
        mRefreshLayout.finishRefresh();
        mAdapter.replaceData(result);
        if (result.size() > 1) {
            if (mFootView == null) {
                mFootView = new TaskFootView(this);
                mAdapter.addFooterView(mFootView);
            }
        }
    }

    @Override
    public void onTaskError() {
        mRefreshLayout.finishRefresh();
    }


    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        KBTaskBean bean = mData.get(position);
        mPresenter.quickJump(bean);
    }

    /****************
     *
     * 发起添加群流程。群号：男人帮(202854728) 的 key 为： KfERHXQKjqHo9OiEageKcivknGxsTKiF
     * 调用 joinQQGroup(KfERHXQKjqHo9OiEageKcivknGxsTKiF) 即可发起手Q客户端申请加群 男人帮(202854728)
     *
     * @return 返回true表示呼起手Q成功，返回fals表示呼起失败
     ******************/
    public void joinQQGroup(KBTaskBean bean) {
        Intent intent = new Intent();
        intent.setData(Uri.parse(bean.getExtend()));
        // 此Flag可根据具体产品需要自定义，如设置，则在加群界面按返回，返回手Q主界面，不设置，按返回会返回到呼起产品界面    //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try {
            startActivity(intent);
            mPresenter.finishTask(bean);
        } catch (Exception e) {
            Toast.showToast("未安装QQ或安装的版本不支持");
        }
    }

    @Override
    public void finisGroupTask(KBTaskBean bean) {
        int index = mData.indexOf(bean);
        mAdapter.setData(index, bean);
    }

    private ShareDialog mShareDialog;

    private void createDialog() {
        if (mShareDialog == null) {
            mShareDialog = new ShareDialog(this);
            mShareDialog.setOnShareClickListener(this);
        }
    }


    private IShareInfo getImageInfo(int platform, String imgUrl) {
        IShareInfo info = new IShareInfo();
        info.setType(IShareInfo.TYPE_IMAGE);
        info.setPlatform(platform);
        info.setImageUrl(imgUrl);
        return info;
    }

    private IShareInfo getShareInfo(int platform, String imgUrl, String targetUrl, String title, String summary) {
        IShareInfo info = new IShareInfo();
        info.setType(IShareInfo.TYPE_DEFAULT);
        info.setPlatform(platform);
        info.setImageUrl(imgUrl);
        info.setTitle(title);
        info.setTargetUrl(targetUrl);
        info.setSummary(summary);
        info.setPlatform(platform);
        return info;
    }

    public void onShareUserCard(KBTaskBean bean) {
        createDialog();
        mShareDialog.setTag(bean);
        mShareDialog.show();
    }

    public void onShareInvite(KBTaskBean bean) {
        createDialog();
        mShareDialog.setTag(bean);
        mShareDialog.show();
    }

    @Override
    public void onShareQQCard(String url) {
        IShareInfo info = getImageInfo(IShareInfo.PLATFORM_QQ, url);
        share(info);
    }

    @Override
    public void onShareWXCard(String url) {
        IShareInfo info = getImageInfo(IShareInfo.PLATFORM_WX, url);
        share(info);
    }

    @Override
    public void onShareWZCard(String url) {
        IShareInfo info = getImageInfo(IShareInfo.PLATFORM_WC, url);
        share(info);
    }

    @Override
    public void onShareError(String message) {
        Toast.showToast(message);
    }

    @Override
    public void onShareQQUrl(ShareUrlBean bean) {
        IShareInfo info = getShareInfo(IShareInfo.PLATFORM_QQ, bean.img, bean.url, bean.title, bean.content);
        share(info);
    }

    @Override
    public void onShareWXUrl(ShareUrlBean bean) {
        IShareInfo info = getShareInfo(IShareInfo.PLATFORM_WX, bean.img, bean.url, bean.title, bean.content);
        share(info);
    }

    @Override
    public void onShareWZUrl(ShareUrlBean bean) {
        IShareInfo info = getShareInfo(IShareInfo.PLATFORM_WC, bean.img, bean.url, bean.title, bean.content);
        share(info);
    }

    @Override
    public void onShareQQ() {
        KBTaskBean bean = (KBTaskBean) mShareDialog.getTag();
        if (bean.isShareCard()) {
            mPresenter.getShareUrl(3);
        } else {
            mPresenter.getShareTaskUrl(3);
        }
    }

    @Override
    public void onShareWXFriend() {
        KBTaskBean bean = (KBTaskBean) mShareDialog.getTag();
        if (bean.isShareCard()) {
            mPresenter.getShareUrl(1);
        } else {
            mPresenter.getShareTaskUrl(1);
        }
    }

    @Override
    public void onShareWXZone() {
        KBTaskBean bean = (KBTaskBean) mShareDialog.getTag();
        if (bean.isShareCard()) {
            mPresenter.getShareUrl(2);
        } else {
            mPresenter.getShareTaskUrl(2);
        }
    }

    private void share(IShareInfo info) {
        if (mDefaultListener == null) {
            mDefaultListener = new ShareDefaultListener();
        }
        ShareUtils.share(info, this, mDefaultListener);
    }

}
