package com.liteon.icampusguardian;

import com.liteon.icampusguardian.fragment.AlarmFragment;
import com.liteon.icampusguardian.fragment.HealthFragment;
import com.liteon.icampusguardian.fragment.SafetyFragment;
import com.liteon.icampusguardian.fragment.SettingFragment;
import com.liteon.icampusguardian.util.BottomNavigationViewHelper;

import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.BottomNavigationView.OnNavigationItemSelectedListener;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

	private BottomNavigationView mBottomView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		findViews();
		setListener();
		BottomNavigationViewHelper.disableShiftMode(mBottomView);
	}
	
	private void findViews() {
		mBottomView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
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
					fragment = new HealthFragment();
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
}
