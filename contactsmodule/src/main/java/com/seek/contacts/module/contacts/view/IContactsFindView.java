package com.seek.contacts.module.contacts.view;

import com.seek.contacts.module.contacts.bean.ContactsInfoBean;
import com.xiu8.base.mvp.view.IBaseView;

/**
 * Created by yangxu on 2018/6/7.
 */

public interface IContactsFindView extends IBaseView {

    void onContactsList(ContactsInfoBean infoBean,boolean isLoadMore);

    void onInvitationSuccess(String msgContent);

    void onUploadStart();

    void onUploadFinish();

    void hideLoading();
}
