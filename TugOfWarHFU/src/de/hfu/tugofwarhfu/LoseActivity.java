package de.hfu.tugofwarhfu;

import de.hfu.tugofwarhfu.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class LoseActivity extends Activity{
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_lose);
	    
	

//TextView tv = (TextView) findViewById(R.id.txt_anleitung);
//tv.setMovementMethod(new ScrollingMovementMethod());



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
