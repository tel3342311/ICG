package com.liteon.icampusguardian.util;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.liteon.icampusguardian.R;

public class AlarmPeriodItem {

	
	public static enum TYPE {
		WEEK_DAY("平日", 0x0111110),
		WEEKEND("假日",  0x1000001),
		EVERYDAY("每日",  0x1111111),
		ONCE("只提醒一次", 0x00000000),
		CUSTOMIZE("自訂", 0x00000000);
		
		String name;
		long value;
		private TYPE(String name, int value) {
			this.name = name;
			this.value = value;
		}
    }
	
	TYPE itemType;
	long value;
	String title;
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
		return title;
	}
	/**
	 * @param itemType the itemType to set
	 */
	public void setItemType(TYPE itemType) {
		this.itemType = itemType;
		this.title = itemType.name();
	}
	/**
	 * @return the value
	 */
	public long getValue() {
		return value;
	}
	/**
	 * @param value the value to set
	 */
	public void setValue(long value) {
		this.value = value;
	}
}
