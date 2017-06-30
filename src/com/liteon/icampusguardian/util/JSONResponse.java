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
		 * @return the type
		 */
		public String getType() {
			return Type;
		}
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
		@SerializedName("ErrorMessage")
		private String ErrorMessage;
		@SerializedName("SessionId")
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
	
	public static class Student {
		@SerializedName("students_id")
		private String student_id;
		@SerializedName("name")
		private String name;
		@SerializedName("nickname")
		private String nickname;
		@SerializedName("class")
		private String _class;
		@SerializedName("roll_no")
		private String roll_no;
		@SerializedName("height")
		private int height;
		@SerializedName("weight")
		private int weight;
		@SerializedName("dob")
		private String dob;
		@SerializedName("gender")
		private String gender;
		@SerializedName("uuid")
		private String uuid; //"f81d4fae-7dec-11d0-a765-00a0c91e6bf6"
		/**
		 * @return the student_id
		 */
		public String getStudent_id() {
			return student_id;
		}
		/**
		 * @param student_id the student_id to set
		 */
		public void setStudent_id(String student_id) {
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
			return roll_no;
		}
		/**
		 * @param roll_no the roll_no to set
		 */
		public void setRoll_no(String roll_no) {
			this.roll_no = roll_no;
		}
		/**
		 * @return the height
		 */
		public int getHeight() {
			return height;
		}
		/**
		 * @param height the height to set
		 */
		public void setHeight(int height) {
			this.height = height;
		}
		/**
		 * @return the weight
		 */
		public int getWeight() {
			return weight;
		}
		/**
		 * @param weight the weight to set
		 */
		public void setWeight(int weight) {
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
}
