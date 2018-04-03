package com.liteon.icampusguardian.fragment;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.Key;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.gson.Gson;
import com.liteon.icampusguardian.App;
import com.liteon.icampusguardian.ChoosePhotoActivity;
import com.liteon.icampusguardian.R;
import com.liteon.icampusguardian.db.DBHelper;
import com.liteon.icampusguardian.util.BluetoothAgent;
import com.liteon.icampusguardian.util.CircularImageView;
import com.liteon.icampusguardian.util.ClsUtils;
import com.liteon.icampusguardian.util.CustomDialog;
import com.liteon.icampusguardian.util.Def;
import com.liteon.icampusguardian.util.DeviceNameJSON;
import com.liteon.icampusguardian.util.GuardianApiClient;
import com.liteon.icampusguardian.util.JSONResponse.Student;
import com.liteon.icampusguardian.util.SettingItem;
import com.liteon.icampusguardian.util.SettingItemAdapter;
import com.liteon.icampusguardian.util.SettingItemAdapter.ViewHolder.ISettingItemClickListener;

import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

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
	private int mCurrentStudentIdx;
	private Toolbar mToolbar;
	private boolean isEditMode;
	private BluetoothAgent mBTAgent;
	private String mBTAddress;
    private CustomDialog mCustomDialog;
    private View mRootView;
	public SettingFragment() {

	}

	public SettingFragment(ISettingItemClickListener clicks) {
		mClicks = new WeakReference<>(clicks);
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
		SharedPreferences sp = getActivity().getSharedPreferences(Def.SHARE_PREFERENCE,Context.MODE_PRIVATE);
		mCurrentStudentIdx = sp.getInt(Def.SP_CURRENT_STUDENT, 0);
		if (mCurrentStudentIdx >= mStudents.size()) {
			mCurrentStudentIdx =  0;
		}

		mStudents = mDbHelper.queryChildList(mDbHelper.getReadableDatabase());
        //get current bt address
		if (mStudents.size() > 0) {
			String studentID = mStudents.get(mCurrentStudentIdx).getStudent_id();
			mBTAddress = mDbHelper.getBlueToothAddrByStudentId(mDbHelper.getReadableDatabase(), studentID);

			((SettingItemAdapter) mAdapter).setChildData(mStudents.get(mCurrentStudentIdx), mBTAddress);
			mAdapter.notifyDataSetChanged();
		}
		mBTAgent = new BluetoothAgent(getApplicationContext(), mHandlerBTClassic);
		return mRootView;
	}

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mClicks = new WeakReference<>((ISettingItemClickListener)context);
	}

    private void setOnClickListener() {
		mChildIcon.setOnClickListener(mOnClickListener);
		mChildName.addTextChangedListener(mOnChildNameChangedListener);
	}
	
	private TextWatcher mOnChildNameChangedListener = new TextWatcher() {
		private int currentEnd = 0;
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			currentEnd = start + count;
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
			//For chinese 7 char
			boolean isChanged = false;
			if (ClsUtils.isChinese(s.toString())) {

				while (s.toString().length() > 7) { // if chinese char more than 7
					// delete last char
					currentEnd--;
					s.delete(currentEnd, currentEnd + 1);
					isChanged = true;
				}
				enterEditMode();
			}
			if (!TextUtils.equals(mStudents.get(mCurrentStudentIdx).getNickname(), s.toString())
					|| s.toString().length() == 14 || isChanged) {
				mStudents.get(mCurrentStudentIdx).setNickname(s.toString());
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
        FragmentActivity activity = getActivity();
        if (activity != null) {
            activity.invalidateOptionsMenu();
        }

	}
	
	private void exitEditMode() {
		isEditMode = false;
        FragmentActivity activity = getActivity();
        if (activity != null){
            activity.invalidateOptionsMenu();
        }
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.action_confirm) {
			new UpdateTask().execute();
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void updateChildInfo() {
		
		GuardianApiClient apiClient = GuardianApiClient.getInstance(App.getContext());
		apiClient.updateChildData(mStudents.get(mCurrentStudentIdx));
		
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

            String btAddress = mDbHelper.getBlueToothAddrByStudentId(mDbHelper.getReadableDatabase(), mStudents.get(mCurrentStudentIdx).getStudent_id());
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
            helper.updateChildByStudentId(db, mStudents.get(mCurrentStudentIdx));
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
	private OnClickListener mOnClickListener = v -> {
        Intent intent = new Intent();
        intent.setClass(getActivity(), ChoosePhotoActivity.class);
        startActivity(intent);
    };
	private void findView(View rootView) {
		mChildIcon = rootView.findViewById(R.id.child_icon);
		mChildName = rootView.findViewById(R.id.child_name);
		mRecyclerView = rootView.findViewById(R.id.setting_view);
		mToolbar = getActivity().findViewById(R.id.toolbar);

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
		
		if (mStudents.size() > 0) {
			if (mCurrentStudentIdx >= mStudents.size()) {
				mCurrentStudentIdx =  0;
			}
			mChildName.setText(mStudents.get(mCurrentStudentIdx).getNickname());
            //get current bt address
            String studentID = mStudents.get(mCurrentStudentIdx).getStudent_id();
            mBTAddress = mDbHelper.getBlueToothAddrByStudentId(mDbHelper.getReadableDatabase(), studentID);
			// read child image file
			RequestOptions options = new RequestOptions();
			options.centerCrop();
			options.placeholder(R.drawable.setup_img_picture);
			options.error(R.drawable.setup_img_picture);
            options.diskCacheStrategy(DiskCacheStrategy.NONE);
            options.signature(new Key() {
                @Override
                public void updateDiskCacheKey(MessageDigest messageDigest) {
                    messageDigest.update(ByteBuffer.allocate(Integer.SIZE).putInt((int)System.currentTimeMillis()).array());
                }
            });
            Glide.with(App.getContext()).asBitmap().load(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
					.getAbsolutePath() + "/" + mStudents.get(mCurrentStudentIdx).getStudent_id() + ".jpg").apply(options).into(new SimpleTarget<Bitmap>() {

				@Override
				public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
					mChildIcon.setImageBitmap(resource);
				}

                @Override
                public void onLoadFailed(@Nullable Drawable errorDrawable) {
                    super.onLoadFailed(errorDrawable);
                    mChildIcon.setImageDrawable(errorDrawable);
                }
            });

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
        mStudents = mDbHelper.queryChildList(mDbHelper.getReadableDatabase());		SharedPreferences sp = getActivity().getSharedPreferences(Def.SHARE_PREFERENCE, Context.MODE_PRIVATE);
		mCurrentStudentIdx = sp.getInt(Def.SP_CURRENT_STUDENT, 0);
		initChildInfo();
		((SettingItemAdapter)mAdapter).setChildData(mStudents.get(mCurrentStudentIdx), mBTAddress);
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
	        String name = mStudents.get(mCurrentStudentIdx).getNickname();
            DeviceNameJSON deviceNameJSON = new DeviceNameJSON();
            deviceNameJSON.setName(name);
            deviceNameJSON.setType("devicename");
            Gson gson = new Gson();
            String deviceNameStr = gson.toJson(deviceNameJSON);
            mBTAgent.write(deviceNameStr.getBytes());
        }
    }

    private void showBTErrorDialog() {
		FragmentActivity activity = getActivity();
		if (activity != null){
			mCustomDialog = new CustomDialog();
			mCustomDialog.setTitle(activity.getString(R.string.alarm_sync_failed));
			mCustomDialog.setIcon(R.drawable.ic_error_outline_black_24dp);
			mCustomDialog.setBtnText(getString(android.R.string.ok));
			mCustomDialog.setBtnConfirm(mOnBLEFailCancelClickListener);
			mCustomDialog.show(activity.getSupportFragmentManager(), "dialog_fragment");
		}

    }

    private View.OnClickListener mOnBLEFailCancelClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            mCustomDialog.dismiss();
        }
    };

    public void notifyBTState() {
		SharedPreferences sp = getActivity().getSharedPreferences(Def.SHARE_PREFERENCE,Context.MODE_PRIVATE);		mStudents = mDbHelper.queryChildList(mDbHelper.getReadableDatabase());
		mCurrentStudentIdx = sp.getInt(Def.SP_CURRENT_STUDENT, 0);
		//get current bt address
        String studentID = mStudents.get(mCurrentStudentIdx).getStudent_id();
        mBTAddress = mDbHelper.getBlueToothAddrByStudentId(mDbHelper.getReadableDatabase(), studentID);

        ((SettingItemAdapter)mAdapter).setChildData(mStudents.get(mCurrentStudentIdx), mBTAddress);
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
