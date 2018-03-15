package com.liteon.icampusguardian.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.AppCompatButton;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.liteon.icampusguardian.R;

public class SelectURLDialog extends DialogFragment {
	private AppCompatButton mBtnURL1;
	private AppCompatButton mBtnURL2;
	private View.OnClickListener mConfirmListener;
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_select_url, container);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        findViews(view);
        setListener();
        return view;
    }
	
	private void findViews(View rootView) {

		mBtnURL1 = rootView.findViewById(R.id.url1);
		mBtnURL2 = rootView.findViewById(R.id.url2);
	}

	private void setListener() {
		mBtnURL1.setOnClickListener( v -> {
			setURL(Def.URL_3rd_party);
			if (mConfirmListener != null) {
				mConfirmListener.onClick(v);
			}
		});
		mBtnURL2.setOnClickListener( v -> {
			setURL(Def.URL_internal);
			if (mConfirmListener != null) {
				mConfirmListener.onClick(v);
			}
		});
	}

	private void setURL(String url) {
		SharedPreferences sp = getContext().getSharedPreferences(Def.SHARE_PREFERENCE, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putString(Def.SP_URL, url);
		editor.commit();
		GuardianApiClient.setServerUri(url);
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

	public void setBtnConfirm(View.OnClickListener mClickListener) {
		mConfirmListener = mClickListener;
	}
}
