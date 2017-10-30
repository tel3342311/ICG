package com.liteon.icampusguardian;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.annotations.SerializedName;
import com.liteon.icampusguardian.db.DBHelper;
import com.liteon.icampusguardian.util.BLEItem;
import com.liteon.icampusguardian.util.BLEItemAdapter;
import com.liteon.icampusguardian.util.BLEItemAdapter.ViewHolder.IBLEItemClickListener;
import com.liteon.icampusguardian.util.BluetoothAgent;
import com.liteon.icampusguardian.util.ConfirmDeleteDialog;
import com.liteon.icampusguardian.util.Def;
import com.liteon.icampusguardian.util.JSONResponse.Student;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class BLEPairingListActivity extends AppCompatActivity implements IBLEItemClickListener {

	private final static String TAG = BLEPairingListActivity.class.getSimpleName();
	private RecyclerView mRecyclerView;
	private RecyclerView.Adapter mAdapter;
	private RecyclerView.LayoutManager mLayoutManager;
	private List<BLEItem> mDataSet;
	private ImageView mCancel;
	//ble
	private BluetoothAdapter mBluetoothAdapter;
	private boolean mScanning;
	private Handler mHandler;
    private static final long SCAN_PERIOD = 10000;
    private int REQUEST_ENABLE_BT = 1;
    private final static int PERMISSION_REQUEST = 3;
	private DBHelper mDbHelper;
	private List<Student> mStudents;
	private int mCurrnetStudentIdx;
    private BluetoothAgent mBTAgent = null;
    private ConfirmDeleteDialog mPermissionDialog;
    //work around for uuid
    private UUIDList mUUIDList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		askPermisson();

		setContentView(R.layout.activity_ble_pairing_list);
		findViews();
		setListener();
		initRecycleView();
		initClassicBT();

		mDbHelper = DBHelper.getInstance(this);
		//get child list
		mStudents = mDbHelper.queryChildList(mDbHelper.getReadableDatabase());


	}
	
	private void askPermisson() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			if (this.checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
				requestPermissions(new String[] {android.Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST);
			}
		}
	}
	
	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		switch(requestCode) {
		case PERMISSION_REQUEST:
			if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				Log.d(TAG, "coarse location permission granted");
			} else {
                mPermissionDialog = new ConfirmDeleteDialog();
                mPermissionDialog.setOnConfirmEventListener(mOnPermissionConfirmClickListener);
                mPermissionDialog.setmOnCancelListener(mOnPermissionCancelClickListener);
                mPermissionDialog.setmTitleText(getString(R.string.pairing_watch_ask_permission));
                mPermissionDialog.setmBtnConfirmText(getString(R.string.bind_confirm));
                mPermissionDialog.setmBtnCancelText(getString(R.string.bind_cancel));
                mPermissionDialog.show(getSupportFragmentManager(), "dialog_fragment");
			}
			break;
		}
	}

	private View.OnClickListener mOnPermissionConfirmClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            if (mPermissionDialog != null) {
                mPermissionDialog.dismiss();
            }
            askPermisson();
        }
    };

    private View.OnClickListener mOnPermissionCancelClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            if (mPermissionDialog != null) {
                mPermissionDialog.dismiss();
            }
            finish();
            Intent intent = new Intent();
            intent.setClass(BLEPairingListActivity.this, MainActivity.class);
            intent.putExtra(Def.EXTRA_GOTO_MAIN_SETTING, true);
            startActivity(intent);        }
    };

	//for class BT set up
	private void initClassicBT() {
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mBTAgent = new BluetoothAgent(this, mHandlerBTClassic);

		// Get a set of currently paired devices
		Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

		// If there are paired devices, add each one to the ArrayAdapter
		if (pairedDevices.size() > 0) {
			for (BluetoothDevice device : pairedDevices) {
                updateList(device);
			}
		}
	}
	
	private void initRecycleView() {
		mRecyclerView.setHasFixedSize(true);
		mLayoutManager = new LinearLayoutManager(this);
		mRecyclerView.setLayoutManager(mLayoutManager);
		setupData();
		mAdapter = new BLEItemAdapter(mDataSet, this);
		mRecyclerView.setAdapter(mAdapter);
	}
	
	private void setupData(){
		mDataSet = new ArrayList<>();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else {
			scanBTDevice();
        }
		SharedPreferences sp = getSharedPreferences(Def.SHARE_PREFERENCE, Context.MODE_PRIVATE);
		mCurrnetStudentIdx = sp.getInt(Def.SP_CURRENT_STUDENT, 0);

        // Register for broadcasts when a device is discovered
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        // Register for broadcasts when discovery has finished
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        //Register for broadcasts the UUID wrapped as a ParcelUuid of the remote device after it has been fetched.
        //filter.addAction(BluetoothDevice.ACTION_UUID);
        this.registerReceiver(mReceiver, filter);
	}
	
	@Override
    protected void onPause() {
        super.onPause();

		if (mBluetoothAdapter.isDiscovering()) {
			mBluetoothAdapter.cancelDiscovery();
		}
		this.unregisterReceiver(mReceiver);
		if (mBTAgent != null) {
			mBTAgent.stop();
		}

    }
 
    @Override
    protected void onDestroy() {
    	super.onDestroy();

        // Make sure we're not doing discovery anymore
        if (mBluetoothAdapter != null) {
            mBluetoothAdapter.cancelDiscovery();
        }

        if (mBTAgent != null) {
            mBTAgent.stop();
        }
    }

    private void scanBTDevice() {

        // If we're already discovering, stop it
        if (mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();
        }

        // Request discover from BluetoothAdapter
        mBluetoothAdapter.startDiscovery();
	}


    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // If it's already paired, skip it, because it's been listed already
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    updateList(device);
                }
                // When discovery is finished, change the Activity title
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                setProgressBarIndeterminateVisibility(false);
            } else if (BluetoothDevice.ACTION_UUID.equals(action)) {
				// This is when we can be assured that fetchUuidsWithSdp has completed.
				// So get the uuids and call fetchUuidsWithSdp on another device in list
				BluetoothDevice deviceExtra = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				Parcelable[] uuidExtra = intent.getParcelableArrayExtra(BluetoothDevice.EXTRA_UUID);
				Log.d(TAG, "DeviceExtra address - " + deviceExtra.getAddress());
				if (uuidExtra != null) {
					for (Parcelable p : uuidExtra) {
						Log.d(TAG, "uuidExtra - " + p);
					}
				} else {
					Log.d(TAG, "uuidExtra is still null");
				}
			}
        }
    };
	
	private void updateList(BluetoothDevice device) {
		BluetoothDevice btDevice = device;
		boolean isDuplicated = false;
		for (BLEItem item : mDataSet) {
			BluetoothDevice bluetoothDevice = item.getmBluetoothDevice();
			if (bluetoothDevice == null) {
				continue;
			} else if (TextUtils.equals(btDevice.getAddress(), bluetoothDevice.getAddress()) ) {
				isDuplicated = true;
				break;
			}
		}
		if (isDuplicated) {
			return;
		}
        BLEItem item = new BLEItem();
        item.setName(btDevice.getName());
        item.setId(btDevice.getAddress());
        //item.setId(btDevice.getUuids());
        int bond_state = btDevice.getBondState();
        if (bond_state == btDevice.BOND_NONE) {
        	item.setValue("Not Connected");
        } else {
        	item.setValue("Connected");
        }
        item.setmBluetoothDevice(btDevice);
        //connectToDevice(btDevice);
        mDataSet.add(item);
        mAdapter.notifyDataSetChanged();
	}
    
	private void findViews() {
		mRecyclerView = (RecyclerView) findViewById(R.id.profile_view);
		mCancel = (ImageView) findViewById(R.id.cancel);
 	}
	
	private void setListener() {
		mCancel.setOnClickListener(mOnCancelClickListener);
	}
	
	private View.OnClickListener mOnCancelClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			onBackPressed();
		}
	};
	
	class UpdateInfoTask extends AsyncTask<String, Void, String> {

		@Override
		protected void onPreExecute() {


		}
		
        protected String doInBackground(String... args) {
        	

        	return null;
        }

        protected void onPostExecute(String token) {
        	Intent intent = new Intent();
        	intent.setClass(BLEPairingListActivity.this, ChildPairingActivity.class);
        	startActivity(intent);
        }
    }

	@Override
	public void onBleItemClick(BLEItem item) {
        if (mBTAgent != null) {
            mBTAgent.stop();
        }
        BluetoothDevice device = item.getmBluetoothDevice();
        Intent intent = new Intent();
        intent.setClass(BLEPairingListActivity.this, BLEPinCodeInputActivity.class);
        intent.putExtra(Def.EXTRA_BT_ADDR, device.getAddress());
        startActivity(intent);
    }
	
	class UUIDList {
		
		@SerializedName("devices")
		private Device[] devices;

		/**
		 * @return the devices
		 */
		public Device[] getDevices() {
			return devices;
		}

		/**
		 * @param devices the devices to set
		 */
		public void setDevices(Device[] devices) {
			this.devices = devices;
		}
	}
	class Device {
		@SerializedName("name")
		private String name;
		@SerializedName("uuid")
		private String uuid;
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
		 * @return the uuid
		 */
		public String getUuid() {
			return uuid;
		}
		/**
		 * @param uuid the uuid to set
		 */
		public void setUuid(String uuid) {
			this.uuid = uuid;
		}
		
	}

    private final static Handler mHandlerBTClassic = new Handler() {
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
                    //mConversationArrayAdapter.add(mConnectedDeviceName + ":  " + readMessage);
                    break;
                case Def.MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    String mConnectedDeviceName = msg.getData().getString(Def.DEVICE_NAME);
                    Toast.makeText(App.getContext(), "Connected to "
                                + mConnectedDeviceName, Toast.LENGTH_SHORT).show();

                    break;
                case Def.MESSAGE_TOAST:
                    Toast.makeText(App.getContext(), msg.getData().getString(Def.TOAST),
                                Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
}
