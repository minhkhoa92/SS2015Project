package com.example.tugofwarhfu;


import java.util.Timer;
import java.util.TimerTask;
import java.util.Random;
import java.lang.Thread;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
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
	private static final int DELAYTOSPEED = 500;
	private final int ARTSOLDAT = 1;
	private final int ABSTANDZWEISOLDATEN = 130;
	public static final int GRENZEFEINDLICHEBASIS = 820;
	public static final int GRENZEMEINEBASE = 60;
	public static final int ABSTANDZWEIEINHEITEN = 275;
	final int STDPOSWIDTH = 100;
	final int STDPOSHEIGHT = 150;
	final int STDPOSY = 450;
	
	// Variablen Minh
	EinheitFiFoStack myUnits;// Einheiten
	EinheitFiFoStack enemyUnits;
	boolean soldatbuttonactive = true;
	int baseown;
	int baseene;
	private int stringpos = 0;
	AbsoluteLayout rl;
	protected Random rand;
	Handler platzierenDerEinheiten;
	Timer updateTimer;
	
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
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_game);
	    rl = (AbsoluteLayout) findViewById(R.id.AbsoluteLayoutGame);
	    leben_rot =(TextView) findViewById(R.id.LebenRot);
	    leben_blau =(TextView) findViewById(R.id.LebenBlau);
		baseown = 1000;
		baseene = 1000;
	    leben_rot.setText(Integer.toString(baseown));
	    leben_blau.setText(Integer.toString(baseene));
		   
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
						autoErstellenNachint(getString(R.string.einheiten_erstellen).charAt(stringpos)); stringpos++; 
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
				if ((int)nachfrage() > 0) aktualisiereSpiel();
				
				} };
				updateTimer = new Timer();
				updateTimer.schedule(timernUeberpruefen, 4000, DELAYTOSPEED);
	    }
	
	protected void onStart(){ //passiert wenn die Activity gestartet wird
		super.onStart();
//		Log.d("Start","Act.Start");
		myUnits = new EinheitFiFoStack(false);
		enemyUnits = new EinheitFiFoStack(true);
	}
	
	
	
	@Override
	protected void onPause() {
		super.onPause();
		rl.removeAllViews();
		updateTimer.cancel();
		finish(); // keine Ahnung ob das den hoeheren Anforderungen entspricht 
		//finish() sorgt fuer keine Ueberbleibsel wenn die GameActivity von anderen Activities ueberlagert wird
	}
	

	private void erstelleSoldat(boolean isenemy) {
		final AbsoluteLayout.LayoutParams lp = new AbsoluteLayout.LayoutParams
				(STDPOSWIDTH, STDPOSHEIGHT, Einheit.XSTARTMYUNIT, STDPOSY);
		if (!isenemy){
			platzierenDerEinheiten.post(new Runnable() { @Override public void run() { 
				ImageView neuerSoldat = new ImageView(returnContext());	
				neuerSoldat.setImageResource(R.drawable.anim_stickman_walking);
				int id = View.generateViewId();
				neuerSoldat.setId(id);
				// erstellung eines neuen ImageViews für jeden Knopfdruck
				rl.addView(neuerSoldat, lp);
				myUnits.images.addLast(id);
				gebelaufanim(id);
			}}) ;
			myUnits.addnewsoldat();
		} else if (isenemy && enemyUnits.getAnzahl() < 5){
			enemyUnits.addnewsoldat();
			platzierenDerEinheiten.post(new Runnable() { @Override public void run() {
				AbsoluteLayout.LayoutParams lpNew = new AbsoluteLayout.LayoutParams
						(STDPOSWIDTH, STDPOSHEIGHT, Einheit.XSTARTENEMY, STDPOSY);
				ImageView neuerSoldat = new ImageView(returnContext());	
				neuerSoldat.setImageResource(R.drawable.anim_stickman_walking_g);
				int id = View.generateViewId();
				neuerSoldat.setId(id);
				enemyUnits.images.addLast(id);
				rl.addView(neuerSoldat, lpNew);
				gebelaufanim_g(id);
			} } ) ;
		}

	}

	
	private void gebelaufanim(int id) {
		Animation ta = AnimationUtils.loadAnimation(returnContext(), R.anim.horizontal_translate);
		ImageView iv_eigenSold = (ImageView) findViewById(id);
		iv_eigenSold.setAnimation(ta); // das neue image view wird sichtbar gemacht und ihm wird die animation zugewiesen
		iv_eigenSold.getAnimation().start();
	}

	private void gebelaufanim_g(int id) {
		Animation ta = AnimationUtils.loadAnimation(returnContext(), R.anim.horizontal_translate_g);
		ImageView feindSold = (ImageView) findViewById(id);
		feindSold.setAnimation(ta);
		feindSold.getAnimation().start();
	}
	 
	private void startKaempfen() {
		
//	setzt den x-wert, wo die Animation abgespielt werden soll
		int x = 0;
		x = myUnits.firstx();
		// hier wird die x position des stickmans übergeben und dementsprechen findet die Kampfanimation an dieser Stelle statt!
		final AbsoluteLayout.LayoutParams lp2 = new AbsoluteLayout.LayoutParams(
				STDPOSWIDTH, 
				STDPOSHEIGHT,
				x,
				STDPOSY); //in welcher Hoehe neu ein ImageView gespawnt wird
		platzierenDerEinheiten.post(new Runnable() { @Override public void run() { 
//			bringt die erste Einheit in den index
			ImageView iv = (ImageView) findViewById(myUnits.images.peekFirst());
			iv.setLayoutParams(lp2);
//			gibt der Einheit eine neue Frame by Frame Animation
		
			
		AnimationDrawable ad = (AnimationDrawable) iv.getDrawable();
		if (ad.isRunning()) ad.stop();
		
		iv.getAnimation().cancel();
		iv.setAnimation(null);
		iv.setImageResource(R.drawable.anim_stickman_kampf);
		ad = (AnimationDrawable) iv.getDrawable();
		if (!ad.isRunning()) ad.start();
		}}) ;
	}
		

	
	private void startKaempfen_g() {
//		Log.d("Kampf","Es wird gekämpft");
		
		int x = enemyUnits.firstx(); // hier wird die x position des stickmans übergeben und dementsprechen findet die Kampfanimation an dieser Stelle statt!

		final AbsoluteLayout.LayoutParams lp2 = new AbsoluteLayout.LayoutParams(
		STDPOSWIDTH, 
		STDPOSHEIGHT,
		x,
		STDPOSY);
		platzierenDerEinheiten.post(new Runnable() { @Override public void run() { 
			ImageView iv = (ImageView) findViewById(enemyUnits.images.peekFirst());
			AnimationDrawable ad = (AnimationDrawable) iv.getDrawable();
			if (ad.isRunning()) ad.stop();
			
			iv.getAnimation().cancel();
			iv.setAnimation(null);
			iv.setImageResource(R.drawable.anim_stickman_kampf_g);
			if ( !ad.isRunning() ) ad.start();
			iv.setLayoutParams(lp2);
		}}) ;
	}
	
	private Context returnContext() {
		return this;
	}
	
        
