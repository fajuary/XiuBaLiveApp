package com.seek;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.xiu8.kb.model.account.detail.ui.KBSeekDetailsActivity;

/**
 * kb模块入口
 */
public class KBmodelActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent=new Intent();
        intent.setClass(this, KBSeekDetailsActivity.class);
        startActivity(intent);
    }
}
