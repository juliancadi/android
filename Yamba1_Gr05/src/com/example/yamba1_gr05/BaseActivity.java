package com.example.yamba1_gr05;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class BaseActivity extends Activity { // 1

   YambaApplication1 yamba; // 2

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      yamba = (YambaApplication1) getApplication(); // 3
   }

   // Called only once first time menu is clicked on
   @Override
   public boolean onCreateOptionsMenu(Menu menu) { // 4
      getMenuInflater().inflate(R.menu.menu, menu);
      return true;
   }

   //Called every time user clicks on a menu item
   @Override
   public boolean onOptionsItemSelected(MenuItem item) { // 5
      switch (item.getItemId()) {
	      
	      case R.id.itemPrefs:
	         startActivity(new Intent(this, PrefsActivity.class).addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
		  break;
	      
	      case R.id.itemPurge:
		      ((YambaApplication1) getApplication()).getStatusData().delete();
		      Toast.makeText(this, R.string.msgAllDataPurged, Toast.LENGTH_LONG).show();
		  break;
		    
	      case R.id.itemTimeline:
	         startActivity(new Intent(this, TimelineActivity.class).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP).addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
		  break;
		    
	      case R.id.itemStatus:
	         startActivity(new Intent(this, StatusActivity.class).addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
	      break;
	      
	      case R.id.itemRefresh:
	    	  startService(new Intent(this, UpdaterService.class)); //
	      break;
	  }
      return true;
   }

}
