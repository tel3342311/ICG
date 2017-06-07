package com.liteon.icampusguardian;

import com.liteon.icampusguardian.fragment.SafetyFragment;
import com.liteon.icampusguardian.util.BottomNavigationViewHelper;

import android.R.raw;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.BottomNavigationView.OnNavigationItemSelectedListener;
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
			switch (item.getItemId()) {
			case R.id.action_safty:
				android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
			    android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
			    SafetyFragment fragment = new SafetyFragment();
			    fragmentTransaction.replace(R.id.container, fragment);
			    fragmentTransaction.commit();
				break;
			case R.id.action_health:
				break;
			case R.id.action_eating:
				break;
			case R.id.action_alarm:
				break;
			case R.id.action_setting:
				break;
			}
			return false;
		}
	};
}
