package com.liteon.icampusguardian.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.liteon.icampusguardian.R;
import com.liteon.icampusguardian.R;
import com.liteon.icampusguardian.App;

public class HealthyItem {

	public static enum TYPE {
        ACTIVITY(App.getContext().getString(R.string.activity), R.drawable.health_img_activity, R.color.color_activity),
        CALORIES_BURNED(App.getContext().getString(R.string.healthy_carlories), R.drawable.health_img_calories, R.color.color_calories ),
        TOTAL_STEPS(App.getContext().getString(R.string.healthy_steps), R.drawable.health_img_step, R.color.color_execrise),
        WALKING_TIME(App.getContext().getString(R.string.healthy_walk), R.drawable.health_img_walk, R.color.color_execrise),
        RUNNING_TIME(App.getContext().getString(R.string.healthy_running), R.drawable.health_img_run, R.color.color_execrise),
        CYCLING_TIME(App.getContext().getString(R.string.healthy_cycling), R.drawable.health_img_bicycle, R.color.color_execrise),
        HEART_RATE(App.getContext().getString(R.string.healthy_avg_bpm), R.drawable.health_img_heartbeat, R.color.color_heart_rate),
        SLEEP_TIME(App.getContext().getString(R.string.healthy_sleep), R.drawable.health_img_sleep, R.color.color_sleep);
		
		private String name;
		private int icon_id;
		private int color_id;
		private TYPE(String name, int icon_id, int color_id) {
			this.name = name;
			this.icon_id = icon_id;
			this.color_id = color_id;
		}
		
		public String getName() {
			return name;
		}
		
		public int getIconId(){
			return icon_id;
		}
		public int getColorId() {
			return color_id;
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
		String output = "";
		switch(itemType) {
			case ACTIVITY:
				output = Integer.toString(value);
				break;
			case CALORIES_BURNED:
				output = Integer.toString(value) + App.getContext().getString(R.string.healthy_cal);
				break;
			case WALKING_TIME:
			case RUNNING_TIME:
			case CYCLING_TIME:	
				output = Integer.toString(value) + App.getContext().getString(R.string.healthy_minutes);
				break;
			case SLEEP_TIME:
				SimpleDateFormat sdf = new SimpleDateFormat("mm");
				try {
					Date date = sdf.parse(Integer.toString(value));
					sdf.applyPattern(App.getContext().getString(R.string.healthy_hour_mins));
					output = sdf.format(date);
				} catch (ParseException e) {
					e.printStackTrace();
				}
				break;
			case HEART_RATE:
				output = Integer.toString(value) + App.getContext().getString(R.string.healthy_bpm);
				break;
			case TOTAL_STEPS:
				output = Integer.toString(value) + App.getContext().getString(R.string.healthy_step);
				break;
			default:
				output = "";
				break;
		}
		return output;
	}
}
