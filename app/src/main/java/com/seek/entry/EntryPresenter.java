package com.seek.entry;

import com.cy.cache.XAppCache;
import com.seek.library.EventKey;
import com.seek.library.SeekBaseApplication;
import com.seek.library.Tag;
import com.seek.library.bean.UserBean;
import com.seek.library.http.SeekPObserver;
import com.seek.library.xrouter.XRouterIntent;
import com.xiu8.base.bus.IBusEvent;
import com.xiu8.base.bus.XBus;
import com.xiu8.base.mvp.model.CreateModel;
import com.xiu8.base.mvp.presenter.BasePresenter;
import com.xiu8.base.router.XRouter;
import com.xiu8.logger.library.Logger;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import io.reactivex.Observable;

/**
 * Created by zshh on 5/23/18.
 */
@CreateModel(EntryModel.class)
public class EntryPresenter extends BasePresenter<IEntryView,EntryModel> {
    private AtomicBoolean mGoToLoginPage = new AtomicBoolean(false);
    private XAppCache appCache ;
    public static final String TAG = "isLoginFirst";

    @Override
    public void onAttach(IEntryView view) {
        super.onAttach(view);
        register(EventKey.User.Login.success, new IBusEvent<UserBean>() {
            @Override
            public void notifyEvent(UserBean data, String key) {
                Logger.t(Tag.Login.TAG).i("EntryPresenter -> 接收到 EventKey.User.Login.success");
                mView.toHomeActivity();
            }
        }).register(EventKey.User.Login.toLoginPage, new IBusEvent<Object>() {
            @Override
            public void notifyEvent(Object data, String key) {
                Logger.t(Tag.Login.TAG).i("EntryPresenter ->　接收到 EventKey.User.Login.toLoginPage");
                if(mGoToLoginPage.compareAndSet(false,true)){
                    mView.toLoginActivity();
                }
            }
        });
        appCache = XAppCache.create(SeekBaseApplication.getInstance(),"isFirst");
    }

    //检查登录状态．
    public void checkLoginInfo() {
        Observable.timer(3, TimeUnit.SECONDS).subscribe(new SeekPObserver<Long>(this) {
            @Override
            public void onSuccess(Long aLong) {
                if(!appCache.getBooleanValue(TAG,false)){
                    appCache.putBoolean(TAG,true).apply();
                    XRouter.get().to(XRouterIntent.Guide.ACTIVITY).navigation(mView.getContext());
                    mView.destroy();
                }else{
                    XBus.getInstance().notify("",EventKey.User.Login.checkState);
                }
            }
        });

    }
}
