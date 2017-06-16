package com.liteon.icampusguardian.util;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.liteon.icampusguardian.R;

public class HealthyItem {

	
	public static enum TYPE {
        ACTIVITY("活動指數", R.drawable.health_img_activity),
        CALORIES_BURNED("每日燃燒卡路里", R.drawable.health_img_calories),
        TOTAL_STEPS("步數", R.drawable.health_img_step),
        WALKING_TIME("走路", R.drawable.health_img_walk),
        RUNNING_TIME("跑步", R.drawable.health_img_run),
        CYCLING_TIME("騎腳踏車", R.drawable.health_img_bicycle),
        HEART_RATE("心率", R.drawable.health_img_heartbeat),
        SLEEP_TIME("睡眠", R.drawable.health_img_sleep);
		
		private String name;
		private int icon_id;
		private TYPE(String name, int icon_id) {
			this.name = name;
			this.icon_id = icon_id;
		}
		
		public String getName() {
			return name;
		}
		
		public int getIconId(){
			return icon_id;
		}
    }
	
	TYPE itemType;
	int value;
	/**
	 * @return the itemType
	 */
	public TYPE getItemType() {
		return itemType;
	}
	/**
	 * @param itemType the itemType to set
	 */
	public void setItemType(TYPE itemType) {
		this.itemType = itemType;
	}
	/**
	 * @return the value
	 */
	public int getValue() {
		return value;
	}
	/**
	 * @param value the value to set
	 */
	public void setValue(int value) {
		this.value = value;
	}
	
	public String getTitle() {
		return itemType.getName();
	}
	
	public int getIconId() {
		return itemType.getIconId();
	}
	
	public String toString() {
		String output;
		switch(itemType) {
			case ACTIVITY:
				output = Integer.toString(value);
				break;
			case CALORIES_BURNED:
				output = Integer.toString(value) + " 卡";
				break;
			case CYCLING_TIME:	
			case RUNNING_TIME:
			case SLEEP_TIME:
			case WALKING_TIME:
				SimpleDateFormat sdf = new SimpleDateFormat("HH 小時 mm 分");
				Date date = new Date(value);
				output = sdf.format(date);
				break;
			case HEART_RATE:
				output = Integer.toString(value) + " bpm";
				break;
			case TOTAL_STEPS:
				output = Integer.toString(value) + " 步";
				break;
			default:
				output = "";
				break;
		}
		return output;
	}
}
