package com.liteon.icampusguardian.util;

import java.util.ArrayList;
import java.util.List;

import com.liteon.icampusguardian.R;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class GeoEventAdapter extends Adapter<GeoEventAdapter.ViewHolder> {

	private List<GeoEventItem> mDataset;
	
	public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public View mRootView;
        public TextView mDateTextView;
        public TextView mEnterSchool;
        public TextView mLeaveSchool;
        public TextView mEmergencyCall;
        public TextView mEmergencyRelease;
        public ImageView mCollapseIcon;
        
        public ViewHolder(View v) {
            super(v);
            mRootView = v;
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public GeoEventAdapter(List<GeoEventItem> geoDataset) {
        mDataset = geoDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public GeoEventAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                               .inflate(R.layout.component_geo_event, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        vh.mDateTextView = (TextView) v.findViewById(R.id.title_text);
        vh.mEnterSchool = (TextView) v.findViewById(R.id.enter_text);
        vh.mLeaveSchool = (TextView) v.findViewById(R.id.leave_text);
        vh.mEmergencyCall = (TextView) v.findViewById(R.id.emergency_text);
        vh.mEmergencyRelease = (TextView) v.findViewById(R.id.emergency_release_text);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

    	GeoEventItem item = mDataset.get(position);
        holder.mDateTextView.setText(item.getDate());
        holder.mEnterSchool.setText(item.getEnterSchool());
        holder.mLeaveSchool.setText(item.getLeavelSchool());
        String emergency = item.getEmergency();
        if (!TextUtils.isEmpty(emergency)) {
        	holder.mEmergencyCall.setText(emergency);
        	holder.mEmergencyCall.setVisibility(View.VISIBLE);
        } else {
        	holder.mEmergencyCall.setVisibility(View.GONE);
        }
        String emergencyRelease = item.getEmergencyRelease();
        if (!TextUtils.isEmpty(emergency)) {
        	holder.mEmergencyRelease.setText(emergencyRelease);
        	holder.mEmergencyRelease.setVisibility(View.VISIBLE);
        } else {
        	holder.mEmergencyRelease.setVisibility(View.GONE);
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
