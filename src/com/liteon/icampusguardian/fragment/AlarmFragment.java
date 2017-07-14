package com.liteon.icampusguardian.fragment;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.liteon.icampusguardian.R;
import com.liteon.icampusguardian.db.DBHelper;
import com.liteon.icampusguardian.util.AlarmItem;
import com.liteon.icampusguardian.util.AlarmItemAdapter;
import com.liteon.icampusguardian.util.AlarmItemAdapter.ViewHolder.IAlarmViewHolderClicks;
import com.liteon.icampusguardian.util.ConfirmDeleteDialog;
import com.liteon.icampusguardian.util.Def;
import com.liteon.icampusguardian.util.JSONResponse.Student;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout.LayoutParams;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

public class AlarmFragment extends Fragment  implements IAlarmViewHolderClicks {

	private static ArrayList<AlarmItem> myDataset = new ArrayList<>();
	private RecyclerView mRecyclerView;
	private RecyclerView.Adapter mAdapter;
	private RecyclerView.LayoutManager mLayoutManager;
	private AppCompatButton mAddAlarm;
	private IAddAlarmClicks mAddAlarmClicks;
	private boolean isEditMode;
	private Toolbar mToolbar;
	private Map<String, List<AlarmItem>> mAlarmMap;
	private DBHelper mDbHelper;
	private List<Student> mStudents;
	private int mCurrnetStudentIdx;
	private ConfirmDeleteDialog mConfirmDeleteDialog;
	private PopupWindow mPopupWindow;
	private View mSyncView;
	
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
		mToolbar.setTitle("鬧鈴");
	}
	
	public void enterEditMode() {
		isEditMode = true;
		getActivity().invalidateOptionsMenu();
		mAddAlarm.setVisibility(View.GONE);
		((AlarmItemAdapter)mAdapter).setEditMode(true);
		mToolbar.setTitle("編輯鬧鈴");
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_edit:
			enterEditMode();
			break;
		case R.id.action_add:
			exitEditMode();
			mAddAlarmClicks.onAddAlarmClick();
			break;
		case R.id.action_complete:
			exitEditMode();
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void findView(View rootView) {
		mRecyclerView = (RecyclerView) rootView.findViewById(R.id.alarm_view);
		mAddAlarm = (AppCompatButton) rootView.findViewById(R.id.add_alarm);
		mToolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
		mSyncView = rootView.findViewById(R.id.sync_view);
	}
	
	public void initRecycleView() {
		mRecyclerView.setHasFixedSize(true);
		mLayoutManager = new LinearLayoutManager(getContext());
		mRecyclerView.setLayoutManager(mLayoutManager);
		//testData();
		mAdapter = new AlarmItemAdapter(myDataset, this);
		mRecyclerView.setAdapter(mAdapter);
	}
	
	private void setupListener() {
		mAddAlarm.setOnClickListener(mAddAlarmClickListener);
	}
	
	private OnClickListener mAddAlarmClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			mAddAlarmClicks.onAddAlarmClick();
		}
	};
	
	public void testData() {
		AlarmItem item = new AlarmItem();
		item.setTitle("上學");
		item.setDate("06:10");
		item.setPeriod("週一");
		item.setEnabled(true);
		myDataset.add(item);
	}
	
	public static interface IAddAlarmClicks {
        public void onAddAlarmClick();
        public void onEditAlarm(int idx);
    }

	@Override
	public void onEditAlarm(int position) {
		mAddAlarmClicks.onEditAlarm(position);
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
		myDataset.get(position).Enabled = enable;		
		
	}
	
	private View.OnClickListener mOnConfirmDelete = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			myDataset.remove(mDeleteIdx);
			mAdapter.notifyDataSetChanged();
			mConfirmDeleteDialog.dismiss();
			if (myDataset.size() == 0) {
				exitEditMode();
			}
		}
	};
	@Override
	public void onResume() {
		super.onResume();
		SharedPreferences sp = getActivity().getSharedPreferences(Def.SHARE_PREFERENCE, Context.MODE_PRIVATE);
		mCurrnetStudentIdx = sp.getInt(Def.SP_CURRENT_STUDENT, 0); 
		showSyncWindow();
		restoreAlarm();
	}
	
	@Override
	public void onPause() {
		super.onPause();
		saveAlarm();
		hideSyncWindow();
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
				title.setText("同步中");
				final Handler handler= new Handler();
				final Runnable hideSyncView = new Runnable() {
					
					@Override
					public void run() {
						mSyncView.setVisibility(View.GONE);
					}
				};
				Runnable runnable = new Runnable(){
					   @Override
					   public void run() {
						   title.setText("同步完成");
						   handler.postDelayed(hideSyncView, 3000);
					} 
				};
				handler.postDelayed(runnable, 2000);
			}
		});
	}
	
	private void restoreAlarm() {
		SharedPreferences sp = getActivity().getSharedPreferences(Def.SHARE_PREFERENCE, Context.MODE_PRIVATE);
		String alarmMap = sp.getString(Def.SP_ALARM_MAP, "");
		Type typeOfHashMap = new TypeToken<Map<String, List<AlarmItem>>>() { }.getType();
        Gson gson = new GsonBuilder().create();
        mAlarmMap = gson.fromJson(alarmMap, typeOfHashMap);
		if (TextUtils.isEmpty(alarmMap)) {
			mAlarmMap = new HashMap<String, List<AlarmItem>>();
			for (Student student : mStudents) {
				String uuid = student.getUuid();
				mAlarmMap.put(uuid, new ArrayList<AlarmItem>());
			}
		}
		myDataset.clear();
		myDataset.addAll((ArrayList) mAlarmMap.get(mStudents.get(mCurrnetStudentIdx).getUuid()));
		mAdapter.notifyDataSetChanged();
	}
	
	private void saveAlarm() {
		mAlarmMap.put(mStudents.get(mCurrnetStudentIdx).getUuid(), myDataset);
		Gson gson = new Gson();
		String input = gson.toJson(mAlarmMap);
		SharedPreferences sp = getActivity().getSharedPreferences(Def.SHARE_PREFERENCE, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putString(Def.SP_ALARM_MAP, input);
		editor.commit();
	}
}
