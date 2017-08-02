package com.liteon.icampusguardian.fragment;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import com.liteon.icampusguardian.ChoosePhotoActivity;
import com.liteon.icampusguardian.R;
import com.liteon.icampusguardian.db.DBHelper;
import com.liteon.icampusguardian.util.CircularImageView;
import com.liteon.icampusguardian.util.Def;
import com.liteon.icampusguardian.util.GuardianApiClient;
import com.liteon.icampusguardian.util.JSONResponse.Student;
import com.liteon.icampusguardian.util.SettingItem;
import com.liteon.icampusguardian.util.SettingItemAdapter;
import com.liteon.icampusguardian.util.SettingItemAdapter.ViewHolder.ISettingItemClickListener;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

public class SettingFragment extends Fragment {
	
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
	
	public SettingFragment(ISettingItemClickListener clicks) {
		mClicks = new WeakReference<ISettingItemClickListener>(clicks);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		setHasOptionsMenu(true);
		View rootView = inflater.inflate(R.layout.fragment_setting, container, false);
		findView(rootView);
		setOnClickListener();
		initRecycleView();
		mDbHelper = DBHelper.getInstance(getActivity());
		//get child list
		mStudents = mDbHelper.queryChildList(mDbHelper.getReadableDatabase());
		((SettingItemAdapter)mAdapter).setChildData(mStudents.get(mCurrnetStudentIdx));
		mAdapter.notifyDataSetChanged();
		return rootView;
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

        protected String doInBackground(Void... params) {
        	updateChildInfo();
        	DBHelper helper = DBHelper.getInstance(getActivity());
        	SQLiteDatabase db = helper.getWritableDatabase();
        	helper.insertChildList(db, mStudents);
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
			mChildName.setText(mStudents.get(mCurrnetStudentIdx).getNickname());
			// read child image file
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inPreferredConfig = Bitmap.Config.ARGB_8888;
			bitmap = BitmapFactory
					.decodeFile(
							Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
									.getAbsolutePath() + "/" + mStudents.get(mCurrnetStudentIdx).getUuid() + ".jpg",
							options);
		}
		if (bitmap != null) {
			mChildIcon.setImageBitmap(bitmap);
		} else {
			mChildIcon.setImageDrawable(getResources().getDrawable(R.drawable.setup_img_picture, null));
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
	}
}
