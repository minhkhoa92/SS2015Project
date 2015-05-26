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
	private final int ARTSOLDAT = 1;
	private final int ABSTANDZWEISOLDATEN = 130;
	private final int GRENZEFEINDLICHEBASIS = 820;
	private final int GRENZEEIGENEBASIS = 820;
	final int STDPOSWIDTH = 100;
	final int STDPOSHEIGHT = 150;
	final int STDPOSY = 500;
	
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

	
	//Variablen von ALex
	

TextView sekunden_anzeige;
TextView goldstand;
int aktuellesguthaben=100;
boolean running=true;
Handler gold_handler;


//Variable von Alex Ende
	
	        
	@Override
	public void onCreate(Bundle savedInstanceState) { // passiert wenn die Activity erstellt wird
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_game); 
	    rl = (AbsoluteLayout) findViewById(R.id.AbsoluteLayoutGame);
	  
		   
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
				new Timer().schedule(timertask, 5000, 1000);
				
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
/**				if ((int)nachfrage() > 0) testhitundkampf(); */
				
				} };
				new Timer().schedule(timernUeberpruefen, 4000, 200);
	    }
	
	protected void onStart(){ //passiert wenn die Activity gestartet wird
		super.onStart();
		Log.d("Start","Act.Start");
		myUnits = new EinheitFiFoStack(false);
		enemyUnits = new EinheitFiFoStack(true);
		baseown = 1000;
		baseene = 1000;
	}
	
	
	
	@Override
	protected void onPause() {
		super.onPause();
		//finish(); // keine Ahnung ob das den hoeheren Anforderungen entspricht 
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
//				myUnits.images.addLast(neuerSoldat);
				rl.addView(neuerSoldat, lp);
				myUnits.images.addLast(id);
				gebelaufanim(id);
			}}) ;
			myUnits.addnewsoldat();
		} else {
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
			}}) ;
		}
		//Bedingung damit der Zähler zählt, wie ein switch
		//teilt mit, dass die Einheit bereit zum kämpfen ist

		
		// dieser Block berechnet die X Koordinate unseres Stickmans
		// Minh Notiz: nach unten verschoben
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
	 
	private void startKaempfen() { // TODO fue alle Einheiten
		
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
		iv.setBackground(null);
		iv.setImageResource(R.drawable.anim_stickman_kampf);
		if (!ad.isRunning()) ad.start();
		}}) ;
	}
		

	
	private void startkaempfen_g() {
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
	/** Veraenderungen werden vorgenommen
	private void animationlaufzukampf() {
		rl.removeView(einheitbilder.get(index)); //hier entferne ich die alte animation
		einheitbilder.set(index, new ImageView(returnContext()));  // und erstelle hier die Kampfanimation
		einheitbilder.get(index).setImageResource(R.drawable.anim_stickman_kampf);
//		bild hinzufuegen an anderer Stelle
	}

	private void animationlaufzukampf_g (int index) {
		rl.removeView(einheitbilder.get(index));
		einheitbilder.set(index, new ImageView(returnContext()));  // und erstelle hier die Kampfanimation
		einheitbilder.get(index).setImageResource(R.drawable.anim_stickman_kampf_g);
//		Bild hizufuegen an anderer Stelle
	}
	*/
	
/*	protected void onResume(){ //passiert nach onStart() braucht ihr gerade eigentlich nicht beachten
		super.onResume();
		hitboxen hitbox1 = new hitboxen();
		hitbox1.runOnUiThread();
		TimerTask timetask = new TimerTask() { //falls hitboxenerkennung ==1; kampfanimation
			
			@Override
			public void run() {
				 
			    
				if (threadzurueck==true){
					//stickman_kampfmethode();
					Log.d("hier","sollte die Kampfanim hin If bedingung");
					 //s_walk_animation.setVisibility(View.GONE);
					// s_walk_animation.setImageDrawable(null);
					//SpawnKrieger();
				//AsyncTry.execute(null);
					
					}
				}		
			
		};
		Timer t1 = new Timer();
		t1.schedule(timetask, 2000, 500);
	
		
/*		TimerTask hitboxtask = new TimerTask() { //hitboxerkennung
			
			@Override
			public void run() {
				if (kampftest==true && kampftestg == true && newtest==true)
				{
					Log.d("Hitboxen","Hitboxen werden aufgerufen");
					
					if(tasknew.xx<=tasknewg.xxg+10 && tasknew.xx>=tasknewg.xxg-10)
					{
						Log.d("Kampf","Es wird gekämpft");
						newtest=false;
						threadzurueck = true;
						
						
					}
					
				}
			}
		};
		Timer t2 = new Timer();
		t2.schedule(hitboxtask,2000, 100); 
			
		         
	} */
        
public void SpawnSoldat(View Buttonsoldat) //onClick Funktion, spawn Soldaten
{
	Log.d("Soldat", "Soldat wird gespawnt!");
	ImageButton thisimagebutton = (ImageButton) findViewById(R.id.imageButtonSoldat);
	
	if((aktuellesguthaben>=20) && soldatbuttonactive)
	{	
		thisimagebutton.setClickable(false);
		soldatbuttonactive = false;  //3 sec nicht wieder aktivierbar
		int cooldowntime = 3000 ;
		new Cooldown(cooldowntime, ARTSOLDAT);
		aktuellesguthaben=aktuellesguthaben-20;	//zaehler ist der Goldwert, der Soldat kostet gerade 20 Gold
		erstelleSoldat(false);
	}
}

public void SpawnKrieger(View v) //onClick Funktion, spawnt Krieger -> Dieser Knopf lässt gerade den gegnerischen Stickman sterben und lässt unseren an der Stelle weiterlaufen. wo er zuletzt gekämpft hat
{
	
	Log.d("kek","methode wird aufgerufen");
	startkaempfen_g();
//	toetegegner_und_weiterlaufen();
}
public void gamePause(View v) //onClick Funktion, soll das Spiel pausieren.
{
//	Log.d("Pause", "Spiel wird pausiert!"); // eig soll das spiel hier pausieren, ich benutze den knopf gerade um gegner zu spawnen, ein gegner funktioniert genau wie unser Stickman, nur dass er nach links läuft, also der x wert kleiner wird
	
	erstelleSoldat(true);
}



private void testhitundkampf() { // hitboxerkennung und Kampferkennung timerstart

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
				if ( myUnits.getFirstData().isamlaufen()) {
					myUnits.setWorkpos((int) 0);
					myUnits.aendernZuKaempfenStart(myUnits.getDelPos()); // Laufen
					try {
						startKaempfen();
						}
					catch (Exception e) {
						e.printStackTrace();
					}
				}
	
				if (enemyUnits.getFirstData().isamlaufen()) {
					try {
						enemyUnits.setWorkpos((int) 0);
						enemyUnits.aendernZuKaempfenStart(enemyUnits.getDelPos());
						startkaempfen_g();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			break;
		}
	
		case 1: { //wenn nur eine eigene Einheit gespawnt ist

			if(myUnits.firstx() >= GRENZEFEINDLICHEBASIS) {//HITBOXEN! Einehit-X-Wert und vorläufiger X wert der basis
				callDmgBase(myUnits.getFirstData());
				if(myUnits.getFirstData().isamlaufen()) {
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
			if(enemyUnits.firstx() <= 60) {//HITBOXEN! Einehit-X-Wert und vorläufiger X wert der basis
				callDmgBase(enemyUnits.getFirstData());
				if (enemyUnits.getFirstData().isamlaufen()) {
//					Log.d("Kampf","GegnerEinheit läuft gegen die basis"); 
					try {
						enemyUnits.setWorkpos((int) 0);
						enemyUnits.aendernZuKaempfenStart(enemyUnits.getDelPos());
						startkaempfen_g();
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



private void callDmgBase(Einheit anEinheit) {
	if (anEinheit.isEnemy()) { baseown -= ( anEinheit.getSchaden() + rand.nextInt(5) );
	} else { baseene-= ( anEinheit.getSchaden() + rand.nextInt(5) ); }
	schauNachObBasisTotIst();
}

private void callDmgEinheit(Einheit angreifer) {
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
	if (enemyUnits.firstSchauObTot()){
		toetegegner_und_weiterlaufen();
	} else {
		//einheit toeten dann weiterlaufen
		if ( myUnits.firstSchauObTot()) {
			toeteeigen_und_weiterlaufen();
		}
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
	Intent intent = new Intent (this, LoseActivity.class);
	startActivity(intent);
	finish();
}

public void spielGewinnen(){ //muss nachher in eine If bedingung in einen Timer rein, die z.B. alles halbe sekunde checkt, ob eine Base zerstört ist
	Intent intent = new Intent (this, WinActivity.class);
	startActivity(intent);
	finish();
}

private void toeteeigen_und_weiterlaufen() { // oberer Teil wie toetegegner_und_weiterlaufen
	//Indexe der Einheiten geholt, die gekaempft haben.
	
	platzierenDerEinheiten.post(new Runnable() { @Override public void run() { 
	
		
		//loeschen von eigener Einheit - nur Bild
	rl.removeView(findViewById(myUnits.images.peekFirst()));
	myUnits.images.removeFirst();
	
	// enden der Kampfanimation des Gegners
	ImageView iv_firstEnemy = (ImageView) findViewById(enemyUnits.images.peekFirst());
	iv_firstEnemy.getAnimation().cancel();
	iv_firstEnemy.setBackgroundResource(R.drawable.anim_stickman_walking_g);
	}}) ;
	
	//Position des feindlichen Soldaten
	int x = enemyUnits.firstx();
	final AbsoluteLayout.LayoutParams lp = new AbsoluteLayout.LayoutParams(STDPOSWIDTH, STDPOSHEIGHT, x, STDPOSY);
	platzierenDerEinheiten.post(new Runnable() { @Override public void run() { 
		//zurueckposten des Bildes vom feindlichen Soldaten
		ImageView iv_firstEnemy = (ImageView) findViewById(enemyUnits.images.peekFirst());
		iv_firstEnemy.setLayoutParams(lp);
		//bewegen des Bildes
		iv_firstEnemy.setAnimation(AnimationUtils.loadAnimation(returnContext(), R.anim.horizontal_translate_g));
		iv_firstEnemy.getAnimation().start();
	}}) ;
	//setzen der Zustandbooleans fuer den wieder laufenden feindlichen Soldaten
	enemyUnits.setWorkpos((int) 0);
	enemyUnits.aendernZuLaufenStart(enemyUnits.getDelPos());
	//loeschen des eigenen Soldaten aus der Warteschlange / FIFO Stack
	myUnits.deleteFirst(); // boolean gibt zurueck ob es funktioniert hat
}

private void toetegegner_und_weiterlaufen() {
	aktuellesguthaben += 100; // Goldbonus fuer das toeten von gegnerischen Soldaten
	
	platzierenDerEinheiten.post(new Runnable() { @Override public void run() { 
	rl.removeView(findViewById(enemyUnits.images.peekFirst()));
	enemyUnits.images.removeFirst();
	enemyUnits.deleteFirst();
	
	ImageView iv_myUnit = (ImageView) findViewById(myUnits.images.peekFirst());
	iv_myUnit.getAnimation().cancel();
	iv_myUnit.setBackgroundResource(R.drawable.anim_stickman_walking);
	}}) ;
	
	
	//Position des ImageViews
	int x = myUnits.firstx();
	
	// Die Layoutparameter sind die Standardwerte in den Feldern auch oben angegeben.
	// Minh Notiz: Die hardgecodeten Layoutparameter habe ich hardgecoded gelassen.
	final AbsoluteLayout.LayoutParams lp = new AbsoluteLayout.LayoutParams(50, 50, x ,300);
	platzierenDerEinheiten.post(new Runnable() { @Override public void run() { 
		ImageView iv_myUnit = (ImageView) findViewById(myUnits.images.peekFirst());
		iv_myUnit.setLayoutParams(lp);
	}}) ;
	myUnits.setWorkpos((int) 0);
	myUnits.aendernZuLaufenStart(myUnits.getDelPos());
	platzierenDerEinheiten.post(new Runnable() { @Override public void run() { 
		ImageView iv_myUnit = (ImageView) findViewById(myUnits.images.peekFirst());
		iv_myUnit.setAnimation(AnimationUtils.loadAnimation(returnContext(), R.anim.horizontal_translate));
		iv_myUnit.getAnimation().start(); } } );
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

	