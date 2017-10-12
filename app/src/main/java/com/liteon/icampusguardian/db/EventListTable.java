package com.liteon.icampusguardian.db;
import android.provider.BaseColumns;

public class EventListTable {

	public EventListTable() {}
	public static final String AUTHORITY = "com.liteon.icampusguardian";
	
	public static abstract class EventListEntry implements BaseColumns {
		public static final String TABLE_NAME = "event_list_entry";
		public static final String COLUMN_NAME_UUID = "uuid";
        public static final String COLUMN_NAME_EVENT_ID = "event_id";
        public static final String COLUMN_NAME_EVENT_NAME = "event_name";
        public static final String COLUMN_NAME_EVENT_SUBSCRIBE = "event_subscribe";
	}
	
}
