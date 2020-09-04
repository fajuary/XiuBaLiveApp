package com.seek.guide;

import com.seek.library.xrouter.XRouterIntent;
import com.xiu8.base.mvp.model.CreateModel;
import com.xiu8.base.mvp.presenter.BasePresenter;
import com.xiu8.base.router.XRouter;

/**
 * Created by Nice on 2018/6/15.
 */
@CreateModel(GuideModel.class)
public class GuidePresenter extends BasePresenter<GuideActivity, GuideModel> {

/*
    //检查登录状态．
    public void checkLoginInfo() {
        XBus.getInstance().notify("",EventKey.User.Login.checkState);
    }
*/

    public void onClick() {
        //TODO 进入登陆页面或者主页
        XRouter.get().to(XRouterIntent.Login.login.LOGIN_ACTIVITY).navigation(mView.getActivity());
        mView.finish();
    }
}
