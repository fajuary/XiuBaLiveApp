package com.seek.contacts.module.contacts.presenter;

import android.content.Context;

import com.seek.contacts.module.ContactModel;
import com.seek.contacts.module.contacts.bean.ContactsInfoBean;
import com.seek.contacts.module.contacts.model.ContactsFindModel;
import com.seek.contacts.module.contacts.view.IContactsFindView;
import com.seek.library.bean.Empty;
import com.seek.library.http.SeekCallBack;
import com.seek.library.http.SeekPObserver;
import com.seek.library.user.UserHelp;
import com.xiu8.base.mvp.model.CreateModel;
import com.xiu8.base.mvp.presenter.BasePresenter;

/**
 * Created by yangxu on 2018/6/7.
 */

@CreateModel(ContactsFindModel.class)
public class ContactsFindPresenter extends BasePresenter<IContactsFindView, ContactsFindModel> {

    ContactModel mContactModel;

    public void getContactsList(int pageIndex, String searchContent, final boolean isLoadMore) {
        mModel.getContactsList(pageIndex, searchContent).subscribe(new SeekPObserver<ContactsInfoBean>(this) {
            @Override
            public void onSuccess(ContactsInfoBean bean) {
                mView.onContactsList(bean,isLoadMore);
            }

            @Override
            protected void onError(int code, String message) {
               mView.hideLoading();
            }
        });

    }

    public void checkUpload(Context context,int pageIndex, String searchContent){
        initContacts(context);
        if (mContactModel.isContactSyncOver()) {
           getContactsList(pageIndex,searchContent,false);
        } else {
            mView.onUploadStart();
            mContactModel.syncContact(true).subscribe(new SeekPObserver<Empty>(this) {
                @Override
                public void onSuccess(Empty empty) {
                    mView.onUploadFinish();
                }
            });

        }
    }

    public void getInvitationContent() {
        UserHelp.getInstance().getDownloadUrl(2, new SeekCallBack<String>() {
            @Override
            public void onSuccess(String s) {
                mView.onInvitationSuccess(s);
            }
        });
    }


    public void initContacts(Context context) {
        if (mContactModel == null) {
            mContactModel = new ContactModel(context);
        }
    }

}
