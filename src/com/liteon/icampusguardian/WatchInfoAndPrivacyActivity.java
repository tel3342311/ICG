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
	
	private View.OnClickListener mOnChangeSurfaceListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			Intent intent = new Intent();
			intent.setClass(WatchInfoAndPrivacyActivity.this, ChoosePhotoActivity.class);
			intent.putExtra(Def.EXTRA_CHOOSE_PHOTO_TYPE, Def.EXTRA_CHOOSE_WATCH_ICON);
			startActivityForResult(intent, REQUEST_WATCH_UPDATE);
		}
	};
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_WATCH_UPDATE) {
			if (resultCode == RESULT_OK) {

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
		mToolbar.setTitle("智慧手錶資訊與使用隱私");
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
        	return Boolean.FALSE;
        }

        protected void onPostExecute(Boolean success) {
        	progressBarHolder.setVisibility(View.GONE);
			mUpdateFirmwareBtn.setVisibility(View.VISIBLE);

        	if (success == true) {
        		mBLEFailConfirmDialog = new ConfirmDeleteDialog();
        		mBLEFailConfirmDialog.setOnConfirmEventListener(mOnBLEFailConfirmClickListener);
        		mBLEFailConfirmDialog.setmOnCancelListener(mOnBLEFailCancelClickListener);
        		mBLEFailConfirmDialog.setmTitleText("智慧手錶韌體更新\n將更新成V1.XX版\n韌體更新資料較大，建議使用wifi下載讓更新過程較順利");
        		mBLEFailConfirmDialog.setmBtnConfirmText("安裝");
        		mBLEFailConfirmDialog.setmBtnCancelText("取消");
        		mBLEFailConfirmDialog.show(getSupportFragmentManager(), "dialog_fragment");
        	} else {
        		mDialog = new CustomDialog();
        		mDialog.setTitle("同步失敗");
        		mDialog.setBtnText("好");
        		mDialog.setBtnConfirm(mOnSyncFailConfirmClickListener);
        		mDialog.show(getSupportFragmentManager(), "dialog_fragment");
        	}
        }
    }
	
	private View.OnClickListener mOnBLEFailConfirmClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			mBLEFailConfirmDialog.dismiss();
			new UpdateTask().execute("");
			
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
