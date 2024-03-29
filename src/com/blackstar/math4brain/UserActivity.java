package com.blackstar.math4brain;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

public class UserActivity extends Activity{
	
	int level=0, average =0, DISPLAYMAX=20, rank, minPointsPro= 5000, FILESIZE = 25;
	int[] aScores;
    String UName = "", VERSION, IPADRS="blackstar.herobo.com", msgs=null, FILEMSG = "m4bfileMsg", 
    		FILETRACK = "m4bfileTrack", locale=Locale.getDefault().getLanguage();
    String[] arry;
    List<String[]> uList = new ArrayList<String[]>();
    UserListAdapter listAdapter;
    boolean connected = false, newMsg = false, ready = false, amazon = false, blackberry = false, isVisible=false;
    Runnable rankTable;
    Handler mHandler = new Handler();
    
	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.information);
        arry = new String[FILESIZE]; 
        aScores = new int [3];
        final TextView info1 = (TextView) findViewById(R.id.textViewInfo1);
        final TextView info1b = (TextView) findViewById(R.id.textViewInfo1b);
        final TextView info2 = (TextView) findViewById(R.id.textViewInfo2);
        final TextView name = (TextView) findViewById(R.id.textViewTitle);
        final Button editName = (Button) findViewById(R.id.buttonEditName);
        final Button done = (Button) findViewById(R.id.buttonInfo);
        final Button share = (Button) findViewById(R.id.buttonShare);
        final Button getPoints = (Button) findViewById(R.id.buttonGetPoints);
        final EditText nameInput = (EditText) findViewById(R.id.editTextName);
        final Button viewRank = (Button) findViewById(R.id.buttonViewRank);
        final TableLayout topUsers = (TableLayout) findViewById(R.id.tableLayoutTopUsers);
        final FrameLayout stats = (FrameLayout) findViewById(R.id.frameLayoutStats);
        final FrameLayout rankReset = (FrameLayout) findViewById(R.id.frameLayoutRR);
        final ProgressBar loadBar = (ProgressBar) findViewById(R.id.progressBarLoading);
        final TrackProgressView progChart = (TrackProgressView) findViewById(R.id.trackProgressView1);
        ListView userList = (ListView) findViewById(R.id.listViewUsers);
        listAdapter = new UserListAdapter(this, R.layout.users_row, uList);
        userList.setAdapter(listAdapter);
        final String FILENAME = "m4bfile1";
        Typeface myTypeface = Typeface.createFromAsset(getAssets(), "fawn.ttf");
        name.setTypeface(myTypeface);
        if(android.os.Build.BRAND.toLowerCase().contains("blackberry"))blackberry=true;
        else if(android.os.Build.MODEL.toLowerCase().contains("kindle"))amazon=true;
        
        
        topUsers.setVisibility(View.GONE);
        info2.setText("");
        //get version
        try {
        	PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
			VERSION= ("v"+pInfo.versionName);
			if (blackberry) VERSION+="BB";
		} catch (NameNotFoundException e1) {
			e1.printStackTrace();
		}
        
      //read progress tracking data 
        	final long[][] dataT = new long[365][2];
      		FileInputStream ft;
      		try {
      			ft = openFileInput(FILETRACK);
      			Scanner in = new Scanner(ft);
      			int i = 0;
      			while(in.hasNext()){
      				dataT[i][0] = in.nextLong();
      				dataT[i][1] = in.nextLong();
      				i++;
      			}
      			progChart.update(dataT,7);
      		} catch (FileNotFoundException e) {
      			try {
      				OutputStreamWriter out = new OutputStreamWriter(openFileOutput(FILETRACK,0)); 
      				out.write("0 0 \n");
      				out.close();
      			} catch (IOException e1) {
      				e1.printStackTrace();
      			}
      			e.printStackTrace();
      		}
      		
        
        //Get User Data from file then display it
        try {	
			FileInputStream fi = openFileInput(FILENAME);
			Scanner in = new Scanner(fi);
			int i = 0;
			while(in.hasNext()){
				arry[i]=in.next();
				i++;
			}
			level = Integer.parseInt(arry[7]);
			aScores[0] = Integer.parseInt(arry[9]);
			aScores[1] = Integer.parseInt(arry[10]);
			aScores[2] = Integer.parseInt(arry[11]);
			
			UName = (arry[13]);
			name.setText(UName.replace("_", " "));
			
			int highScore = aScores[2], total = aScores[0];
			if (aScores[0]!=0 && aScores[1]!=0){
				average = aScores[0]/aScores[1];
			}
			String txt = this.getString(R.string.user_stats);
			String dat = level+"\n"+highScore+"\n"+average+"\n"+total;
			info1.setText(txt);
			info1b.setText(dat);
			
			if(UName.equals("User:_no_name")){
				Toast.makeText(getApplicationContext(), R.string.reminder_change_name,Toast.LENGTH_SHORT).show();
				name.setText(R.string.default_name);
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
        
        if(arry[10].equals("0") || blackberry) getPoints.setVisibility(View.GONE);
        getPoints.setOnClickListener (new View.OnClickListener(){
        	@Override
			public void onClick (View v){
        		Intent i = new Intent(getApplicationContext(), TapJoyLauncher.class);
        		i.putExtra("view_offers","true");
        		startActivity(i);
        		finish();
        	}
        });
        
        editName.setOnClickListener (new View.OnClickListener(){
        	@Override
			public void onClick (View v){
        		nameInput.setVisibility(1);
        		isVisible=true;
        		editName.setVisibility(View.GONE);
        	}
        });
        
        share.setOnClickListener (new View.OnClickListener(){
        	@Override
			public void onClick (View v){
        		Intent shareIntent = new Intent(Intent.ACTION_SEND);
        		shareIntent.setType("text/plain");
        		if(blackberry) shareIntent.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.i_rank)+rank+getResources().getString(R.string.share_msg)+getResources().getString(R.string.bb_link));
        		else if(amazon)shareIntent.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.i_rank)+rank+getResources().getString(R.string.share_msg)+getResources().getString(R.string.amazon_link));
        		else shareIntent.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.i_rank)+rank+getResources().getString(R.string.share_msg)+getResources().getString(R.string.google_link));
        		startActivity(Intent.createChooser(shareIntent, getResources().getString(R.string.share_using)));
        	}
        });
        
        done.setOnClickListener (new View.OnClickListener(){
        	@Override
			public void onClick (View v){
        		if (isVisible){
        			try {	        				
        				String st="", nam=(nameInput.getText()+"").replace(" ", "_");
        				if (nam.equals("")) {
        					nam = "User:_no_name ";
        					Toast.makeText(getApplicationContext(), R.string.no_name_entered,Toast.LENGTH_SHORT).show();
        				}else {
        					Toast.makeText(getApplicationContext(), R.string.user_name_changed,Toast.LENGTH_SHORT).show();
        				}
        				arry[13]=nam;
        				for (int i=0; i<FILESIZE; i++){
        					st+= arry[i]+" ";
        				}
        				OutputStreamWriter out = new OutputStreamWriter(openFileOutput(FILENAME,0)); 
    					out.write(st);
    					out.close();    
        			} catch (IOException e) {
						e.printStackTrace();
					}
        		}
        		finish();
        	}
        });
        
        progChart.setOnClickListener (new View.OnClickListener(){
        	@Override
			public void onClick (View v){
        		setContentView(R.layout.progress);
        		final TrackProgressView progChart2 = (TrackProgressView) findViewById(R.id.trackProgressView2);
        		final TrackProgressView progChart = (TrackProgressView) findViewById(R.id.trackProgressView1);
        		final TextView title = (TextView) findViewById(R.id.textViewTitle);
                final TextView days7 = (TextView) findViewById(R.id.textViewDays7);
                final TextView days30 = (TextView) findViewById(R.id.textViewDays30);
        		progChart2.update(dataT, 30);
        		progChart.update(dataT, 7);
        		title.setText(R.string.prog_chart);
        		days7.setText("7 "+getResources().getString(R.string.days));
        		days30.setText("30 "+getResources().getString(R.string.days));
        	}
        });
        
        if (aScores[0]>minPointsPro) DISPLAYMAX = 50;
        
        rankTable = new Runnable(){
        	@Override
			public void run(){       		
        		if(ready){
	        		rankReset.setVisibility(View.GONE);
	        		topUsers.setVisibility(View.VISIBLE);
	        		loadBar.setVisibility(View.GONE);
	        		stats.setVisibility(View.GONE);
	        		share.setVisibility(View.VISIBLE);
	        		//score calculation	
	        		long myGameScore = (level*10000)+(average*100)+(aScores[0]);
	        		
	        		//check if server is online 
	        		if(rank!=0 && connected){
	        			info2.setText(getResources().getString(R.string.your_rank)+rank+".");
	        			listAdapter.notifyDataSetChanged();
	        			//submitScore(arry[12], UName, level, average, aScores[0], myGameScore);
	        			//Retrieve messages
	    	            newMsg = false;
	    	            String [] prevMsg = new String[2];
	    	            try {
	    	    			FileInputStream fi = openFileInput(FILEMSG);
	    	    			Scanner in = new Scanner(fi);
	    	    			prevMsg[0] = in.nextLine();
	    	    			prevMsg[1] = in.nextLine();
	    	    		} catch (FileNotFoundException e) {
	    	    			prevMsg[0]= "!@";
	    	    			prevMsg[1]= "!@";
	    	    			e.printStackTrace();
	    	    		}
	    	            if (msgs!=null && !prevMsg[0].equals(msgs)){		            
	    		            prevMsg[0] = msgs;
	    		            newMsg = true;
	    	            }
	    	            if(newMsg){
	    	            	try {
	    	        			OutputStreamWriter out = new OutputStreamWriter(openFileOutput(FILEMSG,0)); 
	    	        			out.write(prevMsg[0]+"\n"+prevMsg[1]);
	    	        			out.close();    
	    	            	} catch (IOException z) {
	    	            		z.printStackTrace(); 
	    	            	} catch (Exception E) {};
	    	            	displayMessage(msgs);
	    	            }            
	        			       			
	        		//if server is not online
	        		}else{
	        			topUsers.setVisibility(View.INVISIBLE);
						info2.setTextSize(18);
						info2.setText(R.string.unable_to_connect);
	        		}
	        	}
        		else {
	        		mHandler.postDelayed(rankTable, 100);
        		}
        	}
        };
        
        viewRank.setOnClickListener (new View.OnClickListener(){
        	@Override
			public void onClick (View v){
        		viewRank.setText(R.string.loading);
        		loadBar.setVisibility(View.VISIBLE);
        		mHandler.postDelayed(rankTable, 10);
        	}
        });       
        
        //resetting
        /*
        final Dialog dialog = new Dialog(this);
        reset.setOnClickListener (new View.OnClickListener(){
        	@Override
			public void onClick (View v){
				dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
				dialog.setContentView(R.layout.dialogbox);
				TextView title = (TextView) dialog.findViewById(R.id.textViewTitle);
				title.setVisibility(View.VISIBLE);
				title.setText(R.string.reset);
				TextView body = (TextView) dialog.findViewById(R.id.textViewMsg);
				body.setText(R.string.reset_msg);
				Button dialogButton = (Button) dialog.findViewById(R.id.button1);    		
				dialogButton.setVisibility(View.VISIBLE);   		    		
				dialogButton.setText(R.string.yes);
				dialogButton.setOnClickListener (new View.OnClickListener(){
		        	@Override
					public void onClick (View v) {
		        		try {
        					String c1 = "Type: 12  Sound: 1  Difficulty: 2 ";
        					String c2 = "Level: 1  Scores: 0 0 0 ";
        					String c3 = arry[12]+" "+arry[13]+" music: 1  vibrate: 1";
        					OutputStreamWriter out = new OutputStreamWriter(openFileOutput(FILENAME,0)); 
        					out.write(c1+c2+c3);
        					out.close(); 
        					Toast.makeText(getApplicationContext(), R.string.user_data_erased,Toast.LENGTH_SHORT).show();
        					finish();
        		    	} catch (IOException z) {
        		    		z.printStackTrace(); 
        		    	}
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
        });*/
		
        //update database class
        new UpdateDatabase().execute();    
	}
	
	class UpdateDatabase extends AsyncTask<String, String, String> {
    	String id = arry[12]; 
    	
    	// url to update product
        private  String url_update_user = "http://"+IPADRS+"/sqlphp/update_users.php";
        private  String url_get_rank = "http://"+IPADRS+"/sqlphp/get_rank.php";
        private  String url_get_users = "http://"+IPADRS+"/sqlphp/get_users.php";
        private  String url_get_version = "http://"+IPADRS+"/sqlphp/get_version.php";
     
    	// JSON Node names
        private static final String TAG_SUCCESS = "success";
        private static final String TAG_MESSAGE = "message";
        private static final String TAG_UID = "id";
        private static final String TAG_NAME = "name";
        private static final String TAG_LEVEL = "level";
        private static final String TAG_AVERAGE = "average";
        private static final String TAG_POINTS = "tpoints";
        private static final String TAG_GSCORE = "gscore";
        private static final String TAG_VERSION = "version";
        private static final String TAG_USERS = "users";
        private static final String TAG_MSG = "msg";
        private static final String TAG_LOCALE = "locale";
        /**
         * Updating user table
         * */
        @Override
		protected String doInBackground(String... args) {
        	try{
	            JSONParser jsonParser = new JSONParser();
	            
	          //score calculation
	            long myGameScore = (level*10000)+(average*100)+(aScores[0]);
	 
	            // Building Parameters
	            List<NameValuePair> params = new ArrayList<NameValuePair>();
	            params.add(new BasicNameValuePair(TAG_UID, id));
	            params.add(new BasicNameValuePair(TAG_NAME, UName));
	            params.add(new BasicNameValuePair(TAG_LEVEL, level+""));
	            params.add(new BasicNameValuePair(TAG_AVERAGE, average+""));
	            params.add(new BasicNameValuePair(TAG_POINTS, aScores[0]+""));
	            params.add(new BasicNameValuePair(TAG_GSCORE, myGameScore+""));
	            params.add(new BasicNameValuePair(TAG_VERSION, VERSION));
	            params.add(new BasicNameValuePair(TAG_LOCALE, locale));
	 
	            JSONObject json = jsonParser.makeHttpRequest(url_update_user,
	                    "POST", params);
	            
	            
	            /**
	             * Getting user rank
	             * */
	            List<NameValuePair> params2 = new ArrayList<NameValuePair>();
	            params2.add(new BasicNameValuePair(TAG_UID, id));
	            JSONObject json2 = jsonParser.makeHttpRequest(url_get_rank,
	                    "POST", params2);
	            try {
	                int success = json2.getInt(TAG_SUCCESS);
	                if (success == 1) {
	                    // successfully updated   
	                	connected = true;
	                	System.out.print("Database rank Successful");
	                	rank = Integer.parseInt(json2.getString(TAG_MESSAGE));
	                } else {
	                    // failed to update product
	                	System.out.print("Database rank NOT Successful");
	                	connected = false;
	                }
	            } catch (JSONException e) {
	                e.printStackTrace();
	            }
	            
	            
	            /**
	             * Getting user list
	             * */
	            JSONArray users = null;
	           // Building Parameters
	            List<NameValuePair> params3 = new ArrayList<NameValuePair>();
	            // getting JSON string from URL
	            JSONObject json3 = jsonParser.makeHttpRequest(url_get_users, "GET", params3);
	 
	            // Check your log cat for JSON reponse
	            Log.d("All users: ", json3.toString());
	 
	            try {
	                // Checking for SUCCESS TAG
	                int success = json3.getInt(TAG_SUCCESS);
	 
	                if (success == 1) {
	                    // users found
	                    // Getting Array of Users
	                    users = json3.getJSONArray(TAG_USERS);
	 
	                    // looping through All users
	                    for (int i = 0; i < users.length(); i++) {
	                        JSONObject c = users.getJSONObject(i);

	                        if(rank<=DISPLAYMAX){
	                        	if(i<DISPLAYMAX){
				            		String[] myInf = new String[5];
				            		myInf[0]=(c.getString(TAG_NAME)); 
				            		myInf[1]=(c.getString(TAG_LEVEL)); 
				            		myInf[2]=(c.getString(TAG_AVERAGE));
				            		myInf[3]=(c.getString(TAG_POINTS));
				            		myInf[4]= (i+1)+"";
				            		uList.add(myInf);
	                        	}
	                    	}else{
	                    		if(i<=rank+(DISPLAYMAX/2) && i>=rank-(DISPLAYMAX/2)){
	                    			String[] myInf = new String[5];
				            		myInf[0]=(c.getString(TAG_NAME)); 
				            		myInf[1]=(c.getString(TAG_LEVEL)); 
				            		myInf[2]=(c.getString(TAG_AVERAGE));
				            		myInf[3]=(c.getString(TAG_POINTS));
				            		myInf[4]= (i+1)+"";
				            		uList.add(myInf);
	                    		}
	                    	}
	                        if(i>rank+(DISPLAYMAX/2))break;
	                        if(i+1 == rank) msgs=(c.getString(TAG_MSG));
	                        connected = true;
	                    }
	                }else connected = false; 
	            } catch (JSONException e) {
	                e.printStackTrace();
	            } catch (Exception e) {
	                e.printStackTrace();
	            }
	            
	            /**
	             * Getting current version
	             * */
	            JSONObject json4 = jsonParser.makeHttpRequest(url_get_version, "GET", params3);
	            Log.d("Current Version: ", json4.toString());
	            try{
		            if (json4.getInt(TAG_SUCCESS)== 1){
		            	String currVersion = (json4.getString(TAG_MESSAGE)); 
		            	String ver = VERSION.replace("v","").replace("B","");
		            	if (ver.compareTo(currVersion) < 0) msgs = getString(R.string.version_outdated)+currVersion;
		            }
	            }catch(JSONException e) {
	                e.printStackTrace();
	            }
	            
        	} catch (NullPointerException e) {
                e.printStackTrace();
                connected= false;
            }
        	ready=true;
           return null;
        }
        
    }

	/*//submit Score
	private void submitScore(String id, String nam, int lvl, int avg, int pnts, long scr) {
        HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost("https://spreadsheets.google.com/spreadsheet/formResponse?hl=en_US&formkey=dGxUV0dpVGNDMHctektFbThiOGZoQlE6MQ");

        List<BasicNameValuePair> results = new ArrayList<BasicNameValuePair>();
        results.add(new BasicNameValuePair("entry.0.single", id));
        results.add(new BasicNameValuePair("entry.1.single", nam));
        results.add(new BasicNameValuePair("entry.2.single", lvl+""));
        results.add(new BasicNameValuePair("entry.3.single", avg+""));
        results.add(new BasicNameValuePair("entry.4.single", pnts+""));
        results.add(new BasicNameValuePair("entry.5.single", scr+""));
        results.add(new BasicNameValuePair("entry.6.single", VERSION));
        results.add(new BasicNameValuePair("entry.7.single", locale));

        try {
            post.setEntity(new UrlEncodedFormEntity(results));
        } catch (UnsupportedEncodingException e) {
            // Auto-generated catch block
            Log.e("YOUR_TAG", "An error has occurred", e);
        }
        try {
            client.execute(post);
        } catch (ClientProtocolException e) {
            // Auto-generated catch block
            Log.e("YOUR_TAG", "client protocol exception", e);
        } catch (IOException e) {
            // Auto-generated catch block
            Log.e("YOUR_TAG", "io exception", e);
        }
    }*/
	
	private void displayMessage(String msg){
		final Dialog dialog = new Dialog(this);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.dialogbox);
		TextView body = (TextView) dialog.findViewById(R.id.textViewMsg);
		TextView title = (TextView) dialog.findViewById(R.id.textViewTitle);
		title.setVisibility(View.VISIBLE);
		title.setText(R.string.new_message);
		body.setText(msg);
		dialog.setCancelable(false);
		Button dialogButton = (Button) dialog.findViewById(R.id.button1);
		dialogButton.setVisibility(View.VISIBLE);
		dialogButton.setText(R.string.close);
		dialogButton.setOnClickListener (new View.OnClickListener(){
        	@Override
			public void onClick (View v) {
				dialog.dismiss();
			}
		});
		dialog.show();
	}
}
