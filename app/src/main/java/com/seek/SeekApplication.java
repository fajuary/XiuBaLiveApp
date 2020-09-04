package com.seek;

import android.content.Context;
import android.content.Intent;
import android.support.multidex.MultiDex;

import com.base.channel.ChannelInfo;
import com.fm.openinstall.OpenInstall;
import com.networkbench.agent.impl.NBSAppAgent;
import com.seek.db.DBDao;
import com.seek.hotchat.bundle.chat.XChatModel;
import com.seek.hotchat.bundle.floatview.ActivityState4SmallestListener;
import com.seek.imlibrary.SeekIM;
import com.seek.library.EventKey;
import com.seek.library.SeekBaseApplication;
import com.seek.library.Utils.UnreadCountModel;
import com.seek.library.phonestate.PhoneStatusManager;
import com.seek.library.user.UserHelp;
import com.seek.msgbundle.MsgModule;
import com.seek.nbs.NBSActivityLifeCallbackListener;
import com.seek.oos.module.OssHelper;
import com.seek.prom.room.ui.bottom.chat.ChatManager;
import com.seek.user.module.XUserModel;
import com.seek.web.AdvanceLoadX5Service;
import com.xiu8.base.bus.IBusEvent;
import com.xiu8.base.bus.XBus;
import com.xiu8.base.util.ResourceUtils;
import com.xiu8.base.ximageloader.XImageLoader;
import com.xiu8.base.ximageloader.options.XImageOptions;
import com.xiu8.nice.userbundle.user.IUserBundle;

/**
 * Created by wangjian on 2018/5/14.
 */

public class SeekApplication extends SeekBaseApplication {


    @Override
    public void onAppInit() {
        DBDao.init(this);
        XImageLoader.getInstance().init(this,
                new XImageOptions.Builder()
                        .placeholder(R.drawable.seek_placeholder)
                        .thumbnail(0.1f)
                        .setMemoryCache(true)
                        .diskCacheStrategy(XImageOptions.STRATEGY_ALL)
                        .error(R.drawable.seek_placeholder)
                        .build());

        moduleInit();
    }


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    protected void onApplicationCreate() {
        registerActivityLifecycleCallbacks(new ActivityState4SmallestListener());
        //预加载x5内核
        startService(new Intent(this, AdvanceLoadX5Service.class));
        initNBS();
    }

    private void initNBS() {
        this.registerActivityLifecycleCallbacks(new NBSActivityLifeCallbackListener());
        //听云初始化
        NBSAppAgent.setLicenseKey(ResourceUtils.getString(this, R.string.appKey_nbs)).enableLogging(true).withLocationServiceEnabled(true).startInApplication(this.getApplicationContext());
        //设置渠道信息
        NBSAppAgent.setUserCrashMessage("ChannelPidSid", ChannelInfo.pid() + "_" + ChannelInfo.sid());
        XBus.getInstance().register(EventKey.User.Login.success, new IBusEvent() {
            @Override
            public void notifyEvent(Object data, String key) {
                NBSAppAgent.setUserIdentifier(UserHelp.getInstance().getOpenId());
            }
        });
    }

    private void moduleInit() {
        //初始化话基础模块.
        XUserModel.init(this);
        XChatModel.getInstance().init();
        PhoneStatusManager.instance().init();
        //融云初始化
        SeekIM.init(this);
        OssHelper.init(this);
        ChatManager.init();
        if (isMainProcess()) {
            MsgModule.init();
            OpenInstall.init(this);
            IUserBundle userBundle = new IUserBundle();
            userBundle.init();
            UnreadCountModel.init();
        }

    }

    /**
     * 退出应用，非退出登录
     */
    @Override
    public void exit() {
        SeekIM.instance().disconnect();
        super.exit();
    }
}
