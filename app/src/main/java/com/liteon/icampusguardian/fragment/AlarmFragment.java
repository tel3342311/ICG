package com.liteon.icampusguardian.fragment;

import java.util.List;
import java.util.Set;

import com.liteon.icampusguardian.App;
import com.liteon.icampusguardian.R;
import com.liteon.icampusguardian.db.DBHelper;
import com.liteon.icampusguardian.util.AlarmItemAdapter;
import com.liteon.icampusguardian.util.AlarmManager;
import com.liteon.icampusguardian.util.AlarmItemAdapter.ViewHolder.IAlarmViewHolderClicks;
import com.liteon.icampusguardian.util.BluetoothAgent;
import com.liteon.icampusguardian.util.ConfirmDeleteDialog;
import com.liteon.icampusguardian.util.CustomDialog;
import com.liteon.icampusguardian.util.Def;
import com.liteon.icampusguardian.util.JSONResponse.Student;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

public class AlarmFragment extends Fragment  implements IAlarmViewHolderClicks {

	private RecyclerView mRecyclerView;
	private RecyclerView.Adapter mAdapter;
	private RecyclerView.LayoutManager mLayoutManager;
	private AppCompatButton mAddAlarm;
	private IAddAlarmClicks mAddAlarmClicks;
	private boolean isEditMode;
	private Toolbar mToolbar;
	private TextView mTitleView;
	private DBHelper mDbHelper;
	private List<Student> mStudents;
	private int mCurrnetStudentIdx;
	private ConfirmDeleteDialog mConfirmDeleteDialog;
	private PopupWindow mPopupWindow;
	private View mSyncView;
	//For bluetooth
	private BluetoothAgent mBTAgent;


	public AlarmFragment(IAddAlarmClicks listener) {
		mAddAlarmClicks = listener;
	}
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
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.alarm_menu, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}
	
	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		if (isEditMode) {
			menu.findItem(R.id.action_add).setVisible(true);
			menu.findItem(R.id.action_complete).setVisible(true);
			menu.findItem(R.id.action_edit).setVisible(false);
		} else {
			menu.findItem(R.id.action_add).setVisible(false);
			menu.findItem(R.id.action_complete).setVisible(false);
			menu.findItem(R.id.action_edit).setVisible(true);	
		}
	    super.onPrepareOptionsMenu(menu);
	}
	
	public boolean isEditMode() {
		return isEditMode;
	}
	
	public void exitEditMode() {
		isEditMode = false;
		getActivity().invalidateOptionsMenu();
		mAddAlarm.setVisibility(View.VISIBLE);
		((AlarmItemAdapter)mAdapter).setEditMode(false);
		mTitleView.setText(getString(R.string.alarm));
        showSyncWindow();
	}
	
	public void enterEditMode() {
		isEditMode = true;
		getActivity().invalidateOptionsMenu();
		mAddAlarm.setVisibility(View.GONE);
		((AlarmItemAdapter)mAdapter).setEditMode(true);
		//mToolbar.setTitle("編輯鬧鈴");
		mTitleView.setText(getString(R.string.alarm_edit_period));
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_edit:
			enterEditMode();
			break;
		case R.id.action_add:
			exitEditMode();
			if (AlarmManager.getDataSet().size() >= 4) {
				showAddAlarmErrorDialog();
				return super.onOptionsItemSelected(item);
			}
			mAddAlarmClicks.onAddAlarmClick();
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
		mRecyclerView = (RecyclerView) rootView.findViewById(R.id.alarm_view);
		mAddAlarm = (AppCompatButton) rootView.findViewById(R.id.add_alarm);
		mToolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
		mTitleView = (TextView) getActivity().findViewById(R.id.toolbar_title);
		mSyncView = rootView.findViewById(R.id.sync_view);
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
	}
	
	private OnClickListener mAddAlarmClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			if (AlarmManager.getDataSet().size() >= 4) {
				showAddAlarmErrorDialog();
				return;
			}
			mAddAlarmClicks.onAddAlarmClick();
			AlarmManager.setCurrentAction(AlarmManager.ACTION_ADDING);
		}
	};

	public static interface IAddAlarmClicks {
        public void onAddAlarmClick();
        public void onEditAlarm(int idx);
    }

	@Override
	public void onEditAlarm(int position) {
		if (isEditMode()) {
			mAddAlarmClicks.onEditAlarm(position);
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
		}
	};
	@Override
	public void onResume() {
		super.onResume();
		SharedPreferences sp = getActivity().getSharedPreferences(Def.SHARE_PREFERENCE, Context.MODE_PRIVATE);
		mCurrnetStudentIdx = sp.getInt(Def.SP_CURRENT_STUDENT, 0); 
		if (mStudents.size() > 0 && mCurrnetStudentIdx >= mStudents.size()) {
			mCurrnetStudentIdx = 0;
		}

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

		showSyncWindow();
		restoreAlarm();
	}
	
	@Override
	public void onPause() {
		super.onPause();
		saveAlarm();
		hideSyncWindow();
		if (mBTAgent != null) {
            mBTAgent.stop();
        }
	}
	
	private void hideSyncWindow() {
		if (mPopupWindow != null && mPopupWindow.isShowing()) {
			mPopupWindow.dismiss();
		}
	}
	private void showSyncWindow() {
		View contentview = mSyncView;
		final TextView title = (TextView) contentview.findViewById(R.id.title);
		AppCompatButton button = (AppCompatButton) contentview.findViewById(R.id.button_sync);
		button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				title.setText(R.string.alarm_syncing);

				//sync alarm data with watch
				String alarmString = AlarmManager.getAlarmEditContent();
				if (!TextUtils.isEmpty(alarmString)) {
                    mBTAgent.write(alarmString.getBytes());
                }
                //sync alarm state (enable/disable) with watch
                String alarmState = AlarmManager.getAlarmStateContent();
				if (!TextUtils.isEmpty(alarmState)) {
				    mBTAgent.write(alarmState.getBytes());
                }
				final Handler handler = new Handler();
				final Runnable hideSyncView = new Runnable() {
					
					@Override
					public void run() {
						mSyncView.setVisibility(View.GONE);
					}
				};
				Runnable runnable = new Runnable(){
					   @Override
					   public void run() {
						   title.setText(R.string.alarm_sync_complete);
						   handler.postDelayed(hideSyncView, 3000);
					} 
				};
				handler.postDelayed(runnable, 2000);
			}
		});
	}
	
	private void restoreAlarm() {
		AlarmManager.restoreAlarm();
	}
	
	private void saveAlarm() {
		AlarmManager.saveAlarm();
	}


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
                    Toast.makeText(App.getContext(), "Response : "
                            + readMessage, Toast.LENGTH_SHORT).show();
                    break;
                case Def.MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    String mConnectedDeviceName = msg.getData().getString(Def.DEVICE_NAME);
                    Toast.makeText(App.getContext(), "Connected to "
                            + mConnectedDeviceName, Toast.LENGTH_SHORT).show();

                    break;
                case Def.MESSAGE_TOAST:
//                    Toast.makeText(App.getContext(), msg.getData().getString(Def.TOAST),
//                            Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
}
