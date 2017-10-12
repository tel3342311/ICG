package com.liteon.icampusguardian.util;

import android.support.v7.widget.AppCompatRadioButton;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.liteon.icampusguardian.R;
import com.liteon.icampusguardian.util.WeekAdapter.ViewHolder.IWeekViewHolderClicks;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class WeekAdapter extends Adapter<WeekAdapter.ViewHolder> {

	private List<WeekPeriodItem> mDataset;
	//Current updateing item;
	private final AlarmItem mAlarmItem;
	private static long mCurrentWeekValue;
    public WeakReference<IWeekViewHolderClicks> mClicks;

	public static class ViewHolder extends android.support.v7.widget.RecyclerView.ViewHolder implements android.view.View.OnClickListener{
        // each data item is just a string in this case
        public View mRootView;
        public TextView mTitleTextView;
        public TextView mValueTextView;
        public AppCompatRadioButton mRadioIcon;
        public WeekPeriodItem mItem;
        public WeakReference<IWeekViewHolderClicks> mClicks;
        public ViewHolder(View v, IWeekViewHolderClicks clicks) {
            super(v);
            mRootView = v;
            mClicks = new WeakReference<WeekAdapter.ViewHolder.IWeekViewHolderClicks>(clicks);
        }
		@Override
		public void onClick(View v) {
			
			mRadioIcon.setChecked(!mRadioIcon.isChecked());
			
			if (mRadioIcon.isChecked()) {
				mCurrentWeekValue |= mItem.getValue();
			} else {
				mCurrentWeekValue ^= mItem.getValue();
			}
			mClicks.get().onWeekItemClick(mCurrentWeekValue);
		}
		
		public static interface IWeekViewHolderClicks {
			public void onWeekItemClick(long value);
		}
    }

    public WeekAdapter(AlarmItem item, IWeekViewHolderClicks clicks) {
    	//create week list
    	mDataset = new ArrayList<>();
        for (WeekPeriodItem.TYPE type : WeekPeriodItem.TYPE.values()) {
        	WeekPeriodItem weekdays = new WeekPeriodItem();
        	weekdays.setItemType(type);
        	mDataset.add(weekdays);
        }
        mAlarmItem = item; 
        mCurrentWeekValue = item.getPeriodItem().getCustomValue();
        mClicks = new WeakReference<IWeekViewHolderClicks>(clicks);
    }
    
	@Override
	public int getItemCount() {
		return mDataset.size();
	}

	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		WeekPeriodItem item = mDataset.get(position);
        holder.mTitleTextView.setText(item.getTitle());
        if ((item.getValue() & mCurrentWeekValue) == item.getValue()) {
        	holder.mRadioIcon.setChecked(true);
        } else {
        	holder.mRadioIcon.setChecked(false);
        }
        holder.mItem = item;
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int arg1) {
		// create a new view
        View v = LayoutInflater.from(parent.getContext())
                               .inflate(R.layout.component_alarm_week_item, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v, mClicks.get());
        vh.mTitleTextView = (TextView) v.findViewById(R.id.title_text);
        vh.mRadioIcon = (AppCompatRadioButton) v.findViewById(R.id.radio_icon); 
        v.setOnClickListener(vh);
        return vh;
	}
}
