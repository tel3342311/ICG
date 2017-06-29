package com.liteon.icampusguardian.util;

public class ProfileItem {

	
	public static enum TYPE {
        BIRTHDAY("生日"),
        GENDER("性別"),
        HEIGHT("身高"),
        WEIGHT("體重");
		
		private String name;
		private TYPE(String name) {
			this.name = name;
		}
		
		public String getName() {
			return name;
		}
    }
	
	TYPE itemType;
	String value;
	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}
	/**
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}
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
	
	public String getTitle() {
		return itemType.getName();
	}
}
