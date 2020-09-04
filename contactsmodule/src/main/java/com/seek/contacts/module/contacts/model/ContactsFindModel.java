package com.seek.contacts.module.contacts.model;

import com.http.RxHttp;
import com.http.model.HttpParams;
import com.seek.contacts.module.contacts.bean.ContactsInfoBean;
import com.seek.library.http.url.UrlConstants;
import com.xiu8.base.auth.AuthType;
import com.xiu8.base.mvp.model.BaseModel;

import io.reactivex.Observable;

/**
 * Created by yangxu on 2018/6/7.
 */

public class ContactsFindModel extends BaseModel {

    public Observable<ContactsInfoBean> getContactsList(int pageIndex,String content) {
        HttpParams params = new HttpParams();
        params.put("pageIndex", pageIndex + "")
                .put("content",content).authType(AuthType.AUTH_LOGIN);
        return RxHttp.get(UrlConstants.CONTACTS_FRIENDS_LIST).params(params).execute(ContactsInfoBean.class);
    }

}
