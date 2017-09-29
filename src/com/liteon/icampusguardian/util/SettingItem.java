package com.liteon.icampusguardian.util;

import com.liteon.icampusguardian.App;

import com.liteon.icampusguardian.R;

public class SettingItem {

	
	public static enum TYPE {
        BASIC_INFO(App.getContext().getString(R.string.child_basic_profile)),
        GOAL_SETTING(App.getContext().getString(R.string.child_goal_setting)),
        WATCH_THEME(App.getContext().getString(R.string.child_personalize_theme)),
        PRIVACY_INFO(App.getContext().getString(R.string.child_watch_info_and_privacy)),
        PAIRING(App.getContext().getString(R.string.child_pairing_watch));
		
		private String name;
		private TYPE(String name) {
			this.name = name;
		}
		
		public String getName() {
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
	 * @param itemType the itemType to set
	 */
	public void setItemType(TYPE itemType) {
		this.itemType = itemType;
	}
	
	public String getTitle() {
		return itemType.getName();
	}
}
