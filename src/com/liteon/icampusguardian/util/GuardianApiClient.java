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
import com.liteon.icampusguardian.util.JSONResponse.Student;

import android.net.Uri;
import android.text.TextUtils;

public class GuardianApiClient {

	
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
		
		URL url;
		try {
			Uri uri = mUri.buildUpon().appendPath(Def.REQUEST_USERLOGIN).build();
			url = new URL(uri.toString());
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
		URL url;
		try {
			Uri uri = mUri.buildUpon().appendPath(Def.REQUEST_GET_CHILDREN_LIST).
					appendPath(mToken).build();
			url = new URL(uri.toString());
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
}
