package com.liteon.icampusguardian;

import java.util.UUID;

import com.liteon.icampusguardian.util.CustomDialog;
import com.liteon.icampusguardian.util.GuardianApiClient;

import android.os.AsyncTask;
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
import android.widget.TextView;
import android.widget.Toast;

public class UserRegistrationActivity extends AppCompatActivity implements OnClickListener {

	
	private ImageView mCancel;
	private ImageView mConfirm;
	private EditText mName;
	private EditText mPhone;
	private EditText mAccount;
	private EditText mPassword;
	private EditText mConfirmPassword;
	private TextView mHintText;
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
		mHintText = (TextView) findViewById(R.id.error_hint);
		
	}
	
	private void setListener() {
		mCancel.setOnClickListener(this);
		mConfirm.setOnClickListener(this);
		
		mName.addTextChangedListener(mTextWatcher);
		mPhone.addTextChangedListener(mTextWatcher);
		mAccount.addTextChangedListener(mTextWatcher);
		mPassword.addTextChangedListener(mTextWatcher);
		mConfirmPassword.addTextChangedListener(mTextWatcher);
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
			break;
		case R.id.cancel:
			finish();
			break;
		}
	}
	
	private boolean validateInput() {
		//check if acount email is valid
		String email = mAccount.getText().toString();
		email = email.trim();
		if ( !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
			if (!TextUtils.isEmpty(mAccount.getText())) {
				mAccount.setTextColor(getResources().getColor(R.color.md_red_400));
				mHintText.setVisibility(View.VISIBLE);
			}
			return false;
		} else {
			mAccount.setTextColor(getResources().getColor(R.color.md_black_1000));
			mHintText.setVisibility(View.INVISIBLE);
		}
		if (TextUtils.isEmpty(mName.getText()) || 
			TextUtils.isEmpty(mPhone.getText()) ||  
			TextUtils.isEmpty(mPassword.getText()) ||
			TextUtils.isEmpty(mConfirmPassword.getText())) {
			return false;
		}
		

		return true;
	}
	
	private void showLoginErrorDialog() {
		final CustomDialog dialog = new CustomDialog();
		dialog.setTitle(getString(R.string.login_error_email));
		dialog.setIcon(R.drawable.ic_error_outline_black_24dp);
		dialog.setBtnText(getString(android.R.string.ok));
		dialog.setBtnConfirm(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		dialog.show(getSupportFragmentManager(), "dialog_fragment");
	}
	private void registerAccount() {
		String strName = mName.getText().toString();
		String strPhone = mPhone.getText().toString();
		String strAccount = mAccount.getText().toString();
		strAccount = strAccount.trim();
		String strPassword = mPassword.getText().toString();
		
		//check if password & password confirm is match
		if ((strPassword.length() < 8) || !TextUtils.equals(mPassword.getText(), mConfirmPassword.getText())) {
			showLoginErrorDialog();
			return ;
		}
		GuardianApiClient apiClient = new GuardianApiClient(this);
		String str = "1234";
		UUID uuid = UUID.nameUUIDFromBytes(str.getBytes());
		new RegisterTask().execute(strAccount, strPassword, "parent_admin", uuid.toString(), strName);
		
	}

	class RegisterTask extends AsyncTask<String, Void, String> {

        protected String doInBackground(String... args) {

        	GuardianApiClient apiClient = new GuardianApiClient(UserRegistrationActivity.this);
        	apiClient.registerUser(args[0], args[1], args[2], args[3], args[4]);
        	return null;
        }

        protected void onPostExecute(String token) {
        	finish();
        }
    }
}
