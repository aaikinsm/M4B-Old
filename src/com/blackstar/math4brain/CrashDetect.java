package com.blackstar.math4brain;



import org.acra.ACRA;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

import android.app.Application;


@ReportsCrashes(formKey = "dHJMOTI2Y09WN2d4M0ExQUZyYnBxZ3c6MA", 
	mode = ReportingInteractionMode.TOAST,                 
	forceCloseDialogAfterToast = false, // optional, default false                 
	resToastText = R.string.crash_toast_text)  
public class CrashDetect extends Application {
	@Override
    public void onCreate() {
        // The following line triggers the initialization of ACRA
        ACRA.init(this);
        super.onCreate();//old key= dGVpbUw1aTMzN1laVExlZHpIdzI2YWc6MQ
    }
}

