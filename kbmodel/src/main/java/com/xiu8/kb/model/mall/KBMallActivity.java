package com.xiu8.kb.model.mall;

import android.app.Activity;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;
import com.seek.library.activity.SeekBaseMVPActivity;
import com.seek.library.widget.toast.Toast;
import com.seek.library.widget.toolbar.SeekToolBar;
import com.xiu8.base.mvp.presenter.CreatePresenter;
import com.xiu8.base.util.DensityUtils;
import com.xiu8.kb.model.R;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by guojiel on 2018/8/29.
 */
@CreatePresenter(KBMallPresenter.class)
public class KBMallActivity extends SeekBaseMVPActivity<KBMallPresenter> implements IKBMallView{

    private SeekToolBar mToolBar;
    private SmartRefreshLayout mRefreshLayout;
    private RecyclerView mRv;
    private KBMallListAdapter mAdapter;
    private BaseViewHolder mBalanceTitleViewHolder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kb_mall_activity);
    }

    @Override
    public void initViews() {
        super.initViews();
        mToolBar = findView(R.id.mToolBar);
        mToolBar.setOnItemClickListener(new OnBackClickListener());
        findView(R.id.mCustomerBtn).setOnClickListener(new OnCustomerBtnClickListener());
        mRefreshLayout = findView(R.id.mRefreshLayout);
        mRefreshLayout.setRefreshHeader(
                new ClassicsHeader(this).setAccentColor(0xFFFFFFFF)
        );
        mRefreshLayout.setRefreshFooter(
                new ClassicsFooter(this).setAccentColor(0xFFFFFFFF)
        );
        mRefreshLayout.setEnableLoadMore(true);
        mRefreshLayout.setOnRefreshLoadMoreListener(new OnRefreshListener());
        mRv = findView(R.id.mRv);

        mRv.setLayoutManager(new LinearLayoutManager(this));
        mRv.addItemDecoration(new Divider());
        mAdapter = new KBMallListAdapter();
        mAdapter.setOnItemChildClickListener(new OnItemChildClickListener());
        mRv.setAdapter(mAdapter);
        mBalanceTitleViewHolder = new BaseViewHolder(
                LayoutInflater.from(this).inflate(R.layout.kb_mall_balance_item, mRv, false)
        );
        mAdapter.addHeaderView(mBalanceTitleViewHolder.itemView);
    }

    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    public void onRefreshListSuccess(List<KBGoodsBean> beans) {
        mAdapter.replaceData(beans);
        mRefreshLayout.finishRefresh();
        mRefreshLayout.setNoMoreData(false);
    }

    @Override
    public void onRefreshListFail(String message, int code) {
        mRefreshLayout.finishRefresh();
    }

    @Override
    public void onLoadMoreSuccess(List<KBGoodsBean> beans) {
        if(beans == null || beans.isEmpty()){
            mRefreshLayout.finishLoadMoreWithNoMoreData();
        }else{
            mAdapter.addData(beans);
            mRefreshLayout.finishLoadMore();
        }
    }

    @Override
    public void onLoadMoreFail(String message, int code) {
        mRefreshLayout.finishLoadMore();
    }

    @Override
    public long getAmount() {
        Object amount = mBalanceTitleViewHolder.getView(R.id.balance).getTag();
        if(amount == null){
            return 0;
        }
        return (long) amount;
    }

    @Override
    public void onAmountChange(long amount) {
        mBalanceTitleViewHolder.setTag(R.id.balance, amount);
        String kb = amount == 0 ? "0.00" : new DecimalFormat("#,###.00").format(amount);
        mBalanceTitleViewHolder.setText(R.id.balance, kb);
    }

    @Override
    public void onGoodsChange() {
        mAdapter.notifyDataSetChanged();
    }

    //返回键按钮
    private class OnBackClickListener implements SeekToolBar.OnItemClickListener{
        @Override
        public void onItemClick(int id) {
            if(id == SeekToolBar.ID_LEFT_ICON) {
                onBackPressed();
            }
        }
    }
    //联系客服
    private class OnCustomerBtnClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            mPresenter.startCustomerPage();
        }
    }
    //上拉下拉刷新
    private class OnRefreshListener implements OnRefreshLoadMoreListener{
        @Override
        public void onLoadMore(RefreshLayout refreshLayout) {
            mPresenter.loadMoreList();
        }

        @Override
        public void onRefresh(RefreshLayout refreshLayout) {
            mPresenter.refreshAmount();
            mPresenter.refreshList();
        }
    }
    //item项中点击事件
    private class OnItemChildClickListener implements BaseQuickAdapter.OnItemChildClickListener{
        @Override
        public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
            if(view.getId() == R.id.exchange){
                KBGoodsBean item = mAdapter.getItem(position);
                if(item != null){
                    if(item.isKbEnough(getAmount())){
                        mPresenter.warningBuy(item);
                    }else{
                        Toast.showToast("K币余额不足");
                    }
                }
            }
        }
    }
    //rv divider
    private class Divider extends RecyclerView.ItemDecoration {

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            outRect.bottom = DensityUtils.dip2px(KBMallActivity.this, 15);
            outRect.left = DensityUtils.dip2px(KBMallActivity.this, 10);
            outRect.right = DensityUtils.dip2px(KBMallActivity.this, 10);
        }

    }

}
