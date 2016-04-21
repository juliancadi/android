package com.example.yamba1_gr05;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver 
{

	@Override
	public void onReceive(Context context, Intent callingIntent) {
		// Check if we should do anything at boot at all
		long interval = ((YambaApplication1) context.getApplicationContext()).getInterval(); //
		if (interval == YambaApplication1.INTERVAL_NEVER) //
			return;
		// Create the pending intent
		Intent intent = new Intent(context, UpdaterService.class); //
		PendingIntent pendingIntent = PendingIntent.getService(context, -1, intent,
		PendingIntent.FLAG_UPDATE_CURRENT); //
		// Setup alarm service to wake up and start service periodically
		AlarmManager alarmManager = (AlarmManager) context
		.getSystemService(Context.ALARM_SERVICE); //
		alarmManager.setInexactRepeating(AlarmManager.RTC, System
		.currentTimeMillis(), interval, pendingIntent); //
		Log.d("BootReceiver", "onReceived");
	}
	
}
