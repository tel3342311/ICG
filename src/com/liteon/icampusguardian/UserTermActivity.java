package com.liteon.icampusguardian;

import com.liteon.icampusguardian.util.Def;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

public class UserTermActivity extends AppCompatActivity implements OnClickListener {

	
	private TextView mAgree;
	private TextView mQuit;
	private Toolbar mToolbar;
	private View mBottomView;
	private AppCompatCheckBox mRadioButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_userterm);
		findViews();
		setupToolbar();
		setListener();
	}
	
	private void findViews() {
		mAgree = (TextView) findViewById(R.id.option_agree);
		mQuit = (TextView) findViewById(R.id.option_quit);
		mToolbar = (Toolbar) findViewById(R.id.toolbar);
		mBottomView = (View) findViewById(R.id.bottom_bar); 
		mRadioButton = (AppCompatCheckBox) findViewById(R.id.user_improve_plan);
	}
	
	private void setupToolbar() {
		setSupportActionBar(mToolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);
		mToolbar.setNavigationIcon(R.drawable.ic_navigate_before_white_24dp);
		mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				onBackPressed();
			}
		});
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		mToolbar.setTitle("使用者協議及隱私政策");
		Intent intent = getIntent();
		boolean disableBottom = intent.getBooleanExtra(Def.EXTRA_DISABLE_USERTREM_BOTTOM, false);
		if (disableBottom == true) {
			mBottomView.setVisibility(View.INVISIBLE);
		} else {
			mBottomView.setVisibility(View.VISIBLE);
		}
		SharedPreferences sp = getSharedPreferences(Def.SHARE_PREFERENCE, Context.MODE_PRIVATE);
		boolean imporve = sp.getBoolean(Def.SP_IMPROVE_PLAN, false);
		mRadioButton.setChecked(imporve);
		
	}
	
	private void setListener() {
		mAgree.setOnClickListener(this);
		mQuit.setOnClickListener(this);
		mRadioButton.setOnCheckedChangeListener(mOnCheckChangeListener);
	}
	
	private OnCheckedChangeListener mOnCheckChangeListener = new OnCheckedChangeListener() {
		
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			SharedPreferences sp = getSharedPreferences(Def.SHARE_PREFERENCE, Context.MODE_PRIVATE);
			Editor editor = sp.edit();
			editor.putBoolean(Def.SP_IMPROVE_PLAN, isChecked);
			editor.commit();
			
		}
	};
	
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
