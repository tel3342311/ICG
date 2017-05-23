package com.liteon.icampusguardian;

import com.facebook.accountkit.AccessToken;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKit.InitializeCallback;
import com.facebook.accountkit.AccountKitLoginResult;
import com.facebook.accountkit.LoginModel;
import com.facebook.accountkit.ui.AccountKitActivity;
import com.facebook.accountkit.ui.AccountKitConfiguration;
import com.facebook.accountkit.ui.LoginType;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

	private final static String TAG = MainActivity.class.getName();
	//google login
	private GoogleApiClient mGoogleApiClient;
	private Button signInButtonGoogle;
	private Button signInButtonFacebook;
	//facebook login
	private LoginModel mFBLoginKit;
	private final static int RC_GOOGLE_SIGNIN = 1000;
	private final static int RC_FACEBOOK_SIGNIN = 1001;
	private AccountKit mAccountKit;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setupFacebookSignIn();
		setContentView(R.layout.activity_main);
		setupGoogleSignIn();
		
		
		signInButtonGoogle = (Button) findViewById(R.id.login_button);
		signInButtonGoogle.setOnClickListener(mGoogleSignInClickListener);
		
		signInButtonFacebook = (Button) findViewById(R.id.login_button_fb);
		signInButtonFacebook.setOnClickListener(mFacebookSignInClickListener);
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
		AccountKit.initialize(getApplicationContext(), mInitializeCallback);
	};
	
	private OnConnectionFailedListener mOnConnectionFailedListener = new OnConnectionFailedListener() {

		@Override
		public void onConnectionFailed(ConnectionResult arg0) {
			
		}
		
	};
	
	private OnClickListener mGoogleSignInClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			signInGoogle();
		}
	};
	
	private OnClickListener mFacebookSignInClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			
			//Check for Existing Sessions
			AccessToken accessToken = AccountKit.getCurrentAccessToken();

			if (accessToken != null) {
			  //Handle Returning User
				Log.d(TAG, "Returning User");
			} else {
			  //Handle new or logged out user
				Log.d(TAG, "New User, or logged out user");
			}
			 
			signInFacebook();
		}
	};
	
	private void signInFacebook() {
		  final Intent intent = new Intent(this, AccountKitActivity.class);
		  AccountKitConfiguration.AccountKitConfigurationBuilder configurationBuilder =
		    new AccountKitConfiguration.AccountKitConfigurationBuilder(
		      LoginType.EMAIL,
		      AccountKitActivity.ResponseType.TOKEN); // or .ResponseType.CODE
		  // ... perform additional configuration ...
		  intent.putExtra(
		    AccountKitActivity.ACCOUNT_KIT_ACTIVITY_CONFIGURATION,
		    configurationBuilder.build());
		  startActivityForResult(intent, RC_FACEBOOK_SIGNIN);
	}
	
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
	    } else if (requestCode == RC_FACEBOOK_SIGNIN) {
	    	AccountKitLoginResult loginResult = data.getParcelableExtra(AccountKitLoginResult.RESULT_KEY);
	        String toastMessage;
	        if (loginResult.getError() != null) {
	            toastMessage = loginResult.getError().getErrorType().getMessage();
	            //showErrorActivity(loginResult.getError());
	        } else if (loginResult.wasCancelled()) {
	            toastMessage = "Login Cancelled";
	        } else {
	            if (loginResult.getAccessToken() != null) {
	                toastMessage = "Success:" + loginResult.getAccessToken().getAccountId();
	            } else {
	                toastMessage = String.format(
	                        "Success:%s...",
	                        loginResult.getAuthorizationCode().substring(0,10));
	            }

	            // If you have an authorization code, retrieve it from
	            // loginResult.getAuthorizationCode()
	            // and pass it to your server and exchange it for an access token.

	            // Success! Start your next activity...
	            //goToMyLoggedInActivity();
	        }

	        // Surface the result to your user in an appropriate way.
	        Toast.makeText(
	                this,
	                toastMessage,
	                Toast.LENGTH_LONG)
	                .show();
	    }
	}
	
	private void handleSignInResult(GoogleSignInResult result) {
	    Log.d(TAG, "handleSignInResult:" + result.isSuccess());
	    if (result.isSuccess()) {
	        // Signed in successfully, show authenticated UI.
	        GoogleSignInAccount acct = result.getSignInAccount();
	        Toast.makeText(this, "Google sign in " + acct.getDisplayName() + ", Email : " + acct.getEmail(), Toast.LENGTH_LONG);
	        //mStatusTextView.setText(getString(R.string.signed_in_fmt, acct.getDisplayName()));
	        //updateUI(true);
	    } else {
	        // Signed out, show unauthenticated UI.
	        //updateUI(false);
	    }
	}
	
	private InitializeCallback mInitializeCallback = new InitializeCallback() {

		@Override
		public void onInitialized() {
			Log.d(TAG, "Facebook SDK initialized");
			Toast.makeText(MainActivity.this, "Facebook SDK is initialed", Toast.LENGTH_LONG).show();
			if (AccountKit.isInitialized()) {
				signInFacebook();
			}
		}
	};
}
