package com.seek.ann;

import android.content.Context;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;

import com.http.RxHttp;
import com.http.model.HttpParams;
import com.http.request.CustomRequest;
import com.http.utils.RxUtil;
import com.seek.ann.db.AnnColumn;
import com.seek.ann.db.AnnDB;

import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Scheduler;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

/**
 * Created by chunyang on 2018/6/25.
 */

public class AnnHelper {

    private final static String TAG = "AnnHelper";

    public static final int ENVIRONMENT_NORMAL = 1;
    public static final int ENVIRONMENT_TEST = 2;
    public static final int ENVIRONMENT_1_1 = 4;

    private final static String KEY = "H7RG&bxM11xp";
    private final static String NORMAL = "https://report.2cq.com";
    private static final String DEV = "https://dev_report.2cq.com";
    private static final String DEBUG_REPORT = "https://debug_report.2cq.com";
    private static Context sContext;
    private static int sWidth;
    private static int sHeight;
    private static int sDensityDpi;
    private static AnnCallback sAnnCallback;
    private static String HOST = NORMAL;

    private AnnDB mDBHelper;

    private static final class Holder {
        private static final AnnHelper IN = new AnnHelper();
    }

    public static AnnHelper getInstance() {
        return Holder.IN;
    }

    private AnnHelper() {
        mDBHelper = new AnnDB(sContext);
    }

    public static void init(Context context, int environment, AnnCallback annCallback) {
        if (annCallback == null) throw new RuntimeException("未初始化必要信息 annCallback");
        sAnnCallback = annCallback;
        sContext = context;
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        sWidth = metrics.widthPixels;
        sHeight = metrics.heightPixels;
        sDensityDpi = metrics.densityDpi;
        if (environment == ENVIRONMENT_NORMAL) {
            HOST = NORMAL;
        } else if (environment == ENVIRONMENT_TEST) {
            HOST = DEV;
        } else if (environment == ENVIRONMENT_1_1) {
            HOST = DEBUG_REPORT;
        } else {
            HOST = DEV;
        }
    }

    public static void init(Context context, AnnCallback annCallback) {
        if (annCallback == null) throw new RuntimeException("未初始化必要信息 annCallback");
        sAnnCallback = annCallback;
        sContext = context;
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        sWidth = metrics.widthPixels;
        sHeight = metrics.heightPixels;
        sDensityDpi = metrics.densityDpi;
    }

    public static void setHostType(int environment) {
        if (environment == ENVIRONMENT_NORMAL) {
            HOST = NORMAL;
        } else if (environment == ENVIRONMENT_TEST) {
            HOST = DEV;
        } else if (environment == ENVIRONMENT_1_1) {
            HOST = DEBUG_REPORT;
        } else {
            HOST = DEV;
        }
    }


    public void onAnnMsg(String pt, String en) {
        onAnnMsg(pt, en, null);
    }


    public void onAnnMsg(String pt, String en, JSONObject msg) {
        HttpParams params = new HttpParams();
        params.put(getPublicParams());
        params.put("pt", pt);
        params.put("en", en);
        if (msg != null)
            params.put("msg", msg.toString());

        onAnn(params);
    }


    public void onAnnMsgMap(String pt, String en, Map<String, String> map) {
        HttpParams params = new HttpParams();
        params.put(getPublicParams());
        params.put("pt", pt);
        params.put("en", en);
        if (map != null && !map.isEmpty()) {
            params.put("msg", AnnUtils.map2String(map));
        }
        onAnn(params);
    }

    public void onLostAnnMsg() {

        Observable.fromIterable(mDBHelper.getLostAnnMsg()).flatMap(new Function<AnnColumn, ObservableSource<Long>>() {
            @Override
            public ObservableSource<Long> apply(AnnColumn annColumn) throws Exception {

                long nextRetryTime = annColumn.getNextRetryTime();
                long currentTime = System.currentTimeMillis();
                long time = nextRetryTime - currentTime;//差值
                final int count = annColumn.getRetryCount();//重试次数
                final long id = annColumn.getId();

                long nextTime = 0;
                if (time > 0) {
                    nextTime = time;
                } else {
                    if (count > 0 && count <= AnnRetryFunc.FIRST_COUNT) {
                        nextTime = AnnRetryFunc.FIRST_RETRY_TIME;
                    } else if (count > AnnRetryFunc.FIRST_COUNT && count <= AnnRetryFunc.MAX_COUNT) {
                        nextTime = AnnRetryFunc.LAST_RETRY_TIME;
                    }
                }

                Log.d(TAG, id + " nextTime: " + nextTime);

                if (nextTime == 0) {
                    return Observable.just(id);
                } else {
                    String msg = annColumn.getMsg();
                    final Map<String, String> map = AnnUtils.string2Map(msg);
                    HttpParams params = new HttpParams();
                    params.put(map);
                    final CustomRequest request = RxHttp.custom().params(params).baseUrl(HOST).build();
                    return Observable.timer(nextTime, TimeUnit.MILLISECONDS).flatMap(new Function<Long, ObservableSource<Long>>() {
                        @Override
                        public ObservableSource<Long> apply(Long aLong) throws Exception {
                            return annRetrySaveService(request, count, id).map(new Function<String, Long>() {
                                @Override
                                public Long apply(String s) throws Exception {
                                    return id;
                                }
                            }).onErrorReturn(new Function<Throwable, Long>() {
                                @Override
                                public Long apply(Throwable throwable) throws Exception {
                                    return -1l;
                                }
                            });
                        }
                    });
                }
            }
        }).subscribeOn(Schedulers.io()).unsubscribeOn(Schedulers.io()).subscribe(new Consumer<Long>() {
            @Override
            public void accept(Long id) throws Exception {
                if (id != -1) {
                    mDBHelper.del(id);
                }
                Log.d(TAG, "onLostAnn 成功 " + id);
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                Log.d(TAG, "onLostAnn 失败");
            }
        });
    }


