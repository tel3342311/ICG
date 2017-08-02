package com.liteon.icampusguardian;

import java.util.List;

import com.facebook.share.model.AppGroupCreationContent.AppGroupPrivacy;
import com.liteon.icampusguardian.db.DBHelper;
import com.liteon.icampusguardian.fragment.AlarmEditingFragment;
import com.liteon.icampusguardian.fragment.AlarmFragment;
import com.liteon.icampusguardian.fragment.AlarmFragment.IAddAlarmClicks;
import com.liteon.icampusguardian.fragment.AlarmPeriodFragment;
import com.liteon.icampusguardian.fragment.AppInfoPrivacyFragment;
import com.liteon.icampusguardian.fragment.DailyHealthFragment;
import com.liteon.icampusguardian.fragment.HealthFragment;
import com.liteon.icampusguardian.fragment.HealthMainFragment;
import com.liteon.icampusguardian.fragment.SafetyFragment;
import com.liteon.icampusguardian.fragment.SettingFragment;
import com.liteon.icampusguardian.fragment.SettingProfileFragment;
import com.liteon.icampusguardian.fragment.SettingTargetFragment;
import com.liteon.icampusguardian.util.AlarmItem;
import com.liteon.icampusguardian.util.AlarmPeriodAdapter.ViewHolder.IAlarmPeriodViewHolderClicks;
import com.liteon.icampusguardian.util.AlarmPeriodItem;
import com.liteon.icampusguardian.util.AppInfoPrivacyItem;
import com.liteon.icampusguardian.util.AppInfoPrivacyItemAdapter.ViewHolder.IAppInfoPrivacyViewHolderClicks;
import com.liteon.icampusguardian.util.BottomNavigationViewHelper;
import com.liteon.icampusguardian.util.CircularImageView;
import com.liteon.icampusguardian.util.ConfirmDeleteDialog;
import com.liteon.icampusguardian.util.CustomDialog;
import com.liteon.icampusguardian.util.Def;
import com.liteon.icampusguardian.util.GuardianApiClient;
import com.liteon.icampusguardian.util.JSONResponse;
import com.liteon.icampusguardian.util.HealthyItem.TYPE;
import com.liteon.icampusguardian.util.HealthyItemAdapter.ViewHolder.IHealthViewHolderClicks;
import com.liteon.icampusguardian.util.JSONResponse.Student;
import com.liteon.icampusguardian.util.SettingItemAdapter.ViewHolder.ISettingItemClickListener;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.BottomNavigationView.OnNavigationItemSelectedListener;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;
public class MainActivity extends AppCompatActivity implements IAddAlarmClicks,
		IAlarmPeriodViewHolderClicks, ISettingItemClickListener, NavigationView.OnNavigationItemSelectedListener, IAppInfoPrivacyViewHolderClicks {

	private static final String TAG = MainActivity.class.getName(); 
	private CircularImageView mChildIcon;
	private TextView mChildName;
	private Toolbar mToolbar;
	private BottomNavigationView mBottomView;
	private DrawerLayout mDrawerLayout;
	private NavigationView mNavigationView;
	private Fragment mCurrentFragment;
	private int mCurrentAlarmIdx;
	private List<Student> mStudents;
	private int mCurrentStudentIdx;
	private DBHelper mDbHelper;
	private LocalBroadcastManager mLocalBroadcastManager;
	private AppCompatButton mLogoutButton;
	private ConfirmDeleteDialog mUnPairConfirmDialog;
	private ConfirmDeleteDialog mDeleteAccountConfirmDialog;
	private static final int NAVIGATION_DRAWER = 1;
	private static final int NAVIGATION_BACK = 2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		// registerNotification();
		mDbHelper = DBHelper.getInstance(this);
		SharedPreferences sp = getSharedPreferences(Def.SHARE_PREFERENCE, Context.MODE_PRIVATE);
		String token = sp.getString(Def.SP_LOGIN_TOKEN, "");
		new checkTokenTask().execute(token, null, null);

		mCurrentStudentIdx = sp.getInt(Def.SP_CURRENT_STUDENT, 0);
		//mDbHelper.getAccountToken(mDbHelper.getReadableDatabase(), name)
		mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);
		// get child list
		mStudents = mDbHelper.queryChildList(mDbHelper.getReadableDatabase());
		findViews();
		setListener();
		setupToolbar();
		initChildInfo();
		updateMenuItem();
		BottomNavigationViewHelper.disableShiftMode(mBottomView);
		
		
	}

	private void registerNotification() {

		// if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
		// Create channel to show notifications.
		// String channelId =
		// getString(R.string.default_notification_channel_id);
		// String channelName =
		// getString(R.string.default_notification_channel_name);
		// NotificationManager notificationManager =
		// getSystemService(NotificationManager.class);
		// notificationManager.createNotificationChannel(new
		// NotificationChannel(channelId,
		// channelName, NotificationManager.IMPORTANCE_LOW));
		// }
	}

	private void setupToolbar() {
		setSupportActionBar(mToolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);

		mToolbar.setNavigationIcon(R.drawable.ic_dehaze_white_24dp);
		mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				mDrawerLayout.openDrawer(Gravity.LEFT);
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (getIntent().getBooleanExtra(Def.EXTRA_GOTO_MAIN_SETTING, false)) {
			SettingFragment settingFragment = new SettingFragment(this);
			changeFragment(settingFragment);
		} 

		if (getIntent().getExtras() != null) {
			if (getIntent().getBooleanExtra(Def.EXTRA_GOTO_MAIN_SETTING, false)) {
				mBottomView.setSelectedItemId(R.id.action_setting);
				return;
			}
			if (TextUtils.equals(Def.ACTION_NOTIFY, getIntent().getAction())) {
				String type = getIntent().getStringExtra(Def.EXTRA_NOTIFY_TYPE);
				if (TextUtils.equals(type, "sos")) {
					SafetyFragment safetyFragment = new SafetyFragment(getIntent());
					changeFragment(safetyFragment);
				}
			}
            for (String key : getIntent().getExtras().keySet()) {
                Object value = getIntent().getExtras().get(key);
                Log.d(TAG, "Key: " + key + " Value: " + value);
                Toast.makeText(this, "Key: " + key + " Value: " + value, Toast.LENGTH_SHORT).show();
            }
        } else {
			changeFragment(new SafetyFragment(), "安心", NAVIGATION_DRAWER);
		}
		IntentFilter filter = new IntentFilter();
		filter.addAction(Def.ACTION_NOTIFY);
		mLocalBroadcastManager.registerReceiver(mReceiver, filter);
		
	}

	@Override
	protected void onPause() {
		super.onPause();
		mLocalBroadcastManager.unregisterReceiver(mReceiver);
	}
	
	@Override
	public void onBackPressed() {
		if (mDrawerLayout.isDrawerOpen(Gravity.LEFT)) {
			mDrawerLayout.closeDrawer(Gravity.LEFT);
			return;
		}

		if (mCurrentFragment != null) {
			if (mCurrentFragment instanceof DailyHealthFragment) {
				mBottomView.setSelectedItemId(R.id.action_health);
				return;
			} else if (mCurrentFragment instanceof AlarmFragment) {
				if (((AlarmFragment) mCurrentFragment).isEditMode()) {
					((AlarmFragment) mCurrentFragment).exitEditMode();
					return;
				}
			} else if (mCurrentFragment instanceof AlarmEditingFragment) {
				mBottomView.setSelectedItemId(R.id.action_alarm);
				return;
			} else if (mCurrentFragment instanceof AlarmPeriodFragment) {
				changeFragment(new AlarmEditingFragment(mCurrentAlarmIdx, this), "設定鬧鈴", 0);
				return;
			} else if (mCurrentFragment instanceof SettingProfileFragment || mCurrentFragment instanceof SettingTargetFragment) {
				hideSoftKeyboard();
				changeFragment(new SettingFragment(MainActivity.this), getString(R.string.setting_tab),
						NAVIGATION_DRAWER);
				return;
			} else {
				finish();
				return;
			}
		}
		super.onBackPressed();
	}
	private void hideSoftKeyboard() {
		View view = getCurrentFocus();
        if (view != null) {  
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
	}
	private void findViews() {
		mToolbar = (Toolbar) findViewById(R.id.toolbar);
		mBottomView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mNavigationView = (NavigationView) findViewById(R.id.navigation);
		mChildIcon = (CircularImageView) mNavigationView.getHeaderView(0).findViewById(R.id.child_icon);
		mChildName = (TextView) mNavigationView.getHeaderView(0).findViewById(R.id.child_name);
		mLogoutButton = (AppCompatButton) mNavigationView.findViewById(R.id.drawer_button_logout);
	}

	private void setListener() {
		mBottomView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
		mNavigationView.setNavigationItemSelectedListener(this);
		mLogoutButton.setOnClickListener(mOnLogoutClickListener);
	}
	private OnClickListener mOnLogoutClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			SharedPreferences sp = getSharedPreferences(Def.SHARE_PREFERENCE, Context.MODE_PRIVATE);
			SharedPreferences.Editor editor = sp.edit();
			editor.remove(Def.SP_LOGIN_TOKEN);
			editor.commit();
			
			DBHelper helper = DBHelper.getInstance(MainActivity.this);
			helper.deleteAccount(helper.getWritableDatabase());
			finish();
			Intent intent = new Intent();
			intent.setClass(MainActivity.this, LoginActivity.class);
			startActivity(intent);
		}
	};
	private OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = new OnNavigationItemSelectedListener() {

		@Override
		public boolean onNavigationItemSelected(MenuItem item) {
			Fragment fragment = null;
			String title = "";
			switch (item.getItemId()) {
			case R.id.action_safty:
				fragment = new SafetyFragment();
				title = getString(R.string.safty_tab);
				break;
			case R.id.action_health:
				//fragment = new HealthFragment(MainActivity.this);
				fragment = new HealthMainFragment();
				title = getString(R.string.health_tab);
				break;
			case R.id.action_alarm:
				fragment = new AlarmFragment(MainActivity.this);
				title = getString(R.string.alarm_tab);
				break;
			case R.id.action_setting:
				fragment = new SettingFragment(MainActivity.this);
				title = getString(R.string.setting_tab);
				break;
			}
			if (fragment == null) {
				return false;
			}

			changeFragment(fragment, title, NAVIGATION_DRAWER);
			return true;
		}
	};

	private void changeFragment(Fragment frag) {
		mCurrentFragment = frag;
		FragmentManager fragmentManager = getSupportFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		fragmentTransaction.replace(R.id.container, frag);
		fragmentTransaction.commit();
	}

	private void changeFragment(Fragment frag, String title, int navigation) {
		mCurrentFragment = frag;
		FragmentManager fragmentManager = getSupportFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		fragmentTransaction.replace(R.id.container, frag);
		fragmentTransaction.commit();

		if (mToolbar != null) {

			mToolbar.setTitle(title);
			if (navigation == NAVIGATION_BACK) {
				mToolbar.setNavigationIcon(R.drawable.ic_navigate_before_white_24dp);
				mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						onBackPressed();
					}
				});
			} else if (navigation == NAVIGATION_DRAWER) {
				mToolbar.setNavigationIcon(R.drawable.ic_dehaze_white_24dp);
				mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {

						mDrawerLayout.openDrawer(Gravity.LEFT);
					}
				});
			}
		}
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
			if (mCurrentStudentIdx >= mStudents.size()) {
				mCurrentStudentIdx =  0;
			}
			mChildName.setText(mStudents.get(mCurrentStudentIdx).getNickname());
			//read child image file
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inPreferredConfig = Bitmap.Config.ARGB_8888;
			bitmap = BitmapFactory.decodeFile(
					Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/"
							+ mStudents.get(mCurrentStudentIdx).getStudent_id() + ".jpg",
					options);
		}

		if (bitmap != null) {
			mChildIcon.setImageBitmap(bitmap);
		} else {
			mChildIcon.setImageDrawable(getResources().getDrawable(R.drawable.setup_img_picture, null));
		}
	}

	private void updateMenuItem() {
		Menu menu = mNavigationView.getMenu();
		if (mStudents.size() == 0) {
			
		} else {
			int nextStudent = mCurrentStudentIdx+1;
			if (nextStudent >= mStudents.size()) {
				nextStudent = 0;
			} 
			MenuItem switchAccount = menu.findItem(R.id.action_switch_account);
			switchAccount.setTitle(String.format(getString(R.string.switch_account), mStudents.get(nextStudent).getNickname()));

			MenuItem deleteAccount = menu.findItem(R.id.action_delete_account);
			deleteAccount.setTitle(
					String.format(getString(R.string.delete_account), mStudents.get(mCurrentStudentIdx).getNickname()));
		}
	}

	@Override
	public boolean onNavigationItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_switch_account) {
			switchAccount();
		} else if (id == R.id.action_add_child) {
			addNewChild();
		} else if (id == R.id.action_delete_account) {
			deleteAccount();
		} else if (id == R.id.action_setting) {
			switchSetting();
		}
		mDrawerLayout.closeDrawers();
		return true;
	}

	public void deleteAccount() {
		mDeleteAccountConfirmDialog = new ConfirmDeleteDialog();
		mDeleteAccountConfirmDialog.setOnConfirmEventListener(mOnDeleteAccountConfirm);
		mDeleteAccountConfirmDialog.setmOnCancelListener(mOnDeleteAccountCancel);
		mDeleteAccountConfirmDialog.setmTitleText("刪除追蹤"+ mChildName.getText() + "\n" +
		"此手機將無法看見該帳號紀錄，但雲端記錄不會刪除，如日後要由此手機觀看，請重新加入該帳號");
		mDeleteAccountConfirmDialog.setmBtnConfirmText("確定");
		mDeleteAccountConfirmDialog.setmBtnCancelText("取消");
		mDeleteAccountConfirmDialog.show(getSupportFragmentManager(), "dialog_fragment");
	}
	
	private View.OnClickListener mOnDeleteAccountConfirm = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			Student student = mStudents.get(mCurrentStudentIdx);
			student.setIsDelete(1);
			mDbHelper.updateChildData(mDbHelper.getWritableDatabase(), student);
			mStudents.remove(student);
			mDeleteAccountConfirmDialog.dismiss();
			
			final CustomDialog dialog = new CustomDialog();
			dialog.setTitle("完成刪除追蹤" + mChildName.getText());
			dialog.setBtnText("好");
			dialog.setBtnConfirm(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					dialog.dismiss();
					if (mStudents.size() == 0) {
						mLogoutButton.callOnClick(); 
					} else {
						switchAccount();
					}
				}
			});
			dialog.show(getSupportFragmentManager(), "dialog_fragment");
		}
	};
	
	private View.OnClickListener mOnDeleteAccountCancel = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			mDeleteAccountConfirmDialog.dismiss();
		}
	};
	
	public void addNewChild() {
		finish();
		Intent intent = new Intent();
		intent.setClass(this, ChildInfoUpdateActivity.class);
		startActivity(intent);
	}
	private void switchSetting() {
		AppInfoPrivacyFragment frag = new AppInfoPrivacyFragment(this);
		changeFragment(frag, getString(R.string.drawer_setting), NAVIGATION_BACK);
	}
	
	private void switchAccount() {
		if (mStudents.size() == 0) {
			return;
		}
		if (mStudents.size() == 1) {
			mCurrentStudentIdx = 0;
		} else {
			
			if (mCurrentStudentIdx == mStudents.size() - 1) {
				mCurrentStudentIdx = 0;
			} else {
				mCurrentStudentIdx++;
			}
		}
		initChildInfo();
		updateMenuItem();
		
		SharedPreferences.Editor editor = getSharedPreferences(Def.SHARE_PREFERENCE, Context.MODE_PRIVATE).edit();
		editor.putInt(Def.SP_CURRENT_STUDENT, mCurrentStudentIdx);
		editor.commit();
		
		Intent intent = new Intent();
		intent.setClass(this, LoadingPageActivity.class);
		startActivity(intent);
	}

	@Override
	public void onAddAlarmClick() {
		if (mCurrentFragment instanceof AlarmFragment) {
			AlarmFragment fragment = (AlarmFragment) mCurrentFragment;
			changeFragment(new AlarmEditingFragment(this), "設定鬧鈴", 0);
			mCurrentAlarmIdx = -1;
			if (fragment.isEditMode()) {
				fragment.exitEditMode();
			}
		}
		
	}

	@Override
	public void onEditAlarm(int idx) {
		if (mCurrentFragment instanceof AlarmFragment) {
			AlarmFragment fragment = (AlarmFragment) mCurrentFragment;
			mCurrentAlarmIdx = idx;
			changeFragment(new AlarmEditingFragment(idx, this), "設定鬧鈴", 0);
			
			if (fragment.isEditMode()) {
				fragment.exitEditMode();
			}
		}
		
	}

	@Override
	public void onClick(AlarmPeriodItem item, AlarmItem alarmItem) {
		if (item.getItemType() == AlarmPeriodItem.TYPE.CUSTOMIZE) {
			// TODO get current AlarmItem
			alarmItem.setPeriodItem(item);
			changeFragment(new AlarmPeriodFragment(mCurrentAlarmIdx), "設定鬧鈴週期", NAVIGATION_BACK);
		}
	}

	@Override
	public void onSettingItemClick(com.liteon.icampusguardian.util.SettingItem.TYPE type) {
		switch (type) {
		case BASIC_INFO:
			changeFragment(new SettingProfileFragment(), "基本資料", NAVIGATION_BACK);
			break;
		case GOAL_SETTING:
			changeFragment(new SettingTargetFragment(), "每日目標設定", NAVIGATION_BACK);
			break;
		case PAIRING:
			if (!TextUtils.isEmpty(mStudents.get(mCurrentStudentIdx).getUuid())) {
				showUnPairDialog();
			} else {
				showPairingPage();
			}
			break;
		case PRIVACY_INFO:
			UpdateWatchInfoAndPrivacy();
			break;
		case WATCH_THEME:
			UpdateWatchTheme();
			break;
		default:
			break;
		}
	}
	
	private void showPairingPage() {
		Intent intent = new Intent();
		intent.setClass(this, BLEPairingListActivity.class);
		startActivity(intent);
	}
	
	private void showUnPairDialog() {
		mUnPairConfirmDialog = new ConfirmDeleteDialog();
		mUnPairConfirmDialog.setOnConfirmEventListener(mUnPairConfirmClickListener);
		mUnPairConfirmDialog.setmOnCancelListener(mUnPairCancelClickListener);
		mUnPairConfirmDialog.setmTitleText("將解除已綁定的智慧手錶");
		mUnPairConfirmDialog.setmBtnConfirmText("確定");
		mUnPairConfirmDialog.setmBtnCancelText("取消");
		mUnPairConfirmDialog.show(getSupportFragmentManager(), "dialog_fragment");
	}
	private View.OnClickListener mUnPairConfirmClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			mUnPairConfirmDialog.dismiss();
			new UnPairTask().execute();
		}
	};
	
	private View.OnClickListener mUnPairCancelClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			mUnPairConfirmDialog.dismiss();
		}
	};
	private void UpdateWatchTheme() {
		Intent intent = new Intent();
		intent.setClass(this, PersonalizedWatchActivity.class);
		startActivity(intent);
	}

	private void UpdateWatchInfoAndPrivacy() {
		Intent intent = new Intent();
		intent.setClass(this, WatchInfoAndPrivacyActivity.class);
		startActivity(intent);
	}
	private BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (TextUtils.equals(Def.ACTION_NOTIFY, intent.getAction())) {
				String type = intent.getStringExtra(Def.EXTRA_NOTIFY_TYPE);
				if (TextUtils.equals(type, "sos")) {
					SafetyFragment safetyFragment = new SafetyFragment(intent);
					changeFragment(safetyFragment);
				}
			}
		}
	};

	@Override
	public void onClick(AppInfoPrivacyItem item) {
		if (item.getItemType() == AppInfoPrivacyItem.TYPE.USER_TERM) {
			Intent intent = new Intent();
			intent.setClass(this, UserTermActivity.class);
			intent.putExtra(Def.EXTRA_DISABLE_USERTREM_BOTTOM, true);
			startActivity(intent);
		} else if (item.getItemType() == AppInfoPrivacyItem.TYPE.PARENT_INFO) {
			Intent intent = new Intent();
			intent.setClass(this, UserInfoUpdateActivity.class);
			startActivity(intent);
		}
	}
	
	class checkTokenTask extends AsyncTask<String, Void, String>{
		
		@Override
		protected String doInBackground(String... params) {
			String token = params[0];
			GuardianApiClient apiClient = new GuardianApiClient(MainActivity.this);
			apiClient.setToken(token);
			JSONResponse response = apiClient.getChildrenList();
			if (response == null || TextUtils.equals("ERR01", response.getReturn().getResponseSummary().getStatusCode())) {
				SharedPreferences sp = getSharedPreferences(Def.SHARE_PREFERENCE, Context.MODE_PRIVATE);
				SharedPreferences.Editor editor = sp.edit();
				editor.remove(Def.SP_LOGIN_TOKEN);
				editor.commit();
				// clear token in db 
				mDbHelper.clearAccountToken(mDbHelper.getWritableDatabase(), token);
				return null;
			}
			return token;
		}

		protected void onPostExecute(String result) {
			if (result == null) {
				Intent intent = new Intent();
				intent.setClass(MainActivity.this, LoginActivity.class);
				startActivity(intent);
				Toast.makeText(getApplicationContext(), "Token provided is expired, need to re-login", Toast.LENGTH_LONG).show();
				finish();
			}
		};
	}
	
	class UnPairTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			GuardianApiClient apiClient = new GuardianApiClient(MainActivity.this);
			JSONResponse response = apiClient.unpairDevice(mStudents.get(mCurrentStudentIdx));
			if (response != null) {
				if (response.getReturn() != null) {
					String statusCode = response.getReturn().getResponseSummary().getStatusCode(); 
					if (TextUtils.equals(statusCode, Def.RET_SUCCESS_1)) {
						Student student = mStudents.get(mCurrentStudentIdx);
						student.setUuid("");
						mDbHelper.updateChildData(mDbHelper.getWritableDatabase(), student);
					}
				}
			}
			return null;
		}
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
		}
	}
	
	private void sendNotification(String messageBody) {
		Intent intent = new Intent(this, MainActivity.class);
		intent.setAction(Def.ACTION_NOTIFY);
		intent.putExtra(Def.EXTRA_NOTIFY_TYPE, "sos");
		intent.putExtra(Def.EXTRA_SOS_LOCATION, "25.070108, 121.611435");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle("iCampus Guardian")
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
	}
}
