package com.liteon.icampusguardian.fragment;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.liteon.icampusguardian.App;
import com.liteon.icampusguardian.BLEPinCodeInputActivity;
import com.liteon.icampusguardian.ChoosePhotoActivity;
import com.liteon.icampusguardian.R;
import com.liteon.icampusguardian.db.DBHelper;
import com.liteon.icampusguardian.util.BluetoothAgent;
import com.liteon.icampusguardian.util.CircularImageView;
import com.liteon.icampusguardian.util.CustomDialog;
import com.liteon.icampusguardian.util.Def;
import com.liteon.icampusguardian.util.DeviceNameJSON;
import com.liteon.icampusguardian.util.GuardianApiClient;
import com.liteon.icampusguardian.util.JSONResponse;
import com.liteon.icampusguardian.util.JSONResponse.Student;
import com.liteon.icampusguardian.util.SettingItem;
import com.liteon.icampusguardian.util.SettingItemAdapter;
import com.liteon.icampusguardian.util.SettingItemAdapter.ViewHolder.ISettingItemClickListener;
import com.liteon.icampusguardian.util.WearableInfo;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import static com.facebook.FacebookSdk.getApplicationContext;

public class SettingFragment extends Fragment {

    private final static String TAG = SettingFragment.class.getSimpleName();
	private static ArrayList<SettingItem> myDataset = new ArrayList<>();
	private RecyclerView mRecyclerView;
	private RecyclerView.Adapter mAdapter;
	private RecyclerView.LayoutManager mLayoutManager;
	private AppCompatButton mAddAlarm;
	private CircularImageView mChildIcon;
	private EditText mChildName;
	private WeakReference<ISettingItemClickListener> mClicks;
	private DBHelper mDbHelper;
	private List<Student> mStudents;
	private int mCurrnetStudentIdx;
	private Toolbar mToolbar;
	private boolean isEditMode;
	private BluetoothAgent mBTAgent;
    private CustomDialog mCustomDialog;
    private View mRootView;
	public SettingFragment() {

	}

