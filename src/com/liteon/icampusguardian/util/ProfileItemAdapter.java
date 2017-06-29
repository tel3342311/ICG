package com.liteon.icampusguardian.util;

import java.lang.ref.WeakReference;
import java.util.List;

import com.liteon.icampusguardian.R;
import com.liteon.icampusguardian.util.ProfileItemAdapter.ViewHolder.IProfileItemClickListener;
import com.liteon.icampusguardian.util.SettingItemAdapter.ViewHolder.ISettingItemClickListener;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class ProfileItemAdapter extends Adapter<ProfileItemAdapter.ViewHolder> {

	private List<ProfileItem> mDataset;
    public WeakReference<IProfileItemClickListener> mClicks;

	public static class ViewHolder extends RecyclerView.ViewHolder implements OnClickListener{
        // each data item is just a string in this case
        public View mRootView;
        public TextView mTitleTextView;
        public TextView mValueTextView;
        public ImageView mMoreIcon;
        public WeakReference<IProfileItemClickListener> mClicks;
        public ProfileItem.TYPE mType;
        public ViewHolder(View v, IProfileItemClickListener clicks) {
            super(v);
            mRootView = v;
            mClicks = new WeakReference<IProfileItemClickListener>(clicks);
        }
        
        public static interface IProfileItemClickListener {
        	public void onProfileItemClick(ProfileItem.TYPE type);
        }

		@Override
		public void onClick(View v) {
			mClicks.get().onProfileItemClick(mType);
		}
    }

    public ProfileItemAdapter(List<ProfileItem> dataset, IProfileItemClickListener clicks) {
        mDataset = dataset;
        mClicks = new WeakReference<IProfileItemClickListener>(clicks);
    }
    
	@Override
	public int getItemCount() {
		return mDataset.size();
	}

	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		ProfileItem item = mDataset.get(position);
        holder.mTitleTextView.setText(item.getTitle());
        holder.mValueTextView.setText(item.getValue());
        holder.mType = item.getItemType();
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int arg1) {
		// create a new view
        View v = LayoutInflater.from(parent.getContext())
                               .inflate(R.layout.component_profile_item, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v, mClicks.get());
        vh.mTitleTextView = (TextView) v.findViewById(R.id.title_text);
        vh.mValueTextView = (TextView) v.findViewById(R.id.value_text);
        v.setOnClickListener(vh);
        return vh;
	}
}
