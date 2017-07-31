package com.liteon.icampusguardian.util;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.liteon.icampusguardian.R;

public class WeekPeriodItem {

	
	public static enum TYPE {
		SUNDAY("週日",    0x1000000),
		MONDAY("週一",    0x0100000),
		TUESDAY("週二",   0x0010000),
		WEDNESDAY("週三", 0x0001000),
		THURSDAY("週四",  0x0000100),
		FRIDAY("週五",    0x0000010),
		SATURDAY("週六",  0x0000001);
		
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
