package com.blackstar.math4brain;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;

public class DisplayLevels extends View{
	  Paint circles = new Paint();
	  Bitmap img = BitmapFactory.decodeResource(getResources(), R.drawable.ml);
	  Bitmap localBitmap1;
	  boolean initial = true;
	  int level = 0, lvl, margin=10, max=24;
	  Typeface myTypeface;
	  Paint pBg = new Paint();
	  Paint rec1 = new Paint();
	  Paint rec2 = new Paint();
	  Paint rec3 = new Paint();
	  Paint rec4 = new Paint();
	  int rem = 0;
	  int rows = 0;
	  Paint txtp = new Paint();
	  int x;
	  int y;
	  double txtpY =0;
	  
	  public DisplayLevels(Context paramContext)
	  {
	    super(paramContext);
	  }
	  
	  public DisplayLevels(Context paramContext, AttributeSet paramAttributeSet)
	  {
	    super(paramContext, paramAttributeSet);
	  }
	  
	  public DisplayLevels(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
	  {
	    super(paramContext, paramAttributeSet, paramInt);
	  }
	  
	  public void setLevel(int paramInt)
	  {
	    this.lvl = paramInt;
	  }
	  
	  public void setLevel(int paramInt, Typeface paramTypeface)
	  {
	    this.lvl = paramInt;
	    this.myTypeface = paramTypeface;
	  }
	  
	  @Override
	  protected void onDraw(Canvas paramCanvas){
	    super.onDraw(paramCanvas);
	    this.x = (paramCanvas.getWidth()-(int)paramCanvas.getWidth()/12);
	    this.y = (paramCanvas.getHeight()-(int)paramCanvas.getWidth()/12);
	    if (this.myTypeface != null) {
		    this.txtp.setTypeface(this.myTypeface);
	    }
	    this.txtp.setTextSize(this.x / 10);
	    this.txtp.setTextAlign(Paint.Align.CENTER);
	    this.rec1.setColor(Color.RED);
		this.rec2.setColor(Color.BLUE);
		this.rec3.setColor(Color.GREEN);
		this.rec4.setColor(Color.MAGENTA);
		if(initial){
		    img = Bitmap.createScaledBitmap(this.img, this.x / 6, this.x / 6, false);
		    Rect bounds = new Rect();
		    txtp.getTextBounds("2", 0, 1, bounds);
		    txtpY= img.getHeight()-bounds.height()/2 +txtp.ascent();
		    initial=false;
		}
	    int i = img.getWidth();
	    this.circles.setAlpha(98);
	    if (this.level <= max){
	      this.rows = (this.level / 5);
	      this.rem = (this.level % 5);
	    }else{
	    	this.rows = ((-max-1 + this.level) / 5);
  	        this.rem = ((-max-1 + this.level) % 5);
	    }
	    
	    if (this.rows == 0){
	    	this.rec1.setAlpha(10);
	    	this.rec2.setAlpha(10);
	    	this.rec3.setAlpha(10);
		    this.rec4.setAlpha(10);
	    }
	    else if (this.rows == 1){
	        this.rec2.setAlpha(10);
	        this.rec3.setAlpha(10);
	        this.rec4.setAlpha(10);
	    }
	    else if (this.rows == 2){
	        this.rec3.setAlpha(10);
	        this.rec4.setAlpha(10);
	    }
	    else if (this.rows == 3) {
	        this.rec4.setAlpha(10);
	    }
	    
	    paramCanvas.drawRect(this.margin / 2, i * 1 + 1 * this.margin, this.x - this.margin / 2, i * 2 + 1 * this.margin, this.rec1);
	    paramCanvas.drawRect(this.margin / 2, i * 2 + 2 * this.margin, this.x - this.margin / 2, i * 3 + 2 * this.margin, this.rec2);
	    paramCanvas.drawRect(this.margin / 2, i * 3 + 3 * this.margin, this.x - this.margin / 2, i * 4 + 3 * this.margin, this.rec3);
	    paramCanvas.drawRect(this.margin / 2, i * 4 + 4 * this.margin, this.x - this.margin / 2, i * 5 + 4 * this.margin, this.rec4);  
	    
	    	for (int j=0; j<=rows; j++){
	    		if(j==0 && level<max){
	    			for (int m=0; m<4; m++){
	    				int rem2=rem;
	    				if(rows!=j)rem2 = 4;
	       				if (m <= rem2-1) {
	       					paramCanvas.drawBitmap(img, m * ((this.x - this.margin) / 5) + this.margin / 2 +(i/2), i + this.margin, this.circles);
	     		  	  	    paramCanvas.drawText((1+ m)+"", m * ((this.x - this.margin) / 5) + this.margin / 2 + i / 2 +(i/2), (float) (max + (i + this.margin + txtpY)), this.txtp);
	       				}	    			
	       			}
	    		}else{
		    		for (int m=0; m<5; m++){
		    			int rem2=rem;
	    				if(rows!=j)rem2 = 5;
		    			if (m <= rem2) {
		    				paramCanvas.drawBitmap(img, m * ((this.x - this.margin) / 5) + this.margin / 2, i * (1 + j) + this.margin * (1 + j), this.circles);
		    				if (this.level <= max) {
		  		  	  	      paramCanvas.drawText((m + 5 * j)+"", m * ((this.x - this.margin) / 5) + this.margin / 2 + i / 2, (float) (max + (i * (1 + j) + this.margin * (1 + j) + (i/2.0)+(txtp.ascent()/2))), this.txtp);
		  		  	  	    }else{
		  		  	  	      paramCanvas.drawText( (max+1 + (m + 5 * j))+"", m * ((this.x - this.margin) / 5) + this.margin / 2 + i / 2, (float) (max + (i * (1 +j) + this.margin * (1 +j) + (i/2.0)+(txtp.ascent()/2))), this.txtp);
		  		  	  	    }
		    			}	    			
		    		}
	    		}
	    	}

	    if (this.level < this.lvl) {
	       this.level = (1 + this.level);
	       invalidate();
	    }
	  }
}
