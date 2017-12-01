package com.liteon.icampusguardian.util;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.liteon.icampusguardian.R;
import com.liteon.icampusguardian.db.DBHelper;
import com.liteon.icampusguardian.util.HealthyItem.TYPE;
import com.liteon.icampusguardian.util.JSONResponse.Student;

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
	private Paint textPaint2;
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
	private TargetItem mCurrentTargetItem;
	private Map<String, TargetItem> mTargetMap;
	private List<Student> mStudents;
	private int mCurrentStudentIdx;
	private DBHelper mDbHelper;

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

		textPaint2 = new Paint();
		textPaint2.setColor(Color.BLACK);
		textPaint2.setTextSize(textFontSize);
		textPaint2.setAntiAlias(true);
		textPaint2.setStrokeCap(Cap.ROUND);
		textPaint2.setStrokeWidth(1);
		float[] intervals = new float[] {2.0f, 2.0f};
		float phase = 0.f;
		DashPathEffect effects = new DashPathEffect(intervals, phase);
		textPaint2.setPathEffect(effects);
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
		
		mDbHelper = DBHelper.getInstance(getContext());
		//get child list
		mStudents = mDbHelper.queryChildList(mDbHelper.getReadableDatabase());
		restoreTarget();
	}
	
	private String getTarget() {
		String target = "";
		switch(mType) {
		case ACTIVITY:
			target = "80";
			break;
		case CALORIES_BURNED:
	    	target = mCurrentTargetItem.getCarlos();
			break;
		case CYCLING_TIME:
	    	target = mCurrentTargetItem.getCycling();
			break;
		case HEART_RATE:
			target = "80";
			break;
		case RUNNING_TIME:
	    	target = mCurrentTargetItem.getRunning();
			break;
		case SLEEP_TIME:
	    	target = mCurrentTargetItem.getSleep();
			break;
		case TOTAL_STEPS:
	    	target = mCurrentTargetItem.getStep();
			break;
		case WALKING_TIME:
	    	target = mCurrentTargetItem.getWalking();
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
		double histogram_top = (mHeight * 0.095) + mGraphMarginVertical;
		mHistogramGap = (mWidth - (mGraphMarginHorizon * 2) - (mHistogramWidth * HISTOGRAM_NUM)) / (HISTOGRAM_NUM - 1);

		canvas.drawText(getResources().getString(R.string.activity_last_7_days), 50, mHeight - textFontSize, textPaint);
		canvas.drawText(getResources().getString(R.string.activity_today), mWidth - 150, mHeight - textFontSize, textPaint);

        canvas.drawLine(0.f, graph_bottom, (float)mWidth, graph_bottom, textPaint);
		for (int i = 0; i < HISTOGRAM_NUM; i++) {
			if (mRectList[i] == null) {
				float ratio = 1 - ((float)mValueList.get(i) / mTargetNum);
                float margin = 0;
                int top = 0;
				if (ratio <= 0) {
                    top = 50;
                } else {
                    margin = (graph_bottom - (float)histogram_top) * ratio;
                    top = (int) (histogram_top + margin);
                }

				int left = mGraphMarginHorizon + i * (mHistogramWidth + mHistogramGap);
				int right = left + mHistogramWidth;
				mRectList[i] = new Rect(left, top, right , graph_bottom);
			}
			if (mSelectedHistogram == i) {
				canvas.drawRect(mRectList[i], paintSelected);
			} else {
				canvas.drawRect(mRectList[i], paintOthers);
			}
		}
		if (mType != TYPE.HEART_RATE) {
			canvas.drawText(mSettingTarget, 0, 30, textPaint);

			Path path = new Path();
			Point a = new Point(0, 50);
			Point b = new Point(30, 50);
			Point c = new Point(15, (int) (mHeight * 0.08) + mGraphMarginVertical);
			path.moveTo(a.x, a.y);
			path.lineTo(b.x, b.y);
			path.lineTo(c.x, c.y);

			path.close();
			canvas.drawPath(path, paintTriangle);

			canvas.drawLine(0.f, (float) (mHeight * 0.095) + mGraphMarginVertical, (float) mWidth, (float) (mHeight * 0.095) + mGraphMarginVertical, textPaint);
		} else {
			canvas.drawText("80", 0, (float) (mHeight * 0.095) + mGraphMarginVertical, textPaint);
			canvas.drawLine(50.f, (float) (mHeight * 0.095) + mGraphMarginVertical - 20, (float) mWidth, (float) (mHeight * 0.095) + mGraphMarginVertical - 20, textPaint2);

			canvas.drawText("70", 0, (float) (mHeight * 0.095) + mGraphMarginVertical + 50, textPaint);
			canvas.drawLine(50.f, (float) (mHeight * 0.095) + mGraphMarginVertical + 50 - 20, (float) mWidth, (float) (mHeight * 0.095) + mGraphMarginVertical + 50 - 20, textPaint2);

		}
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
	                if(mRectList[i].contains(touchX,touchY) || (mRectList[i].left < touchX && mRectList[i].right > touchX)){
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
		if (type == TYPE.SLEEP_TIME) {
            mTargetNum *= 60;
        }
	}
	
	private void restoreTarget() {
		SharedPreferences sp = getContext().getSharedPreferences(Def.SHARE_PREFERENCE, Context.MODE_PRIVATE);
		String targetMap = sp.getString(Def.SP_TARGET_MAP, "");
		mCurrentStudentIdx = sp.getInt(Def.SP_CURRENT_STUDENT, 0);
		Type typeOfHashMap = new TypeToken<Map<String, TargetItem >>() { }.getType();
        Gson gson = new GsonBuilder().create();
        mTargetMap = gson.fromJson(targetMap, typeOfHashMap);
		if (TextUtils.isEmpty(targetMap)) {
			mTargetMap = new HashMap<String, TargetItem>();
			for (Student student : mStudents) {
				String studentId = student.getStudent_id();
				TargetItem item = new TargetItem();
				item.setCarlos("2000");
				item.setStep("10000");
				item.setWalking("30");
				item.setRunning("30");
				item.setCycling("30");
				item.setSleep("9");
				mTargetMap.put(studentId, item);
			}
		}
		if (mTargetMap.get(mStudents.get(mCurrentStudentIdx).getStudent_id()) == null) {
			TargetItem item = new TargetItem();
			item.setCarlos("2000");
			item.setStep("10000");
			item.setWalking("30");
			item.setRunning("30");
			item.setCycling("30");
			item.setSleep("9");
			mTargetMap.put(mStudents.get(mCurrentStudentIdx).getStudent_id(), item);
		}
		mCurrentTargetItem = mTargetMap.get(mStudents.get(mCurrentStudentIdx).getStudent_id());

	}
}
