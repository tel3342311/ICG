package com.liteon.icampusguardian.util;

import com.liteon.icampusguardian.App;
import com.liteon.icampusguardian.R;

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
				if (value == 0){
					output = "--";
				} else {
					output = Integer.toString(value);
				}
				break;
			case CALORIES_BURNED:
				if (value == 0){
					output = "--" + App.getContext().getString(R.string.healthy_cal);
				} else {
					output = Integer.toString(value) + App.getContext().getString(R.string.healthy_cal);
				}
				break;
			case WALKING_TIME:
			case RUNNING_TIME:
			case CYCLING_TIME:	
				if (value == 0){
					output = "--" + App.getContext().getString(R.string.healthy_minutes);
				} else {
					output = Integer.toString(value) + App.getContext().getString(R.string.healthy_minutes);
				}
				break;
			case SLEEP_TIME:
                int ALLTIME_IN_MINUTES = value;
                int hour = ALLTIME_IN_MINUTES / 60;
                int min = ALLTIME_IN_MINUTES % 60;
                if (value == 0 ){
                    output = App.getContext().getString(R.string.healthy_hour_mins).replace("hh", "--").replace("mm", "--");
                } else {
                    output = App.getContext().getString(R.string.healthy_hour_mins).replace("hh", Integer.toString(hour)).replace("mm", Integer.toString(min));
                }
                break;
			case HEART_RATE:
				if (value == 0){
					output = "--" + App.getContext().getString(R.string.healthy_bpm);
				} else {
					output = Integer.toString(value) + App.getContext().getString(R.string.healthy_bpm);
				}
				break;
			case TOTAL_STEPS:
				if (value == 0){
					output = "--" + App.getContext().getString(R.string.healthy_step);
				} else {
					output = Integer.toString(value) + App.getContext().getString(R.string.healthy_step);
				}
				break;
			default:
				output = "";
				break;
		}
		return output;
	}
}
