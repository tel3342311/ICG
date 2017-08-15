package com.liteon.icampusguardian.util;

import java.util.List;

import com.liteon.icampusguardian.R;
import com.liteon.icampusguardian.util.HealthyItem.TYPE;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Style;
import android.text.TextUtils;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class HealthHistogramView extends View {

	private int mWidth;
	private int mHeight;
	private Paint paintSelected;
	private Paint paintOthers;
	private Paint textPaint;
	private Paint baseLinePaint;
	private Paint paintTriangle;
	//Text size for graph
	private int textFontSize;
	//bottom dash line
	private Path mBottomPath;
	//magin left& right
	private int mGraphMarginHorizon;
	private int mGraphMarginVertical;
	private int mHistogramWidth;
	private int mHistogramGap;
	private static final int HISTOGRAM_NUM = 7;
	private int mSelectedHistogram = HISTOGRAM_NUM - 1;
	private Rect mRectList[];
	private int mTargetNum = 99;
	private List<Integer> mValueList;
	private List<String> mDateList;
	private OnHistogramChangeListener mHistogramChangeListener;
	private TYPE mType;
	private String mSettingTarget;
	public HealthHistogramView(Context context) {
		super(context);
	}

	public HealthHistogramView(Context context, AttributeSet attrs)
	{
		this(context, attrs, R.attr.circularImageViewStyle);
	}

	public HealthHistogramView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		init(context, attrs, defStyle);
	}
	
	private void init(Context context, AttributeSet attrs, int defStyle)
	{
		// Initialize paint objects
		paintSelected = new Paint();
		paintSelected.setColor(getResources().getColor(R.color.md_amber_700));
		paintSelected.setAntiAlias(true);
		paintOthers = new Paint();
		paintOthers.setColor(getResources().getColor(R.color.md_amber_700));
		paintOthers.setAlpha(128);
		paintOthers.setAntiAlias(true);
		//Text size
		textFontSize = getResources().getDimensionPixelSize(R.dimen.healthy_detail_histogram_font_size);
		//Paint for text
		textPaint = new Paint();  
		textPaint.setColor(Color.BLACK);
		textPaint.setTextSize(textFontSize);
		textPaint.setAntiAlias(true);
		textPaint.setStrokeCap(Cap.ROUND);
		textPaint.setStrokeWidth(1);
		//Paint for base line
		baseLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);  
		baseLinePaint.setStyle(Style.STROKE);
		baseLinePaint.setStrokeCap(Cap.ROUND);
		baseLinePaint.setColor(Color.BLACK);  
		baseLinePaint.setStrokeWidth(5);  
		float[] intervals = new float[] {5.0f, 20.0f};
		float phase = 1.f;
		DashPathEffect effects = new DashPathEffect(intervals, phase);  
		baseLinePaint.setPathEffect(effects);  
		//Paint for Triangle
		paintTriangle = new Paint();
	    paintTriangle.setColor(android.graphics.Color.BLACK);
	    paintTriangle.setStyle(Paint.Style.FILL);
	    paintTriangle.setAntiAlias(true);	
	    //Def. for Histogram 
		mGraphMarginVertical = getResources().getDimensionPixelSize(R.dimen.healthy_detail_histogram_magin_vertical);
		mGraphMarginHorizon = getResources().getDimensionPixelSize(R.dimen.healthy_detail_histogram_magin_horizon);
		mHistogramWidth = getResources().getDimensionPixelSize(R.dimen.healthy_detail_histogram_width);
		mRectList = new Rect[HISTOGRAM_NUM];    	
	}
	
	private String getTarget() {
    	SharedPreferences sp = getContext().getSharedPreferences(Def.SHARE_PREFERENCE, Context.MODE_PRIVATE);
		String target = "";
		switch(mType) {
		case ACTIVITY:
			target = "99";
			break;
		case CALORIES_BURNED:
	    	target = sp.getString(Def.SP_TARGET_CARLOS, "2000");
			break;
		case CYCLING_TIME:
	    	target = sp.getString(Def.SP_TARGET_CYCLING, "30");
			break;
		case HEART_RATE:
			target = "80";
			break;
		case RUNNING_TIME:
	    	target = sp.getString(Def.SP_TARGET_RUNNING, "30");
			break;
		case SLEEP_TIME:
	    	target = sp.getString(Def.SP_TARGET_SLEEPING, "9");
			break;
		case TOTAL_STEPS:
	    	target = sp.getString(Def.SP_TARGET_STEPS, "10000");
			break;
		case WALKING_TIME:
	    	target = sp.getString(Def.SP_TARGET_WALKING, "");
			break;
		default:
			break;
		}
		return target;
	}
	@Override
	public void onDraw(Canvas canvas)
	{
		int graph_bottom = mHeight - textFontSize * 2;
		mHistogramGap = (mWidth - (mGraphMarginHorizon * 2) - (mHistogramWidth * HISTOGRAM_NUM)) / (HISTOGRAM_NUM - 1);

		canvas.drawText("近七天狀態", 50, mHeight - textFontSize, textPaint);
		canvas.drawText("今天", mWidth - 100, mHeight - textFontSize, textPaint);
		
		mBottomPath = new Path();
		mBottomPath.moveTo(0, graph_bottom);
		mBottomPath.lineTo(mGraphMarginHorizon, graph_bottom);
		for (int i = 0; i < HISTOGRAM_NUM; i++) {
			if (mRectList[i] == null) {
				float ratio = 1 - ((float)mValueList.get(i) / mTargetNum);
				float margin = (graph_bottom - mGraphMarginVertical) * ratio;
				int left = mGraphMarginHorizon + i * (mHistogramWidth + mHistogramGap);
				int right = left + mHistogramWidth;
				mRectList[i] = new Rect(left, (int) (mGraphMarginVertical + margin), right , graph_bottom);
			}
			if (mSelectedHistogram == i) {
				canvas.drawRect(mRectList[i], paintSelected);
			} else {
				canvas.drawRect(mRectList[i], paintOthers);
			}
			mBottomPath.moveTo(mRectList[i].right + 10 , graph_bottom);  
			mBottomPath.lineTo(mRectList[i].right + mHistogramGap, graph_bottom);
		}
        canvas.drawPath(mBottomPath, baseLinePaint);
		canvas.drawText(mSettingTarget, 0, 30, textPaint);

	    Path path = new Path();
		Point a = new Point(0, 45);
	    Point b = new Point(30, 45);
	    Point c = new Point(15, (int)(mHeight * 0.09) + mGraphMarginVertical);
	    path.moveTo(a.x, a.y);
	    path.lineTo(b.x, b.y);
	    path.lineTo(c.x, c.y);
	   
	    path.close();
	    canvas.drawPath(path, paintTriangle);
	    
		canvas.drawLine(0.f, (float) (mHeight * 0.09) + mGraphMarginVertical, (float)mWidth, (float)(mHeight * 0.09) + mGraphMarginVertical, textPaint);
		
	}
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent event)
	{
		// Check for clickable state and do nothing if disabled
		if(!this.isClickable()) {
			return super.onTouchEvent(event);
		}
		int touchX = (int)event.getX();
	    int touchY = (int)event.getY();
		// Set selected state based on Motion Event
		switch(event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				for (int i = 0; i < HISTOGRAM_NUM; i++) {
	                if(mRectList[i].contains(touchX,touchY)){
	                    mSelectedHistogram = i;
	                    mHistogramChangeListener.onHistogramChanged(i, mValueList.get(i), mDateList.get(i));
	                	break;
	                }
	            }
				break;
			case MotionEvent.ACTION_UP:
			case MotionEvent.ACTION_SCROLL:
			case MotionEvent.ACTION_OUTSIDE:
			case MotionEvent.ACTION_CANCEL:
				break;
		}
		
		// Redraw image and return super type
		this.invalidate();
		return super.dispatchTouchEvent(event);
	}
	
	public void setTargetNumber(int num) {
		mTargetNum = num;
	}
	
	public void setValuesByDay(List<Integer> values) {
		mValueList = values;
		mHistogramChangeListener.onHistogramChanged(mSelectedHistogram, mValueList.get(mSelectedHistogram), mDateList.get(mSelectedHistogram));
	}
	
	public void setDates(List<String> dateList) {
		mDateList = dateList;
	}
	@Override 
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
	    mWidth = View.MeasureSpec.getSize(widthMeasureSpec);
	    mHeight = View.MeasureSpec.getSize(heightMeasureSpec);

	    setMeasuredDimension(mWidth, mHeight);
	}
	public void setOnHistogramClickListener(OnHistogramChangeListener listener) {
		mHistogramChangeListener = listener;
	}
	public static interface OnHistogramChangeListener {
		public void onHistogramChanged(int idx, int value, String date);
	}
	
	public void setType(TYPE type) {
		mType = type;
		paintSelected.setColor(getResources().getColor(mType.getColorId()));
		paintOthers.setColor(getResources().getColor(mType.getColorId()));
		paintOthers.setAlpha(128);
		mSettingTarget = getTarget();
		mTargetNum = !TextUtils.isEmpty(mSettingTarget) ? Integer.parseInt(mSettingTarget) : 80;
	}
}
