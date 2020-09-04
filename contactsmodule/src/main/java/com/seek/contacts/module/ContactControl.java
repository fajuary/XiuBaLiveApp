package com.seek.contacts.module;

import com.xiu8.base.mvp.view.IBaseView;

/**
 * Created by chunyang on 2018/6/11.
 */

public interface ContactControl {

    interface View extends IBaseView {

    }

    interface Presenter {

        void registerContacts();

        void unregisterContentObserver();

        void syncContact(boolean havePermission);

        void syncChangeList();

    }
}
