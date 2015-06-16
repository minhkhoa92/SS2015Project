package de.hfu.tugofwarhfu;


import java.util.Timer;
import java.util.TimerTask;
import java.util.Random;
import java.lang.Thread;

import de.hfu.tugofwarhfu.R;
import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AbsoluteLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;


@SuppressWarnings("deprecation")
public class GameActivity extends Activity {

	// Konstanten Minh
	private static final int DELAYTOSPEED = 125;
	public static final int ARTKRIEGER = 2;
	public static final int ARTSOLDAT = 1;
	public static final int ABSTANDZWEISOLDATEN = 80;
	public static final int GRENZEFEINDLICHEBASIS = 900;
	public static final int GRENZEMEINEBASE = 90;
	public static final int ABSTANDZWEIEINHEITEN = 200;
	private static final int STDPOSWIDTH = 100;
	private static final int STDPOSHEIGHT = 150;
	private static final int STDPOSY = 450;
	private static final int ticksPerSecond = 1000 / DELAYTOSPEED;
	
	// Variablen Minh
	EinheitFiFoStack myUnits;// Einheiten
	EinheitFiFoStack enemyUnits;
	boolean soldatbuttonactive = true;
	int baseOwn = 1000;
	int baseEnemy = 1000;
	private int stringpos = 0;
	AbsoluteLayout rl;
	protected Random rand;
	Handler platzierenDerEinheiten;
	Handler handlerMessage = new Handler();
	Timer updateTimer;
	MediaPlayer music;
	MediaPlayer sound_schwert1;
	MediaPlayer sound_schwert2;
	MediaPlayer sound_soldat_erstellt;
	int tick;
	
	
	
	//Variablen von ALex
	

	TextView sekunden_anzeige;
	TextView goldstand;
	int aktuellesguthaben=100;
	boolean running=true;
	Handler gold_handler;


//Variable von Alex Ende
	
	TextView leben_rot;
	TextView leben_blau;
	        
	@Override
	public void onCreate(Bundle savedInstanceState) { // passiert wenn die Activity erstellt wird
		music = MediaPlayer.create(this, R.raw.stacy_s_trumpet);
		music.setLooping(true);
		int maxVolume = 1000000;
		double currentVolume = 0.5; // 1.5 stacy's trumpet
		float floatVolume = (float) (Math.log(currentVolume + 1)/Math.log(maxVolume + 1));
		if (music!=null && !music.isPlaying()) {
			try {
				music.start();
				music.setVolume(floatVolume, floatVolume);
			} catch (IllegalStateException e) {
				e.printStackTrace();
			}
		}
		super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_game);
	    rl = (AbsoluteLayout) findViewById(R.id.AbsoluteLayoutGame);
	    leben_rot =(TextView) findViewById(R.id.LebenRot);
	    leben_blau =(TextView) findViewById(R.id.LebenBlau);
	    leben_rot.setText(Integer.toString(baseOwn));
	    leben_blau.setText(Integer.toString(baseEnemy));
	    tick = 0;
		   
			    //Goldschleife von Alex
			    goldstand=(TextView) findViewById(R.id.gold_anzeige);
			    gold_handler = new Handler();
				
				
					TimerTask timetask = new TimerTask() {  //eigentliche Schleife, die Gold dazuaddiert
					
					@Override
					public void run() {
						Runnable runnable=new Runnable(){
							@Override
							public void run(){
								gold_handler.post(new Runnable(){
									@Override
									public void run(){
										aktuellesguthaben=aktuellesguthaben+1;
										goldstand.setText(String.valueOf("Gold "+aktuellesguthaben));
									}
								});
							}
						};
						new Thread(runnable).start();
					}
				};
				Timer time = new Timer();
				time.schedule(timetask, 1000, 1000); 
				
				//Goldschleife von Alex Ende
				
				
				//Anfang von Stringaufruf fuer Einheitenreihenfolge (Bot)
				platzierenDerEinheiten=new Handler();
				TimerTask timertask = new TimerTask() {
					@Override public void run() {
						// bot, autoerstellen
						// bei jedem 'a' wird ein feindlicher Soldat erstellt
						autoErstellenNachInt(getString(R.string.einheiten_erstellen).charAt(stringpos)); stringpos++; 
						} };
				new Timer().schedule(timertask, 5000, 2000);
				
