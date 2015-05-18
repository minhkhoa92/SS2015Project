package com.example.tugofwarhfu;


import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
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
	private final char ARTBASE = 0;
	private final int ABSTANDZWEISOLDATEN = 130;
	private final int GRENZEFEINDLICHEBASIS = 820;
	final int STDPOSWIDTH = 50;
	final int STDPOSHEIGHT = 50;
	final int STDPOSY = 300;
	
	// Variablen Minh
	ArrayList<Einheit> allunits;
	ArrayList<ImageView> einheitbilder;
	int endofarray = 0;
	boolean soldatbuttonactive = true;
	Einheit baseown;
	Einheit baseene;
	private int stringpos = 0;
	AbsoluteLayout rl;
	protected java.util.Random rand;

	
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
	    try {
	    	rl = (AbsoluteLayout) findViewById(R.id.AbsoluteLayoutGame);
	    } catch (Exception e) {
	    	e.printStackTrace();
	    }
	    finally {
	    	
	    }
	  
		   
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
//										goldstand.setText(String.valueOf("Gold "+aktuellesguthaben));
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
						
					// bei jedem 'a' wird ein feindlicher Soldat erstellt
					@Override public void run() {
						// aufraeumen von toten Soldaten (Einheiten)
						obEinheittotist();
						// bot, autoerstellen
						autoErstellenNachChar(getString(R.string.einheiten_erstellen).charAt(stringpos)); stringpos++; 
						if (soldatbuttonactive) { //aktiviert den Button nach dem Cooldown wieder, Aufruf aus einem wiederholten Timertask
							ImageButton thisimagebutton = (ImageButton) findViewById(R.id.imageButtonSoldat);
							thisimagebutton.setClickable(true); //aktivieren von Soldatenbutton
						}} };
				new Timer().schedule(timertask, 5000, 500);

				testhitundkampf();
	    }
	
	protected void onStart(){ //passiert wenn die Activity gestartet wird
		super.onStart();
		Log.d("Start","Act.Start");
		allunits = new ArrayList<GameActivity.Einheit>();
		einheitbilder = new ArrayList<ImageView>();
		baseown = new Einheit(false, ARTBASE);
		baseene = new Einheit(true, ARTBASE);
	}
	
	
	
	@Override
	protected void onPause() {
		super.onPause();
		//finish(); // keine Ahnung ob das den hoeheren Anforderungen entspricht 
		//finish() sorgt fuer keine Ueberbleibsel wenn die GameActivity von anderen Activities ueberlagert wird
	}
	

	private void erstelleSoldat(boolean isenemy) {
		final int jetztindex = endofarray;
		if (!isenemy){
			gold_handler.post(new Runnable() { @Override public void run() { 
			einheitbilder.add(eigenersoldatpic());
			}}) ;
		} else 
		{ gold_handler.post(new Runnable() { @Override public void run() { 
				einheitbilder.add(feindsoldatpic());
			}}) ; }
		
		Einheit neueeinheit = new Einheit(isenemy, ARTSOLDAT);
		
		allunits.add(neueeinheit);
		
		final AbsoluteLayout.LayoutParams lp = new AbsoluteLayout.LayoutParams(STDPOSWIDTH, STDPOSHEIGHT,allunits.get(jetztindex).getXx(), STDPOSY);
		gold_handler.post(new Runnable() { @Override public void run() { 
		rl.addView(einheitbilder.get(jetztindex), lp); 
		}}) ;
		if (!isenemy)
		{gold_handler.post(new Runnable() { @Override public void run() { 
			gebelaufanim(einheitbilder.get(jetztindex));
			}}) ;} else
		{gold_handler.post(new Runnable() { @Override public void run() { 
				gebelaufanimg(einheitbilder.get(jetztindex)); }}) ;}
		
//		// starte Animation
//		AnimationDrawable frameanim = (AnimationDrawable) einheitbilder.get(meinindex).getBackground();
//		frameanim.start(); //startet den Framewechsel
		
		allunits.get(jetztindex).setamlaufen(true); //Bedingung damit der Zähler zählt, wie ein switch
		//teilt mit, dass die Einheit bereit zum kämpfen ist
		
		//Startet den Zähler für die X Berechnung
		//Zaehler checkt nach ob switch amLaufen on ist, dann x++;
		allunits.get(jetztindex).startwalktimer();
		// dieser Block berechnet die X Koordinate unseres Stickmans
		// Minh Notiz: nach unten verschoben

		endofarray++;
	}

	private ImageView eigenersoldatpic() {
		ImageView neuerSoldat = new ImageView(GameActivity.this);
		neuerSoldat.setImageResource(R.drawable.soldat_my_walk_replaced);
		// erstellung eines neuen ImageViews für jeden Knopfdruck
		
		return neuerSoldat;
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
		gegnerspawn.setImageResource(R.drawable.soldat_enemy_walk_replaced);
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
			Einheit einheita = null;
			int i = 0;
		while (einheita == null && i < allunits.size()) {
			if (allunits.get(i).isEnemy() == false) einheita = allunits.get(i);
			i++;
		}
		final int index = allunits.indexOf(einheita);
	
//			gibt der Einheit eine neue Frame by Frame Animation
		gold_handler.post(new Runnable() { @Override public void run() { 
			animationlaufzukampf(index);
		}}) ;
	
//	setzt den x-wert, wo die Animation abgespielt werden soll
		int x = 0;
		x = allunits.get(index).getXx();
		// hier wird die x position des stickmans übergeben und dementsprechen findet die Kampfanimation an dieser Stelle statt!
		final AbsoluteLayout.LayoutParams lp2 = new AbsoluteLayout.LayoutParams(
				STDPOSWIDTH, 
				STDPOSHEIGHT,
				x,
				STDPOSY); //in welcher Hoehe neu ein ImageView gespawnt wird
		gold_handler.post(new Runnable() { @Override public void run() { 
		rl.addView(einheitbilder.get(index), lp2);
		}}) ;
	}
		

	
	private void startkaempfeng() {
		Log.d("Kampf","Es wird gekämpft");
		Einheit feindeinheita = null;
		int i = 0;
		while (feindeinheita == null && i < allunits.size()) {
			if (allunits.get(i).isEnemy())
				feindeinheita = allunits.get(i);
				i++;
		}
		final int myindexg = allunits.indexOf(feindeinheita);
	
		gold_handler.post(new Runnable() { @Override public void run() { 
		animationlaufzukampfg(myindexg);
		}}) ;
		
		int x = feindeinheita.getXx(); // hier wird die x position des stickmans übergeben und dementsprechen findet die Kampfanimation an dieser Stelle statt!

		final AbsoluteLayout.LayoutParams lp2 = new AbsoluteLayout.LayoutParams(
		STDPOSWIDTH, 
		STDPOSHEIGHT,
		x,
		STDPOSY);
		gold_handler.post(new Runnable() { @Override public void run() { 
			rl.addView(einheitbilder.get(myindexg), lp2);  
		}}) ;
	}
	
	private Context returnContext() {
		return this;
	}
	
	private void animationlaufzukampf(int index) {
		rl.removeView(einheitbilder.get(index)); //hier entferne ich die alte animation
		einheitbilder.set(index, new ImageView(returnContext()));  // und erstelle hier die Kampfanimation
		einheitbilder.get(index).setImageResource(R.drawable.soldat_my_fight_replaced);
//		bild hinzufuegen an anderer Stelle
	}

	private void animationlaufzukampfg (int index) {
		rl.removeView(einheitbilder.get(index));
		einheitbilder.set(index, new ImageView(returnContext()));  // und erstelle hier die Kampfanimation
		einheitbilder.get(index).setImageResource(R.drawable.soldat_enemy_fight_replaced);
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
	thisimagebutton.setClickable(false);
	if((aktuellesguthaben>=20) && soldatbuttonactive)
	{	
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
	
/*	Log.d("Krieger", "Krieger wird gespawnt!"); // eig soll dieser Knopf einen Krieger spawnen, ich benutze ihn gerade zum test fürs kämpfen
	s_walk_animation.setVisibility(View.GONE);
	s_walk_animation.setImageDrawable(null);
	
	
	ImageView huehue = new ImageView(GameActivity.this);
	huehue.setImageResource(R.drawable.anim_stickman_kampf);
	AbsoluteLayout al = (AbsoluteLayout) findViewById(R.id.AbsoluteLayoutGame);
	int height;
	int width ;
	int x = tasknew.getXx(); // hier wird die x position des stickmans übergeben und dementsprechen findet die Kampfanimation an dieser Stelle statt!
	int y;
	
	AbsoluteLayout.LayoutParams lp2 = new AbsoluteLayout.LayoutParams(
			height = s_walk_animation.getHeight(), 
			width = s_walk_animation.getWidth(),
			x,
			y = s_walk_animation.getTop());
			
	al.addView(huehue, lp2); 
	
	
 timerstartbool=false; */ // hier einkommentiert sind die alten sachen, die den krieger zum anhalten und die kampfanimation zum abspielen gebracht haben
 
 
	
	
}
public void gamePause(View v) //onClick Funktion, soll das Spiel pausieren.
{
	Log.d("Pause", "Spiel wird pausiert!"); // eig soll das spiel hier pausieren, ich benutze den knopf gerade um gegner zu spawnen, ein gegner funktioniert genau wie unser Stickman, nur dass er nach links läuft, also der x wert kleiner wird
	
	erstelleSoldat(true);
}


private void testhitundkampf() { // hitboxerkennung und Kampferkennung timerstart

	TimerTask hitboxtask = new TimerTask() { //hitboxerkennung
		@Override
		public void run() {
//			if (stringpos > 199 && stringpos < 201 && (stringpos%10 < 1) )  
//			{/*erstelleSoldat(true);*/ new Handler().post(new Runnable() {
//				
//				@Override
//				public void run() {
//					Toast.makeText(GameActivity.this, "minh in schleife", Toast.LENGTH_LONG).show();
//				}
//			});}
			if (!allunits.isEmpty()) {
				Einheit aneinheit = null;
				int i = 0;
				while (aneinheit == null && i < allunits.size()) {
					// schaut nach eigenen Einheiten nach 
					if (!allunits.get(i).isEnemy()) aneinheit = allunits.get(i);
					i++;
				}
				Einheit feindeinheit = null;
				i = 0;
				//sobald Feindeinheit belegt ist, wird die Schleife beendet
				while (feindeinheit == null && i < allunits.size()) {
					// schaut nach feindlichen Einheiten nach
					if (allunits.get(i).isEnemy()) feindeinheit = allunits.get(i);
					i++;
				}
//				testhitboxfeind(feindeinheit, aneinheit);
				
//				 nachfrage = 1 nur eine eigene Einheit
//				 nachfrage = 2 Einheit von beiden da
//				 nachfrage = 3 nur eine feindliche Einheit
				if ( (int)nachfrage() == 2) { //wenn eine eigene Einheit und eine Gegner Einheit gespawnt sind
//					Log.d("Hitboxen","Hitboxen werden aufgerufen");
					if(aneinheit.getXx() >= (feindeinheit.getXx() - ABSTANDZWEISOLDATEN)) // HITBOXEN! Einheit-X-Wert und Gegner-X-Wert
					{ 
						calldmgeinheit(aneinheit, feindeinheit);
					if ( aneinheit.isamlaufen()) {
							try {
								gold_handler.post(new Runnable() {@Override public void run(){startkaempfen();}
								});
								Log.d("Kampf","Es wird gekämpft");
								aneinheit.setKaempfen(true); //gibt zurueck, dass die hitbox mit etwas kollidiert
								aneinheit.setamlaufen(false);
								allunits.set(allunits.indexOf(aneinheit), aneinheit);
							}
							catch (Exception e) {
								e.printStackTrace();
							} finally {
								
							}
						}
						Log.d("Hitboxen","Hitboxen Gegern werden aufgerufen");
						
					if (feindeinheit.isamlaufen()) {
							try {
								allunits.get(allunits.indexOf(feindeinheit)).setamlaufen(false);
								allunits.get(allunits.indexOf(feindeinheit)).setKaempfen(true);
								gold_handler.post(new Runnable() {@Override public void run() {startkaempfeng();} });
							}
							catch (Exception e) {
								e.printStackTrace();
							} finally {
								
							}
						}
					}
				}
				
				if((int)nachfrage() == 1) { //wenn nur eine eigene Einheit gespawnt ist

					if(aneinheit.getXx() >= GRENZEFEINDLICHEBASIS) {//HITBOXEN! Einehit-X-Wert und vorläufiger X wert der basis
						calldmgbase(aneinheit);
						if(aneinheit.isamlaufen()) {
							Log.d("Kampf","Einheit läuft gegen die basis");
							try {
								aneinheit.setKaempfen(true); //gibt zurueck, dass die hitbox mit etwas kollidiert
								aneinheit.setamlaufen(false);
								allunits.set(allunits.indexOf(aneinheit), aneinheit);
								gold_handler.post(new Runnable() {@Override public void run() {startkaempfen();} });
							}
							catch (Exception e) {
								e.printStackTrace();
							} finally {
								
							}		
						}
					}
				}
				
				if((int)nachfrage() == 3) {
					calldmgbase(feindeinheit);
					//wenn nur eine gegnerische  Einheit gespawnt ist
//					final boolean k = feindeinheit.isamlaufen();
//					gold_handler.post(new Runnable() {@Override public void run() { goldstand.setText(Boolean.toString(k));} });
					if(feindeinheit.getXx()<=60) {//HITBOXEN! Einehit-X-Wert und vorläufiger X wert der basis
						if (feindeinheit.isamlaufen()) {
							Log.d("Kampf","GegnerEinheit läuft gegen die basis"); 
							try {
								allunits.get(allunits.indexOf(feindeinheit)).setKaempfen(true);//hört auf weiter zu checken
								allunits.get(allunits.indexOf(feindeinheit)).setamlaufen(false);; //gibt zurueck, dass die hitbox mit etwas kollidiert
								gold_handler.post(new Runnable() {@Override public void run() {startkaempfeng();} });
							}
							catch (Exception e) {
								e.printStackTrace();
							} finally {
								
							}
						}
					}
				}
			}
		}

		private void calldmgbase(Einheit aneinheit) {
			if (aneinheit.isEnemy()) baseown.bekommtschaden( aneinheit.getSchaden() + rand.nextInt(5) );
			else baseene.bekommtschaden( aneinheit.getSchaden() + rand.nextInt(5) );
		}

		private void calldmgeinheit(Einheit aneinheit, Einheit feindeinheit) {
			int indexa=allunits.indexOf(aneinheit), indexb=allunits.indexOf(feindeinheit);
			allunits.get(indexa).bekommtschaden(allunits.get(indexb).getSchaden() + rand.nextInt(5));
			allunits.get(indexb).bekommtschaden(allunits.get(indexa).getSchaden() + rand.nextInt(5));
		}
	};
			
	Timer thitb = new Timer();
	thitb.schedule(hitboxtask, 0, 300);
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
private void obEinheittotist() {
	if (endofarray>0){
		for (Einheit eine : allunits) {
			if ( eine.getHp() < 1) {
				if ( eine.isEnemy() ) {
					toetegegner_und_weiterlaufen();
					endofarray--;
				} else {
					//einheit toeten dann weiterlaufen
					toeteeigen_und_weiterlaufen();
					endofarray--;}
			}
		}
	}
}



public void spielVerlieren(View v){ //muss nachher in eine If bedingung in einen Timer rein, die z.B. alles halbe sekunde checkt, ob eine Base zerstört ist
	
	Intent intent = new Intent (this, LoseActivity.class);
	startActivity(intent);
	finish();
}

public void spielGewinnen(View v){ //muss nachher in eine If bedingung in einen Timer rein, die z.B. alles halbe sekunde checkt, ob eine Base zerstört ist
	
	Intent intent = new Intent (this, WinActivity.class);
	startActivity(intent);
	finish();
}

private void toeteeigen_und_weiterlaufen() { // oberer Teil wie toetegegner_und_weiterlaufen
	Einheit eigene = null;
	int i = 0;
	while (eigene == null && i < allunits.size()) {
		if (allunits.get(i).isEnemy() == false) {eigene = allunits.get(i);} i++; }
	final int eigenindex = allunits.indexOf(eigene);
	Einheit feindl = null; i = 0;
	while (feindl == null && i < allunits.size()) {
		if (allunits.get(i).isEnemy() == true) {feindl = allunits.get(i);} i++; }
	final int feindindex = allunits.indexOf(feindl);
	//Indexe der Einheiten geholt, die gekaempft haben.
	
	gold_handler.post(new Runnable() { @Override public void run() { 
	rl.removeView(einheitbilder.get(eigenindex));
	einheitbilder.get(feindindex).setImageDrawable(null);
	
	einheitbilder.set(feindindex, new ImageView(GameActivity.this));
	einheitbilder.get(feindindex).setImageResource(R.drawable.soldat_enemy_walk_replaced);
	}}) ;
	
	//Position des feindlichen Soldaten
	int x = feindl.getXx();
	final AbsoluteLayout.LayoutParams lp = new AbsoluteLayout.LayoutParams(STDPOSWIDTH, STDPOSHEIGHT, x, STDPOSY);
	gold_handler.post(new Runnable() { @Override public void run() { 
		rl.addView(einheitbilder.get(feindindex), lp); //zurueckposten des Bildes vom feindlichen Soldaten
		gebelaufanimg(einheitbilder.get(feindindex)); //bewegen des Bildes
		}}) ;
	//setzen der Zustandbooleans fuer den wieder laufenden feindlichen Soldaten
	allunits.get(feindindex).setamlaufen(true);
	allunits.get(feindindex).setKaempfen(false);
	
	//loeschen des eigenen Soldaten aus den arrays
	einheitbilder.remove(eigenindex);
	allunits.remove(eigenindex);
}

private void toetegegner_und_weiterlaufen() {
	aktuellesguthaben += 100;
	Einheit eigene = null;
	int i = 0;
	while (eigene == null && i < allunits.size()) {
		if (allunits.get(i).isEnemy() == false)
			eigene = allunits.get(i);

		i++;
	}
	final int eigenindex = allunits.indexOf(eigene);
	
	Einheit feindl = null;
	i = 0;
	while (feindl == null && i < allunits.size()) {
		if (allunits.get(i).isEnemy() == true)
			feindl = allunits.get(i);

		i++;
	}
	final int feindindex = allunits.indexOf(feindl);
	
	gold_handler.post(new Runnable() { @Override public void run() { 
	einheitbilder.get(eigenindex).setImageDrawable(null);
	einheitbilder.get(feindindex).setImageDrawable(null);
	
	einheitbilder.set(eigenindex, new ImageView(GameActivity.this));  // erstellung eines neuen ImageViews für jeden Knopfdruck
	einheitbilder.get(eigenindex).setImageResource(R.drawable.soldat_my_walk_replaced);
	}}) ;
	
	
	//Position des ImageViews
	int x = eigene.getXx();
	
	// Die Layoutparameter sind die Standardwerte in den Feldern auch oben angegeben.
	// Minh Notiz: Die hardgecodeten Layoutparameter habe ich hardgecoded gelassen.
	final AbsoluteLayout.LayoutParams lp = new AbsoluteLayout.LayoutParams(50, 50, x ,300);
	gold_handler.post(new Runnable() { @Override public void run() { 
	rl.addView(einheitbilder.get(eigenindex), lp);
	}}) ;
	allunits.get(eigenindex).setKaempfen(false);
	
	einheitbilder.remove(feindindex);
	allunits.remove(feindindex);
	
	final int neueigenindex;
	eigene = null;
	i = 0;
	while (eigene == null && i < allunits.size()) {
		if (allunits.get(i).isEnemy() == false)
			eigene = allunits.get(i);

		i++;
	}
	neueigenindex = allunits.indexOf(eigene);
	allunits.get(neueigenindex).setamlaufen(true);
	gold_handler.post(new Runnable() { @Override public void run() { 
	gebelaufanim(einheitbilder.get(neueigenindex));
	}}) ;
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
	Einheit eigen = null;
	Einheit feind = null;
	char returnchar = 0;
	int i = 0;
	while (!allunits.isEmpty() && ( i < allunits.size() 
			&& (eigen == null || feind == null) ) ) {
		if (allunits.get(i).isEnemy() && feind == null) feind = allunits.get(i);
		else if (eigen == null) eigen = allunits.get(i);
		i++;
	}
	// beschriebene Werte werden zu != null
	//	beschrieben sind Faelle au\sser der default wenn keine Einheit gespawnt ist
	if (!allunits.isEmpty()) {
		if ( eigen != null && feind != null ) { 
			returnchar = 2;
		}
		if ( eigen != null && feind == null) {
			returnchar = 1;
		}
		if ( eigen == null && feind != null ) {
			returnchar = 3;
		} 
	} else returnchar = 0;
	return returnchar;
}

  
    private class Einheit {
    	private static final int DELAYTOSPEED = 10;
    	public int xx;
    	
    	private boolean laeuft = false;
    	private int hp;
    	private int schaden = 0;
    	

		private char einheitart;
    	private boolean enemy;
    	private boolean kaempft = false;


    	public Einheit(boolean isenemy, char kategorie){
    			xx = 0;
    			if (isenemy) xx = 1000;
    			enemy = isenemy;
    			einheitart = kategorie;
    			if (einheitart == ARTSOLDAT) {
    				schaden = 5;
    				hp = 100;
    			}
    			if (einheitart == ARTBASE) {
    				hp = 1000;
    			}
    	}
    	
    	
    	
		public void startwalktimer() {
			new Timer().scheduleAtFixedRate(new TimerAddX(), 0, DELAYTOSPEED);
		}


		public void setamlaufen(boolean einheitistamlaufen) {
			this.laeuft = einheitistamlaufen;
			Log.w("test", "und geht");
		}
		
		public boolean isamlaufen() {
			return laeuft;
		}
		
		public boolean isKaempfen() {
			return kaempft;
		}

		public void setKaempfen(boolean kaempfen) {
			this.kaempft = kaempfen;
		}

		public int getXx() {
    		return xx;
    	}
    	
		public boolean isEnemy() {
			return enemy;
		}

		public int getHp() {
			return hp;
		}
		
		public int getSchaden() {
			return schaden;
		}
		
		public void bekommtschaden(int angerichteterschaden){
			hp -= angerichteterschaden;
		}

		class TimerAddX extends TimerTask { // gibt den X wert des eigenen Stickmans an

        	public void run() {
        		  if(isamlaufen()){
        			  if (isEnemy()) xx--;
        			  if (!isEnemy()) xx++;
        		  }
        	}
        }
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
    				}
    					
    			}
    		};
    		ticktock = new Timer();
    		ticktock.schedule(activateagain, end);
    	}
    
    }
}

	