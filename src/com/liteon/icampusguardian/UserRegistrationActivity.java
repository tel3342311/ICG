package com.liteon.icampusguardian;

import java.util.UUID;

import com.liteon.icampusguardian.util.GuardianApiClient;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;

public class UserRegistrationActivity extends AppCompatActivity implements OnClickListener {

	
	private ImageView mCancel;
	private ImageView mConfirm;
	private EditText mName;
	private EditText mPhone;
	private EditText mAccount;
	private EditText mPassword;
	private EditText mConfirmPassword;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_registration);
		findViews();
		setListener();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		mConfirm.setEnabled(false);
	}
	
	private void findViews() {
		mName = (EditText) findViewById(R.id.login_name);
		mPhone = (EditText) findViewById(R.id.login_phone);
		mAccount = (EditText) findViewById(R.id.login_account);
		mPassword = (EditText) findViewById(R.id.login_password);
		mConfirmPassword = (EditText) findViewById(R.id.login_password_confirm);
		mCancel = (ImageView) findViewById(R.id.cancel);
		mConfirm = (ImageView) findViewById(R.id.confirm);
	}
	
	private void setListener() {
		mCancel.setOnClickListener(this);
		mConfirm.setOnClickListener(this);
	}
	private TextWatcher mTextWatcher = new TextWatcher() {
		
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			
		}
		
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			
		}
		
		@Override
		public void afterTextChanged(Editable s) {
			if (validateInput()) {
				mConfirm.setEnabled(true);
			} else {
				mConfirm.setEnabled(false);
			}
		}
	};
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.confirm:
			registerAccount();
			finish();
			break;
		case R.id.cancel:
			finish();
			break;
		}
	}
	
	private boolean validateInput() {
		if (TextUtils.isEmpty(mName.getText()) || 
			TextUtils.isEmpty(mPhone.getText()) || 
			TextUtils.isEmpty(mAccount.getText()) || 
			TextUtils.isEmpty(mPassword.getText()) ||
			TextUtils.isEmpty(mConfirmPassword.getText())) {
			return false;
		}
		//check if acount email is valid
		if (!Patterns.EMAIL_ADDRESS.matcher(mAccount.getText()).matches()) {
			return false;
		}
		//check if password & password confirm is match
		if (!TextUtils.equals(mPassword.getText(), mConfirmPassword.getText())) {
			return false;
		}
		return true;
	}
	
	private void registerAccount() {
		String strName = mName.getText().toString();
		String strPhone = mPhone.getText().toString();
		String strAccount = mAccount.getText().toString();
		String strPassword = mPassword.getText().toString();
		
		GuardianApiClient apiClient = new GuardianApiClient(this);
		String str = "1234";
		UUID uuid = UUID.nameUUIDFromBytes(str.getBytes());
		apiClient.registerUser(strAccount, strPassword, "parent_admin", uuid.toString(), strName);
	}

}
