package com.blackstar.math4brain;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.UUID;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.blackstar.math4brain.util.IabHelper;
import com.blackstar.math4brain.util.IabResult;
import com.blackstar.math4brain.util.Inventory;
import com.blackstar.math4brain.util.Purchase;
import com.flurry.android.FlurryAgent;
import com.tapjoy.TapjoyConnect;
import com.tapjoy.TapjoyNotifier;
import com.yasesprox.android.transcommusdk.TransCommuActivity;


public class MainMenu extends Activity implements TapjoyNotifier{
	int minPointsPro = 5000, points, FILESIZE=25, tries=3;
	MediaPlayer mp3Bg;
	GameSettings gSettings;
	String FILENAME = "m4bfile1", FILEPRO = "m4bfilePro1",  FILEMULT = "m4bfileMul";
	boolean resumable = false, pro = false, blackberry=false, amazon=false, 
			connection = true, billUsed=false, openPurchase = false;
	TextView tv;
	String[] gFile = new String[FILESIZE];
	String sku = "pro_version";
	IabHelper mHelper;
	Activity activity = this;
	LinearLayout menuSpace;
	MediaPlayer mp3Click;
	Typeface myTypeface;


    @Override
    public void onCreate(Bundle savedInstanceState) {		
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu);
        final Button practice = (Button) findViewById(R.id.buttonPractice);
        final Button minRun = (Button) findViewById(R.id.button60SRun);
        final Button challenge = (Button) findViewById(R.id.buttonChallenge);
        final Button faceOff = (Button) findViewById(R.id.buttonFaceOff);
        final Button settings = (Button) findViewById(R.id.ButtonSettings);
        final Button user = (Button) findViewById(R.id.ButtonUser);
        final Button exit = (Button) findViewById(R.id.buttonExit);
        final ImageView logo = (ImageView) findViewById(R.id.imageViewLogo);
        final TextView version = (TextView) findViewById(R.id.version);
        menuSpace = (LinearLayout) findViewById(R.id.linearLayoutMenu);
        mp3Bg = MediaPlayer.create(this, R.raw.main_bg_music);
        mp3Click = MediaPlayer.create(this, R.raw.click);
        gSettings = new GameSettings();
        tv = (TextView) findViewById(R.id.textViewTip);
        //set fonts
        myTypeface = Typeface.createFromAsset(getAssets(), "fawn.ttf");
        tv.setTypeface(myTypeface);  exit.setTypeface(myTypeface);
        practice.setTypeface(myTypeface); minRun.setTypeface(myTypeface); challenge.setTypeface(myTypeface);
        faceOff.setTypeface(myTypeface); settings.setTypeface(myTypeface); user.setTypeface(myTypeface);
        
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if(cm.getActiveNetworkInfo() == null) connection = false;
        
        if(android.os.Build.BRAND.toLowerCase().contains("blackberry"))blackberry=true;
        else if(android.os.Build.MODEL.toLowerCase().contains("kindle"))amazon=true; 
        
        Tips tp = new Tips();        

        Bundle extras = getIntent().getExtras();
		if (extras != null) {
		    if (extras.getString("purchase_dialog").equals("true")) openPurchase=true;
		}
		
		//start tapjoy
        TapjoyConnect.requestTapjoyConnect(getApplicationContext(),"d199877d-7cb0-4e00-934f-d04eb573aa47","1SgBmHKgJUk8cw9IOY3s");       

