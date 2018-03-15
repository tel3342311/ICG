package com.liteon.icampusguardian.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aigestudio.wheelpicker.WheelPicker;
import com.liteon.icampusguardian.R;
import com.liteon.icampusguardian.db.DBHelper;
import com.liteon.icampusguardian.util.Def;
import com.liteon.icampusguardian.util.GuardianApiClient;
import com.liteon.icampusguardian.util.JSONResponse.Student;
import com.liteon.icampusguardian.util.ProfileItem;
import com.liteon.icampusguardian.util.ProfileItem.TYPE;
import com.liteon.icampusguardian.util.ProfileItemAdapter;
import com.liteon.icampusguardian.util.ProfileItemAdapter.ViewHolder.IProfileItemClickListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class SettingProfileFragment extends Fragment implements IProfileItemClickListener {


	private RecyclerView mRecyclerView;
	private RecyclerView.Adapter mAdapter;
	private RecyclerView.LayoutManager mLayoutManager;
	private CardView mCardView;
	private List<ProfileItem> mDataSet;
	private WheelPicker mWheel_left;
	private WheelPicker mWheel_center;
	private WheelPicker mWheel_right;
	private WheelPicker mWheel_single;
	private TextView mWheelTitle;
	private View three_wheel;
	private View one_wheel;
	private DBHelper mDbHelper;
	private List<Student> mStudents;
	private int mCurrentStudentIdx;
	private TYPE mType;
	private AppCompatActivity mActivity;
	private Toolbar mToolbar;
	private boolean isEditMode;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		setHasOptionsMenu(true);
		View rootView = inflater.inflate(R.layout.fragment_setting_profile, container, false);
		findView(rootView);
		setupListener();
		mDbHelper = DBHelper.getInstance(getActivity());
		//get child list
        SharedPreferences sp = getActivity().getSharedPreferences(Def.SHARE_PREFERENCE,Context.MODE_PRIVATE);
        mCurrentStudentIdx = sp.getInt(Def.SP_CURRENT_STUDENT, 0);
        mStudents = mDbHelper.queryChildList(mDbHelper.getReadableDatabase());
		initRecycleView();
		mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				getActivity().onBackPressed();

			}
		});
		return rootView;
	}
	
	private void findView(View rootView) {
		mRecyclerView = rootView.findViewById(R.id.profile_view);
		mCardView = rootView.findViewById(R.id.option_wheel);
		three_wheel = rootView.findViewById(R.id.three_wheel);
		mWheel_left = three_wheel.findViewById(R.id.main_wheel_left);
		mWheel_center = three_wheel.findViewById(R.id.main_wheel_center);
		mWheel_right = three_wheel.findViewById(R.id.main_wheel_right);
		one_wheel = rootView.findViewById(R.id.one_wheel);
		mWheel_single = one_wheel.findViewById(R.id.main_wheel_left);
		mWheelTitle = one_wheel.findViewById(R.id.year_title);
		mToolbar = getActivity().findViewById(R.id.toolbar);
	}
	
	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		if (context instanceof AppCompatActivity) {
			mActivity = (AppCompatActivity) context;
		}
	}
	
	private void setupListener() {
		mWheel_left.setOnItemSelectedListener(mWheelClickListener);
		mWheel_center.setOnItemSelectedListener(mWheelClickListener);
		mWheel_right.setOnItemSelectedListener(mWheelClickListener);
		mWheel_single.setOnItemSelectedListener(mWheelClickListener);
	}
	
	private WheelPicker.OnItemSelectedListener mWheelClickListener = new WheelPicker.OnItemSelectedListener() {

		@Override
		public void onItemSelected(WheelPicker wheel, Object data, int position) {
			if (three_wheel.getVisibility() == View.VISIBLE) {
				String dob = mStudents.get(mCurrentStudentIdx).getDob();
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				Date date;
				Calendar calendar = Calendar.getInstance();
				try {
					date = sdf.parse(dob);
					calendar.setTime(date);
				} catch (ParseException e) {
					e.printStackTrace();
				}
				if (R.id.main_wheel_left == wheel.getId()) {
					calendar.set(Calendar.YEAR, (Integer)data);
				} else if (R.id.main_wheel_center == wheel.getId()) {
					int days = calendar.get(Calendar.DAY_OF_MONTH);
					calendar.set(Calendar.DAY_OF_MONTH, 1);
					calendar.set(Calendar.MONTH, (Integer)data - 1);
					int max_days = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
					if (days > max_days) {
						calendar.set(Calendar.DAY_OF_MONTH, 1);
					} else {
						calendar.set(Calendar.DAY_OF_MONTH, days);
					}
				} else if (R.id.main_wheel_right == wheel.getId()) {
					calendar.set(Calendar.DATE, (Integer)data);
				}
				UpdateWheelForDate(calendar);
				mStudents.get(mCurrentStudentIdx).setDob(sdf.format(calendar.getTime()));
				
			} else if (one_wheel.getVisibility() == View.VISIBLE) {
				if (mType == TYPE.GENDER) {
					if (position == 0) {
						mStudents.get(mCurrentStudentIdx).setGender("MALE");
					} else {
						mStudents.get(mCurrentStudentIdx).setGender("FEMALE");
					}
				} else if (mType == TYPE.HEIGHT) {
					mStudents.get(mCurrentStudentIdx).setHeight((String)data);
				} else if (mType == TYPE.WEIGHT) {
					mStudents.get(mCurrentStudentIdx).setWeight((String)data);
				}
			}
			updateData();
		}		
	};
	
	public void UpdateWheelForDate(Calendar date) {

		int year = date.get(Calendar.YEAR);
		int month = date.get(Calendar.MONTH);
		int days = date.get(Calendar.DAY_OF_MONTH);
		
		int max_days = date.getActualMaximum(Calendar.DAY_OF_MONTH);
		List day_of_Month = new ArrayList<Integer>();
		for (int i = 1; i <= max_days; i ++) {
			day_of_Month.add(i);
		}
		mWheel_left.setSelectedItemPosition(mWheel_left.getData().indexOf(year));
		mWheel_center.setSelectedItemPosition(mWheel_center.getData().indexOf(month+1));
		mWheel_right.setData(day_of_Month);
		mWheel_right.setSelectedItemPosition(day_of_Month.indexOf(days));
	}
	private void updateData(){
		Student student = mStudents.get(mCurrentStudentIdx);
        for (ProfileItem item : mDataSet) {
        	TYPE type = item.getItemType();
        	switch(type) {
    		case BIRTHDAY:
    			item.setValue(student.getDob());
    			break;
    		case GENDER:
    			if (TextUtils.equals(student.getGender(), "MALE")) {
    				item.setValue(getString(R.string.setup_kid_male));
    			} else {
    				item.setValue(getString(R.string.setup_kid_female));
        		}
    			break;
    		case HEIGHT:
    			item.setValue(student.getHeight());
    			break;
    		case WEIGHT:
    			item.setValue(student.getWeight());
    			break;
    		default:
    			break;
    		}
        }
        mAdapter.notifyDataSetChanged();
	}
	private void setupData(){
		mDataSet = new ArrayList<>();
		Student student = mStudents.get(mCurrentStudentIdx);
        for (ProfileItem.TYPE type : ProfileItem.TYPE.values()) {
        	ProfileItem item = new ProfileItem();
        	item.setItemType(type);
        	switch(type) {
    		case BIRTHDAY:
    			item.setValue(student.getDob());
    			break;
    		case GENDER:
    			if (TextUtils.equals(student.getGender(), "MALE")) {
    				item.setValue(getString(R.string.setup_kid_male));
    			} else {
    				item.setValue(getString(R.string.setup_kid_female));
        		}
    			break;
    		case HEIGHT:
    			item.setValue(student.getHeight());
    			break;
    		case WEIGHT:
    			item.setValue(student.getWeight());
    			break;
    		default:
    			break;
    		}
        	mDataSet.add(item);
        }
	}
	private void initRecycleView() {
		mRecyclerView.setHasFixedSize(true);
		mLayoutManager = new LinearLayoutManager(getContext());
		mRecyclerView.setLayoutManager(mLayoutManager);
		setupData();
		mAdapter = new ProfileItemAdapter(mDataSet, this);
		mRecyclerView.setAdapter(mAdapter);
	}

	@Override
	public void onProfileItemClick(TYPE type) {
		setupWheel(type);
		enterEditMode();
	}
	
	private void setupWheel(TYPE type) {
		mType = type;
		mCardView.setVisibility(View.VISIBLE);
		three_wheel.setVisibility(View.INVISIBLE);
		one_wheel.setVisibility(View.INVISIBLE);
		switch(type) {
		case BIRTHDAY:

			
			three_wheel.setVisibility(View.VISIBLE);
			List<Integer> years = new ArrayList<>();
			for (int i = 1990; i < 2017 ; i++) {
				years.add(i);
			}
			mWheel_left.setData(years);
			mWheel_left.setCyclic(false);
			List<Integer> months = new ArrayList<>();
			for (int i = 1; i < 13 ; i++) {
				months.add(i);
			}
			mWheel_center.setData(months);
			mWheel_center.setCyclic(false);

			String dob = mStudents.get(mCurrentStudentIdx).getDob();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Date date = null;
			try {
				date = sdf.parse(dob);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			List<Integer> days = new ArrayList<>();
			for (int i = 1; i <= calendar.getActualMaximum(Calendar.DAY_OF_MONTH); i++) {
				days.add(i);
			}
			mWheel_right.setData(days);
			mWheel_right.setCyclic(false);

			mWheel_left.setSelectedItemPosition(years.indexOf(calendar.get(Calendar.YEAR)));
			mWheel_center.setSelectedItemPosition(months.indexOf(calendar.get(Calendar.MONTH) + 1));
			mWheel_right.setSelectedItemPosition(days.indexOf(calendar.get(Calendar.DAY_OF_MONTH)));

			
			break;
		case GENDER:
			one_wheel.setVisibility(View.VISIBLE);
			mWheelTitle.setText(getString(R.string.setup_kid_gender));
			List<String> gender = new ArrayList<>();
			gender.add(getString(R.string.setup_kid_male));
			gender.add(getString(R.string.setup_kid_female));
			mWheel_single.setData(gender);
			mWheel_single.setCyclic(false);
			String gender_now = mStudents.get(mCurrentStudentIdx).getGender();
			if (TextUtils.equals(gender_now, "MALE")) {
				mWheel_single.setSelectedItemPosition(0);
			} else {
				mWheel_single.setSelectedItemPosition(1);
			}
			
			break;
		case HEIGHT:
			one_wheel.setVisibility(View.VISIBLE);
			mWheelTitle.setText(getString(R.string.setup_kid_cm));
			List<String> height = new ArrayList<>();
			for (int i = 0; i < 200; i++) {
				height.add(Integer.toString(i));
			}
			mWheel_single.setData(height);
			String height_now = mStudents.get(mCurrentStudentIdx).getHeight();
			int idx_height = height.indexOf(height_now);
			mWheel_single.setSelectedItemPosition(idx_height);
			break;
		case WEIGHT:
			one_wheel.setVisibility(View.VISIBLE);
			mWheelTitle.setText(getString(R.string.setup_kid_kg));
			List<String> weight = new ArrayList<>();
			for (int i = 0; i < 100; i++) {
				weight.add(Integer.toString(i));
			}
			mWheel_single.setData(weight);
			String weight_now = mStudents.get(mCurrentStudentIdx).getWeight();
			int idx_weight = weight.indexOf(weight_now);
			mWheel_single.setSelectedItemPosition(idx_weight);
			break;
		default:
			break;
		}
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.setting_profile_menu, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}
	
	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		if (isEditMode) {
			menu.findItem(R.id.action_confirm).setVisible(true);
			mToolbar.setNavigationIcon(R.drawable.ic_clear_white_24dp);
		} else {
            menu.findItem(R.id.action_confirm).setVisible(false);
            mToolbar.setNavigationIcon(R.drawable.ic_navigate_before_white_24dp);
        }
	    super.onPrepareOptionsMenu(menu);
	}

	private void enterEditMode() {
		isEditMode = true;
		if (getActivity() != null) {
			getActivity().invalidateOptionsMenu();
		}
	}

	private void exitEditMode() {
		isEditMode = false;
		if (getActivity() != null) {
			getActivity().invalidateOptionsMenu();
		}
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_confirm:
			new UpdateTask().execute(null,null); 			
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void updateChildInfo() {
		
		GuardianApiClient apiClient = GuardianApiClient.getInstance(getContext());
		apiClient.updateChildData(mStudents.get(mCurrentStudentIdx));
		
	}
	
	class UpdateTask extends AsyncTask<Void, Void, String> {

        protected String doInBackground(Void... params) {
        	updateChildInfo();
        	DBHelper helper = DBHelper.getInstance(mActivity);
        	SQLiteDatabase db = helper.getWritableDatabase();
			helper.updateChildByStudentId(db, mStudents.get(mCurrentStudentIdx));
			return null;
        }

        protected void onPostExecute(String token) {
			exitEditMode();
        }
    }
	
	@Override
	public void onResume() {
		super.onResume();
		SharedPreferences sp = mActivity.getSharedPreferences(Def.SHARE_PREFERENCE, Context.MODE_PRIVATE);
		mCurrentStudentIdx = sp.getInt(Def.SP_CURRENT_STUDENT, 0);
		updateData();
	}
	
	public static int getDayOfMonth(Date aDate) {
	    Calendar cal = Calendar.getInstance();
	    cal.setTime(aDate);
	    return cal.get(Calendar.DAY_OF_MONTH);
	}
}
