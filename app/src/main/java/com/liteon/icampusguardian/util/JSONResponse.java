package com.liteon.icampusguardian.util;

import com.google.gson.annotations.SerializedName;

public class JSONResponse {
	
	@SerializedName("Return")
	private Return Return;

	/**
	 * @return the return
	 */
	public Return getReturn() {
		return Return;
	}

	/**
	 * @param return1 the return to set
	 */
	public void setReturn(Return return1) {
		Return = return1;
	}

	public static class Return {
        @SerializedName("Type")
        private String Type;
        @SerializedName("ResponseSummary")
        private ResponseSummary ResponseSummary;

        @SerializedName("Results")
        private Results Results;

        /**
         * @return the results
         */
        public Results getResults() {
            return Results;
        }

        /**
         * @param results the results to set
         */
        public void setResults(Results results) {
            Results = results;
        }

        /**
         * @return the type
         */
        public String getType() {
            return Type;
        }

		/**
		 * @param type the type to set
		 */
		public void setType(String type) {
			Type = type;
		}
		/**
		 * @return the responseSummary
		 */
		public ResponseSummary getResponseSummary() {
			return ResponseSummary;
		}
		/**
		 * @param responseSummary the responseSummary to set
		 */
		public void setResponseSummary(ResponseSummary responseSummary) {
			ResponseSummary = responseSummary;
		}
	}
	
	public static class ResponseSummary{
		@SerializedName("StatusCode")
		private String StatusCode;
		@SerializedName("StatusMessage")
		private String ErrorMessage;
		@SerializedName("token")
		private String SessionId;
		/**
		 * @return the statusCode
		 */
		public String getStatusCode() {
			return StatusCode;
		}
		/**
		 * @param statusCode the statusCode to set
		 */
		public void setStatusCode(String statusCode) {
			StatusCode = statusCode;
		}
		/**
		 * @return the errorMessage
		 */
		public String getErrorMessage() {
			return ErrorMessage;
		}
		/**
		 * @param errorMessage the errorMessage to set
		 */
		public void setErrorMessage(String errorMessage) {
			ErrorMessage = errorMessage;
		}
		/**
		 * @return the sessionId
		 */
		public String getSessionId() {
			return SessionId;
		}
		/**
		 * @param sessionId the sessionId to set
		 */
		public void setSessionId(String sessionId) {
			SessionId = sessionId;
		}
	}
	
	public static class Results {
		@SerializedName("students")
		private Student[] students;
		@SerializedName("token")
		private String token;
		@SerializedName("AccountId")
		private String AccountId;
		@SerializedName("event_id")
		private String event_id;
		@SerializedName("event_name")
		private String event_name;
		@SerializedName("devices")
		private Device devices[];
        /**
         * API 18
         */
        @SerializedName("student_id")
        private int student_id;
        @SerializedName("nickname")
        private String nickname;
        @SerializedName("roll_no")
        private int roll_no;
        @SerializedName("uuid")
        private String uuid;

        public int getStudent_id() {
            return student_id;
        }

