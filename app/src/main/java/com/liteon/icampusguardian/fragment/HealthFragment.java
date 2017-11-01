package com.liteon.icampusguardian.fragment;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.liteon.icampusguardian.R;
import com.liteon.icampusguardian.db.DBHelper;
import com.liteon.icampusguardian.util.Def;
import com.liteon.icampusguardian.util.HealthyItem;
import com.liteon.icampusguardian.util.HealthyItemAdapter;
import com.liteon.icampusguardian.util.HealthyItemAdapter.ViewHolder.IHealthViewHolderClicks;
import com.liteon.icampusguardian.util.JSONResponse;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout.LayoutParams;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

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
    private int mCurrnetStudentIdx;

	public HealthFragment() {}
//	public HealthFragment(IHealthViewHolderClicks listener) {
//		super();
//		mOnItemClickListener = new WeakReference<IHealthViewHolderClicks>(listener);
//	}
	
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
				item.setValue(getTestValue(type, mCurrnetStudentIdx));
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
		mRecyclerView = (RecyclerView) rootView.findViewById(R.id.healthy_event_view);
		mSyncView = rootView.findViewById(R.id.sync_view);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		showSyncWindow();
        SharedPreferences sp = getActivity().getSharedPreferences(Def.SHARE_PREFERENCE, Context.MODE_PRIVATE);

        mCurrnetStudentIdx = sp.getInt(Def.SP_CURRENT_STUDENT, 0);
        myDataset.clear();
        testData();
        mAdapter.notifyDataSetChanged();
    }
	
	private void showSyncWindow() {
		
		View contentview = mSyncView;
		final TextView title = (TextView) contentview.findViewById(R.id.title);
		AppCompatButton button = (AppCompatButton) contentview.findViewById(R.id.button_sync);
		button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				title.setText(getString(R.string.alarm_syncing));
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
						   SimpleDateFormat sdf = new SimpleDateFormat(getString(R.string.healthy_update_time));
						   String currentDateandTime = sdf.format(Calendar.getInstance().getTime());
						   title.setText(currentDateandTime);
						   handler.postDelayed(hideSyncView, 3000);
					} 
				};
				handler.postDelayed(runnable, 2000);
			}
		});
	}
}
