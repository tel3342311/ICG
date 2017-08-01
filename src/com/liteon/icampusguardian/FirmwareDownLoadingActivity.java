package com.liteon.icampusguardian;

import org.w3c.dom.Text;

import com.liteon.icampusguardian.util.ConfirmDeleteDialog;
import com.liteon.icampusguardian.util.CustomDialog;
import com.liteon.icampusguardian.util.Def;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatRadioButton;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ProgressBar;
import android.widget.TextView;

public class FirmwareDownLoadingActivity extends AppCompatActivity {

	private TextView mUpdateText;
	private ProgressBar mProgressBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_firmware_download);
		findViews();
		setListener();
	}
	
	private void findViews() {
		mProgressBar = (ProgressBar) findViewById(R.id.loading_progress);
		mUpdateText = (TextView) findViewById(R.id.update_text);
	}
	
	private void setListener() {

	}
	
	@Override
	protected void onResume() {
		super.onResume();
		mProgressBar.setProgress(0);
		new UpdateTask().execute("");
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		
	}
    
    class UpdateTask extends AsyncTask<String, Integer, Boolean> {

		@Override
		protected void onPreExecute() {
			mUpdateText.setText("傳送新版韌體至手機...");
		}
		
		@Override
		protected void onProgressUpdate(Integer... values) {
			mProgressBar.setProgress(values[0]);
			super.onProgressUpdate(values);
		}
		
        protected Boolean doInBackground(String... args) {
        	
        	//TODO add ble connection function
        	int progress =0;
            while(progress<=100){
                try {
                       Thread.sleep(30);
                   } catch (InterruptedException e) {
                       e.printStackTrace();
                   }
                publishProgress(Integer.valueOf(progress));
                progress++;
            }
        	return Boolean.TRUE;
        }

        protected void onPostExecute(Boolean success) {
        	setResult(RESULT_CANCELED);
        	finish();
        }
    }
}
