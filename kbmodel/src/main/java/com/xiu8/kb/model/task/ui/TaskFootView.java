package com.xiu8.kb.model.task.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.xiu8.kb.model.R;

/**
 * Created by chunyang on 2018/8/31.
 */

public class TaskFootView extends LinearLayout {

    public TaskFootView(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.item_kb_task_foot_view, this, true);
    }
}
