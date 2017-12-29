package com.liteon.icampusguardian.util;

import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.liteon.icampusguardian.R;
import com.liteon.icampusguardian.util.AlarmItemAdapter.ViewHolder.IAlarmViewHolderClicks;

import java.lang.ref.WeakReference;
import java.util.List;

public class AlarmItemAdapter extends Adapter<AlarmItemAdapter.ViewHolder> {

    private List<AlarmItem> mDataset;
    private static boolean isEditMode;
    public WeakReference<IAlarmViewHolderClicks> mClicks;

    public static class ViewHolder extends android.support.v7.widget.RecyclerView.ViewHolder
            implements android.view.View.OnClickListener,
            android.widget.CompoundButton.OnCheckedChangeListener {
        // each data item is just a string in this case
        public View mRootView;
        public TextView mTitleTextView;
        public TextView mDateTextView;
        public TextView mPeriodTextView;
        public SwitchCompat mAlarmEnableView;
        public ImageView mItemIcon;
        public ImageView mMoreIcon;
        public View mIconArea;
        public WeakReference<IAlarmViewHolderClicks> mClicks;
        public int position;

        public ViewHolder(View v, IAlarmViewHolderClicks clicks) {
            super(v);
            mRootView = v;
            mClicks = new WeakReference<IAlarmViewHolderClicks>(clicks);
        }

        public static interface IAlarmViewHolderClicks {
            public void onEditAlarm(int position);

            public void onDeleteAlarm(int position);

            public void onEnableAlarm(int position, boolean enable);
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.icon_area) {
                if (isEditMode) {
                    //mClicks.get().onDeleteAlarm(position);
                    mClicks.get().onEditAlarm(position);
                } else {
                    mClicks.get().onEditAlarm(position);
                }
            } else {
                if (isEditMode) {
                    mClicks.get().onEditAlarm(position);
                }
            }
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            mClicks.get().onEnableAlarm(position, isChecked);
            mTitleTextView.setEnabled(isChecked);
            mDateTextView.setEnabled(isChecked);
            mPeriodTextView.setEnabled(isChecked);
            mAlarmEnableView.setChecked(isChecked);
        }
    }

    public AlarmItemAdapter(List<AlarmItem> alarmDataset, IAlarmViewHolderClicks clicks) {
        mDataset = alarmDataset;
        mClicks = new WeakReference<AlarmItemAdapter.ViewHolder.IAlarmViewHolderClicks>(clicks);
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        AlarmItem item = mDataset.get(position);
        holder.position = position;
        holder.mTitleTextView.setText(item.getTitle());
        holder.mDateTextView.setText(item.getDate());
        holder.mPeriodTextView.setText(item.getPeriod());
        //Set Enable state
        holder.mTitleTextView.setEnabled(item.getEnabled());
        holder.mDateTextView.setEnabled(item.getEnabled());
        holder.mPeriodTextView.setEnabled(item.getEnabled());
        holder.mAlarmEnableView.setChecked(item.getEnabled());
        if (isEditMode) {
            //holder.mItemIcon.setBackgroundResource(R.drawable.health_btnf_cancel);
            holder.mMoreIcon.setVisibility(View.VISIBLE);
            holder.mAlarmEnableView.setVisibility(View.INVISIBLE);
        } else {
            //holder.mItemIcon.setBackgroundResource(R.drawable.alarm_img_alarm);
            holder.mMoreIcon.setVisibility(View.INVISIBLE);
            holder.mAlarmEnableView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int arg1) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.component_alarm_item, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v, mClicks.get());
        vh.mItemIcon = (ImageView) v.findViewById(R.id.alarm_icon);
        vh.mTitleTextView = (TextView) v.findViewById(R.id.title_text);
        vh.mDateTextView = (TextView) v.findViewById(R.id.date_text);
        vh.mPeriodTextView = (TextView) v.findViewById(R.id.period_text);
        vh.mAlarmEnableView = (SwitchCompat) v.findViewById(R.id.switch_alarm_icon);
        vh.mMoreIcon = (ImageView) v.findViewById(R.id.more_info_icon);
        v.setOnClickListener(vh);
        //vh.mItemIcon.setOnClickListener(vh);
        vh.mAlarmEnableView.setOnCheckedChangeListener(vh);
        vh.mIconArea = v.findViewById(R.id.icon_area);
        vh.mIconArea.setOnClickListener(vh);
        return vh;
    }

    public void setEditMode(boolean b) {
        isEditMode = b;
        notifyDataSetChanged();
    }
}
