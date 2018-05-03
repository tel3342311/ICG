package com.liteon.icampusguardian.service;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.liteon.icampusguardian.App;
import com.liteon.icampusguardian.R;
import com.liteon.icampusguardian.db.DBHelper;
import com.liteon.icampusguardian.db.HealthDataTable;
import com.liteon.icampusguardian.util.Def;
import com.liteon.icampusguardian.util.GuardianApiClient;
import com.liteon.icampusguardian.util.JSONResponse;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

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
                case Def.ACTION_GET_HEALTHY_DATA:
                    handleGetHealthyData(intent);
                    break;
            }
        }
    }

    private void handleGetHealthyData(Intent intent) {

        JSONResponse.HealthyData[] fitness;
        JSONResponse.HealthyData[] activity;
        JSONResponse.HealthyData[] calories;
        JSONResponse.HealthyData[] heartrate;
        JSONResponse.HealthyData[] sleep;
        JSONResponse.HealthyData[] steps;

        Date end = Calendar.getInstance().getTime();
        Calendar c = Calendar.getInstance();
        c.setTime(end);
        c.add(Calendar.DAY_OF_YEAR, -7);
        Date start = c.getTime();
        SimpleDateFormat sdfQurey = new SimpleDateFormat("yyyy-MM-dd");
        String startDate = sdfQurey.format(start);
        String endDate = sdfQurey.format(end);

        String student_id = intent.getStringExtra(Def.KEY_STUDENT_ID);
        JSONResponse response = mApiClient.getHealthyData(student_id, startDate, endDate);
        //Parse Event
        if (response != null) {
            JSONResponse.Results results = response.getReturn().getResults();
            if (results != null) {
                if (response.getReturn() != null && response.getReturn().getResults() != null) {
                    fitness = response.getReturn().getResults().getFitness();
                    activity = response.getReturn().getResults().getActivity();
                    calories = response.getReturn().getResults().getCalories();
                    heartrate = response.getReturn().getResults().getHeartrate();
                    sleep = response.getReturn().getResults().getSleep();
                    steps = response.getReturn().getResults().getSteps();
                    //For Activity, use situation in data
                    writeDataToDB(activity, student_id);
                    //For Other healthy data
                    writeDataToDB(fitness, HealthDataTable.HealthDataEntry.SITUATION_FITNESS, student_id);
                    writeDataToDB(calories, HealthDataTable.HealthDataEntry.SITUATION_CALOS, student_id);
                    writeDataToDB(heartrate, HealthDataTable.HealthDataEntry.SITUATION_HEART, student_id);
                    writeDataToDB(sleep, HealthDataTable.HealthDataEntry.SITUATION_SLEEP, student_id);
                    writeDataToDB(steps, HealthDataTable.HealthDataEntry.SITUATION_STEPS, student_id);
                    sendBroadcast(new Intent(Def.ACTION_GET_HEALTHY_DATA));
                }
            }
        }
    }

    private void writeDataToDB(JSONResponse.HealthyData[] data, int situation, String studentId) {

        DBHelper dbHelper = DBHelper.getInstance(App.getContext());
        if (data != null && data.length > 0) {
            List<JSONResponse.HealthyData> list = Arrays.asList(data);
            //if situation is carloies or Steps, sum up
            if (situation == HealthDataTable.HealthDataEntry.SITUATION_STEPS || situation == HealthDataTable.HealthDataEntry.SITUATION_CALOS) {
                HashMap<Long, JSONResponse.HealthyData> map = new HashMap<>();
                for (JSONResponse.HealthyData currentData : list) {
                    if (map.get(new Long(currentData.getDate())) != null) {
                        JSONResponse.HealthyData tmp = map.get(new Long(currentData.getDate()));
                        tmp.setValue(tmp.getValue() + currentData.getValue());
                        continue;
                    }
                    map.put(new Long(currentData.getDate()), currentData);
                }
                dbHelper.updateHealthyData(dbHelper.getWritableDatabase(), new ArrayList(map.values()), situation, studentId);
            } else {
                dbHelper.updateHealthyData(dbHelper.getWritableDatabase(), list, situation, studentId);
            }
        }
    }

    private void writeDataToDB(JSONResponse.HealthyData[] data, String studentId) {

        DBHelper dbHelper = DBHelper.getInstance(App.getContext());
        if (data != null && data.length > 0) {
            List<JSONResponse.HealthyData> list = Arrays.asList(data);
            dbHelper.updateHealthyData(dbHelper.getWritableDatabase(), list, studentId);
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

    public void sendBroadcast(Intent intent) {

        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    public void sendBrocastToAp(String message) {
        Intent intent = new Intent(Def.ACTION_ERROR_NOTIFY);
        intent.putExtra(Def.EXTRA_ERROR_MESSAGE, message);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}