				TimerTask timernUeberpruefen = new TimerTask() {
					@Override public void run() {
				if (soldatbuttonactive) { //aktiviert den Button nach dem Cooldown wieder, Aufruf aus einem wiederholten Timertask
					ImageButton thisimagebutton = (ImageButton) findViewById(R.id.imageButtonSoldat);
					thisimagebutton.setClickable(true); //aktivieren von Soldatenbutton
				}
				//Ueberpruefen von welchem Team Einheiten stehen
				// nachfrage = 0 keine Einheit
				// nachfrage = 1 nur eine eigene Einheit
				// nachfrage = 2 Einheit von beiden da
				// nachfrage = 3 nur eine feindliche Einheit
				tick++;
				if ((int)nachfrage() > 0 && baseEnemy > 0 && baseOwn > 0) aktualisiereSpiel();
				
				} };
				updateTimer = new Timer();
				updateTimer.schedule(timernUeberpruefen, 200, DELAYTOSPEED);
	    }
	
	protected void onStart(){ //passiert wenn die Activity gestartet wird
		super.onStart();
		myUnits = new EinheitFiFoStack(false);
		enemyUnits = new EinheitFiFoStack(true);
		initSounds();
	}
	
	private void initSounds() {
		sound_schwert1 = MediaPlayer.create(this, R.raw.sword_one);
		sound_schwert2 = MediaPlayer.create(this, R.raw.steelsword);
		sound_soldat_erstellt = MediaPlayer.create(this, R.raw.unit_trips);
		sound_schwert1.setLooping(false);
		sound_schwert2.setLooping(false);
		sound_soldat_erstellt.setLooping(false);
		sound_schwert1.setVolume(0.15f, 0.15f);
		sound_schwert2.setVolume(0.10f, 0.10f);
		sound_soldat_erstellt.setVolume(0.07f, 0.07f);
	}
	
	@Override
	protected void onPause() {
		music.stop();
		music.release();
		super.onPause();
		updateTimer.cancel();
		rl.removeAllViews();
		finish();
		//finish() sorgt fuer keine Ueberbleibsel wenn die GameActivity von anderen Activities ueberlagert wird
	}
	
	// erstellung eines neuen ImageViews für jeden Knopfdruck
	private void erstelleSoldat(boolean isEnemy) {
		if (!isEnemy){
			platzierenDerEinheiten.post(new Runnable() { @Override public void run() { 
				ImageView neuerSoldat = new ImageView(returnContext());	
				neuerSoldat.setImageResource(R.drawable.anim_stickman_walking);
				int id = View.generateViewId();
				neuerSoldat.setId(id);
				rl.addView(neuerSoldat, erstelleAbsoluteLayoutParamSoldat(Einheit.XSTARTMYUNIT));
				myUnits.images.addLast(id);
				gebeLaufAnim(id);
			}}) ;
			myUnits.addNewSoldat();
		} else if (isEnemy && enemyUnits.getAnzahl() < 5){
			if ( sound_soldat_erstellt.isPlaying() ) { 
				sound_soldat_erstellt.pause();
				sound_soldat_erstellt.seekTo(0);
			}
			if ( !sound_soldat_erstellt.isPlaying()) sound_soldat_erstellt.start();
			enemyUnits.addNewSoldat();
			platzierenDerEinheiten.post(new Runnable() { @Override public void run() {
				ImageView neuerSoldat = new ImageView(returnContext());	
				neuerSoldat.setImageResource(R.drawable.anim_stickman_walking_g);
				int id = View.generateViewId();
				neuerSoldat.setId(id);
				enemyUnits.images.addLast(id);
				rl.addView(neuerSoldat, erstelleAbsoluteLayoutParamSoldat(Einheit.XSTARTENEMY));
				gebeLaufAnim_g(id);
			} } ) ;
		}
		
		popUpMessages("Zahl" + Integer.toString(myUnits.getAnzahl()) + "Zahl" + Integer.toString(enemyUnits.getAnzahl()));
	}
	
	private void erstelleKrieger(boolean isenemy) {
		if (!isenemy){
			platzierenDerEinheiten.post(new Runnable() { @Override public void run() { 
				ImageView neuerKrieger = new ImageView(returnContext());	
				neuerKrieger.setImageResource(R.drawable.anim_stickwarrior);
				int id = View.generateViewId();
				neuerKrieger.setId(id);
				// erstellung eines neuen ImageViews für jeden Knopfdruck
				rl.addView(neuerKrieger, erstelleAbsoluteLayoutParamWarrior(Einheit.XSTARTMYUNIT));
				myUnits.images.addLast(id);
				gebeLaufAnim(id);
			}}) ;
			myUnits.addNewKrieger();
		} else if (isenemy && enemyUnits.getAnzahl() < 5){
			if ( sound_soldat_erstellt.isPlaying() ) { 
				sound_soldat_erstellt.pause();
				sound_soldat_erstellt.seekTo(0);
			}
			if ( !sound_soldat_erstellt.isPlaying()) sound_soldat_erstellt.start();
			enemyUnits.addNewKrieger();
			platzierenDerEinheiten.post(new Runnable() { @Override public void run() {
				ImageView neuerSoldat = new ImageView(returnContext());	
				neuerSoldat.setImageResource(R.drawable.anim_stickwarrior_g);
				int id = View.generateViewId();
				neuerSoldat.setId(id);
				enemyUnits.images.addLast(id);
				rl.addView(neuerSoldat, erstelleAbsoluteLayoutParamWarrior(Einheit.XSTARTENEMY));
				gebeLaufAnim_g(id);
			} } ) ;
		}
		popUpMessages("Zahl" + Integer.toString(myUnits.getAnzahl()) + "Zahl" + Integer.toString(enemyUnits.getAnzahl()));
	}

	private void gebeLaufAnim(int id) {
		Animation ta = AnimationUtils.loadAnimation(returnContext(), R.anim.horizontal_translate);
		ImageView iv_eigenSold = (ImageView) findViewById(id);
		iv_eigenSold.setAnimation(null);
		iv_eigenSold.setAnimation(ta); // das neue image view wird sichtbar gemacht und ihm wird die animation zugewiesen
		ta.start();
	}

	private void gebeLaufAnim_g(int id) {
		Animation ta = AnimationUtils.loadAnimation(returnContext(), R.anim.horizontal_translate_g);
		ImageView iv_feindSold = (ImageView) findViewById(id);
		iv_feindSold.setAnimation(null);
		iv_feindSold.setAnimation(ta);
		ta.start();
	}


        
	public void SpawnSoldat(View Buttonsoldat) //onClick Funktion, spawn Soldaten
	{
		ImageButton thisimagebutton = (ImageButton) findViewById(R.id.imageButtonSoldat);
		
		if((aktuellesguthaben>=20) && soldatbuttonactive && myUnits.getAnzahl() < 5)
		{	
			if ( sound_soldat_erstellt.isPlaying() ) { 
				sound_soldat_erstellt.pause();
				sound_soldat_erstellt.seekTo(0);
			}
			if ( !sound_soldat_erstellt.isPlaying()) sound_soldat_erstellt.start();
			thisimagebutton.setClickable(false);
			soldatbuttonactive = false;  //3 sec nicht wieder aktivierbar
			int cooldowntime = 3000 ;
			new Cooldown(cooldowntime, ARTSOLDAT);
			aktuellesguthaben=aktuellesguthaben-20;	//zaehler ist der Goldwert, der Soldat kostet gerade 20 Gold
			erstelleSoldat(false);
		} else if (myUnits.getAnzahl() == 5 ){ popUpMessages("Limit ist bei 5."); }
	}

	public void SpawnKrieger(View v) //onClick Funktion, spawnt Krieger -> Dieser Knopf lässt gerade den gegnerischen Stickman sterben und lässt unseren an der Stelle weiterlaufen. wo er zuletzt gekämpft hat
	{
	ImageButton thisimagebutton = (ImageButton) findViewById(R.id.imageButtonKrieger);
		
		if((aktuellesguthaben>=50) && myUnits.getAnzahl() < 5)
		{	
			if ( sound_soldat_erstellt.isPlaying() ) { // anderer Sound?
				sound_soldat_erstellt.pause();
				sound_soldat_erstellt.seekTo(0);
			}
			if ( !sound_soldat_erstellt.isPlaying()) sound_soldat_erstellt.start();
			thisimagebutton.setClickable(false);
			int cooldowntime = 6000 ;
			new Cooldown(cooldowntime, ARTKRIEGER);
			aktuellesguthaben=aktuellesguthaben-50;	//zaehler ist der Goldwert, der Soldat kostet gerade 20 Gold
			erstelleKrieger(false);
		} else if (myUnits.getAnzahl() == 5 ){ popUpMessages("Limit ist bei 5."); }
	//	Log.d("kek","methode wird aufgerufen");
	}



	public void gamePause(View v) //onClick Funktion, soll das Spiel pausieren.
	{
		popUpMessages("Tut nichts.");
	}
	
	private void stillStehen(final boolean isEnemy, final int indexOfPic, final int xPos, final int einheitArt) {
		
		platzierenDerEinheiten.post(new Runnable() {
			@Override
			public void run() {
				ImageView iv = null;
				if (isEnemy){
					iv = (ImageView) findViewById(enemyUnits.images.get(indexOfPic));
					if ( einheitArt == ARTSOLDAT ) {
						iv.setImageResource(R.drawable.stickman3_g);
						iv.setLayoutParams(erstelleAbsoluteLayoutParamSoldat(xPos));
					} else if ( einheitArt == ARTKRIEGER) {
						iv.setImageResource(R.drawable.stickman2_1_g);
						iv.setLayoutParams(erstelleAbsoluteLayoutParamWarrior(xPos));
					}
					iv.setAnimation(null);
					iv.invalidate();
				}
				else if (!isEnemy) {
					iv = (ImageView) findViewById(myUnits.images.get(indexOfPic));
					if ( einheitArt == ARTSOLDAT ) {
						iv.setImageResource(R.drawable.stickman3);
						iv.setLayoutParams(erstelleAbsoluteLayoutParamSoldat(xPos));
					}
					else if ( einheitArt == ARTKRIEGER) {
						iv.setImageResource(R.drawable.stickman2_1);
						iv.setLayoutParams(erstelleAbsoluteLayoutParamWarrior(xPos));
					}
					iv.setAnimation(null);
					iv.invalidate();
				}
			}
			
			
		});
		
	}

	private void aktualisiereSpiel() { // hitboxerkennung und Kampferkennung timerstart
		if ( myUnits.getAnzahl() > 0 ) { 
			myUnits.teamEinSchrittVor();
		}
		if ( enemyUnits.getAnzahl() > 0 ) { 
			enemyUnits.teamEinSchrittVor();
		}
			
				
	//				 nachfrage = 1 nur eine eigene Einheit
	//				 nachfrage = 2 Einheit von beiden da
	//				 nachfrage = 3 nur eine feindliche Einheit
		if (nachfrage() == 2) { //wenn eine eigene Einheit und eine Gegner Einheit gespawnt sind
	//		Log.d("Hitboxen","Hitboxen werden aufgerufen");
			if((enemyUnits.firstX() - myUnits.firstX()) <= ABSTANDZWEISOLDATEN) // HITBOXEN! Einheit-X-Wert und Gegner-X-Wert
			{ 
				if (enemyUnits.getFirstData().amLaufen()) {
					enemyUnits.aendernZuKaempfen(enemyUnits.getDelPos());
						startKaempfen_g();
				}
				if ( myUnits.getFirstData().amLaufen()) {
					myUnits.aendernZuKaempfen(myUnits.getDelPos());
					startKaempfen();
				}
				if ( ( tick % ticksPerSecond ) < 1) {
					playFightTrack1();
					callDmgEinheit(enemyUnits.getFirstData());
					callDmgEinheit(myUnits.getFirstData());
					// aufraeumen von toten Soldaten (Einheiten)
					obEinheitTotIst();
					if ( myUnits.images.size() > 1 && myUnits.isTeamAmKaempfen() ) {
						stoppWennNoetig(false);
					}
					if ( enemyUnits.images.size() > 1 && enemyUnits.isTeamAmKaempfen() ) {
						stoppWennNoetig(true);
					}
				}
			}
		}
		
		if (nachfrage() == 1) { //wenn nur eine eigene Einheit gespawnt ist
			if(myUnits.firstX() >= GRENZEFEINDLICHEBASIS) {//HITBOXEN! Einehit-X-Wert und vorläufiger X wert der basis
				if (tick % ticksPerSecond < 1) {
					callDmgBase(false);
					playFightTrack2();
				}
				if ( myUnits.images.size() > 1) {
					if (baseEnemy > 0)
						stoppWennNoetig(false);
				}
				if(myUnits.getFirstData().amLaufen()) {
					myUnits.setWorkpos((int) 0);
					myUnits.aendernZuKaempfen(myUnits.getDelPos());
					startKaempfen();
				}
			}
		}
		
		if (nachfrage() == 3) {
			//wenn nur eine gegnerische  Einheit gespawnt ist
			if(enemyUnits.firstX() <= GRENZEMEINEBASE) {//HITBOXEN! Einehit-X-Wert und vorläufiger X wert der basis
				if (tick % ticksPerSecond < 1) {
					callDmgBase(true);
					playFightTrack2();
				}
				if ( enemyUnits.images.size() > 1) {
					if (baseOwn > 0)
						stoppWennNoetig(true);
				}
				if (enemyUnits.getFirstData().amLaufen()) {
					enemyUnits.setWorkpos((int) 0);
					enemyUnits.aendernZuKaempfen(enemyUnits.getDelPos());
					startKaempfen_g();
				}
			}
		}
	}

	private void startKaempfen() {
		
//		setzt den x-wert, wo die Animation abgespielt werden soll
			final int x = myUnits.firstX();
			// hier wird die x position des stickmans übergeben und dementsprechen findet die Kampfanimation an dieser Stelle statt!
			//in welcher Hoehe neu ein ImageView gespawnt wird
			platzierenDerEinheiten.post(new Runnable() { @Override public void run() { 
//				bringt die erste Einheit in den index
				ImageView iv = (ImageView) findViewById(myUnits.images.peekFirst());
				if (myUnits.getFirstData().getEinheitart() == ARTSOLDAT)
					iv.setLayoutParams(erstelleAbsoluteLayoutParamSoldat(x));
				if (myUnits.getFirstData().getEinheitart() == ARTKRIEGER)
					iv.setLayoutParams(erstelleAbsoluteLayoutParamWarrior(x));
//				gibt der Einheit eine neue Frame by Frame Animation
			
				
				AnimationDrawable ad = (AnimationDrawable) iv.getDrawable();
				if (ad.isRunning()) ad.stop();
			
				iv.getAnimation().cancel();
				iv.setAnimation(null);
				if (myUnits.getFirstData().getEinheitart() == ARTSOLDAT)
					iv.setImageResource(R.drawable.anim_stickman_kampf);
				if (myUnits.getFirstData().getEinheitart() == ARTKRIEGER)
					iv.setImageResource(R.drawable.anim_stickwarrior_kampf);
				ad = (AnimationDrawable) iv.getDrawable();
				if (!ad.isRunning()) ad.start();
			}}) ;
		}
	
	private void startKaempfen_g() {
		
		final int x = enemyUnits.firstX(); // hier wird die x position des stickmans übergeben und dementsprechen findet die Kampfanimation an dieser Stelle statt!

		
		platzierenDerEinheiten.post(new Runnable() { @Override public void run() { 
			ImageView iv = (ImageView) findViewById(enemyUnits.images.peekFirst());
			AnimationDrawable ad = (AnimationDrawable) iv.getDrawable();
			if (ad.isRunning()) ad.stop();
			
			iv.getAnimation().cancel();
			iv.setAnimation(null);
			if (enemyUnits.getFirstData().getEinheitart() == ARTSOLDAT) {
				iv.setImageResource(R.drawable.anim_stickman_kampf_g);
				iv.setLayoutParams(erstelleAbsoluteLayoutParamSoldat(x));
				}
			else if (enemyUnits.getFirstData().getEinheitart() == ARTKRIEGER) {
				iv.setImageResource(R.drawable.anim_stickman_kampf_g);
				iv.setLayoutParams(erstelleAbsoluteLayoutParamWarrior(x)); 
				}
			if ( !ad.isRunning() ) ad.start();
			
		}}) ;
	}
	
	private void stoppWennNoetig(boolean isEnemy) {
		int first_unit_x = enemyUnits.firstX(), delPos = enemyUnits.getDelPos();
		if ( !isEnemy ) {
			first_unit_x = myUnits.firstX(); delPos = myUnits.getDelPos();
			int i;
			for (i = 1 ; i < myUnits.images.size(); i++ ) {
				if ( first_unit_x - myUnits.getDataFromPos( (delPos + i) % 5 ).getXx() <= 
						ABSTANDZWEIEINHEITEN * i) {
					int j = (delPos + i) % 5;
					myUnits.aendernZuHalten(j);
					int einheitArt = myUnits.getDataFromPos( (delPos + i) % 5 ).getEinheitart();
					stillStehen(isEnemy, i, myUnits.getDataFromPos( (delPos + i) % 5 ).getXx(), einheitArt);
				}
			}
		} else if (isEnemy) { 
			int k;
			for (k = 1 ; k < enemyUnits.images.size(); k++ ) {
				if ( enemyUnits.getDataFromPos( (delPos + k) % 5 ).getXx() - first_unit_x <= 
						ABSTANDZWEIEINHEITEN * k ) {
					int l = (delPos + k) % 5;
					enemyUnits.aendernZuHalten(l);
					int einheitArt = enemyUnits.getDataFromPos( l).getEinheitart();
					stillStehen(isEnemy, k, enemyUnits.getDataFromPos(l).getXx(), einheitArt);
				}
			}
		}
	}

	private void callDmgBase(boolean isEnemy) {
		rand = new Random();
		if (isEnemy) { 
			baseOwn -= ( enemyUnits.getFirstData().getSchaden() + rand.nextInt(5) );
		    platzierenDerEinheiten.post(new Runnable() {
				@Override
				public void run() {
					leben_rot.setText(Integer.toString(baseOwn));
				}
			});
		} else { 
			baseEnemy -= ( myUnits.getFirstData().getSchaden() + rand.nextInt(5) ); 
			platzierenDerEinheiten.post(new Runnable() {
				@Override
				public void run() {
					leben_blau.setText(Integer.toString(baseEnemy));
				}
			});
		}
		schauNachObBasisTotIst();
	}

	private void schauNachObBasisTotIst() {
		if (baseEnemy < 1) {
			spielGewinnen();
		}
		if (baseOwn < 1) {
			spielVerlieren();
		}
	}

	public void spielVerlieren(){ //muss nachher in eine If bedingung in einen Timer rein, die z.B. alles halbe sekunde checkt, ob eine Base zerstört ist
		finish();
		Intent intent = new Intent (this, LoseActivity.class);
		startActivity(intent);
	}
	
	public void spielGewinnen(){ //muss nachher in eine If bedingung in einen Timer rein, die z.B. alles halbe sekunde checkt, ob eine Base zerstört ist
		baseEnemy = -100;
		finish();
		Intent intent = new Intent (this, WinActivity.class);
		startActivity(intent);
	}
	
	private void callDmgEinheit(Einheit angreifer) {
		rand = new Random();
		if (angreifer.isEnemy()) 
		myUnits.firstBekommtSchaden(angreifer.getSchaden() + rand.nextInt(5));
		else if (!angreifer.isEnemy()) enemyUnits.firstBekommtSchaden(angreifer.getSchaden() + rand.nextInt(5));	
	}

