package com.example.tugofwarhfu;


import java.util.ArrayList;
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
	private final char ARTSOLDAT = 1;
	
	// Variablen Minh
	ArrayList<Einheit> allunits;
	ArrayList<ImageView> einheitbilder;
	int endofarray = 0;
	
	ImageView s_fight_animation;
	ImageView s_walk_animation_g;
	TimerAddXG tasknewg;
	ImageView kampf_animation;
	ImageView kampf_animation_g;
	
	public boolean timerstartboolg = false;

	private boolean kampftestg = false;
	private boolean hitboxtest = true;
	private boolean hitboxtestg = true;
	private boolean soebenKollision = false;
	private boolean threadzurueckg = false;
	private boolean kampfanimtest = false;
	private boolean kampfanimtestg = false;
	

	//Variablen von ALex
	

ImageButton einheit;
TextView sekunden_anzeige;
TextView goldanzeige;
int zaehler=100;
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
			    goldanzeige=(TextView) findViewById(R.id.gold_anzeige);
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
											zaehler=zaehler+1;
											goldanzeige.setText(String.valueOf("Gold "+zaehler));
											
											
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
		testhitundkampf();
	}
	
	
	private boolean kampftest = false;
    
	ImageView s_walk_animation;
	
	
	private void eigenersoldatpic() {
		ImageView neuerSoldat = new ImageView(GameActivity.this);  // erstellung eines neuen ImageViews für jeden Knopfdruck
		neuerSoldat.setImageResource(R.drawable.anim_stickman_walking);
		einheitbilder.add(endofarray, neuerSoldat);
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
	
	if(zaehler>=20)
		 {
			zaehler=zaehler-20;	//zaehler ist der Goldwert, der Soldat kostet gerade 20 Gold
			Einheit eigeneeinheit = new Einheit(false, ARTSOLDAT);
			int meinindex = endofarray;
			allunits.add(meinindex, eigeneeinheit);
			eigenersoldatpic();
		
			AbsoluteLayout rl = (AbsoluteLayout) findViewById(R.id.AbsoluteLayoutGame); //Position des ImageViews
			AbsoluteLayout.LayoutParams lp = new AbsoluteLayout.LayoutParams(350, 350,0,300);
			rl.addView(einheitbilder.get(meinindex), lp); 
			gebelaufanim(einheitbilder.get(meinindex));
			
			
			allunits.get(meinindex).setEinheitistamlaufen(true); //Bedinung damit der Zähler zählt
			
			//Startet den Zähler für die X Berechnung
			allunits.get(meinindex).startwalktimer();
			// dieser Block berechnet die X Koordinate unseres Stickmans
			kampftest= true; //teilt mit, dass die Einheit bereit zum kämpfen ist
			endofarray++;
	}
}

