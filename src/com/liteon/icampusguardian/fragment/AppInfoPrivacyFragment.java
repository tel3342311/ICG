package com.liteon.icampusguardian.fragment;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import com.liteon.icampusguardian.R;
import com.liteon.icampusguardian.util.AppInfoPrivacyItem;
import com.liteon.icampusguardian.util.AppInfoPrivacyItemAdapter;
import com.liteon.icampusguardian.util.AppInfoPrivacyItemAdapter.ViewHolder.IAppInfoPrivacyViewHolderClicks;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class AppInfoPrivacyFragment extends Fragment {

	private static ArrayList<AppInfoPrivacyItem> myDataset = new ArrayList<>();
	private RecyclerView mRecyclerView;
	private RecyclerView.Adapter mAdapter;
	private RecyclerView.LayoutManager mLayoutManager;
	private View mRootView;
	private WeakReference<IAppInfoPrivacyViewHolderClicks> mClicks;
	
	public AppInfoPrivacyFragment(IAppInfoPrivacyViewHolderClicks clicks) {
		mClicks = new WeakReference<IAppInfoPrivacyViewHolderClicks>(clicks);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		 mRootView = inflater.inflate(R.layout.fragment_app_info_privacy, container, false);
		 findView(mRootView);
		 initRecycleView();
		 return mRootView;
	}
	
	private void findView(View rootView) {
		mRecyclerView = (RecyclerView) rootView.findViewById(R.id.app_info_view);
	}
	
	public void initRecycleView() {
		mRecyclerView.setHasFixedSize(true);
		mLayoutManager = new LinearLayoutManager(getContext());
		mRecyclerView.setLayoutManager(mLayoutManager);
		setupData();
		PackageInfo pInfo;
		try {
			pInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
			String version = pInfo.versionName;
			mAdapter = new AppInfoPrivacyItemAdapter(myDataset, mClicks.get(), version);
			mRecyclerView.setAdapter(mAdapter);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		
	}
	
	private void setupData(){
		if (myDataset.size() == 0) {
			for (AppInfoPrivacyItem.TYPE type : AppInfoPrivacyItem.TYPE.values()) {
				AppInfoPrivacyItem item = new AppInfoPrivacyItem();
				item.setItemType(type);
				myDataset.add(item);
			}
		}
	}

}
