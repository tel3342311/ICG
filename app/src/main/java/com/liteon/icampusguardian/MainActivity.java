package com.liteon.icampusguardian;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
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
import android.os.Build;
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
import android.widget.TextView;
import android.widget.Toast;

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
import com.liteon.icampusguardian.util.JSONResponse.Student;
import com.liteon.icampusguardian.util.SettingItemAdapter.ViewHolder.ISettingItemClickListener;
import com.liteon.icampusguardian.util.Utils;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

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
    private ConfirmDeleteDialog mLogoutAccountConfirmDialog;
	private static final int NAVIGATION_DRAWER = 1;
	private static final int NAVIGATION_BACK = 2;
	private SafetyFragment mSaftyFragment;
	private TextView mTitleView;
	private SharedPreferences mSharedPreference;
	//For BT pairing
	private String mBtAddress;
	private BluetoothDevice mBTDevice;
	private boolean isChangingToAlarmPageForBT;

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		registerNotification();
		GuardianApiClient.getInstance(MainActivity.this);
		mDbHelper = DBHelper.getInstance(this);
		mSharedPreference = getSharedPreferences(Def.SHARE_PREFERENCE, Context.MODE_PRIVATE);
		String token = mSharedPreference.getString(Def.SP_LOGIN_TOKEN, "");
		new checkTokenTask().execute(token, null, null);

		mCurrentStudentIdx = mSharedPreference.getInt(Def.SP_CURRENT_STUDENT, 0);
		//mDbHelper.getAccountToken(mDbHelper.getReadableDatabase(), name)
		mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);
		// get child list
		mStudents = mDbHelper.queryChildList(mDbHelper.getReadableDatabase());
		if (mStudents.size() == 0) {
			Intent intent = new Intent();
			intent.setClass(this, ChildInfoUpdateActivity.class);
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

		 if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
             //Create channel to show notifications.
             String channelId = Def.DEFAULT_NOTIFICATION_CHANNEL_ID;
             String channelName = Def.DEFAULT_NOTIFICATION_CHANNEL_NAME;
             NotificationManager notificationManager =
                     getSystemService(NotificationManager.class);
             notificationManager.createNotificationChannel(new
                     NotificationChannel(channelId,
                     channelName, NotificationManager.IMPORTANCE_LOW));
         }
	}

	private void setupToolbar() {
		setSupportActionBar(mToolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);

		mToolbar.setNavigationIcon(R.drawable.ic_dehaze_white_24dp);
		mToolbar.setNavigationOnClickListener(v -> mDrawerLayout.openDrawer(Gravity.LEFT));
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
        setIntent(intent);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
        mStudents = mDbHelper.queryChildList(mDbHelper.getReadableDatabase());
        mCurrentStudentIdx = mSharedPreference.getInt(Def.SP_CURRENT_STUDENT, 0);

		if (getIntent().getExtras() != null) {
			if (TextUtils.equals(Def.ACTION_NOTIFY, getIntent().getAction())) {
				String type = getIntent().getStringExtra(Def.EXTRA_NOTIFY_TYPE);
				if (TextUtils.equals(type, "sos")) {
					if (mSaftyFragment == null) {
						mSaftyFragment = new SafetyFragment(getIntent());
					} else {
						mSaftyFragment.setAlertIntent(getIntent());
					}
                    mBottomView.setSelectedItemId(R.id.action_safty);
				}
			} else {
                switchPage();
			}
        } else {
            switchPage();
		}
		IntentFilter filter = new IntentFilter();
		filter.addAction(Def.ACTION_NOTIFY);
		mLocalBroadcastManager.registerReceiver(mReceiver, filter);
		mToolbar.setTitle("");
		new UpdateStudentList().execute();
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
				changeFragment(new SettingFragment(MainActivity.this), getString(R.string.child_setup_profile),
						NAVIGATION_DRAWER);
				return;
			} else if (mCurrentFragment instanceof AppInfoPrivacyFragment) {
                mBottomView.setSelectedItemId(R.id.action_safty);
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
		mToolbar = findViewById(R.id.toolbar);
		mTitleView = findViewById(R.id.toolbar_title);
		mBottomView = findViewById(R.id.bottom_navigation);
		mDrawerLayout = findViewById(R.id.drawer_layout);
		mNavigationView = findViewById(R.id.navigation);
		mChildIcon = mNavigationView.getHeaderView(0).findViewById(R.id.child_icon);
		mChildName = mNavigationView.getHeaderView(0).findViewById(R.id.child_name);
		mLogoutButton = mNavigationView.findViewById(R.id.drawer_button_logout);
	}

	private void setListener() {
		mBottomView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
		mNavigationView.setNavigationItemSelectedListener(this);
		mLogoutButton.setOnClickListener(mOnLogoutClickListener);
		mDrawerLayout.addDrawerListener(this);
	}
	
	
	private OnClickListener mOnLogoutClickListener = v -> logoutAccount();

	private OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = new OnNavigationItemSelectedListener() {

		@Override
		public boolean onNavigationItemSelected(MenuItem item) {
			Fragment fragment = null;
			if (TextUtils.isEmpty(mStudents.get(mCurrentStudentIdx).getUuid())) {
                Utils.showErrorDialog(getString(R.string.pairing_watch_pair));
                return false;
            }
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
				title = getString(R.string.healthy_today_reocrd_not_update);
				break;
			case R.id.action_alarm:
				fragment = new AlarmFragment();
				title = getString(R.string.alarm_tab);
				break;
			case R.id.action_setting:
				fragment = new SettingFragment();
				title = getString(R.string.child_setup_profile);
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
		dialog.setBtnConfirm(v -> dialog.dismiss());
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
				mToolbar.setNavigationOnClickListener(v -> onBackPressed());
			} else if (navigation == NAVIGATION_DRAWER) {
				mToolbar.setNavigationIcon(R.drawable.ic_dehaze_white_24dp);
				mToolbar.setNavigationOnClickListener(v -> mDrawerLayout.openDrawer(Gravity.LEFT));
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
			options.inPreferredConfig = Bitmap.Config.RGB_565;
			bitmap = BitmapFactory.decodeFile(
					Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/"
							+ mStudents.get(mCurrentStudentIdx).getStudent_id() + ".jpg",
					options);
            //get current bt address
            String studentID = mStudents.get(mCurrentStudentIdx).getStudent_id();
            mBtAddress = mDbHelper.getBlueToothAddrByStudentId(mDbHelper.getReadableDatabase(), studentID);

            if (bitmap != null) {
                mChildIcon.setImageBitmap(bitmap);
            } else {
                mChildIcon.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.setup_img_picture));
            }
            //Show Toast when no uuid
            if (TextUtils.isEmpty(mStudents.get(mCurrentStudentIdx).getUuid())) {
                Toast.makeText(this, "Please go to settings and pair with your device", Toast.LENGTH_LONG).show();
            }
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
			switchToAppInfoSetting();
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
		    if (mStudents.size() == 0) {
		        return;
            }

			Student student = mStudents.get(mCurrentStudentIdx);
			student.setIsDelete(1);
			new Thread( () -> {
				GuardianApiClient apiClient = GuardianApiClient.getInstance(MainActivity.this);
				JSONResponse response = apiClient.unpairDevice(student);
			}).start();
			mDbHelper.deleteChildByStudentID(mDbHelper.getWritableDatabase(), student.getStudent_id());
			mStudents.remove(student);
			SharedPreferences.Editor editor = getSharedPreferences(Def.SHARE_PREFERENCE, Context.MODE_PRIVATE).edit();
			editor.putInt(Def.SP_CURRENT_STUDENT, 0);
			editor.commit();
            new UnPairCloudTask().execute(student);
			mDeleteAccountConfirmDialog.dismiss();
			final CustomDialog dialog = new CustomDialog();
			dialog.setTitle(String.format(getString(R.string.child_deleted), mChildName.getText()));
			dialog.setBtnText(getString(android.R.string.ok));
			dialog.setBtnConfirm(v1 -> {
                dialog.dismiss();
                if (mStudents.size() == 0) {
                    mLogoutButton.callOnClick();
                } else {
                    switchAccount();
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


    public void logoutAccount() {
        mLogoutAccountConfirmDialog = new ConfirmDeleteDialog();
        mLogoutAccountConfirmDialog.setOnConfirmEventListener(mOnLogoutAccountConfirm);
        mLogoutAccountConfirmDialog.setmOnCancelListener(mOnLogoutAccountCancel);
        mLogoutAccountConfirmDialog.setmTitleText(getString(R.string.logout_confirm));
        mLogoutAccountConfirmDialog.setmBtnConfirmText(getString(R.string.delete_child_confirm));
        mLogoutAccountConfirmDialog.setmBtnCancelText(getString(R.string.delete_child_cancel));
        mLogoutAccountConfirmDialog.show(getSupportFragmentManager(), "dialog_fragment");
    }

    private View.OnClickListener mOnLogoutAccountConfirm = new OnClickListener() {

        @Override
        public void onClick(View v) {

            mLogoutAccountConfirmDialog.dismiss();
            SharedPreferences sp = getSharedPreferences(Def.SHARE_PREFERENCE, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.remove(Def.SP_USER_TERM_READ);
            editor.remove(Def.SP_LOGIN_TOKEN);
            editor.remove(Def.SP_ALARM_SYNCED);
            editor.commit();

			GuardianApiClient.getInstance(MainActivity.this).setToken(null);
            DBHelper helper = DBHelper.getInstance(MainActivity.this);
            helper.deletaAll(helper.getWritableDatabase());

            finish();
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, LoginActivity.class);
            startActivity(intent);

        }
    };

    private View.OnClickListener mOnLogoutAccountCancel = new OnClickListener() {

        @Override
        public void onClick(View v) {
            mLogoutAccountConfirmDialog.dismiss();
        }
    };

	public void addNewChild() {
		finish();
		Intent intent = new Intent();
		intent.setClass(this, ChildInfoUpdateActivity.class);
		startActivity(intent);
	}
	private void switchToAppInfoSetting() {
        AppInfoPrivacyFragment frag = new AppInfoPrivacyFragment(this);
		changeFragment(frag, getString(R.string.app_info_setting), NAVIGATION_BACK);
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
			changeFragment(new AlarmEditingFragment(), getString(R.string.alarm_edit_period_add), 0);

			if (fragment.isEditMode()) {
				fragment.exitEditMode();
			}
		}
		
	}

	public void onFinishEditPeriod(){
		if (mCurrentFragment instanceof AlarmPeriodFragment) {
			changeFragment(new AlarmEditingFragment(), getString(R.string.alarm_edit_period_add), 0);
		}
	}

	@Override
	public void onEditAlarm(int idx) {
		if (mCurrentFragment instanceof AlarmFragment) {
			AlarmFragment fragment = (AlarmFragment) mCurrentFragment;

            if (fragment.isEditMode()) {
                fragment.exitEditMode();
            }

			changeFragment(new AlarmEditingFragment(), getString(R.string.alarm_edit_period_add), 0);
		}
		
	}

	@Override
	public void onClick(AlarmPeriodItem item, AlarmItem alarmItem) {
		if (item.getItemType() == AlarmPeriodItem.TYPE.CUSTOMIZE) {
			// TODO get current AlarmItem
			alarmItem.setPeriodItem(item);
			changeFragment(new AlarmPeriodFragment(), getString(R.string.alarm_period), NAVIGATION_BACK);
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
			//For Cloud's pair/unpair
            String uuid = mStudents.get(mCurrentStudentIdx).getUuid();

			if (!TextUtils.isEmpty(uuid) && mDbHelper.getWearableInfoByUuid(mDbHelper.getReadableDatabase(),uuid) != null) {
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
					mSaftyFragment.setAlertIntent(intent);
					mBottomView.setSelectedItemId(R.id.action_safty);
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
			GuardianApiClient apiClient = GuardianApiClient.getInstance(MainActivity.this);
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

	class UpdateStudentList extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... voids) {
			//sync student list
			mStudents = mDbHelper.queryChildList(mDbHelper.getReadableDatabase());
			//get student from cloud
			GuardianApiClient apiClient = GuardianApiClient.getInstance(MainActivity.this);
			JSONResponse response_childList = apiClient.getChildrenList();
			if (response_childList == null || response_childList.getReturn().getResults() == null) {
			    Log.d(TAG, "[UpdateStudentList] get child list null");
			    return null;
            }
			List<Student> studentList = Arrays.asList(response_childList.getReturn().getResults().getStudents());

			for (Student oldItem : mStudents) {
				boolean isExist = false;
				for (Student newItem : studentList) {
					if (TextUtils.equals(newItem.getStudent_id(), oldItem.getStudent_id())) {
						mDbHelper.updateChildByStudentId(mDbHelper.getWritableDatabase(), newItem);
						isExist = true;
						break;
					}
				}
				if (!isExist) {
					oldItem.setUuid("");
					mDbHelper.updateChildByStudentId(mDbHelper.getWritableDatabase(), oldItem);
				}
			}
			for (Student newItem : studentList) {
				boolean isExist = false;
				for (Student oldItem : mStudents) {
					if (TextUtils.equals(newItem.getStudent_id(), oldItem.getStudent_id())){
						isExist = true;
						break;
					}
				}
				if (!isExist) {
					mDbHelper.insertChild(mDbHelper.getWritableDatabase(), newItem);
				}
			}
			mStudents = mDbHelper.queryChildList(mDbHelper.getReadableDatabase());

			return null;
		}

		@Override
		protected void onPostExecute(Void aVoid) {
			super.onPostExecute(aVoid);
			initChildInfo();
		}
	}

	//To send unpair event to cloud
	class UnPairCloudTask extends AsyncTask<Student, Void, Void> {

		@Override
		protected Void doInBackground(Student... params) {

			Student student = params[0];
			String uuid = student.getUuid();
			mDbHelper.deleteWearableData(mDbHelper.getWritableDatabase(), uuid);

//			GuardianApiClient apiClient = GuardianApiClient.getInstance(MainActivity.this);
//			JSONResponse response = apiClient.unpairDevice(student);
//
//			if (response != null) {
//				if (response.getReturn() != null) {
//					String statusCode = response.getReturn().getResponseSummary().getStatusCode();
//					if (TextUtils.equals(statusCode, Def.RET_SUCCESS_1)) {
//                        student.setUuid("");
//                        mDbHelper.updateChildData(mDbHelper.getWritableDatabase(), student);
//					} else if (TextUtils.equals(statusCode, Def.RET_ERR_16)) {
//                        student.setUuid("");
//                        mDbHelper.updateChildData(mDbHelper.getWritableDatabase(), student);
//                    }
//                    mDbHelper.deleteWearableData(mDbHelper.getWritableDatabase(), uuid);
//				}
//			}
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

			return null;
		}
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			//call Cloud API 19 to Unpair
            new UnPairCloudTask().execute(mStudents.get(mCurrentStudentIdx));
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

	private void switchPage() {
        if (mStudents.size() > 0 && mCurrentStudentIdx < mStudents.size()) {
            Student student = mStudents.get(mCurrentStudentIdx);
            if (!TextUtils.isEmpty(student.getUuid())) {
                int pageId = getIntent().getIntExtra(Def.EXTRA_GOTO_PAGE_ID, -1);
                Log.d(TAG, "PAGE ID is " + pageId);
                if (getIntent().getIntExtra(Def.EXTRA_GOTO_PAGE_ID, -1) == Def.EXTRA_PAGE_SETTING_ID) {
                    mBottomView.setSelectedItemId(R.id.action_setting);
                } else if (getIntent().getIntExtra(Def.EXTRA_GOTO_PAGE_ID, -1) == Def.EXTRA_PAGE_APPINFO_ID) {
                    AppInfoPrivacyFragment appFragment = new AppInfoPrivacyFragment(this);
                    changeFragment(appFragment);
                } else if (isChangingToAlarmPageForBT || mCurrentFragment instanceof AlarmFragment) {
                    isChangingToAlarmPageForBT = false;
                    if (mCurrentFragment instanceof AlarmFragment) {
                        ((AlarmFragment)mCurrentFragment).startSync();
                    }
                } else {
                    if (mSaftyFragment == null) {
                        mSaftyFragment = new SafetyFragment();
                    }
                    mBottomView.setSelectedItemId(R.id.action_safty);
                }
            } else {
                int pageId = getIntent().getIntExtra(Def.EXTRA_GOTO_PAGE_ID, -1);
                Log.d(TAG, "PAGE ID is " + pageId);
                if (getIntent().getIntExtra(Def.EXTRA_GOTO_PAGE_ID, -1) == Def.EXTRA_PAGE_SETTING_ID) {
                    SettingFragment settingFragment = new SettingFragment();
                    changeFragment(settingFragment);
                    mBottomView.setSelectedItemId(R.id.action_setting);
                } else if (getIntent().getIntExtra(Def.EXTRA_GOTO_PAGE_ID, -1) == Def.EXTRA_PAGE_APPINFO_ID) {
                    AppInfoPrivacyFragment appFragment = new AppInfoPrivacyFragment(this);
                    changeFragment(appFragment);
                } else {
                    SettingFragment settingFragment = new SettingFragment();
                    changeFragment(settingFragment);
                    mBottomView.setSelectedItemId(R.id.action_setting);
                }
            }
        } else {
            if (mStudents.size() == 0) {
                Intent intent = new Intent();
                intent.setClass(this, ChildInfoUpdateActivity.class);
                startActivity(intent);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
            if (mCurrentFragment instanceof AlarmFragment) {
                isChangingToAlarmPageForBT = true;
            }
    }
}
