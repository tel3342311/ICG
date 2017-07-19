package com.liteon.icampusguardian;

import com.liteon.icampusguardian.db.DBHelper;
import com.liteon.icampusguardian.util.Def;
import com.liteon.icampusguardian.util.GuardianApiClient;
import com.liteon.icampusguardian.util.JSONResponse.Parent;

import android.content.Context;
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
import android.view.animation.AlphaAnimation;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

public class UserInfoUpdateActivity extends AppCompatActivity {

	private boolean mIsEditMode;
	private EditText mName;
	private EditText mAccount;
	private EditText mPassword;
	private EditText mConfirmPassword;
	private Toolbar mToolbar;
	private View mSyncView;
	private FrameLayout progressBarHolder;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_update);
		findViews();
		setupToolbar();
		updateEditText();
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
		mToolbar.setTitle("家長資料");
		showSyncWindow();
	}
	
	private void updateEditText() {
		DBHelper helper = DBHelper.getInstance(UserInfoUpdateActivity.this);
    	SharedPreferences sp = getApplicationContext().getSharedPreferences(Def.SHARE_PREFERENCE, Context.MODE_PRIVATE);
    	String token = sp.getString(Def.SP_LOGIN_TOKEN, "");
    	if (!TextUtils.isEmpty(token)) {
    		//Account values
    		Parent parent = helper.getParentByToken(helper.getReadableDatabase(), token);
    		Toast.makeText(this, "Parent username: " + parent.getUsername() , Toast.LENGTH_LONG).show();
    		Toast.makeText(this, "Parent given name: " + parent.getGiven_name() , Toast.LENGTH_LONG).show();
    		Toast.makeText(this, "Parent password: " + parent.getPassword() , Toast.LENGTH_LONG).show();
    		
    		mName.setText(parent.getGiven_name());
    		mPassword.setText(parent.getPassword());
    		mConfirmPassword.setText(parent.getPassword());
    		mAccount.setText(parent.getUsername());
    	}
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
		mAccount = (EditText) findViewById(R.id.login_account);
		mPassword = (EditText) findViewById(R.id.login_password);
		mConfirmPassword = (EditText) findViewById(R.id.login_password_confirm);
		mToolbar = (Toolbar) findViewById(R.id.toolbar);
		mSyncView = (View) findViewById(R.id.sync_view);
		progressBarHolder = (FrameLayout) findViewById(R.id.progressBarHolder);
	}
	
	private void setListener() {
		
		mName.addTextChangedListener(mTextWatcher);
		mAccount.addTextChangedListener(mTextWatcher);
		mPassword.addTextChangedListener(mTextWatcher);
		mConfirmPassword.addTextChangedListener(mTextWatcher);
		
		mName.setOnFocusChangeListener(mOnFocusChangeListener);
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
		String strAccount = mAccount.getText().toString();
		strAccount = strAccount.trim();
		strAccount = strAccount.substring(0, strAccount.indexOf("@"));
		String strPassword = mPassword.getText().toString();
		new UpdateInfoTask().execute(strName, strAccount, strPassword);
	}

	class UpdateInfoTask extends AsyncTask<String, Void, String> {

		@Override
		protected void onPreExecute() {

            progressBarHolder.setVisibility(View.VISIBLE);
            super.onPreExecute();
		}
		
        protected String doInBackground(String... args) {
        	
        	GuardianApiClient apiClient = new GuardianApiClient(UserInfoUpdateActivity.this);
        	apiClient.updateParentDetail(args[0], args[1], args[2]);
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
		View contentview = mSyncView;
		final TextView title = (TextView) contentview.findViewById(R.id.title);
		AppCompatButton button = (AppCompatButton) contentview.findViewById(R.id.button_sync);
		button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				title.setText("同步中");
				final Handler handler= new Handler();
				final Runnable hideSyncView = new Runnable() {
					
					@Override
					public void run() {
						mSyncView.setVisibility(View.GONE);
					}
				};
				Runnable runnable = new Runnable(){
					   @Override
					   public void run() {
						   title.setText("同步完成");
						   handler.postDelayed(hideSyncView, 3000);
					} 
				};
				handler.postDelayed(runnable, 2000);
			}
		});
	}
}
