package com.liteon.icampusguardian.db;

import java.util.List;

import com.google.android.gms.actions.ItemListIntents;
import com.liteon.icampusguardian.db.AccountTable.AccountEntry;
import com.liteon.icampusguardian.db.ChildLocationTable.ChildLocationEntry;
import com.liteon.icampusguardian.db.ChildTable.ChildEntry;
import com.liteon.icampusguardian.db.EventListTable.EventListEntry;
import com.liteon.icampusguardian.util.JSONResponse.Student;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
	private static DBHelper mInstance = null;
	public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "data.db";
    private static final String TEXT_TYPE = " TEXT";
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String COMMA_SEP = ",";
    
    //ACCOUNT
    public static final String SQL_QUERY_ALL_ACCOUNT_DATA = "SELECT * FROM " + AccountEntry.TABLE_NAME;
    private static final String SQL_CREATE_ACCOUNT_TABLE =
    	    "CREATE TABLE " + AccountEntry.TABLE_NAME + " (" +
    	    AccountEntry._ID + " INTEGER PRIMARY KEY" + COMMA_SEP +
    	    AccountEntry.COLUMN_NAME_USER_NAME + TEXT_TYPE + COMMA_SEP +
    	    AccountEntry.COLUMN_NAME_PASSWORD + TEXT_TYPE + COMMA_SEP +
    	    AccountEntry.COLUMN_NAME_UUID + TEXT_TYPE + COMMA_SEP +
    	    AccountEntry.COLUMN_NAME_ROLE_TYPE + TEXT_TYPE + COMMA_SEP +
    	    AccountEntry.COLUMN_NAME_ACCOUNT_NAME + TEXT_TYPE + COMMA_SEP +
    	    AccountEntry.COLUMN_NAME_GIVEN_NAME + TEXT_TYPE + COMMA_SEP +
    	    AccountEntry.COLUMN_NAME_NICK_NAME + TEXT_TYPE + COMMA_SEP +
    	    AccountEntry.COLUMN_NAME_GENDER + TEXT_TYPE + COMMA_SEP +
    	    AccountEntry.COLUMN_NAME_HEIGHT + INTEGER_TYPE + COMMA_SEP +
    	    AccountEntry.COLUMN_NAME_WEIGHT + INTEGER_TYPE + COMMA_SEP +
    	    AccountEntry.COLUMN_NAME_DOB + TEXT_TYPE +
    	    " )";
    private static final String SQL_DELETE_ACCOUNT_TABLE =
    	    "DROP TABLE IF EXISTS " + AccountEntry.TABLE_NAME;
    
    //Child data
    public static final String SQL_QUERY_ALL_CHILDREN_DATA = "SELECT * FROM " + ChildEntry.TABLE_NAME;
    private static final String SQL_CREATE_CHILDREN_TABLE =
    	    "CREATE TABLE " + ChildEntry.TABLE_NAME + " (" +
    	    		ChildEntry._ID + " INTEGER PRIMARY KEY" + COMMA_SEP +
    	    ChildEntry.COLUMN_NAME_UUID + TEXT_TYPE + COMMA_SEP +
    	    ChildEntry.COLUMN_NAME_GIVEN_NAME + TEXT_TYPE + COMMA_SEP +
    	    ChildEntry.COLUMN_NAME_NICK_NAME + TEXT_TYPE + COMMA_SEP +
    	    ChildEntry.COLUMN_NAME_GENDER + TEXT_TYPE + COMMA_SEP +
    	    ChildEntry.COLUMN_NAME_HEIGHT + INTEGER_TYPE + COMMA_SEP +
    	    ChildEntry.COLUMN_NAME_WEIGHT + INTEGER_TYPE + COMMA_SEP +
    	    ChildEntry.COLUMN_NAME_DOB + TEXT_TYPE +
    	    " )";
    private static final String SQL_DELETE_CHILD_TABLE =
    	    "DROP TABLE IF EXISTS " + ChildEntry.TABLE_NAME;
    
    //Event list data
    public static final String SQL_QUERY_ALL_EVENT_DATA = "SELECT * FROM " + EventListEntry.TABLE_NAME;
    private static final String SQL_CREATE_EVENT_TABLE =
    	    "CREATE TABLE " + EventListEntry.TABLE_NAME + " (" +
    	    		EventListEntry._ID + " INTEGER PRIMARY KEY" + COMMA_SEP +
    	    EventListEntry.COLUMN_NAME_UUID + TEXT_TYPE + COMMA_SEP +
    	    EventListEntry.COLUMN_NAME_EVENT_ID + TEXT_TYPE + COMMA_SEP +
    	    EventListEntry.COLUMN_NAME_EVENT_SUBSCRIBE + TEXT_TYPE +
    	    " )";
    private static final String SQL_DELETE_EVENT_TABLE =
    	    "DROP TABLE IF EXISTS " + EventListEntry.TABLE_NAME;
    
    public static DBHelper getInstance(Context ctx) {
        if (mInstance == null) {
            mInstance = new DBHelper(ctx.getApplicationContext());
        }
        return mInstance;
    }
    //childe location data
    public static final String SQL_QUERY_CHILD_LOCATION_DATA = "SELECT * FROM " + ChildLocationEntry.TABLE_NAME;
    private static final String SQL_CREATE_CHILD_LOCAITON_TABLE =
    	    "CREATE TABLE " + ChildLocationEntry.TABLE_NAME + " (" +
    	    		ChildLocationEntry._ID + " INTEGER PRIMARY KEY" + COMMA_SEP +
    	    ChildLocationEntry.COLUMN_NAME_UUID + TEXT_TYPE + COMMA_SEP +
    	    ChildLocationEntry.COLUMN_NAME_LATITUDE + TEXT_TYPE + COMMA_SEP +
    	    ChildLocationEntry.COLUMN_NAME_LONGITUDE + TEXT_TYPE +
    	    " )";
    private static final String SQL_DELETE_CHILD_LOCATION_TABLE =
    	    "DROP TABLE IF EXISTS " + EventListEntry.TABLE_NAME;
    
    
    private DBHelper(Context context) {
    	super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(SQL_CREATE_ACCOUNT_TABLE);
		db.execSQL(SQL_CREATE_CHILDREN_TABLE);
		db.execSQL(SQL_CREATE_EVENT_TABLE);
		db.execSQL(SQL_CREATE_CHILD_LOCAITON_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL(SQL_DELETE_ACCOUNT_TABLE);
		db.execSQL(SQL_DELETE_CHILD_TABLE);
		db.execSQL(SQL_DELETE_EVENT_TABLE);
		db.execSQL(SQL_DELETE_CHILD_LOCATION_TABLE);
		onCreate(db);
	}
	
	public long insertAccount(SQLiteDatabase db, ContentValues value) {
		return db.insert(AccountEntry.TABLE_NAME, "", value);
	}
	
	public void insertChildList(SQLiteDatabase db, List<Student> childList) {
		for (Student item : childList) {
			ContentValues cv = new ContentValues();
			cv.put(ChildEntry.COLUMN_NAME_UUID, item.getUuid());
			cv.put(ChildEntry.COLUMN_NAME_GIVEN_NAME, item.getName());
			cv.put(ChildEntry.COLUMN_NAME_NICK_NAME, item.getNickname());
			cv.put(ChildEntry.COLUMN_NAME_GENDER, item.getGender());
			cv.put(ChildEntry.COLUMN_NAME_DOB, item.getDob());
			cv.put(ChildEntry.COLUMN_NAME_HEIGHT, item.getHeight());
			cv.put(ChildEntry.COLUMN_NAME_WEIGHT, item.getWeight());
			cv.put(ChildEntry.COLUMN_NAME_ROLL_NO, item.getRoll_no());
			cv.put(ChildEntry.COLUMN_NAME_CLASS, item.get_class());
			cv.put(ChildEntry.COLUMN_NAME_STUDENT_ID, item.getStudent_id());
			db.insert(ChildEntry.TABLE_NAME, "", cv);
		}
	}
	
	public void queryChildList(SQLiteDatabase db) {
		Cursor cursor = db.rawQuery(SQL_QUERY_ALL_CHILDREN_DATA, null);
		if (cursor.getCount() > 0 ) {
			cursor.moveToFirst();
			do {
				Student item = new Student();
				item.setUuid(cursor.getString(cursor.getColumnIndex(ChildEntry.COLUMN_NAME_UUID)));
				item.setName(cursor.getString(cursor.getColumnIndex(ChildEntry.COLUMN_NAME_GIVEN_NAME)));
				item.setNickname(cursor.getString(cursor.getColumnIndex(ChildEntry.COLUMN_NAME_NICK_NAME)));
				item.setGender(cursor.getString(cursor.getColumnIndex(ChildEntry.COLUMN_NAME_GENDER)));
				item.setDob(cursor.getString(cursor.getColumnIndex(ChildEntry.COLUMN_NAME_DOB)));
				item.setHeight(cursor.getInt(cursor.getColumnIndex(ChildEntry.COLUMN_NAME_HEIGHT)));
				item.setWeight(cursor.getInt(cursor.getColumnIndex(ChildEntry.COLUMN_NAME_WEIGHT)));
				item.setRoll_no(cursor.getString(cursor.getColumnIndex(ChildEntry.COLUMN_NAME_ROLL_NO)));
				item.set_class(cursor.getString(cursor.getColumnIndex(ChildEntry.COLUMN_NAME_CLASS)));   
				item.setStudent_id(cursor.getString(cursor.getColumnIndex(ChildEntry.COLUMN_NAME_STUDENT_ID)));  
			} while(cursor.moveToNext());
		}
	}
}
