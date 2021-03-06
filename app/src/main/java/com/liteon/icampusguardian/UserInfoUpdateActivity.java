package com.liteon.icampusguardian;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.liteon.icampusguardian.db.DBHelper;
import com.liteon.icampusguardian.util.ClsUtils;
import com.liteon.icampusguardian.util.Def;
import com.liteon.icampusguardian.util.GuardianApiClient;
import com.liteon.icampusguardian.util.JSONResponse;
import com.liteon.icampusguardian.util.JSONResponse.Parent;

public class UserInfoUpdateActivity extends AppCompatActivity {

	private boolean mIsEditMode;
	private EditText mName;
	private EditText mPhoneNumber;
	private EditText mAccount;
	private EditText mPassword;
	private EditText mConfirmPassword;
	private Toolbar mToolbar;
	private ImageView mBackBtn;
	private View mSyncView;
	private FrameLayout progressBarHolder;
	private String mNameGiven;
	private String mMobile_number;
	private ImageView mConfirm;
	private String mToken;
	private Parent mParent;
	private TextView mPasswordHint;
	private TextView mPasswordConstraint;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_update);
        SharedPreferences sp = getApplicationContext().getSharedPreferences(Def.SHARE_PREFERENCE, Context.MODE_PRIVATE);
        mToken = sp.getString(Def.SP_LOGIN_TOKEN, "E8C33BCCC8A1E1627B28B65B0B4DE829");
		findViews();
		updateEditText();
		setListener();
		setupToolbar();
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
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case R.id.action_confirm:
			updateAccount();
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	@Override
	protected void onResume() {
		super.onResume();
		mToolbar.setTitle("");
		showSyncWindow();
	}
	
	private void updateEditText() {
		DBHelper helper = DBHelper.getInstance(UserInfoUpdateActivity.this);

    	if (!TextUtils.isEmpty(mToken)) {
    		//Account values
    		mParent = helper.getParentByToken(helper.getReadableDatabase(), mToken);
    		mName.setText(mParent.getAccount_name());
			mPhoneNumber.setText(mParent.getMobile_number());
    		mPassword.setText(mParent.getPassword());
    		mConfirmPassword.setVisibility(View.INVISIBLE);
			mPasswordHint.setVisibility(View.INVISIBLE);
    		mAccount.setText(mParent.getUsername());
    	}
	}
	
	private void findViews() {
		mName = findViewById(R.id.login_name);
		mPhoneNumber = findViewById(R.id.login_number);
		mAccount = findViewById(R.id.login_account);
		mPassword = findViewById(R.id.login_password);
		mConfirmPassword = findViewById(R.id.login_password_confirm);
		mToolbar = findViewById(R.id.toolbar);
		mBackBtn = findViewById(R.id.cancel);
		mSyncView = findViewById(R.id.sync_view);
		progressBarHolder = findViewById(R.id.progressBarHolder);
		mPasswordHint = findViewById(R.id.password_hint);
		mPasswordConstraint = findViewById(R.id.password_constr);
	}
	
	private void setListener() {
		
		mName.addTextChangedListener(mTextWatcher);
		mPhoneNumber.addTextChangedListener(mTextWatcher);
		mAccount.addTextChangedListener(mTextWatcher);
		mPassword.addTextChangedListener(mTextWatcher);
		mConfirmPassword.addTextChangedListener(mTextWatcher);
		
		mName.setOnFocusChangeListener(mOnFocusChangeListener);
		mPhoneNumber.setOnFocusChangeListener(mOnFocusChangeListener);
		mAccount.setOnFocusChangeListener(mOnFocusChangeListener);
		mPassword.setOnFocusChangeListener(mOnFocusChangeListener);
		mConfirmPassword.setOnFocusChangeListener(mOnFocusChangeListener);
		
		AppCompatButton button = mSyncView.findViewById(R.id.button_sync);
		button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				new UpdateTask().execute("");
			}
		});

		mBackBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				finish();
				Intent intent = new Intent();
				intent.setClass(UserInfoUpdateActivity.this, MainActivity.class);
				intent.putExtra(Def.EXTRA_GOTO_PAGE_ID, Def.EXTRA_PAGE_APPINFO_ID);
				startActivity(intent);
			}
		});
	}

	private void setupToolbar() {
		setSupportActionBar(mToolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(false);
		getSupportActionBar().setHomeButtonEnabled(false);
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
			if (!TextUtils.equals(mParent.getPassword(), mPassword.getText())) {
				mConfirmPassword.setVisibility(View.VISIBLE);
				mPasswordHint.setVisibility(View.VISIBLE);
			} else {
				mConfirmPassword.setVisibility(View.INVISIBLE);
				mPasswordHint.setVisibility(View.INVISIBLE);

			}
			if (validateInput()) {
				//mConfirm.setEnabled(true);
				mIsEditMode = true;
			} else {
				//mConfirm.setEnabled(false);
				mIsEditMode = false;
			}
			invalidateOptionsMenu();
		}
	};
	
	private boolean validateInput() {
		if (TextUtils.isEmpty(mName.getText()) || 
			TextUtils.isEmpty(mAccount.getText()) ||
			TextUtils.isEmpty(mPhoneNumber.getText()) ||
			TextUtils.isEmpty(mPassword.getText())) {
			return false;
		}
		//check if acount email is valid
		String email = mAccount.getText().toString();
		email = email.trim();
		if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
			Toast.makeText(this, "invalid Email Account" , Toast.LENGTH_SHORT).show();
			return false;
		}
		if (!TextUtils.equals(mParent.getPassword(), mPassword.getText())) {
			if (mPassword.getText().length() < 8 || ClsUtils.isValidPassword(mPassword.getText().toString())) {
				mPasswordConstraint.setVisibility(View.VISIBLE);
				return false;
			} else {
				mPasswordConstraint.setVisibility(View.INVISIBLE);
			}
			//check if password & password confirm is match
			if (!TextUtils.equals(mPassword.getText(), mConfirmPassword.getText())) {
				mPasswordHint.setText(getString(R.string.password_not_match));
				return false;
			} else {
				mPasswordHint.setText(getString(R.string.password_hint));
			}

		}
		return true;
	}
	
	private void updateAccount() {
		String strName = mName.getText().toString();
		String strAccount = mAccount.getText().toString();
		String strPhoneNumber = mPhoneNumber.getText().toString();
		String strPassword = mPassword.getText().toString();
		new UpdateInfoTask().execute(strName, strAccount, strPassword, strPhoneNumber);
	}

	class UpdateInfoTask extends AsyncTask<String, Void, String> {

		@Override
		protected void onPreExecute() {

            progressBarHolder.setVisibility(View.VISIBLE);
            super.onPreExecute();
		}
		
        protected String doInBackground(String... args) {
        	
        	GuardianApiClient apiClient = GuardianApiClient.getInstance(UserInfoUpdateActivity.this);
        	apiClient.updateParentDetail(args[0], args[1], args[2], args[3]);
			mParent = new Parent();
			mParent.setAccount_name(args[0]);
			mParent.setUsername(args[1]);
			mParent.setPassword(args[2]);
			mParent.setMobile_number(args[3]);
			mParent.setToken(mToken);
            DBHelper helper = DBHelper.getInstance(UserInfoUpdateActivity.this);
            helper.updateAccount(helper.getWritableDatabase(), mParent);
        	return null;
        }

        protected void onPostExecute(String token) {
            progressBarHolder.setVisibility(View.GONE);
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
	
	private void showSyncWindow() {
		mSyncView.setVisibility(View.VISIBLE);
	}
	
	class UpdateTask extends AsyncTask<String, Void, Boolean> {

		final TextView title = (TextView) mSyncView.findViewById(R.id.title);

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			title.setText(getString(R.string.alarm_syncing));

		}
		@Override
		protected Boolean doInBackground(String... params) {

			GuardianApiClient apiClient = GuardianApiClient.getInstance(UserInfoUpdateActivity.this);
			JSONResponse response = apiClient.getUserDetail();
			if (response == null || !TextUtils.equals(Def.RET_SUCCESS_1 ,response.getReturn().getResponseSummary().getStatusCode())) {
			
				return null;
			}
			mNameGiven = response.getReturn().getResults().getName();
			mMobile_number = response.getReturn().getResults().getMobile_number();
			return null;
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			mName.setText(mNameGiven);
			mPhoneNumber.setText(mMobile_number);
			title.setText(getString(R.string.alarm_sync_complete));
			final Handler handler= new Handler();
			final Runnable hideSyncView = new Runnable() {
				
				@Override
				public void run() {
					mSyncView.setVisibility(View.GONE);
				}
			};
			handler.postDelayed(hideSyncView, 1500);
		}
	}
}
