package com.liteon.icampusguardian.util;

import java.util.List;

import com.liteon.icampusguardian.R;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class SettingItemAdapter extends Adapter<SettingItemAdapter.ViewHolder> {

	private List<SettingItem> mDataset;
	
	public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public View mRootView;
        public TextView mTitleTextView;
        public TextView mValueTextView;
        public ImageView mMoreIcon;
        public ViewHolder(View v) {
            super(v);
            mRootView = v;
        }
    }

    public SettingItemAdapter(List<SettingItem> healthDataset) {
        mDataset = healthDataset;
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
        	holder.mValueTextView.setText("解除綁定");
        	holder.mMoreIcon.setVisibility(View.INVISIBLE);
        } else {
        	holder.mValueTextView.setText("");
        	holder.mMoreIcon.setVisibility(View.VISIBLE);
        }
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int arg1) {
		// create a new view
        View v = LayoutInflater.from(parent.getContext())
                               .inflate(R.layout.component_setting_item, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        vh.mTitleTextView = (TextView) v.findViewById(R.id.title_text);
        vh.mValueTextView = (TextView) v.findViewById(R.id.value_text);
        vh.mMoreIcon = (ImageView) v.findViewById(R.id.more_info_icon);

        return vh;
	}
}
