package com.xiu8.kb.model.account.detail;

import com.http.RxHttp;
import com.http.model.HttpParams;
import com.seek.library.http.url.UrlConstants;
import com.xiu8.base.auth.AuthType;
import com.xiu8.base.mvp.model.BaseModel;

import io.reactivex.Observable;

/**
 * Created by zhangpengfei on 2018/8/29.
 * k币model层
 */

public class KBSeekDetailModel extends BaseModel{
    public Observable<SeekDetailBean> getKBList(int  pageIndex) {

        HttpParams params=new HttpParams();
        params.put("pageIndex",String.valueOf(pageIndex));
        //登录授权参数
        params.authType(AuthType.AUTH_LOGIN);
        return RxHttp.get(UrlConstants.KB_DETAIL_LIST)
                .params(params)
                .execute(SeekDetailBean.class);

    }


}
