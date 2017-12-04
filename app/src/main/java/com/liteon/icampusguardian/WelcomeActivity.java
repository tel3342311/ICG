package com.liteon.icampusguardian;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatCheckBox;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.liteon.icampusguardian.util.Def;

public class WelcomeActivity extends AppCompatActivity implements OnClickListener {

	
	private TextView mTextViewUserTerm;
	private AppCompatCheckBox mRadioButtonImprove;
	private AppCompatCheckBox mRadioButtonTeacher;
	private TextView mQuit;
	private TextView mAgree;
	private TextView mUserTerm;
	private DialogFragment mDialog;
	
	private static final int REQUEST_USERTERM = 1;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_welcome);
		findViews();
		setListener();
	}
	
	private void findViews() {
		mQuit = findViewById(R.id.option_quit);
		mAgree = findViewById(R.id.option_agree);
		mUserTerm = findViewById(R.id.user_term_click);
		mRadioButtonImprove = findViewById(R.id.user_improve_plan);
		mRadioButtonTeacher = findViewById(R.id.user_teacher_plan);
	}
	
	private void setListener() {
		mAgree.setOnClickListener(this);
		mQuit.setOnClickListener(this);
		mUserTerm.setOnClickListener(this);
		mRadioButtonImprove.setOnCheckedChangeListener(mOnCheckChangeListener);
		mRadioButtonTeacher.setOnCheckedChangeListener(mOnCheckChangeListener);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		SharedPreferences sp = getSharedPreferences(Def.SHARE_PREFERENCE, Context.MODE_PRIVATE);
		boolean imporve = sp.getBoolean(Def.SP_IMPROVE_PLAN, false);
		mRadioButtonImprove.setChecked(imporve);
	}
	
	private OnCheckedChangeListener mOnCheckChangeListener = new OnCheckedChangeListener() {
		
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			SharedPreferences sp = getSharedPreferences(Def.SHARE_PREFERENCE, Context.MODE_PRIVATE);
			Editor editor = sp.edit();
			if (buttonView.getId() == R.id.user_improve_plan) {
				editor.putBoolean(Def.SP_IMPROVE_PLAN, isChecked);
			} else if (buttonView.getId() == R.id.user_teacher_plan) {
				editor.putBoolean(Def.SP_TEACHER_PLAN, isChecked);
			}
			editor.commit();
			
		}
	};
	
	@Override
	public void onClick(View v) {
		Intent intent = new Intent();
		switch (v.getId()) {
		case R.id.option_agree:
			SharedPreferences sp = getSharedPreferences(Def.SHARE_PREFERENCE, Context.MODE_PRIVATE);
			SharedPreferences.Editor editor = sp.edit();
			editor.putInt(Def.SP_USER_TERM_READ, 1);
			editor.commit();
			
			setResult(RESULT_OK);
			finish();
			break;
		case R.id.option_quit:
			setResult(RESULT_CANCELED);
			Intent startMain = new Intent(Intent.ACTION_MAIN);
			startMain.addCategory(Intent.CATEGORY_HOME);
			startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(startMain);
			break;
		case R.id.user_term_click:
			intent.setClass(this, UserTermActivity.class);
			startActivityForResult(intent, REQUEST_USERTERM);
			break;
		default:
			break;
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if (requestCode == REQUEST_USERTERM) {
			if (RESULT_OK == resultCode){
				setResult(RESULT_OK);
				finish();
			}
		}
	}

}
