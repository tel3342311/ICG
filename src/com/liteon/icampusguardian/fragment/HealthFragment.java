package com.liteon.icampusguardian.fragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import com.liteon.icampusguardian.R;
import com.liteon.icampusguardian.util.HealthyItem;
import com.liteon.icampusguardian.util.HealthyItemAdapter;
import com.liteon.icampusguardian.util.HealthyItemAdapter.ViewHolder.IHealthViewHolderClicks;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout.LayoutParams;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

public class HealthFragment extends Fragment {

	private static ArrayList<HealthyItem> myDataset = new ArrayList<>();
	private RecyclerView mRecyclerView;
	private RecyclerView.Adapter mAdapter;
	private RecyclerView.LayoutManager mLayoutManager;
	private IHealthViewHolderClicks mOnItemClickListener;
	private View mRootView;
	public HealthFragment(IHealthViewHolderClicks listener) {
		super();
		mOnItemClickListener = listener;
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mRootView = inflater.inflate(R.layout.fragment_healthy, container, false);
		findView(mRootView);
		initRecycleView();
		return mRootView;
	}

	private void initRecycleView() {

		mRecyclerView.setHasFixedSize(true);
		mLayoutManager = new LinearLayoutManager(getContext());
		mRecyclerView.setLayoutManager(mLayoutManager);
		testData();
		mAdapter = new HealthyItemAdapter(myDataset, mOnItemClickListener);
		mRecyclerView.setAdapter(mAdapter);
	}

	private void testData() {
		if (myDataset.size() == 0) {
			for (HealthyItem.TYPE type : HealthyItem.TYPE.values()) {
				HealthyItem item = new HealthyItem();
				item.setItemType(type);
				item.setValue((int) System.currentTimeMillis() % 5000); 
				myDataset.add(item);
			}
		}
	}

	private void findView(View rootView) {
		mRecyclerView = (RecyclerView) rootView.findViewById(R.id.healthy_event_view);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		showSyncWindow();
	}
	
	private void showSyncWindow() {
		LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View contentview = inflater.inflate(R.layout.component_popup, null);
		contentview.setFocusable(true);
		contentview.setFocusableInTouchMode(true);
		final TextView title = (TextView) contentview.findViewById(R.id.title);
		AppCompatButton button = (AppCompatButton) contentview.findViewById(R.id.button_sync);
		button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				title.setText("同步中");
				Handler handler= new Handler();
				Runnable runnable = new Runnable(){
					   @Override
					   public void run() {
						   SimpleDateFormat sdf = new SimpleDateFormat("已於dd/MM HHmm 更新");
						   String currentDateandTime = sdf.format(Calendar.getInstance().getTime());
						   title.setText(currentDateandTime);
					} 
				};
				handler.postDelayed(runnable, 2000);
			}
		});
		final PopupWindow popupWindow = new PopupWindow(contentview, LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
		popupWindow.setFocusable(true);
		popupWindow.setOutsideTouchable(false);
		contentview.setOnKeyListener(new OnKeyListener() {
		    @Override
		    public boolean onKey(View v, int keyCode, KeyEvent event) {
		        if (keyCode == KeyEvent.KEYCODE_BACK) {
		            popupWindow.dismiss();
		                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 
		            return true;
		        }
		        return false;
		    }
		});
		View  toolbar = getActivity().findViewById(R.id.toolbar);
		popupWindow.showAsDropDown(toolbar);
	}
}
