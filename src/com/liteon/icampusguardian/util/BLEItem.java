package com.liteon.icampusguardian.util;

import android.bluetooth.BluetoothDevice;

public class BLEItem {
	String name;
	String id;
	String value;
	BluetoothDevice mBluetoothDevice;
	/**
	 * @return the mBluetoothDevice
	 */
	public BluetoothDevice getmBluetoothDevice() {
		return mBluetoothDevice;
	}
	/**
	 * @param mBluetoothDevice the mBluetoothDevice to set
	 */
	public void setmBluetoothDevice(BluetoothDevice mBluetoothDevice) {
		this.mBluetoothDevice = mBluetoothDevice;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}
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
}
