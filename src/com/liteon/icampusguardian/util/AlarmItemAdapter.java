package com.liteon.icampusguardian.util;

import java.util.List;

import com.liteon.icampusguardian.R;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class AlarmItemAdapter extends Adapter<AlarmItemAdapter.ViewHolder> {

	private List<AlarmItem> mDataset;
	
	public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public View mRootView;
        public TextView mTitleTextView;
        public TextView mDateTextView;
        public TextView mPeriodTextView;
        public SwitchCompat mAlarmEnableView;
        public ImageView mItemIcon;
        public ViewHolder(View v) {
            super(v);
            mRootView = v;
        }
    }

    public AlarmItemAdapter(List<AlarmItem> alarmDataset) {
        mDataset = alarmDataset;
    }
    
	@Override
	public int getItemCount() {
		return mDataset.size();
	}

	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
    	AlarmItem item = mDataset.get(position);
        holder.mTitleTextView.setText(item.getTitle());
        holder.mDateTextView.setText(item.getDate());
        holder.mPeriodTextView.setText(item.getPeriod());
        holder.mAlarmEnableView.setChecked(item.getEnabled());
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int arg1) {
		// create a new view
        View v = LayoutInflater.from(parent.getContext())
                               .inflate(R.layout.component_alarm_item, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        vh.mTitleTextView = (TextView) v.findViewById(R.id.title_text);
        vh.mDateTextView = (TextView) v.findViewById(R.id.date_text);
        vh.mPeriodTextView = (TextView) v.findViewById(R.id.period_text);
        vh.mAlarmEnableView = (SwitchCompat) v.findViewById(R.id.switch_alarm_icon);

        return vh;
	}
}
