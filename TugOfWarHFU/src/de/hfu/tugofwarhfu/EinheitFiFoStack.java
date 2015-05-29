package de.hfu.tugofwarhfu;

import java.util.LinkedList;

public class EinheitFiFoStack {
	
	final int ARTSOLDAT = GameActivity.ARTSOLDAT;
	private boolean teamAmKaempfen;
	public LinkedList<Integer> images;
	public Einheit ein1, ein2, ein3, ein4, ein5;
	private int insPos, delPos, workPos;
	private boolean isEnemy;
	
	public EinheitFiFoStack(boolean gegner) {
		isEnemy = gegner;
		ein1 = new Einheit(isEnemy, ARTSOLDAT); ein1.bekommtschaden(3000);
		ein2 = new Einheit(isEnemy, ARTSOLDAT); ein2.bekommtschaden(3000);
		ein3 = new Einheit(isEnemy, ARTSOLDAT); ein3.bekommtschaden(3000);
		ein4 = new Einheit(isEnemy, ARTSOLDAT); ein4.bekommtschaden(3000);
		ein5 = new Einheit(isEnemy, ARTSOLDAT); ein5.bekommtschaden(3000);
		images = new LinkedList<Integer>();
		insPos = 0; delPos = 0;
		teamAmKaempfen = false;
	}
	
// Bilder hinzufuegen befehl: ${stackname}.images.addLast(${ImageViewName});
// Bild loeschen Befehl: ${stackname}.images.removeFirst();
// Bild ersetzen ${stackname}.images.set(${int_index}, ${variablename});
	
	
	/**
	* Anfang der zyklischen Warteschlange
	*/
	
	//erstellt neuen Soldaten, wenn die Soldatenanzahl nicht die maximale Soldatenanzahlgrenze erreicht hat
	protected boolean addNewSoldat(){
		if ( ! (ein1.getHp() > 0 && (int) calcCount() < 1 ) ) { //bei 5 gefuellten items sind inspos und delpos auf der gleichen Stelle x, x - x ergibt count = 0
			switch (insPos) {
			case 0:
				ein1.einheitWerte(GameActivity.ARTSOLDAT);
				ein1.setamlaufen(true);
				break;
			case 1:
				ein2.einheitWerte(GameActivity.ARTSOLDAT);
				ein2.setamlaufen(true);
				break;
			case 2:
				ein3.einheitWerte(GameActivity.ARTSOLDAT);
				ein3.setamlaufen(true);
				break;
			case 3:
				ein4.einheitWerte(GameActivity.ARTSOLDAT);
				ein4.setamlaufen(true);
				break;
			case 4:
				ein5.einheitWerte(GameActivity.ARTSOLDAT);
				ein5.setamlaufen(true);
				break;
			default:
				break;
			}
			insPos++;
			insPos = (int) (insPos % 5);
			return true;
		} else {return false;}
	}
	
	protected boolean addNewKrieger(){
		if ( ! (ein1.getHp() > 0 && (int) calcCount() < 1 ) ) { //bei 5 gefuellten items sind inspos und delpos auf der gleichen Stelle x, x - x ergibt count = 0
			switch (insPos) {
			case 0:
				ein1.einheitWerte(GameActivity.ARTKRIEGER);
				ein1.setamlaufen(true);
				break;
			case 1:
				ein2.einheitWerte(GameActivity.ARTKRIEGER);
				ein2.setamlaufen(true);
				break;
			case 2:
				ein3.einheitWerte(GameActivity.ARTKRIEGER);
				ein3.setamlaufen(true);
				break;
			case 3:
				ein4.einheitWerte(GameActivity.ARTKRIEGER);
				ein4.setamlaufen(true);
				break;
			case 4:
				ein5.einheitWerte(GameActivity.ARTKRIEGER);
				ein5.setamlaufen(true);
				break;
			default:
				break;
			}
			insPos++;
			insPos = (int) (insPos % 5);
			return true;
		} else {return false;}
	}
	
	protected void deleteFirst() {
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
		delPos = (int) (delPos % 5);
	}
	
	private int calcCount(){
		int result = 7;
		if (insPos >= delPos) {// insPos weiter vorne oder gleich delPos
			result = (int) (insPos - delPos);
		}
		else if (delPos > insPos) { //insert Position ist vor delete Position
			result = (int) (5 + insPos - delPos);
		}
		return result;
	}
	
	public int getAnzahl(){
		int result = calcCount();
		if ( (result == 7 || result == 0) && ein1.getHp() > 0) {
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
	
	/*
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

	public int firstX () {
		return getFirstData().getXx();
	}
	
	public int firstDmg () {
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
	
	public void teamEinSchrittVor(){
		if ( !teamAmKaempfen ) {
			ein1.laufe(); ein2.laufe(); ein3.laufe(); ein4.laufe(); ein5.laufe();
		} else if ( teamAmKaempfen ) {
			if ( ein1.amLaufen() ) ein1.laufe();
			if ( ein2.amLaufen() ) ein2.laufe();
			if ( ein3.amLaufen() ) ein3.laufe();
			if ( ein4.amLaufen() ) ein4.laufe();
			if ( ein5.amLaufen() ) ein5.laufe();
		}
		
	}
	
	public void aendernZuKaempfen(int c) {
		teamAmKaempfen = true;
		aendernZuHalten(c);
	}
	
	public void aendernZuHalten(int c) {
		switch (c) {
		case 0:
			ein1.setamlaufen(false);
			break;
		case 1:
			ein2.setamlaufen(false);
			break;
		case 2:
			ein3.setamlaufen(false);
			break;
		case 3:
			ein4.setamlaufen(false);
			break;
		case 4: 
			ein5.setamlaufen(false);
			break;
		}
	}
	
	protected Einheit getDataFromPos(int c) {
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
	
	public void aendernZuLaufen(int c) {
		teamAmKaempfen = false;
		switch (c) {
		case 0:
			ein1.setamlaufen(true);
			break;
		case 1:
			ein2.setamlaufen(true);
			
			break;
		case 2:
			ein3.setamlaufen(true);
			break;
		case 3:
			ein4.setamlaufen(true);
			break;
		case 4: 
			ein5.setamlaufen(true);
			break;
		}
	}
	
	public boolean firstSchauObTot(){
		if (getFirstData().getHp() < 1){
			return true;
		} else return false;
	}

	public int getWorkpos() {
		return workPos;
	}

	public void setWorkpos(int workpos) {
		this.workPos = workpos;
	}

	public boolean isTeamAmKaempfen() {
		return teamAmKaempfen;
	}

	public void setTeamAmKaempfen(boolean teamAmKaempfen) {
		this.teamAmKaempfen = teamAmKaempfen;
	}

	public int getDelPos() {
		return delPos;
	}

}
