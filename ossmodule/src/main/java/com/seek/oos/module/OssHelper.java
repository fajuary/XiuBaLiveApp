package com.seek.oos.module;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.alibaba.sdk.android.oss.ClientConfiguration;
import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.common.OSSLog;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSStsTokenCredentialProvider;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.seek.media.MetadataRetriever;
import com.seek.oos.module.bean.Domains;
import com.seek.oos.module.bean.OssBean;
import com.seek.oos.module.bean.OssTs;
import com.seek.oos.module.bean.OssType;
import com.xiu8.base.util.MD5Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by chunyang on 2018/5/24.
 */

public class OssHelper {

    private final static class Holder {
        private final static OssHelper IN = new OssHelper();
    }

    public static OssHelper getInstance() {
        return Holder.IN;
    }

    private UploadModel mUploadModel;
    private static Context mContext;
    private OSSClient mOSS;
    private OssBean mOssBean;
    private long mExpireTime = 0;

    private OssHelper() {
        mUploadModel = new UploadModel();
    }

    public static void init(Context context) {
        mContext = context.getApplicationContext();
    }

    private Observable<OssBean> getUploadConfig() {
        return mUploadModel.getUploadConfig().doOnNext(new Consumer<OssBean>() {
            @Override
            public void accept(OssBean ossBean) throws Exception {
                mOssBean = ossBean;
            }
        });
    }

    private <T> Observable<T> checkOssTs(final T t) {
        if (mOssBean == null) {
            return getUploadConfig().flatMap(new Function<OssBean, ObservableSource<T>>() {
                @Override
                public ObservableSource<T> apply(OssBean ossBean) throws Exception {
                    if (mExpireTime < System.currentTimeMillis())
                        return getOssTs().map(new Function<OssTs, T>() {
                            @Override
                            public T apply(OssTs ossTs) throws Exception {
                                return t;
                            }
                        });
                    return Observable.just(t);
                }
            });
        } else {
            if (mExpireTime < System.currentTimeMillis())
                return getOssTs().map(new Function<OssTs, T>() {
                    @Override
                    public T apply(OssTs ossTs) throws Exception {
                        return t;
                    }
                });
            return Observable.just(t);
        }
    }

    private Observable<OssTs> getOssTs() {
        return mUploadModel.getOssTS().doOnNext(new Consumer<OssTs>() {
            @Override
            public void accept(OssTs ossTs) throws Exception {
                String accessKeyId = ossTs.getAccessKeyId();
                String accessKeySecret = ossTs.getAccessKeySecret();
                String securityToken = ossTs.getSecurityToken();
                mExpireTime = Long.valueOf(ossTs.getExpireTime());
                Log.d("OssHelper", "endpoint:" + mOssBean.getEndpoint() + ",accessKeyId:" + accessKeyId + ",accessKeySecret:" + accessKeySecret + ",securityToken:" + securityToken + ",expireTime:" + mExpireTime + ",curTime:" + System.currentTimeMillis());
                initOss(mContext, mOssBean.getEndpoint(), accessKeyId, accessKeySecret, securityToken);

            }
        });
    }

    private void initOss(Context context, String endpoint, String accessKeyId, String secretKeyId, String securityToken) {
        //该配置类如果不设置，会有默认配置，具体可看该类
        ClientConfiguration conf = new ClientConfiguration();
        conf.setConnectionTimeout(15 * 1000); // 连接超时，默认15秒
        conf.setSocketTimeout(15 * 1000); // socket超时，默认15秒
        conf.setMaxConcurrentRequest(6); // 最大并发请求数，默认5个
        conf.setMaxErrorRetry(2); // 失败后最大重试次数，默认2次
        OSSLog.enableLog(); //这个开启会支持写入手机sd卡中的一份日志文件位置在SDCard_path\OSSLog\logs.csv

        OSSCredentialProvider credentialProvider = new OSSStsTokenCredentialProvider(accessKeyId, secretKeyId, securityToken);

        mOSS = new OSSClient(context, endpoint, credentialProvider, conf);
    }


