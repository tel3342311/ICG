package com.liteon.icampusguardian.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.liteon.icampusguardian.App;
import com.liteon.icampusguardian.R;
import com.liteon.icampusguardian.db.DBHelper;
import com.liteon.icampusguardian.db.HealthDataTable;
import com.liteon.icampusguardian.service.DataSyncService;
import com.liteon.icampusguardian.util.Def;
import com.liteon.icampusguardian.util.HealthyItem;
import com.liteon.icampusguardian.util.HealthyItemAdapter;
import com.liteon.icampusguardian.util.HealthyItemAdapter.ViewHolder.IHealthViewHolderClicks;
import com.liteon.icampusguardian.util.JSONResponse;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class HealthFragment extends Fragment {

	private static ArrayList<HealthyItem> myDataset = new ArrayList<>();
	private RecyclerView mRecyclerView;
	private RecyclerView.Adapter mAdapter;
	private RecyclerView.LayoutManager mLayoutManager;
	private WeakReference<IHealthViewHolderClicks> mOnItemClickListener;
	private View mRootView;
	private View mSyncView;
    private DBHelper mDbHelper;
    private List<JSONResponse.Student> mStudents;
    private int mCurrentStudentIdx;
	private TextView mTitleView;
	private static boolean isFirstLaunch = true;
    private LocalBroadcastManager mLocalBroadcastManager;

    public HealthFragment() {}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mRootView = inflater.inflate(R.layout.fragment_healthy, container, false);
		findView(mRootView);
        mDbHelper = DBHelper.getInstance(getActivity());
        //get child list
        mStudents = mDbHelper.queryChildList(mDbHelper.getReadableDatabase());
        mLocalBroadcastManager = LocalBroadcastManager.getInstance(App.getContext());
		initRecycleView();
		return mRootView;
	}

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		mOnItemClickListener = new WeakReference<>((IHealthViewHolderClicks)getParentFragment());
	}

	private void initRecycleView() {

		mRecyclerView.setHasFixedSize(true);
		mLayoutManager = new LinearLayoutManager(getContext());
		mRecyclerView.setLayoutManager(mLayoutManager);
		testData();
		mAdapter = new HealthyItemAdapter(myDataset, mOnItemClickListener.get());
		mRecyclerView.setAdapter(mAdapter);
	}

	private void testData() {
		if (myDataset.size() == 0) {
			for (HealthyItem.TYPE type : HealthyItem.TYPE.values()) {
				HealthyItem item = new HealthyItem();
				item.setItemType(type);
				//item.setValue(getTestValue(type, mCurrentStudentIdx));
				myDataset.add(item);
			}
		}
	}

	private int getTestValue(HealthyItem.TYPE type, int idx) {
        if (idx == 0) {
            switch(type){

                case ACTIVITY:
                    return 85;
                case CALORIES_BURNED:
                    return 1060;
                case TOTAL_STEPS:
                    return 7600;
                case WALKING_TIME:
                    return 25;
                case RUNNING_TIME:
                    return 40;
                case CYCLING_TIME:
                    return 15;
                case HEART_RATE:
                    return 81;
                case SLEEP_TIME:
                    return 560;
            }
        } else {
            switch(type){

                case ACTIVITY:
                    return 76;
                case CALORIES_BURNED:
                    return 830;
                case TOTAL_STEPS:
                    return 6400;
                case WALKING_TIME:
                    return 23;
                case RUNNING_TIME:
                    return 25;
                case CYCLING_TIME:
                    return 15;
                case HEART_RATE:
                    return 85;
                case SLEEP_TIME:
                    return 530;
            }
        }
        return 0;
    }

	private void findView(View rootView) {
		mRecyclerView = rootView.findViewById(R.id.healthy_event_view);
		mSyncView = rootView.findViewById(R.id.sync_view);
		mTitleView = getActivity().findViewById(R.id.toolbar_title);

	}
	
	@Override
	public void onResume() {
		super.onResume();
		showSyncWindow();
        SharedPreferences sp = getActivity().getSharedPreferences(Def.SHARE_PREFERENCE, Context.MODE_PRIVATE);

        mCurrentStudentIdx = sp.getInt(Def.SP_CURRENT_STUDENT, 0);
        mStudents = mDbHelper.queryChildList(mDbHelper.getReadableDatabase());
        getHealthyDataFromDB();
        if (isFirstLaunch) {
            new SyncHealthyData().execute();
            isFirstLaunch = false;
        }
        mAdapter.notifyDataSetChanged();

        IntentFilter filter = new IntentFilter();
        filter.addAction(Def.ACTION_GET_HEALTHY_DATA);
        mLocalBroadcastManager.registerReceiver(mReceiver, filter);
    }

    @Override
    public void onPause() {
        super.onPause();
        mLocalBroadcastManager.unregisterReceiver(mReceiver);
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (TextUtils.equals(Def.ACTION_GET_HEALTHY_DATA, intent.getAction())) {
                getHealthyDataFromDB();
            }
        }
    };

    private JSONResponse.HealthyData getHealthyData(HealthyItem.TYPE type) {
        JSONResponse.HealthyData data = null;
        switch(type) {

            case ACTIVITY:
                data = mDbHelper.getLastHealthyData(mDbHelper.getReadableDatabase(), mStudents.get(mCurrentStudentIdx).getStudent_id(), Integer.toString(HealthDataTable.HealthDataEntry.SITUATION_FITNESS));
                break;
            case CALORIES_BURNED:
                data = mDbHelper.getLastHealthyData(mDbHelper.getReadableDatabase(), mStudents.get(mCurrentStudentIdx).getStudent_id(), Integer.toString(HealthDataTable.HealthDataEntry.SITUATION_CALOS));
                break;
            case TOTAL_STEPS:
                data = mDbHelper.getLastHealthyData(mDbHelper.getReadableDatabase(), mStudents.get(mCurrentStudentIdx).getStudent_id(), Integer.toString(HealthDataTable.HealthDataEntry.SITUATION_STEPS));
                break;
            case WALKING_TIME:
                data = mDbHelper.getLastHealthyData(mDbHelper.getReadableDatabase(), mStudents.get(mCurrentStudentIdx).getStudent_id(), Integer.toString(HealthDataTable.HealthDataEntry.SITUATION_WALKING));
                break;
            case RUNNING_TIME:
                data = mDbHelper.getLastHealthyData(mDbHelper.getReadableDatabase(), mStudents.get(mCurrentStudentIdx).getStudent_id(), Integer.toString(HealthDataTable.HealthDataEntry.SITUATION_RUNNING));
                break;
            case CYCLING_TIME:
                data = mDbHelper.getLastHealthyData(mDbHelper.getReadableDatabase(), mStudents.get(mCurrentStudentIdx).getStudent_id(), Integer.toString(HealthDataTable.HealthDataEntry.SITUATION_CYCLING));
                break;
            case HEART_RATE:
                data = mDbHelper.getLastHealthyData(mDbHelper.getReadableDatabase(), mStudents.get(mCurrentStudentIdx).getStudent_id(), Integer.toString(HealthDataTable.HealthDataEntry.SITUATION_HEART));
                break;
            case SLEEP_TIME:
                data = mDbHelper.getLastHealthyData(mDbHelper.getReadableDatabase(), mStudents.get(mCurrentStudentIdx).getStudent_id(), Integer.toString(HealthDataTable.HealthDataEntry.SITUATION_SLEEP));
                break;
        }
        return data;
    }
	private void showSyncWindow() {
		AppCompatButton button = mSyncView.findViewById(R.id.button_sync);
		button.setOnClickListener(v -> new SyncHealthyData().execute());
	}

	class SyncHealthyData extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            TextView title = mSyncView.findViewById(R.id.title);
            title.setText(getString(R.string.alarm_syncing));
            mTitleView.setText(R.string.healthy_today_reocrd);
        }

        @Override
        protected Void doInBackground(Void... voids) {

            Intent startIntent = new Intent(App.getContext(), DataSyncService.class);
            startIntent.setAction(Def.ACTION_GET_HEALTHY_DATA);
            startIntent.putExtra(Def.KEY_STUDENT_ID, mStudents.get(mCurrentStudentIdx).getStudent_id());
            if (getActivity() != null && isAdded()) {
                getActivity().startService(startIntent);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (HealthFragment.this.isAdded()) {
                TextView title = mSyncView.findViewById(R.id.title);
                SimpleDateFormat sdf = new SimpleDateFormat(getString(R.string.healthy_update_time));
                final String currentDateandTime = sdf.format(Calendar.getInstance().getTime());
                final Handler handler = new Handler();
                final Runnable hideSyncView = () -> mSyncView.setVisibility(View.GONE);
                Runnable runnable = () -> {
                    if (!isDetached()&& !isHidden()) {
                        title.setText(currentDateandTime);
                        handler.postDelayed(hideSyncView, 3000);
                        mAdapter.notifyDataSetChanged();
                    }
                };
                handler.postDelayed(runnable, 2000);
            }
        }
    }

    private void getHealthyDataFromDB() {
        myDataset.clear();
        for (HealthyItem.TYPE type : HealthyItem.TYPE.values()) {
            HealthyItem item = new HealthyItem();
            item.setItemType(type);
            JSONResponse.HealthyData data = getHealthyData(type);
            if (data.getSituation() == HealthDataTable.HealthDataEntry.SITUATION_WALKING) {
                item.setItemType(HealthyItem.TYPE.WALKING_TIME);
                item.setValue(data.getDuration() / 60);
            } else if (data.getSituation() == HealthDataTable.HealthDataEntry.SITUATION_RUNNING) {
                item.setItemType(HealthyItem.TYPE.RUNNING_TIME);
                item.setValue(data.getDuration() / 60);
            } else if (data.getSituation() == HealthDataTable.HealthDataEntry.SITUATION_CYCLING) {
                item.setItemType(HealthyItem.TYPE.CYCLING_TIME);
                item.setValue(data.getDuration() / 60);
            } else if (data.getSituation() == HealthDataTable.HealthDataEntry.SITUATION_SLEEP) {
                item.setItemType(HealthyItem.TYPE.SLEEP_TIME);
                item.setValue(data.getValue() / 60);
            } else if (data.getSituation() == HealthDataTable.HealthDataEntry.SITUATION_FITNESS) {
                item.setItemType(HealthyItem.TYPE.ACTIVITY);
                item.setValue(data.getValue());
            } else if (data.getSituation() == HealthDataTable.HealthDataEntry.SITUATION_HEART) {
                item.setItemType(HealthyItem.TYPE.HEART_RATE);
                item.setValue(data.getValue());
            } else if (data.getSituation() == HealthDataTable.HealthDataEntry.SITUATION_STEPS) {
                item.setItemType(HealthyItem.TYPE.TOTAL_STEPS);
                item.setValue(data.getValue());
            } else if (data.getSituation() == HealthDataTable.HealthDataEntry.SITUATION_CALOS) {
                item.setItemType(HealthyItem.TYPE.CALORIES_BURNED);
                item.setValue(data.getValue());
            }
            myDataset.add(item);
        }
        mAdapter.notifyDataSetChanged();
    }
}
