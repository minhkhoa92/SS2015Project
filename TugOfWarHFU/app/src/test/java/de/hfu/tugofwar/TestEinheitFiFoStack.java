package de.hfu.tugofwar;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestEinheitFiFoStack {

	@Test
	public void testAddNew() {
		EinheitFiFoStack enemy = new EinheitFiFoStack(true);
		EinheitFiFoStack my = new EinheitFiFoStack(false);
		enemy.addNewSoldat();
		enemy.addNewSoldat();
		enemy.addNewKrieger();
		assertEquals(enemy.getAnzahl(), 3);
		
		my.addNewSoldat();
		my.addNewSoldat();
		my.addNewKrieger();
		my.addNewKrieger();
		my.addNewKrieger();
		assertEquals(my.getAnzahl(), 5);
		
		//ueberfuellen
		my.addNewKrieger();
		assertEquals(my.getAnzahl(), 5);
		
		

		assertEquals(my.getDelPos(), 0);
		my.deleteFirst();
		assertEquals(my.getAnzahl(), 4);
		assertEquals(my.getDelPos(), 1);
		
		my.addNewKrieger();
		assertEquals(my.getAnzahl(), 5);
	}
	
	@Test
	public void testDelete() {
		EinheitFiFoStack my = new EinheitFiFoStack(false);
		
		//loeschen wenn keine Einheiten da sind
		my.deleteFirst();
		assertEquals(my.getAnzahl(), 0);
		
		my.addNewSoldat();
		my.addNewSoldat();
		my.addNewKrieger();
		my.addNewKrieger();
		my.addNewKrieger();
		assertEquals(my.getAnzahl(), 5);
		
		//loeschen von mehreren Einheiten
		my.deleteFirst();
		my.deleteFirst();
		my.deleteFirst();
		my.deleteFirst();
		assertEquals(my.getAnzahl(), 1);
		assertEquals(my.getDelPos(), 4);
		
		
		my.addNewSoldat();
		my.addNewSoldat();
		my.addNewKrieger();
		assertEquals(my.getAnzahl(), 4);
		
		//loeschen, dass die Position beim inkrementieren ueber 4 die Zahl 0 annimmt
		my.deleteFirst();
		my.deleteFirst();
		assertEquals(my.getAnzahl(), 2);
		assertEquals(my.getDelPos(), 1);
	}

}
