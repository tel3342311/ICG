package com.liteon.icampusguardian.db;
import android.provider.BaseColumns;

public class AccountTable {

	public AccountTable() {}
	public static final String AUTHORITY = "com.liteon.icampusguardian";
	
	public static abstract class AccountEntry implements BaseColumns {
		public static final String TABLE_NAME = "account_entry";
		public static final String COLUMN_NAME_USER_NAME = "username";
        public static final String COLUMN_NAME_PASSWORD	 = "password";
        public static final String COLUMN_NAME_ROLE_TYPE = "role_type";
        public static final String COLUMN_NAME_UUID = "uuid";
        public static final String COLUMN_NAME_ACCOUNT_NAME = "account_name";
        public static final String COLUMN_NAME_GIVEN_NAME = "name";
        public static final String COLUMN_NAME_NICK_NAME = "nick_name";
        public static final String COLUMN_NAME_GENDER = "gender";
        public static final String COLUMN_NAME_DOB = "dob";
        public static final String COLUMN_NAME_HEIGHT = "height";
        public static final String COLUMN_NAME_WEIGHT = "weight";
	}
	
}
