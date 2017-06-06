package com.liteon.icampusguardian;

import java.util.Arrays;

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
import com.liteon.icampusguardian.util.CustomDialog;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;


public class LoginActivity extends AppCompatActivity {

	private final static String TAG = LoginActivity.class.getName();
	//google login
	private GoogleApiClient mGoogleApiClient;
	private AppCompatButton signInButtonGoogle;
	private AppCompatButton signInButtonFacebook;
	private CallbackManager mFBcallbackManager;
	private AccessToken mFBAccessToken;
	//facebook login
	private final static int RC_GOOGLE_SIGNIN = 1000;
	private final static int RC_FACEBOOK_SIGNIN = 1001;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		setupGoogleSignIn();
		setupFacebookSignIn();
		
		signInButtonGoogle = (AppCompatButton) findViewById(R.id.login_button_google);
		signInButtonGoogle.setOnClickListener(mGoogleSignInClickListener);
		
		signInButtonFacebook = (AppCompatButton) findViewById(R.id.login_button_fb);
		signInButtonFacebook.setOnClickListener(mFacebookSignInClickListener);
		Intent intent = new Intent();
		intent.setClass(LoginActivity.this, WelcomeActivity.class);
		startActivity(intent);
	}
	
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
			
			new CustomDialog().show(getSupportFragmentManager(), "dialog_fragment");
		}
		
	};
	
	private OnClickListener mGoogleSignInClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			new CustomDialog().show(getSupportFragmentManager(), "dialog_fragment");
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

	    // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
	    if (requestCode == RC_GOOGLE_SIGNIN) {
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
	        Toast.makeText(this, "Google sign in " + acct.getDisplayName() + ", Email : " + acct.getEmail(), Toast.LENGTH_LONG).show();
	        //mStatusTextView.setText(getString(R.string.signed_in_fmt, acct.getDisplayName()));
	        //updateUI(true);
	    } else {
	        // Signed out, show unauthenticated UI.
	        //updateUI(false);
	    }
	}
}
