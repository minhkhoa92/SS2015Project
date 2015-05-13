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
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;


public class GameActivity extends Activity {

	// Konstanten Minh
	private final char ARTBASE = 0;
	private final char ARTSOLDAT = 1;
	private final int ABSTANDZWEISOLDATEN = 130;
	private final int GRENZEFEINDLICHEBASIS = 820;
	
	// Variablen Minh
	ArrayList<Einheit> allunits;
	ArrayList<ImageView> einheitbilder;
	Einheit baseown;
	Einheit baseenemy;
	int endofarray = 0;
	boolean soldatbuttonactive = true;
	
	ImageView s_fight_animation;
	ImageView s_walk_animation;
	ImageView s_walk_animation_g;
	ImageView kampf_animation;
	ImageView kampf_animation_g;
	
	public boolean timerstartboolg = false;

	private boolean kampftestg = false;
	private boolean hitboxtestg = true;
	private boolean soebenKollision = false;
	private boolean threadzrueckg = false;
	private boolean kampfanimtest = false;
	private boolean kampfanimtestg = false;
	private boolean kampftest = false;
	

	//Variablen von ALex
	

ImageButton einheit;
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
	   final ImageView animImageView_walk = (ImageView) findViewById(R.id.stickman_walk); // erstellung des ImageView des eigenen Stickmans
	    animImageView_walk.setBackgroundResource(R.drawable.anim_stickman_walking);
		    animImageView_walk.post(new Runnable() {
		        @Override
		        public void run() {
		            AnimationDrawable frameAnimation =
		                (AnimationDrawable) animImageView_walk.getBackground();
		            frameAnimation.start();
		     
		        }
		    }); 
		   
