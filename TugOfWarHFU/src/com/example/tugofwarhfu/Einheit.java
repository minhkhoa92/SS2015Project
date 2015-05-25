package com.example.tugofwarhfu;

import java.util.Timer;
import java.util.TimerTask;

//import android.util.Log;

public class Einheit {
	private final char ARTSOLDAT = 1;
	private static final int DELAYTOSPEED = 1000;
	public static final int XSTARTMYUNIT = 0;
	public static final int XSTARTENEMY = 1000;
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
	}

	public void soldatWerte (){
		schaden = 5;
		hp = 100;
		einheitart = ARTSOLDAT;
	}

	public void resetX() {
		xx = 0;
		if (enemy) xx = 1000;
	}
	
	public void startwalktimer() {
		new Timer().scheduleAtFixedRate(new TimerAddX(), 0, DELAYTOSPEED);
	}


	public void setamlaufen(boolean einheitistamlaufen) {
		this.laeuft = einheitistamlaufen;
//		Log.w("test", "und geht");
	}

	public boolean isamlaufen() {
		return laeuft;
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
			  if (isEnemy()) xx -= 100 ;
			  if (!isEnemy()) xx += 100;
		  	}
		}
	}
}