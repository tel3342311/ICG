package com.liteon.icampusguardian.util;

import com.liteon.icampusguardian.R;
import com.liteon.icampusguardian.App;
public class ProfileItem {

	
	public static enum TYPE {
        BIRTHDAY(App.getContext().getString(R.string.setup_kid_birthday)),
        GENDER(App.getContext().getString(R.string.setup_kid_gender)),
        HEIGHT(App.getContext().getString(R.string.setup_kid_height)),
        WEIGHT(App.getContext().getString(R.string.setup_kid_weight));
		
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
