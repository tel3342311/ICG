package com.liteon.icampusguardian;

import com.liteon.icampusguardian.util.JSONResponse.Student;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class ChildPairingActivity extends AppCompatActivity {

	private Student mStudent;
	private Animation mAnimation;
	private ImageView mPairingIndicator;
	private AppCompatButton mStartPairing;
	private AppCompatButton mPairingLater;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_child_pairing);
		findViews();
		setListener();
		setupFullScreen();
		setupAnimation();
	}
	
	private void setupFullScreen(){
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
		    Window window = getWindow();
		    // Translucent status bar
		    window.setFlags(
		    	WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, 
		    	WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
		    // Translucent navigation bar
		    window.setFlags(
		    	WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, 
		    	WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
		}
	}
	
	private void setupAnimation() {
		mAnimation = AnimationUtils.loadAnimation(this, R.anim.pairing_indicator_ani);
		mPairingIndicator.setAnimation(mAnimation);
	}
	private void findViews() {
		mPairingIndicator = (ImageView) findViewById(R.id.indicator_loading);
		mStartPairing = (AppCompatButton) findViewById(R.id.pairing_watch_now);
		mPairingLater = (AppCompatButton) findViewById(R.id.pairing_watch_later);
	}
	
	private void setListener() {
		mStartPairing.setOnClickListener(mOnClickListener);
		mPairingLater.setOnClickListener(mOnClickListener);
	}
	
	private View.OnClickListener mOnClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {

			Intent intent = new Intent();
			switch(v.getId()) {
			case R.id.pairing_watch_now:
				intent.setClass(ChildPairingActivity.this, BLEPairingListActivity.class);
				startActivity(intent);
				break;
			case R.id.pairing_watch_later:
				intent.setClass(ChildPairingActivity.this, MainActivity.class);
				startActivity(intent);
				finish();
				break;
			}
			
		}
		
	};
}
