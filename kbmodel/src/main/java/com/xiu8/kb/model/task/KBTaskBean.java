package com.xiu8.kb.model.task;

import com.xiu8.base.BaseBean;

/**
 * Created by chunyang on 2018/8/29.
 */

public class KBTaskBean extends BaseBean {


    /**
     * taskId : integer,任务Id
     * taskName : string,任务名称
     * remark : string,任务描述
     * award : integer,任务完成获得的K币
     * finish : integer,任务完成状态：0=未完成，1=已完成
     */
    private int taskId;
    private String taskName;
    private String remark;
    private int award;
    private int finish;
    private String extend;

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public int getAward() {
        return award;
    }

    public void setAward(int award) {
        this.award = award;
    }


    public boolean isFinish() {
        return finish == 1;
    }

    public void setFinish(int finish) {
        this.finish = finish;
    }

    public String getExtend() {
        return extend;
    }


    public boolean isShareCard() {
        return taskId == 1;
    }

    public boolean isShareUrl() {
        return taskId == 3;
    }
}
