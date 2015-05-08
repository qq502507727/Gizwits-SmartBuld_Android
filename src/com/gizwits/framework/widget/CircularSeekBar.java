/**
 * Project Name:XPGSdkV4AppBase
 * File Name:CircularSeekBar.java
 * Package Name:com.gizwits.framework.widget
 * Date:2015-1-27 14:48:07
 * Copyright (c) 2014~2015 Xtreme Programming Group, Inc.
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), 
 * to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, 
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.gizwits.framework.widget;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.gizwits.light.R;
import com.xpg.common.device.DensityUtils;


// TODO: Auto-generated Javadoc
/**
 * The Class CircularSeekBar.
 *
 *	圆形拖动条
 *
 * @author Lien
 */
public class CircularSeekBar extends View {

    /** The context. */
    private Context mContext;

    /** The listener to listen for changes. */
    private OnSeekChangeListener mListener;

    /**
     * the color of the inside circle. Acts as background color
     */
    private Paint innerColor;
    
    //渐变色环参数
    private final int[] mCircleColors = new int[] {0xFFFF0000, 0xFFFF00FF, 0xFF0000FF, 
			0xFF00FFFF, 0xFF00FF00,0xFFFFFF00, 0xFFFF0000};//红、紫、蓝、绿、黄、橙、红

    /** The progress circle ring background. */
    private Paint circleRing;

    /** The angle of progress. */
    private int angle = 0;

    /** The start angle (12 O'clock. */
    private int startAngle = 270;

    /** The width of the progress ring. */
    private int barWidth = 8;

    /** The width of the view. */
    private int width;

    /** The height of the view. */
    private int height;

    /** The maximum progress amount. */
    private int maxProgress = 100;

    /** The current progress. */
    private int progress;

    /** The progress percent. */
    private int progressPercent;

    /** The radius of the inner circle. */
    private float innerRadius;

    /** The radius of the outer circle. */
    private float outerRadius;

    /** The circle's center X coordinate. */
    private float cx;

    /** The circle's center Y coordinate. */
    private float cy;

    /** The left bound for the circle RectF. */
    private float left;

    /** The right bound for the circle RectF. */
    private float right;

    /** The top bound for the circle RectF. */
    private float top;

    /** The bottom bound for the circle RectF. */
    private float bottom;

    /** The X coordinate for the top left corner of the marking drawable. */
    private float dx;

    /** The Y coordinate for the top left corner of the marking drawable. */
    private float dy;

    /** The X coordinate for 12 O'Clock. */
    private float startPointX;

    /** The Y coordinate for 12 O'Clock. */
    private float startPointY;

    /** The X coordinate for the current position of the marker, pre adjustment to center. */
    private float markPointX;

    /** The Y coordinate for the current position of the marker, pre adjustment to center. */
    private float markPointY;

    /**
     * The adjustment factor. This adds an adjustment of the specified size to
     * both sides of the progress bar, allowing touch events to be processed
     * more user friendly (yes, I know that's not a word)
     */
    private float adjustmentFactor = 100;

    /** The progress mark when the view isn't being progress modified. */
    private Bitmap progressMark;

    /**
     * The progress mark when the view is being progress modified.
     */
    private Bitmap progressMarkPressed;

    /** The flag to see if view is pressed. */
    private boolean IS_PRESSED = false;

    /**
     * The flag to see if the setProgress() method was called from our own
     * View's setAngle() method, or externally by a user.
     */
    private boolean CALLED_FROM_ANGLE = false;

    /** The show seekbar. */
    private boolean SHOW_SEEKBAR = true;
    
    /**
     * The rectangle containing our circles and arcs.
     */
    private RectF rect = new RectF();

    {
        mListener = new OnSeekChangeListener() {

            @Override
            public void onProgressChange(CircularSeekBar view, int newProgress) {

            }
        };
        
        circleRing = new Paint(Paint.ANTI_ALIAS_FLAG);
        circleRing.setAntiAlias(true);
    	circleRing.setStyle(Paint.Style.STROKE);
    	circleRing.setStrokeWidth(30);
        
        innerColor = new Paint();
        innerColor.setColor(Color.BLUE); // Set default background color to
        innerColor.setAntiAlias(true);
        innerColor.setStrokeWidth(5);
    }

