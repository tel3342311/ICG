package com.liteon.icampusguardian.util;

import com.liteon.icampusguardian.R;

import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.AppCompatButton;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

public class CustomDialog extends DialogFragment {
	
	private TextView mTitle;
	private ImageView mIcon;
	private AppCompatButton mBtn;
	private String mTitleText;
	private int mResId;
	private String mBtnText;
	private View.OnClickListener mConfirmListener;
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_custom, container);
        // This shows the title, replace My Dialog Title. You can use strings too.
        //getDialog().setTitle("使用者協議及隱私政策");
        // If you want no title, use this code
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        findViews(view);
        return view;
    }
	
	private void findViews(View rootView) {
		mTitle = (TextView) rootView.findViewById(R.id.title);
		mIcon = (ImageView) rootView.findViewById(R.id.icon_error);
		mBtn = (AppCompatButton) rootView.findViewById(R.id.confirm);
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
		mTitle.setText(mTitleText);
		mBtn.setText(mBtnText);
		mIcon.setBackgroundResource(mResId);
		if (mConfirmListener!=null) {
			mBtn.setOnClickListener(mConfirmListener);
		}
	}
	
	public void setTitle(String title) {
		mTitleText = title;
	}
	
	public void setIcon(int resId) {
		mResId = resId;
	}
	
	public void setBtnText(String btn_text) {
		mBtnText = btn_text;
	}
	
	public void setBtnConfirm(View.OnClickListener mClickListener) {
		mConfirmListener = mClickListener;
	}
}
