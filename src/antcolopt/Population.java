package antcolopt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Population {

	int iterationsanzahl = 0;
	int anzahlLoesungen = 0;
	Loesung[] loesungenInPopulation = new Loesung[KonstantenUndHelper.POPULATIONSGROESSE];
	Loesung eliteloesung;
	Loesung alteEliteLoesung;
	Loesung besteLoesungIteration;
	Loesung neueEliteLoesung;
	List<Integer> list = new ArrayList<Integer>();
	List<Integer> list2 = new ArrayList<Integer>();
	int tftEliteLoesung;
	int besterTftInPopulation;
	private int anzahlJobs = 0;
	private int anzahlMaschinen = 0;
	private int[][] ausfuehrungszeiten;
	private int[][][] dueDates;
	double[][] pheromonmatrix;
	Loesung heuristikLoesung;
	boolean bessereLoesungGefunden = false;

	/**
	 * Generiert erste Eliteloesung nach Heuristik
	 * 
	 * @param anzahlJobs         Anzahl der zu optimierenden Jobs
	 * @param anzahlMaschinen    Anzahl der zu optimierenden Maschinen
	 * @param ausfuehrungszeiten Bearbeitungszeiten der Jobs pro Maschine
	 */
	public Population(int jobAnzahl, int maschinenAnzahl, int[][] ausfuehrungszeiten,Loesung heuristikLoesung) {
		anzahlJobs = jobAnzahl;
		anzahlMaschinen = maschinenAnzahl;
		this.heuristikLoesung = heuristikLoesung;
		this.ausfuehrungszeiten = ausfuehrungszeiten;
		pheromonmatrix = new double[anzahlJobs][anzahlJobs];
		dueDates = new int[9][4][anzahlJobs];
		for (int i = 0; i < anzahlJobs; i++) {
			for (int j = 0; j < anzahlJobs; j++) {
				pheromonmatrix[i][j] = (double) 1 / (anzahlJobs);
			}
		}
		for (int k = 0; k < anzahlJobs; k++) {
			list.add(k);
			list2.add(k);
		}
		java.util.Collections.shuffle(list);
		java.util.Collections.shuffle(list2);
		if(KonstantenUndHelper.VERWENDE_HEURISTIK) {
	    eliteloesung = heuristikLoesung;
	   eliteloesung = lokaleSucheBestChangeFound(eliteloesung, KonstantenUndHelper.anzahlLokaleSuche);
		}
		else {
			//zufaellige Eliteloesung
			int[] zufaelligeJobReihenfolge = new int[list.size()];
			  for(int i = 0;i < zufaelligeJobReihenfolge.length;i++) {
			    zufaelligeJobReihenfolge[i] = list.get(i);}
			  Loesung zufaelligeLoesung = new Loesung(0, anzahlJobs, anzahlMaschinen, ausfuehrungszeiten);
			  zufaelligeLoesung.setJobreihenfolge(zufaelligeJobReihenfolge);
			eliteloesung = zufaelligeLoesung;
		}
		tftEliteLoesung = eliteloesung.berechneTFT();
		
		for (int i = 0; i < pheromonmatrix.length; i++) {
			pheromonmatrix[i][eliteloesung.jobreihenfolge[i]] = pheromonmatrix[i][eliteloesung.jobreihenfolge[i]]
					+ KonstantenUndHelper.elite_pheromonzuwachs;
					//+ (KonstantenUndHelper.eliteUpdateGewicht / anzahlJobs);
		}
		
	//	System.out.println(toString());
	}

	/**
	 * Aktualisiert die Pheromonmatrix mit neuer Populationsloesung
	 * 
	 * @param neueLoesung Loesung, die der Population hinzugefügt wird
	 * @param alteLoesung Loesung, die aus der Population geloescht wird
	 */
	private void updateMatrix(Loesung neueLoesung, Loesung alteLoesung) {
		for (int i = 0; i < pheromonmatrix.length; i++) {
			pheromonmatrix[i][neueLoesung.jobreihenfolge[i]] = pheromonmatrix[i][neueLoesung.jobreihenfolge[i]]
					+KonstantenUndHelper.PHEROMON_UPDATE_MENGE;
					//+ (KonstantenUndHelper.updategewicht / anzahlJobs);
			if (anzahlLoesungen >= KonstantenUndHelper.POPULATIONSGROESSE) {
				pheromonmatrix[i][alteLoesung.jobreihenfolge[i]] = pheromonmatrix[i][alteLoesung.jobreihenfolge[i]]
						-KonstantenUndHelper.PHEROMON_UPDATE_MENGE;
						//- (KonstantenUndHelper.updategewicht / anzahlJobs);
				loesungenInPopulation[ermittleIndexAeltesteLoesung()] = neueLoesung;
			}
		}
		if (anzahlLoesungen < KonstantenUndHelper.POPULATIONSGROESSE) {
			anzahlLoesungen++;
		}
	}

	/**
	 * Aktualisiert die Pheromonmatrix mit neuer Eliteloesung
	 * 
	 * @param neueLoesung Neue bessere Eliteloesung
	 * @param alteLoesung Alte schlechtere Eliteloesung
	 */
	private void updateEliteMatrix(Loesung neueLoesung, Loesung alteLoesung) {
		for (int i = 0; i < pheromonmatrix.length; i++) {
			pheromonmatrix[i][neueLoesung.jobreihenfolge[i]] = pheromonmatrix[i][neueLoesung.jobreihenfolge[i]]
					+KonstantenUndHelper.elite_pheromonzuwachs;
					//+ (KonstantenUndHelper.eliteUpdateGewicht / anzahlJobs);
			pheromonmatrix[i][alteLoesung.jobreihenfolge[i]] = pheromonmatrix[i][alteLoesung.jobreihenfolge[i]]
					-KonstantenUndHelper.elite_pheromonzuwachs;
					//- (KonstantenUndHelper.eliteUpdateGewicht / anzahlJobs);
		}
	}

	/** Ermittelt die Loesung, die am laengsten in der Population ist
	 * 
	 * @return Gibt die aelteste Loesung in der Population zurueck
	 */
	private Loesung ermittleAeltesteLoesung() {
		Loesung aeltesteLoesung = loesungenInPopulation[0];
		int alterAeltesteLoesung = loesungenInPopulation[0].getAlter();
		for (int i = 1; i < anzahlLoesungen; i++) {
			if (loesungenInPopulation[i].alter < alterAeltesteLoesung) {
				alterAeltesteLoesung = loesungenInPopulation[i].getAlter();
				aeltesteLoesung = loesungenInPopulation[i];
			}
		}
		return aeltesteLoesung;
	}

	/** Ermittelt den Index der Loesung, die am laengsten in der Population ist
	 * 
	 * @return Gibt Index der aeltesten Loesung im Populationsarray zurueck
	 */
	private int ermittleIndexAeltesteLoesung() {
		int index = 0;
		int alter = loesungenInPopulation[0].alter;
		for (int i = 1; i < anzahlLoesungen; i++) {
			if (loesungenInPopulation[i].alter <= alter) {
				alter = loesungenInPopulation[i].alter;
				index = i;
			}
		}
		return index;
	}

	/**
	 * Berechnet die beste Loesung aus der aktuellen Iteration
	 * 
	 * @param loesungen Loesungen, aus denen man die beste bestimmen will
	 * @return Beste Loesung unter den uebergebenen Loesungen
	 */
	private int ermittleBesteLoesungAusIteration(Loesung[] loesungen) {
		int tft = 999999;
		if (anzahlLoesungen > 0) {
			tft = loesungen[0].berechneTFT();
		}
		int besteLoesung = 0;
		for (int i = 1; i < loesungen.length; i++) {
			int aktuellerTft = loesungen[i].berechneTFT();
			if (aktuellerTft < tft) {
				tft = aktuellerTft;
				besteLoesung = i;
			}
		}
		besterTftInPopulation = tft;
		return besteLoesung;
	}

	/**
	 * Berechnet die beste Loesung aus der Population
	 * 
	 * @param loesungen Loesungen in der Population
	 * @return Beste Loesung aus der Population
	 */
	private int ermittleBesteLoesunginPopulation(Loesung[] loesungen) {
		int tft = 999999;
		if (anzahlLoesungen > 0) {
			tft = loesungen[0].berechneTFT();
		}
		int besteLoesung = 0;
		for (int i = 1; i < anzahlLoesungen; i++) {
			if (loesungen[i].berechneTFT() < tft) {
				tft = loesungen[i].berechneTFT();
				besteLoesung = i;
			}
		}
		return besteLoesung;
	}

	/**
	 * Ameisen suche neue Loesungen, lokale Suche wird angewendet, Pheromonmatrix
	 * wird aktualisiert
	 */
	public void berechneNeueIteration() {
		Loesung[] loesungen = generiereNeueLoesungen();

		if (KonstantenUndHelper.VERWENDE_LOKALESUCHE) {
			for(int a = 0; a<loesungen.length;a++) {
				loesungen[a] = lokaleSucheBestChangeFound(loesungen[a], KonstantenUndHelper.anzahlLokaleSuche);
			}
		}
		aktualisierePopulationUndEliteloesung(loesungen);
		//System.out.println(toString());
		iterationsanzahl++;

	}

	/**
	 * Ameisen suche neue Loesungen
	 * 
	 * @return Loesungen, die von Ameisen neu generiert wurden
	 */
	private Loesung[] generiereNeueLoesungen() {
		Ameise[] ameisen = new Ameise[KonstantenUndHelper.ANZAHL_AMEISEN];
		Loesung[] loesungen = new Loesung[KonstantenUndHelper.ANZAHL_AMEISEN];
		for (int i = 0; i < KonstantenUndHelper.ANZAHL_AMEISEN; i++) {
			ameisen[i] = new Ameise(eliteloesung, anzahlJobs, anzahlMaschinen);
			loesungen[i] = new Loesung(iterationsanzahl, anzahlJobs, anzahlMaschinen, ausfuehrungszeiten);
		}

		for (int j = 0; j < loesungen.length; j++) {
			for (int i = 0; i < anzahlJobs; i++) {
				ameisen[j].bestimmteNaechstenJob(pheromonmatrix);
			}
			loesungen[j].jobreihenfolge = ameisen[j].getBesuchteKnoten();
		}
	//System.out.println(toString());
		return loesungen;
	}

	/**
	 * Pheromonmatrix wird entsprechend der neue Loesungen aktualisiert
	 * 
	 * @param loesungen Loesungen, die von en Ameisen neu generiert wurden
	 */
	private void aktualisierePopulationUndEliteloesung(Loesung[] loesungen) {
		int indexBesteLoesung = ermittleBesteLoesungAusIteration(loesungen);
		besteLoesungIteration = loesungen[indexBesteLoesung];
		if (anzahlLoesungen < KonstantenUndHelper.POPULATIONSGROESSE) {
			loesungenInPopulation[iterationsanzahl] = loesungen[indexBesteLoesung];
		}
		if (besterTftInPopulation < tftEliteLoesung) {
			updateEliteMatrix(loesungen[indexBesteLoesung], eliteloesung);
			eliteloesung = loesungen[indexBesteLoesung];
			tftEliteLoesung = besterTftInPopulation;
		}
		updateMatrix(loesungen[indexBesteLoesung], ermittleAeltesteLoesung());
	}

	/**
	 * Lokale Suche, abwechselnd Insertion und Swap. Bester change wird ausgeführt.
	 * 
	 * @param loesungen Loesungen, die durch lokale SUche verbessert werden sollen
	 * @param k         Suchschritte insgesamt sind 2*k (swap+insertion)
	 */
	private Loesung lokaleSucheBestChangeFound(Loesung loesung, int k) {
	
		for (int l = 0; l < k; l++) {
			if (l % anzahlJobs == 0) {
				if((bessereLoesungGefunden == false) && (l >1)) {return loesung;}
				java.util.Collections.shuffle(list);
				java.util.Collections.shuffle(list2);
				bessereLoesungGefunden = false;
			}
			
				loesung = lokaleSucheInsertion(loesung, list.get(l % anzahlJobs));
				loesung = swapSearch(loesung, list2.get(l % anzahlJobs));
			
		}
		return loesung;
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

	/** Wendet lokale Suche mittels Swap Search an
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

	/** Nimmt einen Job der LOesung und setzt ihn an eine andere Stelle
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

	/** Nimmt einen Job und vertauscht ihn mit einem anderen
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

	
	/**
	 * Alternative Moeglichkeit fuer lokale Suche. Im Moment nicht verwendet.
	 * Lokale Suche, abwechselnd Insertion und Swap. Erster Change, der besser ist,
	 * wird ausgeführt. 
	 * 
	 * @param loesungen Loesungen, die durch lokale SUche verbessert werden sollen
	 * @param k         Suchschritte insgesamt sind 2*k (swap+insertion)
	 */
	private Loesung lokaleSucheFirstImprovementFound(Loesung loesung) {
		Loesung veraenderteLoesung = new Loesung(loesung.getAlter(), anzahlJobs, anzahlMaschinen, ausfuehrungszeiten);
		int loesungsguete = loesung.berechneTFT();
		for (int i = 0; i < KonstantenUndHelper.anzahlLokaleSuche; i++) {
			int jobIndex = (int) (Math.random() * anzahlJobs);
			int insertIndex = (int) (Math.random() * anzahlJobs);
			if ((i & 1) == 0) {
				veraenderteLoesung = insertJob(loesung, jobIndex, insertIndex);
			} else {
				veraenderteLoesung = swapJob(loesung, jobIndex, insertIndex);
			}
			int loesungsguete2 = veraenderteLoesung.berechneTFT();
			if (loesungsguete2 < loesungsguete) {
				loesungsguete = loesungsguete2;
				loesung = veraenderteLoesung;
			}
		}
		return loesung;
	}

	public int getIterationsanzahl() {
		return iterationsanzahl;
	}

	public void setIterationsanzahl(int iterationsanzahl) {
		this.iterationsanzahl = iterationsanzahl;
	}

	public int getAnzahlMaschinen() {
		return anzahlMaschinen;
	}

	public int[][] getAusfuehrungszeiten() {
		return ausfuehrungszeiten;
	}

	public void setAusfuehrungszeiten(int[][] ausfuehrungszeiten0) {
		this.ausfuehrungszeiten = ausfuehrungszeiten0;
	}

	public int[][][] getDueDates() {
		return dueDates;
	}

	public void setDueDates(int[][][] dueDates) {
		this.dueDates = dueDates;
	}

	public void setAnzahlMaschinen(int anzahlMaschinen) {
		this.anzahlMaschinen = anzahlMaschinen;
	}

	public int getAnzahlJobs() {
		return anzahlJobs;
	}

	public void setAnzahlJobs(int anzahlJobs) {
		this.anzahlJobs = anzahlJobs;
	}

	public double[][] getPheromonmatrix() {
		return pheromonmatrix;
	}

	public void setPheromonmatrix(double[][] pheromonmatrix) {
		this.pheromonmatrix = pheromonmatrix;
	}

	public Loesung getEliteloesung() {
		return eliteloesung;
	}

	public void setEliteloesung(Loesung eliteloesung) {
		this.eliteloesung = eliteloesung;
	}

	public int getTftEliteLoesung() {
		return tftEliteLoesung;
	}

	public void setTftEliteLoesung(int tftEliteLoesung) {
		this.tftEliteLoesung = tftEliteLoesung;
	}

	public String toString() {
		String s = "";
		 s += "populationsgroesse: " + anzahlLoesungen + "\n";
		
		  s += "Pheromonmatrix: \n"; for (int i = 0; i < anzahlJobs; i++) { for (int j
		  = 0; j < anzahlJobs; j++) { s += pheromonmatrix[i][j] + " "; } s += "\n"; }
		 

		if (loesungenInPopulation[ermittleBesteLoesunginPopulation(loesungenInPopulation)] != null) {
			s += "beste Lösung der aktuellen Iteration: " + besteLoesungIteration.berechneTFT();
			s += "\n";
			for (int i = 0; i < besteLoesungIteration.getJobreihenfolge().length; i++) {
				s += besteLoesungIteration.getJobreihenfolge()[i] + ", ";
			}
		}
		s += "\n";
		s += "beste Lösung: " + eliteloesung.berechneTFT();
		s += "\n";
		for (int i = 0; i < eliteloesung.getJobreihenfolge().length; i++) {
			s += eliteloesung.getJobreihenfolge()[i] + ", ";
		}
		s += "   Alter: " + eliteloesung.getAlter();
		s += "\n Anzahl Loesungen: " + anzahlLoesungen;
		s += "\n";
		s += "\n";
		return s;
	}
}