//Diese Funktion ueberprueft, ob eine Einheit keine hp hat. Bei keinen hp kann sie die bisher
//	bestehende Funktion gegner toeten aufrufen.
	private void obEinheitTotIst() {
		
		if ( enemyUnits.firstSchauObTot() && enemyUnits.getAnzahl() > 0 ){
			toeteEinheit();
		}
		
		if ( myUnits.firstSchauObTot() && myUnits.getAnzahl() > 0 ) {
		//einheit toeten, innerhalb der Methode wird weiterlaufen aufgerufen
			toeteEinheit();
		}
	}

	private void toeteEinheit() { 
		int id = 0;
		if (enemyUnits.images.size() > 0 && enemyUnits.firstSchauObTot()) {
			if ( enemyUnits.getFirstData().getEinheitart() == ARTSOLDAT ) {
				aktuellesguthaben += 30;}
			else if ( enemyUnits.getFirstData().getEinheitart() == ARTKRIEGER ){
				aktuellesguthaben += 65;}
			enemyUnits.deleteFirst();
			id = enemyUnits.images.peekFirst();
			final int savedId = id;
			enemyUnits.images.removeFirst();
			platzierenDerEinheiten.post(new Runnable() { @Override public void run() { 
				//loeschen von eigener Einheit - nur Bild
				ImageView iv = null;
				iv = (ImageView) findViewById(savedId);
				iv.setVisibility(View.GONE);
				iv.invalidate();
				iv.setImageResource(0);
				rl.removeView(iv);
				enemyUnits.setTeamAmKaempfen(false);
				if ( myUnits.firstX() > GRENZEFEINDLICHEBASIS ) {
					myUnits.setTeamAmKaempfen(false);
				}
				if ( myUnits.images.size() > 0 && !myUnits.isTeamAmKaempfen() ) {
					laufenIfNoetig(false);
				}
				if ( enemyUnits.images.size() > 0 && !enemyUnits.isTeamAmKaempfen() ) {
					laufenIfNoetig(true);
				}
			}}) ;
		} 
		if (myUnits.images.size() > 0 && myUnits.firstSchauObTot() ) {
			myUnits.deleteFirst();
			id = myUnits.images.peekFirst();
			myUnits.images.removeFirst();
			final int savedId = id;
			platzierenDerEinheiten.post(new Runnable() { @Override public void run() { 
				//loeschen von eigener Einheit - nur Bild
				ImageView iv = null;
				iv = (ImageView) findViewById(savedId);
				iv.setVisibility(View.GONE);
				iv.invalidate();
				iv.setImageResource(0);
				rl.removeView(iv);
				myUnits.setTeamAmKaempfen(false);
				if (enemyUnits.images.size() < 1) {
					enemyUnits.setTeamAmKaempfen(false);
				}
				if (enemyUnits.firstX() > GRENZEMEINEBASE) {
					enemyUnits.setTeamAmKaempfen(false);
				}
				if ( myUnits.images.size() > 0 && !myUnits.isTeamAmKaempfen() ) {
					laufenIfNoetig(false);
				}
				if ( enemyUnits.images.size() > 0 && !enemyUnits.isTeamAmKaempfen() ) {
					laufenIfNoetig(true);
				}
			}}) ;
		}
		//loeschen der Soldaten aus der Warteschlange / FIFO Stack
		popUpMessages("Zahl" + Integer.toString(myUnits.getAnzahl()) + "Zahl" + Integer.toString(enemyUnits.getAnzahl()));
	}
	
	private void laufenIfNoetig(boolean isEnemy) {
		if (isEnemy) {
			int i, einheitDataPos, size = enemyUnits.images.size();
			for ( i = 0 ; i < size ; i++ ) {
				einheitDataPos = ( enemyUnits.getDelPos() + i ) % 5 ;
				enemyUnits.aendernZuLaufen(einheitDataPos);
				weiterlaufen(isEnemy, i, einheitDataPos);
			}
		} else if (!isEnemy) {
			int i, einheitDataPos, size = myUnits.images.size();
			for ( i = 0 ; i < size ; i++ ) {
				einheitDataPos = ( myUnits.getDelPos() + i ) % 5 ;
				myUnits.aendernZuLaufen(einheitDataPos);
				weiterlaufen(isEnemy, i, einheitDataPos);
			}
		}
	}

	private void weiterlaufen(boolean isEnemy, final int picIndex, final int einheitDataPos){
		//Position des feindlichen Soldaten
		if (isEnemy) {
			final int x = enemyUnits.getDataFromPos(einheitDataPos).getXx();
			platzierenDerEinheiten.post(new Runnable() { @Override public void run() { 
				int pictureId = enemyUnits.images.get(picIndex);
				//zurueckposten des Bildes vom feindlichen Soldaten
				ImageView iv_Enemy = (ImageView) findViewById(pictureId);
				if (enemyUnits.getDataFromPos(einheitDataPos).getEinheitart() == ARTSOLDAT) {
					iv_Enemy.setImageResource(R.drawable.anim_stickman_walking_g);
					iv_Enemy.setLayoutParams(erstelleAbsoluteLayoutParamSoldat(x));
				}
				else if (enemyUnits.getDataFromPos(einheitDataPos).getEinheitart() == ARTKRIEGER) {
					iv_Enemy.setImageResource(R.drawable.anim_stickwarrior_g);
					iv_Enemy.setLayoutParams(erstelleAbsoluteLayoutParamWarrior(x));
				}
				iv_Enemy.invalidate();
				gebeLaufAnim_g(pictureId); 
			} } );
		}
		
		if (!isEnemy) {
			final int x = myUnits.getDataFromPos(einheitDataPos).getXx();
			
			platzierenDerEinheiten.post(new Runnable() { @Override public void run() { 
				int pictureId = myUnits.images.get(picIndex);
				//zurueckposten des Bildes vom feindlichen Soldaten
				ImageView iv_myUnit = (ImageView) findViewById(pictureId);
				if (myUnits.getDataFromPos(einheitDataPos).getEinheitart() == ARTSOLDAT)
					iv_myUnit.setImageResource(R.drawable.anim_stickman_walking);
				else if (myUnits.getDataFromPos(einheitDataPos).getEinheitart() == ARTKRIEGER)
					iv_myUnit.setImageResource(R.drawable.anim_stickwarrior);
	
				if (myUnits.getDataFromPos(einheitDataPos).getEinheitart() == ARTSOLDAT)
					iv_myUnit.setLayoutParams(erstelleAbsoluteLayoutParamSoldat(x)); 
				else if (myUnits.getDataFromPos(einheitDataPos).getEinheitart() == ARTKRIEGER)
					iv_myUnit.setLayoutParams(erstelleAbsoluteLayoutParamWarrior(x)); 
				gebeLaufAnim(pictureId); } } );
		}
				//bewegen des Bildes
		// enden der Kampfanimation des Gegners
	}


	private void autoErstellenNachInt ( int c) { // selbstaendiges erstellen von Einheiten (Bot)
		// bei jedem 'a' wird ein feindlicher Soldat erstellt
		switch (c) {
		case 'a':
			erstelleSoldat(true);
			popUpMessages("Zahl" + Integer.toString(myUnits.getAnzahl()) + "Zahl" + Integer.toString(enemyUnits.getAnzahl()));
			break;
		case 'b':
			erstelleKrieger(true);
			popUpMessages("Zahl" + Integer.toString(myUnits.getAnzahl()) + "Zahl" + Integer.toString(enemyUnits.getAnzahl()));
			break;
		case 's':
			spielGewinnen();
			break;
		case 'u':
			popUpMessages("Wow! Na gut, dann siege.");
			break;
		case 'v':
			popUpMessages("gewinnst du!");
			break;
		case 'w':
			popUpMessages("ueberlebst,");
			break;
		case 'x':
			popUpMessages("Okay, wenn du das");
			break;
//		case 'y':
//			popUpMessages("Protip: Press buttons.");;
//			break;
//		case 'z':
//			popUpMessages("Start! Have fun!");
//			break;
		default:
			break;
		}
	}
	
