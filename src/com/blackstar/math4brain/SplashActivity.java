package com.blackstar.math4brain;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.ImageView;


public class SplashActivity extends Activity{
	boolean close = false;
	ImageView logo;
	Runnable mStopSplash;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);{ 
        	logo =(ImageView) findViewById(R.id.imageViewBlackstar);
        	//show logo for 2.5 seconds
        	final Handler mHandler = new Handler();
        	
        	mStopSplash = new Runnable() {   
            	@Override
				public void run() {
            		if(!close){
	            		Animation newAnimation = new AlphaAnimation(1,0);
	                    newAnimation.setDuration(500);
	                    logo.startAnimation(newAnimation);
	                    mHandler.postDelayed(mStopSplash, 500);
	                    close = true;
            		}
            		else{
            			logo.setVisibility(View.INVISIBLE);
	            		startActivity(new Intent("android.intent.action.MENU"));
	            		finish();
            		}
            	}
            };                  
    		mHandler.postDelayed(mStopSplash, 2500);
    		
        	//if user clicks screen stop and proceed
        	FrameLayout fl = (FrameLayout ) findViewById(R.id.frameLayout1);
        	fl.setOnClickListener (new View.OnClickListener(){
            	@Override
				public void onClick (View v){
            		startActivity(new Intent("android.intent.action.MENU"));
            		mHandler.removeCallbacks(mStopSplash);
            		finish();
            	}
            });
        }
    }
}
