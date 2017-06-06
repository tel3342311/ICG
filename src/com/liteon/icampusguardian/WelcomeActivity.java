package com.liteon.icampusguardian;

import com.liteon.icampusguardian.util.CustomDialog;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatRadioButton;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class WelcomeActivity extends AppCompatActivity implements OnClickListener {

	
	private TextView mTextViewUserTerm;
	private AppCompatRadioButton mRadioButtonImprove;
	private AppCompatRadioButton mRadioButtonTeacher;
	private TextView mQuit;
	private TextView mAgree;
	private TextView mUserTerm;
	private DialogFragment mDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_welcome);
		findViews();
		setListener();
	}
	
	private void findViews() {
		mQuit = (TextView) findViewById(R.id.option_quit);
		mAgree = (TextView) findViewById(R.id.option_agree);
		mUserTerm = (TextView) findViewById(R.id.user_term_click);
	}
	
	private void setListener() {
		mAgree.setOnClickListener(this);
		mQuit.setOnClickListener(this);
		mUserTerm.setOnClickListener(this);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}
	
	@Override
	public void onClick(View v) {
		Intent intent = new Intent();
		switch (v.getId()) {
		case R.id.option_agree:

			intent.setClass(this, LoginActivity.class);
			startActivity(intent);
			break;
		case R.id.option_quit:
			finish();
			break;
		case R.id.user_term_click:
			intent.setClass(this, UserTermActivity.class);
			startActivity(intent);
			break;
		default:
			break;
		}
	}

}