    public Observable<String> putVoice(String path) {
        return checkOssTs(path).flatMap(new Function<String, ObservableSource<String>>() {
            @Override
            public ObservableSource<String> apply(String path) throws Exception {
                return Observable.just(path).flatMap(new Function<String, ObservableSource<String>>() {
                    @Override
                    public ObservableSource<String> apply(String path) throws Exception {
                        final String dir = mOssBean.getDirs().getVoice();
                        final String bucketName = mOssBean.getBucketName();
                        final String domains = mOssBean.getDomains().getVoice();

                        String duration = MetadataRetriever.getDuration(path);

                        String format = getFormat(path);

                        String key = dir + getMD5UUID() + format;

                        PutObjectResult putObjectResult = put(bucketName, key, path);

                        Log.d("OssHelper", "ETag:" + putObjectResult.getETag() + ",RequestId" + putObjectResult.getRequestId() + ",StatusCode:s" + putObjectResult.getStatusCode());

                        String url = domains + key + "?form:" + format.substring(1, format.length()) + "&duration=" + duration;

                        return Observable.just(url);
                    }
                }).subscribeOn(Schedulers.io()).unsubscribeOn(Schedulers.io());
            }
        });
    }


    public Observable<String> putAvatar(String path) {
        return checkOssTs(path).flatMap(new Function<String, ObservableSource<String>>() {
            @Override
            public ObservableSource<String> apply(String path) throws Exception {
                return Observable.just(path).flatMap(new Function<String, ObservableSource<String>>() {
                    @Override
                    public ObservableSource<String> apply(String path) throws Exception {
                        final String dir = mOssBean.getDirs().getAvatar().getB();
                        final String bucketName = mOssBean.getBucketName();
                        final String domains = mOssBean.getDomains().getAvatar();

                        String format = getFormat(path);

                        String key = dir + getMD5UUID() + format;

                        PutObjectResult putObjectResult = put(bucketName, key, path);

                        Log.d("OssHelper", "ETag:" + putObjectResult.getETag() + ",RequestId" + putObjectResult.getRequestId() + ",StatusCode:s" + putObjectResult.getStatusCode());

                        return Observable.just(domains + key);
                    }
                });
            }
        }).subscribeOn(Schedulers.io()).unsubscribeOn(Schedulers.io());
    }

    public Observable<String> putStory(final int type, String path) {

        return checkOssTs(path).flatMap(new Function<String, ObservableSource<String>>() {
            @Override
            public ObservableSource<String> apply(String path) throws Exception {
                return Observable.just(path).flatMap(new Function<String, ObservableSource<String>>() {
                    @Override
                    public ObservableSource<String> apply(String path) throws Exception {
                        final OssType ossType = mOssBean.getDirs().getStory();
                        final Domains dm = mOssBean.getDomains();
                        String dir = null;
                        String domains = null;
                        if (type == 1) {
                            dir = ossType.getImg();
                            domains = dm.getImg();
                        } else if (type == 2) {
                            dir = ossType.getVideo();
                            domains = dm.getVideo();
                        } else if (type == 3) {
                            dir = ossType.getVoice();
                            domains = dm.getVoice();
                        }
                        if (dir == null || domains == null)
                            return Observable.error(new RuntimeException("存储空间为空"));
                        final String bucketName = mOssBean.getBucketName();


                        String format = getFormat(path);
                        String key = dir + getMD5UUID() + format;

                        PutObjectResult putObjectResult = put(bucketName, key, path);

                        String url = domains + key;
                        if (type == 2) {
                            String duration = MetadataRetriever.getDuration(path);
                            Bitmap bitmap = MetadataRetriever.getVideoThumbnail(path);
                            int widht = bitmap.getWidth();
                            int height = bitmap.getHeight();
                            url = url + "?form=" + format.substring(1, format.length()) + "&duration=" + duration + "&width=" + widht + "&height=" + height;
                        } else if (type == 3) {
                            String duration = MetadataRetriever.getDuration(path);
                            url = url + "?form=" + format.substring(1, format.length()) + "&duration=" + duration;
                        }

                        Log.d("OssHelper", "ETag:" + putObjectResult.getETag() + ",RequestId" + putObjectResult.getRequestId() + ",StatusCode:s" + putObjectResult.getStatusCode());
                        return Observable.just(url);
                    }
                });
            }
        }).subscribeOn(Schedulers.io()).unsubscribeOn(Schedulers.io());
    }

