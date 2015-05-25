package com.example.tugofwarhfu;

import java.util.LinkedList;

import android.widget.ImageView;

public class EinheitFiFoStack {
	final char ARTSOLDAT = 1;
	
	
	public LinkedList<ImageView> images;
	public Einheit ein1, ein2, ein3, ein4, ein5;
	private char insPos, delPos;
	private boolean isEnemy;
	
	public EinheitFiFoStack(boolean gegner) {
		isEnemy = gegner;
		ein1 = new Einheit(isEnemy, ARTSOLDAT); ein1.bekommtschaden(3000);
		ein2 = new Einheit(isEnemy, ARTSOLDAT); ein2.bekommtschaden(3000);
		ein3 = new Einheit(isEnemy, ARTSOLDAT); ein3.bekommtschaden(3000);
		ein4 = new Einheit(isEnemy, ARTSOLDAT); ein4.bekommtschaden(3000);
		ein5 = new Einheit(isEnemy, ARTSOLDAT); ein5.bekommtschaden(3000);
		images = new LinkedList<ImageView>();
	}
	
// Bilder hinzufuegen befehl: ${stackname}.images.addLast(${ImageViewName});
// Bild loeschen Befehl: ${stackname}.images.removeFirst();
// Bild ersetzen ${stackname}.images.set(${int_index}, ${variablename});
	
	
	/**
	* Anfang der zyklischen Warteschlange
	*/
	
	//erstellt neuen Soldaten, wenn die Soldatenanzahl nicht die maximale Soldatenanzahlgrenze erreicht hat
	protected boolean addnewsoldat(){
		if ( ! (ein1.getHp() > 0 && (int) calcCount() < 1 ) ) { //bei 5 gefuellten items sind inspos und delpos auf der gleichen Stelle x, x - x ergibt count = 0
			switch (insPos) {
			case 0:
				ein1.soldatWerte();
				ein1.setamlaufen(true);
				ein1.startwalktimer();
				break;
			case 1:
				ein2.soldatWerte();
				ein2.setamlaufen(true);
				ein2.startwalktimer();
				break;
			case 2:
				ein3.soldatWerte();
				ein3.setamlaufen(true);
				ein3.startwalktimer();
				break;
			case 3:
				ein4.soldatWerte();
				ein4.setamlaufen(true);
				ein4.startwalktimer();
				break;
			case 4:
				ein5.soldatWerte();
				ein5.setamlaufen(true);
				ein5.startwalktimer();
				break;
			default:
				break;
			}
			insPos++;
			insPos = (char) (insPos % 5);
			return true;
		} else {return false;}
	}
	
	protected boolean deleteFirst() {
		if ( ( ( ein1.getHp() > 0 && (int) calcCount() == 0 )  || (int) calcCount() > 0) ) { //bei 5 gefuellten items sind inspos und delpos auf der gleichen Stelle x, x - x ergibt count = 0
			switch (delPos) {
			case 0:
				ein1.setamlaufen(false);
				ein1.resetX();
				break;
			case 1:
				ein2.setamlaufen(false);
				ein2.resetX();
				break;
			case 2:
				ein3.setamlaufen(false);
				ein3.resetX();
				break;
			case 3:
				ein4.setamlaufen(false);
				ein4.resetX();
				break;
			case 4:
				ein5.setamlaufen(false);
				ein5.resetX();
				break;
			default:
				break;
			}
			delPos++;
			delPos = (char) (delPos % 5);
			return true;
		} else {return false;}
	}
	
	private char calcCount(){
		char result = 7;
		if (insPos >= delPos) {// insPos weiter vorne oder gleich delPos
			result = (char) (insPos - delPos);
		}
		else if (delPos > insPos) { //insert Position ist vor delete Position
			result = (char) (5 + insPos - delPos);
		}
		return result;
	}
	
	public int getAnzahl(){
		int result = calcCount();
		if (result == 0 && ein1.getHp() > 0) {
			result = 5;
		}
		return result;
	}

	public String getAnzString() {
		if ( (int) calcCount() == 0 && ein1.getHp() < 1) {
			return Integer.toString(0);
		}
		else if ( (int) calcCount() == 0 && ein1.getHp() > 0) {
			return Integer.toString(5);
		}
		else { return Integer.toString(calcCount()); }
	}
	
	/**
	* Ende der zyklischen Warteschlange
	*/
	
	


	public int firstx () {
		return getEin1().getXx();
	}
	
	public int firstdmg () {
		return getEin1().getSchaden();
	}
	
	public int firstKriegtSchadenAufSich(int i) {
		ein1.bekommtschaden(i);
		return i;
	}
	
	public void firstLaufenZuKaempfen() {
		ein1.setamlaufen(false);
		ein1.setKaempfen(true);
	}
	
	public void firstKaempfenZuLaufen() {
		ein1.setamlaufen(true);
		ein1.setKaempfen(false);
	}
	
	public boolean firstSchauObTot(){
		if (ein1.getHp() < 1){
			return true;
		} else return false;
	}
	
	public Einheit getEin1() {
		return ein1;
	}

	public void setEin1(Einheit ein1) {
		this.ein1 = ein1;
	}

	public Einheit getEin2() {
		return ein2;
	}

	public void setEin2(Einheit ein2) {
		this.ein2 = ein2;
	}

	public Einheit getEin3() {
		return ein3;
	}

	public void setEin3(Einheit ein3) {
		this.ein3 = ein3;
	}

	public Einheit getEin4() {
		return ein4;
	}

	public void setEin4(Einheit ein4) {
		this.ein4 = ein4;
	}

	public Einheit getEin5() {
		return ein5;
	}

	public void setEin5(Einheit ein5) {
		this.ein5 = ein5;
	}

}
