package com.liteon.icampusguardian.util;

import com.liteon.icampusguardian.App;
import com.liteon.icampusguardian.R;

public class GeoEventItem {

	private String Date;
	private String EnterSchool;
	private String LeaveSchool;
	private String Emergency;
	private String EmergencyRelease;

	public GeoEventItem() {
//		EnterSchool = App.getContext().getString(R.string.safty_no_watch_detected);
//		LeaveSchool = App.getContext().getString(R.string.safty_no_watch_detected);
	}
	/**
	 * @return the date of geo event
	 */
	public String getDate() {
		return Date;
	}
	/**
	 * @param date the date to set geo event
	 */
	public void setDate(String date) {
		Date = date;
	}
	/**
	 * @return the enterSchool time
	 */
	public String getEnterSchool() {
		return EnterSchool;
	}
	/**
	 * @param enterSchool the enterSchool time to set
	 */
	public void setEnterSchool(String enterSchool) {
		EnterSchool = enterSchool;
	}
	/**
	 * @return the leaveSchool time
	 */
	public String getLeavelSchool() {
		return LeaveSchool;
	}
	/**
	 * @param leavelSchool the time of leave school to set
	 */
	public void setLeaveSchool(String leaveSchool) {
		LeaveSchool = leaveSchool;
	}
	/**
	 * @return the emergency event 
	 */
	public String getEmergency() {
		return Emergency;
	}
	/**
	 * @param emergency the time of emergency to set
	 */
	public void setEmergency(String emergency) {
		Emergency = emergency;
	}
	/**
	 * @return the time of emergency release
	 */
	public String getEmergencyRelease() {
		return EmergencyRelease;
	}
	/**
	 * @param emergencyRelease the time of emergency Release to set
	 */
	public void setEmergencyRelease(String emergencyRelease) {
		EmergencyRelease = emergencyRelease;
	}
	
	
}
