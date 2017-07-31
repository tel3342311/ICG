package com.liteon.icampusguardian;

import com.liteon.icampusguardian.util.ConfirmDeleteDialog;
import com.liteon.icampusguardian.util.CustomDialog;
import com.liteon.icampusguardian.util.Def;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
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

public class BLEPinCodeInputActivity extends AppCompatActivity implements View.OnFocusChangeListener, View.OnKeyListener, TextWatcher {

	private ImageView mConfirm;
	private ImageView mCancel;
	private EditText mPinFirstDigitEditText;
    private EditText mPinSecondDigitEditText;
    private EditText mPinThirdDigitEditText;
    private EditText mPinForthDigitEditText;
    private EditText mPinHiddenEditText;
    private View mBleConnectingView;
    private ConfirmDeleteDialog mBLEFailConfirmDialog;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.activity_ble_pin_input);
		setContentView(new MainLayout(this, null));
		findViews();
		setListener();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}
	
	private void findViews() {
		mCancel = (ImageView) findViewById(R.id.cancel);
		mConfirm = (ImageView) findViewById(R.id.confirm);
		mPinFirstDigitEditText = (EditText) findViewById(R.id.pin_first_edittext);
        mPinSecondDigitEditText = (EditText) findViewById(R.id.pin_second_edittext);
        mPinThirdDigitEditText = (EditText) findViewById(R.id.pin_third_edittext);
        mPinForthDigitEditText = (EditText) findViewById(R.id.pin_forth_edittext);
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
        mPinHiddenEditText.setOnFocusChangeListener(this);
        
        mPinFirstDigitEditText.setOnKeyListener(this);
        mPinSecondDigitEditText.setOnKeyListener(this);
        mPinThirdDigitEditText.setOnKeyListener(this);
        mPinForthDigitEditText.setOnKeyListener(this);
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
        		mBLEFailConfirmDialog.setmTitleText(mPinHiddenEditText.getText() + "(PIN碼)配對失敗");
        		mBLEFailConfirmDialog.setmBtnConfirmText("重新輸入");
        		mBLEFailConfirmDialog.setmBtnCancelText("之後再配對");
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

        if (s.length() == 0) {
            setFocusedPinBackground(mPinFirstDigitEditText);
            mPinFirstDigitEditText.setText("");
        } else if (s.length() == 1) {
            setFocusedPinBackground(mPinSecondDigitEditText);
            mPinFirstDigitEditText.setText(s.charAt(0) + "");
            mPinSecondDigitEditText.setText("");
            mPinThirdDigitEditText.setText("");
            mPinForthDigitEditText.setText("");
        } else if (s.length() == 2) {
            setFocusedPinBackground(mPinThirdDigitEditText);
            mPinSecondDigitEditText.setText(s.charAt(1) + "");
            mPinThirdDigitEditText.setText("");
            mPinForthDigitEditText.setText("");
        } else if (s.length() == 3) {
        	setFocusedPinBackground(mPinForthDigitEditText);
            mPinThirdDigitEditText.setText(s.charAt(2) + "");
            mPinForthDigitEditText.setText("");
        } else if (s.length() == 4) {
            setDefaultPinBackground(mPinForthDigitEditText);
            mPinForthDigitEditText.setText(s.charAt(3) + "");

            hideSoftKeyboard(mPinForthDigitEditText);
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
                        if (mPinHiddenEditText.getText().length() == 4)
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
        setViewBackground(editText, getResources().getDrawable(R.drawable.btn_bg, null));
    }
	
	private void setFocusedPinBackground(EditText editText) {
        setViewBackground(editText, getResources().getDrawable(R.drawable.pin_bg_selected, null));
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
