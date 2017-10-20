package com.liteon.icampusguardian.util;

import com.liteon.icampusguardian.App;
import com.liteon.icampusguardian.R;

public class AppInfoPrivacyItem {

	
	public static enum TYPE {
		APP_INFO(App.getContext().getString(R.string.app_version_title)),
        PARENT_INFO(App.getContext().getString(R.string.parent_info)),
        USER_TERM(App.getContext().getString(R.string.welcome_user_term));
		
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
