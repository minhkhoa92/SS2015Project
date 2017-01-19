package de.hfu.tugofwarhfu;

public class Einheit {
	private final int ARTSOLDAT = GameActivity.ARTSOLDAT;
	private final int ARTKRIEGER = GameActivity.ARTKRIEGER;

	public static final int XSTARTMYUNIT = 0; // eig -10 wie hier geschrieben, zur Neusetzung der imageviews ist die 10 Differenz besser
	public static final int XSTARTENEMY = 1000;
	public int xx;
	
	private boolean laeuft = false;
	private int hp;
	private int schaden = 0;
	

	private int einheitart;
	private boolean enemy;
	
	public Einheit(boolean isenemy, int kategorie){
		xx = -10;
		if (isenemy) xx = XSTARTENEMY;
		enemy = isenemy;
		einheitart = kategorie;
		if (einheitart == ARTSOLDAT) {
			schaden = 5;
			hp = 100;
		}
	}

	public void einheitWerte (int kategorie){
		einheitart = kategorie;
		schaden = 5;
		hp = 100;
		if (einheitart == ARTKRIEGER) {
			schaden = 4;
			hp = 500;
		}
	}

	public void resetX() {
		xx = XSTARTMYUNIT;
		if (enemy) xx = XSTARTENEMY;
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

	public int getEinheitart() {
		return einheitart;
	}

	public void laufe() {
		if (amLaufen() && hp > 0) {
			if (isEnemy()) xx -= 14 ;
			else if (!isEnemy()) xx += 14;
			}
		}
	
	public void bekommtschaden(int angerichteterschaden){
		hp -= angerichteterschaden;
	}
	
}