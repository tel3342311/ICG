package com.liteon.icampusguardian.util;

public class Def {
	//SharePreference
	public final static String SHARE_PREFERENCE = "com.liteon.icampusguardian.PREFERENCE_FILE_KEY";
	public final static String SP_USER_TERM_READ = "com.liteon.icampusguardian.SP_USER_TERM_READ";
	public final static String SP_CURRENT_STUDENT = "com.liteon.icampusguardian.SP_CURRENT_STUDENT";
	public static final String SP_LOGIN_TOKEN = "com.liteon.icampusguardian.SP_LOGIN_TOKEN";
	public static final String SP_TARGET_CARLOS = "com.liteon.icampusguardian.SP_TARGET_CARLOS";
	public static final String SP_TARGET_STEPS = "com.liteon.icampusguardian.SP_TARGET_STEPS";
	public static final String SP_TARGET_WALKING = "com.liteon.icampusguardian.SP_TARGET_WALKING";
	public static final String SP_TARGET_RUNNING = "com.liteon.icampusguardian.SP_TARGET_RUNNING";
	public static final String SP_TARGET_CYCLING = "com.liteon.icampusguardian.SP_TARGET_CYCLING";
	public static final String SP_TARGET_SLEEPING = "com.liteon.icampusguardian.SP_TARGET_SLEEPING";
	public static final String SP_ALARM_MAP = "com.liteon.icampusguardian.SP_ALARM_MAP";
	public static final String SP_IMPROVE_PLAN = "com.liteon.icampusguardian.SP_IMPROVE_PLAN";
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
	//API 01 registration
	public static final String REQUEST_USER_REGISTRATION = "ParentUserRegistration";
	public static final String KEY_ACCOUNT_NAME = "account_name";
	//API 02 login
	public static final String REQUEST_USERLOGIN = "UserLogin";
	public static final String KEY_TYPE_USERLOGIN = "user.UserLogin";
	public static final String KEY_USERNAME = "username";
	public static final String KEY_PASSWORD = "password";
	public static final String KEY_FORCELOGIN = "forcelogin";
	
	//API 07 get student list
	public static final String REQUEST_GET_CHILDREN_LIST = "StudentList";
	
	//API 14 pair new device
	public static final String REQUEST_PAIR_NEW_DEVICE = "ParentUserDevicePair";
	//API 15 pair new device
	public static final String REQUEST_UNPAIR_DEVICE = "ParentUserDeviceUnPair";
	
	//API 19 get device event report
	public static final String REQUEST_GET_DEVICE_EVENT_REPORT = "DeviceEventReport";
	public static final String KEY_EMAIL = "email";
	//API 32 update child info
	public static final String REQUEST_UPDATE_CHILD_INFO = "UpdateStudentDetails";
	public static final String KEY_STUDENT_ID = "student_id";
	public static final String KEY_NAME = "name";
	public static final String KEY_NICKNAME = "nickname";
	public static final String KEY_CLASS = "101";
	public static final String KEY_ROLL_NO = "roll_no";
	public static final String KEY_HEIGHT  = "height";
	public static final String KEY_WEIGHT  = "weight";
	public static final String KEY_DOB = "dob";
	public static final String KEY_GENDER = "gender";
	public static final String KEY_UUID = "uuid";
	//API 33 reset password
	public static final String REQUEST_PASSWORD_REST = "PasswordResetRequest";
	public static final String KEY_USER_ROLE = "user_role";

	//EVENT ID LIST
	public static final String EVENT_ID_SOS_ALERT = "11";
	public static final String EVENT_ID_SOS_REMOVE = "12";
	public static final String EVENT_ID_GPS_LOCATION = "17";
	
	//EVENT DURATION
	public static final String EVENT_DURATION_ONE_DAY = "1";
	public static final String EVENT_DURATION_WEEK = "7";
	public static final String EVENT_DURATION_MONTH = "30";
	
	//Action
	public static final String ACTION_NOTIFY = "com.liteon.icampusguardian.ACTION_NOTIFY";
	public static final String EXTRA_NOTIFY_TYPE = "com.liteon.icampusguardian.EXTRA_NOTIFY_TYPE";
	public static final String EXTRA_SOS_LOCATION = "com.liteon.icampusguardian.EXTRA_SOS_LOCATION";

	//Intent EXTRA
	public static final String EXTRA_DISABLE_USERTREM_BOTTOM = "com.liteon.icampusguardian.EXTRA_DISABLE_USERTREM_BOTTOM";
	
}
