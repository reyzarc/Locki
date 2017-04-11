package com.xtec.locki.adapter;

import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xtec.locki.R;

import java.util.ArrayList;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by 武昌丶鱼 on 2017/4/11.
 * Description:
 */

public class NumberKeyboardAdapter extends BaseAdapter {
    private ArrayList<Map<String, String>> mKeysList;
    private Context mContext;

    public NumberKeyboardAdapter(Context context, ArrayList<Map<String, String>> keysList) {
        mContext = context;
        mKeysList = keysList;
    }

    @Override
    public int getCount() {
        return mKeysList == null ? 0 : mKeysList.size();
    }

    @Override
    public Object getItem(int i) {
        return mKeysList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_keyboard, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.btnKeys.setText(mKeysList.get(position).get("num"));
        if(position==11){//删除按键,设置文字为16个sp
            viewHolder.btnKeys.setTextSize(TypedValue.COMPLEX_UNIT_SP,16);
        }
        if(position==9){
            viewHolder.btnKeys.setEnabled(false);
            viewHolder.btnKeys.setClickable(false);
        }
        return convertView;
    }

    static class ViewHolder {
        @BindView(R.id.btn_keys)
        TextView btnKeys;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
