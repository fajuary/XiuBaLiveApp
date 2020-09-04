package com.seek.oos.module;

import com.http.RxHttp;
import com.http.model.HttpParams;
import com.seek.library.http.url.UrlConstants;
import com.seek.oos.module.bean.OssBean;
import com.seek.oos.module.bean.OssTs;
import com.xiu8.base.auth.AuthType;
import com.xiu8.base.mvp.model.BaseModel;

import io.reactivex.Observable;


/**
 * Created by chunyang on 2018/5/24.
 */

public class UploadModel extends BaseModel {


    public Observable<OssBean> getUploadConfig() {
        HttpParams params = new HttpParams();
        params.authType(AuthType.AUTH_LOGIN);
        return RxHttp.get(UrlConstants.UPLOAD_CONFIG).params(params).execute(OssBean.class);
    }

    public Observable<OssTs> getOssTS() {
        HttpParams params = new HttpParams();
        params.authType(AuthType.AUTH_LOGIN);
        return RxHttp.get(UrlConstants.OSS_TS).params(params).execute(OssTs.class);
    }

}
