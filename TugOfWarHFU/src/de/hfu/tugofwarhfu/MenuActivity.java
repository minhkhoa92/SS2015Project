package de.hfu.tugofwarhfu;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class MenuActivity extends Activity {

	MediaPlayer  music;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		music = MediaPlayer.create(this, R.raw.a_short_story);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_menue);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menue, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void restartMus(){
        if (!music.isPlaying()){
            try {
                music.release();
                music = MediaPlayer.create(this, R.raw.a_short_story);
                int maxVolume = 1000000;
                double currentVolume = 900000;
                float floatVolume = (float) (Math.log(currentVolume + 1)/Math.log(maxVolume + 1));
                music.setLooping(true);
                music.start();
                music.setVolume(floatVolume, floatVolume);
            } catch (IllegalStateException e) {
                Log.Debug("Music", e.getMessage());
            }
        }
    }
	
	@Override
	protected void onResume() {
		if (music != null) {
			restartMus();
		}
		else if (music == null) {
			try {
				music = MediaPlayer.create(this, R.raw.a_short_story);
				int maxVolume = 1000000;
				double currentVolume = 900000;
				float floatVolume = (float) (Math.log(currentVolume + 1)/Math.log(maxVolume + 1));
				music.setLooping(true);
				music.start();
				music.setVolume(floatVolume, floatVolume);
			} catch (IllegalStateException e) {
                Log.Debug("Music", e.getMessage());
			}
		}
		super.onResume();
	}

	public void gotoSpiel(View view) {
		if (music!=null && music.isPlaying()) {
			try {
				music.stop();
				music.release();
				music = null;
			} catch (IllegalStateException e) {
                Log.Debug("Music", e.getMessage());
			}
		}
		Intent intent = new Intent (this, GameActivity.class);
		startActivity(intent);
	}

	public void gotoAnleitung(View view) {
		Intent intent = new Intent (this, AnleitungActivity.class);
		startActivity(intent);
		
	}

	public void gotoCredits(View view) {
		Intent intent = new Intent (this, CreditsActivity.class);
		startActivity(intent);
	}

}
