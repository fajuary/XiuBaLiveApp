package com.seek.ann;

import android.util.Log;

import com.seek.ann.db.AnnColumn;
import com.seek.ann.db.AnnDB;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;

/**
 * Created by chunyang on 2018/7/31.
 */

public class AnnRetryFunc implements Function<Observable<? extends Throwable>, Observable<?>> {


    public static final int FIRST_RETRY_TIME = 10 * 1000;// 1000;//
    public static final int LAST_RETRY_TIME = 10 * 60 * 1000;//2000;//
    public static final int MAX_COUNT = 15;
    public static final int FIRST_COUNT = 10;

    private int retryCount;
    private Map<String, String> mParamsMap;
    private AnnDB mDBHelper;
    private long id;


    public AnnRetryFunc(AnnDB annDB, Map<String, String> paramsMap, long id, int count) {
        this.retryCount = count;
        this.mParamsMap = paramsMap;
        mDBHelper = annDB;
        this.id = id;
    }

//    public AnnRetryFunc() {
//        retryCount = 0;
//    }

    @Override
    public Observable<?> apply(Observable<? extends Throwable> observable) throws Exception {
        return observable.flatMap(new Function<Throwable, ObservableSource<?>>() {

            @Override
            public ObservableSource<?> apply(Throwable throwable) throws Exception {
//                if ((throwable instanceof ConnectException
//                        || throwable instanceof SocketTimeoutException
//                        || throwable instanceof SocketTimeoutException
//                        || throwable instanceof TimeoutException)) {//如果超出重试次数也抛出错误，否则默认是会进入onCompleted
                String jsonString = AnnUtils.map2String(mParamsMap);
                retryCount++;
                if (retryCount > 0 && retryCount <= AnnRetryFunc.FIRST_COUNT) {
                    if (id == -1) {
                        id = mDBHelper.insert(jsonString);
                    } else {
                        mDBHelper.update(id, jsonString, retryCount);
                    }
                    Log.e("AnnHelper", "第一种情况重试次数:" + retryCount);
                    return Observable.timer(AnnRetryFunc.FIRST_RETRY_TIME, TimeUnit.MILLISECONDS);
                } else if (retryCount > AnnRetryFunc.FIRST_COUNT && retryCount <= AnnRetryFunc.MAX_COUNT) {
                    mDBHelper.update(id, jsonString, retryCount);
                    Log.e("AnnHelper", "第二种情况重试次数:" + retryCount);
                    return Observable.timer(AnnRetryFunc.LAST_RETRY_TIME, TimeUnit.MILLISECONDS);
                } else {
                    Log.e("AnnHelper", "所有情况失败:" + retryCount);
                    mDBHelper.del(id);
                    return Observable.error(throwable);
                }
//                }
//                Log.e("AnnHelper", "直接失败:");
//                return Observable.error(throwable);
            }
        });
    }
}
