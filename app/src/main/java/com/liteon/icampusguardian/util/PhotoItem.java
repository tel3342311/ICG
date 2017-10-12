package com.liteon.icampusguardian.util;

import android.graphics.Bitmap;
import android.net.Uri;

public class PhotoItem {

	public String filePath;
	public String uri;
	/**
	 * @return the uri
	 */
	public String getUri() {
		return uri;
	}
	/**
	 * @param uri the uri to set
	 */
	public void setUri(String uri) {
		this.uri = uri;
	}
	public Bitmap mBitmap;
	public boolean isChecked;
	/**
	 * @return the isChecked
	 */
	public boolean isChecked() {
		return isChecked;
	}
	/**
	 * @param isChecked the isChecked to set
	 */
	public void setChecked(boolean isChecked) {
		this.isChecked = isChecked;
	}
	/**
	 * @return the uri
	 */
	public String getFilePath() {
		return filePath;
	}
	/**
	 * @param uri the uri to set
	 */
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	/**
	 * @return the mBitmap
	 */
	public Bitmap getmBitmap() {
		return mBitmap;
	}
	/**
	 * @param mBitmap the mBitmap to set
	 */
	public void setmBitmap(Bitmap mBitmap) {
		this.mBitmap = mBitmap;
	}
	
}
