package com.example.tugofwarhfu;


import java.util.ArrayList;
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
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;


@SuppressWarnings("deprecation")
public class GameActivity extends Activity {

	// Konstanten Minh
	private final char ARTSOLDAT = 1;
	private final int ABSTANDZWEISOLDATEN = 130;
	private final int GRENZEFEINDLICHEBASIS = 820;
	final int STDPOSWIDTH = 150;
	final int STDPOSHEIGHT = 250;
	final int STDPOSY = 250;
	
	// Variablen Minh
	EinheitFiFostack myUnits;// Einheiten
	EinheitFiFostack enemyUnits;
	boolean soldatbuttonactive = true;
	int baseown;
	int baseene;
	private int stringpos = 0;
	AbsoluteLayout rl;
	protected Random rand;

	
	//Variablen von ALex
	

TextView sekunden_anzeige;
TextView goldstand;
int aktuellesguthaben=100;
Handler gold_handler;
boolean running=true;


//Variable von Alex Ende
	
	        
	@Override
	public void onCreate(Bundle savedInstanceState) { // passiert wenn die Activity erstellt wird
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_game); 
	    rl = (AbsoluteLayout) findViewById(R.id.AbsoluteLayoutGame);
	  
		   
			    //Goldschleife von Alex
			    goldstand=(TextView) findViewById(R.id.gold_anzeige);
				gold_handler=new Handler();
				
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
				
