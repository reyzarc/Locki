package com.xtec.locki.model;

/**
 * Created by 武昌丶鱼 on 2017/5/17.
 * Description:
 */

public class PlanInfoModel {

    /**
     * 类别
     */
    private String type;
    /**
     * 标题
     */
    private String planTitle;
    /**
     * 持续时间
     */
    private String duration;
    /**
     * 开始时间
     */
    private String startTime;
    /**
     * 状态 0-暂停,1-执行,2-放弃
     */
    private String status;

    /**
     * 重复周期
     */

    private String repeat;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRepeat() {
        return repeat;
    }

    public void setRepeat(String repeat) {
        this.repeat = repeat;
    }

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

    @Override
    public String toString() {
        return "PlanInfoModel{" +
                "type='" + type + '\'' +
                ", planTitle='" + planTitle + '\'' +
                ", duration='" + duration + '\'' +
                ", startTime='" + startTime + '\'' +
                ", status='" + status + '\'' +
                ", repeat='" + repeat + '\'' +
                '}';
    }
}
