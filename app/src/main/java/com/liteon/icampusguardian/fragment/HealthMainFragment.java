package com.liteon.icampusguardian.fragment;

import com.liteon.icampusguardian.MainActivity;
import com.liteon.icampusguardian.R;
import com.liteon.icampusguardian.util.HealthyItem.TYPE;
import com.liteon.icampusguardian.util.HealthyItemAdapter.ViewHolder.IHealthViewHolderClicks;

import android.content.Context;
import android.icu.text.DisplayContext.Type;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class HealthMainFragment extends Fragment implements IHealthViewHolderClicks {

	private View mRootView;
	private ViewPager mViewPager;
	private Toolbar mToolbar;
	private TextView mTitleView;
	private DrawerLayout mDrawerLayout;
    private static final int PAGE_COUNT = 9;
    private static final int HEALTHY_MAIN = 0;
    private static final int HEALTHY_ACTIVITY = 1;
    private static final int HEALTHY_CALORIES = 2;
    private static final int HEALTHY_STEPS = 3;
    private static final int HEALTHY_WALKING = 4;
    private static final int HEALTHY_RUNNING = 5;
    private static final int HEALTHY_CYCLING = 6;
    private static final int HEALTHY_HEART_RATE = 7;
    private static final int HEALTHY_SLEEPING = 8;
    
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mRootView = inflater.inflate(R.layout.fragment_healthy_main, container, false);
		findViews();
		setupViewPager();
		return mRootView;
	}
	
	private void findViews() {
		mViewPager = (ViewPager) mRootView.findViewById(R.id.view_pager);
		mToolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
		mTitleView = (TextView) getActivity().findViewById(R.id.toolbar_title);
		mDrawerLayout = (DrawerLayout) getActivity().findViewById(R.id.drawer_layout);
	}
	
	private void setupViewPager() {
		mViewPager.setAdapter(buildAdapter());
		mViewPager.addOnPageChangeListener(mOnPageChangeListener);
	}
	private void setupTitleBar(int position) {
		//mToolbar.setTitle(mViewPager.getAdapter().getPageTitle(position));
		mTitleView.setText(mViewPager.getAdapter().getPageTitle(position));
		if (position != HEALTHY_MAIN) {
			mToolbar.setNavigationIcon(R.drawable.ic_navigate_before_white_24dp);
			mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					mViewPager.setCurrentItem(HEALTHY_MAIN);
				}
			});
		} else {
			mToolbar.setNavigationIcon(R.drawable.ic_dehaze_white_24dp);
			mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {

					mDrawerLayout.openDrawer(Gravity.LEFT);
				}
			});
		}
	}
	private OnPageChangeListener mOnPageChangeListener = new OnPageChangeListener() {
		
		@Override
		public void onPageSelected(int position) {
			setupTitleBar(position);
		}
		
		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onPageScrollStateChanged(int arg0) {
			// TODO Auto-generated method stub
			
		}
	};
	private PagerAdapter buildAdapter() {
	    return(new HealthPageAdapter(getActivity(), getChildFragmentManager()));
	}
	
	@Override
	public void onResume() {
		super.onResume();
		mViewPager.setCurrentItem(HEALTHY_MAIN);
	}
	class HealthPageAdapter extends FragmentPagerAdapter {

		
		Context mContext;
		public HealthPageAdapter(Context mCtx, FragmentManager fm) {
			super(fm);
			mContext = mCtx;
		}

		@Override
		public Fragment getItem(int position) {
			if (position == HEALTHY_MAIN) {
				return new HealthFragment();
			} else if (position == HEALTHY_ACTIVITY) {
				return new DailyHealthFragment(TYPE.ACTIVITY);
			} else if (position == HEALTHY_CALORIES) {
				return new DailyHealthFragment(TYPE.CALORIES_BURNED);
			} else if (position == HEALTHY_STEPS) {
				return new DailyHealthFragment(TYPE.TOTAL_STEPS);
			} else if (position == HEALTHY_WALKING) {
				return new DailyHealthFragment(TYPE.WALKING_TIME);
			} else if (position == HEALTHY_RUNNING) {
				return new DailyHealthFragment(TYPE.RUNNING_TIME);
			} else if (position == HEALTHY_CYCLING) {
				return new DailyHealthFragment(TYPE.CYCLING_TIME);
			} else if (position == HEALTHY_HEART_RATE) {
				return new DailyHealthFragment(TYPE.HEART_RATE);
			} else if (position == HEALTHY_SLEEPING) {
				return new DailyHealthFragment(TYPE.SLEEP_TIME);
			}
			return null;
		}

		@Override
		public int getCount() {
			return PAGE_COUNT;
		}
		@Override
		public CharSequence getPageTitle(int position) {
			if (position == HEALTHY_MAIN) {
				return getString(R.string.healthy_today_reocrd);
			} else if (position == HEALTHY_ACTIVITY) {
				return TYPE.ACTIVITY.getName();
			} else if (position == HEALTHY_CALORIES) {
				return TYPE.CALORIES_BURNED.getName();
			} else if (position == HEALTHY_STEPS) {
				return TYPE.TOTAL_STEPS.getName();
			} else if (position == HEALTHY_WALKING) {
				return TYPE.WALKING_TIME.getName();
			} else if (position == HEALTHY_RUNNING) {
				return TYPE.RUNNING_TIME.getName();
			} else if (position == HEALTHY_CYCLING) {
				return TYPE.CYCLING_TIME.getName();
			} else if (position == HEALTHY_HEART_RATE) {
				return TYPE.HEART_RATE.getName();
			} else if (position == HEALTHY_SLEEPING) {
				return TYPE.SLEEP_TIME.getName();
			}
			return super.getPageTitle(position);
		}
	}

	@Override
	public void onClick(TYPE type) {
		switch(type) {
		case ACTIVITY:
			mViewPager.setCurrentItem(HEALTHY_ACTIVITY, false);
			break;
		case CALORIES_BURNED:
			mViewPager.setCurrentItem(HEALTHY_CALORIES, false);
			break;
		case CYCLING_TIME:
			mViewPager.setCurrentItem(HEALTHY_CYCLING, false);
			break;
		case HEART_RATE:
			mViewPager.setCurrentItem(HEALTHY_HEART_RATE, false);
			break;
		case RUNNING_TIME:
			mViewPager.setCurrentItem(HEALTHY_RUNNING, false);
			break;
		case SLEEP_TIME:
			mViewPager.setCurrentItem(HEALTHY_SLEEPING, false);
			break;
		case TOTAL_STEPS:
			mViewPager.setCurrentItem(HEALTHY_STEPS, false);
			break;
		case WALKING_TIME:
			mViewPager.setCurrentItem(HEALTHY_WALKING, false);
			break;
		default:
			break;
		
		}
	}
}