				TimerTask timertask = new TimerTask() {
					@Override public void run() {
						// bot, autoerstellen
						// bei jedem 'a' wird ein feindlicher Soldat erstellt
						autoErstellenNachChar(getString(R.string.einheiten_erstellen).charAt(stringpos)); stringpos++; 
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
				if ((int)nachfrage() > 0) testhitundkampf();
				
				} };
				new Timer().schedule(timernUeberpruefen, 4000, 200);
	    }
	
	protected void onStart(){ //passiert wenn die Activity gestartet wird
		super.onStart();
		Log.d("Start","Act.Start");
		myUnits = new EinheitFiFostack(false);
		myUnits.resetImageViews(new ImageView(this), new ImageView(this), new ImageView(this), new ImageView(this), new ImageView(this));
		enemyUnits = new EinheitFiFostack(true);
		enemyUnits.resetImageViews(new ImageView(this), new ImageView(this), new ImageView(this), new ImageView(this), new ImageView(this));
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
		if (!isenemy){
			gold_handler.post(new Runnable() { @Override public void run() { 
				ImageView neuerSoldat = new ImageView(returnContext());	
				neuerSoldat.setImageResource(R.drawable.anim_stickman_walking);
				// erstellung eines neuen ImageViews für jeden Knopfdruck
				myUnits.addPic();
			}}) ;
			myUnits.addnewsoldat(); //TODO umschreiben fuer alle Units
			xStartValue = myUnits.firstx();
		} else {
			gold_handler.post(new Runnable() { @Override public void run() { 
				einheitbilder.add(feindsoldatpic());
			}}) ; 
			enemyUnits.addnewsoldat();
			enemyUnits.firstx(); // TODO umschreiben fuer alle Gegner
		}
		
		final AbsoluteLayout.LayoutParams lp = new AbsoluteLayout.LayoutParams(STDPOSWIDTH, STDPOSHEIGHT, xStartValue, STDPOSY);
		gold_handler.post(new Runnable() { @Override public void run() { 
		rl.addView(einheitbilder.get(0), lp); 
		}}) ;
		if (!isenemy)
		{gold_handler.post(new Runnable() { @Override public void run() { 
			gebelaufanim(einheitbilder.get(0));
			}}) ;} else
		{gold_handler.post(new Runnable() { @Override public void run() { 
				gebelaufanimg(einheitbilder.get(0)); }}) ;}
		
		//Bedingung damit der Zähler zählt, wie ein switch
		//teilt mit, dass die Einheit bereit zum kämpfen ist

		
		// dieser Block berechnet die X Koordinate unseres Stickmans
		// Minh Notiz: nach unten verschoben
	}

	
	private void gebelaufanim(ImageView testnu) {
		TranslateAnimation ta = stickman_walk_Animation();
		testnu.setAnimation(ta); // das neue image view wird sichtbar gemacht und ihm wird die animation zugewiesen
		testnu.startAnimation(ta);
	}
	
	private TranslateAnimation stickman_walk_Animation(){ //Bewegung des eigenen stickmans
	    TranslateAnimation stickman_walking_animation = new TranslateAnimation(0.0f, 1000.0f,0.0f, 0.0f);
	    stickman_walking_animation.setInterpolator(new LinearInterpolator());
	    stickman_walking_animation.setDuration(10000);
	    return stickman_walking_animation;
	    };
	    
	private ImageView feindsoldatpic() {
		ImageView gegnerspawn = new ImageView(GameActivity.this);
		gegnerspawn.setImageResource(R.drawable.anim_stickman_walking_g);
		return gegnerspawn;
	}

	private void gebelaufanimg(ImageView feindsold) {
		TranslateAnimation ta = stickman_walk_Animation_gegner();
		feindsold.setAnimation(ta);
		feindsold.startAnimation(ta);
	}
	
	private TranslateAnimation stickman_walk_Animation_gegner(){ //Bewegung des gegnerischen Stickmans
	    TranslateAnimation stickman_walking_animation_gegner = new TranslateAnimation(0.0f, -1000.0f,0.0f, 0.0f);
	    stickman_walking_animation_gegner.setInterpolator(new LinearInterpolator());
	    stickman_walking_animation_gegner.setDuration(10000);
	    return stickman_walking_animation_gegner;
	}
	 
	private void startkaempfen() {
//		runOnUiThread(new Runnable() { //über runOnUiThread() kann man auf die Imageviews aus dem Mainthread zu greifen


//	bringt die erste Einheit in den index

//			gibt der Einheit eine neue Frame by Frame Animation
		gold_handler.post(new Runnable() { @Override public void run() { 
			animationlaufzukampf(0); // TODO fuer alle units
		}}) ;
	
//	setzt den x-wert, wo die Animation abgespielt werden soll
		int x = 0;
		x = myUnits.firstx();
		// hier wird die x position des stickmans übergeben und dementsprechen findet die Kampfanimation an dieser Stelle statt!
		final AbsoluteLayout.LayoutParams lp2 = new AbsoluteLayout.LayoutParams(
				STDPOSWIDTH, 
				STDPOSHEIGHT,
				x,
				STDPOSY); //in welcher Hoehe neu ein ImageView gespawnt wird
		gold_handler.post(new Runnable() { @Override public void run() { 
		rl.addView(einheitbilder.get(0), lp2);  // TODO fuer alle Einheiten spaeter
		}}) ;
	}
		

	
	private void startkaempfen_g() {
//		Log.d("Kampf","Es wird gekämpft");
		
	
		gold_handler.post(new Runnable() { @Override public void run() { 
		animationlaufzukampf_g(1);
		}}) ;
		
		int x = enemyUnits.firstx(); // hier wird die x position des stickmans übergeben und dementsprechen findet die Kampfanimation an dieser Stelle statt!

		final AbsoluteLayout.LayoutParams lp2 = new AbsoluteLayout.LayoutParams(
		STDPOSWIDTH, 
		STDPOSHEIGHT,
		x,
		STDPOSY);
		gold_handler.post(new Runnable() { @Override public void run() { 
			rl.addView(einheitbilder.get(1), lp2);  // TODO fuer alle Einheiten spaeter
		}}) ;
	}
	
	private Context returnContext() {
		return this;
	}
	
	private void animationlaufzukampf(int index) {
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
	toetegegner_und_weiterlaufen();
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
	char c = nachfrage();
	switch (c) {
		case 2: { //wenn eine eigene Einheit und eine Gegner Einheit gespawnt sind
	//		Log.d("Hitboxen","Hitboxen werden aufgerufen");
			if(( myUnits.firstx() - enemyUnits.firstx() ) >= ABSTANDZWEISOLDATEN) // HITBOXEN! Einheit-X-Wert und Gegner-X-Wert
			{ 
				callDmgEinheit(myUnits.getEin1(), enemyUnits.getEin1()); // TODO test ob der Schaden nur in eine Richtung laeuft
				callDmgEinheit(enemyUnits.getEin1(), myUnits.getEin1());
				if ( myUnits.getEin1().isamlaufen()) {
					myUnits.firstLaufenZuKaempfen(); // Laufen
					try {
						gold_handler.post(new Runnable() {@Override public void run(){startkaempfen();}
						});
//						Log.d("Kampf","Es wird gekämpft");
						}
					catch (Exception e) {
						e.printStackTrace();
					}
				}
				Log.d("Hitboxen","Hitboxen Gegern werden aufgerufen");
	
				if (enemyUnits.getEin1().isamlaufen()) {
					try {
						enemyUnits.firstLaufenZuKaempfen();
						gold_handler.post(new Runnable() {@Override public void run() {startkaempfen_g();} });
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			break;
		}
	
		case 1: { //wenn nur eine eigene Einheit gespawnt ist

			if(myUnits.firstx() >= GRENZEFEINDLICHEBASIS) {//HITBOXEN! Einehit-X-Wert und vorläufiger X wert der basis
				calldmgbase(myUnits.getEin1());
				if(myUnits.getEin1().isamlaufen()) {
					Log.d("Kampf","Einheit läuft gegen die basis");
					try {
						myUnits.firstLaufenZuKaempfen();
						gold_handler.post(new Runnable() {@Override public void run() {startkaempfen();} });
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
				calldmgbase(enemyUnits.getEin1());
				if (enemyUnits.getEin1().isamlaufen()) {
//					Log.d("Kampf","GegnerEinheit läuft gegen die basis"); 
					try {
						enemyUnits.firstLaufenZuKaempfen();
						gold_handler.post(new Runnable() {@Override public void run() {startkaempfen_g();} });
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

private void calldmgbase(Einheit aneinheit) {
	if (aneinheit.isEnemy()) { baseown -= ( aneinheit.getSchaden() + rand.nextInt(5) );
	} else { baseene-= ( aneinheit.getSchaden() + rand.nextInt(5) ); }
	schauNachObBasisTotIst();
}

private void callDmgEinheit(Einheit a, Einheit b) {
	a.bekommtschaden(b.getSchaden() + rand.nextInt(5));
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
	
	gold_handler.post(new Runnable() { @Override public void run() { 
	
	einheitbilder.get(1).setImageDrawable(null); // TODO fuer alle Bilder umprogrammieren
	
	einheitbilder.set(1, new ImageView(GameActivity.this));
	einheitbilder.get(1).setImageResource(R.drawable.anim_stickman_walking);
	rl.removeView(einheitbilder.get(0));
	}}) ;
	
	//Position des feindlichen Soldaten
	int x = enemyUnits.firstx();
	final AbsoluteLayout.LayoutParams lp = new AbsoluteLayout.LayoutParams(STDPOSWIDTH, STDPOSHEIGHT, x, STDPOSY);
	gold_handler.post(new Runnable() { @Override public void run() { 
		rl.addView(einheitbilder.get(1), lp); //zurueckposten des Bildes vom feindlichen Soldaten
		gebelaufanimg(einheitbilder.get(1)); //bewegen des Bildes
		}}) ;
	//setzen der Zustandbooleans fuer den wieder laufenden feindlichen Soldaten
	enemyUnits.firstKaempfenZuLaufen();
	//loeschen des eigenen Soldaten aus den arrays
	einheitbilder.remove(0);
	myUnits.deleteEinheitCascade(); // boolean gibt zurueck ob es funktioniert hat
}

private void toetegegner_und_weiterlaufen() {
	aktuellesguthaben += 100; // Goldbonus fuer das toeten von gegnerischen Soldaten
	
	gold_handler.post(new Runnable() { @Override public void run() { 
	einheitbilder.get(0).setImageDrawable(null);
	einheitbilder.get(1).setImageDrawable(null);
	
	einheitbilder.set(0, new ImageView(GameActivity.this));  // erstellung eines neuen ImageViews für jeden Knopfdruck
	einheitbilder.get(0).setImageResource(R.drawable.anim_stickman_walking);
	}}) ;
	
	
	//Position des ImageViews
	int x = myUnits.firstx();
	
	// Die Layoutparameter sind die Standardwerte in den Feldern auch oben angegeben.
	// Minh Notiz: Die hardgecodeten Layoutparameter habe ich hardgecoded gelassen.
	final AbsoluteLayout.LayoutParams lp = new AbsoluteLayout.LayoutParams(50, 50, x ,300);
	gold_handler.post(new Runnable() { @Override public void run() { 
	rl.addView(einheitbilder.get(0), lp);
	}}) ;
	myUnits.firstKaempfenZuLaufen();
	gold_handler.post(new Runnable() { @Override public void run() { 
		gebelaufanim(einheitbilder.get(0)); } } );
	einheitbilder.remove(1);
	enemyUnits.firstKaempfenZuLaufen();;
}



public void autoErstellenNachChar ( char c) { // selbstaendiges erstellen von Einheiten (Bot)
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
private char nachfrage() {
	char returnchar = 0;
	// beschriebene Werte werden zu != null
	//	beschrieben sind Faelle au\sser der default wenn keine Einheit gespawnt ist
	if (myUnits.getAnzahl() > 0 || enemyUnits.getAnzahl() > 0) {
		if ( myUnits.getAnzahl() > 0 && enemyUnits.getAnzahl() > 0 ) { 
			returnchar = 2;
		}
		if ( myUnits.getAnzahl() > 0 && enemyUnits.getAnzahl() < 1) {
			returnchar = 1;
		}
		if ( myUnits.getAnzahl() < 1 && enemyUnits.getAnzahl() > 0 ) {
			returnchar = 3;
		} 
	} else returnchar = 0;
	return returnchar;
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

	