package com.liteon.icampusguardian;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.liteon.icampusguardian.db.DBHelper;
import com.liteon.icampusguardian.util.BluetoothAgent;
import com.liteon.icampusguardian.util.ClsUtils;
import com.liteon.icampusguardian.util.ConfirmDeleteDialog;
import com.liteon.icampusguardian.util.CustomDialog;
import com.liteon.icampusguardian.util.CustomSkinJSON;
import com.liteon.icampusguardian.util.CustomSkinResponseJSON;
import com.liteon.icampusguardian.util.Def;
import com.liteon.icampusguardian.util.JSONResponse.Student;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

public class PersonalizedWatchActivity extends AppCompatActivity {

	private static final String TAG = PersonalizedWatchActivity.class.getSimpleName();
	private Toolbar mToolbar;
	private ProgressBar mProgressBar;
	private ImageView mWatchSurface;
	private ImageView mWatchCover;
	private TextView mTitleUpdating;
	private DBHelper mDbHelper;
	private List<Student> mStudents;
	private int mCurrentStudentIdx;
	private final static int REQUEST_WATCH_SURFACE = 1001;
    private ConfirmDeleteDialog mBLEFailConfirmDialog;
	private ImageView mCancel;
	private ImageView mConfirm;
	private BluetoothAgent mBTAgent;
	private BluetoothDevice mBluetoothDevice;
    private int mLastBondState = BluetoothDevice.BOND_NONE;
    private CustomDialog mCustomDialog;
	private byte[] mFileBytes;
	private boolean isEditMode;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_personalized_watch);
		findViews();
		setListener();
		setupToolbar();
		mProgressBar.setVisibility(View.INVISIBLE);
		mTitleUpdating.setVisibility(View.INVISIBLE);
        mBTAgent = new BluetoothAgent(this, mHandler);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        registerReceiver(mReceiver, intentFilter);
	}
	
	private void findViews() {
		mToolbar = findViewById(R.id.toolbar);
		mProgressBar = findViewById(R.id.loading_progress);
		mWatchSurface = findViewById(R.id.watch_surface);
		mTitleUpdating = findViewById(R.id.watch_surface_updating_text);
		mWatchCover = findViewById(R.id.watch_cover);
		mCancel = findViewById(R.id.cancel);
	}

	private void enterEditMode() {
        isEditMode = true;
    }
	private void setListener() {
		mWatchSurface.setOnClickListener(mOnChangeSurfaceListener);
	}
	
	private void setupToolbar() {
		setSupportActionBar(mToolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(false);
		getSupportActionBar().setHomeButtonEnabled(false);
		mCancel.setOnClickListener(v -> {

            Intent intent = new Intent();
            intent.setClass(PersonalizedWatchActivity.this, MainActivity.class);
            intent.putExtra(Def.EXTRA_GOTO_PAGE_ID, Def.EXTRA_PAGE_SETTING_ID);
            startActivity(intent);
            finish();
        });
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.one_confirm_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	public boolean onPrepareOptionsMenu(Menu menu) {
        if (isEditMode) {
            menu.findItem(R.id.action_confirm).setVisible(true);
        } else {
            menu.findItem(R.id.action_confirm).setVisible(false);
        }
		return true;
	};

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
			case R.id.action_confirm:
				connectToBT();
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	private View.OnClickListener mOnChangeSurfaceListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			Intent intent = new Intent();
			intent.setClass(PersonalizedWatchActivity.this, ChoosePhotoActivity.class);
			intent.putExtra(Def.EXTRA_CHOOSE_PHOTO_TYPE, Def.EXTRA_CHOOSE_WATCH_ICON);
			startActivityForResult(intent, REQUEST_WATCH_SURFACE);
		}
	};
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_WATCH_SURFACE) {
			if (resultCode == RESULT_OK) {
                enterEditMode();
                invalidateOptionsMenu();
			}
		} else if (requestCode == Def.REQUEST_ENABLE_BT) {
			connectToBT();
		}
	};

	private void setupWatchSurface() {
		Bitmap bitmap = null;
		if (mStudents.size() > 0) {
			// read child image file
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inPreferredConfig = Bitmap.Config.RGB_565;
			bitmap = BitmapFactory
					.decodeFile(
							Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
									.getAbsolutePath() + "/" + mStudents.get(mCurrentStudentIdx).getStudent_id() + "_watch.jpg",
							options);
		}
		if (bitmap != null) {
			mWatchSurface.setImageBitmap(bitmap);
			mWatchCover.setVisibility(View.VISIBLE);
		} else {
			mWatchSurface.setImageDrawable(null);
			mWatchCover.setVisibility(View.GONE);
		}
	}
	@Override
	protected void onResume() {
		super.onResume();
		mToolbar.setTitle("");
		mDbHelper = DBHelper.getInstance(this);
		//get child list
		mStudents = mDbHelper.queryChildList(mDbHelper.getReadableDatabase());
		SharedPreferences sp = getSharedPreferences(Def.SHARE_PREFERENCE, Context.MODE_PRIVATE);
		mCurrentStudentIdx = sp.getInt(Def.SP_CURRENT_STUDENT, 0);
		setupWatchSurface();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
	}
	
	private View.OnClickListener mOnBLEFailConfirmClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			mBLEFailConfirmDialog.dismiss();
            connectToBT();
		}
	};
	
	private View.OnClickListener mOnBLEFailCancelClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			mBLEFailConfirmDialog.dismiss();
			Intent intent = new Intent();
        	intent.setClass(PersonalizedWatchActivity.this, MainActivity.class);
        	intent.putExtra(Def.EXTRA_GOTO_PAGE_ID, Def.EXTRA_PAGE_SETTING_ID);
        	startActivity(intent);
        	finish();
		}
	};

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }

    private void connectToBT() {

        //get current bt address
        String studentID = mStudents.get(mCurrentStudentIdx).getStudent_id();
		BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
		if (btAdapter == null || !btAdapter.isEnabled()) {
			Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableBtIntent, Def.REQUEST_ENABLE_BT);
			return;
		}

		String btAddress = mDbHelper.getBlueToothAddrByStudentId(mDbHelper.getReadableDatabase(), studentID);
		if (TextUtils.isEmpty(btAddress) || !mBTAgent.isBluetoothAvailable()) {
            showBTErrorDialog();
            return;
        }
        mBluetoothDevice = btAdapter.getRemoteDevice(btAddress);
        //check if target device is bonded
        if (mBluetoothDevice.getBondState() == BluetoothDevice.BOND_BONDED) {
            mBTAgent.connect(mBluetoothDevice, true);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                mBluetoothDevice.createBond();
            } else {
                try {
                    ClsUtils.createBond(mBluetoothDevice.getClass(), mBluetoothDevice);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void syncDataToBT() {
        mProgressBar.setVisibility(View.VISIBLE);
        mTitleUpdating.setText(R.string.syncing_photo_to_watch);
        mTitleUpdating.setVisibility(View.VISIBLE);

        File watchSkinFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                .getAbsolutePath() + "/" + mStudents.get(mCurrentStudentIdx).getStudent_id() + "_watch.jpg");
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(watchSkinFile);
            mFileBytes = IOUtils.toByteArray(fis);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (mFileBytes != null && mFileBytes.length > 0) {
            //sync alarm data with watch
            CustomSkinJSON customSkinJSON = new CustomSkinJSON();
            customSkinJSON.setType("customskinmetadata");
            customSkinJSON.setFileType("jpeg");
            customSkinJSON.setFileSize(mFileBytes.length);
			Gson gson = new Gson();
			String requestStr = gson.toJson(customSkinJSON);
            mBTAgent.write(requestStr.getBytes());
		}

        final Handler handler = new Handler();
        final Runnable hideSyncView = () -> {
            mProgressBar.setVisibility(View.INVISIBLE);
			mTitleUpdating.setText(R.string.syncing_photo_to_watch_complete);
        };
        Runnable runnable = () -> {
            handler.postDelayed(hideSyncView, 3000);
            if (mBTAgent != null) {
                mBTAgent.stop();
            }
        };
        handler.postDelayed(runnable, 2000);
    }

    private void showBTErrorDialog() {
        mTitleUpdating.setVisibility(View.INVISIBLE);
        mBLEFailConfirmDialog = new ConfirmDeleteDialog();
        mBLEFailConfirmDialog.setOnConfirmEventListener(mOnBLEFailConfirmClickListener);
        mBLEFailConfirmDialog.setmOnCancelListener(mOnBLEFailCancelClickListener);
        mBLEFailConfirmDialog.setmTitleText(getString(R.string.syncing_photo_to_watch_failed));
        mBLEFailConfirmDialog.setmBtnConfirmText(getString(R.string.syncing_photo_to_watch_synced));
        mBLEFailConfirmDialog.setmBtnCancelText(getString(R.string.syncing_photo_to_watch_cancel));
		try {
			getSupportFragmentManager().popBackStackImmediate();
		} catch (IllegalStateException ignored) {
			return;
		}
        mBLEFailConfirmDialog.show(getSupportFragmentManager(), "dialog_fragment");
    }

    //If customSkin data is successfully sent, then write skin image data.
    private void syncImageToBT(String response) {
        Type typeOfResponse = new TypeToken<CustomSkinResponseJSON>(){}.getType();
        Gson gson = new GsonBuilder().create();
        CustomSkinResponseJSON responseJSON = gson.fromJson(response, typeOfResponse);
        if (responseJSON != null) {
            if (TextUtils.equals(responseJSON.getType(), "customskinmetadata")&&
                    TextUtils.equals(responseJSON.getAck(), "SUCCESS")) {
                mBTAgent.write(mFileBytes);
            }
        }
    }

	private final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case Def.MESSAGE_STATE_CHANGE:
					switch (msg.arg1) {
						case BluetoothAgent.STATE_CONNECTED:
							Log.d(TAG, "[BT][STATE_CONNECTED]");
							break;
						case BluetoothAgent.STATE_CONNECTING:
							Log.d(TAG, "[BT][STATE_CONNECTING]");
							break;
						case BluetoothAgent.STATE_LISTEN:
						case BluetoothAgent.STATE_NONE:
							Log.d(TAG, "[BT][STATE_NONE]");
							break;
					}
					break;
				case Def.MESSAGE_WRITE:
					byte[] writeBuf = (byte[]) msg.obj;
					// construct a string from the buffer
					String writeMessage = new String(writeBuf);
					Log.d(TAG, "Write data : " + writeMessage);
					break;
				case Def.MESSAGE_READ:
					byte[] readBuf = (byte[]) msg.obj;
					// construct a string from the valid bytes in the buffer
					String readMessage = new String(readBuf, 0, msg.arg1);
					syncImageToBT(readMessage);
					Log.d(TAG, "Response : " + readMessage);
					break;
				case Def.MESSAGE_DEVICE_NAME:
					// save the connected device's name
					String mConnectedDeviceName = msg.getData().getString(Def.DEVICE_NAME);
					//Toast.makeText(App.getContext(), "Connected to "
					//        + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
					if (!TextUtils.isEmpty(mConnectedDeviceName)) {
					    syncDataToBT();
					}
					break;
				case Def.MESSAGE_TOAST:
					String message = msg.getData().getString(Def.TOAST);
					Log.d(TAG, msg.getData().getString(Def.TOAST));
					if (TextUtils.equals(message, Def.BT_ERR_UNABLE_TO_CONNECT)) {
						showBTErrorDialog();
					}
					break;
			}
		}
	};

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)) {
                int bondState = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE,
                        BluetoothDevice.ERROR);
                // New Paired device
                Log.d(TAG, "bond state : " + bondState);
                if (bondState == BluetoothDevice.BOND_BONDED) {
                    connectToBT();
                } else if (bondState == BluetoothDevice.BOND_NONE && mLastBondState == BluetoothDevice.BOND_BONDING) {
                    showBTErrorDialog();
                }
                mLastBondState = bondState;
            }
        }
    };
}
