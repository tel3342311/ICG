package com.liteon.icampusguardian.fragment;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.liteon.icampusguardian.R;
import com.liteon.icampusguardian.db.DBHelper;
import com.liteon.icampusguardian.util.AlarmItemAdapter;
import com.liteon.icampusguardian.util.AlarmItemAdapter.ViewHolder.IAlarmViewHolderClicks;
import com.liteon.icampusguardian.util.AlarmManager;
import com.liteon.icampusguardian.util.BluetoothAgent;
import com.liteon.icampusguardian.util.ClsUtils;
import com.liteon.icampusguardian.util.ConfirmDeleteDialog;
import com.liteon.icampusguardian.util.CustomDialog;
import com.liteon.icampusguardian.util.Def;
import com.liteon.icampusguardian.util.JSONResponse.Student;

import java.lang.ref.WeakReference;
import java.util.List;

public class AlarmFragment extends Fragment  implements IAlarmViewHolderClicks {

    private final static String TAG = AlarmFragment.class.getSimpleName();
	private RecyclerView mRecyclerView;
	private RecyclerView.Adapter mAdapter;
	private RecyclerView.LayoutManager mLayoutManager;
	private AppCompatButton mAddAlarm;
	private WeakReference<IAddAlarmClicks> mAddAlarmClicks;
	private boolean isEditMode;
	private Toolbar mToolbar;
	private TextView mTitleView;
	private DBHelper mDbHelper;
	private List<Student> mStudents;
	private int mCurrentStudentIdx;
	private ConfirmDeleteDialog mConfirmDeleteDialog;
	private PopupWindow mPopupWindow;
	private View mSyncView;
	private View mProgressView;
	private Button mSyncBtn;
	//For bluetooth
	private BluetoothAgent mBTAgent;
    private BluetoothDevice mBluetoothDevice;
    private int mLastBondState = BluetoothDevice.BOND_NONE;
    private CustomDialog mCustomDialog;
    private ProgressBar mProgressBar;
    private boolean isAlarmEditSync;
    public AlarmFragment() {}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		setHasOptionsMenu(true);
		View rootView = inflater.inflate(R.layout.fragment_alarm, container, false);
		findView(rootView);
		initRecycleView();
		setupListener();
		mDbHelper = DBHelper.getInstance(getActivity());
		//get child list
		mStudents = mDbHelper.queryChildList(mDbHelper.getReadableDatabase());
		mBTAgent = new BluetoothAgent(getContext(), mHandler);
		return rootView;
	}

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		mAddAlarmClicks = new WeakReference<>((IAddAlarmClicks)context);

	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.alarm_menu, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}
	
	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		if (AlarmManager.getDataSet().size() == 0 ) {
            menu.findItem(R.id.action_complete).setVisible(false);
            menu.findItem(R.id.action_edit).setVisible(false);
        } else {
            if (isEditMode) {
                menu.findItem(R.id.action_complete).setVisible(true);
                menu.findItem(R.id.action_edit).setVisible(false);
            } else {
                menu.findItem(R.id.action_complete).setVisible(false);
                menu.findItem(R.id.action_edit).setVisible(true);
            }
        }
	    super.onPrepareOptionsMenu(menu);
	}
	
	public boolean isEditMode() {
		return isEditMode;
	}
	
	public void exitEditMode() {
		isEditMode = false;
		getActivity().invalidateOptionsMenu();
		((AlarmItemAdapter)mAdapter).setEditMode(false);
		mTitleView.setText(getString(R.string.alarm));
        showSyncWindow();
	}
	
	public void enterEditMode() {
		isEditMode = true;
		getActivity().invalidateOptionsMenu();
		((AlarmItemAdapter)mAdapter).setEditMode(true);
		//mToolbar.setTitle("編輯鬧鈴");
		mTitleView.setText(getString(R.string.alarm_edit_period));
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
        if (mProgressView.getVisibility() == View.VISIBLE) {
            return super.onOptionsItemSelected(item);
        }
		switch (item.getItemId()) {
		case R.id.action_edit:
			enterEditMode();
			break;
		case R.id.action_complete:
			exitEditMode();
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void showAddAlarmErrorDialog() {
		final CustomDialog dialog = new CustomDialog();
		dialog.setTitle(getString(R.string.alarm_max_alarm));
		dialog.setIcon(R.drawable.ic_error_outline_black_24dp);
		dialog.setBtnText(getString(android.R.string.ok));
		dialog.setBtnConfirm(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		dialog.show(getActivity().getSupportFragmentManager(), "dialog_fragment");
	}
	
	public void findView(View rootView) {
		mRecyclerView = rootView.findViewById(R.id.alarm_view);
		mAddAlarm = rootView.findViewById(R.id.add_alarm);
		mToolbar = getActivity().findViewById(R.id.toolbar);
		mTitleView = getActivity().findViewById(R.id.toolbar_title);
		mSyncView = rootView.findViewById(R.id.sync_view);
        mProgressView = rootView.findViewById(R.id.progress_view);
        mSyncBtn = mSyncView.findViewById(R.id.button_sync);

	}
	
	public void initRecycleView() {
		mRecyclerView.setHasFixedSize(true);
		mLayoutManager = new LinearLayoutManager(getContext());
		mRecyclerView.setLayoutManager(mLayoutManager);
		mAdapter = new AlarmItemAdapter(AlarmManager.getDataSet(), this);
		mRecyclerView.setAdapter(mAdapter);
	}
	
	private void setupListener() {
		mAddAlarm.setOnClickListener(mAddAlarmClickListener);
        mSyncBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                startSync();
            }
        });
	}
	
	private OnClickListener mAddAlarmClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			if (AlarmManager.getDataSet().size() >= 4) {
				showAddAlarmErrorDialog();
				return;
			}
			mAddAlarmClicks.get().onAddAlarmClick();
			AlarmManager.setCurrentAction(AlarmManager.ACTION_ADDING);
            getActivity().invalidateOptionsMenu();
		}
	};

	public static interface IAddAlarmClicks {
        public void onAddAlarmClick();
        public void onEditAlarm(int idx);
    }

	@Override
	public void onEditAlarm(int position) {
		if (isEditMode()) {
			mAddAlarmClicks.get().onEditAlarm(position);
			AlarmManager.setCurrentAction(AlarmManager.ACTION_EDITING);
			AlarmManager.setCurrentItem(AlarmManager.getDataSet().get(position), position);
		}
	}
	
	private int mDeleteIdx = 0;
	@Override
	public void onDeleteAlarm(int delete) {
		mConfirmDeleteDialog = new ConfirmDeleteDialog();
		mConfirmDeleteDialog.setOnConfirmEventListener(mOnConfirmDelete);
		mConfirmDeleteDialog.show(getActivity().getSupportFragmentManager(), "dialog_fragment");
		mDeleteIdx = delete;

	}
	
	@Override
	public void onEnableAlarm(int position, boolean enable) {
		AlarmManager.getDataSet().get(position).setEnabled(enable);
		mSyncView.setVisibility(View.VISIBLE);
	}
	
	private View.OnClickListener mOnConfirmDelete = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			AlarmManager.getDataSet().remove(mDeleteIdx);
			mAdapter.notifyDataSetChanged();
			mConfirmDeleteDialog.dismiss();
			if (AlarmManager.getDataSet().size() == 0) {
				exitEditMode();
			}
            getActivity().invalidateOptionsMenu();
		}
	};
	@Override
	public void onResume() {
		super.onResume();
		SharedPreferences sp = getActivity().getSharedPreferences(Def.SHARE_PREFERENCE, Context.MODE_PRIVATE);
		mCurrentStudentIdx = sp.getInt(Def.SP_CURRENT_STUDENT, 0);
		if (mStudents.size() > 0 && mCurrentStudentIdx >= mStudents.size()) {
			mCurrentStudentIdx = 0;
		}
		showSyncWindow();
		restoreAlarm();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        getActivity().registerReceiver(mReceiver, intentFilter);
	}
	
	@Override
	public void onPause() {
		super.onPause();
		saveAlarm();
		hideSyncWindow();
		if (mBTAgent != null) {
            mBTAgent.stop();
        }
        getActivity().unregisterReceiver(mReceiver);
	}
	
	private void hideSyncWindow() {
		if (mPopupWindow != null && mPopupWindow.isShowing()) {
			mPopupWindow.dismiss();
		}
	}
	private void showSyncWindow() {
        mProgressView.setVisibility(View.INVISIBLE);
        mAddAlarm.setEnabled(true);
    }

	private void syncEditDataToBT() {
		//sync alarm data with watch
		String alarmString = AlarmManager.getAlarmEditContent();
		if (!TextUtils.isEmpty(alarmString)) {
			mBTAgent.write(alarmString.getBytes());
		}
	}

	private void syncStateDataToBT() {
		//sync alarm state (enable/disable) with watch
		String alarmState = AlarmManager.getAlarmStateContent();
		if (!TextUtils.isEmpty(alarmState)) {
			mBTAgent.write(alarmState.getBytes());
		}
	}

	private void showSynced() {
		View contentview = mSyncView;
		final TextView title = contentview.findViewById(R.id.title);
		final Handler handler = new Handler();
		final Runnable hideSyncView = () -> mSyncView.setVisibility(View.GONE);
		Runnable runnable = () -> {
            title.setText(R.string.alarm_sync_complete);
            handler.postDelayed(hideSyncView, 3000);
            if (mBTAgent != null) {
                mBTAgent.stop();
                isAlarmEditSync = false;
            }
            mProgressView.setVisibility(View.INVISIBLE);
			mAddAlarm.setEnabled(true);
        };
		handler.postDelayed(runnable, 2000);
	}


	private void restoreAlarm() {
	    AlarmManager.setCurrentStudentIdx(mCurrentStudentIdx);
        AlarmManager.restoreAlarm();
	}

	private void saveAlarm() {
		AlarmManager.saveAlarm();
	}

	public void startSync() {
        View contentview = mSyncView;
        final TextView title = contentview.findViewById(R.id.title);
        mProgressView.setVisibility(View.VISIBLE);
        mAddAlarm.setEnabled(false);
        title.setText(R.string.alarm_syncing);
        //connect to BT device
        connectToBT();
    }


	private void connectToBT() {
        //get current bt address
        String studentID = mStudents.get(mCurrentStudentIdx).getStudent_id();
        String btAddress = mDbHelper.getBlueToothAddrByStudentId(mDbHelper.getReadableDatabase(), studentID);

		BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();

		if (btAdapter == null || !btAdapter.isEnabled()) {
			Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableBtIntent, Def.REQUEST_ENABLE_BT);
			return;
		}

        if (TextUtils.isEmpty(btAddress) || !mBTAgent.isBluetoothAvailable()) {
            showBTErrorDialog();
            return;
        }

        mBluetoothDevice = btAdapter.getRemoteDevice(btAddress);

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

    private void getAlarmFromBT() {
        String requestStr = AlarmManager.requestAlarm();
        mBTAgent.write(requestStr.getBytes());
    }

	private void showBTErrorDialog() {
        mCustomDialog = new CustomDialog();
        mCustomDialog.setTitle(getString(R.string.alarm_sync_failed));
        mCustomDialog.setIcon(R.drawable.ic_error_outline_black_24dp);
        mCustomDialog.setBtnText(getString(android.R.string.ok));
        mCustomDialog.setBtnConfirm(mOnBLEFailCancelClickListener);
        mCustomDialog.setCancelable(false);
        mCustomDialog.show(getActivity().getSupportFragmentManager(), "dialog_fragment");
    }

	private View.OnClickListener mOnBLEFailCancelClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            mCustomDialog.dismiss();
            TextView title = mSyncView.findViewById(R.id.title);
            title.setText(R.string.alarm_no_watch_synced);
            mProgressView.setVisibility(View.INVISIBLE);
            mAddAlarm.setEnabled(true);

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
                    mBTAgent.connect(mBluetoothDevice, true);
                } else if (bondState == BluetoothDevice.BOND_NONE && mLastBondState == BluetoothDevice.BOND_BONDING) {
                    showBTErrorDialog();
                }
                mLastBondState = bondState;
            }
        }
    };

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
                    Log.d(TAG, "Response : " + readMessage);
                    if (readMessage.contains("getalarmdata")) {
                        AlarmManager.syncAlarmFromJSON(readMessage);
						mAdapter.notifyDataSetChanged();
						syncEditDataToBT();
                        getActivity().invalidateOptionsMenu();
					} else if (readMessage.contains("alarm")) {
                    	if (!isAlarmEditSync) {
                    		syncStateDataToBT();
							isAlarmEditSync = true;
						} else {
							showSynced();
						}
					}
                    break;
                case Def.MESSAGE_DEVICE_NAME:
                    //Connected device's name
                    //String mConnectedDeviceName = msg.getData().getString(Def.DEVICE_NAME);
					getAlarmFromBT();
                    break;
                case Def.MESSAGE_TOAST:
                    String message = msg.getData().getString(Def.TOAST);
                    Log.d(TAG, msg.getData().getString(Def.TOAST));
                    if (getActivity() == null) {
                        return;
                    }
                    if (TextUtils.equals(message, Def.BT_ERR_UNABLE_TO_CONNECT)) {
                        showBTErrorDialog();
                    }
                    break;
            }
        }
    };
}