    private Observable<String> annService(CustomRequest request) {
        AnnService annService = request.create(AnnService.class);
        final LinkedHashMap<String, String> urlParamsMap = request.getParams().urlParamsMap;
        final Observable<ResponseBody> observable = request.call(annService.ann(urlParamsMap));
        return observable.map(new Function<ResponseBody, String>() {
            @Override
            public String apply(ResponseBody responseBody) throws Exception {
                return responseBody.string();
            }
        });
    }

    private Observable<String> annRetrySaveService(CustomRequest request, int count, long id) {
        final LinkedHashMap<String, String> urlParamsMap = request.getParams().urlParamsMap;
        return annService(request).retryWhen(new AnnRetryFunc(mDBHelper, urlParamsMap, id, count));
    }

    private void onAnn(final HttpParams params) {
        final CustomRequest request = RxHttp.custom().params(params).baseUrl(HOST).build();
        annRetrySaveService(request, 0, -1).compose(RxUtil.<String>io2ui()).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                Log.d(TAG, "成功:  " + s);
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                Log.d(TAG, "错误:" + throwable.getMessage());
            }
        });
    }


    private HttpParams getPublicParams() {
        HttpParams map = new HttpParams();
        map.put("ac", AnnUtils.getNetworkType(sContext));
        map.put("app_name", AnnUtils.getApplicationName(sContext));
        map.put("version_name", AnnUtils.getVersionName(sContext));
        map.put("device_type", AnnUtils.getSystemModel());
        map.put("device_brand", AnnUtils.getDeviceBrand());
        map.put("language", AnnUtils.getSystemLanguage());
        map.put("os", "Android");
        map.put("os_api", AnnUtils.getOSApi());
        map.put("os_version", AnnUtils.getSystemVersion());
        map.put("resolution", sWidth + "*" + sHeight);
        map.put("dpi", String.valueOf(sDensityDpi));

        String tk = String.valueOf(System.currentTimeMillis());
        map.put("tk", tk);

        String imei = sAnnCallback.imei();
        String appId = sAnnCallback.appId();
        String md5 = AnnUtils.md5(KEY + imei + appId + tk);
        String hvalue = "";
        if (!TextUtils.isEmpty(md5)) {
            hvalue = md5.substring(2, 17).toLowerCase();
        }

        map.put("pid", sAnnCallback.pid());
        map.put("sid", sAnnCallback.sid());
        map.put("openId", sAnnCallback.openId());
        map.put("lid", sAnnCallback.loginId());
        map.put("channel", sAnnCallback.channel());

        map.put("appId", appId);
        map.put("uuid", imei);

        map.put("hvalue", hvalue);
        return map;
    }


    public interface AnnService {
        @GET("/report.png")
        Observable<ResponseBody> ann(@QueryMap Map<String, String> map);
    }


//    public void onLostAnn() {
//        Observable.fromIterable(mDBHelper.getAnnMsg()).flatMap(new Function<String, ObservableSource<String>>() {
//            @Override
//            public ObservableSource<String> apply(String jsonMsg) throws Exception {
//                Map<String, String> map = AnnUtils.string2Map(jsonMsg);
//                HttpParams params = new HttpParams();
//                params.put(map);
//                Log.d("AnnHelper", "onLostAnn " + jsonMsg);
//                CustomRequest request = RxHttp.custom().params(params).baseUrl(HOST).build();
//                return annService(request);
//            }
//        }).compose(RxUtil.<String>io2ui()).subscribe(new Consumer<String>() {
//            @Override
//            public void accept(String s) throws Exception {
//                Log.d("AnnHelper", "onLostAnn 成功");
//            }
//        }, new Consumer<Throwable>() {
//            @Override
//            public void accept(Throwable throwable) throws Exception {
//                Log.d("AnnHelper", "onLostAnn 失败");
//            }
//        });
//
//
//    }

//        RxHttp.get(HOST + "/report.png").params(params).execute(String.class).subscribe(new Observer<String>() {
//            Disposable mDisposable;
//
//            @Override
//            public void onSubscribe(Disposable disposable) {
//                mDisposable = disposable;
//            }
//
//            @Override
//            public void onNext(String s) {
//
//            }
//
//            @Override
//            public void onError(Throwable throwable) {
////                if ((wrapper.throwable instanceof ConnectException
////                        || wrapper.throwable instanceof SocketTimeoutException
////                        || errCode == ApiException.ERROR.NETWORD_ERROR
////                        || errCode == ApiException.ERROR.TIMEOUT_ERROR
////                        || wrapper.throwable instanceof SocketTimeoutException
////                        || wrapper.throwable instanceof TimeoutException) && wrapper.index < count + 1) { //如果超出重试次数也抛出错误，否则默认是会进入onCompleted
////                    return Observable.timer(delay + (wrapper.index - 1) * increaseDelay, TimeUnit.MILLISECONDS);
////
////                }
//                if (mDisposable != null && !mDisposable.isDisposed()) {
//                    mDisposable.dispose();
//                }
//                mDisposable = null;
//            }
//
//            @Override
//            public void onComplete() {
//                if (mDisposable != null && !mDisposable.isDisposed()) {
//                    mDisposable.dispose();
//                }
//                mDisposable = null;
//            }
//        });


}
