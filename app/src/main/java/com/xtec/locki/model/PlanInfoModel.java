package com.xtec.locki.model;

/**
 * Created by 武昌丶鱼 on 2017/5/17.
 * Description:
 */

public class PlanInfoModel {

    private String planTitle;
    private String duration;
    private String startTime;
    /**
     * 状态 0-暂停中,1-执行中
     */
    private String status;

    public String getPlanTitle() {
        return planTitle;
    }

    public void setPlanTitle(String planTitle) {
        this.planTitle = planTitle;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
