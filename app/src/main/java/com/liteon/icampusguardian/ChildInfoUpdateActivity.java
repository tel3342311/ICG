package com.liteon.icampusguardian;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.aigestudio.wheelpicker.WheelPicker;
import com.liteon.icampusguardian.db.DBHelper;
import com.liteon.icampusguardian.util.Def;
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

public class ChildInfoUpdateActivity extends AppCompatActivity implements IProfileItemClickListener{

	private boolean mIsEditMode;
	private EditText mName;
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
	private TYPE mType;
	private Toolbar mToolbar;
	private View mSyncView;
	private Student mStudent;
	private ImageView mBackBtn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_child_info);
		findViews();
		setupToolbar();
		setListener();
		initRecycleView();
        mDbHelper = DBHelper.getInstance(this);

    }
	
	private Student createChild() {
		Student student = new Student();
        student.setStudent_id((int)Calendar.getInstance().getTime().getTime());
		student.setDob("2000-01-01");
		student.setName("");
		student.setGender(getString(R.string.setup_kid_male));
		student.setHeight("0");
		student.setWeight("0");
		return student;
	}
	private void initRecycleView() {
		mRecyclerView.setHasFixedSize(true);
		mLayoutManager = new LinearLayoutManager(this);
		mRecyclerView.setLayoutManager(mLayoutManager);
		setupData();
		mAdapter = new ProfileItemAdapter(mDataSet, this);
		mRecyclerView.setAdapter(mAdapter);
	}
	
	private void setupData(){
		mDataSet = new ArrayList<>();
		mStudent = createChild();
        for (ProfileItem.TYPE type : ProfileItem.TYPE.values()) {
        	ProfileItem item = new ProfileItem();
        	item.setItemType(type);
        	switch(type) {
    		case BIRTHDAY:
    			item.setValue(mStudent.getDob());
    			break;
    		case GENDER:
    			if (TextUtils.equals(mStudent.getGender(), getString(R.string.setup_kid_male))) {
    				item.setValue(getString(R.string.setup_kid_male));
    			} else {
    				item.setValue(getString(R.string.setup_kid_female));
        		}
    			break;
    		case HEIGHT:
    			item.setValue(mStudent.getHeight());
    			break;
    		case WEIGHT:
    			item.setValue(mStudent.getWeight());
    			break;
    		default:
    			break;
    		}
        	mDataSet.add(item);
        }
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.one_confirm_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	public boolean onPrepareOptionsMenu(Menu menu) {
		
		if (mIsEditMode) {
			menu.findItem(R.id.action_confirm).setVisible(true);
		} else {
			menu.findItem(R.id.action_confirm).setVisible(false);
		}
		return true;
	};
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case R.id.action_confirm:
			updateAccount();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onResume() {
		super.onResume();
        mToolbar.setTitle("");
	}
	
	private void setupToolbar() {
		setSupportActionBar(mToolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(false);
		getSupportActionBar().setHomeButtonEnabled(false);
	}
	
	private void findViews() {
		mName = findViewById(R.id.login_name);
		mToolbar = findViewById(R.id.toolbar);
		mBackBtn = findViewById(R.id.cancel);
		mRecyclerView = findViewById(R.id.profile_view);
		mCardView = findViewById(R.id.option_wheel);
		three_wheel = findViewById(R.id.three_wheel);
		mWheel_left = three_wheel.findViewById(R.id.main_wheel_left);
		mWheel_center = three_wheel.findViewById(R.id.main_wheel_center);
		mWheel_right = three_wheel.findViewById(R.id.main_wheel_right);
		one_wheel = findViewById(R.id.one_wheel);
		mWheel_single = one_wheel.findViewById(R.id.main_wheel_left);
		mWheelTitle = one_wheel.findViewById(R.id.year_title);
	}
	
	private void setListener() {
		
		mName.addTextChangedListener(mTextWatcher);	
		mName.setOnFocusChangeListener(mOnFocusChangeListener);
		mWheel_left.setOnItemSelectedListener(mWheelClickListener);
		mWheel_center.setOnItemSelectedListener(mWheelClickListener);
		mWheel_right.setOnItemSelectedListener(mWheelClickListener);
		mWheel_single.setOnItemSelectedListener(mWheelClickListener);
		mBackBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				Intent intent = new Intent();
				intent.setClass(ChildInfoUpdateActivity.this, MainActivity.class);
				startActivity(intent);
			}
		});
	}

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.setClass(ChildInfoUpdateActivity.this, MainActivity.class);
        startActivity(intent);
	}

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
	
	private WheelPicker.OnItemSelectedListener mWheelClickListener = new WheelPicker.OnItemSelectedListener() {

		@Override
		public void onItemSelected(WheelPicker wheel, Object data, int position) {
			if (three_wheel.getVisibility() == View.VISIBLE) {
				String dob = mStudent.getDob();
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
				mStudent.setDob(sdf.format(calendar.getTime()));
				
			} else if (one_wheel.getVisibility() == View.VISIBLE) {
				if (mType == TYPE.GENDER) {
					if (position == 0) {
						mStudent.setGender("MALE");
					} else {
						mStudent.setGender("FEMALE");
					}
				} else if (mType == TYPE.HEIGHT) {
					mStudent.setHeight((String)data);
				} else if (mType == TYPE.WEIGHT) {
					mStudent.setWeight((String)data);
				}
			}
			updateData();
		}		
	};
	
	private void updateData(){
		Student student = mStudent;
        for (ProfileItem item : mDataSet) {
        	TYPE type = item.getItemType();
        	switch(type) {
    		case BIRTHDAY:
    			item.setValue(student.getDob());
    			break;
    		case GENDER:
    			if (TextUtils.equals(student.getGender(), getString(R.string.setup_kid_male))) {
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
	
	private OnFocusChangeListener mOnFocusChangeListener = new OnFocusChangeListener() {
		
		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			if (hasFocus) {
				enterEditMode();
				mCardView.setVisibility(View.INVISIBLE);
			} else {
				
				if (v != null) {  
				    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
				    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
				}
			}
		}
	};
	private TextWatcher mTextWatcher = new TextWatcher() {
		
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			
		}
		
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			
		}
		
		@Override
		public void afterTextChanged(Editable s) {

			if (validateInput()) {
				//mConfirm.setEnabled(true);
			} else {
				//mConfirm.setEnabled(false);
			}
		}
	};
	
	private boolean validateInput() {
		if (TextUtils.isEmpty(mName.getText())) {
			return false;
		}
		return true;
	}
	
	private void updateAccount() {
		String strName = mName.getText().toString();
		mStudent.setNickname(strName);
		new UpdateInfoTask().execute("");
	}

    class UpdateInfoTask extends AsyncTask<String, Void, String> {

		@Override
		protected void onPreExecute() {


		}

		@Override
        protected String doInBackground(String... args) {

            mDbHelper.insertChild(mDbHelper.getWritableDatabase(), mStudent);
            List<Student> studentList = mDbHelper.queryChildList(mDbHelper.getReadableDatabase());
            for (int idx = 0; idx < studentList.size();idx++) {
                if (TextUtils.equals(studentList.get(idx).getStudent_id(), mStudent.getStudent_id())) {
                    SharedPreferences sp = getSharedPreferences(Def.SHARE_PREFERENCE, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putInt(Def.SP_CURRENT_STUDENT, idx);
                    editor.commit();
                    break;
                }
            }

        	return null;
        }

        protected void onPostExecute(String token) {
        	Intent intent = new Intent();
        	intent.setClass(ChildInfoUpdateActivity.this, ChildPairingActivity.class);
        	startActivity(intent);
        	finish();
        }
    }
	
	public void exitEditMode() {
		mIsEditMode = false;
		invalidateOptionsMenu();
	}
	
	public void enterEditMode() {
		mIsEditMode = true;
		invalidateOptionsMenu();
	}

	@Override
	public void onProfileItemClick(TYPE type) {
		setupWheel(type);	
		mRecyclerView.requestFocus();
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
			List<Integer> days = new ArrayList<>();
			for (int i = 1; i < 32; i++) {
				days.add(i);
			}
			mWheel_right.setData(days);
			mWheel_right.setCyclic(false);
			String dob = mStudent.getDob();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			try {
				Date date = sdf.parse(dob);
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(date);
				mWheel_left.setSelectedItemPosition(years.indexOf(calendar.get(Calendar.YEAR)));
				mWheel_center.setSelectedItemPosition(months.indexOf(calendar.get(Calendar.MONTH) + 1));
				mWheel_right.setSelectedItemPosition(days.indexOf(calendar.get(Calendar.DAY_OF_MONTH)));
			} catch (ParseException e) {
				e.printStackTrace();
			}
			
			break;
		case GENDER:
			one_wheel.setVisibility(View.VISIBLE);
			mWheelTitle.setText(getString(R.string.setup_kid_gender));
			List<String> gender = new ArrayList<>();
			gender.add(getString(R.string.setup_kid_male));
			gender.add(getString(R.string.setup_kid_female));
			mWheel_single.setData(gender);
			mWheel_single.setCyclic(false);
			String gender_now = mStudent.getGender();
			if (TextUtils.equals(gender_now, getString(R.string.setup_kid_male))) {
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
			String height_now = mStudent.getHeight();
			mWheel_single.setSelectedItemPosition(height.indexOf(height_now));
			break;
		case WEIGHT:
			one_wheel.setVisibility(View.VISIBLE);
			mWheelTitle.setText(getString(R.string.setup_kid_kg));
			List<String> weight = new ArrayList<>();
			for (int i = 0; i < 100; i++) {
				weight.add(Integer.toString(i));
			}
			mWheel_single.setData(weight);
			String weight_now = mStudent.getWeight();
			mWheel_single.setSelectedItemPosition(weight.indexOf(weight_now));
			break;
		default:
			break;
		}
	}
}
