package com.xtec.locki.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by 武昌丶鱼 on 2017/5/17.
 * Description:
 */

public class PlanInfoModel implements Parcelable {

    /**
     * id
     */
    private String id;

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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.type);
        dest.writeString(this.planTitle);
        dest.writeString(this.duration);
        dest.writeString(this.startTime);
        dest.writeString(this.status);
        dest.writeString(this.repeat);
    }

    public PlanInfoModel() {
    }

    protected PlanInfoModel(Parcel in) {
        this.type = in.readString();
        this.planTitle = in.readString();
        this.duration = in.readString();
        this.startTime = in.readString();
        this.status = in.readString();
        this.repeat = in.readString();
    }

    public static final Parcelable.Creator<PlanInfoModel> CREATOR = new Parcelable.Creator<PlanInfoModel>() {
        @Override
        public PlanInfoModel createFromParcel(Parcel source) {
            return new PlanInfoModel(source);
        }

        @Override
        public PlanInfoModel[] newArray(int size) {
            return new PlanInfoModel[size];
        }
    };
}
