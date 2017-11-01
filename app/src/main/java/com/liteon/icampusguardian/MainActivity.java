package com.liteon.icampusguardian;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;

import com.liteon.icampusguardian.db.DBHelper;
import com.liteon.icampusguardian.fragment.AlarmEditingFragment;
import com.liteon.icampusguardian.fragment.AlarmFragment;
import com.liteon.icampusguardian.fragment.AlarmFragment.IAddAlarmClicks;
import com.liteon.icampusguardian.fragment.AlarmPeriodFragment;
import com.liteon.icampusguardian.fragment.AppInfoPrivacyFragment;
import com.liteon.icampusguardian.fragment.DailyHealthFragment;
import com.liteon.icampusguardian.fragment.HealthMainFragment;
import com.liteon.icampusguardian.fragment.SafetyFragment;
import com.liteon.icampusguardian.fragment.SettingFragment;
import com.liteon.icampusguardian.fragment.SettingProfileFragment;
import com.liteon.icampusguardian.fragment.SettingTargetFragment;
import com.liteon.icampusguardian.util.AlarmItem;
import com.liteon.icampusguardian.util.AlarmManager;
import com.liteon.icampusguardian.util.AlarmPeriodAdapter.ViewHolder.IAlarmPeriodViewHolderClicks;
import com.liteon.icampusguardian.util.AlarmPeriodItem.TYPE;
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
import com.liteon.icampusguardian.util.JSONResponse.Student;
import com.liteon.icampusguardian.util.SettingItemAdapter.ViewHolder.ISettingItemClickListener;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
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
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.DrawerLayout.DrawerListener;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.SlidingDrawer.OnDrawerOpenListener;
import android.widget.TextView;
import android.widget.Toast;

import static com.liteon.icampusguardian.App.getContext;

