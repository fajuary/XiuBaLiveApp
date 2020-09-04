package com.xiu8.kb.model.mall;

import android.app.Activity;
import android.content.Context;

import com.xiu8.base.mvp.view.IBaseView;

import java.util.List;

/**
 * Created by guojiel on 2018/8/29.
 * K币 兑换中心
 */

public interface IKBMallView extends IBaseView {

    Activity getActivity();

    void onRefreshListSuccess(List<KBGoodsBean> beans);

    void onRefreshListFail(String message, int code);

    void onLoadMoreSuccess(List<KBGoodsBean> beans);

    void onLoadMoreFail(String message, int code);

    long getAmount();

    void onAmountChange(long amount);

    void onGoodsChange();

}
