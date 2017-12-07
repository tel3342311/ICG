package com.liteon.icampusguardian;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.liteon.icampusguardian.db.DBHelper;
import com.liteon.icampusguardian.util.BluetoothAgent;
import com.liteon.icampusguardian.util.ConfirmDeleteDialog;
import com.liteon.icampusguardian.util.CustomDialog;
import com.liteon.icampusguardian.util.Def;
import com.liteon.icampusguardian.util.GuardianApiClient;
import com.liteon.icampusguardian.util.JSONResponse.Student;

import java.util.List;
import java.util.Set;

public class WatchInfoAndPrivacyActivity extends AppCompatActivity {

	private Toolbar mToolbar;
	private ImageView mBackBtn;
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
	//For bluetooth
	private BluetoothAgent mBTAgent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_watch_info_and_privacy);
		findViews();
		setListener();
		mBTAgent = new BluetoothAgent(this, mHandler);
	}
	
	private void findViews() {
		mToolbar = findViewById(R.id.toolbar);
		mBackBtn = findViewById(R.id.cancel);
		mTeacherCheck = findViewById(R.id.teacher_user_plan);
		mTextViewDeviceName = findViewById(R.id.watch_info_device_title_value);
		mTextViewFirmwardVersion = findViewById(R.id.watch_info_firmware_title_value);
		progressBarHolder = findViewById(R.id.progressBarHolder);
		mUpdateFirmwareBtn = findViewById(R.id.watch_info_update_btn);
	}
	
	private void setListener() {
		mTeacherCheck.setOnCheckedChangeListener(mOnCheckedChangeListener);
		mUpdateFirmwareBtn.setOnClickListener(mOnUpdateBtnClickListener);
		mBackBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				Intent intent = new Intent();
				intent.setClass(WatchInfoAndPrivacyActivity.this, MainActivity.class);
				intent.putExtra(Def.EXTRA_GOTO_PAGE_ID, Def.EXTRA_PAGE_SETTING_ID);
				startActivity(intent);
				finish();
			}
		});
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
    	intent.putExtra(Def.EXTRA_GOTO_PAGE_ID, Def.EXTRA_PAGE_SETTING_ID);
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

			new GrantTeacherTask().execute();
		}
		
	};
	
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
		mTeacherCheck.setChecked(mIsTeacher);

		//Get BT device and check if the device is BONDED
		Set<BluetoothDevice> pairedDevices = BluetoothAdapter.getDefaultAdapter().getBondedDevices();
		BluetoothDevice target = null;
		if (pairedDevices.size() >= 1) {
			for (BluetoothDevice device : pairedDevices) {
				target = device;
				break;
			}
			mBTAgent.connect(target, true);
		}
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		if (mBTAgent != null) {
			mBTAgent .stop();
		}
	}

	class GrantTeacherTask extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... voids) {
			GuardianApiClient mApiClient = new GuardianApiClient(WatchInfoAndPrivacyActivity.this);
			mApiClient.grantTeacherAccessToSleepData(mStudents.get(mCurrnetStudentIdx));
			return null;
		}
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

	private final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case Def.MESSAGE_STATE_CHANGE:
					switch (msg.arg1) {
						case BluetoothAgent.STATE_CONNECTED:
							//setStatus(getString(R.string.title_connected_to, mConnectedDeviceName));
							//mConversationArrayAdapter.clear();
							break;
						case BluetoothAgent.STATE_CONNECTING:
							//setStatus(R.string.title_connecting);
							break;
						case BluetoothAgent.STATE_LISTEN:
						case BluetoothAgent.STATE_NONE:
							//setStatus(R.string.title_not_connected);
							break;
					}
					break;
				case Def.MESSAGE_WRITE:
					byte[] writeBuf = (byte[]) msg.obj;
					// construct a string from the buffer
					String writeMessage = new String(writeBuf);
					//mConversationArrayAdapter.add("Me:  " + writeMessage);
					break;
				case Def.MESSAGE_READ:
					byte[] readBuf = (byte[]) msg.obj;
					// construct a string from the valid bytes in the buffer
					String readMessage = new String(readBuf, 0, msg.arg1);
//					Toast.makeText(App.getContext(), "Response : "
//							+ readMessage, Toast.LENGTH_SHORT).show();
					break;
				case Def.MESSAGE_DEVICE_NAME:
					// save the connected device's name
					String mConnectedDeviceName = msg.getData().getString(Def.DEVICE_NAME);
//					Toast.makeText(App.getContext(), "Connected to "
//							+ mConnectedDeviceName, Toast.LENGTH_SHORT).show();

					break;
				case Def.MESSAGE_TOAST:
//                    Toast.makeText(App.getContext(), msg.getData().getString(Def.TOAST),
//                            Toast.LENGTH_SHORT).show();
					break;
			}
		}
	};
}
