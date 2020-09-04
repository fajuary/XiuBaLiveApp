package com.xiu8.kb.model.task;

import com.seek.library.bean.ShareUrlBean;
import com.xiu8.base.mvp.view.IBaseView;

import java.util.List;

/**
 * Created by chunyang on 2018/8/29.
 */

public interface KBTaskView extends IBaseView {


    void onAccountResult(long amount);

    void onTaskResult(List<KBTaskBean> result);

    void onTaskError();

    void joinQQGroup(KBTaskBean bean);

    void finisGroupTask(KBTaskBean bean);

    void onShareUserCard(KBTaskBean bean);

    void onShareInvite(KBTaskBean bean);

    void onShareQQCard(String url);

    void onShareWXCard(String url);

    void onShareWZCard(String url);

    void onShareError(String message);

    void onShareQQUrl(ShareUrlBean bean);

    void onShareWXUrl(ShareUrlBean bean);

    void onShareWZUrl(ShareUrlBean bean);
}
