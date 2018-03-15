package com.liteon.icampusguardian.service;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.liteon.icampusguardian.MainActivity;
import com.liteon.icampusguardian.util.Def;
import com.liteon.icampusguardian.util.GuardianApiClient;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

public class GuardianInstanceIDService extends FirebaseInstanceIdService {

	private static final String TAG = GuardianInstanceIDService.class.getName();
	
	@Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(refreshedToken);
    }
	
	/**
     * Persist token to third-party servers.
     *
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String fireBaseToken) {
        // TODO: Implement this method to send token to your app server.
    	SharedPreferences sp = getApplicationContext().getSharedPreferences(Def.SHARE_PREFERENCE, Context.MODE_PRIVATE);
    	String loginToken = sp.getString(Def.SP_LOGIN_TOKEN, "");
    	if (!TextUtils.isEmpty(loginToken)) {
    		GuardianApiClient apiClient = GuardianApiClient.getInstance(getApplicationContext());
    		apiClient.setToken(loginToken);
    		apiClient.updateAppToken(fireBaseToken);
    	}
    }
    
    
}
