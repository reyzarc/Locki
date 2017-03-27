package com.xtec.locki.widget;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xtec.locki.R;


/**
 * Created by 武昌丶鱼 on 2016/10/14.
 * Description:自定义dialog
 */
public class FastDialog {

    private static final String TAG = "FastDialog";
    private Context mContext;
    private String mTitle;
    private String mContent;
    private String mSingleButtonText;
    private boolean mIsSingleButton;
    private String mPositiveButtonText;
    private String mNegativeButtonText;
    private boolean mIsCancelable;
    private boolean mIsCancelOnTouchOutside;
    private Dialog mDialog;
    private int mPositiveTextColor= R.color.black,mNegativeTextColor= R.color.black,mSingleTextColor = R.color.black;

    private TextView cancelText;
    private TextView confirmText;
    private TextView singleText;
    private TextView titleText;
    private TextView contentText;
    private LinearLayout llSingle;
    private LinearLayout llDouble;
    private LinearLayout llTitle;
    private boolean isCustom;
    private View mContentView;

    public FastDialog(Context context) {
        isCustom = false;
        mContext = context;
        mDialog = new Dialog(context, R.style.FastDialogStyle);
        View contentView = LayoutInflater.from(context).inflate(R.layout.layout_fast_dialog, null);
        mDialog.setContentView(contentView);

        cancelText = (TextView) contentView.findViewById(R.id.tv_cancel);
        confirmText = (TextView) contentView.findViewById(R.id.tv_confirm);
        singleText = (TextView) contentView.findViewById(R.id.tv_single);
        titleText = (TextView) contentView.findViewById(R.id.tv_title);
        contentText = (TextView) contentView.findViewById(R.id.tv_content);
        llSingle = (LinearLayout) contentView.findViewById(R.id.ll_single);
        llDouble = (LinearLayout) contentView.findViewById(R.id.ll_double);
        llTitle = (LinearLayout) contentView.findViewById(R.id.ll_title);
    }

    public FastDialog(Context context, View contentView){
        isCustom = true;
        mContext = context;
        mDialog = new Dialog(context, R.style.FastDialogStyle);
        mDialog.setContentView(contentView);
    }

//    public FastDialog setContentView(View contentView){
//        isCustom = true;
//        mContentView = contentView;
//        return this;
//    }

    private void initDialogAttributes() {
        Window dialogWindow = mDialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.CENTER);
        WindowManager m = ((Activity) mContext).getWindowManager();
        Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
        lp.width = (int) (d.getWidth() * 0.8); // 宽度设置为屏幕的0.8
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.alpha = 1f; // 透明度
        dialogWindow.setAttributes(lp);
    }



    /**
     * dialog标题
     *
     * @param title
     * @return
     */
    public FastDialog setTitle(String title) {
        mTitle = title;
        return this;
    }

    /**
     * dialog内容
     *
     * @param content
     * @return
     */
    public FastDialog setContent(String content) {
        mContent = content;
        return this;
    }

    /**
     * dialog的确定按钮
     *
     * @param positiveText
     * @param onClickListener
     * @return
     */
    public FastDialog setPositiveButton(String positiveText, final OnClickListener onClickListener) {
        mPositiveButtonText = positiveText;
        confirmText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                onClickListener.onClick(FastDialog.this);
            }
        });
        return this;
    }

    /**
     * dialog的确定按钮
     *
     * @param positiveText
     * @param onClickListener
     * @return
     */
    public FastDialog setPositiveButton(String positiveText,int textColor,final OnClickListener onClickListener) {
        mPositiveButtonText = positiveText;
        mPositiveTextColor = textColor;
        confirmText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                onClickListener.onClick(FastDialog.this);
            }
        });
        return this;
    }

    /**
     * dialog的取消按钮
     *
     * @param negativeText
     * @param onClickListener
     * @return
     */
    public FastDialog setNegativeButton(String negativeText, final OnClickListener onClickListener) {
        mNegativeButtonText = negativeText;
        cancelText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                onClickListener.onClick(FastDialog.this);
            }
        });
        return this;
    }

    /**
     * dialog的取消按钮
     *
     * @param negativeText
     * @param onClickListener
     * @return
     */
    public FastDialog setNegativeButton(String negativeText, int textColor,final OnClickListener onClickListener) {
        mNegativeButtonText = negativeText;
        mNegativeTextColor = textColor;
        cancelText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                onClickListener.onClick(FastDialog.this);
            }
        });
        return this;
    }

    /**
     * dialog是否只有一个按钮
     *
     * @return
     */
    public FastDialog setSingleButton(String singleText, final OnClickListener onClickListener) {
        mSingleButtonText = singleText;
        mIsSingleButton = true;
        llSingle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                onClickListener.onClick(FastDialog.this);
            }
        });
        return this;
    }

    /**
     * dialog是否只有一个按钮
     *
     * @return
     */
    public FastDialog setSingleButton(String singleText,int textColor, final OnClickListener onClickListener) {
        mSingleButtonText = singleText;
        mSingleTextColor = textColor;
        mIsSingleButton = true;
        llSingle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                onClickListener.onClick(FastDialog.this);
            }
        });
        return this;
    }

    /**
     * 是否允许点击取消
     *
     * @param isCancelable
     * @return
     */
    public FastDialog setCancelable(boolean isCancelable) {
        mIsCancelable = isCancelable;
        return this;
    }

    /**
     * 是否允许点击窗口外面取消
     *
     * @param isCancelOnTouchOutside
     * @return
     */
    public FastDialog setCanceledOnTouchOutside(boolean isCancelOnTouchOutside) {
        mIsCancelOnTouchOutside = isCancelOnTouchOutside;
        return this;
    }

    public FastDialog create() {
        if(!isCustom){//不是自定义的dialog

            if (TextUtils.isEmpty(mTitle)) {//没有标题
                llTitle.setVisibility(View.GONE);
            } else {//有标题
                llTitle.setVisibility(View.VISIBLE);
                titleText.setText(mTitle);
            }

            if (mIsSingleButton) {//单个按钮的dialog
                llDouble.setVisibility(View.GONE);
                llSingle.setVisibility(View.VISIBLE);
                singleText.setText(mSingleButtonText);
                singleText.setTextColor(mContext.getResources().getColor(mSingleTextColor));
            } else {//两个按钮的dialog
                llDouble.setVisibility(View.VISIBLE);
                llSingle.setVisibility(View.GONE);
                confirmText.setText(mPositiveButtonText);
                cancelText.setText(mNegativeButtonText);
                confirmText.setTextColor(mContext.getResources().getColor(mPositiveTextColor));
                cancelText.setTextColor(mContext.getResources().getColor(mNegativeTextColor));
            }

            contentText.setText(mContent);
        }

        mDialog.setCanceledOnTouchOutside(mIsCancelOnTouchOutside);
        mDialog.setCancelable(mIsCancelable);

        initDialogAttributes();
        return this;
    }

    public void show() {
        if(mDialog!=null&&!mDialog.isShowing()){
            mDialog.show();
        }
    }

    public boolean isShowing(){
        return mDialog.isShowing();
    }

    public void dismiss(){
            mDialog.dismiss();
    }

    public interface OnClickListener{
        void onClick(FastDialog dialog);
    }
}