		    final ImageView animImageView_walk_g = (ImageView) findViewById(R.id.stickman_walk_gegner); // erstellung des ImageView des gegnerischen Stickmans
		    animImageView_walk_g.setBackgroundResource(R.drawable.anim_stickman_walking_g);
			    animImageView_walk_g.post(new Runnable() {
			        @Override
			        public void run() {
			            AnimationDrawable frameAnimation =
			                (AnimationDrawable) animImageView_walk_g.getBackground();
			            frameAnimation.start();
			       
			        }
			    }); 
			    
			    
			    //Goldschleife von Alex
			    goldstand=(TextView) findViewById(R.id.gold_anzeige);
				einheit=(ImageButton) findViewById(R.id.imageButtonSoldat);
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
			    
			    
	    }
	 
	
	protected void onStart(){ //passiert wenn die Activity gestartet wird
		super.onStart();
		Log.d("Start","Act.Start");
		allunits = new ArrayList<GameActivity.Einheit>();
		einheitbilder = new ArrayList<ImageView>();
		baseown = new Einheit(false, ARTBASE);
		baseenemy = new Einheit(true, ARTBASE);
		testhitundkampf();
		
	}
	
	
	
	private ImageView eigenersoldatpic() {
		ImageView neuerSoldat = new ImageView(GameActivity.this);  // erstellung eines neuen ImageViews für jeden Knopfdruck
		neuerSoldat.setImageResource(R.drawable.anim_stickman_walking);
		return neuerSoldat;
	}
	
	private void gebelaufanim(ImageView testnu) {
		s_walk_animation = (ImageView) testnu;
		s_walk_animation.setVisibility(View.VISIBLE);
		s_walk_animation.startAnimation(stickman_walk_Animation()); // das neue image view wird sichtbar gemacht und ihm wird die animation zugewiesen
	}
	
	private TranslateAnimation stickman_walk_Animation(){ //Bewegung des eigenen stickmans
	    TranslateAnimation stickman_walking_animation = new TranslateAnimation(0.0f, 1000.0f,0.0f, 0.0f);
	    stickman_walking_animation.setInterpolator(new LinearInterpolator());
	    stickman_walking_animation.setDuration(10000);
	    stickman_walking_animation.setRepeatCount(0);
	    stickman_walking_animation.setRepeatMode(0);
	    stickman_walking_animation.setFillAfter(false); 
	    return stickman_walking_animation;
	    };
	    
	private ImageView feindsoldatpic() {
		ImageView gegnerspawn = new ImageView(GameActivity.this);
		gegnerspawn.setImageResource(R.drawable.anim_stickman_walking_g);
		return gegnerspawn;
	}

	private void gebelaufanimg(ImageView feindsold) {
		s_walk_animation_g = feindsold;
		s_walk_animation_g.setVisibility(View.VISIBLE);
		s_walk_animation_g.startAnimation(stickman_walk_Animation_gegner());
	}
	
	private TranslateAnimation stickman_walk_Animation_gegner(){ //Bewegung des gegnerischen Stickmans
	    TranslateAnimation stickman_walking_animation_gegner = new TranslateAnimation(0.0f, -1000.0f,0.0f, 0.0f);
	    stickman_walking_animation_gegner.setInterpolator(new LinearInterpolator());
	    stickman_walking_animation_gegner.setDuration(10000);
	    stickman_walking_animation_gegner.setRepeatCount(0);
	    stickman_walking_animation_gegner.setRepeatMode(0);
	    stickman_walking_animation_gegner.setFillAfter(false); 
	    return stickman_walking_animation_gegner;
	}
	  
	private void animationlaufzukampf(int index) {
		einheitbilder.get(index).setVisibility(View.GONE); //hier entferne ich die alte animation
		einheitbilder.get(index).setImageDrawable(null); 
		
		einheitbilder.set(index, new ImageView(GameActivity.this)); // und erstelle hier die Kampfanimation
		einheitbilder.get(index).setImageResource(R.drawable.anim_stickman_kampf);
		allunits.get(index).setKaempfen(true);
	}

	private void animationlaufzukampfg (int index) {
		einheitbilder.get(index).setVisibility(View.GONE);
		einheitbilder.get(index).setImageDrawable(null);
		
		einheitbilder.set(index, new ImageView(GameActivity.this));
		einheitbilder.get(index).setImageResource(R.drawable.anim_stickman_kampf_g);
		allunits.get(index).setKaempfen(true);
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
        
@SuppressWarnings("deprecation")
public void SpawnSoldat(View Buttonsoldat) //onClick Funktion, spawn Soldaten
{
	
	Log.d("Soldat", "Soldat wird gespawnt!");
	
	if((aktuellesguthaben>=20) && soldatbuttonactive)
		 {	
			ImageButton thisimagebutton = (ImageButton) findViewById(R.id.imageButtonSoldat);
			thisimagebutton.setClickable(false);
			soldatbuttonactive = false;  //3 sec nicht wieder aktivierbar
			int cooldowntime = 3000 ;
			new Cooldown(cooldowntime, ARTSOLDAT);
			aktuellesguthaben=aktuellesguthaben-20;	//zaehler ist der Goldwert, der Soldat kostet gerade 20 Gold
			einheitbilder.add(endofarray, eigenersoldatpic());
			Einheit eigeneeinheit = new Einheit(false, ARTSOLDAT);
			int meinindex = endofarray;
			allunits.add(meinindex, eigeneeinheit);
			eigenersoldatpic();
		
			AbsoluteLayout rl = (AbsoluteLayout) findViewById(R.id.AbsoluteLayoutGame); //Position des ImageViews
			AbsoluteLayout.LayoutParams lp = new AbsoluteLayout.LayoutParams(350, 350,0,300);
			rl.addView(einheitbilder.get(meinindex), lp); 
			gebelaufanim(einheitbilder.get(meinindex));
			
			
			allunits.get(meinindex).setamlaufen(true); //Bedinung damit der Zähler zählt
			
			//Startet den Zähler für die X Berechnung
			allunits.get(meinindex).startwalktimer();
			// dieser Block berechnet die X Koordinate unseres Stickmans
			//teilt mit, dass die Einheit bereit zum kämpfen ist
			endofarray++;
	}
}

private void testhitundkampf() { // hitboxerkennung und Kampferkennung timerstart
	TimerTask hitboxtask = new TimerTask() { //hitboxerkennung
		
		@Override
		public void run() {
			if (!allunits.isEmpty()) { //wenn keine Einheit da ist
				Einheit aneinheit = null;
				int i = 0;
				while (aneinheit == null && i < allunits.size()) {
					// schaut nach eigenen Einheiten nach und ob sie laufen
					// wenn laufen == true heisst das sie sind bereit zum kaempfen oder warten
					if (!allunits.get(i).isEnemy() && allunits.get(i).isamlaufen())
						aneinheit = allunits.get(i);
	
					i++;
				}
				Einheit feindeinheit = null;
				i = 0;
				//sobald Feindeinheit belegt ist, wird die Schleife beendet
				while (feindeinheit == null && i < allunits.size()) {
					// schaut nach eigenen Einheiten nach und ob sie laufen
					// wenn laufen == true heisst das sie sind bereit zum kaempfen oder warten
					if (allunits.get(i).isEnemy() && allunits.get(i).isamlaufen())
						feindeinheit = allunits.get(i);
					i++;
				}
				
				testhitboxfeind(feindeinheit, aneinheit);
				if(aneinheit != null) { //eigene Einheit da, die kaempft oder wartet
					kampftest = true;
					
					if (feindeinheit != null) //wenn eine eigene Einheit und eine Gegner Einheit gespawnt sind
					{
	//					Log.d("Hitboxen","Hitboxen werden aufgerufen");
						if ( feindeinheit != null ) {
							if(aneinheit.getXx() >= (feindeinheit.getXx() - ABSTANDZWEISOLDATEN)) // HITBOXEN! Einheit-X-Wert und Gegner-X-Wert
							{
								Log.d("Kampf","Es wird gekämpft");
//								soebenKollision = true; //gibt zurueck, dass die hitbox mit etwas kollidiert
								allunits.get(allunits.indexOf(aneinheit)).setKaempfen(true);
								allunits.get(allunits.indexOf(aneinheit)).setamlaufen(false);
							}
						}
					
					}
					else if(feindeinheit == null) //wenn nur eine eigene Einheit gespawnt ist
					{
						if(aneinheit.getXx() >= GRENZEFEINDLICHEBASIS) //HITBOXEN! Einehit-X-Wert und vorläufiger X wert der basis
						{
							Log.d("Kampf","Einheit läuft gegen die basis");
//							soebenKollision = true; //gibt zurueck, dass die hitbox mit etwas kollidiert
							allunits.get(allunits.indexOf(aneinheit)).setamlaufen(false);
							allunits.get(allunits.indexOf(aneinheit)).setKaempfen(true);
						}
					}
				}
				else { // wenn keine eigene Einheit da ist
					kampftest = false;
					
				}
			}
			
		}
		

		
	};
	Timer thitb = new Timer();
	thitb.schedule(hitboxtask,0,100); 
	
	
	TimerTask timetask = new TimerTask() { //falls hitboxenerkennung dann kampfanimation von eigenen Einheiten
		
		public void run() { //hier wird die Kampfanimation abgespielt
			Einheit meine = null;
			int i = 0;
			while (meine == null && i < allunits.size()) {
				if (allunits.get(i).isEnemy() == false)
					meine = allunits.get(i);

				i++;
			}
			
			if(meine.kaempft == true && kampfanimtest ==false)
			{
				
				runOnUiThread(new Runnable() { //über runOnUiThread() kann man auf die Imageviews aus dem Mainthread zu greifen
					@Override
					public void run(){
				
				Einheit einheita = null;
				int i = 0;
				while (einheita == null && i < allunits.size()) {
					if (allunits.get(i).isEnemy() == false)
						einheita = allunits.get(i);

					i++;
				}
				int index = allunits.indexOf(einheita);
				
				animationlaufzukampf(index);
				
				
				@SuppressWarnings("deprecation")
				AbsoluteLayout al = (AbsoluteLayout) findViewById(R.id.AbsoluteLayoutGame);
				int x = 0;
				Einheit eineeigeneEinhe = null;
				i = 0;
				while (eineeigeneEinhe == null && i < allunits.size()) {
					if (!allunits.get(i).isEnemy()) {
						eineeigeneEinhe = allunits.get(i);
						x = eineeigeneEinhe.getXx();
					}

					i++;
				}// hier wird die x position des stickmans übergeben und dementsprechen findet die Kampfanimation an dieser Stelle statt!
				
				@SuppressWarnings("deprecation")
				AbsoluteLayout.LayoutParams lp2 = new AbsoluteLayout.LayoutParams(
						s_walk_animation.getHeight(), 
						+s_walk_animation.getWidth(),
						x,
						+s_walk_animation.getTop()); //in welcher Hoehe neu ein ImageView gespawnt wird
						
				al.addView(einheitbilder.get(index), lp2);
				
				}
			});
//			soebenKollision = false;
			kampfanimtest=true; //hier wird mitgeteilt, dass die Einheit aktuell kämpft ( wenn also die Einheit nachher einen Gegner tötet, wird diese Variable wieder false
			}		
		
		};
	};
	Timer t1 = new Timer();
	t1.schedule(timetask, 0, 100); //es wird alle zehntel sekunde gecheckt, ob die hitbox etwas getroffen hat

TimerTask timetaskg = new TimerTask() { //falls hitboxenerkennung  kampfanimation
		
		public void run() { //hier soll die Kampfanimation abgespielt werden			 
			if (!allunits.isEmpty()) {
				Einheit feindeinheita = null;
				int i = 0;
				while (feindeinheita == null && i < allunits.size()) {
					if (allunits.get(i).isEnemy())
						feindeinheita = allunits.get(i);
	
					i++;
				}
				if (feindeinheita != null) {
					int myindexg = allunits.indexOf(feindeinheita);
					if(allunits.get(myindexg).isKaempfen()==true && kampfanimtestg ==false)
					{	
						runOnUiThread(new Runnable() {
							
							@Override
							public void run() {
								Einheit feindeinheita = null;
								int i = 0, myindex;
								while (feindeinheita == null && i < allunits.size()) {
									if (allunits.get(i).isEnemy())
										feindeinheita = allunits.get(i);
									i++;
								}
								myindex = allunits.indexOf(feindeinheita);
								
								animationlaufzukampfg(myindex);
								@SuppressWarnings("deprecation")
								AbsoluteLayout al = (AbsoluteLayout) findViewById(R.id.AbsoluteLayoutGame);
				
								int x = allunits.get(myindex).getXx(); // hier wird die x position des stickmans übergeben und dementsprechen findet die Kampfanimation an dieser Stelle statt!
				
								
								@SuppressWarnings("deprecation")
								AbsoluteLayout.LayoutParams lp2 = new AbsoluteLayout.LayoutParams(
								s_walk_animation_g.getHeight(), 
								s_walk_animation_g.getWidth(),
								x,
								s_walk_animation_g.getTop());
										
								al.addView(einheitbilder.get(myindex), lp2);  
								
							}
						});
						kampfanimtestg=true;
					}
							
				}
			}
		obEinheittotist(); // Aufruf aus einem wiederholten Timertask
		if (soldatbuttonactive) { //aktiviert den Button nach dem Cooldown wieder, Aufruf aus einem wiederholten Timertask
			ImageButton thisimagebutton = (ImageButton) findViewById(R.id.imageButtonSoldat);
			thisimagebutton.setClickable(true);
		}
		}
		
	};
	Timer tg = new Timer();
	tg.schedule(timetaskg, 0, 100); 
	
	TimerTask schaden = new TimerTask() {
		
		@Override
		public void run() {
			if (!allunits.isEmpty()) {
				Random rand = new Random();
				boolean enemyfights = false , ownsoldierfights = false;
			
				for (Einheit eine : allunits) {
					if (eine.isKaempfen()) {
						if (eine.isEnemy()) enemyfights = true;
						else ownsoldierfights = true;
					}
				}
				Einheit ownsold = null;
				int i = 0, ownindex = 0;
				while (ownsold == null && i < allunits.size()) {
					if (!allunits.get(i).isEnemy())
						ownsold = allunits.get(i);
						ownindex = i;
					i++;
				}
				Einheit enemysold = null;
				i = 0; int enemyindex = 0;
				while (enemysold == null && i < allunits.size()) {
					if (allunits.get(i).isEnemy())
						enemysold = allunits.get(i);
						enemyindex = i;
					i++;
				}
				if (enemyfights && ownsoldierfights) {
					
					allunits.get(ownindex).bekommtschaden
					(enemysold.getSchaden() + rand.nextInt(5));
					allunits.get(enemyindex).bekommtschaden
					(ownsold.getSchaden() + rand.nextInt(5));
				}
				if (enemyfights && !ownsoldierfights) {
					baseown.bekommtschaden(enemysold.getSchaden() + rand.nextInt(5));
//					Log.w("Basisown", "Eigene Basis bekommt Schaden");
				}
				if (!enemyfights && ownsoldierfights) {
					baseenemy.bekommtschaden(ownsold.getSchaden() + rand.nextInt(5));
//					Log.w("Basisene", "Gegnerbasis wird geschaedigt");
				}
			}
		}
	};
	Timer schadenuhr = new Timer();
	schadenuhr.schedule(schaden, 0 , 900);
}

private void testhitboxfeind(Einheit feindeinheit, Einheit eigeneEin) {
	if (!allunits.isEmpty()) {
		
		
		if (feindeinheit != null) {
			if (feindeinheit.isamlaufen()) {
				int feindindex = allunits.indexOf(feindeinheit);
				boolean nurgegner = true;
				for (Einheit e : allunits)
				{if (!e.isEnemy()) nurgegner = false;}
				if(nurgegner) //wenn nur eine gegnerische  Einheit gespawnt ist
				{
					if(allunits.get(feindindex).getXx()<=60) //HITBOXEN! Einehit-X-Wert und vorläufiger X wert der basis
					{
						Log.d("Kampf","GegnerEinheit läuft gegen die basis"); 
						allunits.get(feindindex).setamlaufen(false);
						hitboxtestg=false;//hört auf weiter zu checken
						allunits.get(feindindex).setKaempfen(true); //gibt zurueck, dass die hitbox mit etwas kollidiert
						timerstartboolg=false;
					}
				} 
				else {
			//		Log.d("Hitboxen","Hitboxen Gegern werden aufgerufen");
					int xeigen = eigeneEin.getXx();
					
					if (allunits.get(feindindex).getXx()<=(xeigen+130))
					{
						Log.d("Kampf","Es wird gekämpft");
						allunits.get(feindindex).setamlaufen(false);
						allunits.get(feindindex).setKaempfen(true);
					}
				}
			}
		}
	}

}



//Diese Funktion ueberprueft, ob eine Einheit keine hp hat. Bei keinen hp kann sie die bisher
//	bestehende Funktion gegner toeten aufrufen.
private void obEinheittotist() {
	for (Einheit eine : allunits) {
		if ( eine.getHp() < 1) {
			if ( eine.isEnemy() ) {
				toetegegner_und_weiterlaufen();
				break;
			}
//			else { toeteeigene_und_weiterlaufen(); break;}
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

//public void toeteeigene_und_weiterlaufen() {
//	
//	runOnUiThread(new Runnable() {
//		
//		@Override
//		public void run() {
//			Einheit eigene = null;
//			int i = 0;
//			while (eigene == null && i < allunits.size()) {
//				if (allunits.get(i).isEnemy() == false)
//					eigene = allunits.get(i);
//
//				i++;
//			}
//			int eigenindex = allunits.indexOf(eigene);
//			
//			Einheit feindl = null;
//			i = 0;
//			while (feindl == null && i < allunits.size()) {
//				if (allunits.get(i).isEnemy() == true)
//					feindl = allunits.get(i);
//
//				i++;
//			}
//			int feindindex = allunits.indexOf(feindl);
//			
//			
//			einheitbilder.get(eigenindex).setImageDrawable(null);
//			einheitbilder.get(feindindex).setImageDrawable(null);
//			
//			einheitbilder.set(feindindex, new ImageView(GameActivity.this));  // erstellung eines neuen ImageViews für jeden Knopfdruck
//			einheitbilder.get(feindindex).setImageResource(R.drawable.anim_stickman_walking_g);
//			@SuppressWarnings("deprecation")
//			AbsoluteLayout rl = (AbsoluteLayout) findViewById(R.id.AbsoluteLayoutGame); //Position des ImageViews
//
//			int x = feindl.getXx();
//			@SuppressWarnings("deprecation")
//			AbsoluteLayout.LayoutParams lp = new AbsoluteLayout.LayoutParams(350, 350, x ,300);
//			rl.addView(einheitbilder.get(feindindex), lp); 
//			allunits.get(feindindex).setKaempfen(false);
//			
//			einheitbilder.remove(eigenindex);
//			allunits.remove(eigenindex);
//			
//			endofarray--;
//			
//			kampfanimtest = false;
//			kampfanimtestg = false;
//			kampftestg = true;
//			
//			eigene = null;
//			for (Einheit aEinheit : allunits) {
//				if (aEinheit.isEnemy()) eigene = aEinheit;
//			}
//			if (eigene == null) kampftest = false;
//			
//			
//			feindl = null;
//			i = 0;
//			while (feindl == null && i < allunits.size()) {
//				if (allunits.get(i).isEnemy() == false)
//					feindl = allunits.get(i);
//
//				i++;
//			}
//			feindindex = allunits.indexOf(feindl);
//			allunits.get(feindindex).setamlaufen(true);
//			gebelaufanim(einheitbilder.get(feindindex));
//			
//		}
//	});
//	
//}

public void toetegegner_und_weiterlaufen() {
	aktuellesguthaben += 100;
	
	
	
	runOnUiThread(new Runnable() {
		
		@Override
		public void run() {
			Einheit eigene = null;
			int i = 0;
			while (eigene == null && i < allunits.size()) {
				if (allunits.get(i).isEnemy() == false)
					eigene = allunits.get(i);

				i++;
			}
			int eigenindex = allunits.indexOf(eigene);
			
			Einheit feindl = null;
			i = 0;
			while (feindl == null && i < allunits.size()) {
				if (allunits.get(i).isEnemy() == true)
					feindl = allunits.get(i);

				i++;
			}
			int feindindex = allunits.indexOf(feindl);
			
			einheitbilder.get(eigenindex).setImageDrawable(null);
			einheitbilder.get(feindindex).setImageDrawable(null);
			
			einheitbilder.set(eigenindex, new ImageView(GameActivity.this));  // erstellung eines neuen ImageViews für jeden Knopfdruck
			einheitbilder.get(eigenindex).setImageResource(R.drawable.anim_stickman_walking);
			@SuppressWarnings("deprecation")
			AbsoluteLayout rl = (AbsoluteLayout) findViewById(R.id.AbsoluteLayoutGame); //Position des ImageViews

			int x = eigene.getXx();
			@SuppressWarnings("deprecation")
			AbsoluteLayout.LayoutParams lp = new AbsoluteLayout.LayoutParams(350, 350, x ,300);
			rl.addView(einheitbilder.get(eigenindex), lp); 
			allunits.get(eigenindex).setKaempfen(false);
			
			einheitbilder.remove(feindindex);
			allunits.remove(feindindex);
			
			endofarray--;

			kampfanimtest = false;
			kampfanimtestg = false;
			kampftest = true;
			
			feindl = null;
			for (Einheit aEinheit : allunits) {
				if (aEinheit.isEnemy()) feindl = aEinheit;
			}
			if (feindl == null) kampftestg = false;
			
			
			eigene = null;
			i = 0;
			while (eigene == null && i < allunits.size()) {
				if (allunits.get(i).isEnemy() == false)
					eigene = allunits.get(i);

				i++;
			}
			eigenindex = allunits.indexOf(eigene);
			allunits.get(eigenindex).setamlaufen(true);
			gebelaufanim(einheitbilder.get(eigenindex));
		}
		
	});
	
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



@SuppressWarnings("deprecation")
public void gamePause(View v) //onClick Funktion, soll das Spiel pausieren.
{
	Log.d("Pause", "Spiel wird pausiert!"); // eig soll das spiel hier pausieren, ich benutze den knopf gerade um gegner zu spawnen, ein gegner funktioniert genau wie unser Stickman, nur dass er nach links läuft, also der x wert kleiner wird
	
	int meinindex = endofarray;
	ImageView gegnerspawn = feindsoldatpic();
	einheitbilder.add(meinindex, gegnerspawn);
	AbsoluteLayout grl = (AbsoluteLayout) findViewById(R.id.AbsoluteLayoutGame);
	AbsoluteLayout.LayoutParams glp = new AbsoluteLayout.LayoutParams(350, 350,900,300);
	grl.addView(einheitbilder.get(meinindex), glp);
	gebelaufanimg(einheitbilder.get(meinindex));
	
	
	allunits.add(new Einheit(true, ARTSOLDAT));
	allunits.get(meinindex).startwalktimer();
	allunits.get(meinindex).setamlaufen(true);


	kampftestg = true;
	endofarray++;

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
        			if (isenemy) xx = 900;
        			enemy = isenemy;
        			einheitart = kategorie;
        			if (einheitart == ARTSOLDAT) {
        				schaden = 5;
        				hp = 100;
        			}
        			if (einheitart == ARTBASE) {
        				schaden = 0;
        				hp = 1000;
        			}
        	}
        	
        	
        	
			public void startwalktimer() {
				new Timer().scheduleAtFixedRate(new TimerAddX(), 0, DELAYTOSPEED);
			}


			public void setamlaufen(boolean einheitistamlaufen) {
				this.laeuft = einheitistamlaufen;
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
            			  else xx++;

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

	