package com.liteon.icampusguardian.util;

import java.text.SimpleDateFormat;

import com.liteon.icampusguardian.R;
import com.liteon.icampusguardian.util.HealthHistogramView.OnHistogramChangeListener;
import com.liteon.icampusguardian.util.HealthyItem.TYPE;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Join;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class HealthPieChartView extends View implements OnHistogramChangeListener{

	//Text size for graph
	private int mWidth;
	private int mHeight;
	private int textFontSize;
	private int textTargetSize;
	private Paint textPaint;
	private Paint textTargetPaint;
	private Paint arcPaintBackground;
	private Paint arcPaintPrimary;
	private HealthyItem.TYPE mType;
	private int mTargetValue;
	private int mPieChartSize;
	private int mTypeIconSize;
	private Bitmap mBackground;
	private Bitmap mTypeIcon;
	private int mTypeIconLocationY;
	private int mTargetOffsetY;
	private int mDateOffsetY;
	private int mCurrentValue;
	private String mCurrentDate = "2017/06/20";
	public HealthPieChartView(Context context) {
		super(context);
	}

	public HealthPieChartView(Context context, AttributeSet attrs)
	{
		this(context, attrs, R.attr.circularImageViewStyle);
	}

	public HealthPieChartView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		init(context, attrs, defStyle);
	}
	
	private void init(Context context, AttributeSet attrs, int defStyle)
	{
		// Initialize paint objects
		mCurrentValue = 68;
		//Text size
		textFontSize = getResources().getDimensionPixelSize(R.dimen.healthy_detail_histogram_font_size);
		textTargetSize = getResources().getDimensionPixelSize(R.dimen.healthy_detail_pie_chart_target_font_size);
		//Pie chart size
		mPieChartSize = getResources().getDimensionPixelSize(R.dimen.healthy_detail_pie_chart_size);
		//type icon size
		mTypeIconSize = getResources().getDimensionPixelSize(R.dimen.healthy_detail_pie_chart_icon_size);
		//type icon location Y
		mTypeIconLocationY = getResources().getDimensionPixelSize(R.dimen.healthy_detail_pie_chart_icon_location_y);
		//Target location offset from center Y
		mTargetOffsetY = getResources().getDimensionPixelSize(R.dimen.healthy_detail_pie_chart_target_offset_y);
		//Date location offset from center Y
		mDateOffsetY = getResources().getDimensionPixelSize(R.dimen.healthy_detail_pie_chart_date_offset_y);
		//Paint for text
		textPaint = new Paint();  
		textPaint.setColor(Color.BLACK);
		textPaint.setTextSize(textFontSize);
		textPaint.setAntiAlias(true);
		textPaint.setStrokeCap(Cap.ROUND);
		textPaint.setStrokeWidth(1);
		//Paint for Target text
		textTargetPaint = new Paint();
		textTargetPaint.setColor(getResources().getColor(R.color.md_amber_700, null));
		textTargetPaint.setTextSize(textTargetSize);
		textTargetPaint.setAntiAlias(true);
		
		//for draw arc
		arcPaintBackground = new Paint(); 
		arcPaintBackground.setDither(true);
		arcPaintBackground.setStyle(Paint.Style.STROKE);
		arcPaintBackground.setStrokeCap(Cap.BUTT);
		arcPaintBackground.setStrokeJoin(Join.BEVEL);
		arcPaintBackground.setColor(getResources().getColor(R.color.md_grey_400, null));
		arcPaintBackground.setStrokeWidth(25);
		arcPaintBackground.setAntiAlias(true);
		  
		arcPaintPrimary = new Paint(); 
		arcPaintPrimary.setDither(true);
		arcPaintPrimary.setStyle(Paint.Style.STROKE);
		arcPaintPrimary.setStrokeCap(Cap.BUTT);
		arcPaintPrimary.setStrokeJoin(Join.BEVEL);
		arcPaintPrimary.setColor(getResources().getColor(R.color.md_amber_700,null));
		arcPaintPrimary.setStrokeWidth(25);
		arcPaintPrimary.setAntiAlias(true);
	}
	
	@Override 
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
	    mWidth = View.MeasureSpec.getSize(widthMeasureSpec);
	    mHeight = View.MeasureSpec.getSize(heightMeasureSpec);

	    setMeasuredDimension(mWidth, mHeight);
	}
	
	@Override
	public void onDraw(Canvas canvas)
	{
		switch(mType) {
		case ACTIVITY:
			onDrawActivity(canvas);
			break;
		case CALORIES_BURNED:
			onDrawCalories(canvas);
			break;
		case CYCLING_TIME:
			onDrawCycling(canvas);
			break;
		case HEART_RATE:
			onDrawHeartRate(canvas);
			break;
		case RUNNING_TIME:
			onDrawRunning(canvas);
			break;
		case SLEEP_TIME:
			onDrawSleeping(canvas);
			break;
		case TOTAL_STEPS:
			onDrawSteps(canvas);
			break;
		case WALKING_TIME:
			onDrawWalking(canvas);
			break;
		default:
			break;
		}			
	}

	private void onDrawWalking(Canvas canvas) {
		if (mTypeIcon == null) {
			mTypeIcon = getBitmap(R.drawable.health_img_walk, mTypeIconSize, mTypeIconSize);
		}
		
		int cx = (mWidth - mTypeIcon.getWidth()) >> 1; // same as (...) / 2
	    int cy = mTypeIconLocationY;//(mHeight - mTypeIcon.getHeight()) >> 1;
	    canvas.drawBitmap(mTypeIcon, cx, cy, null);
	    
	    cx = (mWidth - mPieChartSize) >> 1; // same as (...) / 2
	    cy = (mHeight - mPieChartSize) >> 1;
	    RectF rect = new RectF(cx, cy, cx + mPieChartSize, cy + mPieChartSize);

		// background full circle arc
        canvas.drawArc(rect, 270, 360, false, arcPaintBackground);

        // draw starting at top of circle in the clockwise direction
        canvas.drawArc(rect, 270, (360 * (60 / 100f)), false, arcPaintPrimary);
        
        drawCenter(canvas, textTargetPaint, Integer.toString(mCurrentValue) + "分", mTargetOffsetY);
        drawCenter(canvas, textPaint, mCurrentDate, mDateOffsetY);			
	}

	private void onDrawSteps(Canvas canvas) {
		if (mTypeIcon == null) {
			mTypeIcon = getBitmap(R.drawable.health_img_step, mTypeIconSize, mTypeIconSize);
		}
		
		int cx = (mWidth - mTypeIcon.getWidth()) >> 1; // same as (...) / 2
	    int cy = mTypeIconLocationY;//(mHeight - mTypeIcon.getHeight()) >> 1;
	    canvas.drawBitmap(mTypeIcon, cx, cy, null);
	    
	    cx = (mWidth - mPieChartSize) >> 1; // same as (...) / 2
	    cy = (mHeight - mPieChartSize) >> 1;
	    RectF rect = new RectF(cx, cy, cx + mPieChartSize, cy + mPieChartSize);

		// background full circle arc
        canvas.drawArc(rect, 270, 360, false, arcPaintBackground);

        // draw starting at top of circle in the clockwise direction
        canvas.drawArc(rect, 270, (360 * (60 / 100f)), false, arcPaintPrimary);
        
        drawCenter(canvas, textTargetPaint, Integer.toString(mCurrentValue) + "步", mTargetOffsetY);
        drawCenter(canvas, textPaint, mCurrentDate, mDateOffsetY);		
	}

	private void onDrawSleeping(Canvas canvas) {
		// TODO Auto-generated method stub
		
	}

	private void onDrawRunning(Canvas canvas) {
		if (mTypeIcon == null) {
			mTypeIcon = getBitmap(R.drawable.health_img_run, mTypeIconSize, mTypeIconSize);
		}
		
		int cx = (mWidth - mTypeIcon.getWidth()) >> 1; // same as (...) / 2
	    int cy = mTypeIconLocationY;//(mHeight - mTypeIcon.getHeight()) >> 1;
	    canvas.drawBitmap(mTypeIcon, cx, cy, null);
	    
	    cx = (mWidth - mPieChartSize) >> 1; // same as (...) / 2
	    cy = (mHeight - mPieChartSize) >> 1;
	    RectF rect = new RectF(cx, cy, cx + mPieChartSize, cy + mPieChartSize);

		// background full circle arc
        canvas.drawArc(rect, 270, 360, false, arcPaintBackground);

        // draw starting at top of circle in the clockwise direction
        canvas.drawArc(rect, 270, (360 * (60 / 100f)), false, arcPaintPrimary);
        
        drawCenter(canvas, textTargetPaint, Integer.toString(mCurrentValue) + "步", mTargetOffsetY);
        drawCenter(canvas, textPaint, mCurrentDate, mDateOffsetY);			
	}

	private void onDrawHeartRate(Canvas canvas) {
		if (mTypeIcon == null) {
			mTypeIcon = getBitmap(R.drawable.health_img_heartbeat, mTypeIconSize, mTypeIconSize);
		}
		
		int cx = (mWidth - mTypeIcon.getWidth()) >> 1; // same as (...) / 2
	    int cy = mTypeIconLocationY;//(mHeight - mTypeIcon.getHeight()) >> 1;
	    canvas.drawBitmap(mTypeIcon, cx, cy, null);
        
        drawCenter(canvas, textTargetPaint, Integer.toString(mCurrentValue) + "步", mTargetOffsetY);
        drawCenter(canvas, textPaint, mCurrentDate, mDateOffsetY);			
	}

	private void onDrawCycling(Canvas canvas) {
		if (mTypeIcon == null) {
			mTypeIcon = getBitmap(R.drawable.health_img_bicycle, mTypeIconSize, mTypeIconSize);
		}
		
		int cx = (mWidth - mTypeIcon.getWidth()) >> 1; // same as (...) / 2
	    int cy = mTypeIconLocationY;//(mHeight - mTypeIcon.getHeight()) >> 1;
	    canvas.drawBitmap(mTypeIcon, cx, cy, null);
	    
	    cx = (mWidth - mPieChartSize) >> 1; // same as (...) / 2
	    cy = (mHeight - mPieChartSize) >> 1;
	    RectF rect = new RectF(cx, cy, cx + mPieChartSize, cy + mPieChartSize);

		// background full circle arc
        canvas.drawArc(rect, 270, 360, false, arcPaintBackground);

        // draw starting at top of circle in the clockwise direction
        canvas.drawArc(rect, 270, (360 * (60 / 100f)), false, arcPaintPrimary);
        
        drawCenter(canvas, textTargetPaint, Integer.toString(mCurrentValue) + "分", mTargetOffsetY);
        drawCenter(canvas, textPaint, mCurrentDate, mDateOffsetY);			
	}

	private void onDrawActivity(Canvas canvas) {
		if (mBackground == null) {
			int resId;
			if (mCurrentValue >= 80) {
				resId = R.drawable.activity_img_scale5;
			} else if (mCurrentValue >= 60) {
				resId = R.drawable.activity_img_scale4;
			} else if (mCurrentValue >= 40) {
				resId = R.drawable.activity_img_scale3;
			} else if (mCurrentValue >= 20) {
				resId = R.drawable.activity_img_scale2;
			} else if (mCurrentValue >= 10) {
				resId = R.drawable.activity_img_scale1;
			} else {
				resId = R.drawable.activity_img_scale;
			}
			mBackground = getBitmap(resId, mPieChartSize, mPieChartSize);
		}
		if (mTypeIcon == null) {
			mTypeIcon = getBitmap(R.drawable.health_img_activity, mTypeIconSize, mTypeIconSize);
		}
		int cx = (mWidth - mBackground.getWidth()) >> 1; 
	    int cy = (mHeight - mBackground.getHeight()) >> 1;
	    
	    canvas.drawBitmap(mBackground, cx, cy, null);
		
	    cx = (mWidth - mTypeIcon.getWidth()) >> 1;
	    cy = mTypeIconLocationY;
	    canvas.drawBitmap(mTypeIcon, cx, cy, null);
	    
	    drawCenter(canvas, textTargetPaint, Integer.toString(mCurrentValue), mTargetOffsetY);
	    
	    drawCenter(canvas, textPaint, mCurrentDate, mDateOffsetY);
	    
	    canvas.drawText("非常不好", (float) (mWidth * 0.75), (float) (mHeight * 0.2), textPaint);
	    canvas.drawText("非常好", (float) (mWidth * 0.12), (float) (mHeight * 0.2), textPaint);
	    canvas.drawText("好", (float) (mWidth * 0.12), (float) (mHeight * 0.7), textPaint);
	    canvas.drawText("不好", (float) (mWidth * 0.85), (float)(mHeight * 0.7), textPaint);
	    canvas.drawText("普通", (float)(mWidth * 0.45), (float)(mHeight * 0.97), textPaint);
	}
	
	private void onDrawCalories(Canvas canvas) {
		if (mTypeIcon == null) {
			mTypeIcon = getBitmap(R.drawable.health_img_calories, mTypeIconSize, mTypeIconSize);
		}
		
		int cx = (mWidth - mTypeIcon.getWidth()) >> 1; // same as (...) / 2
	    int cy = mTypeIconLocationY;//(mHeight - mTypeIcon.getHeight()) >> 1;
	    canvas.drawBitmap(mTypeIcon, cx, cy, null);
	    
	    cx = (mWidth - mPieChartSize) >> 1; // same as (...) / 2
	    cy = (mHeight - mPieChartSize) >> 1;
	    RectF rect = new RectF(cx, cy, cx + mPieChartSize, cy + mPieChartSize);

		// background full circle arc
        canvas.drawArc(rect, 270, 360, false, arcPaintBackground);

        // draw starting at top of circle in the clockwise direction
        canvas.drawArc(rect, 270, (360 * (60 / 100f)), false, arcPaintPrimary);
        
        drawCenter(canvas, textTargetPaint, Integer.toString(mCurrentValue) + "卡", mTargetOffsetY);
        drawCenter(canvas, textPaint, mCurrentDate, mDateOffsetY);
	}
	
	private Bitmap getBitmap(int resId, int requestWidth, int requestHeight) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeResource(getResources(), resId, options);
		int imageHeight = options.outHeight;
		int imageWidth = options.outWidth;
		String imageType = options.outMimeType;
		
		final int height = options.outHeight;
	    final int width = options.outWidth;
	    int inSampleSize = 1;

	    if (height > requestHeight || width > requestWidth) {

	        final int halfHeight = height / 2;
	        final int halfWidth = width / 2;

	        // Calculate the largest inSampleSize value that is a power of 2 and keeps both
	        // height and width larger than the requested height and width.
	        while ((halfHeight / inSampleSize) >= requestHeight
	                && (halfWidth / inSampleSize) >= requestWidth) {
	            inSampleSize *= 2;
	        }
	    }
	    options.inSampleSize = inSampleSize;
	    // Decode bitmap with inSampleSize set
	    options.inJustDecodeBounds = false;
	    Bitmap bitmap = BitmapFactory.decodeResource(getResources(), resId, options);
	    return Bitmap.createScaledBitmap(bitmap, requestWidth, requestHeight, false);
		
	}
	
	private void drawCenter(Canvas canvas, Paint paint, String text, int yOffset) {
		Rect r = new Rect();
		canvas.getClipBounds(r);
	    int cHeight = r.height();
	    int cWidth = r.width();
	    paint.setTextAlign(Paint.Align.LEFT);
	    paint.getTextBounds(text, 0, text.length(), r);
	    float x = cWidth / 2f - r.width() / 2f - r.left;
	    float y = cHeight / 2f + r.height() / 2f - r.bottom + yOffset;
	    canvas.drawText(text, x, y, paint);
	}
	
	public void setDate(String date) {
		mCurrentDate = date;
	}
	
	public void setValue(int value) {
		mCurrentValue = value;
	}
	
	public void setType(TYPE type) {
		mType = type;
	}

	@Override
	public void onHistogramChanged(int idx, int value) {
		mCurrentValue = value;
	}
}