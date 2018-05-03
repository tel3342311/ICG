package com.liteon.icampusguardian.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.liteon.icampusguardian.db.AccountTable.AccountEntry;
import com.liteon.icampusguardian.db.ChildLocationTable.ChildLocationEntry;
import com.liteon.icampusguardian.db.ChildTable.ChildEntry;
import com.liteon.icampusguardian.db.EventListTable.EventListEntry;
import com.liteon.icampusguardian.db.HealthDataTable.HealthDataEntry;
import com.liteon.icampusguardian.db.WearableTable.WearableEntry;
import com.liteon.icampusguardian.util.JSONResponse;
import com.liteon.icampusguardian.util.JSONResponse.Parent;
import com.liteon.icampusguardian.util.JSONResponse.Student;
import com.liteon.icampusguardian.util.WearableInfo;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

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
            + ChildEntry.COLUMN_NAME_STUDENT_ID + TEXT_TYPE + " PRIMARY KEY" + COMMA_SEP
            + ChildEntry.COLUMN_NAME_UUID + TEXT_TYPE + COMMA_SEP
            + ChildEntry.COLUMN_NAME_GIVEN_NAME + TEXT_TYPE + COMMA_SEP
            + ChildEntry.COLUMN_NAME_NICK_NAME + TEXT_TYPE + COMMA_SEP
			+ ChildEntry.COLUMN_NAME_GENDER + TEXT_TYPE + COMMA_SEP
            + ChildEntry.COLUMN_NAME_HEIGHT + INTEGER_TYPE + COMMA_SEP
            + ChildEntry.COLUMN_NAME_WEIGHT + INTEGER_TYPE + COMMA_SEP
            + ChildEntry.COLUMN_NAME_CLASS + TEXT_TYPE + COMMA_SEP
            + ChildEntry.COLUMN_NAME_ROLL_NO + TEXT_TYPE + COMMA_SEP
			+ ChildEntry.COLUMN_NAME_IS_DELETED +  INTEGER_TYPE + COMMA_SEP
            + ChildEntry.COLUMN_NAME_DOB + TEXT_TYPE + " )";
	private static final String SQL_DELETE_CHILD_TABLE = "DROP TABLE IF EXISTS " + ChildEntry.TABLE_NAME;
	
	// Event list data
	public static final String SQL_QUERY_ALL_EVENT_DATA = "SELECT * FROM " + EventListEntry.TABLE_NAME;
	private static final String SQL_CREATE_EVENT_TABLE = "CREATE TABLE " + EventListEntry.TABLE_NAME + " ("
			+ EventListEntry.COLUMN_NAME_UUID + TEXT_TYPE + " PRIMARY KEY" + COMMA_SEP
			+ EventListEntry.COLUMN_NAME_EVENT_ID + TEXT_TYPE + COMMA_SEP + EventListEntry.COLUMN_NAME_EVENT_SUBSCRIBE
			+ TEXT_TYPE + " )";
	private static final String SQL_DELETE_EVENT_TABLE = "DROP TABLE IF EXISTS " + EventListEntry.TABLE_NAME;

	//Wearable data
    public static final String SQL_QUERY_ALL_WEARABLE_DATA = "SELECT * FROM " + WearableTable.WearableEntry.TABLE_NAME;
    private static final String SQL_CREATE_WEARABLE_TABLE = "CREATE TABLE " + WearableTable.WearableEntry.TABLE_NAME + " ("
            + WearableEntry.COLUMN_NAME_UUID + TEXT_TYPE + " PRIMARY KEY" + COMMA_SEP
            + WearableEntry.COLUMN_NAME_ADDR + TEXT_TYPE  + COMMA_SEP
            + WearableEntry.COLUMN_NAME_STUDENT_ID + TEXT_TYPE   + " )";
    private static final String SQL_DELETE_WEARABLE_TABLE = "DROP TABLE IF EXISTS " + WearableEntry.TABLE_NAME;

    //Healthy data
    public static final String SQL_QUERY_ALL_HEALTHY_DATA = "SELET * FROM " + HealthDataEntry.TABLE_NAME;
    private static final String SQL_CREATE_HEALTHY_DATA_TABLE = "CREATE TABLE " + HealthDataEntry.TABLE_NAME + " ("
            + HealthDataEntry.COLUMN_NAME_STUDENTID + TEXT_TYPE + COMMA_SEP
            + HealthDataEntry.COLUMN_NAME_DATE + INTEGER_TYPE + COMMA_SEP
            + HealthDataEntry.COLUMN_NAME_DURATION + INTEGER_TYPE + COMMA_SEP
            + HealthDataEntry.COLUMN_NAME_VALUE + INTEGER_TYPE + COMMA_SEP
            + HealthDataEntry.COLUMN_NAME_SITUATION + INTEGER_TYPE + COMMA_SEP
            + "PRIMARY KEY (" + HealthDataEntry.COLUMN_NAME_STUDENTID + COMMA_SEP
            + HealthDataEntry.COLUMN_NAME_DATE + COMMA_SEP
            + HealthDataEntry.COLUMN_NAME_SITUATION + " ))";
    private static final String SQL_DELETE_HEALTHY_TABLE = "DROP TABLE IF EXISTS " + HealthDataEntry.TABLE_NAME;

	public static DBHelper getInstance(Context ctx) {
		if (mInstance == null) {
			mInstance = new DBHelper(ctx.getApplicationContext());
		}
		return mInstance;
	}

	// child location data
	public static final String SQL_QUERY_CHILD_LOCATION_DATA = "SELECT * FROM " + ChildLocationEntry.TABLE_NAME;
	private static final String SQL_CREATE_CHILD_LOCAITON_TABLE = "CREATE TABLE " + ChildLocationEntry.TABLE_NAME + " ("
			+ ChildLocationEntry.COLUMN_NAME_STUDENTID + TEXT_TYPE + " PRIMARY KEY" + COMMA_SEP
			+ ChildLocationEntry.COLUMN_NAME_LATITUDE + TEXT_TYPE + COMMA_SEP
            + ChildLocationEntry.COLUMN_NAME_LONGITUDE + TEXT_TYPE + COMMA_SEP
            + ChildLocationEntry.COLUMN_NAME_UPDATE_TIME + INTEGER_TYPE + " )";
	private static final String SQL_DELETE_CHILD_LOCATION_TABLE = "DROP TABLE IF EXISTS " + ChildLocationEntry.TABLE_NAME;

	private DBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(SQL_CREATE_ACCOUNT_TABLE);
		db.execSQL(SQL_CREATE_CHILDREN_TABLE);
		db.execSQL(SQL_CREATE_EVENT_TABLE);
		db.execSQL(SQL_CREATE_CHILD_LOCAITON_TABLE);
		db.execSQL(SQL_CREATE_WEARABLE_TABLE);
		db.execSQL(SQL_CREATE_HEALTHY_DATA_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL(SQL_DELETE_ACCOUNT_TABLE);
		db.execSQL(SQL_DELETE_CHILD_TABLE);
		db.execSQL(SQL_DELETE_EVENT_TABLE);
		db.execSQL(SQL_DELETE_CHILD_LOCATION_TABLE);
        db.execSQL(SQL_DELETE_WEARABLE_TABLE);
        db.execSQL(SQL_DELETE_HEALTHY_TABLE);
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
        String[] args = new String[]{parent.getUsername()};
        cv.put(AccountEntry.COLUMN_NAME_ACCOUNT_NAME, parent.getAccount_name());
        cv.put(AccountEntry.COLUMN_NAME_MOBILE_NUMBER, parent.getMobile_number());
        cv.put(AccountEntry.COLUMN_NAME_PASSWORD, parent.getPassword());
        db.update(AccountEntry.TABLE_NAME, cv, "username=?", args);
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

            long ret = db.insert(ChildEntry.TABLE_NAME, null, cv);
            Log.d(DATABASE_NAME, "insert " + item.getNickname() + "RET is " + ret);
		}
		db.close();
	}

	public void updateChildByStudentId(SQLiteDatabase db, Student item) {
        String studentID = item.getStudent_id();
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

        int ret = db.update(ChildEntry.TABLE_NAME, cv, "student_id=?", new String[] { studentID });
    }

    public void deleteChildByStudentID(SQLiteDatabase db, String studentId) {
		long ret = db.delete(ChildEntry.TABLE_NAME, ChildEntry.COLUMN_NAME_STUDENT_ID + "=" + studentId, null);
		db.close();
		return ;
	}

	public void clearChildList(SQLiteDatabase db) {
		db.execSQL("delete from "+ ChildEntry.TABLE_NAME);
		db.close();
	}

	public boolean isChildExist(SQLiteDatabase db, String studentId) {
		if (TextUtils.isEmpty(studentId)) {
			return false;
		}
		Cursor c = db.query(ChildEntry.TABLE_NAME, null, "student_id =?", new String[] { studentId }, null, null, null, null);
		if (c.moveToFirst()) { // if the row exist then return the id
			c.close();
			return true;
		}
		c.close();
		return false;
	}

	public Student getChildByUUID(SQLiteDatabase db, String uuid) {
	    Cursor cursor = db.query(ChildEntry.TABLE_NAME, null,"uuid=?", new String[]{uuid}, null,null,null,null);
        Student item = null;
	    if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                item = new Student();
                item.setUuid(cursor.getString(cursor.getColumnIndex(ChildEntry.COLUMN_NAME_UUID)));
                item.setName(cursor.getString(cursor.getColumnIndex(ChildEntry.COLUMN_NAME_GIVEN_NAME)));
                item.setNickname(cursor.getString(cursor.getColumnIndex(ChildEntry.COLUMN_NAME_NICK_NAME)));
                item.setGender(cursor.getString(cursor.getColumnIndex(ChildEntry.COLUMN_NAME_GENDER)));
                item.setDob(cursor.getString(cursor.getColumnIndex(ChildEntry.COLUMN_NAME_DOB)));
                item.setHeight(Integer.toString(cursor.getInt(cursor.getColumnIndex(ChildEntry.COLUMN_NAME_HEIGHT))));
                item.setWeight(Integer.toString(cursor.getInt(cursor.getColumnIndex(ChildEntry.COLUMN_NAME_WEIGHT))));
                item.setRoll_no(Integer.parseInt(cursor.getString(cursor.getColumnIndex(ChildEntry.COLUMN_NAME_ROLL_NO))));
                item.set_class(cursor.getString(cursor.getColumnIndex(ChildEntry.COLUMN_NAME_CLASS)));
                item.setStudent_id(Integer.parseInt(cursor.getString(cursor.getColumnIndex(ChildEntry.COLUMN_NAME_STUDENT_ID))));
            } while(cursor.moveToNext());
        }
        cursor.close();
	    db.close();
	    return item;
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
				item.setRoll_no(Integer.parseInt(cursor.getString(cursor.getColumnIndex(ChildEntry.COLUMN_NAME_ROLL_NO))));
				item.set_class(cursor.getString(cursor.getColumnIndex(ChildEntry.COLUMN_NAME_CLASS)));
				item.setStudent_id(Integer.parseInt(cursor.getString(cursor.getColumnIndex(ChildEntry.COLUMN_NAME_STUDENT_ID))));
				list.add(item);
			} while (cursor.moveToNext());
			cursor.close();
			db.close();
		}
		return list;
	}

    public void insertChild(SQLiteDatabase db, Student item) {
        ContentValues cv = new ContentValues();
        cv.put(ChildEntry.COLUMN_NAME_UUID, item.getUuid());
        cv.put(ChildEntry.COLUMN_NAME_NICK_NAME, item.getNickname());
        cv.put(ChildEntry.COLUMN_NAME_ROLL_NO, item.getRoll_no());
        cv.put(ChildEntry.COLUMN_NAME_STUDENT_ID, item.getStudent_id());
		cv.put(ChildEntry.COLUMN_NAME_GENDER, item.getGender());
		cv.put(ChildEntry.COLUMN_NAME_DOB, item.getDob());
		cv.put(ChildEntry.COLUMN_NAME_HEIGHT, item.getHeight());
		cv.put(ChildEntry.COLUMN_NAME_WEIGHT, item.getWeight());

        long ret = db.insert(ChildEntry.TABLE_NAME, null, cv);
        Log.d(DATABASE_NAME, "insert " + item.getNickname() + "RET is " + ret);
    }
	
	public void updateChildData(SQLiteDatabase db, Student student) {
		ContentValues cv = new ContentValues();
		String[] args = new String[]{student.getStudent_id()};
		cv.put(ChildEntry.COLUMN_NAME_UUID, student.getUuid());
		cv.put(ChildEntry.COLUMN_NAME_IS_DELETED, student.getIsDelete());
		db.update(ChildEntry.TABLE_NAME, cv, "student_id=?", args);
	}

	public long insertWearableData(SQLiteDatabase db, WearableInfo info) {
        ContentValues cv = new ContentValues();
        cv.put(WearableEntry.COLUMN_NAME_UUID, info.getUuid());
        cv.put(WearableEntry.COLUMN_NAME_ADDR, info.getBtAddr());
        cv.put(WearableEntry.COLUMN_NAME_STUDENT_ID, info.getStudentID());
        long ret = db.insert(WearableEntry.TABLE_NAME, null, cv);
        db.close();
        return ret;
    }

    public long deleteWearableData(SQLiteDatabase db, String uuid) {
		long ret = db.delete(WearableEntry.TABLE_NAME, "uuid=?", new String[]{uuid});
		db.close();
		return ret;
	}

	public void replaceWearableData(SQLiteDatabase db, WearableInfo info) {
		ContentValues cv = new ContentValues();
		cv.put(WearableEntry.COLUMN_NAME_ADDR, info.getBtAddr());
		cv.put(WearableEntry.COLUMN_NAME_STUDENT_ID, info.getStudentID());
		cv.put(WearableEntry.COLUMN_NAME_UUID, info.getUuid());
		db.replace(WearableEntry.TABLE_NAME, null ,cv);
		db.close();
	}

	public WearableInfo getWearableInfoByUuid(SQLiteDatabase db, String uuid) {
		if (TextUtils.isEmpty(uuid)) {
			return null;
		}
		Cursor c = db.query(WearableEntry.TABLE_NAME, null, "uuid =?", new String[] { uuid }, null, null, null, null);
		if (c.moveToFirst()) { // if the row exist then return the id
			WearableInfo info = new WearableInfo();
			info.setUuid(uuid);
			info.setBtAddr(c.getString(c.getColumnIndex(WearableEntry.COLUMN_NAME_ADDR)));
			info.setStudentID(c.getString(c.getColumnIndex(WearableEntry.COLUMN_NAME_STUDENT_ID)));
			c.close();
			return info;
		}
		c.close();
		return null;
	}

	public String getBlueToothAddrByStudentId(SQLiteDatabase db, String student_id) {
        Cursor c = db.query(WearableEntry.TABLE_NAME, new String[] { WearableEntry.COLUMN_NAME_ADDR}, "student_id =?",
                new String[] { student_id }, null, null, null, null);
        if (c.moveToFirst()) // if the row exist then return the id
            return c.getString(c.getColumnIndex(WearableEntry.COLUMN_NAME_ADDR));
        return "";
	}

    public void updateWearableData(SQLiteDatabase db, WearableInfo info) {
        ContentValues cv = new ContentValues();
        String[] args = new String[]{info.getUuid()};
        cv.put(WearableEntry.COLUMN_NAME_ADDR, info.getBtAddr());
        cv.put(WearableEntry.COLUMN_NAME_STUDENT_ID, info.getStudentID());
        db.update(WearableEntry.TABLE_NAME, cv, "uuid=?", args);
    }

    public boolean isWearableExist(SQLiteDatabase db, String uuid) {
        if (TextUtils.isEmpty(uuid)) {
            return false;
        }
        Cursor c = db.query(WearableEntry.TABLE_NAME, null, "uuid =?", new String[] { uuid }, null, null, null, null);
        if (c.moveToFirst()) { // if the row exist then return the id
            c.close();
            return true;
        }
        c.close();
        return false;
    }

    public JSONResponse.HealthyData getLastHealthyData(SQLiteDatabase db, String studentId, String situation) {
	    Cursor c = db.query(HealthDataEntry.TABLE_NAME, null, "student_id=? and situation=?", new String[] {studentId, situation}, null, null, HealthDataEntry.COLUMN_NAME_DATE+" DESC", "1");
        JSONResponse.HealthyData data = new JSONResponse.HealthyData();
	    if (c.moveToFirst()) {
            data.setDate(c.getInt(c.getColumnIndex(HealthDataEntry.COLUMN_NAME_DATE)));
            data.setDuration(c.getInt(c.getColumnIndex(HealthDataEntry.COLUMN_NAME_DURATION)));
            data.setSituation(c.getInt(c.getColumnIndex(HealthDataEntry.COLUMN_NAME_SITUATION)));
            data.setValue(c.getInt(c.getColumnIndex(HealthDataEntry.COLUMN_NAME_VALUE)));
            c.close();
        }
        db.close();
	    return data;
    }

    public List<JSONResponse.HealthyData> getHealthyDataByDuration(SQLiteDatabase db, String studentId, String situation) {
        Cursor c = db.query(HealthDataEntry.TABLE_NAME, null, "student_id=? and situation=?", new String[] {studentId, situation}, null, null, HealthDataEntry.COLUMN_NAME_DATE+" desc", "7");
        List<JSONResponse.HealthyData> list = new ArrayList<>();
        if (c.moveToFirst()) {
            do {
                JSONResponse.HealthyData data = new JSONResponse.HealthyData();
                data.setDate(c.getInt(c.getColumnIndex(HealthDataEntry.COLUMN_NAME_DATE)));
                data.setDuration(c.getInt(c.getColumnIndex(HealthDataEntry.COLUMN_NAME_DURATION)));
                data.setSituation(c.getInt(c.getColumnIndex(HealthDataEntry.COLUMN_NAME_SITUATION)));
                data.setValue(c.getInt(c.getColumnIndex(HealthDataEntry.COLUMN_NAME_VALUE)));
                list.add(0, data);
            } while(c.moveToNext());
            c.close();
        }
        db.close();
        return list;
    }

    public void updateHealthyData(SQLiteDatabase db, List<JSONResponse.HealthyData> data, int situation, String studentId) {

	    for (JSONResponse.HealthyData item : data) {
            ContentValues cv = new ContentValues();
            cv.put(HealthDataEntry.COLUMN_NAME_DATE, item.getDate());
            cv.put(HealthDataEntry.COLUMN_NAME_SITUATION, situation);
            cv.put(HealthDataEntry.COLUMN_NAME_VALUE, item.getValue());
            cv.put(HealthDataEntry.COLUMN_NAME_DURATION, item.getDuration());
            cv.put(HealthDataEntry.COLUMN_NAME_STUDENTID, studentId);
            db.replace(HealthDataEntry.TABLE_NAME, null, cv);
        }
        db.close();
    }

    public void updateHealthyData(SQLiteDatabase db, List<JSONResponse.HealthyData> data, String studentId) {

        for (JSONResponse.HealthyData item : data) {
            ContentValues cv = new ContentValues();
            cv.put(HealthDataEntry.COLUMN_NAME_DATE, item.getDate());
            cv.put(HealthDataEntry.COLUMN_NAME_SITUATION, item.getSituation());
            cv.put(HealthDataEntry.COLUMN_NAME_VALUE, item.getValue());
            cv.put(HealthDataEntry.COLUMN_NAME_DURATION, item.getDuration());
            cv.put(HealthDataEntry.COLUMN_NAME_STUDENTID, studentId);
            db.replace(HealthDataEntry.TABLE_NAME, null, cv);
        }
        db.close();
    }


	private void createDummyData(SQLiteDatabase db) {

	    //Child Data
        List<Student> studentList = new ArrayList<>();
		Student item = new Student();
		//student 1
		item.setUuid("");
		item.setName("Name");
		item.setNickname("Pink");
		item.setGender("FeMale");
		item.setDob("1995-01-01");
		item.setHeight("150");
		item.setWeight("40");
		item.setRoll_no(11);
		item.set_class("1");
		item.setStudent_id(1);
		studentList.add(item);

		//student 2
        item = new Student();
		item.setUuid("");
		item.setName("Name");
		item.setNickname("Gibert");
		item.setGender("Male");
		item.setDob("1996-03-01");
		item.setHeight("140");
		item.setWeight("40");
		item.setRoll_no(12);
		item.set_class("2");
		item.setStudent_id(2);
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

        //location Data child 1
        cv.clear();
        cv.put(ChildLocationEntry.COLUMN_NAME_STUDENTID, "1");
        cv.put(ChildLocationEntry.COLUMN_NAME_LATITUDE, "25.029600");
        cv.put(ChildLocationEntry.COLUMN_NAME_LONGITUDE, "121.533260");
        cv.put(ChildLocationEntry.COLUMN_NAME_UPDATE_TIME, Calendar.getInstance().getTimeInMillis());
        insertChildLocation(getWritableDatabase(), cv);
        //location Data child 2
        cv.clear();
        cv.put(ChildLocationEntry.COLUMN_NAME_STUDENTID, "2");
        cv.put(ChildLocationEntry.COLUMN_NAME_LATITUDE, "25.039594");
        cv.put(ChildLocationEntry.COLUMN_NAME_LONGITUDE, "121.559538");
        cv.put(ChildLocationEntry.COLUMN_NAME_UPDATE_TIME, Calendar.getInstance().getTimeInMillis());
        insertChildLocation(getWritableDatabase(), cv);
	}

    public void insertChildLocation(SQLiteDatabase db, ContentValues cv) {
        db.insert(ChildLocationEntry.TABLE_NAME, null, cv);
        db.close();
	}

	public LatLng getChildLocationByID(SQLiteDatabase db, String studentID) {
        Cursor c = db.query(ChildLocationEntry.TABLE_NAME, new String[] { ChildLocationEntry.COLUMN_NAME_LATITUDE, ChildLocationEntry.COLUMN_NAME_LONGITUDE}, "student_id =?",
                new String[] { studentID }, null, null, null, null);
        if (c.moveToFirst()) {// if the row exist then return the id
            String lat = c.getString(c.getColumnIndex(ChildLocationEntry.COLUMN_NAME_LATITUDE));
            String lng = c.getString(c.getColumnIndex(ChildLocationEntry.COLUMN_NAME_LONGITUDE));
            LatLng location = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));
            return location;
        }
        return null;
    }

    public void deletaAll(SQLiteDatabase db) {
        db.delete(AccountEntry.TABLE_NAME, null, null);
        db.delete(ChildEntry.TABLE_NAME, null, null);
        db.delete(WearableEntry.TABLE_NAME, null, null);
        db.delete(ChildLocationEntry.TABLE_NAME, null, null);
        db.delete(WearableEntry.TABLE_NAME, null, null);
        db.close();
    }
}