    public Observable<String> putChat(final int type, String path) {
        return checkOssTs(path).flatMap(new Function<String, ObservableSource<String>>() {
            @Override
            public ObservableSource<String> apply(String path) throws Exception {
                return Observable.just(path).flatMap(new Function<String, ObservableSource<String>>() {
                    @Override
                    public ObservableSource<String> apply(String path) throws Exception {
                        final OssType ossType = mOssBean.getDirs().getChat();
                        final Domains dm = mOssBean.getDomains();
                        String dir = null;
                        String domains = null;
                        if (type == 1) {
                            dir = ossType.getImg();
                            domains = dm.getImg();
                        } else if (type == 2) {
                            dir = ossType.getVideo();
                            domains = dm.getVideo();
                        } else if (type == 3) {
                            dir = ossType.getVoice();
                            domains = dm.getVoice();
                        }
                        if (dir == null)
                            return Observable.error(new RuntimeException("存储空间为空"));
                        final String bucketName = mOssBean.getBucketName();

                        String format = getFormat(path);
                        String key = dir + getMD5UUID() + format;

                        PutObjectResult putObjectResult = put(bucketName, key, path);

                        String url = domains + key;
                        if (type == 2) {
                            String duration = MetadataRetriever.getDuration(path);
                            Bitmap bitmap = MetadataRetriever.getVideoThumbnail(path);
                            int widht = bitmap.getWidth();
                            int height = bitmap.getHeight();
                            url = url + "?form=" + format.substring(1, format.length()) + "&duration=" + duration + "&width=" + widht + "&height=" + height;
                        } else if (type == 3) {
                            String duration = MetadataRetriever.getDuration(path);
                            url = url + "?form=" + format.substring(1, format.length()) + "&duration=" + duration;
                        }

                        Log.d("OssHelper", "ETag:" + putObjectResult.getETag() + ",RequestId" + putObjectResult.getRequestId() + ",StatusCode:s" + putObjectResult.getStatusCode());
                        return Observable.just(url);
                    }
                });
            }
        }).subscribeOn(Schedulers.io()).unsubscribeOn(Schedulers.io());
    }

