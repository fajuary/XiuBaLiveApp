package com.seek.nbs;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import com.networkbench.agent.impl.NBSAppAgent;

/**
 * Created by 13932 on 2018/6/22.
 */

public class NBSActivityLifeCallbackListener implements Application.ActivityLifecycleCallbacks{

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        NBSAppAgent.leaveBreadcrumb("Create:" + activity.getLocalClassName());
    }

    @Override
    public void onActivityStarted(Activity activity) {
        NBSAppAgent.leaveBreadcrumb("onActivityStarted:" + activity.getLocalClassName());
    }

    @Override
    public void onActivityResumed(Activity activity) {
        NBSAppAgent.leaveBreadcrumb("onActivityResumed:" + activity.getLocalClassName());
    }

    @Override
    public void onActivityPaused(Activity activity) {
        NBSAppAgent.leaveBreadcrumb("onActivityPaused:" + activity.getLocalClassName());
    }

    @Override
    public void onActivityStopped(Activity activity) {
        NBSAppAgent.leaveBreadcrumb("onActivityStopped:" + activity.getLocalClassName());
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
         NBSAppAgent.leaveBreadcrumb("onActivitySaveInstanceState:" + activity.getLocalClassName());
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        NBSAppAgent.leaveBreadcrumb("onActivityDestroyed:" + activity.getLocalClassName());
    }
}
