package com.liteon.icampusguardian;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.liteon.icampusguardian.db.DBHelper;
import com.liteon.icampusguardian.util.BluetoothAgent;
import com.liteon.icampusguardian.util.ClsUtils;
import com.liteon.icampusguardian.util.ConfirmDeleteDialog;
import com.liteon.icampusguardian.util.CustomDialog;
import com.liteon.icampusguardian.util.Def;
import com.liteon.icampusguardian.util.DeviceNameJSON;
import com.liteon.icampusguardian.util.DeviceUUIDJSON;
import com.liteon.icampusguardian.util.GuardianApiClient;
import com.liteon.icampusguardian.util.JSONResponse;
import com.liteon.icampusguardian.util.JSONResponse.Student;
import com.liteon.icampusguardian.util.PetDataJSON;
import com.liteon.icampusguardian.util.UUIDResponseJSON;
import com.liteon.icampusguardian.util.WearableInfo;

import java.lang.reflect.Type;
import java.util.List;

import static com.liteon.icampusguardian.App.getContext;

public class BLEPinCodeInputActivity extends AppCompatActivity implements View.OnFocusChangeListener, View.OnKeyListener, TextWatcher {

    private final static String TAG = BLEPinCodeInputActivity.class.getSimpleName();
	private ImageView mConfirm;
	private ImageView mCancel;
	private EditText mPinFirstDigitEditText;
    private EditText mPinSecondDigitEditText;
    private EditText mPinThirdDigitEditText;
    private EditText mPinForthDigitEditText;
    private EditText mPinFifthDigitEditText;
    private EditText mPinSixthDigitEditText;
    private EditText mPinHiddenEditText;
    private View mBleConnectingView;
    private ConfirmDeleteDialog mBLEFailConfirmDialog;
	private DBHelper mDbHelper;
	private List<Student> mStudents;
	private int mCurrnetStudentIdx;

