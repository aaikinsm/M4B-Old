package com.blackstar.math4brain;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NotificationReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
		NotificationManager nm = (NotificationManager) context
	            .getSystemService(Context.NOTIFICATION_SERVICE);
	    CharSequence from = "Math For The Brain";
	    CharSequence message = context.getString(R.string.notification_msg);
	    Intent notificationIntent = new Intent(context, MainMenu.class);
	    notificationIntent.getExtras();
	    PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
	            notificationIntent, 0);
	    Notification notif = new Notification(R.drawable.math4thebrain_icon, 
	    		context.getString(R.string.notification_msg), System.currentTimeMillis());
	    notif.setLatestEventInfo(context, from, message, contentIntent);
	    notif.defaults = Notification.DEFAULT_SOUND
	            | Notification.DEFAULT_LIGHTS;
	    nm.notify(1, notif);	
	}

}
