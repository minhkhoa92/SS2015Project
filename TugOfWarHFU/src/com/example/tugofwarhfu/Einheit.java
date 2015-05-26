package com.example.tugofwarhfu;

//import android.util.Log;

public class Einheit {
	private final int ARTSOLDAT = 1;

	public static final int XSTARTMYUNIT = 0;
	public static final int XSTARTENEMY = 1000;
	public int xx;
	
	private boolean laeuft = false;
	private int hp;
	private int schaden = 0;
	

	private int einheitart;
	private boolean enemy;
	
	public Einheit(boolean isenemy, int kategorie){
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

	public void setamlaufen(boolean einheitistamlaufen) {
		this.laeuft = einheitistamlaufen;
//		Log.w("test", "und geht");
	}

	public boolean amLaufen() {
		return laeuft;
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

	public void laufe() {
		if (amLaufen() && hp > 0) {
			if (isEnemy()) xx -= 100 ;
			else if (!isEnemy()) xx += 100;
			}
		}
	
	public void bekommtschaden(int angerichteterschaden){
		hp -= angerichteterschaden;
	}
	
}