	public SettingFragment(ISettingItemClickListener clicks) {
		mClicks = new WeakReference<ISettingItemClickListener>(clicks);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		setHasOptionsMenu(true);
        mRootView = inflater.inflate(R.layout.fragment_setting, container, false);
		findView(mRootView);
		setOnClickListener();
		initRecycleView();
		mDbHelper = DBHelper.getInstance(getActivity());
		//get child list
		mStudents = mDbHelper.queryChildList(mDbHelper.getReadableDatabase());
		((SettingItemAdapter)mAdapter).setChildData(mStudents.get(mCurrnetStudentIdx));
		mAdapter.notifyDataSetChanged();

		mBTAgent = new BluetoothAgent(getApplicationContext(), mHandlerBTClassic);
		return mRootView;
	}

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mClicks = new WeakReference<ISettingItemClickListener>((ISettingItemClickListener)context);
	}

    private void setOnClickListener() {
		mChildIcon.setOnClickListener(mOnClickListener);
		mChildName.addTextChangedListener(mOnChildNameChangedListener);
	}
	
	private TextWatcher mOnChildNameChangedListener = new TextWatcher() {
		
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			
		}
		
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			
		}
		
		@Override
		public void afterTextChanged(Editable s) {
			if (TextUtils.isEmpty(s.toString())) {
				exitEditMode();
				return;
			}
			if (!TextUtils.equals(mStudents.get(mCurrnetStudentIdx).getNickname(), s.toString())) {
				mStudents.get(mCurrnetStudentIdx).setNickname(s.toString());
				enterEditMode();			
			} else {
				exitEditMode();
			}
		}
	}; 
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.one_confirm_menu, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}
	
	private void enterEditMode() {
		isEditMode = true;
		getActivity().invalidateOptionsMenu();

	}
	
	private void exitEditMode() {
		isEditMode = false;
		getActivity().invalidateOptionsMenu();

	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.action_confirm) {
			new UpdateTask().execute();
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void updateChildInfo() {
		
		GuardianApiClient apiClient = new GuardianApiClient(getContext());
		apiClient.updateChildData(mStudents.get(mCurrnetStudentIdx));
		
	}

	class UpdateTask extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(mChildName.getWindowToken(), 0);
            mRootView.requestFocus();
        }

        protected String doInBackground(Void... params) {

            String btAddress = mDbHelper.getBlueToothAddrByStudentId(mDbHelper.getReadableDatabase(), mStudents.get(mCurrnetStudentIdx).getStudent_id());
            if (!TextUtils.isEmpty(btAddress)) {
                BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
                BluetoothDevice mBluetoothDevice = btAdapter.getRemoteDevice(btAddress);
                mBTAgent.connect(mBluetoothDevice, true);
            } else {
                showBTErrorDialog();
            }
            updateChildInfo();
        	DBHelper helper = DBHelper.getInstance(getActivity());
        	SQLiteDatabase db = helper.getWritableDatabase();
            helper.updateChildByStudentId(db, mStudents.get(mCurrnetStudentIdx));
        	return null;
        }

        protected void onPostExecute(String token) {
        	exitEditMode();
        }
    }
	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
		if (isEditMode) {
			menu.findItem(R.id.action_confirm).setVisible(true);
		} else {
			menu.findItem(R.id.action_confirm).setVisible(false);
		}
	}
	private OnClickListener mOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			Intent intent = new Intent();
			intent.setClass(getActivity(), ChoosePhotoActivity.class);
			startActivity(intent);
		}
	};
	private void findView(View rootView) {
		mChildIcon = (CircularImageView) rootView.findViewById(R.id.child_icon);
		mChildName = (EditText) rootView.findViewById(R.id.child_name);
		mRecyclerView = (RecyclerView) rootView.findViewById(R.id.setting_view);
		mToolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);

	}
	
	private void initRecycleView() {
		mRecyclerView.setHasFixedSize(true);
		mLayoutManager = new LinearLayoutManager(getContext());
		mRecyclerView.setLayoutManager(mLayoutManager);
		testData();
		mAdapter = new SettingItemAdapter(myDataset, mClicks.get());
		mRecyclerView.setAdapter(mAdapter);
	}

	private void initChildInfo() {
		
		mChildIcon.setBorderColor(getResources().getColor(R.color.md_white_1000));
		mChildIcon.setBorderWidth(10);
		mChildIcon.setSelectorColor(getResources().getColor(R.color.md_blue_400));
		mChildIcon.setSelectorStrokeColor(getResources().getColor(R.color.md_blue_800));
		mChildIcon.setSelectorStrokeWidth(10);
		mChildIcon.addShadow();
		
		Bitmap bitmap = null;
		if (mStudents.size() > 0) {
			if (mCurrnetStudentIdx >= mStudents.size()) {
				mCurrnetStudentIdx =  0;
			}
			mChildName.setText(mStudents.get(mCurrnetStudentIdx).getNickname());
			// read child image file
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inPreferredConfig = Bitmap.Config.ARGB_8888;
			bitmap = BitmapFactory
					.decodeFile(
							Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
									.getAbsolutePath() + "/" + mStudents.get(mCurrnetStudentIdx).getStudent_id() + ".jpg",
							options);
		}
		if (bitmap != null) {
			mChildIcon.setImageBitmap(bitmap);
		} else {
			mChildIcon.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.setup_img_picture));
		}
	}
	
	private void testData() {
		if (myDataset.size() == 0) {
			for (SettingItem.TYPE type : SettingItem.TYPE.values()) {
				SettingItem item = new SettingItem();
				item.setItemType(type);
				myDataset.add(item);
			}
		}
	}
	
	@Override
	public void onResume() {
		super.onResume();
		SharedPreferences sp = getActivity().getSharedPreferences(Def.SHARE_PREFERENCE, Context.MODE_PRIVATE);
		mCurrnetStudentIdx = sp.getInt(Def.SP_CURRENT_STUDENT, 0); 
		initChildInfo();
		((SettingItemAdapter)mAdapter).setChildData(mStudents.get(mCurrnetStudentIdx));
		mAdapter.notifyDataSetChanged();
		mToolbar.setTitle("");
	}

    @Override
    public void onPause() {
        super.onPause();
        if (mBTAgent != null) {
            mBTAgent.stop();
        }
    }

    private void syncDataToBT() {
	    if (mBTAgent!=null) {
	        String name = mStudents.get(mCurrnetStudentIdx).getNickname();
            DeviceNameJSON deviceNameJSON = new DeviceNameJSON();
            deviceNameJSON.setName(name);
            deviceNameJSON.setType("devicename");
            Gson gson = new Gson();
            String deviceNameStr = gson.toJson(deviceNameJSON);
            mBTAgent.write(deviceNameStr.getBytes());
        }
    }

    private void showBTErrorDialog() {
        mCustomDialog = new CustomDialog();
        mCustomDialog.setTitle(getString(R.string.alarm_sync_failed));
        mCustomDialog.setIcon(R.drawable.ic_error_outline_black_24dp);
        mCustomDialog.setBtnText(getString(android.R.string.ok));
        mCustomDialog.setBtnConfirm(mOnBLEFailCancelClickListener);
        mCustomDialog.show(getActivity().getSupportFragmentManager(), "dialog_fragment");
    }

    private View.OnClickListener mOnBLEFailCancelClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            mCustomDialog.dismiss();
        }
    };

    public void notifyBTState() {
        mStudents = mDbHelper.queryChildList(mDbHelper.getReadableDatabase());
        ((SettingItemAdapter)mAdapter).setChildData(mStudents.get(mCurrnetStudentIdx));
        mAdapter.notifyDataSetChanged();
	}

    private final Handler mHandlerBTClassic = new Handler() {
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
                    Log.d(TAG, "Response : " + readMessage);break;
                case Def.MESSAGE_DEVICE_NAME:
                    // save the connected device's name
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
}