//Ueberpruefen von welchem Team Einheiten stehen
// nachfrage = 0 keine Einheit
// nachfrage = 1 nur eine eigene Einheit
// nachfrage = 2 Einheit von beiden da
// nachfrage = 3 nur eine feindliche Einheit
	private int nachfrage() {
		int returnint = 0;
		// beschriebene Werte werden zu != null
		//	beschrieben sind Faelle au\sser der default wenn keine Einheit gespawnt ist
		if (myUnits.getAnzahl() > 0 || enemyUnits.getAnzahl() > 0) {
			if ( myUnits.getAnzahl() > 0 && enemyUnits.getAnzahl() > 0 ) { 
				returnint = 2;
			}
			if ( myUnits.getAnzahl() > 0 && enemyUnits.getAnzahl() < 1) {
				returnint = 1;
			}
			if ( myUnits.getAnzahl() < 1 && enemyUnits.getAnzahl() > 0 ) {
				returnint = 3;
			} 
		} else returnint = 0;
		return returnint;
	}

	private void popUpMessages (final String message) {
		TextView txt = (TextView) findViewById(R.id.messages);
		if (!txt.isShown()){
			handlerMessage.post(new Runnable() {
				@Override
				public void run() {
					TextView txt = (TextView) findViewById(R.id.messages);
					txt.setText(message);
					txt.setVisibility(View.VISIBLE);
					txt.setAnimation(null);
				}
			});
		}
		handlerMessage.postDelayed((new Runnable() {
			@Override
			public void run() {
				final Animation anim = AnimationUtils.loadAnimation(returnContext(), R.anim.fade_two_sec);
				TextView txt = (TextView) findViewById(R.id.messages);
				if ( txt.getAnimation() == null ) {
					txt.setAnimation(anim);
					anim.start();
				}
				txt.setVisibility(View.INVISIBLE);
			}
		}) , 2000);
	}
	
	private void playFightTrack1(){
		if (sound_schwert1!= null && sound_schwert2!= null) {
			if (!sound_schwert1.isPlaying() && !sound_schwert2.isPlaying()) {
				try {
					sound_schwert2.start();
					new Handler().postDelayed(new Runnable() {
						
						@Override
						public void run() {
							playFightTrack2();
							
						}
					}, ( sound_schwert2.getDuration() + 10 ) );
				} catch (IllegalStateException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private void playFightTrack2(){
		if (sound_schwert1!=null ) {
			if (!sound_schwert1.isPlaying()) {
				try {
					sound_schwert1.start();
				} catch (IllegalStateException e) {
					e.printStackTrace();
				}
			}
		}
	}
        
    private class Cooldown {
    	Timer cooldownTimer;
    	int buttonTyp;
    	public Cooldown (int millisecondsToCooldown, int buttonArt) {
    		int end = millisecondsToCooldown;
    		buttonTyp = buttonArt;
    		TimerTask activateUnitButton = new TimerTask() {
    			
    			@Override
    			public void run() {
    				if (buttonTyp == ARTSOLDAT) {
    					soldatbuttonactive = true;
    					ImageView iv = (ImageView) findViewById(R.id.imageButtonSoldat);
    					iv.setClickable(true);
    				}
    				if (buttonTyp == ARTKRIEGER) {
    					ImageView iv = (ImageView) findViewById(R.id.imageButtonKrieger);
    					iv.setClickable(true);
    				}
    					
    			}
    		};
    		cooldownTimer = new Timer();
    		cooldownTimer.schedule(activateUnitButton, end);
    	}
    }
    
    private AbsoluteLayout.LayoutParams erstelleAbsoluteLayoutParamSoldat(int x){
    	AbsoluteLayout.LayoutParams lp = new AbsoluteLayout.LayoutParams(
    			STDPOSWIDTH, STDPOSHEIGHT, x, STDPOSY);
    	return lp;
    }
    
    private AbsoluteLayout.LayoutParams erstelleAbsoluteLayoutParamWarrior(int x){
    	AbsoluteLayout.LayoutParams lp = new AbsoluteLayout.LayoutParams(
    			STDPOSWIDTH+25, STDPOSHEIGHT+25, x, STDPOSY-25);
    	return lp;
    }
    
	private Context returnContext() {
		return this;
	}
    
}

	