package com.liteon.icampusguardian;

import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.BottomNavigationView.OnNavigationItemSelectedListener;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.liteon.icampusguardian.fragment.AlarmFragment;
import com.liteon.icampusguardian.fragment.DailyHealthFragment;
import com.liteon.icampusguardian.fragment.HealthFragment;
import com.liteon.icampusguardian.fragment.SafetyFragment;
import com.liteon.icampusguardian.fragment.SettingFragment;
import com.liteon.icampusguardian.util.BottomNavigationViewHelper;
import com.liteon.icampusguardian.util.CircularImageView;
import com.liteon.icampusguardian.util.HealthyItem.TYPE;
import com.liteon.icampusguardian.util.HealthyItemAdapter.ViewHolder.IHealthViewHolderClicks;

public class MainActivity extends AppCompatActivity implements IHealthViewHolderClicks {

	private CircularImageView mChildIcon;
	private EditText mChildName;
	private Toolbar mToolbar;
	private BottomNavigationView mBottomView;
	private DrawerLayout mDrawerLayout;
	private ActionBarDrawerToggle mDrawerToggle;
	private NavigationView mNavigationView;
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
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.open, R.string.close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);

            }
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };
        mDrawerToggle.syncState();
        mDrawerLayout.addDrawerListener(mDrawerToggle);
	}

	private void findViews() {
		mToolbar = (Toolbar) findViewById(R.id.toolbar);
		mBottomView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mNavigationView = (NavigationView) findViewById(R.id.navigation);
		mChildIcon = (CircularImageView) mNavigationView.getHeaderView(0).findViewById(R.id.child_icon);
		mChildName = (EditText) mNavigationView.getHeaderView(0).findViewById(R.id.child_name);
	}
	
	private void setListener() {
		mBottomView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
	}
	
	private OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = new OnNavigationItemSelectedListener() {
		
		@Override
		public boolean onNavigationItemSelected(MenuItem item) {
			Fragment fragment = null;
			switch (item.getItemId()) {
				case R.id.action_safty:
				    fragment = new SafetyFragment();
					break;
				case R.id.action_health:
					fragment = new HealthFragment(MainActivity.this);
					break;
				case R.id.action_alarm:
					fragment = new AlarmFragment();
					break;
				case R.id.action_setting:
					fragment = new SettingFragment();
					break;
			}
			if (fragment == null) {
				return false;
			}
			changeFragment(fragment);
			return true;
		}
	};
	
	private void changeFragment(Fragment frag) {
		FragmentManager fragmentManager = getSupportFragmentManager();
	    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
	    fragmentTransaction.replace(R.id.container, frag);
	    fragmentTransaction.commit();
	}

	@Override
	public void onClick(TYPE type) {
		Fragment fragment = new DailyHealthFragment(type);
		changeFragment(fragment);
	}
	
	private void initChildInfo() {
		mChildIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_launcher, null));
		mChildIcon.setBorderColor(getResources().getColor(R.color.md_white_1000));
		mChildIcon.setBorderWidth(10);
		mChildIcon.setSelectorColor(getResources().getColor(R.color.md_blue_400));
		mChildIcon.setSelectorStrokeColor(getResources().getColor(R.color.md_blue_800));
		mChildIcon.setSelectorStrokeWidth(10);
		mChildIcon.addShadow();
		mChildName.setText("王小明");
	}
}
