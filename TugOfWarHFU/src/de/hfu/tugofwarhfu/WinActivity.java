package de.hfu.tugofwarhfu;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class WinActivity extends Activity{
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_win);
	}
	
	public void gotoMenu(View v){
		Intent intent = new Intent (this,MenuActivity.class);
		startActivity(intent);
		finish();
	};
	
	public void gotoGame(View v){
		Intent intent = new Intent (this, GameActivity.class);
		startActivity(intent);
		finish();
	}

}
