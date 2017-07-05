package com.liteon.icampusguardian.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.liteon.icampusguardian.util.JSONResponse.Device;
import com.liteon.icampusguardian.util.JSONResponse.Student;

import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

public class GuardianApiClient {

	private static final String TAG = GuardianApiClient.class.getName();
	private String mUrlPrepend = "http://61.246.61.175:8080/icgwearable/mobile/%s";
	private static String mSessionId;
	private static String mToken;
	private Uri mUri;
	
	public GuardianApiClient() {
		//Current url "http://61.246.61.175:8080/icgwearable/mobile/%s"
		Uri.Builder builder = new Uri.Builder();
		mUri = builder.scheme("http")
		    .encodedAuthority("61.246.61.175:8080")
		    .appendPath("icgwearable")
		    .appendPath("mobile").build();
	}
	public JSONResponse login(String user, String password) {
		Uri uri = mUri.buildUpon().appendPath(Def.REQUEST_USERLOGIN).build();
		try {
			URL url = new URL(uri.toString());
			HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.setRequestMethod("POST");
			urlConnection.setRequestProperty("Content-Type", "application/json");
			urlConnection.setDoInput(true);
			urlConnection.setDoOutput(true);
			urlConnection.setUseCaches(false);

			JSONObject jsonParam = new JSONObject();
			jsonParam.put(Def.KEY_USERNAME, user);
			jsonParam.put(Def.KEY_PASSWORD, password);
			jsonParam.put(Def.KEY_FORCELOGIN, "yes");
			OutputStream os = urlConnection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(jsonParam.toString());
            writer.flush();
            writer.close();
            int status = urlConnection.getResponseCode();
            if (status == HttpURLConnection.HTTP_OK) {
            	JSONResponse result = (JSONResponse) getResponseJSON(urlConnection.getInputStream(), JSONResponse.class);
            	mSessionId = result.getReturn().getResponseSummary().getSessionId();
            	mToken = result.getReturn().getResults().getToken();
            	return result;
            }
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private String concateUrl(String request) {
		return String.format(mUrlPrepend, request);
	}
	
	private Object getResponseJSON(InputStream is, Class<?> class_type) {
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line;
        try {
			while ((line = br.readLine()) != null) {
			    sb.append(line+"\n");
			}
	        br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
        return new Gson().fromJson(sb.toString(), class_type);
	}
	
	public JSONResponse getChildrenList() {
		Uri uri = mUri.buildUpon().appendPath(Def.REQUEST_GET_CHILDREN_LIST).
				appendPath(mToken).build();
		try {
			URL url = new URL(uri.toString());
			HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.setRequestMethod("GET");
			urlConnection.setRequestProperty("Content-Type", "application/json");
			urlConnection.setDoInput(true);
			urlConnection.setDoOutput(false);
			urlConnection.setUseCaches(false);
            
			int status = urlConnection.getResponseCode();
            if (status == HttpURLConnection.HTTP_OK) {
            	JSONResponse result = (JSONResponse) getResponseJSON(urlConnection.getInputStream(), JSONResponse.class);
            	if (!TextUtils.isEmpty(result.getReturn().getResponseSummary().getStatusCode())) {
            		Student[] students = result.getReturn().getResults().getStudents();
            		return result;
            	}
            }
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void setToken(String token) {
		mToken = token;
	}
	
	public JSONResponse getDeviceEventReport(String student_id, String event_id, String duration) {

		Uri uri = mUri.buildUpon().appendPath(Def.REQUEST_GET_DEVICE_EVENT_REPORT).
				appendPath(mToken).
				appendPath(student_id).
				appendPath(event_id).
				appendPath(duration).build();
		try {

			URL url = new URL(uri.toString());
			HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.setRequestMethod("GET");
			urlConnection.setRequestProperty("Content-Type", "application/json");
			urlConnection.setDoInput(true);
			urlConnection.setDoOutput(false);
			urlConnection.setUseCaches(false);
            
			int status = urlConnection.getResponseCode();
            if (status == HttpURLConnection.HTTP_OK) {
            	JSONResponse result = (JSONResponse) getResponseJSON(urlConnection.getInputStream(), JSONResponse.class);
            	String statusCode = result.getReturn().getResponseSummary().getStatusCode();
            	if (TextUtils.equals(statusCode, Def.RET_SUCCESS_2) || TextUtils.equals(statusCode, Def.RET_SUCCESS_1)) {
            		Device[] devices = result.getReturn().getResults().getDevices();
            		return result;
            	} else {
            		Log.e(TAG, "status code: " + statusCode+ ", Error message: " + result.getReturn().getResponseSummary().getErrorMessage());
            	}
            }
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
