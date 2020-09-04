package com.seek.home;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import com.seek.R;
import com.seek.ann.AnnHelper;
import com.seek.home.view.HomeBottomView;
import com.seek.home.view.HomeToolBar;
import com.seek.hotchat.AnnReport;
import com.seek.hotchat.bundle.main.view.HotChatHomeFragment;
import com.seek.hotchat.bundle.main.view.component.HotChatTitleView;
import com.seek.library.SeekBaseApplication;
import com.seek.library.activity.SeekBaseActivity;
import com.seek.library.widget.switchview.SwitchButton;
import com.seek.library.widget.toast.Toast;
import com.seek.library.xrouter.XRouterIntent;
import com.seek.msgbundle.ann.AnnMsg;
import com.seek.msgbundle.ann.MsgAnnHelper;
import com.seek.msgbundle.main.view.widget.MsgHomeBar;
import com.seek.recommend.bundle.widget.FindToolBar;
import com.seek.updatebundle.UpdateHelp;
import com.seek.user.module.bean.OpenInstallBean;
import com.xiu8.base.analysis.json.JsonUtils;
import com.xiu8.base.router.XRouter;
import com.xiu8.permission.PermissionListener;
import com.xiu8.permission.XPermission;
import com.yanzhenjie.permission.AndPermission;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;

public class HomeActivity extends SeekBaseActivity implements HomeControl.View, View.OnClickListener, ViewPager.OnPageChangeListener, HomeBottomView.OnBottomChangeListener, HotChatHomeFragment.OnHotChatToolBarListener {

    private final static String KEY_TYPE = "keyShowType";

    private HomeToolBar mHomeToolBar;
    private ViewPager mViewPager;
    private HomePagerAdapter mPagerAdapter;
    private List<Fragment> mFragments;
    private long mFirstClickTime = 0;
    private FindToolBar mFindToolBar;
    private HotChatTitleView mHotChatTitleView;
    private HomeBottomView mHomeBottomView;
    private MsgHomeBar mMsgHomeBar;

    private HomePresenter mHomePresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHomePresenter = new HomePresenter(this);
        mHomePresenter.onAttach(this);

        setContentView(R.layout.activity_home);
        registerReceiver(mStartAppBroadCast, new IntentFilter(XRouterIntent.OpenInstall.SCHEMA.HOME));

        AndPermission.with(this).runtime().permission(Manifest.permission.RECORD_AUDIO).start();
        if (savedInstanceState != null) {

        }
        initContacts();
        checkFromRong(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        checkFromRong(intent);
        checkFromTask(intent);
    }

    @Override
    public void initViews() {
        mHomeToolBar = findView(R.id.home_tool_bar);
        //
        mFindToolBar = new FindToolBar(this);
        mFindToolBar.getMeIcon().setOnClickListener(this);
        mHomeToolBar.addView(mFindToolBar);
        //
        mHotChatTitleView = new HotChatTitleView(this);
        mHotChatTitleView.getMeIcon().setOnClickListener(this);
        mHomeToolBar.addView(mHotChatTitleView);
        //
        mMsgHomeBar = new MsgHomeBar(this);
        mMsgHomeBar.getUser().setOnClickListener(this);
        mMsgHomeBar.getSearch().setOnClickListener(this);
        mMsgHomeBar.getAddChat().setOnClickListener(this);
        mHomeToolBar.addView(mMsgHomeBar);
        //
        mViewPager = findView(R.id.home_view_pager);
        mViewPager.addOnPageChangeListener(this);
        //
        mHomeBottomView = findView(R.id.home_bottom_container);
        mHomeBottomView.setOnBottomChangeListener(this);
    }

    @Override
    public void initData() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        mFragments = new ArrayList<>();
        mFragments.add(HomeFragment.newFragment(0));
        mFragments.add(HomeFragment.newFragment(1));
        mFragments.add(HomeFragment.newFragment(2));

