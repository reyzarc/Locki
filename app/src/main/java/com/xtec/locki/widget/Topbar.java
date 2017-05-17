package com.xtec.locki.widget;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xtec.locki.R;

/**
 * Created by 武昌丶鱼 on 2017/4/18.
 * Description:自定义topbar
 */

public class Topbar extends LinearLayout {
    /**
     * topbar高度
     */
    private int mTopbarHeight;
    /**
     * 是否透明状态栏
     */
    private boolean mIsTransparent;
    /**
     * 是否激活返回按键
     */
    private boolean mEnableBack;
    /**
     * 左边图标
     */
    private Drawable mLeftIcon;
    /**
     * 右边图标
     */
    private Drawable mRightIcon;
    /**
     * 左边文字
     */
    private String mLeftText;
    /**
     * 右边文字
     */
    private String mRightText;
    /**
     * 标题
     */
    private String mTitleText;
    /**
     * topbar的背景色
     */
    private int mTopbarColor;
    /**
     * 标题文字颜色,默认白色
     */
    private int mTitleColor;

    private LayoutInflater mLayoutInflater;
    private View mView;

    private TextView leftTextView, rightTextView, titleTextView;
    private ImageButton leftButton, rightButton;


    public Topbar(Context context) {
        this(context, null);
    }

    public Topbar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Topbar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(final Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.Topbar);
        mEnableBack = ta.getBoolean(R.styleable.Topbar_back_enable, false);
        mIsTransparent = ta.getBoolean(R.styleable.Topbar_topbar_transparent, false);
        mTopbarColor = ta.getColor(R.styleable.Topbar_topbar_color, getResources().getColor(R.color.colorPrimary));
        mTopbarHeight = (int) ta.getDimension(R.styleable.Topbar_topbar_height, 50);
        mLeftIcon = ta.getDrawable(R.styleable.Topbar_left_icon);
        mRightIcon = ta.getDrawable(R.styleable.Topbar_right_icon);
        mLeftText = ta.getString(R.styleable.Topbar_left_text);
        mRightText = ta.getString(R.styleable.Topbar_right_text);
        mTitleText = ta.getString(R.styleable.Topbar_title);
        mTitleColor = ta.getColor(R.styleable.Topbar_title_color, Color.parseColor("#FFFFFF"));

        ta.recycle();

        mLayoutInflater = LayoutInflater.from(context);
        mView = mLayoutInflater.inflate(R.layout.layout_topbar_new, null);

        leftButton = (ImageButton) mView.findViewById(R.id.topbar_left_icon);
        rightButton = (ImageButton) mView.findViewById(R.id.topbar_right_icon);
        leftTextView = (TextView) mView.findViewById(R.id.topbar_left_title);
        rightTextView = (TextView) mView.findViewById(R.id.topbar_right_title);
        titleTextView = (TextView) mView.findViewById(R.id.topbar_title);

        //设置标题
        if(!TextUtils.isEmpty(mTitleText)){
            titleTextView.setText(mTitleText);
            titleTextView.setTextColor(mTitleColor);
        }

        //设置背景透明
        if (mIsTransparent) {//背景透明,则默认将文字颜色设置为主题色
            mView.setBackgroundColor(0x00000000);
            leftTextView.setTextColor(getResources().getColor(R.color.colorPrimary));
            rightTextView.setTextColor(getResources().getColor(R.color.colorPrimary));
        } else {//背景不透明,默认为白色
            mView.setBackgroundColor(mTopbarColor);
            leftTextView.setTextColor(Color.parseColor("#FFFFFF"));
            rightTextView.setTextColor(Color.parseColor("#FFFFFF"));
        }

        if (mLeftIcon == null) {
            leftButton.setVisibility(GONE);
        } else {
            leftTextView.setVisibility(GONE);
            leftButton.setVisibility(VISIBLE);
            leftButton.setImageDrawable(mLeftIcon);
        }
        if (mRightIcon == null) {
            rightButton.setVisibility(GONE);
        } else {
            rightTextView.setVisibility(GONE);
            rightButton.setVisibility(VISIBLE);
            rightButton.setImageDrawable(mRightIcon);
        }

        if (TextUtils.isEmpty(mLeftText)) {
            leftTextView.setVisibility(GONE);
        } else {
            leftButton.setVisibility(GONE);
            leftTextView.setVisibility(VISIBLE);
            leftTextView.setText(mLeftText);
        }
        if (TextUtils.isEmpty(mRightText)) {
            rightTextView.setVisibility(GONE);
        } else {
            rightButton.setVisibility(GONE);
            rightTextView.setVisibility(VISIBLE);
            rightTextView.setText(mRightText);
        }

