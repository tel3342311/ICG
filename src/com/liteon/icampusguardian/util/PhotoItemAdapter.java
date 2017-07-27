package com.liteon.icampusguardian.util;

import java.lang.ref.WeakReference;
import java.util.List;

import com.liteon.icampusguardian.R;
import com.liteon.icampusguardian.util.JSONResponse.Student;
import com.liteon.icampusguardian.util.PhotoItemAdapter.ViewHolder.IPhotoViewHolderClicks;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

public class PhotoItemAdapter extends Adapter<PhotoItemAdapter.ViewHolder> {

	private float photoSize;
	private List<PhotoItem> mDataset;
	public WeakReference<IPhotoViewHolderClicks> mClicks;
	private PhotoItem mCurrentUsedItem;

	
	public static class ViewHolder extends RecyclerView.ViewHolder implements OnClickListener {
        // each data item is just a string in this case
        public View mRootView;
        public ImageView mPhotoItem;
        public ImageView mCheckIcon;
        public PhotoItem mItem;
        public WeakReference<IPhotoViewHolderClicks> mClicks;
        public int position;

        public ViewHolder(View v, IPhotoViewHolderClicks clicks) {
            super(v);
            mRootView = v;
            mClicks = new WeakReference<IPhotoViewHolderClicks>(clicks);
        }
        
		public static interface IPhotoViewHolderClicks {
	        public void onPhotoClick(int position);
	    }

		@Override
		public void onClick(View v) {
			mClicks.get().onPhotoClick(position);
		}
    }

    public PhotoItemAdapter(List<PhotoItem> photoDataset, IPhotoViewHolderClicks clicks) {
        mDataset = photoDataset;
        mClicks = new WeakReference<PhotoItemAdapter.ViewHolder.IPhotoViewHolderClicks>(clicks);
    }
    
	@Override
	public int getItemCount() {
		return mDataset.size();
	}

	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		PhotoItem item = mDataset.get(position);
    	holder.position = position;
    	holder.mItem = item;
    	if (item.isChecked()) {
    		holder.mCheckIcon.setVisibility(View.VISIBLE);
    	} else {
    		holder.mCheckIcon.setVisibility(View.INVISIBLE);
    	}
    	//load bitmap 
    	Bitmap resized = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(item.getFilePath()), (int)photoSize, (int)photoSize);
    	holder.mPhotoItem.setImageBitmap(resized);
    	//crash issue, and need resize
    	//holder.mPhotoItem.setImageURI(item.getUri());
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int arg1) {
		// create a new view
        View v = LayoutInflater.from(parent.getContext())
                               .inflate(R.layout.component_photo_item, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v, mClicks.get());
        vh.mPhotoItem = (ImageView) v.findViewById(R.id.photo_item);
        vh.mCheckIcon = (ImageView) v.findViewById(R.id.select_icon);
        v.setOnClickListener(vh);
        photoSize = convertDpToPixel(parent.getResources().getDimension(R.dimen.choose_photo_item_size), parent.getContext());
        return vh;
	}
	
	public static float convertDpToPixel(float dp, Context context){
	    Resources resources = context.getResources();
	    DisplayMetrics metrics = resources.getDisplayMetrics();
	    float px = dp * ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
	    return px;
	}
}
