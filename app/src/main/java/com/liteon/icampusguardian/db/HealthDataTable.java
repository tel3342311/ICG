package com.liteon.icampusguardian.db;
import android.provider.BaseColumns;

public class HealthDataTable {

	public HealthDataTable() {}
	public static final String AUTHORITY = "com.liteon.icampusguardian";
	
	public static abstract class HealthDataEntry implements BaseColumns {
		public static final String TABLE_NAME = "healthy_data_entry";
        public static final String COLUMN_NAME_STUDENTID = "student_id";
        public static final String COLUMN_NAME_DATE = "date";
        public static final String COLUMN_NAME_VALUE = "value";
		public static final String COLUMN_NAME_SITUATION = "situation";
		public static final String COLUMN_NAME_DURATION = "duration";

		//Define situation of Healthy data
		public static final int SITUATION_WALKING = 2;
		public static final int SITUATION_RUNNING = 3;
		public static final int SITUATION_CYCLING = 4;
		public static final int SITUATION_SLEEP   = 5;
		public static final int SITUATION_FITNESS = 6;
		public static final int SITUATION_HEART   = 7;
		public static final int SITUATION_STEPS   = 8;
		public static final int SITUATION_CALOS   = 9;
	}
	
}
