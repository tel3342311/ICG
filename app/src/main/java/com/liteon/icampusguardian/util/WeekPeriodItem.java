package com.liteon.icampusguardian.util;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.liteon.icampusguardian.App;
import com.liteon.icampusguardian.R;

public class WeekPeriodItem {

	
	public static enum TYPE {
		SUNDAY(App.getContext().getString(R.string.alarm_sun),    0x1000000),
		MONDAY(App.getContext().getString(R.string.alarm_mon),    0x0100000),
		TUESDAY(App.getContext().getString(R.string.alarm_tue),   0x0010000),
		WEDNESDAY(App.getContext().getString(R.string.alarm_wed), 0x0001000),
		THURSDAY(App.getContext().getString(R.string.alarm_thr),  0x0000100),
		FRIDAY(App.getContext().getString(R.string.alarm_fri),    0x0000010),
		SATURDAY(App.getContext().getString(R.string.alarm_sat),  0x0000001);
		
		String name;
		long value;
		private TYPE(String name, int value) {
			this.name = name;
			this.value = value;
		}
		
		public long getValue(){
			return value;
		}
		
		public String getName(){
			return name;
		}
    }
	
	TYPE itemType;
	/**
	 * @return the itemType
	 */
	public TYPE getItemType() {
		return itemType;
	}
	/**
	 * @return the title
	 */
	public String getTitle() {
		return itemType.name;
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
	public long getValue() {
		return itemType.value;
	}

}