public class MainActivity extends AppCompatActivity implements IAddAlarmClicks,
		IAlarmPeriodViewHolderClicks, ISettingItemClickListener, NavigationView.OnNavigationItemSelectedListener, IAppInfoPrivacyViewHolderClicks, DrawerListener {

	private static final String TAG = MainActivity.class.getName(); 
	private CircularImageView mChildIcon;
	private TextView mChildName;
	private Toolbar mToolbar;
	private BottomNavigationView mBottomView;
	private DrawerLayout mDrawerLayout;
	private NavigationView mNavigationView;
	private Fragment mCurrentFragment;
	private List<Student> mStudents;
	private int mCurrentStudentIdx;
	private DBHelper mDbHelper;
	private LocalBroadcastManager mLocalBroadcastManager;
	private AppCompatButton mLogoutButton;
	private ConfirmDeleteDialog mUnPairConfirmDialog;
	private ConfirmDeleteDialog mDeleteAccountConfirmDialog;
	private static final int NAVIGATION_DRAWER = 1;
	private static final int NAVIGATION_BACK = 2;
	private SafetyFragment mSaftyFragment;
	private TextView mTitleView;
	//For BT pairing
	private String mBtAddress;
	private BluetoothDevice mBTDevice;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		// registerNotification();
		mDbHelper = DBHelper.getInstance(this);
		SharedPreferences sp = getSharedPreferences(Def.SHARE_PREFERENCE, Context.MODE_PRIVATE);
		if (!App.isOffline) {
			String token = sp.getString(Def.SP_LOGIN_TOKEN, "");
			new checkTokenTask().execute(token, null, null);
		}
		mCurrentStudentIdx = sp.getInt(Def.SP_CURRENT_STUDENT, 0);
		//mDbHelper.getAccountToken(mDbHelper.getReadableDatabase(), name)
		mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);
		// get child list
		mStudents = mDbHelper.queryChildList(mDbHelper.getReadableDatabase());
		if (mStudents.size() == 0) {
			Intent intent = new Intent();
			intent.setClass(this, ChildPairingActivity.class);
			startActivity(intent);
		}
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
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		if (TextUtils.equals(Def.ACTION_NOTIFY, intent.getAction())) {
			String type = intent.getStringExtra(Def.EXTRA_NOTIFY_TYPE);
			if (TextUtils.equals(type, "sos")) {
				if (mSaftyFragment == null) {
					mSaftyFragment = new SafetyFragment(intent);
				} else {
					mSaftyFragment.setAlertIntent(intent);
				}
			}
		}
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
					if (mSaftyFragment == null) {
						mSaftyFragment = new SafetyFragment(getIntent());
					} else {
						mSaftyFragment.setAlertIntent(getIntent());
					}
					changeFragment(mSaftyFragment);
                    mBottomView.setSelectedItemId(R.id.action_safty);
				}
			}
            for (String key : getIntent().getExtras().keySet()) {
                Object value = getIntent().getExtras().get(key);
                Log.d(TAG, "Key: " + key + " Value: " + value);
                Toast.makeText(this, "Key: " + key + " Value: " + value, Toast.LENGTH_SHORT).show();
            }
        } else {
        	if (mSaftyFragment == null) {
				mSaftyFragment = new SafetyFragment();
			}
			changeFragment(mSaftyFragment, getString(R.string.safty), NAVIGATION_DRAWER);
            mBottomView.setSelectedItemId(R.id.action_safty);
		}
		IntentFilter filter = new IntentFilter();
		filter.addAction(Def.ACTION_NOTIFY);
		mLocalBroadcastManager.registerReceiver(mReceiver, filter);
		mToolbar.setTitle("");
		initChildInfo();
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
				//changeFragment(new AlarmEditingFragment(mCurrentAlarmIdx, this), getString(R.string.alarm_edit_period), 0);
				if (((AlarmPeriodFragment) mCurrentFragment).getCurrentPeriod() == 0) {
					showPeriodErrorDialog();
					return;
				}
				onFinishEditPeriod();
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
		mTitleView = (TextView) findViewById(R.id.toolbar_title);
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
		mDrawerLayout.addDrawerListener(this);
	}
	
	
	private OnClickListener mOnLogoutClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			SharedPreferences sp = getSharedPreferences(Def.SHARE_PREFERENCE, Context.MODE_PRIVATE);
			SharedPreferences.Editor editor = sp.edit();
			editor.remove(Def.SP_USER_TERM_READ);
			editor.remove(Def.SP_LOGIN_TOKEN);
			editor.commit();

			if (!App.isOffline) {
                DBHelper helper = DBHelper.getInstance(MainActivity.this);
                helper.deleteAccount(helper.getWritableDatabase());
            }
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
				if (mSaftyFragment == null) {
					mSaftyFragment = new SafetyFragment();
				}
				fragment = mSaftyFragment;
				title = getString(R.string.safty_tab);
				break;
			case R.id.action_health:
				//fragment = new HealthFragment(MainActivity.this);
				fragment = new HealthMainFragment();
				title = getString(R.string.healthy_today_reocrd);
				break;
			case R.id.action_alarm:
				fragment = new AlarmFragment();
				title = getString(R.string.alarm_tab);
				break;
			case R.id.action_setting:
				fragment = new SettingFragment();
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

	private void showPeriodErrorDialog() {
		final CustomDialog dialog = new CustomDialog();
		dialog.setTitle("請輸入週期");
		dialog.setIcon(R.drawable.ic_error_outline_black_24dp);
		dialog.setBtnText("好");
		dialog.setBtnConfirm(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		dialog.show(getSupportFragmentManager(), "dialog_fragment");
	}
	
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

			//mToolbar.setTitle(title);
			mTitleView.setText(title);
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
		// get child list
		mStudents = mDbHelper.queryChildList(mDbHelper.getReadableDatabase());
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
            //get current bt address
            String studentID = mStudents.get(mCurrentStudentIdx).getStudent_id();
            mBtAddress = mDbHelper.getBlueToothAddrByStudentId(mDbHelper.getReadableDatabase(), studentID);
		}

		if (bitmap != null) {
			mChildIcon.setImageBitmap(bitmap);
		} else {
			mChildIcon.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.setup_img_picture));
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
			String title = String.format(getString(R.string.switch_account), mStudents.get(nextStudent).getNickname());
			//if only one child left, disable switch menu item;
			if (mStudents.size() == 1) {
				switchAccount.setEnabled(false);
				SpannableString s = new SpannableString(title);
			    s.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.md_grey_400)), 0, s.length(), 0);
			    switchAccount.setTitle(s);
			} else {
				switchAccount.setTitle(title);
			}
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
		StringBuilder sBuilder = new StringBuilder();
		sBuilder.append(String.format(getString(R.string.delete_tracking), mChildName.getText()))
		.append("\n")
		.append(getString(R.string.delete_warning));
		mDeleteAccountConfirmDialog = new ConfirmDeleteDialog();
		mDeleteAccountConfirmDialog.setOnConfirmEventListener(mOnDeleteAccountConfirm);
		mDeleteAccountConfirmDialog.setmOnCancelListener(mOnDeleteAccountCancel);
		mDeleteAccountConfirmDialog.setmTitleText(sBuilder.toString());
		mDeleteAccountConfirmDialog.setmBtnConfirmText(getString(R.string.delete_child_confirm));
		mDeleteAccountConfirmDialog.setmBtnCancelText(getString(R.string.delete_child_cancel));
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
			dialog.setTitle(String.format(getString(R.string.child_deleted), mChildName.getText()));
			dialog.setBtnText(getString(android.R.string.ok));
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
			changeFragment(new AlarmEditingFragment(), getString(R.string.alarm_edit_period), 0);

			if (fragment.isEditMode()) {
				fragment.exitEditMode();
			}
		}
		
	}

	public void onFinishEditPeriod(){
		if (mCurrentFragment instanceof AlarmPeriodFragment) {
			changeFragment(new AlarmEditingFragment(), getString(R.string.alarm_edit_period), 0);
		}
	}

	@Override
	public void onEditAlarm(int idx) {
		if (mCurrentFragment instanceof AlarmFragment) {
			AlarmFragment fragment = (AlarmFragment) mCurrentFragment;

			changeFragment(new AlarmEditingFragment(), getString(R.string.alarm_edit_period), 0);
			
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
			changeFragment(new AlarmPeriodFragment(), getString(R.string.alarm_edit_period), NAVIGATION_BACK);
		}
	}

	@Override
	public void onSettingItemClick(com.liteon.icampusguardian.util.SettingItem.TYPE type) {
		switch (type) {
		case BASIC_INFO:
			changeFragment(new SettingProfileFragment(), getString(R.string.child_basic_profile), NAVIGATION_BACK);
			break;
		case GOAL_SETTING:
			changeFragment(new SettingTargetFragment(), getString(R.string.child_goal_setting), NAVIGATION_BACK);
			break;
		case PAIRING:
			//TODO For BT Testing
			//Get BT device and check if the device is BONDED

//			if (!TextUtils.isEmpty(mBtAddress)) {
//				showUnPairDialog();
//			} else {
//				showPairingPage();
//			}
			//For Cloud's pair/unpair
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
		mUnPairConfirmDialog.setmTitleText(getString(R.string.will_unbind_watch));
		mUnPairConfirmDialog.setmBtnConfirmText(getString(R.string.bind_confirm));
		mUnPairConfirmDialog.setmBtnCancelText(getString(R.string.bind_cancel));
		mUnPairConfirmDialog.show(getSupportFragmentManager(), "dialog_fragment");
	}
	private View.OnClickListener mUnPairConfirmClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			mUnPairConfirmDialog.dismiss();
            String btAddr = mDbHelper.getBlueToothAddrByStudentId(mDbHelper.getReadableDatabase(), mStudents.get(mCurrentStudentIdx).getStudent_id());
            Set<BluetoothDevice> pairedDevices = BluetoothAdapter.getDefaultAdapter().getBondedDevices();
            for (BluetoothDevice device : pairedDevices) {
                if (TextUtils.equals(device.getAddress(), btAddr)) {
                    mBTDevice = device;
                    break;
                }
            }
            new UnPairBTTask().execute(mBTDevice);
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
					if (mSaftyFragment == null) {
						mSaftyFragment = new SafetyFragment(intent);
					}
					changeFragment(mSaftyFragment);
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

	//Unpair for BT
	class UnPairBTTask extends AsyncTask<BluetoothDevice, Void, Void> {

		@Override
		protected Void doInBackground(BluetoothDevice... params) {
			try {
				Method m = params[0].getClass()
						.getMethod("removeBond", (Class[]) null);
				m.invoke(params[0], (Object[]) null);
			} catch (Exception e) {
				Log.e("RemoveBond failed.", e.getMessage());
			}
			mStudents.get(mCurrentStudentIdx).setUuid("");
			mDbHelper.updateChildByStudentId(mDbHelper.getWritableDatabase(), mStudents.get(mCurrentStudentIdx));
			return null;
		}
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			Toast.makeText(App.getContext(), getString(R.string.unbind_watch),Toast.LENGTH_SHORT).show();
			if (mCurrentFragment instanceof SettingFragment) {
				((SettingFragment)mCurrentFragment).notifyBTState();
			}
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

	@Override
	public void onDrawerClosed(View arg0) {
		
	}

	@Override
	public void onDrawerOpened(View arg0) {
		mStudents = mDbHelper.queryChildList(mDbHelper.getReadableDatabase());
		initChildInfo();
		updateMenuItem();
	}

	@Override
	public void onDrawerSlide(View arg0, float arg1) {
		
	}

	@Override
	public void onDrawerStateChanged(int arg0) {
		
	}
	
	
}
