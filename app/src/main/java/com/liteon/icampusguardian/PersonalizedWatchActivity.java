package com.liteon.icampusguardian;

import java.util.List;

import com.liteon.icampusguardian.db.DBHelper;
import com.liteon.icampusguardian.util.ConfirmDeleteDialog;
import com.liteon.icampusguardian.util.CustomDialog;
import com.liteon.icampusguardian.util.Def;
import com.liteon.icampusguardian.util.JSONResponse.Student;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatRadioButton;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class PersonalizedWatchActivity extends AppCompatActivity {

	private Toolbar mToolbar;
	private TextView mTextViewUserTerm;
	private ProgressBar mProgressBar;
	private Handler mHandlerTime;
	private int mProgressStep;
	private ImageView mWatchSurface;
	private TextView mTitleUpdating;
	private DBHelper mDbHelper;
	private List<Student> mStudents;
	private int mCurrnetStudentIdx;
	private final static int REQUEST_WATCH_SURFACE = 1;
    private ConfirmDeleteDialog mBLEFailConfirmDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_personalized_watch);
		findViews();
		setListener();
		setupToolbar();
		mProgressBar.setVisibility(View.INVISIBLE);
		mTitleUpdating.setVisibility(View.INVISIBLE);
	}
	
	private void findViews() {
		mToolbar = (Toolbar) findViewById(R.id.toolbar);
		mProgressBar = (ProgressBar) findViewById(R.id.loading_progress);
		mWatchSurface = (ImageView) findViewById(R.id.watch_surface);
		mTitleUpdating = (TextView) findViewById(R.id.watch_surface_updating_text);
	}
	
	private void setListener() {
		mWatchSurface.setOnClickListener(mOnChangeSurfaceListener);
	}
	
	private void setupToolbar() {
		setSupportActionBar(mToolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);
		mToolbar.setNavigationIcon(R.drawable.ic_navigate_before_white_24dp);
		mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				Intent intent = new Intent();
            	intent.setClass(PersonalizedWatchActivity.this, MainActivity.class);
            	intent.putExtra(Def.EXTRA_GOTO_MAIN_SETTING, true);
            	startActivity(intent);
            	finish();
			}
		});
	}
	
	private View.OnClickListener mOnChangeSurfaceListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			Intent intent = new Intent();
			intent.setClass(PersonalizedWatchActivity.this, ChoosePhotoActivity.class);
			intent.putExtra(Def.EXTRA_CHOOSE_PHOTO_TYPE, Def.EXTRA_CHOOSE_WATCH_ICON);
			startActivityForResult(intent, REQUEST_WATCH_SURFACE);
		}
	};
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_WATCH_SURFACE) {
			if (resultCode == RESULT_OK) {
				mProgressBar.setVisibility(View.VISIBLE);
				mTitleUpdating.setText(R.string.syncing_photo_to_watch);
				mTitleUpdating.setVisibility(View.VISIBLE);
				new ConnectBleTask().execute("");
			}
		}
	};
	private void setupWatchSurface() {
		Bitmap bitmap = null;
		if (mStudents.size() > 0) {
			// read child image file
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inPreferredConfig = Bitmap.Config.ARGB_8888;
			bitmap = BitmapFactory
					.decodeFile(
							Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
									.getAbsolutePath() + "/" + mStudents.get(mCurrnetStudentIdx).getStudent_id() + "_watch.jpg",
							options);
		}
		if (bitmap != null) {
			mWatchSurface.setImageBitmap(bitmap);
		} else {
			mWatchSurface.setImageDrawable(null);
		}
	}
	@Override
	protected void onResume() {
		super.onResume();
		mDbHelper = DBHelper.getInstance(this);
		//get child list
		mStudents = mDbHelper.queryChildList(mDbHelper.getReadableDatabase());
		SharedPreferences sp = getSharedPreferences(Def.SHARE_PREFERENCE, Context.MODE_PRIVATE);
		mCurrnetStudentIdx = sp.getInt(Def.SP_CURRENT_STUDENT, 0); 
		setupWatchSurface();
		mToolbar.setTitle(R.string.watch_surface);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
	}
	
	class ConnectBleTask extends AsyncTask<String, Void, Boolean> {

		@Override
		protected void onPreExecute() {
			mProgressBar.setVisibility(View.VISIBLE);
			mTitleUpdating.setText(R.string.syncing_photo_to_watch);
			mTitleUpdating.setVisibility(View.VISIBLE);
		}
		
        protected Boolean doInBackground(String... args) {
        	
        	//TODO add ble connection function
        	try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
        	return Boolean.FALSE;
        }

        protected void onPostExecute(Boolean success) {
        	mProgressBar.setVisibility(View.INVISIBLE);
        	if (success == true) {
        		mTitleUpdating.setText(R.string.syncing_photo_to_watch_complete);
        	} else {
        		mTitleUpdating.setVisibility(View.INVISIBLE);
        		mBLEFailConfirmDialog = new ConfirmDeleteDialog();
        		mBLEFailConfirmDialog.setOnConfirmEventListener(mOnBLEFailConfirmClickListener);
        		mBLEFailConfirmDialog.setmOnCancelListener(mOnBLEFailCancelClickListener);
        		mBLEFailConfirmDialog.setmTitleText(getString(R.string.syncing_photo_to_watch_failed));
        		mBLEFailConfirmDialog.setmBtnConfirmText(getString(R.string.syncing_photo_to_watch_synced));
        		mBLEFailConfirmDialog.setmBtnCancelText(getString(R.string.syncing_photo_to_watch_cancel));
        		mBLEFailConfirmDialog.show(getSupportFragmentManager(), "dialog_fragment");
        	}
        }
    }
	
	private View.OnClickListener mOnBLEFailConfirmClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			mBLEFailConfirmDialog.dismiss();
			new ConnectBleTask().execute("");
			
		}
	};
	
	private View.OnClickListener mOnBLEFailCancelClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			mBLEFailConfirmDialog.dismiss();
			Intent intent = new Intent();
        	intent.setClass(PersonalizedWatchActivity.this, MainActivity.class);
        	intent.putExtra(Def.EXTRA_GOTO_MAIN_SETTING, true);
        	startActivity(intent);
        	finish();
		}
	};
}
