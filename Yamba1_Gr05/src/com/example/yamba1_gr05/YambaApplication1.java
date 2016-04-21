package com.example.yamba1_gr05;

import java.util.List;

import winterwell.jtwitter.Twitter;
import android.app.Application;
import android.content.ContentValues;
//import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

public class YambaApplication1 extends Application implements OnSharedPreferenceChangeListener {
	
	public static final String LOCATION_PROVIDER_NONE = "NONE";
	
	 private static final String TAG = YambaApplication1.class.getSimpleName();

	public static final long INTERVAL_NEVER = 0;
	
	 public Twitter twitter; // 
	 private SharedPreferences prefs;
	 private boolean serviceRunning;
	 private StatusData statusData; 
	 
	 public String getProvider() {
		 return prefs.getString("provider", LOCATION_PROVIDER_NONE);
	 }

	 @Override
	  public void onCreate() 
	  {
	    super.onCreate();
	    this.prefs = PreferenceManager.getDefaultSharedPreferences(this);
	    this.prefs.registerOnSharedPreferenceChangeListener(this);
	    Log.i(TAG, "onCreated");
	    //startService(new Intent(this, UpdaterService.class));
	  }

	  @Override
	  public void onTerminate() 
	  { 
	    super.onTerminate();
	    Log.i(TAG, "onTerminated");
	  }	 

	  public synchronized Twitter getTwitter() 
	  { 
		    if (this.twitter == null) {
		      String username = this.prefs.getString("username", "student");
		      //String username = this.prefs.getString("username", "jfranco");
		      String password = this.prefs.getString("password", "password");
		      //String password = this.prefs.getString("password", "rtos12");
		      
		      String apiRoot = prefs.getString("apiRoot","http://yamba.marakana.com/api");
		      //String apiRoot = prefs.getString("apiRoot","http://identi.ca/api");
		      if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(apiRoot)) {
		        this.twitter = new Twitter(username, password);
		        this.twitter.setAPIRootUrl(apiRoot);
		      }
		    }
		    return this.twitter;
	  }	  

	  
    public boolean isServiceRunning()  
	{  
      return serviceRunning;
	}	 

    public void setServiceRunning(boolean serviceRunning)  
    { 
    	Log.i("Servicio", "ServiceRunning: "+serviceRunning);
       this.serviceRunning = serviceRunning;
    }	 

	  
	  
	  public synchronized void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) 
	  { 
	     this.twitter = null;
	  }	 
	  
      public StatusData getStatusData() // 2 
	  { 
		  if (statusData==null) {
		    statusData = new StatusData(this);
		  }
		  return statusData;	  
	  }

      public SharedPreferences getPrefs() 
      {
         return prefs;
      }      
      

   // Connects to the online service and puts the latest statuses into DB.
   // Returns the count of new statuses

   public synchronized int fetchStatusUpdates()   // 3  
   { 
      Log.d(TAG, "Fetching status updates");
      Twitter twitter = this.getTwitter();
      if (twitter == null) 
      {
         Log.d(TAG, "Twitter connection info not initialized");
         return 0;
      }
      try 
      {
         List<Twitter.Status> statusUpdates = twitter.getFriendsTimeline();
         long latestStatusCreatedAtTime = this.getStatusData().getLatestStatusCreatedAtTime();
         int count = 0;
         ContentValues values = new ContentValues();
         for (Twitter.Status status : statusUpdates) 
         {
            values.put(StatusData.C_ID, status.getId());
            long createdAt = status.getCreatedAt().getTime();
            values.put(StatusData.C_CREATED_AT, createdAt);
            values.put(StatusData.C_TEXT, status.getText());
            values.put(StatusData.C_USER, status.getUser().getName());
            Log.d(TAG, "Got update with id " + status.getId() + ". Saving");
            this.getStatusData().insertOrIgnore(values);
            if (latestStatusCreatedAtTime < createdAt) 
            {
               count++;
            }
         }
         Log.d(TAG, count > 0 ? "Got " + count + " status updates": "No new status updates");
         return count;
      } 
      catch (RuntimeException e) 
      {
         Log.e(TAG, "Failed to fetch status updates", e);
         return 0;
     }
   }

	public long getInterval() {
		return Long.parseLong(prefs.getString("interval", "0"));
	}
	
}