    private BluetoothAgent mBTAgent = null;
    private BluetoothDevice mBluetoothDevice;
    private int mLastBondState = BluetoothDevice.BOND_NONE;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.activity_ble_pin_input);
		setContentView(new MainLayout(this, null));
		findViews();
		setListener();
		mDbHelper = DBHelper.getInstance(this);
		//get child list
		mStudents = mDbHelper.queryChildList(mDbHelper.getReadableDatabase());
        mBTAgent = new BluetoothAgent(this, mHandlerBTClassic);
        SharedPreferences sp = getSharedPreferences(Def.SHARE_PREFERENCE, Context.MODE_PRIVATE);
        mCurrnetStudentIdx = sp.getInt(Def.SP_CURRENT_STUDENT, 0);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        registerReceiver(mReceiver, intentFilter);
        Intent intent = getIntent();
        String btAddress = intent.getStringExtra(Def.EXTRA_BT_ADDR);
        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
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
            mBleConnectingView.setVisibility(View.VISIBLE);
        }
    }

	@Override
	protected void onResume() {
		super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBTAgent != null) {
            mBTAgent.stop();
        }
        unregisterReceiver(mReceiver);
    }

    private void findViews() {
		mCancel = findViewById(R.id.cancel);
		mConfirm = findViewById(R.id.confirm);
		mPinFirstDigitEditText = findViewById(R.id.pin_first_edittext);
        mPinSecondDigitEditText = findViewById(R.id.pin_second_edittext);
        mPinThirdDigitEditText = findViewById(R.id.pin_third_edittext);
        mPinForthDigitEditText = findViewById(R.id.pin_forth_edittext);
        mPinFifthDigitEditText = findViewById(R.id.pin_fifth_edittext);
        mPinSixthDigitEditText = findViewById(R.id.pin_sixth_edittext);
        mPinHiddenEditText = findViewById(R.id.pin_hidden_edittext);
        mBleConnectingView = findViewById(R.id.ble_pairing_progress);
 	}
	
	private void setListener() {
		mCancel.setOnClickListener(mOnCancelClickListener);
		mConfirm.setOnClickListener(mOnConfirmClickListener);
		mPinFirstDigitEditText.setOnFocusChangeListener(this);
        mPinSecondDigitEditText.setOnFocusChangeListener(this);
        mPinThirdDigitEditText.setOnFocusChangeListener(this);
        mPinForthDigitEditText.setOnFocusChangeListener(this);
        mPinFifthDigitEditText.setOnFocusChangeListener(this);
        mPinSixthDigitEditText.setOnFocusChangeListener(this);
        mPinHiddenEditText.setOnFocusChangeListener(this);
        
        mPinFirstDigitEditText.setOnKeyListener(this);
        mPinSecondDigitEditText.setOnKeyListener(this);
        mPinThirdDigitEditText.setOnKeyListener(this);
        mPinForthDigitEditText.setOnKeyListener(this);
        mPinFifthDigitEditText.setOnKeyListener(this);
        mPinSixthDigitEditText.setOnKeyListener(this);
        mPinHiddenEditText.setOnKeyListener(this);
        
        mPinHiddenEditText.addTextChangedListener(this);
	}
	
	public void setViewBackground(View view, Drawable background) {
        if (view == null || background == null) {
        	return ;
        }
        view.setBackground(background);
    }
	
	public void hideSoftKeyboard(EditText editText) {
        if (editText == null)
            return;

        InputMethodManager imm = (InputMethodManager) getSystemService(Service.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

	private View.OnClickListener mOnCancelClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
            try {
                ClsUtils.cancelBondProcess(mBluetoothDevice.getClass(), mBluetoothDevice);
            } catch (Exception e) {
                e.printStackTrace();
            }
            onBackPressed();
		}
	};
	
	private View.OnClickListener mOnConfirmClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {

			new ConnectBleTask().execute();
			
		}
	};

	class UpdateUUIDToCloud extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            String uuid = strings[0];
            Student student = mStudents.get(mCurrnetStudentIdx);
            //Save Wearable info
            WearableInfo info = new WearableInfo();
            info.setUuid(uuid);
            info.setBtAddr(mBluetoothDevice.getAddress());
            info.setStudentID(mStudents.get(mCurrnetStudentIdx).getStudent_id());
            if (!mDbHelper.isWearableExist(mDbHelper.getReadableDatabase(), uuid)){
                mDbHelper.insertWearableData(mDbHelper.getWritableDatabase(), info);
            } else {
                mDbHelper.updateWearableData(mDbHelper.getWritableDatabase(), info);
            }

            GuardianApiClient mApiClient = new GuardianApiClient(BLEPinCodeInputActivity.this);
            student.setUuid(uuid);
            JSONResponse response = mApiClient.pairNewDevice(mStudents.get(mCurrnetStudentIdx));
            if (response != null) {
                String statusCode = response.getReturn().getResponseSummary().getStatusCode();
                if (!TextUtils.equals(statusCode, Def.RET_SUCCESS_1)) {
                    //student.setUuid("");
                    //mDbHelper.updateChildData(mDbHelper.getWritableDatabase(), student);
                } else {
                    if (response.getReturn().getResults() != null) {

                        String studentID = Integer.toString(response.getReturn().getResults().getStudent_id());
                        String nickName = response.getReturn().getResults().getNickname();
                        String roll_no = Integer.toString(response.getReturn().getResults().getRoll_no());
                        Student item = new Student();
                        item.setUuid(uuid);
                        item.setStudent_id(Integer.parseInt(studentID));
                        item.setNickname(nickName);
                        item.setRoll_no(Integer.parseInt(roll_no));
                        //For New child
                        if (!TextUtils.equals(studentID, student.getStudent_id())) {

                            item.setNickname(student.getNickname());
                            item.setDob(student.getDob());
                            item.setGender(student.getGender());
                            item.setHeight(student.getHeight());
                            item.setWeight(student.getWeight());
                            mDbHelper.deleteChildByStudentID(mDbHelper.getWritableDatabase(), student.getStudent_id());
                            mDbHelper.insertChild(mDbHelper.getWritableDatabase(), item);
                        }
                        mApiClient.updateChildData(item);
                    }
                    mDbHelper.updateChildByStudentId(mDbHelper.getWritableDatabase(), student);
                }

            }
            return null;
        }
    }
	
	class ConnectBleTask extends AsyncTask<Boolean, Void, Boolean> {

		@Override
		protected void onPreExecute() {
			mBleConnectingView.setVisibility(View.VISIBLE);
		}
		
        protected Boolean doInBackground(Boolean... args) {
        	
            boolean ret = args[0];
            //setPin
            try {

                if (ret && mBTAgent != null) {

                    //Device name update
                    DeviceNameJSON deviceNameInfo = new DeviceNameJSON();
                    deviceNameInfo.setType("devicename");
                    deviceNameInfo.setName(mStudents.get(mCurrnetStudentIdx).getNickname());

                    Gson gson = new Gson();
                    String deviceNameStr = gson.toJson(deviceNameInfo);
                    mBTAgent.write(deviceNameStr.getBytes());

                    SharedPreferences sp = getSharedPreferences(Def.SHARE_PREFERENCE, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString(Def.SP_BT_WATCH_ADDRESS, mBluetoothDevice.getAddress());
                    editor.commit();
                    Thread.sleep(1000);

                    DeviceUUIDJSON deviceUUIDJSON = new DeviceUUIDJSON();
                    deviceUUIDJSON.setType("getuuid");
                    String getUUIDJSON = gson.toJson(deviceUUIDJSON);
                    mBTAgent.write(getUUIDJSON.getBytes());
                    Thread.sleep(1000);

                    PetDataJSON petDataJSON = new PetDataJSON();
                    petDataJSON.setType("petviewdata");
                    petDataJSON.setAction("new");
                    petDataJSON.setPetType("dog");
                    petDataJSON.setPetName("Snoopy");
                    String strPetData = gson.toJson(petDataJSON);
                    mBTAgent.write(strPetData.getBytes());

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        	return ret;
        }

        protected void onPostExecute(Boolean success) {
        	mBleConnectingView.setVisibility(View.INVISIBLE);
        	if (success.booleanValue() == true) {
        		CustomDialog dialog = new CustomDialog();
        		String title = String.format(getString(R.string.pairing_watch_success), mStudents.get(mCurrnetStudentIdx).getNickname());
        		dialog.setTitle(title);
        		dialog.setIcon(0);
        		dialog.setBtnText(getString(android.R.string.ok));
        		dialog.setBtnConfirm(mOnBLEFailCancelClickListener);
                try {
                    getSupportFragmentManager().popBackStackImmediate();
                } catch (IllegalStateException ignored) {
                    return;
                }
        		dialog.show(getSupportFragmentManager(), "dialog_fragment");
        	} else {
        		mBLEFailConfirmDialog = new ConfirmDeleteDialog();
        		mBLEFailConfirmDialog.setOnConfirmEventListener(mOnBLEFailConfirmClickListener);
        		mBLEFailConfirmDialog.setmOnCancelListener(mOnBLEFailCancelClickListener);
        		mBLEFailConfirmDialog.setmTitleText(getString(R.string.pairing_watch_pin_error));
        		mBLEFailConfirmDialog.setmBtnConfirmText(getString(R.string.pairing_watch_pair));
        		mBLEFailConfirmDialog.setmBtnCancelText(getString(R.string.pairing_watch_later));
                try {
                    getSupportFragmentManager().popBackStackImmediate();
                } catch (IllegalStateException ignored) {
                    return;
                }
        		mBLEFailConfirmDialog.show(getSupportFragmentManager(), "dialog_fragment");
        	}
        }
    }
	
	private OnClickListener mOnBLEFailConfirmClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			mBLEFailConfirmDialog.dismiss();
			mPinFirstDigitEditText.setText("");
	        mPinSecondDigitEditText.setText("");
	        mPinThirdDigitEditText.setText("");
	        mPinForthDigitEditText.setText("");
            mPinFifthDigitEditText.setText("");
            mPinSixthDigitEditText.setText("");
			mPinHiddenEditText.setText("");
			//mPinFirstDigitEditText.requestFocus();
            onBackPressed();
		}
	};

	private OnClickListener mOnBLEFailCancelClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			finish();
			Intent intent = new Intent();
			intent.setClass(BLEPinCodeInputActivity.this, MainActivity.class);
            intent.putExtra(Def.EXTRA_GOTO_PAGE_ID, Def.EXTRA_PAGE_SETTING_ID);
			startActivity(intent);
		}
	};
	
	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		setDefaultPinBackground(mPinFirstDigitEditText);
        setDefaultPinBackground(mPinSecondDigitEditText);
        setDefaultPinBackground(mPinThirdDigitEditText);
        setDefaultPinBackground(mPinForthDigitEditText);
        setDefaultPinBackground(mPinFifthDigitEditText);
        setDefaultPinBackground(mPinSixthDigitEditText);

        if (s.length() == 0) {
            setFocusedPinBackground(mPinFirstDigitEditText);
            mPinFirstDigitEditText.setText("");
        } else if (s.length() == 1) {
            setFocusedPinBackground(mPinSecondDigitEditText);
            mPinFirstDigitEditText.setText(s.charAt(0) + "");
            mPinSecondDigitEditText.setText("");
            mPinThirdDigitEditText.setText("");
            mPinForthDigitEditText.setText("");
            mPinFifthDigitEditText.setText("");
            mPinSixthDigitEditText.setText("");
        } else if (s.length() == 2) {
            setFocusedPinBackground(mPinThirdDigitEditText);
            mPinSecondDigitEditText.setText(s.charAt(1) + "");
            mPinThirdDigitEditText.setText("");
            mPinForthDigitEditText.setText("");
            mPinFifthDigitEditText.setText("");
            mPinSixthDigitEditText.setText("");
        } else if (s.length() == 3) {
        	setFocusedPinBackground(mPinForthDigitEditText);
            mPinThirdDigitEditText.setText(s.charAt(2) + "");
            mPinForthDigitEditText.setText("");
            mPinFifthDigitEditText.setText("");
            mPinSixthDigitEditText.setText("");
        } else if (s.length() == 4) {
            setFocusedPinBackground(mPinFifthDigitEditText);
            mPinForthDigitEditText.setText(s.charAt(3) + "");
            mPinFifthDigitEditText.setText("");
            mPinSixthDigitEditText.setText("");

        } else if (s.length() == 5) {
            setFocusedPinBackground(mPinSixthDigitEditText);
            mPinFifthDigitEditText.setText(s.charAt(4) + "");
            mPinSixthDigitEditText.setText("");


        } else if (s.length() == 6) {
            setDefaultPinBackground(mPinSixthDigitEditText);
            mPinSixthDigitEditText.setText(s.charAt(5) + "");

            hideSoftKeyboard(mPinSixthDigitEditText);
        }
	}

	@Override
	public void afterTextChanged(Editable s) {
		
	}

	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_DOWN) {
            final int id = v.getId();
            switch (id) {
                case R.id.pin_hidden_edittext:
                    if (keyCode == KeyEvent.KEYCODE_DEL) {
                        if (mPinHiddenEditText.getText().length() == 6)
                            mPinSixthDigitEditText.setText("");
                        else if (mPinHiddenEditText.getText().length() == 5)
                            mPinFifthDigitEditText.setText("");
                        else if (mPinHiddenEditText.getText().length() == 4)
                            mPinForthDigitEditText.setText("");
                        else if (mPinHiddenEditText.getText().length() == 3)
                            mPinThirdDigitEditText.setText("");
                        else if (mPinHiddenEditText.getText().length() == 2)
                            mPinSecondDigitEditText.setText("");
                        else if (mPinHiddenEditText.getText().length() == 1)
                            mPinFirstDigitEditText.setText("");

                        if (mPinHiddenEditText.length() > 0)
                            mPinHiddenEditText.setText(mPinHiddenEditText.getText().subSequence(0, mPinHiddenEditText.length() - 1));

                        
                        return true;
                    }

                    break;

                default:
                    return false;
            }
        }

        return false;
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		final int id = v.getId();
        switch (id) {

            case R.id.pin_first_edittext:
            case R.id.pin_second_edittext:
            case R.id.pin_third_edittext:
            case R.id.pin_forth_edittext:
            case R.id.pin_fifth_edittext:
            case R.id.pin_sixth_edittext:
                if (hasFocus) {
                    setFocus(mPinHiddenEditText);
                    showSoftKeyboard(mPinHiddenEditText);
                }
                break;
            default:
                break;	
        }
	}
	
	public void showSoftKeyboard(EditText editText) {
        if (editText == null)
            return;

        InputMethodManager imm = (InputMethodManager) getSystemService(Service.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editText, 0);
    }
	
	private void setDefaultPinBackground(EditText editText) {

        setViewBackground(editText, ContextCompat.getDrawable(getContext(), R.drawable.btn_bg));
    }
	
	private void setFocusedPinBackground(EditText editText) {
        setViewBackground(editText, ContextCompat.getDrawable(getContext(), R.drawable.pin_bg_selected));
    }
	
	public static void setFocus(EditText editText) {
        if (editText == null)
            return;

        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();
    }
	
	/**
     * Overridden onMeasure() method
     * for handling software keyboard show and hide events.
     */
	public class MainLayout extends RelativeLayout {

        public MainLayout(Context context, AttributeSet attributeSet) {
            super(context, attributeSet);
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            inflater.inflate(R.layout.activity_ble_pin_input, this);
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            final int proposedHeight = MeasureSpec.getSize(heightMeasureSpec);
            final int actualHeight = getHeight();

            Log.d("TAG", "proposed: " + proposedHeight + ", actual: " + actualHeight);

            if (actualHeight >= proposedHeight) {
                // Keyboard is shown
                if (mPinHiddenEditText.length() == 0) {
                    setFocusedPinBackground(mPinFirstDigitEditText);
                    View view = BLEPinCodeInputActivity.this.getCurrentFocus();
                    if (view != null) {  
                        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.showSoftInput(view, 0);
                    }
                } else {
                    setDefaultPinBackground(mPinFirstDigitEditText);
                }
            }

            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)) {
                int bondState = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE,
                        BluetoothDevice.ERROR);
                // New Paired device
                Log.d(TAG, "bond state : " + bondState);
                if (bondState == BluetoothDevice.BOND_BONDED) {
                    mBTAgent.connect(mBluetoothDevice, true);
                } else if (bondState == BluetoothDevice.BOND_NONE && mLastBondState == BluetoothDevice.BOND_BONDING) {
                    new ConnectBleTask().execute(new Boolean(false));
                }
                mLastBondState = bondState;
            }
        }
    };

    private final Handler mHandlerBTClassic = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Def.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothAgent.STATE_CONNECTED:
                            //setStatus(getString(R.string.title_connected_to, mConnectedDeviceName));
                            //mConversationArrayAdapter.clear();
                            mBleConnectingView.setVisibility(View.INVISIBLE);
                            new ConnectBleTask().execute(new Boolean(true));
                            //new Ble
                            break;
                        case BluetoothAgent.STATE_CONNECTING:
                            //setStatus(R.string.title_connecting);
                            mBleConnectingView.setVisibility(View.VISIBLE);
                            break;
                        case BluetoothAgent.STATE_LISTEN:
                        case BluetoothAgent.STATE_NONE:
                            //setStatus(R.string.title_not_connected);
                            //new ConnectBleTask().execute(new Boolean(false));
                            break;
                    }
                    break;
                case Def.MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    // construct a string from the buffer
                    String writeMessage = new String(writeBuf);
                    Log.d(TAG, "Write data : " + writeMessage);
                    //mConversationArrayAdapter.add("Me:  " + writeMessage);
                    break;
                case Def.MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    Log.d(TAG, "Response :" + readMessage);
                    if (readMessage.contains("uuid")) {
                        Gson gson = new GsonBuilder().create();
                        Type typeOfUUIDRepsonse = new TypeToken<UUIDResponseJSON>(){}.getType();

                        UUIDResponseJSON responseJSON = gson.fromJson(readMessage, typeOfUUIDRepsonse);
                        String uuid = responseJSON.getUuid();
                        Log.d(TAG, "Device UUID : " + uuid);
                        new UpdateUUIDToCloud().execute(uuid);

                    }
                    //mConversationArrayAdapter.add(mConnectedDeviceName + ":  " + readMessage);

                    break;
                case Def.MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    String mConnectedDeviceName = msg.getData().getString(Def.DEVICE_NAME);
//                    Toast.makeText(getApplicationContext(), "Connected to "
//                            + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                    if (!TextUtils.isEmpty(mConnectedDeviceName)) {

                    }
                    break;
                case Def.MESSAGE_TOAST:
//                    Toast.makeText(getApplicationContext(), msg.getData().getString(Def.TOAST),
//                            Toast.LENGTH_SHORT).show();
                    if (TextUtils.equals(msg.getData().getString(Def.TOAST), Def.BT_ERR_UNABLE_TO_CONNECT)) {
                        new ConnectBleTask().execute(new Boolean(false));
                    }
                    break;
            }
        }
    };
}