        //if file not found/not created yet, jump to next catch block
        //if file is old format convert to new format
        try{
        	//read
        	FileInputStream fi = openFileInput(FILENAME);
			Scanner in = new Scanner(fi);
			int i = -2;
			String data = "", dat="", scan="", uid="";
			while(in.hasNext() && !scan.equals("null")){
				i++;
				data += scan+" ";
				scan = in.next();
				if (i==11){
					dat=data;
					uid=scan;
				}

			}
			in.close();
			//very old file format
			if(i==12 || !uid.substring(8,9).equals("-")){
				String myUID = UUID.randomUUID().toString().substring(0,10);
				data = (dat+" "+myUID+" User:_no_name ");
				i=13;
			}
			//write new file
			if(i<15){
				try {
					Toast.makeText(getApplicationContext(), R.string.welcome_back,Toast.LENGTH_LONG).show();
	    			OutputStreamWriter out = new OutputStreamWriter(openFileOutput(FILENAME,0)); 
	    			out.write(data+=" music: 1  vibrate: 1");
	    			out.close();       
	        	} catch (IOException z) {
	        		z.printStackTrace(); 
	        	}
			}
        }catch (FileNotFoundException e) {
			e.printStackTrace();
			String myUID = UUID.randomUUID().toString().substring(0,10);
			try {
				Toast.makeText(getApplicationContext(), R.string.welcome_to_m4b,Toast.LENGTH_LONG).show();
				String c1 = "Type: 12  Sound: 1  Difficulty: 2 ";
				String c2 = "Level: 1  Scores: 0 0 0 ";
				String c3 = " User:_no_name  music: 1  vibrate: 1 rate_popup: 0 mic: 0";
    			OutputStreamWriter out = new OutputStreamWriter(openFileOutput(FILENAME,0)); 
    			out.write(c1+c2+myUID+c3);
    			out.close();       
    			mp3Bg.start();
		        mp3Bg.setLooping(true);
        	} catch (IOException z) {
        		z.printStackTrace(); 
        	} catch (Exception E) {};
		}
        
