package com.liteon.icampusguardian.db;

import java.util.ArrayList;
import java.util.List;

import com.liteon.icampusguardian.App;
import com.liteon.icampusguardian.db.AccountTable.AccountEntry;
import com.liteon.icampusguardian.db.ChildLocationTable.ChildLocationEntry;
import com.liteon.icampusguardian.db.ChildTable.ChildEntry;
import com.liteon.icampusguardian.db.EventListTable.EventListEntry;
import com.liteon.icampusguardian.util.JSONResponse.Parent;
import com.liteon.icampusguardian.util.JSONResponse.Student;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {
	private static DBHelper mInstance = null;
	public static final int DATABASE_VERSION = 3;
	public static final String DATABASE_NAME = "data.db";
	private static final String TEXT_TYPE = " TEXT";
	private static final String INTEGER_TYPE = " INTEGER";
	private static final String COMMA_SEP = ",";

	// ACCOUNT
	public static final String SQL_QUERY_ALL_ACCOUNT_DATA = "SELECT * FROM " + AccountEntry.TABLE_NAME;
	private static final String SQL_CREATE_ACCOUNT_TABLE = "CREATE TABLE " + AccountEntry.TABLE_NAME + " ("
			+ AccountEntry.COLUMN_NAME_USER_NAME + TEXT_TYPE + " PRIMARY KEY" + COMMA_SEP
			+ AccountEntry.COLUMN_NAME_PASSWORD + TEXT_TYPE + COMMA_SEP
            + AccountEntry.COLUMN_NAME_UUID + TEXT_TYPE + COMMA_SEP
            + AccountEntry.COLUMN_NAME_ROLE_TYPE + TEXT_TYPE + COMMA_SEP
			+ AccountEntry.COLUMN_NAME_ACCOUNT_NAME + TEXT_TYPE + COMMA_SEP
            + AccountEntry.COLUMN_NAME_MOBILE_NUMBER + TEXT_TYPE + COMMA_SEP
            + AccountEntry.COLUMN_NAME_GIVEN_NAME + TEXT_TYPE + COMMA_SEP
            + AccountEntry.COLUMN_NAME_NICK_NAME + TEXT_TYPE + COMMA_SEP
			+ AccountEntry.COLUMN_NAME_GENDER + TEXT_TYPE + COMMA_SEP
            + AccountEntry.COLUMN_NAME_HEIGHT + INTEGER_TYPE + COMMA_SEP
            + AccountEntry.COLUMN_NAME_WEIGHT + INTEGER_TYPE + COMMA_SEP
            + AccountEntry.COLUMN_NAME_DOB + TEXT_TYPE + COMMA_SEP
            + AccountEntry.COLUMN_NAME_TOKEN + TEXT_TYPE + " )";
	private static final String SQL_DELETE_ACCOUNT_TABLE = "DROP TABLE IF EXISTS " + AccountEntry.TABLE_NAME;

	// Child data
	public static final String SQL_QUERY_ALL_CHILDREN_DATA = "SELECT * FROM " + ChildEntry.TABLE_NAME;
	private static final String SQL_CREATE_CHILDREN_TABLE = "CREATE TABLE " + ChildEntry.TABLE_NAME + " ("
			+ ChildEntry.COLUMN_NAME_UUID + TEXT_TYPE + COMMA_SEP + ChildEntry.COLUMN_NAME_GIVEN_NAME
			+ TEXT_TYPE + COMMA_SEP + ChildEntry.COLUMN_NAME_NICK_NAME + TEXT_TYPE + COMMA_SEP
			+ ChildEntry.COLUMN_NAME_GENDER + TEXT_TYPE + COMMA_SEP + ChildEntry.COLUMN_NAME_HEIGHT + INTEGER_TYPE
			+ COMMA_SEP + ChildEntry.COLUMN_NAME_WEIGHT + INTEGER_TYPE + COMMA_SEP + ChildEntry.COLUMN_NAME_CLASS
			+ TEXT_TYPE + COMMA_SEP + ChildEntry.COLUMN_NAME_ROLL_NO + TEXT_TYPE + COMMA_SEP
			+ ChildEntry.COLUMN_NAME_IS_DELETED +  INTEGER_TYPE + COMMA_SEP
			+ ChildEntry.COLUMN_NAME_STUDENT_ID +  TEXT_TYPE + " PRIMARY KEY" + COMMA_SEP + ChildEntry.COLUMN_NAME_DOB + TEXT_TYPE + " )";
	private static final String SQL_DELETE_CHILD_TABLE = "DROP TABLE IF EXISTS " + ChildEntry.TABLE_NAME;
	
	// Event list data
	public static final String SQL_QUERY_ALL_EVENT_DATA = "SELECT * FROM " + EventListEntry.TABLE_NAME;
	private static final String SQL_CREATE_EVENT_TABLE = "CREATE TABLE " + EventListEntry.TABLE_NAME + " ("
			+ EventListEntry.COLUMN_NAME_UUID + TEXT_TYPE + " PRIMARY KEY" + COMMA_SEP
			+ EventListEntry.COLUMN_NAME_EVENT_ID + TEXT_TYPE + COMMA_SEP + EventListEntry.COLUMN_NAME_EVENT_SUBSCRIBE
			+ TEXT_TYPE + " )";
	private static final String SQL_DELETE_EVENT_TABLE = "DROP TABLE IF EXISTS " + EventListEntry.TABLE_NAME;

	public static DBHelper getInstance(Context ctx) {
		if (mInstance == null) {
			mInstance = new DBHelper(ctx.getApplicationContext());
		}
		return mInstance;
	}

	// childe location data
	public static final String SQL_QUERY_CHILD_LOCATION_DATA = "SELECT * FROM " + ChildLocationEntry.TABLE_NAME;
	private static final String SQL_CREATE_CHILD_LOCAITON_TABLE = "CREATE TABLE " + ChildLocationEntry.TABLE_NAME + " ("
			+ ChildLocationEntry.COLUMN_NAME_UUID + TEXT_TYPE + " PRIMARY KEY" + COMMA_SEP
			+ ChildLocationEntry.COLUMN_NAME_LATITUDE + TEXT_TYPE + COMMA_SEP + ChildLocationEntry.COLUMN_NAME_LONGITUDE
			+ TEXT_TYPE + " )";
	private static final String SQL_DELETE_CHILD_LOCATION_TABLE = "DROP TABLE IF EXISTS " + ChildLocationEntry.TABLE_NAME;

	private DBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
        if (App.isOffline) {
            createDummyData(getWritableDatabase());
        }
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

	
	public Parent getParentByToken(SQLiteDatabase db, String token) {
		Parent item = new Parent();
		Cursor cursor = db.rawQuery(SQL_QUERY_ALL_ACCOUNT_DATA, null);
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			do {
				item.setAccount_name(cursor.getString(cursor.getColumnIndex(AccountEntry.COLUMN_NAME_ACCOUNT_NAME)));
				item.setPassword(cursor.getString(cursor.getColumnIndex(AccountEntry.COLUMN_NAME_PASSWORD)));
				item.setGiven_name(cursor.getString(cursor.getColumnIndex(AccountEntry.COLUMN_NAME_GIVEN_NAME)));
				item.setUsername(cursor.getString(cursor.getColumnIndex(AccountEntry.COLUMN_NAME_USER_NAME)));
				item.setMobile_number(cursor.getString(cursor.getColumnIndex(AccountEntry.COLUMN_NAME_MOBILE_NUMBER)));
				item.setToken(token);
			} while (cursor.moveToNext());
			cursor.close();
			db.close();
		}
		return item;
	}
	
	public String getAccountToken(SQLiteDatabase db, String name) {
		Cursor c = db.query(AccountEntry.TABLE_NAME, new String[] { AccountEntry.COLUMN_NAME_TOKEN }, "username =?",
				new String[] { name }, null, null, null, null);
		if (c.moveToFirst()) // if the row exist then return the id
			return c.getString(c.getColumnIndex(AccountEntry.COLUMN_NAME_TOKEN));
		return "";
	}

	public void clearAccountToken(SQLiteDatabase db, String token) {
		ContentValues cv = new ContentValues();
		cv.put(AccountEntry.COLUMN_NAME_TOKEN, "");
		db.update(AccountEntry.TABLE_NAME, cv, "token=?", new String [] { token });
		db.close();
	}
	public long insertAccount(SQLiteDatabase db, ContentValues value) {

		long ret = db.insert(AccountEntry.TABLE_NAME, "", value);
		db.close();
		return ret;

	}

	public long deleteAccount(SQLiteDatabase db) {
		long ret = db.delete(AccountEntry.TABLE_NAME, null, null);
		db.close();
		return ret;
	}

    public void updateAccount(SQLiteDatabase db, Parent parent) {
        ContentValues cv = new ContentValues();
        String[] args = new String[]{parent.getToken()};
        cv.put(AccountEntry.COLUMN_NAME_ACCOUNT_NAME, parent.getAccount_name());
        cv.put(AccountEntry.COLUMN_NAME_MOBILE_NUMBER, parent.getMobile_number());
        cv.put(AccountEntry.COLUMN_NAME_USER_NAME, parent.getUsername());
        cv.put(AccountEntry.COLUMN_NAME_PASSWORD, parent.getPassword());
        db.update(AccountEntry.TABLE_NAME, cv, "token=?", args);
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
			if (isChildExist(db, item.getUuid())) {
				db.update(ChildEntry.TABLE_NAME, cv, "uuid=?", new String[] { item.getUuid() });
			} else {
				long ret = db.insert(ChildEntry.TABLE_NAME, null, cv);
				Log.d(DATABASE_NAME, "insert RET is " + ret);
			}
		}
		db.close();
	}

	public void clearChildList(SQLiteDatabase db) {
		db.execSQL("delete from "+ ChildEntry.TABLE_NAME);
		db.close();
	}
	public boolean isChildExist(SQLiteDatabase db, String uuid) {
		if (TextUtils.isEmpty(uuid)) {
			return false;
		}
		Cursor c = db.query(ChildEntry.TABLE_NAME, null, "uuid =?", new String[] { uuid }, null, null, null, null);
		if (c.moveToFirst()) { // if the row exist then return the id
			c.close();
			return true;
		}
		c.close();
		return false;
	}

	public List<Student> queryChildList(SQLiteDatabase db) {
		List<Student> list = new ArrayList<>();
		Cursor cursor = db.rawQuery(SQL_QUERY_ALL_CHILDREN_DATA, null);
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			do {
				if (cursor.getInt(cursor.getColumnIndex(ChildEntry.COLUMN_NAME_IS_DELETED)) != 0) {
					continue;
				}
				Student item = new Student();
				item.setUuid(cursor.getString(cursor.getColumnIndex(ChildEntry.COLUMN_NAME_UUID)));
				item.setName(cursor.getString(cursor.getColumnIndex(ChildEntry.COLUMN_NAME_GIVEN_NAME)));
				item.setNickname(cursor.getString(cursor.getColumnIndex(ChildEntry.COLUMN_NAME_NICK_NAME)));
				item.setGender(cursor.getString(cursor.getColumnIndex(ChildEntry.COLUMN_NAME_GENDER)));
				item.setDob(cursor.getString(cursor.getColumnIndex(ChildEntry.COLUMN_NAME_DOB)));
				item.setHeight(Integer.toString(cursor.getInt(cursor.getColumnIndex(ChildEntry.COLUMN_NAME_HEIGHT))));
				item.setWeight(Integer.toString(cursor.getInt(cursor.getColumnIndex(ChildEntry.COLUMN_NAME_WEIGHT))));
				item.setRoll_no(cursor.getString(cursor.getColumnIndex(ChildEntry.COLUMN_NAME_ROLL_NO)));
				item.set_class(cursor.getString(cursor.getColumnIndex(ChildEntry.COLUMN_NAME_CLASS)));
				item.setStudent_id(cursor.getString(cursor.getColumnIndex(ChildEntry.COLUMN_NAME_STUDENT_ID)));
				list.add(item);
			} while (cursor.moveToNext());
			cursor.close();
			db.close();
		}
		return list;
	}
	
	public void updateChildData(SQLiteDatabase db, Student student) {
		ContentValues cv = new ContentValues();
		String[] args = new String[]{student.getStudent_id()};
		cv.put(ChildEntry.COLUMN_NAME_UUID, student.getUuid());
		cv.put(ChildEntry.COLUMN_NAME_IS_DELETED, student.getIsDelete());
		db.update(ChildEntry.TABLE_NAME, cv, "student_id=?", args);
	}

	private void createDummyData(SQLiteDatabase db) {

	    //Child Data
        List<Student> studentList = new ArrayList<>();
		Student item = new Student();
		//student 1
		item.setUuid("1111-1111-111111111");
		item.setName("Name");
		item.setNickname("Pink");
		item.setGender("FeMale");
		item.setDob("1995-01-01");
		item.setHeight("150");
		item.setWeight("40");
		item.setRoll_no("1");
		item.set_class("1");
		item.setStudent_id("1");
		studentList.add(item);

		//student 2
        item = new Student();
		item.setUuid("2222-2222-22222222");
		item.setName("Name");
		item.setNickname("Gibert");
		item.setGender("Male");
		item.setDob("1996-03-01");
		item.setHeight("140");
		item.setWeight("40");
		item.setRoll_no("2");
		item.set_class("2");
		item.setStudent_id("2");
        studentList.add(item);

		insertChildList(db, studentList);

		//Parent Data
        ContentValues cv = new ContentValues();
        cv.put(AccountEntry.COLUMN_NAME_USER_NAME, "admin3@parent.com");
        cv.put(AccountEntry.COLUMN_NAME_PASSWORD, "password");
        cv.put(AccountEntry.COLUMN_NAME_TOKEN, "E8C33BCCC8A1E1627B28B65B0B4DE829");
        cv.put(AccountEntry.COLUMN_NAME_ACCOUNT_NAME, "LO Parent User");
        cv.put(AccountEntry.COLUMN_NAME_MOBILE_NUMBER, "9030008893");
        insertAccount(getWritableDatabase(), cv);
	}
}
