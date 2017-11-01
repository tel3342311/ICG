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

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import static com.liteon.icampusguardian.util.AlarmPeriodItem.TYPE.CUSTOMIZE;
import static com.liteon.icampusguardian.util.AlarmPeriodItem.TYPE.EVERYDAY;
import static com.liteon.icampusguardian.util.AlarmPeriodItem.TYPE.ONCE;
import static com.liteon.icampusguardian.util.AlarmPeriodItem.TYPE.WEEKEND;
import static com.liteon.icampusguardian.util.AlarmPeriodItem.TYPE.WEEK_DAY;

public class AlarmManager {

	private static ArrayList<AlarmItem> myDataset = new ArrayList<>();
	private static Map<String, List<AlarmItem>> mAlarmMap;
	private static DBHelper mDbHelper;
	private static List<Student> mStudents;
	private static int mCurrentStudentIdx;
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
		if (mAlarmMap.get(mStudents.get(mCurrentStudentIdx).getStudent_id()) == null) {
			mAlarmMap.put(mStudents.get(mCurrentStudentIdx).getStudent_id(), new ArrayList<AlarmItem>());
		}
		myDataset.clear();
		myDataset.addAll((ArrayList) mAlarmMap.get(mStudents.get(mCurrentStudentIdx).getStudent_id()));
	}
	
	public static void saveAlarm() {
		mAlarmMap.put(mStudents.get(mCurrentStudentIdx).getStudent_id(), myDataset);
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

	public static String getAlarmEditContent() {
		AlarmDataJSON alarmDataJSON = new AlarmDataJSON();
		alarmDataJSON.setType("alarm");
		List<AlarmDataJSON.AlarmData> data = new ArrayList<>();
		for (AlarmItem item : myDataset) {
			AlarmDataJSON.AlarmData alarmInfo = new AlarmDataJSON.AlarmData();
			if (!item.isAdded()) {
			    alarmInfo.setAction("add");
			    item.setAdded(true);
            } else {
                alarmInfo.setAction("edit");
            }
			alarmInfo.setAlarmId(Integer.toString(myDataset.indexOf(item) + 1));
			alarmInfo.setAlarmtitle(item.getTitle());
			alarmInfo.setHour(item.getDate().substring(0,2));
            alarmInfo.setMinutes(item.getDate().substring(3));
            if (item.getPeriodItem().getItemType() == AlarmPeriodItem.TYPE.CUSTOMIZE) {
                alarmInfo.setRepeat(Long.toString(item.getPeriodItem().getCustomValue()));
            } else {
                alarmInfo.setRepeat(Long.toString(item.getPeriodItem().getValue()));
            }
            data.add(alarmInfo);
		}

		if (data.size() > 0) {
			AlarmDataJSON.AlarmData [] alarmData = new AlarmDataJSON.AlarmData[data.size()];
			data.toArray(alarmData);
			//Insert array to JSON
			alarmDataJSON.setAlarmList(alarmData);
			Gson gson = new Gson();
			String alarmStr = gson.toJson(alarmDataJSON);
			return alarmStr;
		}
		return "";
	}

    public static String getAlarmStateContent() {
        AlarmDataJSON alarmDataJSON = new AlarmDataJSON();
        alarmDataJSON.setType("alarm");
        List<AlarmDataJSON.AlarmData> data = new ArrayList<>();
        for (AlarmItem item : myDataset) {
            if (item.isStateChange()) {
                item.setStateChange(false);
                AlarmDataJSON.AlarmData alarmInfo = new AlarmDataJSON.AlarmData();
                if (item.getEnabled()) {
                    alarmInfo.setAction("enable");
                } else {
                    alarmInfo.setAction("disable");
                }
                alarmInfo.setAlarmId(Integer.toString(myDataset.indexOf(item) + 1));
                alarmInfo.setAlarmtitle(item.getTitle());
                alarmInfo.setHour(item.getDate().substring(0, 2));
                alarmInfo.setMinutes(item.getDate().substring(3));
                if (item.getPeriodItem().getItemType() == AlarmPeriodItem.TYPE.CUSTOMIZE) {
                    alarmInfo.setRepeat(Long.toString(item.getPeriodItem().getCustomValue()));
                } else {
                    alarmInfo.setRepeat(Long.toString(item.getPeriodItem().getValue()));
                }
                data.add(alarmInfo);
            }
        }

        if (data.size() > 0) {
            AlarmDataJSON.AlarmData [] alarmData = new AlarmDataJSON.AlarmData[data.size()];
            data.toArray(alarmData);
            //Insert array to JSON
            alarmDataJSON.setAlarmList(alarmData);
            Gson gson = new Gson();
            String alarmStr = gson.toJson(alarmDataJSON);
            return alarmStr;
        }
        return "";
    }

    public static String requestAlarm() {
		RequestAlarmJSON requestAlarmJSON = new RequestAlarmJSON();
		requestAlarmJSON.setType("getalarmdata");

		Gson gson = new Gson();
		return gson.toJson(requestAlarmJSON);
	}

    public static void syncAlarmFromJSON(String readMessage) {
        myDataset.clear();
	    Type typeOfRequestAlarmResponse = new TypeToken<RequestAlarmResponseJSON>(){}.getType();
	    Gson gson = new Gson();
	    RequestAlarmResponseJSON responseJSON = gson.fromJson(readMessage, typeOfRequestAlarmResponse);
	    if (TextUtils.equals(responseJSON.getAck(), "SUCCESS")) {
            AlarmDataJSON.AlarmData[] data = responseJSON.getAlarmList();
            for (AlarmDataJSON.AlarmData item : data) {
                AlarmItem alarmItem = new AlarmItem();
                alarmItem.setAdded(true);
                alarmItem.setTitle(item.getAlarmtitle());
                alarmItem.setDate(item.getHour() + ":" +item.getMinutes());
                setPeriodItem(alarmItem, Integer.parseInt(item.getRepeat()));
                myDataset.add(alarmItem);
            }
        }
    }

    public static void setPeriodItem(AlarmItem item, int repeat) {
        AlarmPeriodItem periodItem = new AlarmPeriodItem();

        switch (AlarmPeriodItem.TYPE.values()[repeat]) {

            case WEEK_DAY:
                periodItem.setItemType(WEEK_DAY);
                break;
            case WEEKEND:
                periodItem.setItemType(WEEKEND);
                break;
            case EVERYDAY:
                periodItem.setItemType(EVERYDAY);
                break;
            case ONCE:
                periodItem.setItemType(ONCE);
                break;
            default:
                periodItem.setItemType(CUSTOMIZE);
                periodItem.setCustomValue(repeat);
                break;
        }
        item.setPeriodItem(periodItem);
        if (periodItem.getItemType() != CUSTOMIZE) {
            item.setPeriod(periodItem.getTitle());
        } else {
            StringBuilder sb = new StringBuilder();
            for (WeekPeriodItem.TYPE type : WeekPeriodItem.TYPE.values()) {
                if ((repeat & type.getValue()) == type.getValue()) {
                    sb.append(type.getName() + " ");
                }
            }
            item.setPeriod(sb.toString());
        }
    }

    public static void setCurrentStudentIdx(int currentStudentIdx) {
        mCurrentStudentIdx = currentStudentIdx;
    }
}
