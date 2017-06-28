package com.liteon.icampusguardian;

import com.liteon.icampusguardian.fragment.AlarmEditingFragment;
import com.liteon.icampusguardian.fragment.AlarmFragment;
import com.liteon.icampusguardian.fragment.AlarmFragment.IAddAlarmClicks;
import com.liteon.icampusguardian.fragment.AlarmPeriodFragment;
import com.liteon.icampusguardian.fragment.DailyHealthFragment;
import com.liteon.icampusguardian.fragment.HealthFragment;
import com.liteon.icampusguardian.fragment.SafetyFragment;
import com.liteon.icampusguardian.fragment.SettingFragment;
import com.liteon.icampusguardian.util.AlarmItem;
import com.liteon.icampusguardian.util.AlarmPeriodAdapter.ViewHolder.IAlarmPeriodViewHolderClicks;
import com.liteon.icampusguardian.util.AlarmPeriodItem;
import com.liteon.icampusguardian.util.BottomNavigationViewHelper;
import com.liteon.icampusguardian.util.CircularImageView;
import com.liteon.icampusguardian.util.HealthyItem.TYPE;
import com.liteon.icampusguardian.util.HealthyItemAdapter.ViewHolder.IHealthViewHolderClicks;

import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.BottomNavigationView.OnNavigationItemSelectedListener;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements IAddAlarmClicks, IHealthViewHolderClicks, IAlarmPeriodViewHolderClicks, NavigationView.OnNavigationItemSelectedListener {

	private CircularImageView mChildIcon;
	private TextView mChildName;
	private Toolbar mToolbar;
	private BottomNavigationView mBottomView;
	private DrawerLayout mDrawerLayout;
	private NavigationView mNavigationView;
	private Fragment mCurrentFragment;
	private static final int NAVIGATION_DRAWER = 1;
	private static final int NAVIGATION_BACK = 2;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		findViews();
		setListener();
		setupToolbar();
		initChildInfo();
		BottomNavigationViewHelper.disableShiftMode(mBottomView);
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
		changeFragment(new SafetyFragment());
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
			} else if (mCurrentFragment instanceof AlarmFragment){
				if (((AlarmFragment) mCurrentFragment).isEditMode()) {
					((AlarmFragment) mCurrentFragment).exitEditMode();
					return;
				}
			} else if (mCurrentFragment instanceof AlarmEditingFragment){
				mBottomView.setSelectedItemId(R.id.action_alarm);
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
	}
	
	private void setListener() {
		mBottomView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
		mNavigationView.setNavigationItemSelectedListener(this);
	}
	
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
		mChildIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_launcher, null));
		mChildIcon.setBorderColor(getResources().getColor(R.color.md_white_1000, null));
		mChildIcon.setBorderWidth(10);
		mChildIcon.setSelectorColor(getResources().getColor(R.color.md_blue_400, null));
		mChildIcon.setSelectorStrokeColor(getResources().getColor(R.color.md_blue_800, null));
		mChildIcon.setSelectorStrokeWidth(10);
		mChildIcon.addShadow();
		mChildName.setText("王小明");
	}

	@Override
	public boolean onNavigationItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_switch_account) {
			
		} else if (id == R.id.action_add_child) {
			
		} else if (id == R.id.action_delete_account) {
			
		} else if (id == R.id.action_setting) {
			
		}
		mDrawerLayout.closeDrawers();
		return true;
	}

	@Override
	public void onAddAlarmClick() {
		changeFragment(new AlarmEditingFragment(this), "設定鬧鈴", 0);
	}

	@Override
	public void onEditAlarm(int idx) {
		changeFragment(new AlarmEditingFragment(idx, this),"設定鬧鈴", 0);
	}

	@Override
	public void onClick(AlarmPeriodItem item) {
		if (item.getItemType() == AlarmPeriodItem.TYPE.CUSTOMIZE) {
			//TODO get current AlarmItem
			AlarmItem alarmItem = new AlarmItem();
			alarmItem.setPeriodItem(item);
			changeFragment(new AlarmPeriodFragment(alarmItem), "設定鬧鈴週期", NAVIGATION_BACK);
		}
	}
}