    public Observable<List<String>> putPhotoWall(List<String> photoWall) {
        final List<String> mList = new ArrayList<>();
        return checkOssTs(photoWall).flatMap(new Function<List<String>, ObservableSource<List<String>>>() {
            @Override
            public ObservableSource<List<String>> apply(List<String> list) throws Exception {
                return Observable.fromIterable(list).flatMap(new Function<String, ObservableSource<String>>() {
                    @Override
                    public ObservableSource<String> apply(String path) throws Exception {
                        final String bucketName = mOssBean.getBucketName();
                        final String dir = mOssBean.getDirs().getWall();
                        final String domains = mOssBean.getDomains().getImg();

                        String key = dir + getMD5UUID() + getFormat(path);

                        PutObjectResult putObjectResult = put(bucketName, key, path);
                        String url = domains + key;
                        mList.add(url);

                        Log.d("OssHelper", "ETag:" + putObjectResult.getETag() + ",RequestId" + putObjectResult.getRequestId() + ",StatusCode:s" + putObjectResult.getStatusCode());
                        return Observable.just(url);
                    }
                }).lastElement().flatMapObservable(new Function<String, ObservableSource<List<String>>>() {
                    @Override
                    public ObservableSource<List<String>> apply(String s) throws Exception {
                        return Observable.just(mList);
                    }
                });
            }
        }).subscribeOn(Schedulers.io()).unsubscribeOn(Schedulers.io());
    }

//    public void asyncPut(final String bucketName, final String objectKey, String uploadFilePath) {
//        if (objectKey.equals("")) {
//            Log.w("AsyncPutImage", "ObjectNull");
//            return;
//        }
//
//        if (uploadFilePath == null || uploadFilePath.isEmpty()) return;
//
//        PutObjectRequest put = new PutObjectRequest(bucketName, objectKey, uploadFilePath);
//
//        OSSAsyncTask taks = mOSS.asyncPutObject(put, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
//            @Override
//            public void onSuccess(PutObjectRequest request, PutObjectResult result) {
//                Log.d("PutObject", "UploadSuccess");
//            }
//
//            @Override
//            public void onFailure(PutObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
//                // 请求异常
//                if (clientExcepion != null) {
//                    // 本地异常如网络异常等
//                    clientExcepion.printStackTrace();
//                }
//                if (serviceException != null) {
//                    // 服务异常
//                    Log.e("ErrorCode", serviceException.getErrorCode());
//                    Log.e("RequestId", serviceException.getRequestId());
//                    Log.e("HostId", serviceException.getHostId());
//                    Log.e("RawMessage", serviceException.getRawMessage());
//                }
//            }
//        });
//    }


    public PutObjectResult put(final String bucketName, final String objectKey, String uploadFilePath) throws ClientException, ServiceException, IOException {
        PutObjectRequest put = new PutObjectRequest(bucketName, objectKey, uploadFilePath);
//        ObjectMetadata metadata = new ObjectMetadata();
//        metadata.setContentType("application/octet-stream");
//        // 设置Md5以便校验
//        metadata.setContentMD5(BinaryUtil.calculateBase64Md5(uploadFilePath)); // 如果是从文件上传
//        // metadata.setContentMD5(BinaryUtil.calculateBase64Md5(byte[])); // 如果是上传二进制数据
//        put.setMetadata(metadata);
        Log.d("OssHelper", "bucketName:" + bucketName + ",objectKey:" + objectKey + ",uploadFilePath:" + uploadFilePath);
        return mOSS.putObject(put);
    }

    public String getFormat(String path) {
        return path.substring(path.lastIndexOf("."), path.length());
    }


    private String getMD5UUID() {
        return MD5Utils.stringMd5(UUID.randomUUID().toString());
    }

//    public String fileToMD5(String filePath) {
//        InputStream inputStream = null;
//        try {
//            inputStream = new FileInputStream(filePath); // Create an FileInputStream instance according to the filepath
//            byte[] buffer = new byte[1024]; // The buffer to read the file
//            MessageDigest digest = MessageDigest.getInstance("MD5"); // Get a MD5 instance
//            int numRead = 0; // Record how many bytes have been read
//            while (numRead != -1) {
//                numRead = inputStream.read(buffer);
//                if (numRead > 0)
//                    digest.update(buffer, 0, numRead); // Update the digest
//            }
//            byte[] md5Bytes = digest.digest(); // Complete the hash computing
//            return convertHashToString(md5Bytes); // Call the function to convert to hex digits
//        } catch (Exception e) {
//            return null;
//        } finally {
//            if (inputStream != null) {
//                try {
//                    inputStream.close(); // Close the InputStream
//                } catch (Exception e) {
//                }
//            }
//        }
//    }

//    private static String convertHashToString(byte[] hashBytes) {
//        String returnVal = "";
//        for (int i = 0; i < hashBytes.length; i++) {
//            returnVal += Integer.toString((hashBytes[i] & 0xff) + 0x100, 16).substring(1);
//        }
//        return returnVal.toLowerCase();
//    }


}
