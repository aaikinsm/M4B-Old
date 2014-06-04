package com.blackstar.math4brain;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Scanner;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tapjoy.TapjoyConnect;
import com.tapjoy.TapjoyDisplayAdNotifier;

public class AlarmRunActivity extends Activity implements TapjoyDisplayAdNotifier{
	
	Handler mHandler;
	Runnable mUpdateTimer, gotInput;
	MediaPlayer mp3Tick;
	double startTime = 0, nextTime=0, time=0;
	int count = 0;
	boolean init = true, update_display_ad=false;
	String review = "";
	LinearLayout adLinearLayout;
  	View adView;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mathquestion);

        TapjoyConnect.requestTapjoyConnect(getApplicationContext(),"d199877d-7cb0-4e00-934f-d04eb573aa47","1SgBmHKgJUk8cw9IOY3s");
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        final TextView showEq = (TextView) findViewById(R.id.textViewEquation);
        final TextView clock = (TextView) findViewById(R.id.textViewTimer);
        final TextView showIn = (TextView) findViewById(R.id.textViewInput);
        final TextView result = (TextView) findViewById(R.id.textViewResult);
        final TextView info = (TextView) findViewById(R.id.textViewInfo);
        final ImageView emoticon0 = (ImageView) findViewById(R.id.ImageView00);
        final ImageView emoticon1 = (ImageView) findViewById(R.id.ImageView01);
        final ImageView emoticon2 = (ImageView) findViewById(R.id.ImageView02);
        final ImageView emoticon3 = (ImageView) findViewById(R.id.ImageView03);
        final ImageView emoticon4 = (ImageView) findViewById(R.id.ImageView04);
        final ImageView emoticon5 = (ImageView) findViewById(R.id.ImageView05);
        final ImageView emoticon6 = (ImageView) findViewById(R.id.ImageView06);
        final ImageButton b0 = (ImageButton) findViewById(R.id.button0);
        final ImageButton b1 = (ImageButton) findViewById(R.id.button1);
        final ImageButton b2 = (ImageButton) findViewById(R.id.button2);
        final ImageButton b3 = (ImageButton) findViewById(R.id.button3);
        final ImageButton b4 = (ImageButton) findViewById(R.id.button4);
        final ImageButton b5 = (ImageButton) findViewById(R.id.button5);
        final ImageButton b6 = (ImageButton) findViewById(R.id.button6);
        final ImageButton b7 = (ImageButton) findViewById(R.id.button7);
        final ImageButton b8 = (ImageButton) findViewById(R.id.button8);
        final ImageButton b9 = (ImageButton) findViewById(R.id.button9);
        final ImageButton pass = (ImageButton) findViewById(R.id.buttonPass);
        final ImageButton clear = (ImageButton) findViewById(R.id.buttonClr);
        final Button next = (Button) findViewById(R.id.buttonNext);
        final Button back = (Button) findViewById(R.id.buttonBack);
        final FrameLayout numPad = (FrameLayout) findViewById(R.id.frameLayoutNumPad);
        final MediaPlayer mp3Correct = MediaPlayer.create(this, R.raw.correct);
        final MediaPlayer mp3Timeup = MediaPlayer.create(this, R.raw.wrong);
        mp3Tick = MediaPlayer.create(this, R.raw.ticktok);
        final GameSettings gSettings = new GameSettings();       
        final Vibrator vb = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE); 
        mHandler = new Handler();
        final String FILENAME = "m4bfile1";
        final int[] aScores = new int [3];
        Tips tp = new Tips();
        Typeface myTypeface = Typeface.createFromAsset(getAssets(), "digital.ttf");
        Typeface myTypeface2 = Typeface.createFromAsset(getAssets(), "fawn.ttf");
        showEq.setTypeface(myTypeface2);
        showIn.setTypeface(myTypeface2);
        result.setTypeface(myTypeface2);
        info.setTypeface(myTypeface2);
        clock.setTypeface(myTypeface);
        
        //Display ad
      	TapjoyConnect.getTapjoyConnectInstance().enableDisplayAdAutoRefresh(true);
      	TapjoyConnect.getTapjoyConnectInstance().getDisplayAdWithCurrencyID("684e6285-de7c-47bb-9341-3afbbfeb6eea", this);
      	adLinearLayout = (LinearLayout)findViewById(R.id.AdLinearLayout1);
                
        //get user settings then create equation 
        try {
			int num1, num2, num3;	
			FileInputStream fi = openFileInput(FILENAME);
			Scanner in = new Scanner(fi);
			in.next();
			num1= Integer.parseInt(in.next());
			in.next();
			num2= Integer.parseInt(in.next());
			in.next();
			num3= Integer.parseInt(in.next());
			in.next(); in.next(); in.next(); 
			aScores[0] = Integer.parseInt(in.next());
			aScores[1] = Integer.parseInt(in.next());
			aScores[2] = Integer.parseInt(in.next());
			
			
			gSettings.equationType = num1;
			gSettings.sound = num2;
			gSettings.difficulty = num3;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        mp3Timeup.start();
        final Equation eq = new Equation(gSettings.equationType, gSettings.difficulty, this);
        final double setTime = gSettings.clock;
        //Initial brain fact dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Brain Fact") ;
        builder.setMessage(tp.getTip(false, getResources()));
        builder.setCancelable(false);
        builder.setPositiveButton("START", new DialogInterface.OnClickListener() {
        	@Override
			public void onClick(DialogInterface dialog, int id) {
        		gSettings.start =true;
        		eq.createNew();
        		showEq.setText(eq.getEquation());
        		startTime = System.currentTimeMillis();
        		gSettings.inputTimer = -1;
        		init=false;
        		dialog.cancel();
        	}       
        });
        builder.show();
        
        
        showEq.setText("Press Start to Begin");
        showIn.setText("");
 
        
        //Decrement game timer and check if time is up
        mUpdateTimer = new Runnable() {   
        	@Override
			public void run() {
        		if (gSettings.start){ 
        			nextTime = System.currentTimeMillis();
        			if(startTime!=nextTime){
        				String sTim = (nextTime-startTime)+"";
        				if (sTim.length()<5) time=10;
        				else time = setTime - Double.parseDouble(sTim.substring(0,sTim.length()-5)+"."+sTim.substring(sTim.length()-5,sTim.length()-4));
        				gSettings.clock = time;
        			}
        			//Quick fix
        			try{
        				if(gSettings.sound==1 && mp3Tick.isPlaying()== false && Double.parseDouble(gSettings.getClock())<10){
	        	        	mp3Tick.start();
	        	        	mp3Tick.setLooping(true);
	        			}
        			} catch(Exception E){}
        		}
        		clock.setText(gSettings.getClock());
        		if(gSettings.clock==0){
        			if (update_display_ad){
            			// Remove all subviews of our ad layout.
            			adLinearLayout.removeAllViews();
            			
            			// Add the ad to our layout.
            			adLinearLayout.addView(adView);
            			
            			update_display_ad = false;
            		}   
        			//Quick fix
        			try{
        			if(gSettings.sound==1) mp3Timeup.start();
        			}catch(Exception E){}
        			vb.vibrate(1000);
            		showEq.setText("Time Is Up");
            		//result.setText("You earned "+gSettings.getPoints()+" points");
            		gSettings.timeUp = true;
            		numPad.setVisibility(View.GONE);
            		showIn.setText("");
            		aScores[0] += gSettings.getPoints();
            		aScores[1] += 1;
            		int average=aScores[0]/aScores[1];
            		info.setText("Your Average Score is "+average+ gSettings.getPointCalculation(getApplicationContext()));
            		if (gSettings.getPoints()>aScores[2]){
            			info.setText("NEW HIGH SCORE!"+ gSettings.getPointCalculation(getApplicationContext()));
            			aScores[2]=gSettings.getPoints();
            		}
            		if(review.equals(""))info.setText("\n"+info.getText());
            		else info.setText("\n"+info.getText()+"*REVIEW*"+review+"\n");
            		info.setBackgroundResource(R.drawable.shadow_bg);
            		next.setText("Try Again");
            		next.setVisibility(1);
            		back.setText("Exit");
            		back.setVisibility(1);
            		
            		//set emoticons
            		if (gSettings.getPoints()>average+10){
            			emoticon6.setVisibility(1);
            		}else if (gSettings.getPoints()>average+5){
            			emoticon5.setVisibility(1);
            		}else if (gSettings.getPoints()>=average+2){
            			emoticon4.setVisibility(1);
            		}else if (gSettings.getPoints()>=average-2){
            			emoticon3.setVisibility(1);
            		}else if (gSettings.getPoints()>average-5){
            			emoticon2.setVisibility(1);
            		}else if (gSettings.getPoints()>average-10){
            			emoticon1.setVisibility(1);
            		}else{
            			emoticon0.setVisibility(1);
            		}
            		
            		try {
            			String content="";
            			FileInputStream fi = openFileInput(FILENAME);
            			Scanner in = new Scanner(fi);
            			for (int i= 0; i<8; i++){
            				content += in.next()+" ";
            			}
            			content +=("\nScores: "+aScores[0]+" "+aScores[1]+" "+aScores[2]);
            			in.next(); in.next(); in.next(); in.next();
            			while(in.hasNext()){
            				content+= (" "+in.next());
            			}
            			OutputStreamWriter out = new OutputStreamWriter(openFileOutput(FILENAME,0)); 
            			out.write(content);
            			out.close();    
            		} catch (IOException e) {
            			// TODO Auto-generated catch block
            			e.printStackTrace();
            		}
            	} else{ 
            		mHandler.postDelayed(this,100);
            	}     		
        	}
        };
        mHandler.removeCallbacks(mUpdateTimer);            
		mHandler.postDelayed(mUpdateTimer, 100);
        
		//Decrement input timer an check if input is correct
        gotInput = new Runnable() {   
        	@Override
			public void run() { 
        		if(gSettings.timeUp){
        			showIn.setText("");
        			result.setTextColor(Color.rgb(0,0,0));
        			//Quick fix
        			try{
        			mp3Tick.stop();
        			}catch(Exception E){}
        			//count down
        			if(count<gSettings.getPoints()&& gSettings.getPoints()>10){
        				count= count+(gSettings.getPoints()/10);
        				result.setText("You earned "+count+" points");
        			} else result.setText("You earned "+gSettings.getPoints()+" points");
        		}else{
        			//restart if user is inactive for too long
        			if(gSettings.inputTimer == -100 && !init){
        				startActivity(new Intent("android.intent.action.ALARMRUN"));
                		finish();
        			}
	        		gSettings.inputTimer -= 1;
	        		if(showIn.getText().equals(eq.getAnswer())){
	        			try{
	        			if(gSettings.sound==1) mp3Correct.start();
	        			}catch(Exception E){}
	        			gSettings.score +=1;
	        			result.setText("");
	        			eq.createNew();
	        	        showEq.setText(eq.getEquation());
	        	        showIn.setText("");
	        	        gSettings.inputTimer = -1;
	        		}else{
		        		if (gSettings.inputTimer == 0){
		        			result.setTextColor(Color.rgb(200,0,0));
			        		result.setText("X");
			        		vb.vibrate(500);
			       			showIn.setText("");
			       			gSettings.wrong+=1;
		        		}
	        		}
        		}
        		if (init)vb.vibrate(150);
        		mHandler.postDelayed(this,200);
        	}
        };
        mHandler.removeCallbacks(gotInput);            
		mHandler.postDelayed(gotInput, 100);
		
		
        
		b0.setOnClickListener (new View.OnClickListener(){
        	@Override
			public void onClick (View v){
        		vb.vibrate(15);
        		showIn.setText(showIn.getText()+"0");
        		gSettings.inputTimer=5;
        	}
        });
        b1.setOnClickListener (new View.OnClickListener(){
        	@Override
			public void onClick (View v){
        		vb.vibrate(15);
        		showIn.setText(showIn.getText()+"1");
        		gSettings.inputTimer=5;
        	}
        });
        b2.setOnClickListener (new View.OnClickListener(){
        	@Override
			public void onClick (View v){
        		vb.vibrate(15);
        		showIn.setText(showIn.getText()+"2");
        		gSettings.inputTimer=5;
        	}
        });
        b3.setOnClickListener (new View.OnClickListener(){
        	@Override
			public void onClick (View v){
        		vb.vibrate(15);
        		showIn.setText(showIn.getText()+"3");
        		gSettings.inputTimer=5;
        	}
        });
        b4.setOnClickListener (new View.OnClickListener(){
        	@Override
			public void onClick (View v){
        		vb.vibrate(15);
        		showIn.setText(showIn.getText()+"4");
        		gSettings.inputTimer=5;
        	}
        });
        b5.setOnClickListener (new View.OnClickListener(){
        	@Override
			public void onClick (View v){
        		vb.vibrate(15);
        		showIn.setText(showIn.getText()+"5");
        		gSettings.inputTimer=5;
        	}
        });
        b6.setOnClickListener (new View.OnClickListener(){
        	@Override
			public void onClick (View v){
        		vb.vibrate(15);
        		showIn.setText(showIn.getText()+"6");
        		gSettings.inputTimer=5;
        	}
        });
        b7.setOnClickListener (new View.OnClickListener(){
        	@Override
			public void onClick (View v){
        		vb.vibrate(15);
        		showIn.setText(showIn.getText()+"7");
        		gSettings.inputTimer=5;
        	}
        });
        b8.setOnClickListener (new View.OnClickListener(){
        	@Override
			public void onClick (View v){
        		vb.vibrate(15);
        		showIn.setText(showIn.getText()+"8");
        		gSettings.inputTimer=5;
        	}
        });
        b9.setOnClickListener (new View.OnClickListener(){
        	@Override
			public void onClick (View v){
        		vb.vibrate(15);
        		showIn.setText(showIn.getText()+"9");
        		gSettings.inputTimer=5;
        	}
        });
        
        clear.setOnClickListener (new View.OnClickListener(){
        	@Override
			public void onClick (View v){
        		vb.vibrate(15);
        		showIn.setText("");
        		gSettings.inputTimer=-1;
        	}
        });
        
        pass.setOnClickListener (new View.OnClickListener(){
        	@Override
			public void onClick (View v){
        		result.setText("");
        		review += "\n"+eq.getEquation() + eq.getAnswer();
        		eq.createNew();
                showEq.setText(eq.getEquation());
                showIn.setText("");
        		gSettings.wrong+=1;
        		vb.vibrate(500);
        	}
        });
        
        next.setOnClickListener (new View.OnClickListener(){
        	@Override
			public void onClick (View v){
        		startActivity(new Intent("android.intent.action.ALARMRUN"));
        		finish();
        	}
        });
        back.setOnClickListener (new View.OnClickListener(){
        	@Override
			public void onClick (View v){
        		finish();
        	}
        });
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacks(mUpdateTimer);
        mHandler.removeCallbacks(gotInput);
        //Quick fix
        try{
        mp3Tick.stop();
        }catch(Exception E){}
    }
    @Override
    public void onBackPressed(){
    	//Do nothing when back button is pressed
    }
    
    @Override
	public void getDisplayAdResponse(View view)
	{
		// Using screen width, but substitute for the any width.
		int desired_width = adLinearLayout.getMeasuredWidth();
		
		// Scale the display ad to fit incase the width is smaller than the display ad width.
		adView = scaleDisplayAd(view, desired_width);
		
		update_display_ad = true;
	}

	// Notifier for a failed display ad request.
	@Override
	public void getDisplayAdResponseFailed(String error){
		System.out.println("failed ad display request");
	}
	public static View scaleDisplayAd(View adView, int targetWidth)
	{
		int adWidth = adView.getLayoutParams().width;
		int adHeight = adView.getLayoutParams().height;

		// Scale if the ad view is too big for the parent view.
		if (adWidth > targetWidth)
		{
			int scale;
			int width = targetWidth;
			Double val = Double.valueOf(width) / Double.valueOf(adWidth);
			val = val * 100d;
			scale = val.intValue();

			((android.webkit.WebView) (adView)).getSettings().setSupportZoom(true);
			((android.webkit.WebView) (adView)).setPadding(0, 0, 0, 0);
			((android.webkit.WebView) (adView)).setVerticalScrollBarEnabled(false);
			((android.webkit.WebView) (adView)).setHorizontalScrollBarEnabled(false);
			((android.webkit.WebView) (adView)).setInitialScale(scale);

			// Resize banner to desired width and keep aspect ratio.
			LayoutParams layout = new LayoutParams(targetWidth, (targetWidth*adHeight)/adWidth);
			adView.setLayoutParams(layout);
		}

		return adView;
	}
}