public void SpawnSoldat(View Buttonsoldat) //onClick Funktion, spawn Soldaten
{
	Log.d("Soldat", "Soldat wird gespawnt!");
	ImageButton thisimagebutton = (ImageButton) findViewById(R.id.imageButtonSoldat);
	
	if((aktuellesguthaben>=20) && soldatbuttonactive && myUnits.getAnzahl() < 5)
	{	
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
	
//	Log.d("kek","methode wird aufgerufen");
	popUpMessages("Tut nichts.");
//	stillStehen(true, enemyUnits.images.peekFirst(), enemyUnits.firstx());
	
}
public void gamePause(View v) //onClick Funktion, soll das Spiel pausieren.
{
	popUpMessages("Tut nichts.");
//	erstelleSoldat(true);
}

private void stillStehen(final boolean isEnemy, int id, final int xPos) {
	final ImageView iv = (ImageView) findViewById(id);
	platzierenDerEinheiten.post(new Runnable() {
		@Override
		public void run() {
			iv.getAnimation().cancel();
			iv.setAnimation(null);
			iv.setLayoutParams(new AbsoluteLayout.LayoutParams(STDPOSWIDTH, STDPOSHEIGHT, xPos, STDPOSY));
			if (!isEnemy)
				iv.setImageResource(R.drawable.stickman3);
			else if (isEnemy)
				iv.setImageResource(R.drawable.stickman3_g);
		}
	});
	
}

private void aktualisiereSpiel() { // hitboxerkennung und Kampferkennung timerstart

	if ( myUnits.getAnzahl() > 0 ) { 
		myUnits.teamEinSchrittVor();
		int i = 1;
		while (i < 5){
			if (!myUnits.getDataFromPos( ( myUnits.getDelPos() + i ) % 5 ).amLaufen() &&
					myUnits.getDataFromPos( ( myUnits.getDelPos() + i ) % 5 ).getHp() > 0)
				stillStehen(false, myUnits.images.get(i), myUnits.getDataFromPos( ( myUnits.getDelPos() + i ) % 5 ).getXx());
			i++;
		}
	}
	if ( enemyUnits.getAnzahl() > 0 ) { 
		enemyUnits.teamEinSchrittVor();
		int i = 1;
		while (i < 5){
			if (!enemyUnits.getDataFromPos( ( enemyUnits.getDelPos() + i ) % 5 ).amLaufen() &&
					enemyUnits.getDataFromPos( ( enemyUnits.getDelPos() + i ) % 5 ).getHp() > 0)
				stillStehen(true, enemyUnits.images.get(i), enemyUnits.getDataFromPos( ( enemyUnits.getDelPos() + i ) % 5 ).getXx());
			i++;
		}
	}
		
			
//				 nachfrage = 1 nur eine eigene Einheit
//				 nachfrage = 2 Einheit von beiden da
//				 nachfrage = 3 nur eine feindliche Einheit
	int c = nachfrage();
	switch (c) {
		case 2: { //wenn eine eigene Einheit und eine Gegner Einheit gespawnt sind
	//		Log.d("Hitboxen","Hitboxen werden aufgerufen");
			if(( myUnits.firstx() - enemyUnits.firstx() ) >= ABSTANDZWEISOLDATEN) // HITBOXEN! Einheit-X-Wert und Gegner-X-Wert
			{ 
				callDmgEinheit(enemyUnits.getFirstData()); // TODO test ob der Schaden nur in eine Richtung laeuft
				callDmgEinheit(myUnits.getFirstData());
				if ( myUnits.getFirstData().amLaufen()) {
					myUnits.setWorkpos((int) 0);
					myUnits.aendernZuKaempfenStart(myUnits.getDelPos()); // Laufen
					try {
						startKaempfen();
						}
					catch (Exception e) {
						e.printStackTrace();
					}
				}
	
				if (enemyUnits.getFirstData().amLaufen()) {
					try {
						enemyUnits.setWorkpos((int) 0);
						enemyUnits.aendernZuKaempfenStart(enemyUnits.getDelPos());
						startKaempfen_g();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			break;
		}
	
		case 1: { //wenn nur eine eigene Einheit gespawnt ist

			if(myUnits.firstx() >= GRENZEFEINDLICHEBASIS) {//HITBOXEN! Einehit-X-Wert und vorläufiger X wert der basis
				callDmgBase(false);
				if(myUnits.getFirstData().amLaufen()) {
//					Log.d("Kampf","Einheit läuft gegen die basis");
					try {
						myUnits.setWorkpos((int) 0);
						myUnits.aendernZuKaempfenStart(myUnits.getDelPos());
						startKaempfen();
					} catch (Exception e) {
						e.printStackTrace();	
					}
				}
			}
			break;
		}
	
		case 3: {
			
		//wenn nur eine gegnerische  Einheit gespawnt ist
//					final boolean k = feindeinheit.isamlaufen();
//					gold_handler.post(new Runnable() {@Override public void run() { goldstand.setText(Boolean.toString(k));} });
			if(enemyUnits.firstx() <= GRENZEMEINEBASE) {//HITBOXEN! Einehit-X-Wert und vorläufiger X wert der basis
				callDmgBase(true);
				if (enemyUnits.getFirstData().amLaufen()) {
//					Log.d("Kampf","GegnerEinheit läuft gegen die basis"); 
					try {
						enemyUnits.setWorkpos((int) 0);
						enemyUnits.aendernZuKaempfenStart(enemyUnits.getDelPos());
						startKaempfen_g();
					}
					catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			break;
		}
	}
}



private void callDmgBase(boolean isEnemy) {
	rand = new Random();
	if (isEnemy) { 
		baseown -= ( enemyUnits.getFirstData().getSchaden() + rand.nextInt(5) );
	    platzierenDerEinheiten.post(new Runnable() {
			@Override
			public void run() {
				leben_rot.setText(Integer.toString(baseown));
			}
		});
	} else { 
		baseene -= ( myUnits.getFirstData().getSchaden() + rand.nextInt(5) ); 
		platzierenDerEinheiten.post(new Runnable() {
			@Override
			public void run() {
				leben_blau.setText(Integer.toString(baseene));
			}
		});
	}
	schauNachObBasisTotIst();
}

private void callDmgEinheit(Einheit angreifer) {
	rand = new Random();
	if (angreifer.isEnemy()) 
	myUnits.firstBekommtSchaden(angreifer.getSchaden() + rand.nextInt(5));
	else if (!angreifer.isEnemy()) enemyUnits.firstBekommtSchaden(angreifer.getSchaden() + rand.nextInt(5));
	// aufraeumen von toten Soldaten (Einheiten)
	obEinheitTotIst();
}
	
//	TimerTask timetask = new TimerTask() { //falls hitboxenerkennung dann kampfanimation von eigenen Einheiten
//		
//		public void run() { //hier wird die Kampfanimation abgespielt
//			if(soebenKollision ==true && kampfanimtest ==false)
//			{
//				
//				runOnUiThread(new Runnable() { //über runOnUiThread() kann man auf die Imageviews aus dem Mainthread zu greifen
//						@Override
//						public void run(){
//				
//						Einheit einheita = null;
//						int i = 0;
//						while (einheita == null && i < allunits.size()) {
//							if (allunits.get(i).isEnemy() == false)
//								einheita = allunits.get(i);
//
//							i++;
//						}
//						int index = allunits.indexOf(einheita);
//				
//						animationlaufzukampf(index);
//				
//				
//						@SuppressWarnings("deprecation")
//						AbsoluteLayout al = (AbsoluteLayout) findViewById(R.id.AbsoluteLayoutGame);
//						int x = 0;
//						Einheit eineeigeneEinhe = null;
//						i = 0;
//						while (eineeigeneEinhe == null && i < allunits.size()) {
//							if (!allunits.get(i).isEnemy()) {
//								eineeigeneEinhe = allunits.get(i);
//								x = eineeigeneEinhe.getXx();
//							}
//
//							i++;
//						}// hier wird die x position des stickmans übergeben und dementsprechen findet die Kampfanimation an dieser Stelle statt!
//				
//						@SuppressWarnings("deprecation")
//						AbsoluteLayout.LayoutParams lp2 = new AbsoluteLayout.LayoutParams(
//								s_walk_animation.getHeight(), 
//								+s_walk_animation.getWidth(),
//								x,
//								+s_walk_animation.getTop()); //in welcher Hoehe neu ein ImageView gespawnt wird
//						
//						al.addView(einheitbilder.get(index), lp2);
//				
//						}
//				});
//				kampfanimtest=true; //hier wird mitgeteilt, dass die Einheit aktuell kämpft ( wenn also die Einheit nachher einen Gegner tötet, wird diese Variable wieder false
//			}
//		};
//	};
//	Timer t1 = new Timer();
//	t1.schedule(timetask, 0, 100); //es wird alle zehntel sekunde gecheckt, ob die hitbox etwas getroffen hat




//Diese Funktion ueberprueft, ob eine Einheit keine hp hat. Bei keinen hp kann sie die bisher
//	bestehende Funktion gegner toeten aufrufen.
private void obEinheitTotIst() {
	if ( enemyUnits.firstSchauObTot() && enemyUnits.getAnzahl() > 0 ){
		toeteEinheit(true);
	} else if ( myUnits.firstSchauObTot() && myUnits.getAnzahl() > 0 ) {
		//einheit toeten, innerhalb der Methode wird weiterlaufen aufgerufen
		toeteEinheit(false);
	}
}

private void schauNachObBasisTotIst() {
	if (baseene < 1) {
		spielGewinnen();
	}
	if (baseown < 1) {
		spielVerlieren();
	}
}

public void spielVerlieren(){ //muss nachher in eine If bedingung in einen Timer rein, die z.B. alles halbe sekunde checkt, ob eine Base zerstört ist
	finish();
	Intent intent = new Intent (this, LoseActivity.class);
	startActivity(intent);
}

public void spielGewinnen(){ //muss nachher in eine If bedingung in einen Timer rein, die z.B. alles halbe sekunde checkt, ob eine Base zerstört ist
	finish();
	Intent intent = new Intent (this, WinActivity.class);
	startActivity(intent);
}


private void weiterlaufen(boolean isEnemy, int pos, int count){
	//Position des feindlichen Soldaten
	final int ipos = count;
	if (isEnemy) {
		
		int x = enemyUnits.getDataFromPos(pos).getXx();
		if (enemyUnits.getDataFromPos(pos).amLaufen()) 
		{ final AbsoluteLayout.LayoutParams lp = new AbsoluteLayout.LayoutParams(STDPOSWIDTH, STDPOSHEIGHT, x, STDPOSY);
		platzierenDerEinheiten.post(new Runnable() { @Override public void run() { 
			//zurueckposten des Bildes vom feindlichen Soldaten
			ImageView iv_Enemy = (ImageView) findViewById(enemyUnits.images.get(ipos));
			AnimationDrawable ad = (AnimationDrawable) iv_Enemy.getDrawable();
			if (ad.isRunning()) ad.stop();
			iv_Enemy.setImageResource(R.drawable.anim_stickman_walking_g);
			ad = (AnimationDrawable) iv_Enemy.getDrawable();
			ad.start();
			iv_Enemy.setLayoutParams(lp);
			gebelaufanim_g(enemyUnits.images.get(ipos)); } } );
		}
	}
	if (!isEnemy) {
		int x = myUnits.getDataFromPos(pos).getXx();
		if (myUnits.getDataFromPos(pos).amLaufen()) 
		{ final AbsoluteLayout.LayoutParams lp = new AbsoluteLayout.LayoutParams(STDPOSWIDTH, STDPOSHEIGHT, x, STDPOSY);
		platzierenDerEinheiten.post(new Runnable() { @Override public void run() { 
			//zurueckposten des Bildes vom feindlichen Soldaten
			ImageView iv_myUnit = (ImageView) findViewById(myUnits.images.get(ipos));
			AnimationDrawable ad = (AnimationDrawable) iv_myUnit.getDrawable();
			if (ad.isRunning()) ad.stop();
			iv_myUnit.setImageResource(R.drawable.anim_stickman_walking);
			ad = (AnimationDrawable) iv_myUnit.getDrawable();
			ad.start();
			iv_myUnit.setLayoutParams(lp); 
			gebelaufanim(myUnits.images.get(ipos)); } } );
		}
	}
	if ( ( isEnemy && count + 1 < enemyUnits.getAnzahl() ) || 
			( !isEnemy && count + 1 < myUnits.getAnzahl() )) {
		count++;
		weiterlaufen(isEnemy, (pos + 1) % 5, count);
	}
			//bewegen des Bildes
		
	// enden der Kampfanimation des Gegners
}

private void toeteEinheit(final boolean isEnemy) { 
	platzierenDerEinheiten.post(new Runnable() { @Override public void run() { 
		//loeschen von eigener Einheit - nur Bild
		ImageView iv = null;
		if (isEnemy) {iv = (ImageView) findViewById(enemyUnits.images.peekFirst()); }
		else if (!isEnemy) { iv = (ImageView) findViewById(myUnits.images.peekFirst());} 
			iv.setVisibility(View.GONE);
			iv.setImageResource(0);
			rl.removeView(iv);
			if (isEnemy) {
				enemyUnits.images.removeFirst();
			} else if ( !isEnemy ) {
				myUnits.images.removeFirst();
			}
	}}) ;
	
	//loeschen der Soldaten aus der Warteschlange / FIFO Stack
	if ( isEnemy ) {
		aktuellesguthaben += 100;
		enemyUnits.deleteFirst();
	} else if ( !isEnemy ) {myUnits.deleteFirst();  }
	
	//Daten umsetzen fuer das Laufen
	setzeUmFuerDasLaufen();
}

private void setzeUmFuerDasLaufen() {
	enemyUnits.setWorkpos((int) 0);
	myUnits.setWorkpos((int) 0);
	if ( enemyUnits.getAnzahl() > 0 ) {
		enemyUnits.aendernZuLaufenStart(enemyUnits.getDelPos());
	}
	if (myUnits.getAnzahl() > 0) {
		myUnits.aendernZuLaufenStart(enemyUnits.getDelPos());
	}
	
	//setzen der Zustandbooleans fuer die wieder laufenden Soldaten
	enemyUnits.setWorkpos((int) 0);
	myUnits.setWorkpos((int) 0);
	if (myUnits.getAnzahl() > 0) {
		weiterlaufen(false, myUnits.getDelPos(), 0);
	}
	if ( enemyUnits.getAnzahl() > 0 ) {
		weiterlaufen(true, enemyUnits.getDelPos(), 0);
	}
	
}

public void autoErstellenNachint ( int c) { // selbstaendiges erstellen von Einheiten (Bot)
	// bei jedem 'a' wird ein feindlicher Soldat erstellt
	switch (c) {
	case 'a':
		erstelleSoldat(true);
		break;
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
	  new Handler().post(new Runnable() {
		
		@Override
		public void run() {
			TextView txt = (TextView) findViewById(R.id.messages);
			txt.setText(message);
			txt.setVisibility(View.VISIBLE);
			txt.setAnimation(null);
		}
	});
	  final Animation anim = AnimationUtils.loadAnimation(returnContext(), R.anim.fade_two_sec);
	  new Handler().postDelayed((new Runnable() {
			
			@Override
			public void run() {
				TextView txt = (TextView) findViewById(R.id.messages);
				if ( txt.getAnimation() == null ) {
				txt.setAnimation(anim);
				anim.start();
				}
				txt.setVisibility(View.INVISIBLE);
				
				
			}
		}) , 2000);
  }
	
        
    private class Cooldown {
    	Timer ticktock;
    	int buttontyp;
    	public Cooldown (int millisecondstocooldown, int buttonart) {
    		int end = millisecondstocooldown;
    		buttontyp = buttonart;
    		TimerTask activateagain = new TimerTask() {
    			
    			@Override
    			public void run() {
    				if (buttontyp == ARTSOLDAT) {
    					soldatbuttonactive = true;
    					ImageView iv = (ImageView) findViewById(R.id.imageButtonSoldat);
    					iv.setClickable(true);
    				}
    					
    			}
    		};
    		ticktock = new Timer();
    		ticktock.schedule(activateagain, end);
    	}
    
    }
}

	