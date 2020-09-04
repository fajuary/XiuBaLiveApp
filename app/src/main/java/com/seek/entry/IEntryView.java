package com.seek.entry;

import android.app.Activity;

import com.xiu8.base.mvp.view.IBaseView;

/**
 * Created by zshh on 5/23/18.
 */

public interface IEntryView extends IBaseView{
    void toHomeActivity();
    void toLoginActivity();
    Activity getContext();
    void destroy();
}
