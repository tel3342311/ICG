package com.liteon.icampusguardian.util;

import java.lang.ref.WeakReference;
import java.util.List;

import com.liteon.icampusguardian.R;
import com.liteon.icampusguardian.util.WeekAdapter.ViewHolder.IAlarmPeriodViewHolderClicks;
import com.liteon.icampusguardian.util.AlarmPeriodItem.TYPE;
import com.liteon.icampusguardian.util.HealthyItemAdapter.ViewHolder.IHealthViewHolderClicks;

import android.support.v7.widget.AppCompatRadioButton;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class WeekAdapter extends Adapter<WeekAdapter.ViewHolder> {

	private List<WeekPeriodItem> mDataset;
	//Current updateing item;
	private AlarmItem mAlarmItem;
	public static class ViewHolder extends RecyclerView.ViewHolder implements OnClickListener{
        // each data item is just a string in this case
        public View mRootView;
        public TextView mTitleTextView;
        public TextView mValueTextView;
        public AppCompatRadioButton mRadioIcon;
        private TYPE mType;
        public WeakReference<IAlarmPeriodViewHolderClicks> mClicks;
        public ViewHolder(View v) {
            super(v);
            mRootView = v;
        }
		@Override
		public void onClick(View v) {
			
			mClicks.get().onClick(mType);			
		}
		
		public static interface IAlarmPeriodViewHolderClicks {
	        public void onClick(AlarmPeriodItem.TYPE type);
	    }
    }

    public WeekAdapter(AlarmItem item) {
        for (WeekPeriodItem.TYPE type : WeekPeriodItem.TYPE.values()) {
        	WeekPeriodItem weekdays = new WeekPeriodItem();
        	weekdays.setItemType(type);
        	mDataset.add(weekdays);
        }
        mAlarmItem = item; 
    }
    
	@Override
	public int getItemCount() {
		return mDataset.size();
	}

	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		WeekPeriodItem item = mDataset.get(position);
        holder.mTitleTextView.setText(item.getTitle());

	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int arg1) {
		// create a new view
        View v = LayoutInflater.from(parent.getContext())
                               .inflate(R.layout.component_alarm_period_item, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        vh.mTitleTextView = (TextView) v.findViewById(R.id.title_text);
        vh.mRadioIcon = (AppCompatRadioButton) v.findViewById(R.id.radio_icon);
        v.setOnClickListener(vh);
        return vh;
	}
	
	public void setAlarmItem(AlarmItem item) {
		mAlarmItem = item;
	}
}
