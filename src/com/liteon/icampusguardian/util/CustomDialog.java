package com.liteon.icampusguardian.util;

import com.liteon.icampusguardian.R;

import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

public class CustomDialog extends DialogFragment {
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_custom, container);
        // This shows the title, replace My Dialog Title. You can use strings too.
        //getDialog().setTitle("使用者協議及隱私政策");
        // If you want no title, use this code
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        return view;
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
