package com.liteon.icampusguardian.util;

import java.lang.ref.WeakReference;
import java.util.List;

import com.liteon.icampusguardian.R;
import com.liteon.icampusguardian.util.BLEItemAdapter.ViewHolder.IBLEItemClickListener;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class BLEItemAdapter extends Adapter<BLEItemAdapter.ViewHolder> {

	private List<BLEItem> mDataset;
    public WeakReference<IBLEItemClickListener> mClicks;

	public static class ViewHolder extends RecyclerView.ViewHolder implements OnClickListener{
        // each data item is just a string in this case
        public View mRootView;
        public TextView mTitleTextView;
        public TextView mIdTextView;
        public TextView mStateTextView;
        public ImageView mMoreIcon;
        public WeakReference<IBLEItemClickListener> mClicks;
        public BLEItem mItem;
        public ViewHolder(View v, IBLEItemClickListener clicks) {
            super(v);
            mRootView = v;
            mClicks = new WeakReference<IBLEItemClickListener>(clicks);
        }
        
        public static interface IBLEItemClickListener {
        	public void onBleItemClick(BLEItem item);
        }

		@Override
		public void onClick(View v) {
			mClicks.get().onBleItemClick(mItem);
		}
    }

    public BLEItemAdapter(List<BLEItem> dataset, IBLEItemClickListener clicks) {
        mDataset = dataset;
        mClicks = new WeakReference<IBLEItemClickListener>(clicks);
    }
    
	@Override
	public int getItemCount() {
		return mDataset.size();
	}

	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		BLEItem item = mDataset.get(position);
        holder.mTitleTextView.setText(item.getName());
        holder.mIdTextView.setText(item.getId());
        holder.mStateTextView.setText(item.getValue());
        holder.mItem = item;
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int arg1) {
		// create a new view
        View v = LayoutInflater.from(parent.getContext())
                               .inflate(R.layout.component_ble_item, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v, mClicks.get());
        vh.mTitleTextView = (TextView) v.findViewById(R.id.title_text);
        vh.mIdTextView = (TextView) v.findViewById(R.id.uuid_text);
        vh.mStateTextView = (TextView) v.findViewById(R.id.connect_text);
        v.setOnClickListener(vh);
        return vh;
	}
}
