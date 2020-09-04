package com.seek.home;

import android.content.Context;

import com.seek.contacts.module.ContactPresenter;
import com.seek.library.EventKey;
import com.seek.library.Utils.UnreadCountModel;
import com.xiu8.base.bus.IBusEvent;

/**
 * Created by chunyang on 2018/6/12.
 */

public class HomePresenter extends ContactPresenter<HomeControl.View> implements HomeControl.Presenter, UnreadCountModel.UnreadCountListener, IBusEvent<Object> {


    public HomePresenter(Context context) {
        super(context);
    }

    @Override
    public void onAttach(HomeControl.View view) {
        super.onAttach(view);
        register(EventKey.KB.JUMP_HOME_RECOMMEND, this);
        UnreadCountModel.instance().addUnreadCountListener(this);
    }

    @Override
    public void unreadCountChange(int friendApplyUnreadCount, int messageUnreadCount) {
        if (!isViewAttached()) return;
        int count = friendApplyUnreadCount + messageUnreadCount;
        if (count > 0) {
            //show 小红点
            mView.showMsgDot(count);
        } else {
            //hide 小红点
            mView.hideMsgDot();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        UnreadCountModel.instance().removeUnreadCountListener(this);
    }

    @Override
    public void notifyEvent(Object data, String key) {
        if (!isViewAttached()) return;
        if (EventKey.KB.JUMP_HOME_RECOMMEND.equals(key)) {
            mView.jumpRecommend();
        }
    }
}
