package com.liteon.icampusguardian.util;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.liteon.icampusguardian.App;
import com.liteon.icampusguardian.db.DBHelper;
import com.liteon.icampusguardian.util.JSONResponse.Student;

import android.R.integer;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.MatrixCursor;
import android.text.TextUtils;

public class AlarmManager {

	private static ArrayList<AlarmItem> myDataset = new ArrayList<>();
	private static Map<String, List<AlarmItem>> mAlarmMap;
	private static DBHelper mDbHelper;
	private static List<Student> mStudents;
	private static int mCurrnetStudentIdx;
	public static final int ACTION_EDITING = 1;
	public static final int ACTION_ADDING = 2;
	public static int mCurrentAction = -1;
	
	public static int mIdx;
	public static AlarmItem mCurrentItem;
	public static AlarmItem mNewItem;
	public static String mOriginItem;
	public static void setCurrentAction(int action) {
		mCurrentAction = action;
	}
	
	public static int getCurrentAction() {
		return mCurrentAction;
	}
	
	public static void setCurrentItem(AlarmItem item, int idx) {
		mCurrentItem = item;
		Gson gson = new Gson();
		mOriginItem = gson.toJson(mCurrentItem);
		mIdx = idx;
	}
	
	public static AlarmItem restoreCurrentItem() {
		Gson gson = new GsonBuilder().create();
		Type typeOfItem= new TypeToken<AlarmItem>() { }.getType();
		AlarmItem item = gson.fromJson(mOriginItem, typeOfItem);
		mCurrentItem.setDate(item.getDate());
		mCurrentItem.setEnabled(item.Enabled);
		mCurrentItem.setPeriod(item.Period);
		mCurrentItem.setPeriodItem(item.getPeriodItem());
		mCurrentItem.setTitle(item.getTitle());
		return mCurrentItem;
	}
	
	public static void restoreAlarm() {
		SharedPreferences sp = App.getContext().getSharedPreferences(Def.SHARE_PREFERENCE, Context.MODE_PRIVATE);
		String alarmMap = sp.getString(Def.SP_ALARM_MAP, "");
		mDbHelper = DBHelper.getInstance(App.getContext());
		//get child list
		mStudents = mDbHelper.queryChildList(mDbHelper.getReadableDatabase());
		Type typeOfHashMap = new TypeToken<Map<String, List<AlarmItem>>>() { }.getType();
        Gson gson = new GsonBuilder().create();
        mAlarmMap = gson.fromJson(alarmMap, typeOfHashMap);
		if (TextUtils.isEmpty(alarmMap)) {
			mAlarmMap = new HashMap<String, List<AlarmItem>>();
			for (Student student : mStudents) {
				String studentId = student.getStudent_id();
				mAlarmMap.put(studentId, new ArrayList<AlarmItem>());
			}
		}
		if (mAlarmMap.get(mStudents.get(mCurrnetStudentIdx).getStudent_id()) == null) {
			mAlarmMap.put(mStudents.get(mCurrnetStudentIdx).getStudent_id(), new ArrayList<AlarmItem>());
		}
		myDataset.clear();
		myDataset.addAll((ArrayList) mAlarmMap.get(mStudents.get(mCurrnetStudentIdx).getStudent_id()));
	}
	
	public static void saveAlarm() {
		mAlarmMap.put(mStudents.get(mCurrnetStudentIdx).getStudent_id(), myDataset);
		Gson gson = new Gson();
		String input = gson.toJson(mAlarmMap);
		SharedPreferences sp = App.getContext().getSharedPreferences(Def.SHARE_PREFERENCE, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putString(Def.SP_ALARM_MAP, input);
		editor.commit();
	}
	
	public static ArrayList<AlarmItem> getDataSet(){
		return myDataset;
	}
}
