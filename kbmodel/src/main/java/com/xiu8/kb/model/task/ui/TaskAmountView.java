package com.xiu8.kb.model.task.ui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.xiu8.kb.model.R;

/**
 * Created by chunyang on 2018/8/29.
 */

public class TaskAmountView extends FrameLayout {

    private TextView mAmountView;

    public TaskAmountView(@NonNull Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.item_kb_task_amount, this, true);
        mAmountView = (TextView) findViewById(R.id.tv_kb_mall_amount);
    }

    public void setAmount(String text) {
        mAmountView.setText(text);
    }

}
