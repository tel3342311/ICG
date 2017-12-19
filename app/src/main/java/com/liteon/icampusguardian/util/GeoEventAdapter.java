package com.liteon.icampusguardian.util;

import android.graphics.Matrix;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.liteon.icampusguardian.App;
import com.liteon.icampusguardian.R;

import java.util.List;

public class GeoEventAdapter extends Adapter<GeoEventAdapter.ViewHolder> {

	private List<GeoEventItem> mDataset;
	
	public static class ViewHolder extends RecyclerView.ViewHolder implements OnClickListener {
        // each data item is just a string in this case
        public View mRootView;
        public TextView mDateTextView;
        public TextView mEnterSchool;
        public TextView mLeaveSchool;
        public TextView mEmergencyCall;
        public TextView mEmergencyRelease;
        public ImageView mCollapseIcon;
        public View mDetailInfo;
        public ViewHolder(View v) {
            super(v);
            mRootView = v;
        }
        
        @Override
        public void onClick(View v) {
        	if (mDetailInfo.getVisibility() == View.VISIBLE) {
        		mDetailInfo.setVisibility(View.GONE);
        		rotateCollapseIcon(mCollapseIcon, 0);
        	} else {
        		mDetailInfo.setVisibility(View.VISIBLE);
        		rotateCollapseIcon(mCollapseIcon, 180);
        	}
        }
        
        private void rotateCollapseIcon(ImageView imageView, int angle) {

        	Matrix matrix = new Matrix();
        	imageView.setScaleType(ImageView.ScaleType.MATRIX);   //required
        	matrix.postRotate((float) angle, imageView.getWidth()/2, imageView.getHeight()/2);
        	imageView.setImageMatrix(matrix);
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
        vh.mDateTextView = v.findViewById(R.id.title_text);
        vh.mEnterSchool = v.findViewById(R.id.enter_text);
        vh.mLeaveSchool = v.findViewById(R.id.leave_text);
        vh.mEmergencyCall = v.findViewById(R.id.emergency_text);
        vh.mEmergencyRelease = v.findViewById(R.id.emergency_release_text);
        vh.mDetailInfo = v.findViewById(R.id.detail_info);
        vh.mDetailInfo.setVisibility(View.GONE);
        vh.mCollapseIcon = v.findViewById(R.id.arrow);
        vh.mCollapseIcon.setOnClickListener(vh);

        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

    	GeoEventItem item = mDataset.get(position);
        holder.mDateTextView.setText(item.getDate());
        if (!TextUtils.isEmpty(item.getEnterSchool())) {
            holder.mEnterSchool.setText(item.getEnterSchool());
        } else {
            holder.mEnterSchool.setText(App.getContext().getText(R.string.safty_no_watch_detected));
        }

        if (!TextUtils.isEmpty(item.getLeavelSchool())) {
            holder.mLeaveSchool.setText(item.getLeavelSchool());
        } else {
            holder.mLeaveSchool.setText(App.getContext().getText(R.string.safty_no_watch_detected));
        }
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
