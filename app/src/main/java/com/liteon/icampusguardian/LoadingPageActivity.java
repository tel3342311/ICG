package com.liteon.icampusguardian;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.ProgressBar;

public class LoadingPageActivity extends AppCompatActivity {

	private ProgressBar mProgressBar;
	private Handler mHandlerTime;
	private int mProgressStep;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_loading_page);
		findViews();
		setListener();
		mHandlerTime = new Handler();
	}
	
	private void findViews() {
		mProgressBar = (ProgressBar) findViewById(R.id.loading_progress);
		
	}
	
	private void setListener() {

	}
	
	@Override
	protected void onResume() {
		super.onResume();
		mProgressStep = 0;
		mProgressBar.setProgress(0);
		mHandlerTime.postDelayed(UpdateProgress, 200);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		mHandlerTime.removeCallbacks(UpdateProgress);
	}
	
	public Runnable UpdateProgress = new Runnable()
    {
        public void run() {
        	if (mProgressStep >= 10) {
        		mProgressBar.setProgress(100);
        		onBackPressed();
        	} else {
        		mProgressStep++;
        		int progress =  mProgressStep * 100 / 10;
        		mProgressBar.setProgress(progress);
        		mHandlerTime.postDelayed(UpdateProgress, 200);
        	}
        }
    };
}
