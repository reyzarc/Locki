package com.xtec.locki.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xtec.locki.R;
import com.xtec.locki.model.AppInfo;
import com.xtec.locki.widget.FastDialog;
import com.xtec.locki.widget.SwitchView;

import java.util.List;

import de.halfbit.pinnedsection.PinnedSectionListView;

//�Զ����������࣬�ṩ��listView���Զ���view
public class BrowseApplicationInfoAdapter extends BaseAdapter implements PinnedSectionListView.PinnedSectionListAdapter{
	
	private List<AppInfo> mlistAppInfo = null;
	
	LayoutInflater infater = null;
	private Context mContext;
	private OnStatusChangedListener mOnstatusChangedListener;
    
	public BrowseApplicationInfoAdapter(Context context,  List<AppInfo> apps,OnStatusChangedListener onStatusChangedListener) {
		mContext = context;
		infater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mlistAppInfo = apps ;
		this.mOnstatusChangedListener = onStatusChangedListener;
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
//		System.out.println("size" + mlistAppInfo.size());
		return mlistAppInfo.size();
	}
	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mlistAppInfo.get(position);
	}
	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		ViewHolder holder ;
		if (convertView == null || convertView.getTag() == null) {
			convertView = infater.inflate(R.layout.item_app_info, null);
			holder = new ViewHolder(convertView);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag() ;
		}
		AppInfo appInfo = (AppInfo) getItem(position);
		if(appInfo.getType()==AppInfo.SECTION){
			holder.appIcon.setVisibility(View.GONE);
            holder.lockStatus.setVisibility(View.GONE);
            holder.tvAppLabel.setTextColor(mContext.getResources().getColor(R.color.white));
			holder.llRoot.setBackgroundColor(mContext.getResources().getColor(R.color.red_f3323b));
		}else{
			holder.appIcon.setVisibility(View.VISIBLE);
            holder.lockStatus.setVisibility(View.VISIBLE);
            holder.llRoot.setBackgroundColor(mContext.getResources().getColor(R.color.white));
		}

		holder.appIcon.setImageDrawable(appInfo.getAppIcon());
		holder.tvAppLabel.setText(appInfo.getAppLabel());

		holder.lockStatus.setOpened(appInfo.isOpened());

		holder.lockStatus.setOnStateChangedListener(new SwitchView.OnStateChangedListener() {
			@Override
			public void toggleToOn(View view) {
				((SwitchView)view).toggleSwitch(true);
				appInfo.setOpened(true);
				mOnstatusChangedListener.onStatusChange(appInfo);
			}

			@Override
			public void toggleToOff(View view) {
				new FastDialog(mContext)
						.setTitle("提示")
						.setContent("关闭密码锁功能将导致应用安全性降低,存在隐私泄露的风险,\n您确定要关闭吗")
						.setNegativeButton("取消", new FastDialog.OnClickListener() {
							@Override
							public void onClick(FastDialog dialog) {
								((SwitchView)view).toggleSwitch(true);
								appInfo.setOpened(true);

							}
						})
						.setPositiveButton("确定", new FastDialog.OnClickListener() {
							@Override
							public void onClick(FastDialog dialog) {
								((SwitchView)view).toggleSwitch(false);
								appInfo.setOpened(false);
								mOnstatusChangedListener.onStatusChange(appInfo);
							}
						}).create().show();
			}
		});
		return convertView;
	}

	@Override
	public boolean isItemViewTypePinned(int viewType) {
		return viewType== AppInfo.SECTION;
	}

	@Override public int getItemViewType(int position) {
		return ((AppInfo)getItem(position)).getType();
	}

	@Override
	public int getViewTypeCount() {
		return 2;
	}

	class ViewHolder {
		ImageView appIcon;
		TextView tvAppLabel;
		TextView tvPkgName;
		LinearLayout llRoot;
        SwitchView lockStatus;

		public ViewHolder(View view) {
			this.appIcon = (ImageView) view.findViewById(R.id.imgApp);
			this.tvAppLabel = (TextView) view.findViewById(R.id.tvAppLabel);
			this.tvPkgName = (TextView) view.findViewById(R.id.tvPkgName);
			this.llRoot = (LinearLayout) view.findViewById(R.id.ll_root);
            this.lockStatus = (SwitchView) view.findViewById(R.id.lock_status);
		}
	}

	public interface OnStatusChangedListener{
		void onStatusChange(AppInfo appInfo);
	}
}