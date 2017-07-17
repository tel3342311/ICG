package com.liteon.icampusguardian;

import java.util.UUID;

import com.liteon.icampusguardian.util.AlarmItemAdapter;
import com.liteon.icampusguardian.util.GuardianApiClient;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class UserInfoUpdateActivity extends AppCompatActivity {

	private boolean mIsEditMode;
	private EditText mName;
	private EditText mPhone;
	private EditText mAccount;
	private EditText mPassword;
	private EditText mConfirmPassword;
	private Toolbar mToolbar;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_update);
		findViews();
		setupToolbar();
		setListener();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.one_confirm_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	public boolean onPrepareOptionsMenu(Menu menu) {
		
		if (mIsEditMode) {
			menu.findItem(R.id.action_confirm).setVisible(true);
		} else {
			menu.findItem(R.id.action_confirm).setVisible(false);
		}
		return true;
	};
	
	@Override
	protected void onResume() {
		super.onResume();
		mToolbar.setTitle("家長資料");
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
	
	private void findViews() {
		mName = (EditText) findViewById(R.id.login_name);
		mPhone = (EditText) findViewById(R.id.login_phone);
		mAccount = (EditText) findViewById(R.id.login_account);
		mPassword = (EditText) findViewById(R.id.login_password);
		mConfirmPassword = (EditText) findViewById(R.id.login_password_confirm);
		mToolbar = (Toolbar) findViewById(R.id.toolbar);
	}
	
	private void setListener() {
		
		mName.addTextChangedListener(mTextWatcher);
		mPhone.addTextChangedListener(mTextWatcher);
		mAccount.addTextChangedListener(mTextWatcher);
		mPassword.addTextChangedListener(mTextWatcher);
		mConfirmPassword.addTextChangedListener(mTextWatcher);
		
		mName.setOnFocusChangeListener(mOnFocusChangeListener);
		mPhone.setOnFocusChangeListener(mOnFocusChangeListener);
		mAccount.setOnFocusChangeListener(mOnFocusChangeListener);
		mPassword.setOnFocusChangeListener(mOnFocusChangeListener);
		mConfirmPassword.setOnFocusChangeListener(mOnFocusChangeListener);
	}
	
	private OnFocusChangeListener mOnFocusChangeListener = new OnFocusChangeListener() {
		
		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			if (hasFocus) {
				enterEditMode();
			}
		}
	};
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
				//mConfirm.setEnabled(true);
			} else {
				//mConfirm.setEnabled(false);
			}
		}
	};
	
	private boolean validateInput() {
		if (TextUtils.isEmpty(mName.getText()) || 
			TextUtils.isEmpty(mPhone.getText()) || 
			TextUtils.isEmpty(mAccount.getText()) || 
			TextUtils.isEmpty(mPassword.getText()) ||
			TextUtils.isEmpty(mConfirmPassword.getText())) {
			return false;
		}
		//check if acount email is valid
		String email = mAccount.getText().toString();
		email = email.trim();
		if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
			Toast.makeText(this, "invalid Email Account" , Toast.LENGTH_SHORT).show();
			return false;
		}
		//check if password & password confirm is match
		if (!TextUtils.equals(mPassword.getText(), mConfirmPassword.getText())) {
			return false;
		}
		return true;
	}
	
	private void updateAccount() {
		String strName = mName.getText().toString();
		String strPhone = mPhone.getText().toString();
		String strAccount = mAccount.getText().toString();
		strAccount = strAccount.trim();
		String strPassword = mPassword.getText().toString();
		
		GuardianApiClient apiClient = new GuardianApiClient(this);
		String str = "1234";
		UUID uuid = UUID.nameUUIDFromBytes(str.getBytes());
		new UpdateInfoTask().execute(strAccount, strPassword, "parent_admin", uuid.toString(), strName);
		
	}

	class UpdateInfoTask extends AsyncTask<String, Void, String> {

        protected String doInBackground(String... args) {

        	GuardianApiClient apiClient = new GuardianApiClient(UserInfoUpdateActivity.this);
        	apiClient.registerUser(args[0], args[1], args[2], args[3], args[4]);
        	return null;
        }

        protected void onPostExecute(String token) {
        	finish();
        }
    }
	
	public void exitEditMode() {
		mIsEditMode = false;
		invalidateOptionsMenu();
	}
	
	public void enterEditMode() {
		mIsEditMode = true;
		invalidateOptionsMenu();
	}
}
