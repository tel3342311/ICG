package com.liteon.icampusguardian;

import com.liteon.icampusguardian.util.CustomDialog;
import com.liteon.icampusguardian.util.GuardianApiClient;
import com.liteon.icampusguardian.util.JSONResponse;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

public class UserResetPasswordActivity extends AppCompatActivity implements OnClickListener {
	
	private EditText mName;
	private TextView mTitleView;
	private TextView mDescView;
	private AppCompatButton mSend;
	private AppCompatButton mBackToLogin;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_resetpassword);
		findViews();
		setListener();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		mSend.setEnabled(false);
	}
	
	private void findViews() {
		mName = (EditText) findViewById(R.id.login_name);
		mTitleView = (TextView) findViewById(R.id.login_title); 
		mDescView = (TextView) findViewById(R.id.login_desc);
		mSend = (AppCompatButton) findViewById(R.id.email_send);
		mBackToLogin = (AppCompatButton) findViewById(R.id.back_to_login);
	}
	
	private void setListener() {
		mSend.setOnClickListener(this);
		mBackToLogin.setOnClickListener(this);
		mName.addTextChangedListener(mTextWatcher);
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
				mSend.setEnabled(true);
			} else {
				mSend.setEnabled(false);
			}
		}
	};
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.email_send:
			new ResetTask().execute(null,null);
			
			break;
		case R.id.back_to_login:
			finish();
			break;
		}
	}
	
	private boolean validateInput() {
		if (TextUtils.isEmpty(mName.getText())) {
			return false;
		}
		//check if acount email is valid
		if (!Patterns.EMAIL_ADDRESS.matcher(mName.getText()).matches()) {
			return false;
		}
		return true;
	}
	
	private JSONResponse resetPassword() {
		String strName = mName.getText().toString();
		GuardianApiClient apiClient = new GuardianApiClient(this);
		return apiClient.resetPassword(strName);
	}

	private void showErrorDialog() {
		
		final CustomDialog dialog = new CustomDialog();
		dialog.setTitle("帳號不正確，請再確認！");
		dialog.setIcon(R.drawable.ic_error_outline_black_24dp);
		dialog.setBtnText("好");
		dialog.setBtnConfirm(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		dialog.show(getSupportFragmentManager(), "dialog_fragment");

	}
	class ResetTask extends AsyncTask<Void, Void, Boolean> {

        protected Boolean doInBackground(Void... params) {
        	JSONResponse response = resetPassword();
        	if (response == null) {
				return false;
			}
			if (response.getReturn().getResults() == null) {
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						showErrorDialog();
					}
				});
				return false;
			}
        	return true;
        }

        protected void onPostExecute(Boolean isSuccess) {
        	if (isSuccess) {
				mTitleView.setText("系統已重設你的密碼");
				mDescView.setText("請察看信箱並使用系統寄出的預設密碼回到登入頁面進行登入\n");
				mSend.setVisibility(View.GONE);
				mName.setVisibility(View.INVISIBLE);
        	}
        }
    }
}
