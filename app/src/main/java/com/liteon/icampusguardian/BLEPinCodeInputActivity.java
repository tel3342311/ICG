package com.liteon.icampusguardian;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import com.liteon.icampusguardian.db.DBHelper;
import com.liteon.icampusguardian.util.ConfirmDeleteDialog;
import com.liteon.icampusguardian.util.CustomDialog;
import com.liteon.icampusguardian.util.Def;
import com.liteon.icampusguardian.util.GuardianApiClient;
import com.liteon.icampusguardian.util.JSONResponse;
import com.liteon.icampusguardian.util.JSONResponse.Student;

import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import static com.liteon.icampusguardian.App.getContext;

public class BLEPinCodeInputActivity extends AppCompatActivity implements View.OnFocusChangeListener, View.OnKeyListener, TextWatcher {

	private ImageView mConfirm;
	private ImageView mCancel;
	private EditText mPinFirstDigitEditText;
    private EditText mPinSecondDigitEditText;
    private EditText mPinThirdDigitEditText;
    private EditText mPinForthDigitEditText;
    private EditText mPinFifthDigitEditText;
    private EditText mPinSixthDigitEditText;
    private EditText mPinHiddenEditText;
    private View mBleConnectingView;
    private ConfirmDeleteDialog mBLEFailConfirmDialog;
	private DBHelper mDbHelper;
	private List<Student> mStudents;
	private int mCurrnetStudentIdx;
    // Unique UUID for this application
    private static final UUID MY_UUID_SECURE =
            UUID.fromString("1eafc0af-8dd6-40b2-8114-26163835c38d");
    private static final UUID MY_UUID_INSECURE =
            UUID.fromString("1eafc0af-8dd6-40b2-8114-26163835c38d");
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.activity_ble_pin_input);
		setContentView(new MainLayout(this, null));
		findViews();
		setListener();
		mDbHelper = DBHelper.getInstance(this);
		//get child list
		mStudents = mDbHelper.queryChildList(mDbHelper.getReadableDatabase());
	}

	@Override
	protected void onResume() {
		super.onResume();
		SharedPreferences sp = getSharedPreferences(Def.SHARE_PREFERENCE, Context.MODE_PRIVATE);
		mCurrnetStudentIdx = sp.getInt(Def.SP_CURRENT_STUDENT, 0);
	}
	
	private void findViews() {
		mCancel = (ImageView) findViewById(R.id.cancel);
		mConfirm = (ImageView) findViewById(R.id.confirm);
		mPinFirstDigitEditText = (EditText) findViewById(R.id.pin_first_edittext);
        mPinSecondDigitEditText = (EditText) findViewById(R.id.pin_second_edittext);
        mPinThirdDigitEditText = (EditText) findViewById(R.id.pin_third_edittext);
        mPinForthDigitEditText = (EditText) findViewById(R.id.pin_forth_edittext);
        mPinFifthDigitEditText = (EditText) findViewById(R.id.pin_fifth_edittext);
        mPinSixthDigitEditText = (EditText) findViewById(R.id.pin_sixth_edittext);
        mPinHiddenEditText = (EditText) findViewById(R.id.pin_hidden_edittext);
        mBleConnectingView = (View) findViewById(R.id.ble_pairing_progress);
 	}
	
	private void setListener() {
		mCancel.setOnClickListener(mOnCancelClickListener);
		mConfirm.setOnClickListener(mOnConfirmClickListener);
		mPinFirstDigitEditText.setOnFocusChangeListener(this);
        mPinSecondDigitEditText.setOnFocusChangeListener(this);
        mPinThirdDigitEditText.setOnFocusChangeListener(this);
        mPinForthDigitEditText.setOnFocusChangeListener(this);
        mPinFifthDigitEditText.setOnFocusChangeListener(this);
        mPinSixthDigitEditText.setOnFocusChangeListener(this);
        mPinHiddenEditText.setOnFocusChangeListener(this);
        
        mPinFirstDigitEditText.setOnKeyListener(this);
        mPinSecondDigitEditText.setOnKeyListener(this);
        mPinThirdDigitEditText.setOnKeyListener(this);
        mPinForthDigitEditText.setOnKeyListener(this);
        mPinFifthDigitEditText.setOnKeyListener(this);
        mPinSixthDigitEditText.setOnKeyListener(this);
        mPinHiddenEditText.setOnKeyListener(this);
        
        mPinHiddenEditText.addTextChangedListener(this);
	}
	
	public void setViewBackground(View view, Drawable background) {
        if (view == null || background == null) {
        	return ;
        }
        view.setBackground(background);
    }
	
	public void hideSoftKeyboard(EditText editText) {
        if (editText == null)
            return;

        InputMethodManager imm = (InputMethodManager) getSystemService(Service.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }
	
	private View.OnClickListener mOnCancelClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			onBackPressed();
		}
	};
	
	private View.OnClickListener mOnConfirmClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			new ConnectBleTask().execute();
			
		}
	};
	
	class ConnectBleTask extends AsyncTask<String, Void, Boolean> {

		@Override
		protected void onPreExecute() {
			mBleConnectingView.setVisibility(View.VISIBLE);
		}
		
        protected Boolean doInBackground(String... args) {
        	
        	//TODO add ble connection function
        	try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
        	GuardianApiClient mApiClient = new GuardianApiClient(BLEPinCodeInputActivity.this);
        	JSONResponse response = mApiClient.pairNewDevice(mStudents.get(mCurrnetStudentIdx));
        	if (response != null) {
        		String statusCode = response.getReturn().getResponseSummary().getStatusCode();
        		if (!TextUtils.equals(statusCode, Def.RET_SUCCESS_1)) {
        			Student student = mStudents.get(mCurrnetStudentIdx);
        			student.setUuid("");
        			mDbHelper.updateChildData(mDbHelper.getWritableDatabase(), student);
        		}
        	}
        	return Boolean.TRUE;
        }

        protected void onPostExecute(Boolean success) {
        	mBleConnectingView.setVisibility(View.INVISIBLE);
        	if (success == true) {
        		CustomDialog dialog = new CustomDialog();
        		dialog.setTitle(mPinHiddenEditText.getText() + "(PIN碼)配對成功\n已綁定為智慧手錶");
        		dialog.setIcon(0);
        		dialog.setBtnText("好");
        		dialog.setBtnConfirm(mOnBLEFailCancelClickListener);
        		dialog.show(getSupportFragmentManager(), "dialog_fragment");
        	} else {
        		mBLEFailConfirmDialog = new ConfirmDeleteDialog();
        		mBLEFailConfirmDialog.setOnConfirmEventListener(mOnBLEFailConfirmClickListener);
        		mBLEFailConfirmDialog.setmOnCancelListener(mOnBLEFailCancelClickListener);
        		mBLEFailConfirmDialog.setmTitleText(getString(R.string.pairing_watch_pin_error));
        		mBLEFailConfirmDialog.setmBtnConfirmText(getString(R.string.pairing_watch_pair));
        		mBLEFailConfirmDialog.setmBtnCancelText(getString(R.string.pairing_watch_later));
        		mBLEFailConfirmDialog.show(getSupportFragmentManager(), "dialog_fragment");
        	}
        }
    }
	
	private OnClickListener mOnBLEFailConfirmClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			mBLEFailConfirmDialog.dismiss();
			mPinFirstDigitEditText.setText("");
	        mPinSecondDigitEditText.setText("");
	        mPinThirdDigitEditText.setText("");
	        mPinForthDigitEditText.setText("");
            mPinFifthDigitEditText.setText("");
            mPinSixthDigitEditText.setText("");
			mPinHiddenEditText.setText("");
			mPinFirstDigitEditText.requestFocus();
		}
	};

	private OnClickListener mOnBLEFailCancelClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			finish();
			Intent intent = new Intent();
			intent.setClass(BLEPinCodeInputActivity.this, MainActivity.class);
			intent.putExtra(Def.EXTRA_GOTO_MAIN_SETTING, true);
			startActivity(intent);
		}
	};
	
	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		setDefaultPinBackground(mPinFirstDigitEditText);
        setDefaultPinBackground(mPinSecondDigitEditText);
        setDefaultPinBackground(mPinThirdDigitEditText);
        setDefaultPinBackground(mPinForthDigitEditText);
        setDefaultPinBackground(mPinFifthDigitEditText);
        setDefaultPinBackground(mPinSixthDigitEditText);

        if (s.length() == 0) {
            setFocusedPinBackground(mPinFirstDigitEditText);
            mPinFirstDigitEditText.setText("");
        } else if (s.length() == 1) {
            setFocusedPinBackground(mPinSecondDigitEditText);
            mPinFirstDigitEditText.setText(s.charAt(0) + "");
            mPinSecondDigitEditText.setText("");
            mPinThirdDigitEditText.setText("");
            mPinForthDigitEditText.setText("");
            mPinFifthDigitEditText.setText("");
            mPinSixthDigitEditText.setText("");
        } else if (s.length() == 2) {
            setFocusedPinBackground(mPinThirdDigitEditText);
            mPinSecondDigitEditText.setText(s.charAt(1) + "");
            mPinThirdDigitEditText.setText("");
            mPinForthDigitEditText.setText("");
            mPinFifthDigitEditText.setText("");
            mPinSixthDigitEditText.setText("");
        } else if (s.length() == 3) {
        	setFocusedPinBackground(mPinForthDigitEditText);
            mPinThirdDigitEditText.setText(s.charAt(2) + "");
            mPinForthDigitEditText.setText("");
            mPinFifthDigitEditText.setText("");
            mPinSixthDigitEditText.setText("");
        } else if (s.length() == 4) {
            setDefaultPinBackground(mPinFifthDigitEditText);
            mPinForthDigitEditText.setText(s.charAt(3) + "");
            mPinFifthDigitEditText.setText("");
            mPinSixthDigitEditText.setText("");

        } else if (s.length() == 5) {
            setDefaultPinBackground(mPinSixthDigitEditText);
            mPinFifthDigitEditText.setText(s.charAt(4) + "");
            mPinSixthDigitEditText.setText("");


        } else if (s.length() == 6) {
            setDefaultPinBackground(mPinSixthDigitEditText);
            mPinSixthDigitEditText.setText(s.charAt(5) + "");

            hideSoftKeyboard(mPinSixthDigitEditText);
        }
	}

	@Override
	public void afterTextChanged(Editable s) {
		
	}

	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_DOWN) {
            final int id = v.getId();
            switch (id) {
                case R.id.pin_hidden_edittext:
                    if (keyCode == KeyEvent.KEYCODE_DEL) {
                        if (mPinHiddenEditText.getText().length() == 6)
                            mPinSixthDigitEditText.setText("");
                        else if (mPinHiddenEditText.getText().length() == 5)
                            mPinFifthDigitEditText.setText("");
                        else if (mPinHiddenEditText.getText().length() == 4)
                            mPinForthDigitEditText.setText("");
                        else if (mPinHiddenEditText.getText().length() == 3)
                            mPinThirdDigitEditText.setText("");
                        else if (mPinHiddenEditText.getText().length() == 2)
                            mPinSecondDigitEditText.setText("");
                        else if (mPinHiddenEditText.getText().length() == 1)
                            mPinFirstDigitEditText.setText("");

                        if (mPinHiddenEditText.length() > 0)
                            mPinHiddenEditText.setText(mPinHiddenEditText.getText().subSequence(0, mPinHiddenEditText.length() - 1));

                        
                        return true;
                    }

                    break;

                default:
                    return false;
            }
        }

        return false;
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		final int id = v.getId();
        switch (id) {

            case R.id.pin_first_edittext:
            case R.id.pin_second_edittext:
            case R.id.pin_third_edittext:
            case R.id.pin_forth_edittext:
            case R.id.pin_fifth_edittext:
            case R.id.pin_sixth_edittext:
                if (hasFocus) {
                    setFocus(mPinHiddenEditText);
                    showSoftKeyboard(mPinHiddenEditText);
                }
                break;
            default:
                break;	
        }
	}
	
	public void showSoftKeyboard(EditText editText) {
        if (editText == null)
            return;

        InputMethodManager imm = (InputMethodManager) getSystemService(Service.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editText, 0);
    }
	
	private void setDefaultPinBackground(EditText editText) {

        setViewBackground(editText, ContextCompat.getDrawable(getContext(), R.drawable.btn_bg));
    }
	
	private void setFocusedPinBackground(EditText editText) {
        setViewBackground(editText, ContextCompat.getDrawable(getContext(), R.drawable.pin_bg_selected));
    }
	
	public static void setFocus(EditText editText) {
        if (editText == null)
            return;

        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();
    }
	
	/**
     * Overridden onMeasure() method
     * for handling software keyboard show and hide events.
     */
	public class MainLayout extends RelativeLayout {

        public MainLayout(Context context, AttributeSet attributeSet) {
            super(context, attributeSet);
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            inflater.inflate(R.layout.activity_ble_pin_input, this);
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            final int proposedHeight = MeasureSpec.getSize(heightMeasureSpec);
            final int actualHeight = getHeight();

            Log.d("TAG", "proposed: " + proposedHeight + ", actual: " + actualHeight);

            if (actualHeight >= proposedHeight) {
                // Keyboard is shown
                if (mPinHiddenEditText.length() == 0) {
                    setFocusedPinBackground(mPinFirstDigitEditText);
                    View view = BLEPinCodeInputActivity.this.getCurrentFocus();
                    if (view != null) {  
                        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.showSoftInput(view, 0);
                    }
                } else {
                    setDefaultPinBackground(mPinFirstDigitEditText);
                }
            }

            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }
}
