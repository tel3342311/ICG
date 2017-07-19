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

public class BLEPairingListActivity extends AppCompatActivity implements IBLEItemClickListener {

	private RecyclerView mRecyclerView;
	private RecyclerView.Adapter mAdapter;
	private RecyclerView.LayoutManager mLayoutManager;
	private List<BLEItem> mDataSet;
	private Toolbar mToolbar;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ble_pairing_list);
		findViews();
		setupToolbar();
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
		
	}

	@Override
	protected void onResume() {
		super.onResume();
		mToolbar.setTitle("請選擇配對的智慧手錶");
	}
	
	private void setupToolbar() {
		setSupportActionBar(mToolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);
		mToolbar.setNavigationIcon(R.drawable.ic_navigate_before_white_24dp);
		mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				onBackPressed();
			}
		});
	}
	
	private void findViews() {
		mToolbar = (Toolbar) findViewById(R.id.toolbar);
		mRecyclerView = (RecyclerView) findViewById(R.id.profile_view);
	}
	
	private void setListener() {
		
	}

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
		// TODO Auto-generated method stub
		
	}
}