    /**
     * Instantiates a new circular seek bar.
     *
     * @param context  the context
     * @param attrs    the attrs
     * @param defStyle the def style
     */
    public CircularSeekBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        initDrawable();
    }

    /**
     * Instantiates a new circular seek bar.
     *
     * @param context the context
     * @param attrs   the attrs
     */
    public CircularSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initDrawable();
    }

    /**
     * Instantiates a new circular seek bar.
     *
     * @param context the context
     */
    public CircularSeekBar(Context context) {
        super(context);
        mContext = context;
        initDrawable();
    }

    /**
     * Inits the drawable.
     */
    public void initDrawable() {
        progressMark = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.light_sewen_bg2);
        progressMarkPressed = BitmapFactory.decodeResource(mContext.getResources(),
                R.drawable.light_sewen_bg2);
    }

    /*
     * (non-Javadoc)
     *
     * @see android.view.View#onMeasure(int, int)
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        barWidth = DensityUtils.dp2px(getContext(), 5);

        width = getWidth(); // Get View Width
        height = getHeight();// Get View Height

        int size = (width > height) ? height : width; // Choose the smaller

        cx = width / 2; // Center X for circle
        cy = height / 2; // Center Y for circle
        outerRadius = size / 2 - DensityUtils.dp2px(getContext(), 20); // Radius of the outer circle

        innerRadius = outerRadius - barWidth; // Radius of the inner circle

        left = cx - outerRadius; // Calculate left bound of our rect
        right = cx + outerRadius;// Calculate right bound of our rect
        top = cy - outerRadius;// Calculate top bound of our rect
        bottom = cy + outerRadius;// Calculate bottom bound of our rect

        startPointX = cx; // 12 O'clock X coordinate
        startPointY = cy - outerRadius;// 12 O'clock Y coordinate
        markPointX = startPointX;// Initial locatino of the marker X coordinate
        markPointY = startPointY;// Initial locatino of the marker Y coordinate

        rect.set(left, top, right, bottom); // assign size to rect
        
        Shader s = new SweepGradient(cx, cy, mCircleColors, null);
    	circleRing.setShader(s);
    }

    /*
     * (non-Javadoc)
     *
     * @see android.view.View#onDraw(android.graphics.Canvas)
     */
    @Override
    protected void onDraw(Canvas canvas) {

        canvas.drawCircle(cx, cy, outerRadius, circleRing);
        canvas.drawCircle(cx, cy, 30, innerColor);
        if (SHOW_SEEKBAR) {
            setInitMarkToXY(getAngle());
            dx = getXFromAngle();
            dy = getYFromAngle();
            drawMarkerAtProgress(canvas);
        }
        super.onDraw(canvas);
    }

    /**
     * Draw marker at the current progress point onto the given canvas.
     *
     * @param canvas the canvas
     */
    public void drawMarkerAtProgress(Canvas canvas) {
        if (IS_PRESSED) {
            canvas.drawBitmap(progressMarkPressed, dx, dy, null);
        } else {
            canvas.drawBitmap(progressMark, dx, dy, null);
        }
    }

    /**
     * Gets the X coordinate of the arc's end arm's point of intersection with
     * the circle.
     *
     * @return the X coordinate
     */
    public float getXFromAngle() {
        int size1 = progressMark.getWidth();
        int size2 = progressMarkPressed.getWidth();
        int adjust = (size1 > size2) ? size1 : size2;
        float x = markPointX - (adjust / 2);
        return x;
    }

    /**
     * Gets the Y coordinate of the arc's end arm's point of intersection with
     * the circle.
     *
     * @return the Y coordinate
     */
    public float getYFromAngle() {
        int size1 = progressMark.getHeight();
        int size2 = progressMarkPressed.getHeight();
        int adjust = (size1 > size2) ? size1 : size2;
        float y = markPointY - (adjust / 2);
        return y;
    }

    /**
     * Get the angle.
     *
     * @return the angle
     */
    public int getAngle() {
        return angle;
    }

    /**
     * Set the angle.
     *
     * @param angle the new angle
     */
    public void setAngle(int angle) {
        this.angle = angle;
        // setInitMarkToXY(angle);
        float donePercent = (((float) this.angle) / 360) * 100;
        float progress = (donePercent / 100) * getMaxProgress();
        setProgressPercent(Math.round(donePercent));
        CALLED_FROM_ANGLE = true;
        setProgress(Math.round(progress));
    }

    /**
     * Sets the seek bar change listener.
     *
     * @param listener the new seek bar change listener
     */
    public void setSeekBarChangeListener(OnSeekChangeListener listener) {
        mListener = listener;
    }

    /**
     * Gets the seek bar change listener.
     *
     * @return the seek bar change listener
     */
    public OnSeekChangeListener getSeekBarChangeListener() {
        return mListener;
    }

    /** The m continue listener. */
    private OnSeekContinueChangeListener mContinueListener;

    /**
     * Sets the seek continue change listener.
     *
     * @param listener the new seek continue change listener
     */
    public void setSeekContinueChangeListener(OnSeekContinueChangeListener listener) {
        mContinueListener = listener;
    }

    /**
     * Gets the seek continue change listener.
     *
     * @return the seek continue change listener
     */
    public OnSeekContinueChangeListener getSeekContinueChangeListener() {
        return mContinueListener;
    }

    /**
     * Gets the bar width.
     *
     * @return the bar width
     */
    public int getBarWidth() {
        return barWidth;
    }

    /**
     * Sets the bar width.
     *
     * @param barWidth the new bar width
     */
    public void setBarWidth(int barWidth) {
        this.barWidth = barWidth;
    }

    /**
     * The listener interface for receiving onSeekChange events. The class that
     * is interested in processing a onSeekChange event implements this
     * interface, and the object created with that class is registered with a
     * component using the component's
     * <code>setSeekBarChangeListener(OnSeekChangeListener)<code> method. When
     * the onSeekChange event occurs, that object's appropriate
     * method is invoked.
     *
     * @see OnSeekChangeEvent
     */
    public interface OnSeekChangeListener {

        /**
         * On progress change.
         *
         * @param view        the view
         * @param newProgress the new progress
         */
        public void onProgressChange(CircularSeekBar view, int newProgress);
    }

    /**
     * The listener interface for receiving onSeekContinueChange events.
     * The class that is interested in processing a onSeekContinueChange
     * event implements this interface, and the object created
     * with that class is registered with a component using the
     * component's <code>addOnSeekContinueChangeListener<code> method. When
     * the onSeekContinueChange event occurs, that object's appropriate
     * method is invoked.
     *
     * @see OnSeekContinueChangeEvent
     */
    public interface OnSeekContinueChangeListener {

        /**
         * On progress change.
         *
         * @param view        the view
         * @param newProgress the new progress
         */
        public void onProgressContinueChange(CircularSeekBar view, int newProgress);
    }

    /**
     * Gets the max progress.
     *
     * @return the max progress
     */
    public int getMaxProgress() {
        return maxProgress;
    }

    /**
     * Sets the max progress.
     *
     * @param maxProgress the new max progress
     */
    public void setMaxProgress(int maxProgress) {
        this.maxProgress = maxProgress;
    }

    /**
     * Gets the progress.
     *
     * @return the progress
     */
    public int getProgress() {
        return progress;
    }

    /**
     * Sets the progress.
     *
     * @param progress the new progress
     */
    public void setProgress(int progress) {
        if (this.progress != progress) {
            this.progress = progress;
            if (!CALLED_FROM_ANGLE) {
                int newPercent = (this.progress * 100) / this.maxProgress;
                int newAngle = (newPercent * 360) / 100;
                this.setAngle(newAngle);
                this.setProgressPercent(newPercent);
            }
            // mListener.onProgressChange(this, this.getProgress());
            if (mContinueListener != null) {
                mContinueListener.onProgressContinueChange(this, this.getProgress());
            }
            CALLED_FROM_ANGLE = false;
        }
    }

    /**
     * 重新设置进度时调用.
     *
     * @author Administrator
     * @param progress the new m progress
     * @return void
     * @Title: setMProgress
     * @Description: TODO
     */
    public void setMProgress(int progress) {
        int newPercent = (progress * 100) / this.maxProgress;
        int newAngle = (newPercent * 360) / 100;
        this.setAngle(newAngle);
        this.setProgressPercent(newPercent);
    }

    /**
     * Gets the progress percent.
     *
     * @return the progress percent
     */
    public int getProgressPercent() {
        return progressPercent;
    }

    /**
     * Sets the progress percent.
     *
     * @param progressPercent the new progress percent
     */
    public void setProgressPercent(int progressPercent) {
        this.progressPercent = progressPercent;
    }

    /**
     * Sets the ring background color.
     *
     * @param color the new ring background color
     */
    public void setRingBackgroundColor(int color) {
        circleRing.setColor(color);
    }

    /**
     * Checks if is mark point range.
     *
     * @param x the x
     * @param y the y
     * @return true, if is mark point range
     */
    public boolean isMarkPointRange(float x, float y) {
        float range = DensityUtils.dp2px(getContext(), 60);
        if (x > (markPointX - range) && x < (markPointX + range) && y > (markPointY - range)
                && y < (markPointY + range)) {
            return true;
        } else {
            return false;
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see android.view.View#onTouchEvent(android.view.MotionEvent)
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float markRange = DensityUtils.dp2px(getContext(), 60);
        float x = event.getX();
        float y = event.getY();
        
        boolean up = false;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (x < markPointX + markRange && x > markPointX - markRange && y > markPointY - markRange && y < markPointY + markRange) {
                    setParentScrollAble(false);
                }

                moved(x, y, up);
                break;
            case MotionEvent.ACTION_MOVE:
                moved(x, y, up);
                break;
            case MotionEvent.ACTION_UP:
                up = true;
                moved(x, y, up);
                break;
            case MotionEvent.ACTION_CANCEL:
                setParentScrollAble(true);
                up = true;
                moved(x, y, up);
                break;
        }
        return true;
    }

    /**
     * Moved.
     *
     * @param x  the x
     * @param y  the y
     * @param up the up
     */
    private void moved(float x, float y, boolean up) {
        if (!isMarkPointRange(x, y)) {
            return;
        }
        
        float distance = (float) Math.sqrt(Math.pow((x - cx), 2) + Math.pow((y - cy), 2));
        if (distance < outerRadius + adjustmentFactor && distance > innerRadius - adjustmentFactor && !up) {
            IS_PRESSED = true;

            markPointX = (float) (cx + outerRadius * Math.cos(Math.atan2(x - cx, cy - y) - (Math.PI / 2)));
            markPointY = (float) (cy + outerRadius * Math.sin(Math.atan2(x - cx, cy - y) - (Math.PI / 2)));

            float degrees = (float) ((float) ((Math.toDegrees(Math.atan2(x - cx, cy - y)) + 360.0)) % 360.0);
            // and to make it count 0-360
            if (degrees < 0) {
                degrees += 2 * Math.PI;
            }

            int CircleColor=interpCircleColor(mCircleColors, degrees);
            innerColor.setColor(CircleColor);
            
            setAngle(Math.round(degrees));
            invalidate();

        } else {
            if (mListener != null) {
                mListener.onProgressChange(this, this.getProgress());
            }
            IS_PRESSED = false;
            invalidate();
        }

    }

    /**
     * Gets the adjustment factor.
     *
     * @return the adjustment factor
     */
    public float getAdjustmentFactor() {
        return adjustmentFactor;
    }

    /**
     * Sets the adjustment factor.
     *
     * @param adjustmentFactor the new adjustment factor
     */
    public void setAdjustmentFactor(float adjustmentFactor) {
        this.adjustmentFactor = adjustmentFactor;
    }

    /**
     * Sets the inits the mark to xy.
     *
     * @param angle the new inits the mark to xy
     */
    public void setInitMarkToXY(int angle) {
        // double angle = 360+90+45;
        // double dx = 7 + 5 * Math.sin(angle*Math.PI/180);
        // double dy = 7 - 5 * Math.cos(angle*Math.PI/180);
        markPointX = (float) (cx + outerRadius * Math.sin(angle * Math.PI / 180));
        markPointY = (float) (cy - outerRadius * Math.cos(angle * Math.PI / 180));
        invalidate();
    }

    /**
     * To display seekbar.
     */
    public void ShowSeekBar() {
        SHOW_SEEKBAR = true;
        postInvalidate();
    }

    /**
     * To hide seekbar.
     */
    public void hideSeekBar() {
        SHOW_SEEKBAR = false;
        postInvalidate();
    }

    /**
     * Sets the parent scroll able.
     *
     * @param b the new parent scroll able
     */
    private void setParentScrollAble(boolean b) {
    	getParent().requestDisallowInterceptTouchEvent(!b);
    }
    
    public int getInnerColor(){
    	return innerColor.getColor();
    }
    
    public void setInnerColor(int color){
    	innerColor.setColor(color);
    	float degree =fromColor2Degree(color);
    	int progress=(int)(degree*100/(float)360);
    	Log.e("progress", progress+"");
    	setProgress(progress);
    }
    
	private float fromColor2Degree(int color) {
		//TODO 当有两个相等时，mid会越界
		
		float degree = 0;
		int diff = 360 / (mCircleColors.length - 1);

		int r = Color.red(color);
		int g = Color.green(color);
		int b = Color.blue(color);

		int[] mColor = { b, g, r };

		// 把最大的，置0xFF，最小的，置0
		int min = findMin(b, g, r);
		int max = findMax(b, g, r);
		if (min == max) {

			Log.e("test", 90 + "");
		} else {
			mColor[min] = 0;
			mColor[max] = 0xff;
		}

		int temp = 0xff * (int) Math.pow(256, max) + 0xff
				* (int) Math.pow(256, 3);
		int mid = 3 - max - min;

		int start = 0;
		int end = 0;
		for (int i = 0; i < mCircleColors.length - 2; i++) {
			if (mCircleColors[i] - temp == 0)
				start = i;
			if (mCircleColors[i] - temp == (0xff * (int) Math.pow(256, mid)))
				end = i;
		}

		float percent = (float) mColor[mid] / (float) 0xff;
		int degreeDiff = (int) (percent * diff);
		if (start < end) {
			degree = start * diff;
			degree += degreeDiff;
		} else {
			degree = end * diff;
			degree -= degreeDiff;
		}

		degree += 90;
		if (degree > 360)
			degree -= 360;

		return degree;
	}
    
	private int findMin(int one,int two,int three) {
		if (one < two && one < three) {
			return 0;
		} else if (two < three) {
			return 1;
		} else {
			return 2;
		}
	}
	
	private int findMax(int one,int two,int three) {
		if (one > two && one > three) {
			return 0;
		} else if (two > three) {
			return 1;
		} else {
			return 2;
		}
	}
    
    /**
	 * 获取圆环上颜色
	 * @param colors
	 * @param unit
	 * @return
	 */
	private int interpCircleColor(int colors[], float degree) {
		Log.e(" degree", degree+"");
        degree-=90;
        
        if(degree<0)
        	degree+=360;
        	
        float p = degree * (colors.length-1)/360;
        int i = (int)p;
        p -= i;

        // now p is just the fractional part [0...1) and i is the index
        int c0 = colors[i];
        int c1 = colors[i+1];
        int a = ave(Color.alpha(c0), Color.alpha(c1), p);
        int r = ave(Color.red(c0), Color.red(c1), p);
        int g = ave(Color.green(c0), Color.green(c1), p);
        int b = ave(Color.blue(c0), Color.blue(c1), p);
        
        Log.e("color","r="+r+",g="+g+",b="+b);
        
        return Color.argb(a, r, g, b);
    }
	
	private int ave(int s, int d, float p) {
        return s + Math.round(p * (d - s));
    }
}