private void testhitundkampf() { // hitboxerkennung und Kampferkennung timerstart
	TimerTask hitboxtask = new TimerTask() { //hitboxerkennung
		
		@Override
		public void run() {
			if (kampftest==true && hitboxtest==true && kampftestg == true) //wenn eine eigene Einheit gespawnt ist und eine Gegner Einheit
			{
				Log.d("Hitboxen","Hitboxen werden aufgerufen");
				Einheit aneinheit = null;
				int i = 0;
				while (aneinheit == null && i < allunits.size()) {
					if (!allunits.get(i).isEnemy());
						aneinheit = allunits.get(i);

					i++;
				}
				if(aneinheit != null) {
					if(aneinheit.getXx() >= (tasknewg.xxg-130)) // HITBOXEN! Einheit-X-Wert und Gegner-X-Wert
					{
						Log.d("Kampf","Es wird gekämpft"); 
						hitboxtest=false; //hört auf weiter zu checken
						soebenKollision = true; //gibt zurueck, dass die hitbox mit etwas kollidiert
						aneinheit.setEinheitistamlaufen(false);
					}
					else if(kampftest==true && hitboxtest==true && kampftestg != true) //wenn nur eine eigene Einheit gespawnt ist
					{
						if(aneinheit.getXx()>=820) //HITBOXEN! Einehit-X-Wert und vorläufiger X wert der basis
						{
							Log.d("Kampf","Einheit läuft gegen die basis"); 
							hitboxtest=false;//hört auf weiter zu checken
							soebenKollision = true; //gibt zurueck, dass die hitbox mit etwas kollidiert
							aneinheit.setEinheitistamlaufen(false);
						}
					}
				}
			}
				
			
		}
	};
	Timer thitb = new Timer();
	thitb.schedule(hitboxtask,0,100); 
	
	/*
	TimerTask hitboxtaskg = new TimerTask() { //hitboxerkennung
		
		@Override
		public void run() {
			if (kampftestg == true && hitboxtestg==true && kampftest == true)
			{
				Log.d("Hitboxen","Hitboxen Gegern werden aufgerufen");
				boolean istschondarueber = false;
				int xeigen = 0;
				for (Einheit eineeinheit : allunits) {
					if (!eineeinheit.isEnemy() && !istschondarueber) {
						xeigen = eineeinheit.getXx();
						istschondarueber = true;
					}
				}
				
				if(tasknewg.xxg<=(xeigen+130))
				{
					Log.d("Kampf","Es wird gekämpft");
					hitboxtestg=false;
					threadzurueckg = true;
					
					
				}
				
			}
			else if(kampftestg==true && hitboxtestg==true && kampftest != true) //wenn nur eine gegnerische  Einheit gespawnt ist
			{
				if(tasknewg.xxg<=60) //HITBOXEN! Einehit-X-Wert und vorläufiger X wert der basis
				{
					Log.d("Kampf","GegnerEinheit läuft gegen die basis"); 
					hitboxtestg=false;//hört auf weiter zu checken
					threadzurueckg = true; //gibt zurueck, dass die hitbox mit etwas kollidiert
					timerstartboolg=false;
				}
			}
		}
	};
	Timer thitbg = new Timer();
	thitbg.schedule(hitboxtaskg,0, 100);
	*/
	
	TimerTask timetask = new TimerTask() { //falls hitboxenerkennung dann kampfanimation
		
		public void run() { //hier wird die Kampfanimation abgespielt
			if(soebenKollision==true && kampfanimtest ==false)
			{
				
				runOnUiThread(new Runnable() { //über runOnUiThread() kann man auf die Imageviews aus dem Mainthread zu greifen
					@Override
					public void run(){
				
				
				s_walk_animation.setVisibility(View.GONE); //hier entferne ich die alte animation
				s_walk_animation.setImageDrawable(null); 
				
				
				kampf_animation = new ImageView(GameActivity.this); // und erstelle hier die Kampfanimation
				kampf_animation.setImageResource(R.drawable.anim_stickman_kampf);
				@SuppressWarnings("deprecation")
				AbsoluteLayout al = (AbsoluteLayout) findViewById(R.id.AbsoluteLayoutGame);
				int x = 0;
				boolean warschonerste = true;
				for (Einheit eineEinheit : allunits) {
					if (!eineEinheit.isEnemy() && warschonerste)
						{ x = eineEinheit.getXx();
						warschonerste = false;
						}
				}// hier wird die x position des stickmans übergeben und dementsprechen findet die Kampfanimation an dieser Stelle statt!
				
				@SuppressWarnings("deprecation")
				AbsoluteLayout.LayoutParams lp2 = new AbsoluteLayout.LayoutParams(
						s_walk_animation.getHeight(), 
						+s_walk_animation.getWidth(),
						x,
						+s_walk_animation.getTop()); //in welcher Hoehe neu ein ImageView gespawnt wird
						
				al.addView(kampf_animation, lp2);  
				
				}
			});
				
			kampfanimtest=true; //hier wird mitgeteilt, dass die Einheit aktuell kämpft ( wenn also die Einheit nachher einen Gegner tötet, wird diese Variable wieder false
			}		
		
		};
	};
	Timer t1 = new Timer();
	t1.schedule(timetask, 0, 100); //es wird alle zehntel sekunde gecheckt, ob die hitbox etwas getroffen hat
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



    
private TranslateAnimation stickman_walk_Animation_gegner(){ //Bewegung des gegnerischen Stickmans

		
	    TranslateAnimation stickman_walking_animation_gegner = new TranslateAnimation(0.0f, -1000.0f,0.0f, 0.0f);
        stickman_walking_animation_gegner.setInterpolator(new LinearInterpolator());
        stickman_walking_animation_gegner.setDuration(10000);
        stickman_walking_animation_gegner.setRepeatCount(0);
        stickman_walking_animation_gegner.setRepeatMode(0);
        stickman_walking_animation_gegner.setFillAfter(false); 
        return stickman_walking_animation_gegner;
        };   
        
      
@SuppressWarnings("deprecation")
public void SpawnKrieger(View v) //onClick Funktion, spawnt Krieger -> Dieser Knopf lässt gerade den gegnerischen Stickman sterben und lässt unseren an der Stelle weiterlaufen. wo er zuletzt gekämpft hat
{
	Log.d("kek","methode wird aufgerufen");

	kampf_animation.setImageDrawable(null);
	kampf_animation_g.setImageDrawable(null);
	
	ImageView testnu = new ImageView(GameActivity.this);  // erstellung eines neuen ImageViews für jeden Knopfdruck
	testnu.setImageResource(R.drawable.anim_stickman_walking);
	AbsoluteLayout rl = (AbsoluteLayout) findViewById(R.id.AbsoluteLayoutGame); //Position des ImageViews
	for (Einheit eineEinh : allunits) {
		eineEinh.setEinheitistamlaufen(true);
		int x= eineEinh.getXx();
		AbsoluteLayout.LayoutParams lp = new AbsoluteLayout.LayoutParams(350, 350,x,300);
		rl.addView(testnu, lp); 
	}
	
	
	
	s_walk_animation = (ImageView) testnu;
	s_walk_animation.setVisibility(View.VISIBLE);
	s_walk_animation.startAnimation(stickman_walk_Animation());
	
	
	
	

	
	
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
	
	ImageView gegnerspawn = new ImageView(GameActivity.this);
	gegnerspawn.setImageResource(R.drawable.anim_stickman_walking_g);
	AbsoluteLayout grl = (AbsoluteLayout) findViewById(R.id.AbsoluteLayoutGame);
	AbsoluteLayout.LayoutParams glp = new AbsoluteLayout.LayoutParams(350, 350,900,300);
	grl.addView(gegnerspawn, glp); 
	
	s_walk_animation_g = (ImageView) gegnerspawn;
	s_walk_animation_g.setVisibility(View.VISIBLE);
	s_walk_animation_g.startAnimation(stickman_walk_Animation_gegner());
	
	timerstartboolg=true;
	tasknewg = new TimerAddXG();
	new Timer().scheduleAtFixedRate(tasknewg,0,10);	
	kampftestg = true;
	
	hitboxen_gegner hitbox1 = new hitboxen_gegner(); 
	hitbox1.start();
	
TimerTask timetaskg = new TimerTask() { //falls hitboxenerkennung  kampfanimation
		
		public void run() { //hier soll die Kampfanimation abgespielt werden			 
			if(threadzurueckg==true && kampfanimtestg ==false)
			{
			
				
				runOnUiThread(new Runnable() {
					@Override
					public void run(){
				
				
				s_walk_animation_g.setVisibility(View.GONE);
				s_walk_animation_g.setImageDrawable(null); 
				
				
				kampf_animation_g = new ImageView(GameActivity.this);
				kampf_animation_g.setImageResource(R.drawable.anim_stickman_kampf_g);
				AbsoluteLayout al = (AbsoluteLayout) findViewById(R.id.AbsoluteLayoutGame);

				int x = tasknewg.getXxg(); // hier wird die x position des stickmans übergeben und dementsprechen findet die Kampfanimation an dieser Stelle statt!

				
				AbsoluteLayout.LayoutParams lp2 = new AbsoluteLayout.LayoutParams(
						s_walk_animation_g.getHeight(), 
						s_walk_animation_g.getWidth(),
						x,
						s_walk_animation_g.getTop());
						
				al.addView(kampf_animation_g, lp2);  
				
				}
			});
				
			kampfanimtestg=true;
			}		
		
	};
	};
	Timer tg = new Timer();
	tg.schedule(timetaskg, 0, 100); 
}


public class hitboxen_gegner extends Thread {  // der Thread der für die hitbox berechnung einer Gegnereinheit zuständig ist 
	public void run(){
		Log.d("thread gestarten","...");
		
		
		TimerTask hitboxtaskg = new TimerTask() { //hitboxerkennung
			
			@Override
			public void run() {
				if (kampftestg == true && hitboxtestg==true && kampftest == true)
				{
					Log.d("Hitboxen","Hitboxen Gegern werden aufgerufen");
					boolean istschondarueber = false;
					int xeigen = 0;
					for (Einheit eineeinheit : allunits) {
						if (!eineeinheit.isEnemy() && !istschondarueber) {
							xeigen = eineeinheit.getXx();
							istschondarueber = true;
						}
					}
					
					if(tasknewg.xxg<=(xeigen+130))
					{
						Log.d("Kampf","Es wird gekämpft");
						hitboxtestg=false;
						threadzurueckg = true;
						
						
					}
					
				}
				else if(kampftestg==true && hitboxtestg==true && kampftest != true) //wenn nur eine gegnerische  Einheit gespawnt ist
				{
					if(tasknewg.xxg<=60) //HITBOXEN! Einehit-X-Wert und vorläufiger X wert der basis
					{
						Log.d("Kampf","GegnerEinheit läuft gegen die basis"); 
						hitboxtestg=false;//hört auf weiter zu checken
						threadzurueckg = true; //gibt zurueck, dass die hitbox mit etwas kollidiert
						timerstartboolg=false;
					}
				}
			}
		};
		Timer thitbg = new Timer();
		thitbg.schedule(hitboxtaskg,0, 100);

	
	}
	
	
		
}


        
        
        class TimerAddXG extends TimerTask { //gibt den X wert des gegnerischen Stickmans an
        	public int xxg = 900;
        	public int getXxg() {
        		return xxg;
        	}
        	public void setXxg(int xxg) {
        		this.xxg = xxg;
        	} 
        	public void run() {
        		  if(timerstartboolg==true){
        		  xxg = xxg-1;
        		 // Log.d("xg","xg wurde verkleinert");
        		  
        		  }
        	  }
        
        }


        
        private class Einheit {
        	private static final int DELAYTOSPEED = 10;
        	public int xx;
        	
        	private boolean einheitistamlaufen = false;
        	boolean hitboxtest = true;

        	private char einheitart;
        	private boolean angehauen = false;
        	private boolean enemy;


        	public Einheit(boolean isenemy, char kategorie){
        			xx = 0;
        			enemy = isenemy;
        			einheitart = kategorie;
        	}

			public void startwalktimer() {
				new Timer().scheduleAtFixedRate(new TimerAddX(), 0, DELAYTOSPEED);
			}


			public void setEinheitistamlaufen(boolean einheitistamlaufen) {
				this.einheitistamlaufen = einheitistamlaufen;
			}
			
			public int getXx() {
        		return xx;
        	}
        	
			public boolean isEnemy() {
				return enemy;
			}

			class TimerAddX extends TimerTask { // gibt den X wert des eigenen Stickmans an
            	
            	
            	public void run() {
            		  if(einheitistamlaufen==true){
            		  xx = xx+1;
            		//  Log.d("x","x wurde erhöht");
            		  
            		  }
            	  }
            	}

        }


}








	
	