package com.liteon.icampusguardian.util;

import java.lang.ref.WeakReference;
import java.util.List;

import com.liteon.icampusguardian.R;
import com.liteon.icampusguardian.util.AppInfoPrivacyItemAdapter.ViewHolder.IAppInfoPrivacyViewHolderClicks;

import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class AppInfoPrivacyItemAdapter extends Adapter<AppInfoPrivacyItemAdapter.ViewHolder> {

	private WeakReference<IAppInfoPrivacyViewHolderClicks> mClickListener;
	private List<AlarmPeriodItem> mDataset;
	//Current updateing item;
	private AlarmItem mAlarmItem;
	public static class ViewHolder extends RecyclerView.ViewHolder implements OnClickListener{
        // each data item is just a string in this case
        public View mRootView;
        public TextView mTitleTextView;
        public TextView mValueTextView;
        public ImageView mMoreIcon;
        private AppInfoPrivacyItem mItem;
        public WeakReference<IAppInfoPrivacyViewHolderClicks> mClicks;
        public ViewHolder(View v, IAppInfoPrivacyViewHolderClicks click) {
            super(v);
            mRootView = v;
            mClicks = new WeakReference<AppInfoPrivacyItemAdapter.ViewHolder.IAppInfoPrivacyViewHolderClicks>(click);
        }
		@Override
		public void onClick(View v) {
			mClicks.get().onClick(mItem);			
		}
		
		public static interface IAppInfoPrivacyViewHolderClicks {
	        public void onClick(AppInfoPrivacyItem item);
	    }
    }

    public AppInfoPrivacyItemAdapter(List<AlarmPeriodItem> alarmDataset, IAppInfoPrivacyViewHolderClicks clicks, AlarmItem item) {
        mDataset = alarmDataset;
        mClickListener = new WeakReference<IAppInfoPrivacyViewHolderClicks>(clicks);
        mAlarmItem = item; 
    }
    
	@Override
	public int getItemCount() {
		return mDataset.size();
	}

	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		AlarmPeriodItem item = mDataset.get(position);
        holder.mTitleTextView.setText(item.getTitle());
        if (item.getItemType() == AlarmPeriodItem.TYPE.CUSTOMIZE) {
        	holder.mMoreIcon.setVisibility(View.VISIBLE);
        } else {
        	holder.mMoreIcon.setVisibility(View.INVISIBLE);
        }
        if (mAlarmItem.getPeriodItem().getItemType() == item.getItemType()) {
        	holder.mTitleTextView.setTextColor(holder.mTitleTextView.getResources().getColor(R.color.color_accent));
        }
        holder.mItem = item;
        mAlarmItem.PeriodItem = item;
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int arg1) {
		// create a new view
        View v = LayoutInflater.from(parent.getContext())
                               .inflate(R.layout.component_alarm_period_item, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v, mClickListener.get());
        vh.mTitleTextView = (TextView) v.findViewById(R.id.title_text);
        vh.mValueTextView = (TextView) v.findViewById(R.id.value_text);
        vh.mMoreIcon = (ImageView) v.findViewById(R.id.more_info_icon);
        v.setOnClickListener(vh);
        return vh;
	}
	
	public void setAlarmItem(AlarmItem item) {
		mAlarmItem = item;
	}
}
