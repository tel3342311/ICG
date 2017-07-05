package com.liteon.icampusguardian.util;

public class Def {
	//RET CODE there are two kind of success code
	public static final String RET_SUCCESS_1 = "SUC01";
	public static final String RET_SUCCESS_2 = "WSUC01";
	//Default user 
	public static final String USER = "admin1@parent.com";
	public static final String PASSWORD = "password";
	//JSON label
	public static final String KEY_RETURN = "return";
	public static final String KEY_TYPE = "type";
	public static final String KEY_RESPONSESUMMARY = "ResponseSummary";
	public static final String KEY_STATUSCODE = "StatusCode";
	public static final String KEY_SESSIONID = "SessionId";
	public static final String KEY_ERRORMESSAGE = "ErrorMessage";
	
	//API 02 login
	public static final String REQUEST_USERLOGIN = "UserLogin";
	public static final String KEY_TYPE_USERLOGIN = "user.UserLogin";
	public static final String KEY_USERNAME = "username";
	public static final String KEY_PASSWORD = "password";
	public static final String KEY_FORCELOGIN = "forcelogin";
	
	//API 07 get student list
	public static final String REQUEST_GET_CHILDREN_LIST = "StudentList";
	
	//API 19 get device event report
	public static final String REQUEST_GET_DEVICE_EVENT_REPORT = "DeviceEventReport";

	//EVENT ID LIST
	public static final String EVENT_ID_SOS_ALERT = "11";
	public static final String EVENT_ID_SOS_REMOVE = "12";
	public static final String EVENT_ID_GPS_LOCATION = "17";
	
	//EVENT DURATION
	public static final String EVENT_DURATION_ONE_DAY = "1";
	public static final String EVENT_DURATION_WEEK = "7";
	public static final String EVENT_DURATION_MONTH = "30";
}
