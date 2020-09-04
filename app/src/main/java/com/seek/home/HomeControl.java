package com.seek.home;

import com.seek.contacts.module.ContactControl;

/**
 * Created by chunyang on 2018/6/12.
 */

public interface HomeControl {

    interface View extends ContactControl.View {

        void showMsgDot(int count);

        void hideMsgDot();

        void jumpRecommend();

    }

    interface Presenter {

    }
}