        //设置激活返回键
        if (mEnableBack) {
            leftTextView.setVisibility(GONE);
            leftButton.setVisibility(VISIBLE);
            leftButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_back_white));
            leftButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((Activity) context).finish();
                }
            });
        }

        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        addView(mView, lp);
    }

    /**
     * 设置标题
     * @param title
     */
    public void setTitle(String title){
        if(!TextUtils.isEmpty(title)){
            titleTextView.setText(title);
        }
    }

    /**
     * 设置标题,带颜色
     * @param title
     * @param color
     */
    public void setTitle(String title,int color){
        if(!TextUtils.isEmpty(title)){
            titleTextView.setText(title);
            titleTextView.setTextColor(color);
        }
    }

    /**
     * 设置标题颜色
     * @param color
     */
    public void setTitleColor(int color){
        if(!TextUtils.isEmpty(titleTextView.getText().toString())){
            titleTextView.setTextColor(color);
        }
    }

    /**
     * 左边纯图片
     *
     * @param icon
     * @param listener
     */
    public void setLeftIcon(Drawable icon, OnClickListener listener) {
        if (leftButton != null) {
            leftButton.setImageDrawable(icon);
            leftTextView.setVisibility(GONE);
            leftButton.setVisibility(VISIBLE);
            leftButton.setOnClickListener(listener);
        }
    }

    /**
     * 右边纯图片
     *
     * @param icon
     * @param listener
     */
    public void setRightIcon(Drawable icon, OnClickListener listener) {
        if (leftButton != null) {
            rightButton.setImageDrawable(icon);
            rightTextView.setVisibility(GONE);
            rightButton.setVisibility(VISIBLE);
            rightButton.setOnClickListener(listener);
        }
    }

    /**
     * 左边纯文字
     *
     * @param leftText
     * @param listener
     */
    public void setLeftText(String leftText, OnClickListener listener) {
        if (!TextUtils.isEmpty(leftText)) {
            leftButton.setVisibility(GONE);
            leftTextView.setVisibility(VISIBLE);
            leftTextView.setText(leftText);
            leftTextView.setOnClickListener(listener);
        }
    }

    /**
     * 设置右边带图片的文字
     *
     * @param leftText
     * @param drawable
     * @param listener
     */
    public void setLeftText(String leftText, Drawable drawable, OnClickListener listener) {
        if (!TextUtils.isEmpty(leftText)) {
            leftButton.setVisibility(GONE);
            leftTextView.setVisibility(VISIBLE);
            leftTextView.setText(leftText);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            leftTextView.setCompoundDrawables(drawable, null, null, null);
            leftTextView.setCompoundDrawablePadding(10);
            leftTextView.setOnClickListener(listener);
        }
    }

    /**
     * 右边纯文字
     *
     * @param rightText
     * @param listener
     */
    public void setRightText(String rightText, OnClickListener listener) {
        if (!TextUtils.isEmpty(rightText)) {
            rightButton.setVisibility(GONE);
            rightTextView.setVisibility(VISIBLE);
            rightTextView.setText(rightText);
            rightTextView.setOnClickListener(listener);
        }
    }

    /**
     * 设置右边带图片的文字
     *
     * @param rightText
     * @param drawable
     * @param listener
     */
    public void setRightText(String rightText, Drawable drawable, OnClickListener listener) {
        if (!TextUtils.isEmpty(rightText)) {
            rightButton.setVisibility(GONE);
            rightTextView.setVisibility(VISIBLE);
            rightTextView.setText(rightText);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            rightTextView.setCompoundDrawables(null, null, drawable, null);
            rightTextView.setCompoundDrawablePadding(10);
            rightTextView.setOnClickListener(listener);
        }
    }

    /**
     * 右边bar点击事件
     *
     * @param listener
     */
    public void setRightBarClickListener(OnClickListener listener) {
        if (rightTextView.getVisibility() == View.VISIBLE) {
            rightTextView.setOnClickListener(listener);
        } else if (rightButton.getVisibility() == View.VISIBLE) {
            rightButton.setOnClickListener(listener);
        }
    }

    /**
     * 左边bar点击事件
     *
     * @param listener
     */
    public void setLeftBarClickListener(OnClickListener listener) {
        if (leftTextView.getVisibility() == View.VISIBLE) {
            leftTextView.setOnClickListener(listener);
        } else if (leftButton.getVisibility() == View.VISIBLE) {
            leftButton.setOnClickListener(listener);
        }
    }

    public TextView getRightTextView() {
        if (rightTextView != null) {
            return rightTextView;
        }
        return null;
    }

    public TextView getLeftTextView() {
        if (leftTextView != null) {
            return leftTextView;
        }
        return null;
    }
}
