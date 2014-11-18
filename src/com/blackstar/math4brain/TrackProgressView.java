package com.blackstar.math4brain;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class TrackProgressView extends View{
	public TrackProgressView(Context context) {
		super(context);
	}
	
	public TrackProgressView(Context context, AttributeSet attrs) {
		super(context,attrs);
	}
	
	public TrackProgressView(Context context, AttributeSet attrs, int defStyle) {
		super(context,attrs,defStyle);
	}
	
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		setMeasuredDimension(widthMeasureSpec,widthMeasureSpec);
	}

	@Override
	protected void onDraw(Canvas canvas){
		super.onDraw(canvas);
		canvas.drawLine(0, canvas.getWidth()/2, canvas.getWidth(), canvas.getWidth()/2, new Paint());
	}
}
