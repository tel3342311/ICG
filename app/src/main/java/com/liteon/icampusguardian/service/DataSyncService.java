package com.liteon.icampusguardian.service;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.support.v4.content.LocalBroadcastManager;

import com.liteon.icampusguardian.R;
import com.liteon.icampusguardian.util.Def;
import com.liteon.icampusguardian.util.GuardianApiClient;
import com.liteon.icampusguardian.util.JSONResponse;

public class DataSyncService extends IntentService {
    private final static String TAG = DataSyncService.class.getName();
    private GuardianApiClient mApiClient;
    public DataSyncService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (mApiClient == null) {
            mApiClient = GuardianApiClient.getInstance(getApplicationContext());
        }
        if (intent != null) {
            switch (intent.getAction()) {
                case Def.ACTION_REGISTERATION_USER:
                    handleRegisterNewUser(intent);
                    break;
                case Def.ACTION_LOGIN_USER:
                    handleLoginUser(intent);
                    break;
                case Def.ACTION_LOGOUT_USER:
                    handleLogoutUser(intent);
                    break;
                case Def.ACTION_GET_EVENT_REPORT:
                    handleGetEventReport(intent);
                    break;
                case Def.ACTION_GET_LOCATION:
                    handleGetLocation(intent);
                    break;
                case Def.ACTION_GET_PARENT_DETAIL:
                    handleGetParentDetail(intent);
                    break;
                case Def.ACTION_GET_STUDENT_LIST:
                    handleGetStudentList(intent);
                    break;
                case Def.ACTION_PAIR_NEW_DEVICE:
                    handlePairNewDevice(intent);
                    break;
                case Def.ACTION_UNPAIR_DEVICE:
                    handleUnpairDevice(intent);
                    break;
                case Def.ACTION_RESET_PASSWORD:
                    handleResetPassword(intent);
                    break;
                case Def.ACTION_UPDATE_APP_TOKEN:
                    handleUpdateAppToken(intent);
                    break;
                case Def.ACTION_UPDATE_STUDENT_DETAIL:
                    handleUpdateStudentDetail(intent);
                    break;
                case Def.ACTION_UPDATE_PARENT_DETAIL:
                    handleUpdateParentDetail(intent);
                    break;
            }
        }
    }

    private void handleUpdateParentDetail(Intent intent) {

    }

    private void handleUpdateStudentDetail(Intent intent) {

    }

    private void handleUpdateAppToken(Intent intent) {

    }

    private void handleResetPassword(Intent intent) {

    }

    private void handleUnpairDevice(Intent intent) {

    }

    private void handlePairNewDevice(Intent intent) {

    }

    private void handleGetStudentList(Intent intent) {

    }

    private void handleGetParentDetail(Intent intent) {

    }

    private void handleGetLocation(Intent intent) {

    }

    private void handleGetEventReport(Intent intent) {

    }

    private void handleLogoutUser(Intent intent) {

    }

    private void handleRegisterNewUser(Intent intent) {

    }

    private void handleLoginUser(Intent intent) {

        String account = intent.getStringExtra(Def.KEY_ACCOUNT_NAME);
        String password = intent.getStringExtra(Def.KEY_PASSWORD);
        JSONResponse response = mApiClient.login(account, password);
        if (response == null) {
            sendBrocastToAp(getString(R.string.login_error_no_server_connection));
        }
    }

    public void sendBrocastToAp(String message) {
        Intent intent = new Intent(Def.ACTION_ERROR_NOTIFY);
        intent.putExtra(Def.EXTRA_ERROR_MESSAGE, message);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}
