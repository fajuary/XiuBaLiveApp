package com.xiu8.kb.model.task;

import com.seek.library.EventKey;
import com.seek.library.bean.Empty;
import com.seek.library.bean.ShareUrlBean;
import com.seek.library.http.SeekCallBack;
import com.seek.library.http.SeekPObserver;
import com.seek.user.module.UserCardHelper;
import com.xiu8.base.bus.IBusEvent;
import com.xiu8.base.mvp.model.CreateModel;
import com.xiu8.base.mvp.presenter.BasePresenter;
import com.xiu8.logger.library.Logger;

import java.util.List;

/**
 * Created by chunyang on 2018/8/29.
 */
@CreateModel(KBTaskModel.class)
public class KBTaskPresenter extends BasePresenter<KBTaskView, KBTaskModel> {

    private AccountInfo mAccountInfo;

    private class AccountInfo implements IBusEvent<Long> {

        @Override
        public void notifyEvent(Long data, String key) {
            if (isViewAttached()) {
                mView.onAccountResult(data);
            }
        }

    }


    @Override
    public void onAttach(KBTaskView view) {
        super.onAttach(view);
        mAccountInfo = new AccountInfo();
        register(EventKey.KB.RESULT_ACCOUNT_INFO, mAccountInfo);
    }

    public void getTaskList() {
        getAccountInfo();
        mModel.getTaskList().subscribe(new SeekPObserver<List<KBTaskBean>>(this) {
            @Override
            public void onSuccess(List<KBTaskBean> kbTaskBeans) {
                if (isViewAttached()) {
                    mView.onTaskResult(kbTaskBeans);
                }
            }

            @Override
            protected void onError(int code, String message) {
                if (isViewAttached()) {
                    mView.onTaskError();
                }
            }
        });

    }

    public void finishTask(final KBTaskBean kbTaskBean) {
        int taskId = kbTaskBean.getTaskId();
        mModel.finishTask(taskId).subscribe(new SeekPObserver<Empty>(this) {
            @Override
            public void onSuccess(Empty empty) {
                if (isViewAttached()) {
                    kbTaskBean.setFinish(1);
                    mView.finisGroupTask(kbTaskBean);
                }
            }
        });
    }

    //1.微信好友，2.微信朋友圈，3.QQ
    public void getShareUrl(final int index) {
        final int type = (index == 1 || index == 2) ? 1 : 2;//[1:微信,2:QQ,3:短信]
        mModel.getDownLoadUrl(type, new SeekCallBack<String>() {
            @Override
            public void onSuccess(String url) {
                Logger.t("KBTask").w(index + " getShareUrl:" + url);
                UserCardHelper.get().downloadShareCard(url, type, new UserCardHelper.UserCardCallback<String>() {
                    @Override
                    public void onUserCardSuccess(String url) {
                        Logger.t("KBTask").w(index + " getSharePath:" + url);
                        if (!isViewAttached()) return;
                        if (index == 1) {
                            mView.onShareWXCard(url);
                        } else if (index == 2) {
                            mView.onShareWZCard(url);
                        } else {
                            mView.onShareQQCard(url);
                        }
                    }

                    @Override
                    public void onUserCardError(String message) {
                        if (!isViewAttached()) return;
                        mView.onShareError(message);
                    }
                });
            }
        });
    }

    public void getShareTaskUrl(final int index) {
        final int type = (index == 1 || index == 2) ? 1 : 2;//[1:微信,2:QQ,3:短信]
        mModel.getShareTaskUrl(type, new SeekCallBack<ShareUrlBean>() {
            @Override
            public void onSuccess(ShareUrlBean bean) {
                if (!isViewAttached()) return;
                if (index == 1) {
                    mView.onShareWXUrl(bean);
                } else if (index == 2) {
                    mView.onShareWZUrl(bean);
                } else {
                    mView.onShareQQUrl(bean);
                }
            }
        });
    }


    public void quickJump(KBTaskBean bean) {
        int taskId = bean.getTaskId();
        switch (taskId) {
            case 1://邀请好友
                mView.onShareUserCard(bean);
                break;
            case 2://电话达人
                callHotChat();
                break;
            case 3://好友登录
                mView.onShareUserCard(bean);
                break;
            case 4://寻找组织
                mView.joinQQGroup(bean);
                break;
            case 5://拓展交际
                jumpGreet();
                break;
            case 6://每日登录
                break;
            default:
                break;
        }
    }

    private void callHotChat() {
        notify("", EventKey.KB.JUMP_CALL_HOT);
    }

    private void jumpGreet() {
        notify("1", EventKey.KB.JUMP_HOME_RECOMMEND);
    }

    private void getAccountInfo() {
        notify(0l, EventKey.KB.GET_ACCOUNT_INFO);
    }
}
