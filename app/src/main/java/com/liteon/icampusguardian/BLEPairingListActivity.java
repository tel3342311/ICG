package com.liteon.icampusguardian;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.liteon.icampusguardian.db.DBHelper;
import com.liteon.icampusguardian.util.BLEItem;
import com.liteon.icampusguardian.util.BLEItemAdapter;
import com.liteon.icampusguardian.util.BLEItemAdapter.ViewHolder.IBLEItemClickListener;
import com.liteon.icampusguardian.util.BluetoothAgent;
import com.liteon.icampusguardian.util.JSONResponse.Student;
import com.liteon.icampusguardian.util.CustomDialog;
import com.liteon.icampusguardian.util.Def;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

public class BLEPairingListActivity extends AppCompatActivity implements IBLEItemClickListener {

	private final static String TAG = BLEPairingListActivity.class.getName();
	private RecyclerView mRecyclerView;
	private RecyclerView.Adapter mAdapter;
	private RecyclerView.LayoutManager mLayoutManager;
	private List<BLEItem> mDataSet;
	private ImageView mCancel;
	//class BT flag
    private boolean isClassBT = true;
	//ble 
	private BluetoothAdapter mBluetoothAdapter;
	private boolean mScanning;
	private Handler mHandler;
    private static final long SCAN_PERIOD = 10000;
    private int REQUEST_ENABLE_BT = 1;
    private BluetoothLeScanner mLEScanner;
    private ScanSettings settings;
    private List<ScanFilter> filters;
    private BluetoothGatt mGatt;
    private static int VERSION_CODES = 21;
    private final static int PERMISSION_REQUEST = 3;
	private DBHelper mDbHelper;
	private List<Student> mStudents;
	private int mCurrnetStudentIdx;
    private BluetoothAgent mBTAgent = null;

    //work around for uuid
    private UUIDList mUUIDList;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		askPermisson();
		//read uuid file
		File downloadDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/" +"UUID.txt");