        //read main file
        try {
			FileInputStream fi = openFileInput(FILENAME);
			Scanner in = new Scanner(fi);
			int i = 0;
			while(in.hasNext()){
				gFile[i] = in.next();
				i++;
			}
			gSettings.sound = Integer.parseInt(gFile[3]);
			gSettings.music = Integer.parseInt(gFile[15]);
			points = Integer.parseInt(gFile[9]);
			if (points >= minPointsPro) pro = true;
			if (gSettings.music == 1){
				try{
		        mp3Bg.start();
		        mp3Bg.setLooping(true);
				}catch(Exception E){}
			}
			in.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (NumberFormatException n) {
			Toast.makeText(getApplicationContext(), R.string.file_is_corrupt,Toast.LENGTH_LONG).show();
			animateTransition("android.intent.action.USER");
		}
         
        //read pro file
        try {
        	//if file not found/not created yet, jump to next catch block
			FileInputStream fi = openFileInput(FILEPRO);
			logo.setImageResource(R.drawable.math4thebrain_pro_logo);
			pro = true;
		} catch (FileNotFoundException e) {
			if (pro){
				createPro();
			}
        }
        
        //show brain fact
        tv.setText(tp.getTip(pro, getResources()));
        
        //user data to report to flurry analytics
        final Map<String, String> userParams = new HashMap<String, String>();
        userParams.put("Name", gFile[13]); 
        userParams.put("Type", gFile[1]); 
        userParams.put("Difficulty", gFile[5]);
        userParams.put("Level", gFile[7]);
        
        //In app billing setup
        if(!blackberry && connection && !pro){
        	String base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA0AJ6MHObBIHIoexJCqMlkm2ZMfZ/";
        	base64EncodedPublicKey +="nV5Z1nR1IVRlkFT6iKLk6VsS/mh90HlDzh9QRELNd1Fw1gix3Y0jelNNAU3h6UQE1964HGDCu1PBtZadmlt";
        	base64EncodedPublicKey +="RX4ofD+5OFgBElmTDFAuhCHxeUFsY0IM+OsPSYYp5tNu0UvA+4NakRVR33JVwOWzTrrUcZaRRsd1mYgz47ihvotn/";
        	base64EncodedPublicKey +="d5Lhm8HnERnZLKYo2jKfwZYg9ped11lafvfsJu2dZC2gJuRvY+MzQZ9bo28Fm+cFT6MMU+FhgMnctzoXQE6fgit/";
        	base64EncodedPublicKey +="gXyJMUEypwR6whDufn/LqZTPrdYWqPl2WVMwUUkPHjYMqyUYELaTCQIDAQAB";
        	   // compute your public key and store it in base64EncodedPublicKey
        	mHelper = new IabHelper(this, base64EncodedPublicKey);
        	   
        	mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
        		@Override
				public void onIabSetupFinished(IabResult result) {
        		    if (!result.isSuccess()) {
        		         // Oh no, there was a problem.
        		         Log.d("INAPP BILLING", "Problem setting up In-app Billing: " + result);
        		         connection = false;
        		    }else{            
        		         // Hooray, IAB is fully set up! 
        		    	// if user has requested a purchase then open in-app billing else check to see if already purchased
        		    	if(openPurchase){
        		    		try{
        		    			mHelper.launchPurchaseFlow(activity, sku, 10001, mPurchaseFinishedListener, gFile[13]);
        		    			billUsed = true;
        		    		}catch(NullPointerException e){finish();}
        		    	}else mHelper.queryInventoryAsync(mGotInventoryListener);
        		    }
        		}
        	});
        }
        
        
        //Get Version and display
        try {
        	PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
			version.setText("v"+pInfo.versionName);
		} catch (NameNotFoundException e1) {
			e1.printStackTrace();
		}
        
        
        practice.setOnClickListener (new View.OnClickListener(){
        	@Override
			public void onClick (View v){
        		animateTransition("android.intent.action.PRACTICE");
        		//check
                /*try {
        			FileInputStream i = openFileInput(FILENAME);
        			Scanner n = new Scanner(i);
        			String ct="";
        			while (n.hasNextLine()){
        				ct += n.nextLine();
        			}
        			tv.setText(ct);
        		} catch (FileNotFoundException e1) {
        			e1.printStackTrace();
        		}*/
        	}
        });
        
        minRun.setOnClickListener (new View.OnClickListener(){
        	@Override
			public void onClick (View v){
        		animateTransition("android.intent.action.MINUTERUN");        		
        		FlurryAgent.logEvent("Minute_Run", userParams);
        	}
        });
        
        settings.setOnClickListener (new View.OnClickListener(){
        	@Override
			public void onClick (View v){
        		animateTransition("android.intent.action.SETTINGS");
        		FlurryAgent.logEvent("Settings");
        	}
        });
        
        user.setOnClickListener (new View.OnClickListener(){
        	@Override
			public void onClick (View v){
        		animateTransition("android.intent.action.USER");
        		FlurryAgent.logEvent("User_info");
        	}
        });
        
        challenge.setOnClickListener (new View.OnClickListener(){
        	@Override
			public void onClick (View v){
                if (resumable)resumeDialog();
                else animateTransition("android.intent.action.CHALLENGE");
                FlurryAgent.logEvent("Challenge");
        	}
        });
        
        faceOff.setOnClickListener (new View.OnClickListener(){
        	@Override
			public void onClick (View v){
        		multiplayerDialog();
        		FlurryAgent.logEvent("Multiplayer");
        	}
        });
        
    }
    
    @Override
    public void onStart() {
        super.onStart();
      //check level
        try {
			FileInputStream ck = openFileInput(FILENAME);
			Scanner in = new Scanner(ck);
			for(int i =0; i<7; i++){
				in.next();
			}
			if(Integer.parseInt(in.next())>1){
				resumable=true;
			}
			else resumable = false;
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
    }
    
    @Override
    public void onDestroy() {    	
        super.onDestroy();
        try{
            mp3Bg.stop();
        }catch(Exception E){};
        //stop tapjoy
        TapjoyConnect.getTapjoyConnectInstance().sendShutDownEvent();
        //set game reminder for a week
        Intent intent = new Intent(this, NotificationReceiver.class);
	    PendingIntent pendingIntent = PendingIntent.getBroadcast(this,002000,intent,0);
	    AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
	    alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 604800000, pendingIntent);
	    //set reminder for 3 days
	    PendingIntent pendingIntent2 = PendingIntent.getBroadcast(this,001000,intent,0);
	    alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 259200000, pendingIntent2);
	    //stop flurry
        FlurryAgent.onEndSession(this);
        //unbind inApp billing service
        if(connection){
	        if (mHelper != null) mHelper.dispose();
	        mHelper = null;
        }
    }
    
    @Override
    public void onStop() {
        super.onStop();
    }
    
    @Override
    public void onPause() {
        super.onPause();
        try{
        if(mp3Bg.isPlaying()) mp3Bg.pause();
        }catch(Exception E){};
    }
    
    @Override
    public void onResume() {
        super.onResume();
        //set sound and check if qualified for pro
        reload();
		int tPoints=0, ratePopup=0; 
		tPoints = Integer.parseInt(gFile[9]);
		gSettings.music = Integer.parseInt(gFile[15]);	
		gSettings.sound = Integer.parseInt(gFile[3]);
		try{
			if (gSettings.music == 1){
		        mp3Bg.start();
		        mp3Bg.setLooping(true);
			}else{
				if(mp3Bg.isPlaying()) mp3Bg.pause();
			}
		}catch(NullPointerException e){}
		//Check and restart if pro version has been unlocked
		if(!pro && tPoints >= minPointsPro){
			startActivity(new Intent("android.intent.action.MENU"));
        	finish();
		}
		//check if rating is active
		try{
			ratePopup=Integer.parseInt(gFile[19]);
		}catch(Exception e){
			gFile[18]= "rate_popup:";
			gFile[19]= "0";
			write();
		}
		
		//set feedback frequency
        int fb = (int) (Math.random()*(50)) ;
        
        if ((fb==4 || fb==5) && ratePopup==0  && Integer.parseInt(gFile[7])>2 && connection){
        	//Google Play rating dialog
        	final Dialog dialog = new Dialog(this);
    		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
    		dialog.setContentView(R.layout.dialogbox);
    		TextView title = (TextView) dialog.findViewById(R.id.textViewTitle);
			title.setVisibility(View.VISIBLE);
			title.setText(this.getString(R.string.rate));
    		TextView body = (TextView) dialog.findViewById(R.id.textViewMsg);
    		body.setText(R.string.rate_msg);
    		Button dialogButton = (Button) dialog.findViewById(R.id.button1);    		
    		dialogButton.setVisibility(View.VISIBLE);   		    		
    		dialogButton.setText(R.string.rate);
    		dialogButton.setOnClickListener (new View.OnClickListener(){
            	@Override
				public void onClick (View v) {
            		try{
            			if(amazon)startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.amazon.ca/Blackstar-Math-For-The-Brain/dp/B00DR7TK6I"))); 
            			else if(blackberry)startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://appworld.blackberry.com/webstore/content/20484402/"))); 
            			else startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.blackstar.math4brain"))); 
        			}catch(Exception E){
            			startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=com.blackstar.math4brain")));
        			}
            		gFile[19]="1";
            		write();
	        		dialog.dismiss();
    			}
    		});
    		Button dialogButton2 = (Button) dialog.findViewById(R.id.button2);
    		dialogButton2.setVisibility(View.VISIBLE);
    		dialogButton2.setText(R.string.perhaps_later);
    		dialogButton2.setOnClickListener (new View.OnClickListener(){
            	@Override
				public void onClick (View v) {
	        		dialog.dismiss();
    			}
    		});
    		Button dialogButton3 = (Button) dialog.findViewById(R.id.button3);
    		dialogButton3.setVisibility(View.VISIBLE);
    		dialogButton3.setText(R.string.no_thanks);
    		dialogButton3.setOnClickListener (new View.OnClickListener(){
            	@Override
				public void onClick (View v) {
            		gFile[19]="1";
            		write();
	        		dialog.dismiss();
    			}
    		});
    		dialog.show();
        	
        }
        
        if (fb==6 && resumable && connection){
	        //Leave email feedback dialog
        	final Dialog dialog = new Dialog(this);
    		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
    		dialog.setContentView(R.layout.dialogbox);
    		dialog.setCancelable(false);
    		TextView title = (TextView) dialog.findViewById(R.id.textViewTitle);
			title.setVisibility(View.VISIBLE);
			title.setText(this.getString(R.string.leave_feedback));
    		TextView body = (TextView) dialog.findViewById(R.id.textViewMsg);
    		body.setText(R.string.feedback);
    		Button dialogButton = (Button) dialog.findViewById(R.id.button1);    		
    		dialogButton.setVisibility(View.VISIBLE);   		    		
    		dialogButton.setText(R.string.leave_feedback);
    		dialogButton.setOnClickListener (new View.OnClickListener(){
            	@Override
				public void onClick (View v) {
            		Intent i = new Intent(Intent.ACTION_SEND);
	        		i.setType("text/plain");
	        		i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"blackstar.feedback@gmail.com"});
	        		i.putExtra(Intent.EXTRA_SUBJECT, R.string.email_subject);
	        		i.putExtra(Intent.EXTRA_TEXT   , "");
	        		try {
	        		    startActivity(Intent.createChooser(i, getString(R.string.send_email_using)));
	        		} catch (android.content.ActivityNotFoundException ex) {
	        		    Toast.makeText(MainMenu.this, R.string.no_email_client, Toast.LENGTH_SHORT).show();
	        		}
	        		dialog.dismiss();
    			}
    		});
    		Button dialogButton2 = (Button) dialog.findViewById(R.id.button2);
    		dialogButton2.setVisibility(View.VISIBLE);
    		dialogButton2.setText(R.string.perhaps_later);
    		dialogButton2.setOnClickListener (new View.OnClickListener(){
            	@Override
				public void onClick (View v) {
	        		dialog.dismiss();
    			}
    		});
    		dialog.show();
        }
        
        if (fb==7 && !blackberry && points>0 && connection && !pro){
			//open tapjoy dialog
        	final Dialog dialog = new Dialog(this);
    		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
    		dialog.setContentView(R.layout.dialogbox);
    		TextView body = (TextView) dialog.findViewById(R.id.textViewMsg);
    		body.setText(this.getString(R.string.get_free_points));
    		Button dialogButton = (Button) dialog.findViewById(R.id.button1);    		
    		dialogButton.setVisibility(View.VISIBLE);   		    		
    		dialogButton.setText(R.string.yes);
    		dialogButton.setOnClickListener (new View.OnClickListener(){
            	@Override
				public void onClick (View v) {
            		Intent i = new Intent(getApplicationContext(), TapJoyLauncher.class);
	        		i.putExtra("view_offers","true");
	        		startActivity(i);
	        		dialog.dismiss();
    			}
    		});
    		Button dialogButton2 = (Button) dialog.findViewById(R.id.button2);    		
    		dialogButton2.setVisibility(View.VISIBLE);   		    		
    		dialogButton2.setText(R.string.no);
    		dialogButton2.setOnClickListener (new View.OnClickListener(){
            	@Override
				public void onClick (View v) {
	        		dialog.dismiss();
    			}
    		});
    		dialog.show();
		}
        
        
        if ((fb==8 || fb==9 || fb==10) && !blackberry && points>0 && connection && !pro && !billUsed){
			//open dialog for purchase request
        	final Dialog dialog = new Dialog(this);
    		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
    		dialog.setContentView(R.layout.dialogbox);
    		TextView title = (TextView) dialog.findViewById(R.id.textViewTitle);
			title.setVisibility(View.VISIBLE);
			title.setText(this.getString(R.string.get_pro_version));
    		TextView body = (TextView) dialog.findViewById(R.id.textViewMsg);
    		body.setText(R.string.pro_features);
    		Button dialogButton = (Button) dialog.findViewById(R.id.button1);    		
    		dialogButton.setVisibility(View.VISIBLE);   		    		
    		dialogButton.setText(R.string.yes);
    		dialogButton.setOnClickListener (new View.OnClickListener(){
            	@Override
				public void onClick (View v) {
            		mHelper.launchPurchaseFlow(activity, sku, 10001, mPurchaseFinishedListener, gFile[13]);
            		dialog.dismiss();
    			}
    		});
    		Button dialogButton2 = (Button) dialog.findViewById(R.id.button2);    		
    		dialogButton2.setVisibility(View.VISIBLE);   		    		
    		dialogButton2.setText(R.string.no);
    		dialogButton2.setOnClickListener (new View.OnClickListener(){
            	@Override
				public void onClick (View v) {
	        		dialog.dismiss();
    			}
    		});
    		dialog.show();
		}
        
        if ((fb==11 || fb==12 || fb==13) && getResources().getConfiguration().locale.toString().contains("en") && points>0 && connection){
			//open translate dialog
        	final Dialog dialog = new Dialog(this);
    		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
    		dialog.setContentView(R.layout.dialogbox);
    		TextView body = (TextView) dialog.findViewById(R.id.textViewMsg);
    		body.setText("Help translate this application into another language?");
    		Button dialogButton = (Button) dialog.findViewById(R.id.button1);    		
    		dialogButton.setVisibility(View.VISIBLE);   		    		
    		dialogButton.setText(R.string.yes);
    		dialogButton.setOnClickListener (new View.OnClickListener(){
            	@Override
				public void onClick (View v) {
            		Intent intent = new Intent(getApplicationContext(), TransCommuActivity.class);
            		intent.putExtra(TransCommuActivity.APPLICATION_CODE_EXTRA, "DxYEruZOPP");
            		startActivity(intent);       
	        		dialog.dismiss();
    			}
    		});
    		Button dialogButton2 = (Button) dialog.findViewById(R.id.button2);    		
    		dialogButton2.setVisibility(View.VISIBLE);   		    		
    		dialogButton2.setText(R.string.no);
    		dialogButton2.setOnClickListener (new View.OnClickListener(){
            	@Override
				public void onClick (View v) {
	        		dialog.dismiss();
    			}
    		});
    		dialog.show();
		}
        TapjoyConnect.getTapjoyConnectInstance().getTapPoints(this);
        
        animateTransition(null);
    }
    
    public void resumeDialog(){
    	//Challenge mode: restart or resume;   	
    	reload();
    	clickSound();
    	final Dialog dialog = new Dialog(this);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.dialogbox);
		TextView body = (TextView) dialog.findViewById(R.id.textViewMsg);
		Button dialogButton = (Button) dialog.findViewById(R.id.button1);
		final DisplayLevels localDisplayLevels = (DisplayLevels)dialog.findViewById(R.id.displayLevels1);
	    localDisplayLevels.setVisibility(View.VISIBLE);
	    localDisplayLevels.setLevel(Integer.parseInt(gFile[7]), myTypeface);
		dialogButton.setVisibility(View.VISIBLE);
		body.setText(R.string.resume_saved);
		dialogButton.setText(R.string.resume);
		dialogButton.setOnClickListener (new View.OnClickListener(){
        	@Override
			public void onClick (View v) {
        		startActivity(new Intent("android.intent.action.CHALLENGE"));
        		dialog.dismiss();
			}
		});		
		localDisplayLevels.setOnTouchListener(new View.OnTouchListener(){
			@Override
			public boolean onTouch(View arg0, MotionEvent event) {
				// TODO Auto-generated method stub
				if(event.getAction() == MotionEvent.ACTION_DOWN){
					int out = (localDisplayLevels.getSelectedLevel(event.getX(),event.getY()))-1;
					if(out>0 && out<=Integer.parseInt(gFile[7])){
						gFile[7]=out+"";
		        		write();
		        		startActivity(new Intent("android.intent.action.CHALLENGE"));
		        		dialog.dismiss();
						
					}
				}
				return false;
			}
		});
		dialog.show();
    }
    
    public void multiplayerDialog(){
    	//Multiplayer: 1 or 2;
    	clickSound();
    	final Dialog dialog = new Dialog(this);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.dialogbox);
		TextView body = (TextView) dialog.findViewById(R.id.textViewMsg);
		ImageButton dialogButton = (ImageButton) dialog.findViewById(R.id.imageButton1);
		ImageButton dialogButton2 = (ImageButton) dialog.findViewById(R.id.imageButton2);
		dialogButton.setVisibility(View.VISIBLE);
		dialogButton2.setVisibility(View.VISIBLE);
		body.setText(R.string.one_or_two_devices);
		dialogButton.setOnClickListener (new View.OnClickListener(){
        	@Override
			public void onClick (View v) {
        		clickSound();
        		FlurryAgent.logEvent("multiplayer1");
        		startActivity(new Intent("android.intent.action.MULTIPLAYER"));
        		dialog.dismiss();
			}
		});
		dialogButton2.setOnClickListener (new View.OnClickListener(){
        	@Override
			public void onClick (View v) {
        		clickSound();
        		FlurryAgent.logEvent("multiplayer2");
        		if (pro) startActivity(new Intent("android.intent.action.MULTIPLAYER2"));
        		else m2Dialog(); 
        		dialog.dismiss();
			}
		});
		dialog.show();
		
    }
    
    public void m2Dialog(){ 
    	String date="1234", msg= getResources().getString(R.string.initial_try); 
    	//check if user has exceeded his trial limit
    	try{
        	//read
        	FileInputStream fi = openFileInput(FILEMULT);
			Scanner in = new Scanner(fi);
			date = in.next(); tries=Integer.parseInt(in.next());
			in.close();
			Calendar c = Calendar.getInstance();
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
	        String currentDate = df.format(c.getTime());
	        if (!date.equals(currentDate)){
	        	tries = 3;
	        	try{
			        OutputStreamWriter out = new OutputStreamWriter(openFileOutput(FILEMULT,0)); 			
					out.write(currentDate+" "+tries);
					out.close();
		        }catch(IOException e1){}
	        }
			if(tries>1) msg =  getResources().getString(R.string.you_have)+" "+tries+" "+ getResources().getString(R.string.games_avail);
			else if (tries == 1) msg = getResources().getString(R.string.last_game);
			else  msg =  getResources().getString(R.string.no_more_games);
        }catch (FileNotFoundException e) {
        	e.printStackTrace(); 
        	try {
        		OutputStreamWriter out = new OutputStreamWriter(openFileOutput(FILEMULT,0)); 			
				out.write(date+" "+tries);
				out.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}			
        }
    	final Dialog dialog = new Dialog(this);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.dialogbox);
		dialog.setCancelable(false); 
		TextView body = (TextView) dialog.findViewById(R.id.textViewMsg);
		Button dialogButton = (Button) dialog.findViewById(R.id.button1);
		dialogButton.setVisibility(View.VISIBLE);
		dialogButton.setText(R.string.ok);
		body.setText(msg);
		dialogButton.setOnClickListener (new View.OnClickListener(){
        	@Override
			public void onClick (View v) {
        		if(tries>0){
        			startActivity(new Intent("android.intent.action.MULTIPLAYER2"));
        		}
        		dialog.dismiss();
			}
		});
		dialog.show();
    }
    
    
    public void reload(){
    	 try {
 			FileInputStream fi = openFileInput(FILENAME);
 			Scanner in = new Scanner(fi);
 			int i = 0;
 			while(in.hasNext()){
 				gFile[i] = in.next();
 				i++;
 			}
 			in.close();
 		} catch (FileNotFoundException e) {
 			e.printStackTrace();
 		}
    }
    
    public void write(){
    	try {
    		String data="";
    		for(int i=0; i<FILESIZE; i++) data+= gFile[i]+" ";
    		OutputStreamWriter out = new OutputStreamWriter(openFileOutput(FILENAME,0)); 
			out.write(data);
			out.close();
 		} catch (IOException e) {
 			e.printStackTrace();
 		}
    }

	@Override
	public void getUpdatePoints(String currency, int pointTotal) {
		if (pointTotal > 0){
			Intent i = new Intent(getApplicationContext(), TapJoyLauncher.class);
    		i.putExtra("view_offers","false");
    		startActivity(i);
		}
	}

	@Override
	public void getUpdatePointsFailed(String error) {
		// Do nothing.
	}
	
	public void createPro(){
		Toast.makeText(getApplicationContext(), R.string.pro_version_unlocked,Toast.LENGTH_LONG).show();				
		String myUID2 = UUID.randomUUID().toString().substring(0,10);
		String myUID3 = UUID.randomUUID().toString().substring(0,10);
		try {
			String cur = "currentUser: 1 curBackground: bg1 \n";
			String a1a = "User:_no_name background: bg1 \n";
			String a1b = "Type: 12  Sound: 1  Difficulty: 2 Level: 1  Scores: 0 0 0 UUID User:_no_name  music: 1  vibrate: 1 \n";
			String b1a = "User:_no_name background: bg1 \n";
			String b1b = "Type: 12  Sound: 1  Difficulty: 2 Level: 1  Scores: 0 0 0 "+myUID2+" User:_no_name  music: 1  vibrate: 1 \n";
			String c1a = "User:_no_name background: bg1 \n";
			String c1b = "Type: 12  Sound: 1  Difficulty: 2 Level: 1  Scores: 0 0 0 "+myUID3+" User:_no_name  music: 1  vibrate: 1 \n";
			String data = cur+a1a+a1b+b1a+b1b+c1a+c1b;
			OutputStreamWriter out = new OutputStreamWriter(openFileOutput(FILEPRO,0)); 
			out.write(data);
			out.close(); 
		} catch (IOException z) {
    		z.printStackTrace(); 
    	} catch (Exception E) {};
	}
	
	public void clickSound(){
	    if (this.gSettings.sound == 1) {
		    try{
		      this.mp3Click.start();
		      return;
		    }
		    catch (Exception localException) {}
	    }
	}
	
	public void animateTransition(final String intent){
		if (intent != null){
			clickSound();
			Animation newAnimation = new TranslateAnimation(0,0,0,450);
	        newAnimation.setDuration(600);
	        menuSpace.startAnimation(newAnimation);
	        newAnimation.setAnimationListener(new AnimationListener() {
	            @Override
				public void onAnimationEnd(Animation animation) {
	            	menuSpace.setVisibility(View.INVISIBLE);
	            	startActivity(new Intent(intent));
	            }
				@Override
				public void onAnimationRepeat(Animation animation) {}
				@Override
				public void onAnimationStart(Animation animation) {}
	        });
		}else{
	        Animation newAnimation = new TranslateAnimation(0,0,450,0);
	        newAnimation.setDuration(600);
	        menuSpace.setVisibility(View.VISIBLE);
	        menuSpace.startAnimation(newAnimation);
	        Animation aAnimation = new AlphaAnimation(0,1);
	        aAnimation.setDuration(1000);
	        tv.startAnimation(aAnimation);
		}
	}
	
	IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener 
	   = new IabHelper.OnIabPurchaseFinishedListener() {
	   @Override
	public void onIabPurchaseFinished(IabResult result, Purchase purchase){
	      if (result.isFailure()) {
	         Log.d("INAPP BILLING", "Error purchasing: " + result);
	         return;
	      }      
	      else if (purchase.getSku().equals(sku)) {
	         //update the UI
	    	  createPro();
	    	  startActivity(new Intent("android.intent.action.MENU"));
	    	  finish();
	      }
	   }
	};
	
	IabHelper.QueryInventoryFinishedListener mGotInventoryListener 
	   = new IabHelper.QueryInventoryFinishedListener() {
	   @Override
		public void onQueryInventoryFinished(IabResult result,Inventory inventory) {
	      if (result.isFailure()) {
	    	  Log.d("INAPP BILLING", "Error getting inventory: " + result);
	      }else if(inventory.hasPurchase(sku) && !pro){       
	    	  createPro();
	    	  startActivity(new Intent("android.intent.action.MENU"));
	    	  finish();
	      }
	   }
	};
	
	// passes result to PurchaseFinishedListener
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d("INAPP BILLING", "onActivityResult(" + requestCode + "," + resultCode + ","
                + data);

        // Pass on the activity result to the helper for handling
        if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        } else {
            Log.d("INAPP BILLING", "onActivityResult handled by IABUtil.");
        }
    }
}