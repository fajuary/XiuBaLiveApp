package com.seek.entry;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.IntentService;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.seek.R;
import com.seek.ann.AnnApp;
import com.seek.ann.AnnHelper;
import com.seek.library.SeekBaseApplication;
import com.seek.library.Tag;
import com.seek.library.activity.SeekBaseMVPActivity;
import com.seek.library.xrouter.XRouterIntent;
import com.seek.user.module.bean.OpenInstallBean;
import com.xiu8.base.analysis.json.JsonUtils;
import com.xiu8.base.mvp.presenter.CreatePresenter;
import com.xiu8.base.router.XRouter;
import com.xiu8.logger.library.Logger;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * Created by wangjian on 2018/5/14.
 * 登录和注册页面
 */
@CreatePresenter(EntryPresenter.class)
public class EntryActivity extends SeekBaseMVPActivity<EntryPresenter> implements IEntryView {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.splash);
        AnnHelper.getInstance().onLostAnnMsg();
        AnnHelper.getInstance().onAnnMsg(AnnApp.PT, AnnApp.APP_CREATE);

    }

    @Override
    public void onPresenterFinished() {
        super.onPresenterFinished();
        Logger.t(Tag.Login.TAG).i("EntryActivity -> checkLoginInfo()");
            mPresenter.checkLoginInfo();
    }
    Disposable subscribe;
    @Override
    public void toLoginActivity() {
         subscribe = Observable.interval(0, 2, TimeUnit.SECONDS).subscribe(new Consumer<Long>() {
            @Override
            public void accept(Long aLong) throws Exception {
                if (isConn(getContext())) {
                    if(subscribe!=null && !subscribe.isDisposed()){
                        subscribe.dispose();
                    }
                    Logger.t(Tag.Login.TAG).i("EntryActivity -> toLoginActivity");
                    XRouter.get().to(XRouterIntent.Login.login.LOGIN_ACTIVITY).navigation(EntryActivity.this);
                    finish();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!isConn(getContext())){
            showNoNetWorkDlg(getContext());
        }
    }

    @Override
    public Activity getContext() {
        return this;
    }

    @Override
    public void destroy() {
        if(subscribe!=null && !subscribe.isDisposed()){
            subscribe.dispose();
        }
        finish();
    }

    @Override
    public void toHomeActivity() {
        Logger.t(Tag.Login.TAG).i("EntryActivity-> toHomeActivity");
        XRouter.get().to(XRouterIntent.Home.ACTIVITY).navigation(this);
        final String data = getIntent().getStringExtra(XRouterIntent.OpenInstall.SCHEMA.Key);
        if (data != null) {
            final OpenInstallBean openInstallBean = JsonUtils.json2Obj(data, OpenInstallBean.class);
            if (openInstallBean.getU() > 0) {
                Observable.timer(500, TimeUnit.MILLISECONDS).subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        Bundle homePageBundle = new Bundle();
                        homePageBundle.putSerializable("UserId", openInstallBean.getU());
                        Activity currentActivity = SeekBaseApplication.getInstance().getActivityManager().getCurrentActivity();
                        if (currentActivity != null) {
                            XRouter.get().to(XRouterIntent.User.HOME_PAGE_INTENT).bundle(homePageBundle).navigation(currentActivity);
                        }
                    }
                });
            }
        }
        finish();
    }

    /**
     * 判断网络连接是否已开
     * true 已打开  false 未打开
     * */
    public static boolean isConn(Context context) {
        boolean bisConnFlag = false;
        ConnectivityManager conManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo network = conManager.getActiveNetworkInfo();
        if (network != null) {
            bisConnFlag = conManager.getActiveNetworkInfo().isAvailable();
        }
        return bisConnFlag;
    }

    /**
     * 当判断当前手机没有网络时选择是否打开网络设置
     * @param context
     */
    public void showNoNetWorkDlg(final Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setIcon(R.drawable.ic_launcher)
                .setTitle(R.string.app_name)
                .setMessage("当前无网络").setPositiveButton("设置", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 跳转到系统的网络设置界面
                Intent intent;
                // 先判断当前系统版本
                if (android.os.Build.VERSION.SDK_INT > 10) {  // 3.0以上
                    intent = new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS);
                } else {
                    intent = new Intent();
                    intent.setClassName("com.android.settings", "com.android.settings.WirelessSettings");
                }
                context.startActivity(intent);
            }
        }).setNegativeButton("我知道了", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                EntryActivity.this.finish();
            }
        }).setCancelable(false).show();
    }

}
