package com.liteon.icampusguardian.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.liteon.icampusguardian.App;
import com.liteon.icampusguardian.R;
import com.liteon.icampusguardian.db.DBHelper;
import com.liteon.icampusguardian.util.Def;
import com.liteon.icampusguardian.util.GuardianApiClient;
import com.liteon.icampusguardian.util.HealthyItem;
import com.liteon.icampusguardian.util.HealthyItemAdapter;
import com.liteon.icampusguardian.util.HealthyItemAdapter.ViewHolder.IHealthViewHolderClicks;
import com.liteon.icampusguardian.util.JSONResponse;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
	public HealthFragment() {}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mRootView = inflater.inflate(R.layout.fragment_healthy, container, false);
		findView(mRootView);
        mDbHelper = DBHelper.getInstance(getActivity());
        //get child list
        mStudents = mDbHelper.queryChildList(mDbHelper.getReadableDatabase());

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
				item.setValue(getTestValue(type, mCurrentStudentIdx));
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
        myDataset.clear();
        testData();
        mAdapter.notifyDataSetChanged();
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
        }

        @Override
        protected Void doInBackground(Void... voids) {

            Date end = Calendar.getInstance().getTime();
            Calendar c = Calendar.getInstance();
            c.setTime(end);
            c.add(Calendar.DAY_OF_YEAR, -7);
            Date start = c.getTime();
            SimpleDateFormat sdfQurey = new SimpleDateFormat("yyyy-MM-dd");
            String startDate = sdfQurey.format(start);
            String endDate = sdfQurey.format(end);

            GuardianApiClient apiClient = GuardianApiClient.getInstance(App.getContext());
            JSONResponse jsonResponse = apiClient.getHealthyData(mStudents.get(mCurrentStudentIdx), startDate, endDate);
            if (jsonResponse != null) {
                if (jsonResponse.getReturn() != null && jsonResponse.getReturn().getResults() != null) {
                    JSONResponse.HealthyData[] fitness = jsonResponse.getReturn().getResults().getFitness();
                    JSONResponse.HealthyData[] activity = jsonResponse.getReturn().getResults().getActivity();
                    JSONResponse.HealthyData[] calories = jsonResponse.getReturn().getResults().getCalories();
                    JSONResponse.HealthyData[] heartrate = jsonResponse.getReturn().getResults().getHeartrate();
                    JSONResponse.HealthyData[] sleep = jsonResponse.getReturn().getResults().getSleep();
                    JSONResponse.HealthyData[] steps = jsonResponse.getReturn().getResults().getSteps();
                    for (HealthyItem item : myDataset) {
                        switch (item.getItemType()) {

                            case ACTIVITY:
                                if (fitness.length > 0) {
                                    item.setValue(fitness[0].getValue());
                                } else {
                                    item.setValue(0);
                                }
                                break;
                            case CALORIES_BURNED:
                                if (calories.length > 0) {
                                    item.setValue(fitness[0].getValue());
                                } else {
                                    item.setValue(0);
                                }
                                break;
                            case TOTAL_STEPS:
                                if (steps.length > 0) {
                                    item.setValue(fitness[0].getValue());
                                } else {
                                    item.setValue(0);
                                }
                                break;
                            case WALKING_TIME:
                            case RUNNING_TIME:
                            case CYCLING_TIME:
                                if (activity.length > 0) {
                                    for (int i = 0; i < activity.length; i++) {

                                        if (activity[i].getSituation() == 2 && item.getItemType() == HealthyItem.TYPE.WALKING_TIME) {
                                            item.setValue(activity[i].getValue());
                                        } else if (activity[i].getSituation() == 3 && item.getItemType() == HealthyItem.TYPE.RUNNING_TIME) {
                                            item.setValue(activity[i].getValue());
                                        } else if (activity[i].getSituation() == 4 && item.getItemType() == HealthyItem.TYPE.CYCLING_TIME) {
                                            item.setValue(activity[i].getValue());
                                        }
                                    }
                                } else {
                                    item.setValue(0);
                                }
                                break;
                            case HEART_RATE:
                                if (heartrate.length > 0) {
                                    item.setValue(heartrate[0].getValue());
                                } else {
                                    item.setValue(0);
                                }
                                break;
                            case SLEEP_TIME:
                                if (sleep.length > 0) {
                                    item.setValue(sleep[0].getValue());
                                } else {
                                    item.setValue(0);
                                }
                                break;
                        }
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            TextView title = mSyncView.findViewById(R.id.title);
            SimpleDateFormat sdf = new SimpleDateFormat(getString(R.string.healthy_update_time));
            final String currentDateandTime = sdf.format(Calendar.getInstance().getTime());
            final Handler handler= new Handler();
            final Runnable hideSyncView = () -> mSyncView.setVisibility(View.GONE);
            Runnable runnable = () -> {
                if (!isDetached()) {
                    title.setText(currentDateandTime);
                    handler.postDelayed(hideSyncView, 3000);
                    mTitleView.setText(R.string.healthy_today_reocrd);
                    mAdapter.notifyDataSetChanged();
                }
            };
            handler.postDelayed(runnable, 2000);
        }
    }
}
