package com.liteon.icampusguardian.util;

import org.w3c.dom.Text;

import com.liteon.icampusguardian.R;

import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.AppCompatButton;
import android.text.TextUtils;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class ConfirmDeleteDialog extends DialogFragment {
	
	private TextView mTitle;
	private ImageView mIcon;
	private AppCompatButton mBtnConfirm;
	private AppCompatButton mBtnCancel;
	private OnClickListener mOnConfirmListener;
	private OnClickListener mOnCancelListener;
	private String mTitleText;
	private int mResId;
	private String mBtnConfirmText;
	private String mBtnCancelText;
	/**
	 * @param mTitleText the mTitleText to set
	 */
	public void setmTitleText(String mTitleText) {
		this.mTitleText = mTitleText;
	}

	/**
	 * @param mResId the mResId to set
	 */
	public void setmResId(int mResId) {
		this.mResId = mResId;
	}

	/**
	 * @param mBtnConfirmText the mBtnConfirmText to set
	 */
	public void setmBtnConfirmText(String mBtnConfirmText) {
		this.mBtnConfirmText = mBtnConfirmText;
	}

	/**
	 * @param mBtnCancelText the mBtnCancelText to set
	 */
	public void setmBtnCancelText(String mBtnCancelText) {
		this.mBtnCancelText = mBtnCancelText;
	}

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_confirm_delete, container);
        // This shows the title, replace My Dialog Title. You can use strings too.
        //getDialog().setTitle("使用者協議及隱私政策");
        // If you want no title, use this code

        findViews(view);
        setListener();
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        if (!TextUtils.isEmpty(mTitleText)) {
        	mTitle.setText(mTitleText);
        }
        
        if (!TextUtils.isEmpty(mBtnCancelText)) {
        	mBtnCancel.setText(mBtnCancelText);
        }
        
        if (!TextUtils.isEmpty(mBtnConfirmText)) {
        	mBtnConfirm.setText(mBtnConfirmText);
        }
        return view;
    }
	
	private void findViews(View rootView) {
		mTitle = (TextView) rootView.findViewById(R.id.title);
		mIcon = (ImageView) rootView.findViewById(R.id.icon_error);
        mBtnConfirm = (AppCompatButton) rootView.findViewById(R.id.confirm);
        mBtnCancel = (AppCompatButton) rootView.findViewById(R.id.cancel);
	}
	
	private void setListener() {
		mBtnConfirm.setOnClickListener(mOnConfirmListener);
		if (mOnCancelListener != null) {
			mBtnCancel.setOnClickListener(mOnCancelListener);
		} else {
			mBtnCancel.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					dismiss();
				}
			});
		}
	}
	
	public void setOnConfirmEventListener(OnClickListener listener) {
		mOnConfirmListener = listener;
	}
	
	public void setmOnCancelListener(OnClickListener onCancelListener) {
		mOnCancelListener = onCancelListener;
	}
	@Override
	public void onResume() {
		super.onResume();
		Display display = getActivity().getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		int width = size.x;
		int height = size.y;
		int window_size = width > height ? width / 2 : height / 2;
		getDialog().getWindow().setLayout(window_size, window_size);
	}
	
	
	
}