        public void setStudent_id(int student_id) {
            this.student_id = student_id;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public int getRoll_no() {
            return roll_no;
        }

        public void setRoll_no(int roll_no) {
            this.roll_no = roll_no;
        }

        public String getUuid() {
            return uuid;
        }

        public void setUuid(String uuid) {
            this.uuid = uuid;
        }

        /**
		 * API 36
		 */
		@SerializedName("name")
		private String name;
		@SerializedName("mobile_number")
		private String mobile_number;
		@SerializedName("username")
		private String username;
		@SerializedName("account_name")
		private String account_name;
		/**
		 * @return the name
		 */
		public String getName() {
			return name;
		}

		/**
		 * @param name the name to set
		 */
		public void setName(String name) {
			this.name = name;
		}

		/**
		 * @return the mobile_number
		 */
		public String getMobile_number() {
			return mobile_number;
		}

		/**
		 * @param mobile_number the mobile_number to set
		 */
		public void setMobile_number(String mobile_number) {
			this.mobile_number = mobile_number;
		}

		/**
		 * @return the username
		 */
		public String getUsername() {
			return username;
		}

		/**
		 * @param username the username to set
		 */
		public void setUsername(String username) {
			this.username = username;
		}

		/**
		 * @return the account_name
		 */
		public String getAccount_name() {
			return account_name;
		}

		/**
		 * @param account_name the account_name to set
		 */
		public void setAccount_name(String account_name) {
			this.account_name = account_name;
		}

		@SerializedName("event_occured_date")
		private String event_occured_date;
		/**
		 * @return the event_occured_date
		 */
		public String getEvent_occured_date() {
			return event_occured_date;
		}

		/**
		 * @param event_occured_date the event_occured_date to set
		 */
		public void setEvent_occured_date(String event_occured_date) {
			this.event_occured_date = event_occured_date;
		}

		@SerializedName("latitude")
		private String latitude;
		@SerializedName("longitude")
		private String longitude;
		/**
		 * @return the latitude
		 */
		public String getLatitude() {
			return latitude;
		}

		/**
		 * @param latitude the latitude to set
		 */
		public void setLatitude(String latitude) {
			this.latitude = latitude;
		}

		/**
		 * @return the longitude
		 */
		public String getLongitude() {
			return longitude;
		}

		/**
		 * @param longitude the longitude to set
		 */
		public void setLongitude(String longitude) {
			this.longitude = longitude;
		}

		/**
		 * @return the event_id
		 */
		public String getEvent_id() {
			return event_id;
		}

		/**
		 * @param event_id the event_id to set
		 */
		public void setEvent_id(String event_id) {
			this.event_id = event_id;
		}

		/**
		 * @return the event_name
		 */
		public String getEvent_name() {
			return event_name;
		}

		/**
		 * @param event_name the event_name to set
		 */
		public void setEvent_name(String event_name) {
			this.event_name = event_name;
		}

		/**
		 * @return the devices
		 */
		public Device[] getDevices() {
			return devices;
		}

		/**
		 * @param devices the devices to set
		 */
		public void setDevices(Device[] devices) {
			this.devices = devices;
		}

		/**
		 * @return the students
		 */
		public Student[] getStudents() {
			return students;
		}

		/**
		 * @param students the students to set
		 */
		public void setStudents(Student[] students) {
			this.students = students;
		}
		/**
		 * @return the token
		 */
		public String getToken() {
			return token;
		}

		/**
		 * @param token the token to set
		 */
		public void setToken(String token) {
			this.token = token;
		}

		/**
		 * @return the accountId
		 */
		public String getAccountId() {
			return AccountId;
		}

		/**
		 * @param accountId the accountId to set
		 */
		public void setAccountId(String accountId) {
			AccountId = accountId;
		}
	}
	
	public static class Parent {
		private String account_name;//prefix of email
		private String username;//email
		private String password;
		private String given_name;
		private String mobile_number;
		private String token;

		public String getToken() {
			return token;
		}

		public void setToken(String token) {
			this.token = token;
		}


		public String getMobile_number() {
			return mobile_number;
		}

		public void setMobile_number(String mobile_number) {
			this.mobile_number = mobile_number;
		}

		/**
		 * @return the account_name
		 */
		public String getAccount_name() {
			return account_name;
		}
		/**
		 * @param account_name the account_name to set
		 */
		public void setAccount_name(String account_name) {
			this.account_name = account_name;
		}
		/**
		 * @return the username
		 */
		public String getUsername() {
			return username;
		}
		/**
		 * @param username the username to set
		 */
		public void setUsername(String username) {
			this.username = username;
		}
		/**
		 * @return the password
		 */
		public String getPassword() {
			return password;
		}
		/**
		 * @param password the password to set
		 */
		public void setPassword(String password) {
			this.password = password;
		}
		/**
		 * @return the given_name
		 */
		public String getGiven_name() {
			return given_name;
		}
		/**
		 * @param given_name the given_name to set
		 */
		public void setGiven_name(String given_name) {
			this.given_name = given_name;
		}
	}
	
	public static class Student {
		@SerializedName("student_id")
		private int student_id;
		@SerializedName("name")
		private String name;
		@SerializedName("nickname")
		private String nickname;
		@SerializedName("class")
		private String _class;
		@SerializedName("roll_no")
		private int roll_no;
		@SerializedName("height")
		private String height;
		@SerializedName("weight")
		private String weight;
		@SerializedName("dob")
		private String dob;
		@SerializedName("gender")
		private String gender;
		@SerializedName("uuid")
		private String uuid; 
		
		private int isDelete;
		
