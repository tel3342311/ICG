package com.liteon.icampusguardian;

import java.util.ArrayList;
import java.util.List;

import com.liteon.icampusguardian.util.BLEItem;
import com.liteon.icampusguardian.util.BLEItemAdapter;
import com.liteon.icampusguardian.util.BLEItemAdapter.ViewHolder.IBLEItemClickListener;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

public class BLEPairingListActivity extends AppCompatActivity implements IBLEItemClickListener {

	private RecyclerView mRecyclerView;
	private RecyclerView.Adapter mAdapter;
	private RecyclerView.LayoutManager mLayoutManager;
	private List<BLEItem> mDataSet;
	private ImageView mCancel;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ble_pairing_list);
		findViews();
		setListener();
		initRecycleView();
	}
	
	private void initRecycleView() {
		mRecyclerView.setHasFixedSize(true);
		mLayoutManager = new LinearLayoutManager(this);
		mRecyclerView.setLayoutManager(mLayoutManager);
		setupData();
		mAdapter = new BLEItemAdapter(mDataSet, this);
		mRecyclerView.setAdapter(mAdapter);
	}
	
	private void setupData(){
		mDataSet = new ArrayList<>();
		BLEItem item = new BLEItem();
		item.setId("0000-0000-0000-0000");
		item.setName("iCampus Guardian");
		item.setValue("Not Connected");
		mDataSet.add(item);
	}

	@Override
	protected void onResume() {
		super.onResume();
	}
	
	private void findViews() {
		mRecyclerView = (RecyclerView) findViewById(R.id.profile_view);
		mCancel = (ImageView) findViewById(R.id.cancel);
		
 	}
	
	private void setListener() {
		mCancel.setOnClickListener(mOnCancelClickListener);
	}
	
	private View.OnClickListener mOnCancelClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			onBackPressed();
		}
	};
	
	class UpdateInfoTask extends AsyncTask<String, Void, String> {

		@Override
		protected void onPreExecute() {


		}
		
        protected String doInBackground(String... args) {
        	

        	return null;
        }

        protected void onPostExecute(String token) {
        	Intent intent = new Intent();
        	intent.setClass(BLEPairingListActivity.this, ChildPairingActivity.class);
        	startActivity(intent);
        }
    }

	@Override
	public void onBleItemClick(BLEItem item) {
		Intent intent = new Intent();
		intent.setClass(this, BLEPinCodeInputActivity.class);
		startActivity(intent);
	}
}
