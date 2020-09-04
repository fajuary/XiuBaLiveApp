package com.xiu8.kb.model.account;

import com.cy.cache.XAppCache;
import com.http.RxHttp;
import com.http.model.HttpParams;
import com.seek.library.EventKey;
import com.seek.library.SeekBaseApplication;
import com.seek.library.http.SeekErrorConsumer;
import com.seek.library.http.url.UrlConstants;
import com.xiu8.base.auth.AuthType;
import com.xiu8.base.bus.IBusEvent;
import com.xiu8.base.mvp.model.BaseModel;
import com.xiu8.logger.library.Logger;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

/**
 * Created by chunyang on 2018/8/29.
 */

public class KBAccountModel extends BaseModel {

    /**
     * 常量配置
     */
    private final static String TAG = "KBAccountModel";

    private final static int DEFAULT_TIME = 10;
    private final static String CACHE_NAME = "KB_Account";
    private final static String KEY_ACCOUNT = "key_account";


    /**
     * 私有属性
     */
    private Disposable mAccountTask;
    private AccountBusEvent mAccountInfo;
    private XAppCache mXAppCache;

    private static final class Holder {
        private static final KBAccountModel IN = new KBAccountModel();
    }

    private KBAccountModel() {
        Logger.t(TAG).w("开始初始化");
        mXAppCache = XAppCache.create(SeekBaseApplication.getInstance(), CACHE_NAME);
        mAccountInfo = new AccountBusEvent();
        register(EventKey.KB.GET_ACCOUNT_INFO, mAccountInfo);
    }

    private static KBAccountModel get() {
        return Holder.IN;
    }

    public static void init() {
        get().getCacheAmount().getSeekAccountInfo(0, true);
    }

    private class AccountBusEvent implements IBusEvent<Long> {

        @Override
        public void notifyEvent(Long data, String key) {
            getSeekAccountInfo(0, true);
        }
    }

    /**
     * 轮询查询接口
     */
    private void getSeekAccountInfo(long time, boolean refresh) {
        final boolean isRefresh = refresh;
        cleanTask();
        mAccountTask = Observable.timer(time, TimeUnit.SECONDS).flatMap(new Function<Long, ObservableSource<AccountInfoBean>>() {
            @Override
            public ObservableSource<AccountInfoBean> apply(Long aLong) throws Exception {
                return getAccountInfo();
            }
        }).subscribe(new Consumer<AccountInfoBean>() {
            @Override
            public void accept(AccountInfoBean bean) throws Exception {
                long amount = bean.getAmount();
                int time = bean.getTime();
                setAmount(amount, isRefresh);
                cleanTask();
                getSeekAccountInfo(time, false);//下次轮询请求，不在强制刷新
            }
        }, new SeekErrorConsumer() {
            @Override
            public void onError(int code, String message) {
                if (isRefresh)
                    notifyAmount(amount);
                cleanTask();
                getSeekAccountInfo(DEFAULT_TIME, false);//错误后，不在强制刷新，前面已经发送缓存数据，因此不在强制刷新
            }
        });
    }

    private void cleanTask() {
        if (mAccountTask != null && !mAccountTask.isDisposed()) {
            mAccountTask.dispose();
        }
        mAccountTask = null;
    }

    private long amount;

    private void setAmount(long amount, boolean refresh) {
        if (refresh) {
            notifyAmount(amount);
            if (this.amount != amount) {
                this.amount = amount;
                mXAppCache.putLong(KEY_ACCOUNT, amount).apply();
            }
            Logger.t(TAG).w("强制刷新 K 币金额:" + amount);
        } else if (this.amount != amount) {
            this.amount = amount;
            notifyAmount(amount);
            mXAppCache.putLong(KEY_ACCOUNT, amount).apply();
            Logger.t(TAG).w("获取 K 币金额:" + amount);
        }
    }

    private void notifyAmount(long amount) {
        notify(amount, EventKey.KB.RESULT_ACCOUNT_INFO);
    }

    private KBAccountModel getCacheAmount() {
        amount = mXAppCache.getLongValue(KEY_ACCOUNT, 0l);
        Logger.t(TAG).w("CacheAmount:" + amount);
        return this;
    }

    public long getAmount() {
        return amount;
    }

    public Observable<AccountInfoBean> getAccountInfo() {
        HttpParams params = new HttpParams();
        params.authType(AuthType.AUTH_LOGIN);
        return RxHttp.get(UrlConstants.KB_INFO).params(params).execute(AccountInfoBean.class);
    }

}