		/**
		 * @return the isDelete
		 */
		public int getIsDelete() {
			return isDelete;
		}
		/**
		 * @param isDelete the isDelete to set
		 */
		public void setIsDelete(int isDelete) {
			this.isDelete = isDelete;
		}
		/**
		 * @return the student_id
		 */
		public String getStudent_id() {
			return Integer.toString(student_id);
		}
		/**
		 * @param student_id the student_id to set
		 */
		public void setStudent_id(int student_id) {
			this.student_id = student_id;
		}
		/**
		 * @return the name
		 */
		public String getName() {
			return name;
		}
		/**
		 * @param name the name to set
		 */
		public void setName(String name) {
			this.name = name;
		}
		/**
		 * @return the nickname
		 */
		public String getNickname() {
			return nickname;
		}
		/**
		 * @param nickname the nickname to set
		 */
		public void setNickname(String nickname) {
			this.nickname = nickname;
		}
		/**
		 * @return the _class
		 */
		public String get_class() {
			return _class;
		}
		/**
		 * @param _class the _class to set
		 */
		public void set_class(String _class) {
			this._class = _class;
		}
		/**
		 * @return the roll_no
		 */
		public String getRoll_no() {
			return Integer.toString(roll_no);
		}
		/**
		 * @param roll_no the roll_no to set
		 */
		public void setRoll_no(int roll_no) {
			this.roll_no = roll_no;
		}
		/**
		 * @return the height
		 */
		public String getHeight() {
			return height;
		}
		/**
		 * @param height the height to set
		 */
		public void setHeight(String height) {
			this.height = height;
		}
		/**
		 * @return the weight
		 */
		public String getWeight() {
			return weight;
		}
		/**
		 * @param weight the weight to set
		 */
		public void setWeight(String weight) {
			this.weight = weight;
		}
		/**
		 * @return the dob
		 */
		public String getDob() {
			return dob;
		}
		/**
		 * @param dob the dob to set
		 */
		public void setDob(String dob) {
			this.dob = dob;
		}
		/**
		 * @return the gender
		 */
		public String getGender() {
			return gender;
		}
		/**
		 * @param gender the gender to set
		 */
		public void setGender(String gender) {
			this.gender = gender;
		}
		/**
		 * @return the uuid
		 */
		public String getUuid() {
			return uuid;
		}
		/**
		 * @param uuid the uuid to set
		 */
		public void setUuid(String uuid) {
			this.uuid = uuid;
		}
		
	}
	public static class Device {
		@SerializedName("uuid")
		private String uuid;
		@SerializedName("device_active")
		private String device_active;
		@SerializedName("activation_date")
		private String activation_date;
		@SerializedName("device_events")
		private DeviceEvent[] device_events;
		/**
		 * @return the uuid
		 */
		public String getUuid() {
			return uuid;
		}
		/**
		 * @param uuid the uuid to set
		 */
		public void setUuid(String uuid) {
			this.uuid = uuid;
		}
		/**
		 * @return the device_active
		 */
		public String getDevice_active() {
			return device_active;
		}
		/**
		 * @param device_active the device_active to set
		 */
		public void setDevice_active(String device_active) {
			this.device_active = device_active;
		}
		/**
		 * @return the activation_date
		 */
		public String getActivation_date() {
			return activation_date;
		}
		/**
		 * @param activation_date the activation_date to set
		 */
		public void setActivation_date(String activation_date) {
			this.activation_date = activation_date;
		}
		/**
		 * @return the device_events
		 */
		public DeviceEvent[] getDevice_events() {
			return device_events;
		}
		/**
		 * @param device_events the device_events to set
		 */
		public void setDevice_events(DeviceEvent[] device_events) {
			this.device_events = device_events;
		}
	}
	public static class DeviceEvent {
        @SerializedName("gps_data_code")
		private String gps_data_code;//"L2C",
    	@SerializedName("gps_location_data")
    	private String gps_location_data;//"25.0220,121.5271",
    	@SerializedName("event_occured_date")
    	private String event_occured_date;//"2017-07-03 10:25:27.0"
		/**
		 * @return the gps_data_code
		 */
		public String getGps_data_code() {
			return gps_data_code;
		}
		/**
		 * @param gps_data_code the gps_data_code to set
		 */
		public void setGps_data_code(String gps_data_code) {
			this.gps_data_code = gps_data_code;
		}
		/**
		 * @return the gps_location_data
		 */
		public String getGps_location_data() {
			return gps_location_data;
		}
		/**
		 * @param gps_location_data the gps_location_data to set
		 */
		public void setGps_location_data(String gps_location_data) {
			this.gps_location_data = gps_location_data;
		}
		/**
		 * @return the event_occured_date
		 */
		public String getEvent_occured_date() {
			return event_occured_date;
		}
		/**
		 * @param event_occured_date the event_occured_date to set
		 */
		public void setEvent_occured_date(String event_occured_date) {
			this.event_occured_date = event_occured_date;
		}
	}
}
