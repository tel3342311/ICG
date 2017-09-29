package com.liteon.icampusguardian.util;

public class AlarmItem {

	public String Title;
	public String Date;
	public AlarmPeriodItem PeriodItem;
	public String Period;
	public boolean Enabled;
	
	/**
	 * @return the title
	 */
	public String getTitle() {
		return Title;
	}
	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		Title = title;
	}
	/**
	 * @return the date
	 */
	public String getDate() {
		return Date;
	}
	/**
	 * @param date the date to set
	 */
	public void setDate(String date) {
		Date = date;
	}
	/**
	 * @return the period
	 */
	public String getPeriod() {
		return Period;
	}
	/**
	 * @param period the period to set
	 */
	public void setPeriod(String period) {
		Period = period;
	}
	/**
	 * @return the enabled
	 */
	public boolean getEnabled() {
		return Enabled;
	}
	/**
	 * @param enabled the enabled to set
	 */
	public void setEnabled(boolean enabled) {
		Enabled = enabled;
	}
	/**
	 * @return the periodItem
	 */
	public AlarmPeriodItem getPeriodItem() {
		return PeriodItem;
	}
	/**
	 * @param periodItem the periodItem to set
	 */
	public void setPeriodItem(AlarmPeriodItem periodItem) {
		PeriodItem = periodItem;
	}
	
	
}
