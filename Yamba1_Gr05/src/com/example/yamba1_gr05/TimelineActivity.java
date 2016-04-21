package com.example.yamba1_gr05;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.SimpleCursorAdapter.ViewBinder;
import android.widget.TextView;
import android.widget.Toast;

public class TimelineActivity extends BaseActivity // 1 
{
	
	static final String SEND_TIMELINE_NOTIFICATIONS = "com.example.yamba1_gr05.SEND_TIMELINE_NOTIFICATIONS";
	
	DbHelper1 dbHelper;
	Cursor cursor; 
	ListView listTimeline; 
	SimpleCursorAdapter adapter;
	static final String[] FROM = { DbHelper1.C_CREATED_AT, DbHelper1.C_USER,DbHelper1.C_TEXT };
	static final int[] TO = { R.id.textCreatedAt, R.id.textUser, R.id.textText };

	TimelineReceiver receiver;
	IntentFilter filter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.timeline);

		// Check if preferences have been set
		if (yamba.getPrefs().getString("username", null) == null) { // 2
			startActivity(new Intent(this, PrefsActivity.class));
			Toast.makeText(this, R.string.msgSetupPrefs, Toast.LENGTH_LONG).show();
		}     
		// Find your views
		listTimeline = (ListView) findViewById(R.id.listTimeline);        
		// Create the receiver
		receiver = new TimelineReceiver();
		filter = new IntentFilter( UpdaterService.NEW_STATUS_INTENT);
	}

	@Override
	protected void onResume() {
		super.onResume();
		// Setup List
		this.setupList(); // 3

		// Register the receiver
		super.registerReceiver(receiver, filter, SEND_TIMELINE_NOTIFICATIONS, null);
	}
	
	@Override
	protected void onPause() {
		super.onPause();

		// UNregister the receiver
		unregisterReceiver(receiver); 
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		// Close the database
		yamba.getStatusData().close(); // 4 
	}   


	// Responsible for fetching data and setting up the list and the adapter
	@SuppressWarnings("deprecation")
	private void setupList() { // 5

		// Get the data from the database
		YambaApplication1 yamba = (YambaApplication1) super.getApplication();
		this.cursor = yamba.getStatusData().getStatusUpdates();
		startManagingCursor(this.cursor);

		// Setup the adapter
		adapter = new SimpleCursorAdapter(this, R.layout.row, cursor, FROM, TO);
		adapter.setViewBinder(VIEW_BINDER); 
		listTimeline.setAdapter(adapter); 
	}


	// View binder constant to inject business logic that converts a timestamp to
	// relative time
	static final ViewBinder VIEW_BINDER = new ViewBinder() { // 7 

		public boolean setViewValue(View view, Cursor cursor, int columnIndex) { // 
			if (view.getId() != R.id.textCreatedAt)
				return false; // 
			// Update the created at text to relative time
			long timestamp = cursor.getLong(columnIndex); // 
			CharSequence relTime = DateUtils.getRelativeTimeSpanString(view.getContext(), timestamp); // 
			((TextView) view).setText(relTime); // 
			return true; // 
		}
	};	 
	
	// Receiver to wake up when UpdaterService gets a new status
	// It refreshes the timeline list by requerying the cursor
	class TimelineReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			setupList();
			Log.d("TimelineReceiver", "onReceived");
		}
	}
	
}