        mPagerAdapter = new HomePagerAdapter(fragmentManager, mFragments);
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.setCurrentItem(1);


    }

    private void checkUpdate() {
        //检查更新
        mViewPager.postDelayed(new Runnable() {
            @Override
            public void run() {
                UpdateHelp.getInstance().checkUpdate(HomeActivity.this);
            }
        }, 3000);
    }

    private void checkFromTask(Intent intent) {
        if (intent == null) {
            return;
        }
        if (intent.hasExtra(XRouterIntent.Home.Key.RecommendIndex)) {
            mViewPager.setCurrentItem(0);
        }
    }

    private void checkFromRong(Intent intent) {
        if (intent == null) {
            return;
        }
        //TODO 添加匹配关闭页面数据跳转
        if (intent.hasExtra(KEY_TYPE)) {
            int current = intent.getIntExtra(KEY_TYPE, 1);
            mViewPager.setCurrentItem(current, false);
        }

        Uri data = intent.getData();
        if (data == null) {
            return;
        }
        String scheme = data.getScheme();
        if ("rong".equals(scheme)) {
            mViewPager.setCurrentItem(2, false);
        }
    }

    private void initContacts() {
//        if (!mHomePresenter.isRequestPermission()) {
        XPermission.requestPermission(this, new PermissionListener() {
            @Override
            public void permissionGranted(@NonNull String[] permission) {
                checkUpdate();
                mHomePresenter.syncContact(true);
                mHomePresenter.registerContacts();
            }

            @Override
            public void permissionDenied(@NonNull String[] permission) {
                checkUpdate();
                mHomePresenter.syncContact(false);
            }
        }, Manifest.permission.READ_CONTACTS);
//        }

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
//        Logger.d("HomeFragment onLazyResume " + position);
        mHomeBottomView.toggle(position);
        if (position == 0) {
            changeFindView();
        } else if (position == 1) {
            changeHotView();
        } else if (position == 2) {
            changeMessageView();
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    private void changeFindView() {
        mHotChatTitleView.setVisibility(View.GONE);
        mFindToolBar.setVisibility(View.VISIBLE);
        mMsgHomeBar.setVisibility(View.GONE);
    }

    private void changeHotView() {
        mHotChatTitleView.setVisibility(View.VISIBLE);
        mFindToolBar.setVisibility(View.GONE);
        mMsgHomeBar.setVisibility(View.GONE);
    }

    private void changeMessageView() {
        mHotChatTitleView.setVisibility(View.GONE);
        mFindToolBar.setVisibility(View.GONE);
        mMsgHomeBar.setVisibility(View.VISIBLE);
    }

    private void startUserPersonal() {
        XRouter.get().to(XRouterIntent.User.PERSONAL_CENTER_INTENT).navigation(this);
    }


    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() - mFirstClickTime > 800) {
            Toast.showToast("再点击一下退出");
            mFirstClickTime = System.currentTimeMillis();
        } else {
            super.onBackPressed();
            SeekBaseApplication.getInstance().exit();
        }
    }

    @Override
    public SwitchButton getSwitchButton() {
        return mHotChatTitleView.getMatchingSwitch();
    }

    @Override
    public TextView getKbBalanceTv() {
        return mHotChatTitleView.getKbBalance();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == mFindToolBar.getMeIconId() || id == mHotChatTitleView.getMeIconId() || id == mMsgHomeBar.getUserId()) {
            startUserPersonal();
            tjMeIconClick(id);

        } else if (id == mMsgHomeBar.getSearchId()) {
            XRouter.get().to(XRouterIntent.Search.SEARCH_INTENT).navigation(this);
            MsgAnnHelper.tj(AnnMsg.EN_SEARCH_ICON);
        } else if (id == mMsgHomeBar.getAddChatId()) {
            XRouter.get().to(XRouterIntent.Friends.FriendsList).navigation(this);
            MsgAnnHelper.tj(AnnMsg.EN_NEW_CHAT_ICON);
        }
    }

    private void tjMeIconClick(int id) {
        if (id == mFindToolBar.getMeIconId()) {
            AnnHelper.getInstance().onAnnMsg("25000", "25001");
//            AnnHelper.getInstance().onAnnMsg(AnnReport.HotChat.Pt, AnnReport.HotChat.MineIcon);
        } else if (id == mHotChatTitleView.getMeIconId()) {
            AnnHelper.getInstance().onAnnMsg(AnnReport.HotChat.Pt, AnnReport.HotChat.MineIcon);
        } else if (id == mMsgHomeBar.getUserId()) {
            MsgAnnHelper.tj(AnnMsg.EN_ME_ICON);
        }
    }

    @Override
    public void onChange(int position) {
        if (mViewPager.getCurrentItem() != position) {
            mViewPager.setCurrentItem(position, false);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHomePresenter.unregisterContentObserver();
        unregisterReceiver(mStartAppBroadCast);
        mHomePresenter.onDetach();
    }

    //跳转到个人中心.
    private BroadcastReceiver mStartAppBroadCast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, final Intent intent) {
            android.app.ActivityManager activityManager = (android.app.ActivityManager) getSystemService(ACTIVITY_SERVICE);
            try {
                activityManager.moveTaskToFront(getTaskId(), android.app.ActivityManager.MOVE_TASK_WITH_HOME);

                String data = intent.getStringExtra(XRouterIntent.OpenInstall.SCHEMA.Key);
                if (data != null) {
                    final OpenInstallBean openInstallBean = JsonUtils.json2Obj(data, OpenInstallBean.class);
                    if (openInstallBean.getU() > 0) {
                        Observable.timer(500, TimeUnit.MILLISECONDS).subscribe(new Consumer<Long>() {
                            @Override
                            public void accept(Long aLong) throws Exception {
                                Bundle homePageBundle = new Bundle();
                                homePageBundle.putSerializable("UserId", openInstallBean.getU());
                                XRouter.get().to(XRouterIntent.User.HOME_PAGE_INTENT).bundle(homePageBundle).navigation(HomeActivity.this);
                            }
                        });
                    }
                }
            } catch (Exception e) {
            }
        }
    };

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

    }

    @Override
    public void showMsgDot(int count) {
        mHomeBottomView.setBadge(2, "");
    }

    @Override
    public void hideMsgDot() {
        mHomeBottomView.setBadge(2, null);
    }

    @Override
    public void jumpRecommend() {//自己跳转自己也可以
        Intent intent = new Intent(this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(XRouterIntent.Home.Key.RecommendIndex, 1);
        startActivity(intent);
    }

}
