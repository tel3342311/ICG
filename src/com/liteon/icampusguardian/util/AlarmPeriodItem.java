package com.liteon.icampusguardian.util;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.liteon.icampusguardian.R;

public class AlarmPeriodItem {

	
	public static enum TYPE {
		WEEK_DAY("週一至週五", 0x1),
		WEEKEND("假日",       0x10),
		EVERYDAY("每日",      0x100),
		ONCE("只提醒一次",     0x1000),
		CUSTOMIZE("自訂",     0x10000);
		
		String name;
		long value;
		private TYPE(String name, int value) {
			this.name = name;
			this.value = value;
		}
    }
	
	TYPE itemType;
	long value;
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
		return value;
	}
	/**
	 * @param value the value to set
	 */
	public void setValue(long value) {
		this.value = value;
	}
}
