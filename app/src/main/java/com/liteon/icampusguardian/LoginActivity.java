package com.liteon.icampusguardian;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.liteon.icampusguardian.db.AccountTable.AccountEntry;
import com.liteon.icampusguardian.db.DBHelper;
import com.liteon.icampusguardian.util.CustomDialog;
import com.liteon.icampusguardian.util.Def;
import com.liteon.icampusguardian.util.GuardianApiClient;
import com.liteon.icampusguardian.util.JSONResponse;
import com.liteon.icampusguardian.util.JSONResponse.Student;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class LoginActivity extends AppCompatActivity {

	private final static String TAG = LoginActivity.class.getName();
	//google login
	private GoogleApiClient mGoogleApiClient;
	private AppCompatButton signInButtonGoogle;
	private AppCompatButton signInButtonFacebook;
	private AppCompatButton signInButtonNormal;
	private AppCompatButton mQuitButton;
	private CallbackManager mFBcallbackManager;
	private AccessToken mFBAccessToken;
	private TextView mCreateAccount;
	private TextView mForgetPassword;
	private EditText mUserName;
	private EditText mPassword;
	private GuardianApiClient mApiClient;
	private List<Student> mStudentList = new ArrayList<>();

	//facebook login
	private final static int RC_GOOGLE_SIGNIN = 1000;
	private final static int RC_FACEBOOK_SIGNIN = 1001;
	
	private final static int RC_USER_TERM = 1002;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		setupGoogleSignIn();
		setupFacebookSignIn();
		findViews();
		setListener();
		
		mApiClient = new GuardianApiClient(this);
		SharedPreferences sp = getSharedPreferences(Def.SHARE_PREFERENCE, Context.MODE_PRIVATE);
		String token = sp.getString(Def.SP_LOGIN_TOKEN, "");
		if (sp.getInt(Def.SP_USER_TERM_READ, 0) == 0) {
			Intent intent = new Intent();
			intent.setClass(LoginActivity.this, WelcomeActivity.class);
			startActivityForResult(intent, RC_USER_TERM);
		} else if (!TextUtils.isEmpty(token)) {
    		
    		Intent intent = new Intent();
    		intent.setClass(getApplicationContext(), MainActivity.class);
    		startActivity(intent);
    		finish();	
		} else {
			
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		signInButtonNormal.setEnabled(false);
		//mUserName.setText(Def.USER);
		//mPassword.setText(Def.PASSWORD);
	}
	private void findViews() {
		signInButtonNormal = (AppCompatButton) findViewById(R.id.ap_login);
		signInButtonGoogle = (AppCompatButton) findViewById(R.id.login_button_google);
		signInButtonFacebook = (AppCompatButton) findViewById(R.id.login_button_fb);
		mQuitButton = (AppCompatButton) findViewById(R.id.login_button_quit);
		mUserName = (EditText) findViewById(R.id.login_account);
		mPassword = (EditText) findViewById(R.id.login_password);
		mCreateAccount = (TextView) findViewById(R.id.create_account);
		mForgetPassword = (TextView) findViewById(R.id.forget_password);
	}
	
	private void setListener() {
		signInButtonNormal.setOnClickListener(mOnNormalSignInListener);
		signInButtonGoogle.setOnClickListener(mGoogleSignInClickListener);
		signInButtonFacebook.setOnClickListener(mFacebookSignInClickListener);
		mQuitButton.setOnClickListener(mOnQuitClickListener);
		mCreateAccount.setOnClickListener(mOnCreateAccountClickListener);
		mForgetPassword.setOnClickListener(mOnForgetPasswordClickListener);
		mUserName.addTextChangedListener(mTextWatcher);
		mPassword.addTextChangedListener(mTextWatcher);
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
				signInButtonNormal.setEnabled(true);
			} else {
				signInButtonNormal.setEnabled(false);
			}
		}
	};
	
	private boolean validateInput() {
		String user = mUserName.getText().toString();
		String password = mPassword.getText().toString();
		if (TextUtils.isEmpty(user) || TextUtils.isEmpty(password)) {
			return false;
		}
		return true;
	}
	
	private View.OnClickListener mOnCreateAccountClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			Intent intent = new Intent();
			intent.setClass(getApplicationContext(), UserRegistrationActivity.class);
			startActivity(intent);
		}
	};
	
	private View.OnClickListener mOnForgetPasswordClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			Intent intent = new Intent();
			intent.setClass(getApplicationContext(), UserResetPasswordActivity.class);
			startActivity(intent);
		}
	};
	private View.OnClickListener mOnNormalSignInListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			new LoginTask().execute(mUserName.getText().toString(), mPassword.getText().toString());
		}
	};
	private void setupGoogleSignIn(){
		// Configure sign-in to request the user's ID, email address, and basic
		// profile. ID and basic profile are included in DEFAULT_SIGN_IN.
		GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
		        .requestEmail()
		        .build();
		
		// Build a GoogleApiClient with access to the Google Sign-In API and the
		// options specified by gso.
		mGoogleApiClient = new GoogleApiClient.Builder(this)
		        .enableAutoManage(this, mOnConnectionFailedListener)
		        .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
		        .build();
	}
	
	private void setupFacebookSignIn() {
		FacebookSdk.sdkInitialize(getApplicationContext());
		mFBcallbackManager = CallbackManager.Factory.create();
		LoginManager.getInstance().registerCallback(mFBcallbackManager,
	            new FacebookCallback<LoginResult>() {

					@Override
					public void onSuccess(LoginResult result) {
						mFBAccessToken = result.getAccessToken();
	        			Intent intent = new Intent();
	        			intent.setClass(getApplicationContext(), MainActivity.class);
	        			startActivity(intent);
					}

					@Override
					public void onCancel() {
				        Toast.makeText(LoginActivity.this, "Facebook sign in canceled!", Toast.LENGTH_LONG).show();
					}

					@Override
					public void onError(FacebookException error) {
				        Toast.makeText(LoginActivity.this, "Facebook sign in Error" + error.getMessage(), Toast.LENGTH_LONG).show();
					}

	    });
	}
	
	private OnConnectionFailedListener mOnConnectionFailedListener = new OnConnectionFailedListener() {

		@Override
		public void onConnectionFailed(ConnectionResult arg0) {
			
			//new CustomDialog().show(getSupportFragmentManager(), "dialog_fragment");
		}
		
	};
	
	private OnClickListener mGoogleSignInClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			//new CustomDialog().show(getSupportFragmentManager(), "dialog_fragment");
			signInGoogle();
		}
	};
	
	private OnClickListener mFacebookSignInClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this, Arrays.asList("public_profile", "user_friends"));
		}
	};
	
	private void signInGoogle() {
		Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
		startActivityForResult(signInIntent, RC_GOOGLE_SIGNIN);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);

	    if (requestCode == RC_USER_TERM) {
	    	if (resultCode == RESULT_CANCELED) {
	    		finish();
	    	}
	    } else if (requestCode == RC_GOOGLE_SIGNIN) {
	    	// Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
	        GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
	        handleSignInResult(result);
	    } else {
	    	if(mFBcallbackManager.onActivityResult(requestCode, resultCode, data)) {
	            return;
	        }
	    	//else if (requestCode == RC_FACEBOOK_SIGNIN) { 
	    }
	}
	
	private void handleSignInResult(GoogleSignInResult result) {
	    Log.d(TAG, "handleSignInResult:" + result.isSuccess());
	    if (result.isSuccess()) {
	        // Signed in successfully, show authenticated UI.
	        GoogleSignInAccount acct = result.getSignInAccount();
	        //Toast.makeText(this, "Google sign in " + acct.getDisplayName() + ", Email : " + acct.getEmail(), Toast.LENGTH_LONG).show();
	        //mStatusTextView.setText(getString(R.string.signed_in_fmt, acct.getDisplayName()));
	        //updateUI(true);
			Intent intent = new Intent();
			intent.setClass(getApplicationContext(), MainActivity.class);
			startActivity(intent);
	    } else {
	        // Signed out, show unauthenticated UI.
	        //updateUI(false);
	    }
	}
	
	private OnClickListener mOnQuitClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			finish();
			
		}
	};
	
	private void showLoginErrorDialog(String title, String btnText) {
		final CustomDialog dialog = new CustomDialog();
		dialog.setTitle(title);
		dialog.setIcon(R.drawable.ic_error_outline_black_24dp);
		dialog.setBtnText(btnText);
		dialog.setBtnConfirm(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		dialog.show(getSupportFragmentManager(), "dialog_fragment");
	}
	
	class LoginTask extends AsyncTask<String, Void, String> {

        protected String doInBackground(String... args) {
        	DBHelper helper = DBHelper.getInstance(LoginActivity.this);
        	SharedPreferences sp = getApplicationContext().getSharedPreferences(Def.SHARE_PREFERENCE, Context.MODE_PRIVATE);
        	String token = "";
        	//Account values
        	ContentValues cv = new ContentValues();
        	//check network 
        	if (!isNetworkConnectionAvailable()) {
        		runOnUiThread(new Runnable() {

					@Override
					public void run() {
						showLoginErrorDialog( getString(R.string.login_no_network), getString(android.R.string.ok));
					}
				});
        		return null;
        	}
        	//check server 
        	if (!isURLReachable(LoginActivity.this, mApiClient.getServerUri().toString())) {
        		runOnUiThread(new Runnable() {

					@Override
					public void run() {
						showLoginErrorDialog(getString(R.string.login_error_no_server_connection), getString(android.R.string.ok));
					}
				});
        		return null;
        	}
			final JSONResponse response = mApiClient.login(args[0], args[1]);
			if (response == null) {
				return null;
			}
			if (response.getReturn().getResults() == null) {
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						showLoginErrorDialog(getString(R.string.login_error_email), getString(android.R.string.ok));
					}
				});
				return null;
			}
			token = response.getReturn().getResults().getToken();

			cv.put(AccountEntry.COLUMN_NAME_USER_NAME, args[0]);
			cv.put(AccountEntry.COLUMN_NAME_PASSWORD, args[1]);
			cv.put(AccountEntry.COLUMN_NAME_TOKEN, token);
			helper.insertAccount(helper.getWritableDatabase(), cv);


        	//get Child list
        	JSONResponse response_childList = mApiClient.getChildrenList();
        	mStudentList = Arrays.asList(response_childList.getReturn().getResults().getStudents());
        	if (mStudentList.size() == 0) {
        		//First use
        		return token;
        	}
        	//clear child list in db 
        	helper.clearChildList(helper.getWritableDatabase());
        	//Save child list to db
        	helper.insertChildList(helper.getWritableDatabase(), mStudentList);
        	
        	//get Device event report
        	String eventId = Def.EVENT_ID_GPS_LOCATION;
        	String duration = Def.EVENT_DURATION_WEEK;
        	for (Student student : mStudentList) {
        		JSONResponse res = mApiClient.getDeviceEventReport(student.getStudent_id(), eventId, duration);
        		if (res != null) {
        			res.getReturn().getResults().getDevices();
        		}
        	}
    		//Send FireBase Instance token to server
        	String fcmToken = FirebaseInstanceId.getInstance().getToken();
        	mApiClient.updateAppToken(fcmToken);
        	Log.i(TAG, "API 11 UpdateAppToken called : FCM Token is " + fcmToken);
    		SharedPreferences.Editor editor = sp.edit();
    		editor.putString(Def.SP_LOGIN_TOKEN, token);
        	if (mStudentList.size() > 0 && !sp.contains(Def.SP_CURRENT_STUDENT)) {
        		editor.putInt(Def.SP_CURRENT_STUDENT, 0);
        	}
        	editor.commit();
        	return token;
        }

        protected void onPostExecute(String token) {
        	if (token != null) {
        		finish();
        		Intent intent = new Intent();
        		//First use, pairing child
        		if (mStudentList.size() == 0) {			
	        		intent.setClass(getApplicationContext(), ChildInfoUpdateActivity.class);
	        		startActivity(intent);
        		} else {
	        		intent.setClass(getApplicationContext(), MainActivity.class);
	        		startActivity(intent);
        		}
        	}
        }
    }
	
	public boolean isNetworkConnectionAvailable() {  
	    ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo info = cm.getActiveNetworkInfo();     
	    if (info == null) return false;
	    State network = info.getState();
	    return (network == NetworkInfo.State.CONNECTED || network == NetworkInfo.State.CONNECTING);
	} 
	
	public boolean isURLReachable(Context context, String Url) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected()) {
            try {
                URL url = new URL(Url);   // Change to "http://google.com" for www  test.
                HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
                urlc.setConnectTimeout(1000);          // 1 s.
                urlc.connect();
                int responseCode = urlc.getResponseCode();
                if (responseCode == 200 || responseCode == 404) {        // 200 = "OK" code (http connection is fine).
                    Log.i(TAG, "Connect to "+ Url +" Success !");
                    return true;
                } else {
                    Log.i(TAG, "Connect to " + Url + " Fail ! Response code is " + responseCode);
                    return false;
                }
            } catch (MalformedURLException e1) {
                return false;
            } catch (IOException e) {
                return false;
            }
        }
        return false;
    }
}
