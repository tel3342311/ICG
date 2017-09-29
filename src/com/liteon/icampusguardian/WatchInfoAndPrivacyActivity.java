package com.liteon.icampusguardian;

import java.util.List;

import com.liteon.icampusguardian.db.DBHelper;
import com.liteon.icampusguardian.util.ConfirmDeleteDialog;
import com.liteon.icampusguardian.util.CustomDialog;
import com.liteon.icampusguardian.util.Def;
import com.liteon.icampusguardian.util.JSONResponse.Student;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatRadioButton;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class WatchInfoAndPrivacyActivity extends AppCompatActivity {

	private Toolbar mToolbar;
	private AppCompatCheckBox mTeacherCheck;
	private TextView mTextViewDeviceName;
	private TextView mTextViewFirmwardVersion;
	private FrameLayout progressBarHolder;
	private AppCompatButton mUpdateFirmwareBtn;
	private DBHelper mDbHelper;
	private List<Student> mStudents;
	private int mCurrnetStudentIdx;
	private final static int REQUEST_WATCH_UPDATE = 1;
	private final static int REQUEST_WATCH_UPDATE_FIRMWARE = 2;
	private boolean isUpdateFirmwareFailed;
    private ConfirmDeleteDialog mBLEFailConfirmDialog;
    private CustomDialog mDialog;
	private boolean mIsTeacher;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_watch_info_and_privacy);
		findViews();
		setListener();
		setupToolbar();
	}
	
	private void findViews() {
		mToolbar = (Toolbar) findViewById(R.id.toolbar);
		mTeacherCheck = (AppCompatCheckBox) findViewById(R.id.teacher_user_plan);
		mTextViewDeviceName = (TextView) findViewById(R.id.watch_info_device_title_value);
		mTextViewFirmwardVersion = (TextView) findViewById(R.id.watch_info_firmware_title_value);
		progressBarHolder = (FrameLayout) findViewById(R.id.progressBarHolder);
		mUpdateFirmwareBtn = (AppCompatButton) findViewById(R.id.watch_info_update_btn);
	}
	
	private void setListener() {
		mTeacherCheck.setOnCheckedChangeListener(mOnCheckedChangeListener);
		mUpdateFirmwareBtn.setOnClickListener(mOnUpdateBtnClickListener);
	}
	
	@Override
	protected void onResumeFragments() {
		super.onResumeFragments();
		if (isUpdateFirmwareFailed) {
			mDialog = new CustomDialog();
    		mDialog.setTitle(getString(R.string.connect_failed));
    		mDialog.setIcon(R.drawable.ic_error_outline_black_24dp);
    		mDialog.setBtnText(getString(android.R.string.ok));
    		mDialog.setBtnConfirm(mOnSyncFailConfirmClickListener);
    		mDialog.show(getSupportFragmentManager(), "dialog_fragment");
    		isUpdateFirmwareFailed = false;
		}
	}
	
	@Override
	public void onBackPressed() {
		Intent intent = new Intent();
    	intent.setClass(WatchInfoAndPrivacyActivity.this, MainActivity.class);
    	intent.putExtra(Def.EXTRA_GOTO_MAIN_SETTING, true);
    	startActivity(intent);
    	finish();		
	}
	
	private View.OnClickListener mOnUpdateBtnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			new UpdateTask().execute("");
		}
	};
	private OnCheckedChangeListener mOnCheckedChangeListener = new OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			SharedPreferences sp = getSharedPreferences(Def.SHARE_PREFERENCE, Context.MODE_PRIVATE);
			Editor editor = sp.edit();
			editor.putBoolean(Def.SP_TEACHER_PLAN, isChecked);
			editor.commit();
		}
		
	};
	private void setupToolbar() {
		setSupportActionBar(mToolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);
		mToolbar.setNavigationIcon(R.drawable.ic_navigate_before_white_24dp);
		mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				Intent intent = new Intent();
            	intent.setClass(WatchInfoAndPrivacyActivity.this, MainActivity.class);
            	intent.putExtra(Def.EXTRA_GOTO_MAIN_SETTING, true);
            	startActivity(intent);
            	finish();
			}
		});
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_WATCH_UPDATE) {
			if (resultCode == RESULT_OK) {

			}
		} else if (requestCode == REQUEST_WATCH_UPDATE_FIRMWARE) {
			if (resultCode == RESULT_CANCELED) {
				isUpdateFirmwareFailed = true;
			}
		}
	};

	@Override
	protected void onResume() {
		super.onResume();
		mDbHelper = DBHelper.getInstance(this);
		//get child list
		mStudents = mDbHelper.queryChildList(mDbHelper.getReadableDatabase());
		SharedPreferences sp = getSharedPreferences(Def.SHARE_PREFERENCE, Context.MODE_PRIVATE);
		mCurrnetStudentIdx = sp.getInt(Def.SP_CURRENT_STUDENT, 0); 
		mIsTeacher = sp.getBoolean(Def.SP_TEACHER_PLAN, false);
		mToolbar.setTitle(R.string.watch_info_privacy);
		mTeacherCheck.setChecked(mIsTeacher);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
	}
	
	class UpdateTask extends AsyncTask<String, Void, Boolean> {

		@Override
		protected void onPreExecute() {
			progressBarHolder.setVisibility(View.VISIBLE);
			mUpdateFirmwareBtn.setVisibility(View.INVISIBLE);
		}
		
        protected Boolean doInBackground(String... args) {
        	
        	//TODO add ble connection function
        	try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
        	return Boolean.TRUE;
        }

        protected void onPostExecute(Boolean success) {
        	progressBarHolder.setVisibility(View.GONE);
			mUpdateFirmwareBtn.setVisibility(View.VISIBLE);

        	if (success == true) {
        		mBLEFailConfirmDialog = new ConfirmDeleteDialog();
        		mBLEFailConfirmDialog.setOnConfirmEventListener(mOnBLEFailConfirmClickListener);
        		mBLEFailConfirmDialog.setmOnCancelListener(mOnBLEFailCancelClickListener);
        		mBLEFailConfirmDialog.setmTitleText(getString(R.string.update_to_version));
        		mBLEFailConfirmDialog.setmBtnConfirmText(getString(R.string.watch_update_install));
        		mBLEFailConfirmDialog.setmBtnCancelText(getString(R.string.watch_update_cancel));
        		mBLEFailConfirmDialog.show(getSupportFragmentManager(), "dialog_fragment");
        	} else {
        		mDialog = new CustomDialog();
        		mDialog.setTitle(getString(R.string.syncing_fail));
        		mDialog.setIcon(R.drawable.ic_error_outline_black_24dp);
        		mDialog.setBtnText(getString(android.R.string.ok));
        		mDialog.setBtnConfirm(mOnSyncFailConfirmClickListener);
        		mDialog.show(getSupportFragmentManager(), "dialog_fragment");
        	}
        }
    }
	
	private View.OnClickListener mOnBLEFailConfirmClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			mBLEFailConfirmDialog.dismiss();
			Intent intent = new Intent();
			intent.setClass(WatchInfoAndPrivacyActivity.this, FirmwareDownLoadingActivity.class);
			startActivityForResult(intent, REQUEST_WATCH_UPDATE_FIRMWARE);
			
		}
	};
	
	private View.OnClickListener mOnBLEFailCancelClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			mBLEFailConfirmDialog.dismiss();
		}
	};
	
	private View.OnClickListener mOnSyncFailConfirmClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			mDialog.dismiss();
		}
	};
}
