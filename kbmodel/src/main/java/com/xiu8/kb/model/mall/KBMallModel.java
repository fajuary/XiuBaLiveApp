package com.xiu8.kb.model.mall;

import com.google.gson.JsonObject;
import com.http.RxHttp;
import com.http.model.HttpParams;
import com.seek.library.http.url.UrlConstants;
import com.xiu8.base.analysis.json.JsonUtils;
import com.xiu8.base.auth.AuthType;
import com.xiu8.base.mvp.model.BaseModel;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.functions.Function;

/**
 * Created by guojiel on 2018/8/29.
 */

public class KBMallModel extends BaseModel {

    private int mPageIndex = 1;

    public Observable<List<KBGoodsBean>> refreshList() {
        return reqList(1);
    }

    public Observable<List<KBGoodsBean>> loadMoreList() {
        return reqList(mPageIndex);
    }

    private Observable<List<KBGoodsBean>> reqList(final int pageIndex){
        HttpParams params = new HttpParams();
        params.put("pageIndex", "" + pageIndex);
        params.authType(AuthType.AUTH_LOGIN);
        return RxHttp.get(UrlConstants.KB_MALL_LIST).params(params).execute(JsonObject.class).map(new Function<JsonObject, List<KBGoodsBean>>() {
            @Override
            public List<KBGoodsBean> apply(JsonObject jsonObject) throws Exception {
                List<KBGoodsBean> list = JsonUtils.json2List(JsonUtils.getString(jsonObject.toString(), "list"), KBGoodsBean[].class);
                mPageIndex = pageIndex + 1;
                return list;
            }
        });
    }

    public Observable<Long> exchangeGoods(long goodsId) {
        HttpParams params = new HttpParams();
        params.put("goodsId", "" + goodsId);
        params.authType(AuthType.AUTH_LOGIN);
        return RxHttp.get(UrlConstants.KB_MALL_BUY).params(params).execute(JsonObject.class).map(new Function<JsonObject, Long>() {
            @Override
            public Long apply(JsonObject jsonObject) throws Exception {
                long amount = JsonUtils.getLong(jsonObject.toString(), "amount");
                return amount;
            }
        });
    }
}
