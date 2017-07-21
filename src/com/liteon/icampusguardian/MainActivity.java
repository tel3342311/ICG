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
import com.liteon.icampusguardian.util.Def;
import com.liteon.icampusguardian.util.GuardianApiClient;
import com.liteon.icampusguardian.util.JSONResponse;
import com.liteon.icampusguardian.util.HealthyItem.TYPE;
import com.liteon.icampusguardian.util.HealthyItemAdapter.ViewHolder.IHealthViewHolderClicks;
import com.liteon.icampusguardian.util.JSONResponse.Student;
import com.liteon.icampusguardian.util.SettingItemAdapter.ViewHolder.ISettingItemClickListener;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.BottomNavigationView.OnNavigationItemSelectedListener;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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
import android.widget.TextView;
import android.widget.Toast;
public class MainActivity extends AppCompatActivity implements IAddAlarmClicks, IHealthViewHolderClicks,
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
		
		if (getIntent().getExtras() != null) {
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
        }
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
		changeFragment(new SafetyFragment(), "安心", NAVIGATION_DRAWER);

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
				fragment = new HealthFragment(MainActivity.this);
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

	@Override
	public void onClick(TYPE type) {
		Fragment fragment = new DailyHealthFragment(type);
		changeFragment(fragment, type.getName(), NAVIGATION_BACK);
	}

	private void initChildInfo() {
		mChildIcon.setImageDrawable(getResources().getDrawable(R.drawable.setup_img_picture, null));
		mChildIcon.setBorderColor(getResources().getColor(R.color.md_white_1000));
		mChildIcon.setBorderWidth(10);
		mChildIcon.setSelectorColor(getResources().getColor(R.color.md_blue_400));
		mChildIcon.setSelectorStrokeColor(getResources().getColor(R.color.md_blue_800));
		mChildIcon.setSelectorStrokeWidth(10);
		mChildIcon.addShadow();
		if (mStudents.size() > 0) {
			mChildName.setText(mStudents.get(mCurrentStudentIdx).getName());
		}
	}

	private void updateMenuItem() {
		Menu menu = mNavigationView.getMenu();
		if (mStudents.size() == 0) {
			
		} else {
			int nextStudent = mCurrentStudentIdx == 0 ? 1 : 0;
			MenuItem switchAccount = menu.findItem(R.id.action_switch_account);
			switchAccount
					.setTitle(String.format(getString(R.string.switch_account), mStudents.get(nextStudent).getName()));

			MenuItem deleteAccount = menu.findItem(R.id.action_delete_account);
			deleteAccount.setTitle(
					String.format(getString(R.string.delete_account), mStudents.get(mCurrentStudentIdx).getName()));
		}
	}

	@Override
	public boolean onNavigationItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_switch_account) {
			switchAccount();
		} else if (id == R.id.action_add_child) {

		} else if (id == R.id.action_delete_account) {

		} else if (id == R.id.action_setting) {
			switchSetting();
		}
		mDrawerLayout.closeDrawers();
		return true;
	}

	private void switchSetting() {
		AppInfoPrivacyFragment frag = new AppInfoPrivacyFragment(this);
		changeFragment(frag, getString(R.string.drawer_setting), NAVIGATION_BACK);
	}
	
	private void switchAccount() {
		mCurrentStudentIdx = mCurrentStudentIdx == 0 ? 1 : 0;
		initChildInfo();
		updateMenuItem();
		
		SharedPreferences.Editor editor = getSharedPreferences(Def.SHARE_PREFERENCE, Context.MODE_PRIVATE).edit();
		editor.putInt(Def.SP_CURRENT_STUDENT, mCurrentStudentIdx);
		editor.commit();
	}

	@Override
	public void onAddAlarmClick() {
		changeFragment(new AlarmEditingFragment(this), "設定鬧鈴", 0);
	}

	@Override
	public void onEditAlarm(int idx) {
		mCurrentAlarmIdx = idx;
		changeFragment(new AlarmEditingFragment(idx, this), "設定鬧鈴", 0);
	}

	@Override
	public void onClick(AlarmPeriodItem item) {
		if (item.getItemType() == AlarmPeriodItem.TYPE.CUSTOMIZE) {
			// TODO get current AlarmItem
			AlarmItem alarmItem = new AlarmItem();
			alarmItem.setPeriodItem(item);
			changeFragment(new AlarmPeriodFragment(alarmItem), "設定鬧鈴週期", NAVIGATION_BACK);
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
//			Intent intent = new Intent();
//			intent.setClass(this, ChildInfoUpdateActivity.class);
//			startActivity(intent);
			break;
		case PRIVACY_INFO:
			break;
		case WATCH_THEME:
			break;
		default:
			break;
		}
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
			if (TextUtils.equals("ERR01", response.getReturn().getResponseSummary().getStatusCode())) {
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
}
