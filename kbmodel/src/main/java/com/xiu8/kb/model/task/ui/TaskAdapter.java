package com.xiu8.kb.model.task.ui;

import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.xiu8.kb.model.R;
import com.xiu8.kb.model.task.KBTaskBean;
import com.xiu8.ximageview.XImageView;

import java.util.List;

/**
 * Created by chunyang on 2018/8/29.
 */

public class TaskAdapter extends BaseQuickAdapter<KBTaskBean, TaskAdapter.Holder> {
    public TaskAdapter(@Nullable List<KBTaskBean> data) {
        super(R.layout.item_kb_task_view, data);
    }


    @Override
    protected void convert(Holder helper, KBTaskBean item) {
        helper.setEnabled(item.isFinish()).setIcon(item.getTaskId());
        helper.setText(R.id.tv_kb_task_name, item.getTaskName())
                .setText(R.id.tv_kb_task_remark, item.getRemark())
                .setText(R.id.tv_kb_task_award, "+" + item.getAward() + "K币");
    }


    class Holder extends BaseViewHolder {

        public Holder(View view) {
            super(view);
        }

        public Holder setEnabled(boolean finish) {
            TextView textView = getView(R.id.tv_kb_task_finish);
            textView.setEnabled(finish);
            if (finish) {
                textView.setText("已完成");
            } else {
                textView.setText("立即前往");
            }
            return this;
        }

        public Holder setIcon(int id) {
            XImageView imageView = getView(R.id.xiv_kb_task_icon);
            int resultId = imageView.getResources().getIdentifier("ic_kb_task_" + id, "drawable", imageView.getContext().getPackageName());
            imageView.setImageResource(resultId);
            return this;
        }
    }

}
