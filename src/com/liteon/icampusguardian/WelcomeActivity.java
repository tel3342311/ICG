package com.liteon.icampusguardian;

import android.os.Bundle;
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
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_welcome);
	}
	@Override
	public void onClick(View v) {
		
	}

}
