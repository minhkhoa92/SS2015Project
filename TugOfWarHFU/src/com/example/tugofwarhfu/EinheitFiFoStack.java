package com.example.tugofwarhfu;

import java.util.LinkedList;

import android.widget.ImageView;

public class EinheitFiFoStack {
	final char ARTSOLDAT = 1;
	
	
	public LinkedList<ImageView> images;
	public Einheit ein1, ein2, ein3, ein4, ein5;
	private char insPos, delPos, workPos;
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
	
	
	protected Einheit getFirstData(){
		switch (delPos) {
		case 0:
			return ein1;
		case 1:
			return ein2;
		case 2:
			return ein3;
		case 3:
			return ein4;
		case 4: 
			return ein5;
		default: // nur fuer den compiler error
			return ein1;
		}
	}

	public int firstx () {
		return getFirstData().getXx();
	}
	
	public int firstdmg () {
		return getFirstData().getSchaden();
	}
	
	public void firstBekommtSchaden(int i) {
		switch (delPos) {
		case 0:
			ein1.bekommtschaden(i);
			break;
		case 1:
			ein2.bekommtschaden(i);
			break;
		case 2:
			ein3.bekommtschaden(i);
			break;
		case 3:
			ein4.bekommtschaden(i);
			break;
		case 4: 
			ein5.bekommtschaden(i);
			break;
		}
	}
	
	
	
	public void aendernZuKaempfenStart(char c) { // TODO kaempfen einbringen
		switch (c) {
		case 0:
			ein1.setamlaufen(false);
//			if (workPos == 0) ein1.setKaempfen(true);
			break;
		case 1:
			ein2.setamlaufen(false);
//			if (workPos == 0) ein2.setKaempfen(true);
			break;
		case 2:
			ein3.setamlaufen(false);
//			if (workPos == 0) ein3.setKaempfen(true);
			break;
		case 3:
			ein4.setamlaufen(false);
//			if (workPos == 0) ein4.setKaempfen(true);
			break;
		case 4: 
			ein5.setamlaufen(false);
//			if (workPos == 0) ein5.setKaempfen(true);
			break;
		}
		if (getAnzahl() >= workPos) {
			char oldPos = workPos;
			workPos ++;
			char newPos = (char) ((delPos + workPos) % 5);
			if ( total( getDataFromPos(oldPos).getXx() - getDataFromPos(newPos).getXx() ) <= 275 )
				aendernZuKaempfenStart(newPos);
		}
	}
	
	private int total(int i) {
		if (i < 0 ) i*= -1;
		return i;
	}
	
	private Einheit getDataFromPos(char c) {
		switch (c) {
		case 0:
			return ein1;
		case 1:
			return ein2;
		case 2:
			return ein3;
		case 3:
			return ein4;
		case 4: 
			return ein5;
		default: // nur fuer den compiler error
			return ein1;
		}
	}
	
	public void aendernZuLaufenStart(char c) { // TODO kaempfen einbringen
		switch (c) {
		case 0:
			ein1.setamlaufen(true);
//			ein1.setKaempfen(false);
			break;
		case 1:
			ein2.setamlaufen(true);
//			ein2.setKaempfen(false);
			break;
		case 2:
			ein3.setamlaufen(true);
//			ein3.setKaempfen(false);
			break;
		case 3:
			ein4.setamlaufen(true);
//			ein4.setKaempfen(false);
			break;
		case 4: 
			ein5.setamlaufen(true);
//			ein5.setKaempfen(false);
			break;
		}
		if (getAnzahl() >= workPos) {
			char oldPos = workPos;
			workPos ++;
			char newPos = (char) ((delPos + workPos) % 5);
			if ( total( getDataFromPos(oldPos).getXx() - getDataFromPos(newPos).getXx() ) <= 275 )
				aendernZuKaempfenStart(newPos);
		}
	}
	
	public boolean firstSchauObTot(){
		if (getFirstData().getHp() < 1){
			return true;
		} else return false;
	}

	public char getWorkpos() {
		return workPos;
	}

	public void setWorkpos(char workpos) {
		this.workPos = workpos;
	}

	public char getDelPos() {
		return delPos;
	}

}
