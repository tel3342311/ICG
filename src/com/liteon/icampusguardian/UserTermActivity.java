package com.liteon.icampusguardian;

import com.liteon.icampusguardian.util.Def;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.SensorEventListener;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class UserTermActivity extends AppCompatActivity implements OnClickListener {

	
	private TextView mAgree;
	private TextView mQuit;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_userterm);
		findViews();
		setListener();
	}
	
	private void findViews() {
		mAgree = (TextView) findViewById(R.id.option_agree);
		mQuit = (TextView) findViewById(R.id.option_quit);
		
	}
	
	private void setListener() {
		mAgree.setOnClickListener(this);
		mQuit.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.option_agree:
			userTermRead();
			break;
		case R.id.option_quit:
			finish();
			break;
		}
	}
	private void userTermRead() {	
		Intent intent = new Intent();
		intent.setClass(this, LoginActivity.class);
		startActivity(intent);
	}
}