		getFilesFromDir(downloadDir);	
		setContentView(R.layout.activity_ble_pairing_list);
		findViews();
		setListener();
		initRecycleView();
		if (isClassBT) {
            initClassicBT();
        } else {
            initBleComponent();
        }
		mDbHelper = DBHelper.getInstance(this);
		//get child list
		mStudents = mDbHelper.queryChildList(mDbHelper.getReadableDatabase());


	}
	
	public static boolean hasPermissions(Context context, String... permissions) {
	    if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
	        for (String permission : permissions) {
	            if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
	                return false;
	            }
	        }
	    }
	    return true;
	}
	
	public void getFilesFromDir(File currentFile) {

		try {
			FileInputStream iStream = new FileInputStream(currentFile);
			mUUIDList = (UUIDList) getFakeUUID(iStream, UUIDList.class);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private Object getFakeUUID(InputStream is, Class<?> class_type) {
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line;
        try {
			while ((line = br.readLine()) != null) {
			    sb.append(line+"\n");
			}
	        br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
        return new Gson().fromJson(sb.toString(), class_type);
	}
	
	private void askPermisson() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			if (this.checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
				requestPermissions(new String[] {android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST);
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
				CustomDialog dialog = new CustomDialog();
        		dialog.setTitle("應用程式要求權限以繼續");
        		dialog.setIcon(0);
        		dialog.setBtnText("好");
        		dialog.show(getSupportFragmentManager(), "dialog_fragment");
			}
			
			if (grantResults[1] == PackageManager.PERMISSION_GRANTED) {
				Log.d(TAG, "coarse location permission granted");
			} else {
				CustomDialog dialog = new CustomDialog();
        		dialog.setTitle("應用程式要求權限以繼續");
        		dialog.setIcon(0);
        		dialog.setBtnText("好");
        		dialog.show(getSupportFragmentManager(), "dialog_fragment");
			}
			
			if (grantResults[2] == PackageManager.PERMISSION_GRANTED) {
				Log.d(TAG, "coarse location permission granted");
			} else {
				CustomDialog dialog = new CustomDialog();
        		dialog.setTitle("應用程式要求權限以繼續");
        		dialog.setIcon(0);
        		dialog.setBtnText("好");
        		dialog.show(getSupportFragmentManager(), "dialog_fragment");
			}
			break;
		}
	}
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


	private void initBleComponent() {
		mHandler = new Handler();
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "BLE Not Supported",
                    Toast.LENGTH_SHORT).show();
            finish();
        }
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
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
		if (mUUIDList != null) {
			for (Device device : mUUIDList.getDevices()) {
				BLEItem item = new BLEItem();
				item.setId(device.getUuid());
				item.setName(device.getName());
				mDataSet.add(item);
			}
		} else {
			BLEItem item = new BLEItem();
			item.setId("0000-0000-0000-0000");
			item.setName("iCampus Guardian");
			item.setValue("Not Connected");
			mDataSet.add(item);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else {
		    if (isClassBT) {
		        scanBTDevice();
            } else {
                if (Build.VERSION.SDK_INT >= VERSION_CODES) {
                    mLEScanner = mBluetoothAdapter.getBluetoothLeScanner();
                    settings = new ScanSettings.Builder()
                            .setScanMode(ScanSettings.SCAN_MODE_LOW_POWER)
                            .build();
                    filters = new ArrayList<ScanFilter>();
                }
                scanLeDevice(true);
            }
        }
		SharedPreferences sp = getSharedPreferences(Def.SHARE_PREFERENCE, Context.MODE_PRIVATE);
		mCurrnetStudentIdx = sp.getInt(Def.SP_CURRENT_STUDENT, 0);

        // Register for broadcasts when a device is discovered
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(mReceiver, filter);

        // Register for broadcasts when discovery has finished
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        this.registerReceiver(mReceiver, filter);

	}
	
	@Override
    protected void onPause() {
        super.onPause();
        if (isClassBT) {
            if (mBluetoothAdapter.isDiscovering()) {
                mBluetoothAdapter.cancelDiscovery();
            }
            this.unregisterReceiver(mReceiver);
        }
        else if (mBluetoothAdapter != null && mBluetoothAdapter.isEnabled()) {
            scanLeDevice(false);
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
    	if (mGatt == null) {
            return;
        }
        mGatt.close();
        mGatt = null;  
    }

    private void scanBTDevice() {
        // Indicate scanning in the title
        setProgressBarIndeterminateVisibility(true);
        //setTitle(R.string.scanning);

        // Turn on sub-title for new devices
        //findViewById(R.id.title_new_devices).setVisibility(View.VISIBLE);

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
            }
        }
    };

	private void scanLeDevice(final boolean enable) {
        if (enable) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (Build.VERSION.SDK_INT < VERSION_CODES) {
                        mBluetoothAdapter.stopLeScan(mLeScanCallback);
                    } else {
                        mLEScanner.stopScan(mScanCallback);
 
                    }
                }
            }, SCAN_PERIOD);
            if (Build.VERSION.SDK_INT < VERSION_CODES) {
                mBluetoothAdapter.startLeScan(mLeScanCallback);
            } else {
                mLEScanner.startScan(filters, settings, mScanCallback);
            }
        } else {
            if (Build.VERSION.SDK_INT < VERSION_CODES) {
                mBluetoothAdapter.stopLeScan(mLeScanCallback);
            } else {
                mLEScanner.stopScan(mScanCallback);
            }
        }
    }
	
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
	
	private ScanCallback mScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            Log.i("callbackType", String.valueOf(callbackType));
            Log.i("result", result.toString());
            BluetoothDevice btDevice = result.getDevice();
            updateList(btDevice);
        }
 
        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            for (ScanResult sr : results) {
                Log.i("ScanResult - Results", sr.toString());
            }
        }
 
        @Override
        public void onScanFailed(int errorCode) {
            Log.e("Scan Failed", "Error Code: " + errorCode);
        }
    };
    
    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {
                @Override
                public void onLeScan(final BluetoothDevice device, int rssi,
                                     byte[] scanRecord) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.i("onLeScan", device.toString());
                            //connectToDevice(device);
                            updateList(device);
                        }
                    });
                }
            };
 
    public void connectToDevice(BluetoothDevice device) {
        if (mGatt == null) {
            mGatt = device.connectGatt(this, false, gattCallback);
            scanLeDevice(false);// will stop after first device detection
        }
    }
    
    private final BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            Log.i("onConnectionStateChange", "Status: " + status);
            switch (newState) {
                case BluetoothProfile.STATE_CONNECTED:
                    Log.i("gattCallback", "STATE_CONNECTED");
                    gatt.discoverServices();
                    break;
                case BluetoothProfile.STATE_DISCONNECTED:
                    Log.e("gattCallback", "STATE_DISCONNECTED");
                    break;
                default:
                    Log.e("gattCallback", "STATE_OTHER");
            }
 
        }
 
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            List<BluetoothGattService> services = gatt.getServices();
            Log.i("onServicesDiscovered", services.toString());
            gatt.readCharacteristic(services.get(1).getCharacteristics().get
                    (0));
        }
 
        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic
                                                 characteristic, int status) {
            Log.i("onCharacteristicRead", characteristic.toString());
            gatt.disconnect();
        }
    };
    
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
		
		//if (TextUtils.isEmpty(mStudents.get(mCurrnetStudentIdx).getUuid())) {
		//	mStudents.get(mCurrnetStudentIdx).setUuid(item.getId());
		//	mDbHelper.updateChildData(mDbHelper.getWritableDatabase(), mStudents.get(mCurrnetStudentIdx));
		//}
        BluetoothDevice device = item.getmBluetoothDevice();//mBluetoothAdapter.getRemoteDevice();
        // Attempt to connect to the device
        //mBTAgent.connect(device, true);

        SharedPreferences sp = getSharedPreferences(Def.SHARE_PREFERENCE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(Def.SP_BT_WATCH_ADDRESS, device.getAddress());
        editor.commit();

		Intent intent = new Intent();
		intent.setClass(this, BLEPinCodeInputActivity.class);
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

    private final Handler mHandlerBTClassic = new Handler() {
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
                    Toast.makeText(getApplicationContext(), "Connected to "
                                + mConnectedDeviceName, Toast.LENGTH_SHORT).show();

                    break;
                case Def.MESSAGE_TOAST:
                    Toast.makeText(getApplicationContext(), msg.getData().getString(Def.TOAST),
                                Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
}
