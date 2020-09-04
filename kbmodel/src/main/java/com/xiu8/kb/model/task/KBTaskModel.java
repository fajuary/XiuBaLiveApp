package com.xiu8.kb.model.task;

import com.google.gson.JsonObject;
import com.http.RxHttp;
import com.http.model.HttpParams;
import com.seek.library.bean.Empty;
import com.seek.library.bean.ShareUrlBean;
import com.seek.library.http.SeekCallBack;
import com.seek.library.http.url.UrlConstants;
import com.seek.library.user.UserHelp;
import com.xiu8.base.analysis.json.JsonUtils;
import com.xiu8.base.auth.AuthType;
import com.xiu8.base.mvp.model.BaseModel;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.functions.Function;


/**
 * Created by chunyang on 2018/8/29.
 */

public class KBTaskModel extends BaseModel {

    public Observable<List<KBTaskBean>> getTaskList() {
        HttpParams params = new HttpParams();
        params.authType(AuthType.AUTH_LOGIN);
        return RxHttp.get(UrlConstants.KB_TASK_LIST).params(params).execute(JsonObject.class).map(new Function<JsonObject, List<KBTaskBean>>() {
            @Override
            public List<KBTaskBean> apply(JsonObject jsonObject) throws Exception {
                return JsonUtils.json2List(JsonUtils.getString(jsonObject.toString(), "list"), KBTaskBean[].class);
            }
        });
    }


    public Observable<Empty> finishTask(int taskId) {
        HttpParams params = new HttpParams();
        params.put("taskId", String.valueOf(taskId));
        params.authType(AuthType.AUTH_LOGIN);
        return RxHttp.get(UrlConstants.KB_TASK_FINISH).params(params).execute(Empty.class);
    }

    /**
     * @param type         [1:微信,2:QQ,3:短信]
     * @param seekCallBack 会调方法
     */
    public void getDownLoadUrl(int type, SeekCallBack<String> seekCallBack) {
        UserHelp.getInstance().getDownloadUrl(type, seekCallBack);
    }

    public void getShareTaskUrl(int type, SeekCallBack<ShareUrlBean> seekCallBack) {
        UserHelp.getInstance().getShareTaskUrl(type, seekCallBack);
    }
}
