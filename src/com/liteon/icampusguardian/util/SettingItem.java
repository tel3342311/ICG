package com.liteon.icampusguardian.util;

public class SettingItem {

	
	public static enum TYPE {
        BASIC_INFO("基本資料"),
        GOAL_SETTING("每日目標設定"),
        WATCH_THEME("個性化錶面"),
        PRIVACY_INFO("智慧手錶資訊與使用隱私"),
        PAIRING("配對智慧手錶");
		
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
