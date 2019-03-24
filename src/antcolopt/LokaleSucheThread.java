package antcolopt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LokaleSucheThread extends Thread {

	private boolean bessereLoesungGefunden = false;
	private int anzahlJobs;
	private Loesung loesung;
	List<Integer> list = new ArrayList<Integer>();
	List<Integer> list2 = new ArrayList<Integer>();
	private int anzahlMaschinen;
	private int[][] ausfuehrungszeiten;
	private Loesung eliteLoesung;
	private int iterationsanzahl;
	private double[][] pheromonmatrix;
	

	public LokaleSucheThread(int anzahlJobs, int anzahlMaschinen, int[][] ausfuehrungszeiten, Loesung eliteLoesung, int iterationsanzahl, double[][] pheromonmatrix) {
		this.anzahlJobs = anzahlJobs;
		this.loesung = loesung;
		this.anzahlMaschinen = anzahlMaschinen;
		this.ausfuehrungszeiten = ausfuehrungszeiten;
		this.eliteLoesung = eliteLoesung;
		this.iterationsanzahl = iterationsanzahl;
		this.pheromonmatrix = pheromonmatrix;
		 list = new ArrayList<Integer>();
		list2 = new ArrayList<Integer>();
		for (int k = 0; k < anzahlJobs; k++) {
			list.add(k);
			list2.add(k);
		}

	}

	public void run() {
		
		Ameise ameise = new Ameise(eliteLoesung, anzahlJobs, anzahlMaschinen);
		loesung = new Loesung(iterationsanzahl, anzahlJobs, anzahlMaschinen, ausfuehrungszeiten);
	
		for (int i = 0; i < anzahlJobs; i++) {
			ameise.bestimmteNaechstenJob(pheromonmatrix);
		}
		loesung.jobreihenfolge = ameise.getBesuchteKnoten();
	
		for (int aktuellerSchritt = 0; aktuellerSchritt < KonstantenUndHelper.anzahlLokaleSuche; aktuellerSchritt++) {
			if (aktuellerSchritt % anzahlJobs == 0) {
			
				if ((bessereLoesungGefunden == false) && (aktuellerSchritt > 0)) {
				//	System.out.println(this.getName() + ">Loesung: " + loesung.berechneTFT());
					return;
				}
				java.util.Collections.shuffle(list);
				java.util.Collections.shuffle(list2);
				bessereLoesungGefunden = false;
			}

			loesung = lokaleSucheInsertion(loesung, list.get(aktuellerSchritt % anzahlJobs));
			loesung = swapSearch(loesung, list2.get(aktuellerSchritt % anzahlJobs));

		}
	//	System.out.println(this.getName() + ">Loesung: " + loesung.berechneTFT());
	}
	

	/**
	 * Wendet die Lokale Suche mittel Insertion Search an
	 * 
	 * @param loesung Loesung, die verbessert werden soll
	 * @param index   Index, des Jobs der verschoben werden soll
	 * @return neue beste gefundene Loesung
	 */
	private Loesung lokaleSucheInsertion(Loesung loesung, int index) {
		int besterTft = loesung.berechneTFT();
		int tftTemp = 0;
		Loesung besteLoesung = loesung;

		Loesung tempLoesung = new Loesung(loesung.getAlter(), anzahlJobs, anzahlMaschinen, ausfuehrungszeiten);

		for (int i = 0; i < loesung.getJobreihenfolge().length; i++) {

			tempLoesung = insertJob(loesung, index, i);
			tftTemp = tempLoesung.berechneTFT();
			if (tftTemp < besterTft) {
				bessereLoesungGefunden = true;
				besteLoesung = tempLoesung;
				besterTft = tftTemp;

			}

		}
		return besteLoesung;
	}

	/*
	 * public int findeIndexJob(Loesung loesung, int job) { for (int i = 0; i <
	 * anzahlJobs; i++) { if (loesung.jobreihenfolge[i] == job) { return i; } }
	 * //return 999; }
	 */

	/**
	 * Wendet lokale Suche mittels Swap Search an
	 * 
	 * @param loesung Loesung, die verbessert werden soll
	 * @param index   Index des Jobs, der vertauscht werden soll
	 * @return neue beste gefundene Loesung
	 */
	private Loesung swapSearch(Loesung loesung, int index) {

		int besterTft = loesung.berechneTFT();
		int tftTemp = 0;
		Loesung besteLoesung = loesung;

		Loesung tempLoesung = new Loesung(loesung.getAlter(), anzahlJobs, anzahlMaschinen, ausfuehrungszeiten);

		for (int i = 0; i < loesung.getJobreihenfolge().length; i++) {

			tempLoesung = swapJob(loesung, index, i);
			tftTemp = tempLoesung.berechneTFT();
			if (tftTemp < besterTft) {
				bessereLoesungGefunden = true;
				besteLoesung = tempLoesung;
				besterTft = tftTemp;

			}

		}

		return besteLoesung;
	}

	/**
	 * Nimmt einen Job der LOesung und setzt ihn an eine andere Stelle
	 * 
	 * @param loesung        Loesung, die veraendert werden soll
	 * @param jobPosition    Index des Jobs, der wo anders eingesetzt werden soll
	 * @param insertPosition Index der Position, an der der Job eingesetzt werden
	 *                       soll
	 * @return neue modifizierte Loesung
	 */
	private Loesung insertJob(Loesung loesung, int jobPosition, int insertPosition) {
		Loesung loesung2 = new Loesung(loesung.getAlter(), anzahlJobs, anzahlMaschinen, ausfuehrungszeiten);
		loesung2.jobreihenfolge = Arrays.copyOf(loesung.getJobreihenfolge(), loesung.getJobreihenfolge().length);
		int job = loesung2.getJobreihenfolge()[jobPosition];
		if (jobPosition < insertPosition) {
			for (int i = jobPosition; i < insertPosition; i++) {
				loesung2.getJobreihenfolge()[i] = loesung2.getJobreihenfolge()[i + 1];
			}
		}
		if (jobPosition > insertPosition) {
			for (int i = jobPosition; i > insertPosition; i--) {
				loesung2.getJobreihenfolge()[i] = loesung2.getJobreihenfolge()[i - 1];
			}

		}
		loesung2.getJobreihenfolge()[insertPosition] = job;
		return loesung2;
	}

	/**
	 * Nimmt einen Job und vertauscht ihn mit einem anderen
	 * 
	 * @param               Loesung, die veraendert werden soll
	 * @param swapPosition1 Index des ersten Jobs, der getauscht werden soll
	 * @param swapPosition2 Index des zweiten Jobs, der getauscht werden soll
	 * @return neue modifizierte Loesung
	 */
	private Loesung swapJob(Loesung loesung, int swapPosition1, int swapPosition2) {
		Loesung loesung2 = new Loesung(loesung.getAlter(), anzahlJobs, anzahlMaschinen, ausfuehrungszeiten);
		loesung2.jobreihenfolge = Arrays.copyOf(loesung.getJobreihenfolge(), loesung.getJobreihenfolge().length);
		int job = loesung2.getJobreihenfolge()[swapPosition1];
		loesung2.getJobreihenfolge()[swapPosition1] = loesung2.getJobreihenfolge()[swapPosition2];
		loesung2.getJobreihenfolge()[swapPosition2] = job;
		return loesung2;
	}

	public Loesung getLoesung() {
		return this.loesung;
	}
	
	@Override
	public String toString() {
		return this.getName() + "[Lösung: " + this.getLoesung().getLetztesTft() + "]";
	}
	
}