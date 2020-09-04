package com.xiu8.kb.model.account.detail.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.seek.ann.AnnHelper;
import com.seek.library.activity.SeekBaseMVPActivity;
import com.seek.library.widget.toolbar.SeekToolBar;
import com.seek.library.xrouter.XRouterIntent;
import com.xiu8.base.mvp.presenter.CreatePresenter;
import com.xiu8.base.router.XRouter;
import com.xiu8.kb.model.R;
import com.xiu8.kb.model.account.detail.KBSeekDetailPresenter;
import com.xiu8.kb.model.account.detail.KBSeekDetailView;
import com.xiu8.kb.model.account.detail.SeekKBInfoBean;
import com.xiu8.kb.model.account.detail.ann.KbANNContacts;

import java.text.Annotation;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * 注：主要任务
 * 1，获取我的k币值信息
 * 2，获取k币明细列表数据（分页）
 *
 * 上传代码部分：
 * publiclibrary下面的http/url/UrlConstants文件
 * activity
 * res
 * http的url文件
 *
 */
@CreatePresenter(KBSeekDetailPresenter.class)
public class KBSeekDetailsActivity extends SeekBaseMVPActivity<KBSeekDetailPresenter>
        implements KBSeekDetailView, OnRefreshListener,BaseQuickAdapter.RequestLoadMoreListener {
    private RecyclerView mRecyclerView;
    private SmartRefreshLayout mSmartRefreshLayout;
    /**
     * 兑换按钮
     */
    private TextView exchangeTxt;
    /**
     * 我的k币数量
     */
    private TextView minekNumTxt;

    private KBSeekListAdapter kbSeekListAdapter;


    private SeekToolBar mSeekToolBar;
    //获取更多k币
    private TextView morekbTxt;
    private List<SeekKBInfoBean> allkbgetList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kb_activity_kbdetails);
    }

    /**
     * 在setContentView后面执行
     * 也即是如果不在onCreate中设置view的话将加载不出页面来
     * 该方法适合于初始化布局控件
     */
    @Override
    public void initViews() {
        super.initViews();

        mRecyclerView = findView(R.id.kb_activity_mRecyclerView);
        mSmartRefreshLayout = findView(R.id.kb_activity_mSmartRefreshLayout);

        exchangeTxt = findView(R.id.kbdetail_headlayout_exchangeTxt);
        minekNumTxt = findView(R.id.kbdetail_headlayout_minekNumTxt);

        mSeekToolBar = findView(R.id.kb_activity_mSeekToolBar);
        morekbTxt = findView(R.id.kb_activity_morekbTxt);

        setSmartRefreshLayout();

        allkbgetList = new ArrayList<>();
        kbSeekListAdapter = new KBSeekListAdapter(allkbgetList);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(kbSeekListAdapter);
//        AnnHelper.getInstance().
    }

    @Override
    protected void onResume() {
        super.onResume();
        AnnHelper.getInstance().onAnnMsg(KbANNContacts.PT,KbANNContacts.EN_PAGE);

    }

    /**
     * 设置SmartRefreshLayout属性
     */
    private void setSmartRefreshLayout() {
        //设置刷新时候头部展示view
        mSmartRefreshLayout.setRefreshHeader(
                new ClassicsHeader(this).setAccentColor(0xFFFFFFFF)
        );
        //设置加载更多时候显示的footerview
        mSmartRefreshLayout.setRefreshFooter(
                new ClassicsFooter(this).setAccentColor(0xFFFFFFFF)
        );
        mSmartRefreshLayout.setNoMoreData(false);
        ////是否能刷新
        mSmartRefreshLayout.setEnableRefresh(true);
        //是否加载更多 不能加载更多
        mSmartRefreshLayout.setEnableLoadMore(false);

    }

    /**
     * 事件点击处理
     */
    @Override
    public void initEvent() {
        super.initEvent();

        //刷新
        mSmartRefreshLayout.setOnRefreshListener(this);

        //加载更多
        kbSeekListAdapter.setOnLoadMoreListener(this, mRecyclerView);
        //兑换跳转的地方
        exchangeTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AnnHelper.getInstance().onAnnMsg(KbANNContacts.PT,KbANNContacts.EN_TO_REDEEM);

                //跳转
                XRouter.get().to(XRouterIntent.KB.Mall.Activity).navigation(KBSeekDetailsActivity.this);
            }
        });
        //左上角图片点击返回
        mSeekToolBar.setOnItemClickListener(new SeekToolBar.OnItemClickListener() {
            @Override
            public void onItemClick(int id) {

                if(id == SeekToolBar.ID_LEFT_ICON) {
                    onBackPressed();
                }
            }
        });
        //获取更多k币
        morekbTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**
                 * 跳到某个地方，当前activity对象
                 */
                AnnHelper.getInstance().onAnnMsg(KbANNContacts.PT,KbANNContacts.EN_GET_MORE_K_COIN);
                XRouter.get().to(XRouterIntent.KB.Task.Activity).navigation(KBSeekDetailsActivity.this);
            }
        });
    }

    /**
     * presenter的处理
     * 初始化完毕
     */
    @Override
    public void onPresenterFinished() {
        super.onPresenterFinished();
        mPresenter.refreshKBList();
    }

    /**
     * 账户k币余额
     *
     * @param amount
     */
    @Override
    public void onAccountResult(long amount) {
        DecimalFormat format = new DecimalFormat("#,###.00");
        String formatmoney = amount == 0 ? "0.00" : new DecimalFormat("#,###.00").format(amount);
        minekNumTxt.setText(formatmoney);
    }

    /**
     * 网络获取到的k币列表
     *
     * @param kbListInfoList
     * @param nomore   为true表示没有更多数据了，false表示还有数据
     */
    @Override
    public void getLoadMoreKbList(List<SeekKBInfoBean> kbListInfoList, boolean nomore) {
        kbSeekListAdapter.addData(kbListInfoList);
        if (nomore) {
            //没有更多数据的加载完成
            kbSeekListAdapter.loadMoreEnd();
        } else {
            //加载完成
            kbSeekListAdapter.loadMoreComplete();
        }
    }

    /**
     * 刷新获取到的数据
     * @param kbListInfoList
     */
    @Override
    public void getRefreshKblist(List<SeekKBInfoBean> kbListInfoList) {
        kbSeekListAdapter.setNewData(kbListInfoList);
        int allListlength=kbListInfoList.size();
        //总数据大于10条时候才会自动加载更多
        if (allListlength>10){
            kbSeekListAdapter.setPreLoadNumber(3);
        }
        //总数据少于6条时候蕴藏没有更多数据提示
        if (allListlength<6){
            kbSeekListAdapter.setEnableLoadMore(false);
        }
        mSmartRefreshLayout.finishRefresh();
        //
        kbSeekListAdapter.setEnableLoadMore(true);

    }

    /**
     * 没有更多数据了
     */
    @Override
    public void noMoreDataList() {
        kbSeekListAdapter.loadMoreEnd();
    }

    /**
     * 空页面展示
     */
    @Override
    public void showEmptyPage() {
        mSmartRefreshLayout.finishRefresh();
    }


    /**
     *
     * @param type 刷新还是加载更多 0：表示刷新 1：表示加载更多
     * @param errorMsg 错误提示
     */
    @Override
    public void onKBListError(int type,String errorMsg) {

        if (type == 0) {
            mSmartRefreshLayout.finishRefresh();
        } else {
            kbSeekListAdapter.loadMoreComplete();
        }
    }

    /**
     * 刷新数据
     *
     * @param refreshLayout
     */
    @Override
    public void onRefresh(RefreshLayout refreshLayout) {
        //这里的作用是防止下拉刷新的时候还可以上拉加载
        kbSeekListAdapter.setEnableLoadMore(false);
        mPresenter.refreshKBList();
    }
    
    /**
     * 加载更多数据
     */
    @Override
    public void onLoadMoreRequested() {
        mPresenter.loadMoreKbList();
    }

}
