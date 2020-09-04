package com.seek.openinstall;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.fm.openinstall.OpenInstall;
import com.fm.openinstall.listener.AppWakeUpListener;
import com.fm.openinstall.model.AppData;
import com.fm.openinstall.model.Error;
import com.seek.entry.EntryActivity;
import com.seek.home.HomeActivity;
import com.seek.library.SeekBaseApplication;
import com.seek.library.xrouter.XRouterIntent;
import com.seek.login.bundle.login.entry.LoginActivity;
import com.xiu8.base.ActivityManager;

import java.util.List;


/**
 * Created by zshh on 12/14/17.
 * 应用唤起
 */
public class OpenInstallActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        OpenInstall.getWakeUp(getIntent(), wakeUpAdapter);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        OpenInstall.getWakeUp(intent, wakeUpAdapter);
    }

    //openInstall接入．
    AppWakeUpListener wakeUpAdapter = new AppWakeUpListener() {
        @Override
        public void onWakeUpFinish(AppData appData, Error error) {
            if(appData!=null){
                try{
                    String data = appData.getData(); //获取绑定数据d
                    System.out.println("zshh data"+data);
                    startApp(OpenInstallActivity.this,data);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }else{
                try {

                    startApp(OpenInstallActivity.this, "test 数据为空");
                    System.out.println("zshh data"+"test 数据为空");

                }catch (Exception e){}
            }
            finish();
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        wakeUpAdapter = null;
    }

    private void startApp(Context context, String result) throws Exception {
        ActivityManager activityManager = SeekBaseApplication.getInstance().getActivityManager();
        if(isAppExist() && activityManager!= null){
            //说明用户登录App了,
            if (activityManager.isContaisTargetActivity(HomeActivity.class)) {
                Intent intent = new Intent(XRouterIntent.OpenInstall.SCHEMA.HOME);
                intent.putExtra(XRouterIntent.OpenInstall.SCHEMA.Key,result);
                SeekBaseApplication.getInstance().sendBroadcast(intent);
            }else {
                //包含Activity.
                if (activityManager.isContaisTargetActivity(LoginActivity.class)) {
                    Intent intent = new Intent(XRouterIntent.OpenInstall.SCHEMA.LOGIN);
                    intent.putExtra(XRouterIntent.OpenInstall.SCHEMA.Key, result);
                    SeekBaseApplication.getInstance().sendBroadcast(intent);
                } else {
                    Intent intent = new Intent(context, EntryActivity.class);
                    intent.putExtra(XRouterIntent.OpenInstall.SCHEMA.Key,result);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            }
        }else if(!isAppExist()){
            Intent intent = new Intent(context, EntryActivity.class);
            intent.putExtra(XRouterIntent.OpenInstall.SCHEMA.Key,result);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
        finish();
    }

    public static boolean isAppExist() {
		android.app.ActivityManager am = (android.app.ActivityManager) SeekBaseApplication.getInstance().getSystemService(Context.ACTIVITY_SERVICE);
		List<android.app.ActivityManager.RunningTaskInfo> runningTasks = am.getRunningTasks(20);
		int numActivities = 0;
		for (android.app.ActivityManager.RunningTaskInfo runningTaskInfo : runningTasks) {
			if (runningTaskInfo.topActivity.getPackageName().contains(SeekBaseApplication.getInstance().getPackageName())) {
				numActivities += runningTaskInfo.numActivities;
			}
		}
		return numActivities > 0;
	}
}
