package com.xtec.locki;

/**
 * Created by 武昌丶鱼 on 2017/3/24.
 * Description:
 */

public class Constant {
    /**
     * 解锁方式
     */
    public static final String LOCK_METHOD = "lock_method";
    /**
     * 指纹
     */
    public static final String FINGERPRINT = "fingerprint";
    /**
     * 数字
     */
    public static final String NUMBER = "number";
    /**
     * 手势
     */
    public static final String GESTURE = "gesture";
    /**
     * 解锁成功
     */
    public static final String UNLOCK_SUCCESS = "unlock_success";
    /**
     * 包名
     */
    public static final String PACKAGE_NAME = "package_name";
    /**
     * 解锁成功的广播的action
     */
    public static final String ACTION_UNLOCK_SUCCESS = "action_unlock_success";
    /**
     * 更新加锁列表的广播的action
     */
    public static final String ACTION_UPDATE_UNLOCK_LIST = "action_update_unlock_list";
    /**
     * 解锁时间
     */
    public static final String UNLOCK_DATE = "unlock_date";

    //*/ 手势密码点的状态
    public static final int POINT_STATE_NORMAL = 0; // 正常状态

    public static final int POINT_STATE_SELECTED = 1; // 按下状态

    public static final int POINT_STATE_WRONG = 2; // 错误状态
    public static final String GESTURE_PASSWORD = "gesture_password";
    public static final String LOCK_LIST = "lock_list";
    /**
     * 数字密码
     */
    public static final String NUMBER_PASSWORD = "number_password";
    /**
     * 手势密码结果标识
     */
    public static final int RESULT_GESTURE = 0;
    /**
     * 数字密码结果标识
     */
    public static final int RESULT_NUMBER = 1;
}
