package com.liteon.icampusguardian.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.liteon.icampusguardian.util.JSONResponse.Student;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class GuardianApiClient {

	private static final String TAG = GuardianApiClient.class.getName();
	private static String mSessionId;
	private static String mToken;
	private static Uri mUri;
	private WeakReference<Context> mContext;

	private static GuardianApiClient mApiClient;

	public static GuardianApiClient getInstance(Context context) {
		if (mApiClient == null) {
			mApiClient = new GuardianApiClient(context);
		}
		return mApiClient;
	}

	public GuardianApiClient(Context context) {
		//Current url "http://icg.aricentcoe.com:8080/icgcloud/mobile/%s"
		SharedPreferences sp = context.getSharedPreferences(Def.SHARE_PREFERENCE, Context.MODE_PRIVATE);
		String url = sp.getString(Def.SP_URL, "icg.aricentcoe.com:8080");
		setServerUri(url);
		mContext = new WeakReference<>(context);
	}

	public JSONResponse login(String user, String password) {
		HttpURLConnection urlConnection = null;
		OutputStream os = null;
		InputStream is = null;
		Uri uri = mUri.buildUpon().appendPath(Def.REQUEST_USERLOGIN).build();
		try {
			URL url = new URL(uri.toString());
			urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.setRequestMethod("POST");
			urlConnection.setRequestProperty("Content-Type", "application/json");
			urlConnection.setDoInput(true);
			urlConnection.setDoOutput(true);
			urlConnection.setUseCaches(false);

			JSONObject jsonParam = new JSONObject();
			jsonParam.put(Def.KEY_USERNAME, user);
			jsonParam.put(Def.KEY_PASSWORD, password);
			//jsonParam.put(Def.KEY_FORCELOGIN, "yes");
			os = urlConnection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(jsonParam.toString());
            writer.flush();
            writer.close();
            int status = urlConnection.getResponseCode();
            if (status == HttpURLConnection.HTTP_OK) {
            	is = urlConnection.getInputStream();
            	JSONResponse result = (JSONResponse) getResponseJSON(is, JSONResponse.class);
            	if (result.getReturn() != null) {
            		mToken = result.getReturn().getResponseSummary().getSessionId();
            	}
            	return result;
            } else {
            	showError(status);
            }
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		} finally {
			if (urlConnection != null) {
                try {
                    urlConnection.getInputStream().close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                urlConnection.disconnect();
			}
		}
		return null;
	}
	
	public JSONResponse registerUser(String userEmail, String password, String profileName, String mobileNumber) {

		Uri uri = mUri.buildUpon().appendPath(Def.REQUEST_USER_REGISTRATION).build();
		HttpURLConnection urlConnection = null;
		OutputStream os = null;
		InputStream is = null;
		try {
			URL url = new URL(uri.toString());
			urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.setRequestMethod("POST");
			urlConnection.setRequestProperty("Content-Type", "application/json");
			urlConnection.setDoInput(true);
			urlConnection.setDoOutput(true);
			urlConnection.setUseCaches(false);

			JSONObject jsonParam = new JSONObject();
			jsonParam.put(Def.KEY_USERNAME, userEmail);
			jsonParam.put(Def.KEY_PASSWORD, password);
			jsonParam.put(Def.KEY_PROFILE_NAME, profileName);
			jsonParam.put(Def.KEY_MOBILE_NUMBER, mobileNumber);


			os = urlConnection.getOutputStream();
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
			writer.write(jsonParam.toString());
			writer.flush();
			writer.close();
			os.close();
			final int status = urlConnection.getResponseCode();
			if (status == HttpURLConnection.HTTP_OK) {
			    is = urlConnection.getInputStream();
				JSONResponse result = (JSONResponse) getResponseJSON(is, JSONResponse.class);
				is.close();
				showStatus(result);

				return result;
			} else {
				showError(status);
			}

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		} finally {
            if (urlConnection != null) {
                try {
                    urlConnection.getInputStream().close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                urlConnection.disconnect();
            }
        }
        return null;
	}
	
	public JSONResponse updateParentDetail(String given_name, String account_name, String password, String phoneNumber) {

		Uri uri = mUri.buildUpon().appendPath(Def.REQUEST_USER_UPDATE).appendPath(mToken).build();
		HttpURLConnection urlConnection = null;
		OutputStream os = null;
		InputStream is = null;
		try {
			URL url = new URL(uri.toString());
			urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.setRequestMethod("PUT");
			urlConnection.setRequestProperty("Content-Type", "application/json");
			urlConnection.setDoInput(true);
			urlConnection.setDoOutput(true);
			urlConnection.setUseCaches(false);

			JSONObject jsonParam = new JSONObject();
			jsonParam.put(Def.KEY_NAME, given_name);
			jsonParam.put(Def.KEY_ACCOUNT_NAME, account_name);
			jsonParam.put(Def.KEY_PASSWORD, password);

			os = urlConnection.getOutputStream();
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
			writer.write(jsonParam.toString());
			writer.flush();
			writer.close();
			os.close();
			final int status = urlConnection.getResponseCode();
			if (status == HttpURLConnection.HTTP_OK) {
				is = urlConnection.getInputStream();
				JSONResponse result = (JSONResponse) getResponseJSON(is, JSONResponse.class);
				is.close();
				//showStatus(result);
            	String statusCode = result.getReturn().getResponseSummary().getStatusCode();
        		Log.e(TAG, "status code: " + statusCode + ", Error message: " + result.getReturn().getResponseSummary().getErrorMessage());
				return result;
			} else {
				showError(status);
			}

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		} finally {
            if (urlConnection != null) {
                try {
                    urlConnection.getInputStream().close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                urlConnection.disconnect();
            }
		}
		return null;
	}
	
	public JSONResponse resetPassword(String userEmail) {

		Uri uri = mUri.buildUpon().appendPath(Def.REQUEST_PASSWORD_REST).build();
		HttpURLConnection urlConnection = null;
		OutputStream os = null;
		InputStream is = null;
		try {
			URL url = new URL(uri.toString());
			urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.setRequestMethod("POST");
			urlConnection.setRequestProperty("Content-Type", "application/json");
			urlConnection.setDoInput(true);
			urlConnection.setDoOutput(true);
			urlConnection.setUseCaches(false);

			JSONObject jsonParam = new JSONObject();
			jsonParam.put(Def.KEY_USERNAME, userEmail);
			jsonParam.put(Def.KEY_USER_ROLE, "parent_admin");

			os = urlConnection.getOutputStream();
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
			writer.write(jsonParam.toString());
			writer.flush();
			writer.close();
			os.close();
			final int status = urlConnection.getResponseCode();
			if (status == HttpURLConnection.HTTP_OK) {
				is = urlConnection.getInputStream();
				JSONResponse result = (JSONResponse) getResponseJSON(is, JSONResponse.class);
				is.close();
				return result;
			} else {
				showError(status);
			}

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JsonSyntaxException e) {
			e.printStackTrace();
			return null;
		} catch (JSONException e) {
			e.printStackTrace();
		} finally {
            if (urlConnection != null) {
                try {
                    urlConnection.getInputStream().close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                urlConnection.disconnect();
            }
		}
		return null;
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

		HttpURLConnection urlConnection = null;
		OutputStream os = null;
		InputStream is = null;
		Uri uri = mUri.buildUpon().appendPath(Def.REQUEST_GET_CHILDREN_LIST).
				appendPath(mToken).build();
		try {
			URL url = new URL(uri.toString());
			urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.setRequestMethod("GET");
			urlConnection.setRequestProperty("Content-Type", "application/json");
			urlConnection.setDoInput(true);
			urlConnection.setDoOutput(false);
			urlConnection.setUseCaches(false);
            
			int status = urlConnection.getResponseCode();
            if (status == HttpURLConnection.HTTP_OK) {
            	is = urlConnection.getInputStream();
            	JSONResponse result = (JSONResponse) getResponseJSON(is, JSONResponse.class);
				is.close();
            	if (!TextUtils.isEmpty(result.getReturn().getResponseSummary().getStatusCode())) {
            		return result;
            	}
            } else {
            	showError(status);
            }
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
            if (urlConnection != null) {
                try {
                    urlConnection.getInputStream().close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                urlConnection.disconnect();
            }
		}
		return null;
	}
	
	public void setToken(String token) {
		mToken = token;
	}
	
	public JSONResponse getDeviceEventReport(String student_id, String event_id, String duration) {

		Calendar calendar = Calendar.getInstance();
		Date date = calendar.getTime();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String endDate = sdf.format(date);
		calendar.add(Calendar.DAY_OF_YEAR, -Integer.parseInt(duration));
		date = calendar.getTime();
		String startDate = sdf.format(date);
		Uri uri = mUri.buildUpon().appendPath(Def.REQUEST_GET_DEVICE_EVENT_REPORT).
				appendPath(mToken).
				appendPath(student_id).
				appendPath(event_id).
				appendPath(startDate).
                appendPath(endDate).build();
		HttpURLConnection urlConnection = null;
		OutputStream os = null;
		InputStream is = null;
		try {

			URL url = new URL(uri.toString());
			urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.setRequestMethod("GET");
			urlConnection.setRequestProperty("Content-Type", "application/json");
			urlConnection.setDoInput(true);
			urlConnection.setDoOutput(false);
			urlConnection.setUseCaches(false);
            
			int status = urlConnection.getResponseCode();
            if (status == HttpURLConnection.HTTP_OK) {
            	is = urlConnection.getInputStream();
            	JSONResponse result = (JSONResponse) getResponseJSON(is, JSONResponse.class);
				is.close();
            	if (result == null || result.getReturn() == null) {
            	    return null;
                }
            	String statusCode = result.getReturn().getResponseSummary().getStatusCode();
            	if (TextUtils.equals(statusCode, Def.RET_SUCCESS_2) || TextUtils.equals(statusCode, Def.RET_SUCCESS_1)) {
            		return result;
            	} else {
            		Log.e(TAG, "status code: " + statusCode+ ", Error message: " + result.getReturn().getResponseSummary().getErrorMessage());
            	}
            } else {
            	showError(status);
            }
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
            if (urlConnection != null) {
                try {
                    urlConnection.getInputStream().close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                urlConnection.disconnect();
            }
		}
		return null;
	}
	
	public JSONResponse updateChildData(Student student) {

		Uri uri = mUri.buildUpon().appendPath(Def.REQUEST_UPDATE_CHILD_INFO).
					appendPath(mToken).build();
		HttpURLConnection urlConnection = null;
		OutputStream os = null;
		InputStream is = null;
		try {
			URL url = new URL(uri.toString());
			urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.setRequestMethod("PUT");
			urlConnection.setRequestProperty("Content-Type", "application/json");
			urlConnection.setDoInput(true);
			urlConnection.setDoOutput(true);
			urlConnection.setUseCaches(false);

			JSONObject jsonParam = new JSONObject();
			jsonParam.put(Def.KEY_UUID, student.getUuid());
			jsonParam.put(Def.KEY_STUDENT_ID, student.getStudent_id());
			jsonParam.put(Def.KEY_ROLL_NO, student.getRoll_no());
			jsonParam.put(Def.KEY_NICKNAME, student.getNickname());
			jsonParam.put(Def.KEY_HEIGHT, student.getHeight());
			jsonParam.put(Def.KEY_WEIGHT, student.getWeight());
			jsonParam.put(Def.KEY_DOB, student.getDob());
			jsonParam.put(Def.KEY_GENDER, student.getGender());
			
			os = urlConnection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(jsonParam.toString());
            writer.flush();
            writer.close();
			os.close();
            final int status = urlConnection.getResponseCode();
            if (status == HttpURLConnection.HTTP_OK) {
            	is = urlConnection.getInputStream();
            	JSONResponse result = (JSONResponse) getResponseJSON(is, JSONResponse.class);
            	is.close();
            	String statusCode = result.getReturn().getResponseSummary().getStatusCode();
            	Log.e(TAG, "status code: " + statusCode+ ", Error message: " + result.getReturn().getResponseSummary().getErrorMessage());
            	return result;
            } else {
            	showError(status);
            }
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                try {
                    urlConnection.getInputStream().close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                urlConnection.disconnect();
            }
		}
		return null;
	}
	
	public JSONResponse pairNewDevice(Student student) {

		Uri uri = mUri.buildUpon().appendPath(Def.REQUEST_PAIR_NEW_DEVICE).
				appendPath(mToken).
				appendPath(student.getUuid()).build();
		HttpURLConnection urlConnection = null;
		OutputStream os = null;
		InputStream is = null;
		try {
			URL url = new URL(uri.toString());
			urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.setRequestMethod("GET");
			urlConnection.setRequestProperty("Content-Type", "application/json");
			urlConnection.setDoInput(true);
			urlConnection.setDoOutput(false);
			urlConnection.setUseCaches(false);

			final int status = urlConnection.getResponseCode();
			if (status == HttpURLConnection.HTTP_OK) {
				is = urlConnection.getInputStream();
				JSONResponse result = (JSONResponse) getResponseJSON(urlConnection.getInputStream(),
						JSONResponse.class);
				is.close();
				String statusCode = result.getReturn().getResponseSummary().getStatusCode();
				Log.e(TAG, "status code: " + statusCode+ ", Error message: " + result.getReturn().getResponseSummary().getErrorMessage());
				return result;
			} else {
				showError(status);
			}

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
            if (urlConnection != null) {
                try {
                    urlConnection.getInputStream().close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                urlConnection.disconnect();
            }
		}
		return null;
	}
	
	public JSONResponse unpairDevice(Student student) {

		Uri uri = mUri.buildUpon().appendPath(Def.REQUEST_UNPAIR_DEVICE).
				appendPath(mToken).
				appendPath(student.getUuid()).build();
		HttpURLConnection urlConnection = null;
		OutputStream os = null;
		InputStream is = null;
		try {
			URL url = new URL(uri.toString());
			urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.setRequestMethod("POST");
			urlConnection.setRequestProperty("Content-Type", "application/json");
			urlConnection.setDoInput(true);
			urlConnection.setDoOutput(true);
			urlConnection.setUseCaches(false);

			final int status = urlConnection.getResponseCode();
			if (status == HttpURLConnection.HTTP_OK) {
				is = urlConnection.getInputStream();
				JSONResponse result = (JSONResponse) getResponseJSON(is, JSONResponse.class);
				is.close();
				String statusCode = result.getReturn().getResponseSummary().getStatusCode();
				Log.e(TAG, "status code: " + statusCode+ ", Error message: " + result.getReturn().getResponseSummary().getErrorMessage());
				return result;
			} else {
				showError(status);
			}

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
            if (urlConnection != null) {
                try {
                    urlConnection.getInputStream().close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                urlConnection.disconnect();
            }
		}
		return null;
	}
	
	public JSONResponse updateAppToken(String fireBaseInstanceToken) {

		Uri uri = mUri.buildUpon().appendPath(Def.REQUEST_UPDATE_APP_TOKEN).
				appendPath(mToken).build();
		HttpURLConnection urlConnection = null;
		OutputStream os = null;
		InputStream is = null;
		try {
			URL url = new URL(uri.toString());
			urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.setRequestMethod("PUT");
			urlConnection.setRequestProperty("Content-Type", "application/json");
			urlConnection.setDoInput(true);
			urlConnection.setDoOutput(true);
			urlConnection.setUseCaches(false);

			JSONObject jsonParam = new JSONObject();

			jsonParam.put(Def.KEY_APP_TOKEN, fireBaseInstanceToken);
			jsonParam.put(Def.KEY_APP_TYPE, Def.KEY_APP_TYPE_ANDROID);

			os = urlConnection.getOutputStream();
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
			writer.write(jsonParam.toString());
			writer.flush();
			writer.close();
			os.close();
			final int status = urlConnection.getResponseCode();
			if (status == HttpURLConnection.HTTP_OK) {
				is = urlConnection.getInputStream();
				JSONResponse result = (JSONResponse) getResponseJSON(is, JSONResponse.class);
				is.close();
				return result;
			} else {
				showError(status);
			}

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		} finally {
            if (urlConnection != null) {
                try {
                    urlConnection.getInputStream().close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                urlConnection.disconnect();
            }
		}
		return null;
	}

	public JSONResponse socialSignIn(String name, String email, String token) {
        Uri uri = mUri.buildUpon().appendPath(Def.REQUEST_FEDERATEDLOGIN).build();
		HttpURLConnection urlConnection = null;
		OutputStream os = null;
		InputStream is = null;
        try {

            URL url = new URL(uri.toString());
			urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
            urlConnection.setUseCaches(false);

            JSONObject jsonParam = new JSONObject();

            jsonParam.put(Def.KEY_SOCIAL_NAME, name);
            jsonParam.put(Def.KEY_SOCIAL_EMAIL, email);
            jsonParam.put(Def.KEY_SOCIAL_TOKEN, token);
            jsonParam.put(Def.KEY_SOCIAL_USERAGENT, Def.VALUE_SOCIAL_USERAGENT);
			os = urlConnection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(jsonParam.toString());
            writer.flush();
            writer.close();
            os.close();
            int status = urlConnection.getResponseCode();
            if (status == HttpURLConnection.HTTP_OK) {
            	is = urlConnection.getInputStream();
                JSONResponse result = (JSONResponse) getResponseJSON(is, JSONResponse.class);
				is.close();
                if (result.getReturn() != null) {
                    mToken = result.getReturn().getResponseSummary().getSessionId();
                }
                return result;
            } else {
                showError(status);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                try {
                    urlConnection.getInputStream().close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                urlConnection.disconnect();
            }
		}
		return null;
    }

	public JSONResponse getStudentLocation(Student student) {

		Uri uri = mUri.buildUpon().appendPath(Def.REQUEST_GET_CHILDREN_LOCATION).
				appendPath(mToken).
				appendPath(student.getUuid()).build();
		HttpURLConnection urlConnection = null;
		OutputStream os = null;
		InputStream is = null;
		try {

			URL url = new URL(uri.toString());
			urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.setRequestMethod("GET");
			urlConnection.setRequestProperty("Content-Type", "application/json");
			urlConnection.setDoInput(true);
			urlConnection.setDoOutput(false);
			urlConnection.setUseCaches(false);

			int status = urlConnection.getResponseCode();
            if (status == HttpURLConnection.HTTP_OK) {
            	is = urlConnection.getInputStream();
            	JSONResponse result = (JSONResponse) getResponseJSON(is, JSONResponse.class);
            	is.close();
            	String statusCode = result.getReturn().getResponseSummary().getStatusCode();
            	if (TextUtils.equals(statusCode, Def.RET_SUCCESS_2) || TextUtils.equals(statusCode, Def.RET_SUCCESS_1)) {
                    if (result.getReturn().getResults() != null) {
                        Log.e(TAG, "lat: " + result.getReturn().getResults().getLatitude() + ", longtitude " + result.getReturn().getResults().getLongitude());
                    } else {
                        Log.e(TAG, "status code: " + statusCode+ ", Error message: " + result.getReturn().getResponseSummary().getErrorMessage());
                    }
            	} else {
            		Log.e(TAG, "status code: " + statusCode+ ", Error message: " + result.getReturn().getResponseSummary().getErrorMessage());
            	}
            	return result;
            } else {
            	showError(status);
            }
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
            if (urlConnection != null) {
                try {
                    urlConnection.getInputStream().close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                urlConnection.disconnect();
            }
		}
		return null;
	}
	
	public JSONResponse getUserDetail() {

		Uri uri = mUri.buildUpon().appendPath(Def.REQUEST_USER_DETAIL).
				appendPath(mToken).build();
		HttpURLConnection urlConnection = null;
		OutputStream os = null;
		InputStream is = null;
		try {

			URL url = new URL(uri.toString());
			urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.setRequestMethod("GET");
			urlConnection.setRequestProperty("Content-Type", "application/json");
			urlConnection.setDoInput(true);
			urlConnection.setDoOutput(false);
			urlConnection.setUseCaches(false);
            
			int status = urlConnection.getResponseCode();
            if (status == HttpURLConnection.HTTP_OK) {
            	is = urlConnection.getInputStream();
            	JSONResponse result = (JSONResponse) getResponseJSON(is, JSONResponse.class);
            	is.close();
            	String statusCode = result.getReturn().getResponseSummary().getStatusCode();
            	if (TextUtils.equals(statusCode, Def.RET_SUCCESS_2) || TextUtils.equals(statusCode, Def.RET_SUCCESS_1)) {
            		Log.e(TAG, "lat: " + result.getReturn().getResults().getLatitude() + ", longtitude " + result.getReturn().getResults().getLongitude());
            	} else {
            		Log.e(TAG, "status code: " + statusCode+ ", Error message: " + result.getReturn().getResponseSummary().getErrorMessage());
            	}
            	return result;
            } else {
            	showError(status);
            }
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
            if (urlConnection != null) {
                try {
                    urlConnection.getInputStream().close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                urlConnection.disconnect();
            }
		}
		return null;
	}

	public JSONResponse grantTeacherAccessToSleepData(Student student) {
        Uri uri = mUri.buildUpon().appendPath(Def.REQUEST_GRANT_TEDETAIL).
                appendPath(mToken).
                appendPath(student.getStudent_id()).build();
		HttpURLConnection urlConnection = null;
		OutputStream os = null;
		InputStream is = null;
        try {

            URL url = new URL(uri.toString());
			urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(false);
            urlConnection.setUseCaches(false);

            int status = urlConnection.getResponseCode();
            if (status == HttpURLConnection.HTTP_OK) {
            	is = urlConnection.getInputStream();
                JSONResponse result = (JSONResponse) getResponseJSON(is, JSONResponse.class);
                is.close();
                String statusCode = result.getReturn().getResponseSummary().getStatusCode();
                if (TextUtils.equals(statusCode, Def.RET_SUCCESS_2) || TextUtils.equals(statusCode, Def.RET_SUCCESS_1)) {

                } else {
                    Log.e(TAG, "status code: " + statusCode+ ", Error message: " + result.getReturn().getResponseSummary().getErrorMessage());
                }
                return result;
            } else {
                showError(status);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                try {
                    urlConnection.getInputStream().close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                urlConnection.disconnect();
            }
		}
        return null;
    }

    public JSONResponse getHealthyData(Student student, String startDate , String endDate) {
        Uri.Builder builder = new Uri.Builder();
        //To use Web API 02 StudentActivity
	    Uri uri = builder.scheme("http")
                .encodedAuthority(mUri.getEncodedAuthority())
                .appendPath("icgcloud")
                .appendPath("web")
                .appendPath(Def.REQUEST_STUDENT_ACTIVITY)
                .appendPath(mToken).build();

        HttpURLConnection urlConnection = null;
        OutputStream os = null;
        InputStream is = null;
        try {

            URL url = new URL(uri.toString());
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
            urlConnection.setUseCaches(false);

            JSONObject jsonParam = new JSONObject();

            jsonParam.put(Def.KEY_MEASURE_TYPE, "fitness, steps, activity, heartrate, calories, sleep");
            jsonParam.put(Def.KEY_START_DATE, startDate);
            jsonParam.put(Def.KEY_END_DATE, endDate);
            jsonParam.put(Def.KEY_STUDENT_ID, student.getStudent_id());

            os = urlConnection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(jsonParam.toString());
            writer.flush();
            writer.close();
            os.close();

            int status = urlConnection.getResponseCode();
            if (status == HttpURLConnection.HTTP_OK) {
                is = urlConnection.getInputStream();
                JSONResponse result = (JSONResponse) getResponseJSON(is, JSONResponse.class);
                is.close();
                String statusCode = result.getReturn().getResponseSummary().getStatusCode();
                if (TextUtils.equals(statusCode, Def.RET_SUCCESS_2) || TextUtils.equals(statusCode, Def.RET_SUCCESS_1)) {

                } else {
                    Log.e(TAG, "status code: " + statusCode+ ", Error message: " + result.getReturn().getResponseSummary().getErrorMessage());
                }
                return result;
            } else {
                showError(status);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                try {
                    urlConnection.getInputStream().close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                urlConnection.disconnect();
            }
        }
        return null;
    }

	private void showError(int status) {
		final int status_code = status; 
		if (mContext.get() != null) {
    		((Activity)mContext.get()).runOnUiThread(() -> Toast.makeText(mContext.get(), "Error : Http response " + status_code, Toast.LENGTH_SHORT).show());
    	}
	}
	
	private void showStatus(JSONResponse result) {
		final String errorCode = result.getReturn().getResponseSummary().getStatusCode();
    	final String errorMessage = result.getReturn().getResponseSummary().getErrorMessage();
    	if (mContext.get() != null) {
    		((Activity)mContext.get()).runOnUiThread(() -> Toast.makeText(mContext.get(), "Error Code " + errorCode + ", Message: " + errorMessage, Toast.LENGTH_SHORT).show());
    	}
	}
	
	public static Uri getServerUri() {
		return mUri;
	}

	public static void setServerUri(String url) {
        Uri.Builder builder = new Uri.Builder();
        mUri = builder.scheme("http")
                .encodedAuthority(url)
                .appendPath("icgcloud")
                .appendPath("mobile").build();
    }
}
