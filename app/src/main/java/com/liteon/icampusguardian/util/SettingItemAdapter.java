package com.liteon.icampusguardian.util;

import java.lang.ref.WeakReference;
import java.util.List;

import com.liteon.icampusguardian.App;
import com.liteon.icampusguardian.R;
import com.liteon.icampusguardian.util.JSONResponse.Student;
import com.liteon.icampusguardian.util.SettingItemAdapter.ViewHolder.ISettingItemClickListener;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

public class SettingItemAdapter extends Adapter<SettingItemAdapter.ViewHolder> {

	private List<SettingItem> mDataset;
    public WeakReference<ISettingItemClickListener> mClicks;
    private Student mStudent;
	public static class ViewHolder extends android.support.v7.widget.RecyclerView.ViewHolder implements android.view.View.OnClickListener{
        // each data item is just a string in this case
        public View mRootView;
        public TextView mTitleTextView;
        public AppCompatButton mValueBtn;
        public ImageView mMoreIcon;
        public WeakReference<ISettingItemClickListener> mClicks;
        public SettingItem.TYPE mType;
        public ViewHolder(View v, ISettingItemClickListener clicks) {
            super(v);
            mRootView = v;
            mClicks = new WeakReference<ISettingItemClickListener>(clicks);
        }
        
        public static interface ISettingItemClickListener {
        	public void onSettingItemClick(SettingItem.TYPE type);
        }

		@Override
		public void onClick(View v) {
			mClicks.get().onSettingItemClick(mType);
		}
    }

    public SettingItemAdapter(List<SettingItem> healthDataset, ISettingItemClickListener clicks) {
        mDataset = healthDataset;
        mClicks = new WeakReference<ISettingItemClickListener>(clicks);
    }
    
    public void setChildData(Student student) {
    	mStudent = student;
    }
	@Override
	public int getItemCount() {
		return mDataset.size();
	}

	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		SettingItem item = mDataset.get(position);
        holder.mTitleTextView.setText(item.getTitle());
        if (item.getItemType() == SettingItem.TYPE.PAIRING) {
        	holder.mValueBtn.setVisibility(View.VISIBLE);
            if (!TextUtils.isEmpty(mStudent.getUuid())) {
                holder.mValueBtn.setText(holder.mValueBtn.getResources().getString(R.string.unbind_watch));
            } else {
                holder.mValueBtn.setText(holder.mValueBtn.getResources().getString(R.string.bind_watch));
            }
//        	if (mStudent != null && !TextUtils.isEmpty(mStudent.getUuid())) {
//        		holder.mValueBtn.setText(holder.mValueBtn.getResources().getString(R.string.unbind_watch));
//        	} else {
//        		holder.mValueBtn.setText(holder.mValueBtn.getResources().getString(R.string.bind_watch));
//        	}
        	holder.mMoreIcon.setVisibility(View.INVISIBLE);
        } else {
        	holder.mValueBtn.setVisibility(View.INVISIBLE);
        	holder.mMoreIcon.setVisibility(View.VISIBLE);
        }
        holder.mType = item.getItemType();
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int arg1) {
		// create a new view
        View v = LayoutInflater.from(parent.getContext())
                               .inflate(R.layout.component_setting_item, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v, mClicks.get());
        vh.mTitleTextView = (TextView) v.findViewById(R.id.title_text);
        vh.mValueBtn = (AppCompatButton) v.findViewById(R.id.value_text);
        vh.mMoreIcon = (ImageView) v.findViewById(R.id.more_info_icon);
        v.setOnClickListener(vh);
        vh.mValueBtn.setOnClickListener(vh);
        return vh;
	}
}
