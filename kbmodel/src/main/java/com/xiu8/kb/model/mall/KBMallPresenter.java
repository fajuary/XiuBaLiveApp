package com.xiu8.kb.model.mall;

import android.content.Intent;
import android.graphics.Paint;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.LineHeightSpan;

import com.seek.library.EventKey;
import com.seek.library.http.SeekPObserver;
import com.seek.library.user.UserHelp;
import com.seek.library.widget.dialog.XWarningDialog;
import com.seek.library.xrouter.XRouterIntent;
import com.xiu8.base.bus.IBusEvent;
import com.xiu8.base.mvp.model.CreateModel;
import com.xiu8.base.mvp.presenter.BasePresenter;
import com.xiu8.base.router.XRouter;
import com.xiu8.base.util.DensityUtils;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by guojiel on 2018/8/29.
 */
@CreateModel(KBMallModel.class)
public class KBMallPresenter extends BasePresenter<IKBMallView, KBMallModel> {

    @Override
    public void onAttach(IKBMallView view) {
        super.onAttach(view);
        register(EventKey.KB.RESULT_ACCOUNT_INFO, new AmountEvent());
        refreshAmount();
        refreshList();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void refreshList(){
        mModel.refreshList().subscribe(new SeekPObserver<List<KBGoodsBean>>(this) {
            @Override
            public void onSuccess(List<KBGoodsBean> beans) {
                final IKBMallView view = mView;
                if(view != null){
                    view.onRefreshListSuccess(beans);
                }
            }

            @Override
            protected void onError(int code, String message) {
                super.onError(code, message);
                final IKBMallView view = mView;
                if(view != null){
                    view.onRefreshListFail(message, code);
                }
            }
        });
    }

    public void loadMoreList(){
        mModel.loadMoreList().subscribe(new SeekPObserver<List<KBGoodsBean>>(this) {
            @Override
            public void onSuccess(List<KBGoodsBean> beans) {
                final IKBMallView view = mView;
                if(view != null){
                    view.onLoadMoreSuccess(beans);
                }
            }

            @Override
            protected void onError(int code, String message) {
                super.onError(code, message);
                final IKBMallView view = mView;
                if(view != null){
                    view.onLoadMoreFail(message, code);
                }
            }
        });
    }

    private void exchangeGoods(final KBGoodsBean bean){
        mModel.exchangeGoods(bean.getGoodsId()).subscribe(new SeekPObserver<Long>(this) {
            @Override
            public void onSuccess(Long amount) {
                KBMallPresenter.this.notify(amount, EventKey.KB.RESULT_ACCOUNT_INFO);
                bean.selfBuyOne();
                waringBuySuccess();
            }
        });
    }

    public void refreshAmount(){
        notify(0l, EventKey.KB.GET_ACCOUNT_INFO);
    }

    public void warningBuy(final KBGoodsBean bean) {
        final IKBMallView view = mView;
        if(view == null){
            return;
        }

        SpannableStringBuilder spanned = new SpannableStringBuilder();
        spanned.append("确认消耗 ");
        SpannableString s1 = new SpannableString(bean.getPrice() + "K");
        s1.setSpan(new ForegroundColorSpan(0xFF22B6FE), 0, s1.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spanned.append(s1);
        spanned.append(" 币兑换");
        spanned.append("" + bean.getName());
        spanned.append("？\r\n");
        String kb = view.getAmount() == 0 ? "0.00" : new DecimalFormat("####.00").format(view.getAmount());
        SpannableString s2 = new SpannableString("当前K币：" + kb);
        s2.setSpan(new ForegroundColorSpan(0xFF22B6FE), 0, s2.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        s2.setSpan(new AbsoluteSizeSpan(15, true), 0, s2.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        s2.setSpan(new LineHeightSpan() {
            @Override
            public void chooseHeight(CharSequence text, int start, int end, int spanstartv, int v, Paint.FontMetricsInt fm) {
                final int marginTop = DensityUtils.dip2px(view.getActivity(), 8);
                fm.ascent -= marginTop;
                fm.descent -= marginTop;
                fm.top += marginTop;
            }
        }, 0, s2.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spanned.append(s2);
        XWarningDialog.create(view.getActivity())
                .setTitleText("兑换")
                .setContent(spanned)
                .setLeftBtnText("联系客服")
                .setRightBtnText("确定")
                .setBtnClickListener(new XWarningDialog.BtnClickListener() {
                    @Override
                    public void onLeftBtnClick(XWarningDialog dialog) {
                        startCustomerPage();
                        dialog.dismiss();
                    }
                    @Override
                    public void onRightBtnClick(XWarningDialog dialog) {
                        exchangeGoods(bean);
                        dialog.dismiss();
                    }
                }).show();
    }

    private void waringBuySuccess(){
        final IKBMallView view = mView;
        if(view == null){
            return;
        }
        view.onGoodsChange();
        SpannableStringBuilder spanned = new SpannableStringBuilder();
        spanned.append("兑换成功！请联系客服，填写收货地址及相关信息\r\n");

        SpannableString s1 = new SpannableString("注意：不联系客服无法邮寄哦");
        s1.setSpan(new ForegroundColorSpan(0xFFFF6262), 0, s1.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        s1.setSpan(new AbsoluteSizeSpan(15, true), 0, s1.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        s1.setSpan(new LineHeightSpan() {
            @Override
            public void chooseHeight(CharSequence text, int start, int end, int spanstartv, int v, Paint.FontMetricsInt fm) {
                final int marginTop = DensityUtils.dip2px(view.getActivity(), 8);
                fm.ascent -= marginTop;
                fm.descent -= marginTop;
                fm.top += marginTop;
            }
        }, 0, s1.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spanned.append(s1);

        XWarningDialog.create(view.getActivity())
                .setContent(spanned)
                .setLeftBtnText("联系客服")
                .setRightBtnText("关闭")
                .setBtnClickListener(new XWarningDialog.BtnClickListener() {
                    @Override
                    public void onLeftBtnClick(XWarningDialog dialog) {
                        startCustomerPage();
                        dialog.dismiss();
                    }
                    @Override
                    public void onRightBtnClick(XWarningDialog dialog) {
                        dialog.dismiss();
                    }
                }).show();
    }

    public void startCustomerPage(){
        final IKBMallView view = mView;
        if(view == null){
            return;
        }
        XRouter.get().navigation(view.getActivity(), Intent.ACTION_VIEW, XRouterIntent.Msg.UriBuilder.build(XRouterIntent.Msg.ConversationType.PRIVATE, "" + UserHelp.System.CUSTOMER_USER_ID));
    }

    private class AmountEvent implements IBusEvent<Long> {
        @Override
        public void notifyEvent(final Long data, String key) {
            final IKBMallView view = mView;
            if(view != null){
                view.getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        view.onAmountChange(data);
                    }
                });
            }
        }
    }

}
