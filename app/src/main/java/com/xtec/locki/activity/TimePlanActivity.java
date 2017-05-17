package com.xtec.locki.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.xtec.locki.R;
import com.xtec.locki.widget.Topbar;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by 武昌丶鱼 on 2017/5/17.
 * Description:时间计划页面,提供的功能包括设置计划列表,到设定时间检查手机屏幕开关状态,
 * 如果是亮屏,则在屏幕上显示一方块,并随着时间的增加不断变大,此时用户可拖动方块,但不可消除,
 * 直到铺满整个屏幕(显示提示文字:保持专注),持续时间结束,则当前周期的这个计划完成.
 * 当计划时间启动时用户可长按方块5s强制取消当前计划(屏幕破碎效果),拟提供延期功能,弹窗提示整个延期还是只延期当前
 * 列表显示,侧滑弹出,编辑,删除
 */

public class TimePlanActivity extends BaseActivity {

    @BindView(R.id.topbar)
    Topbar topbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_plan);
        ButterKnife.bind(this);
        initTopbar(this,topbar);
    }
}
