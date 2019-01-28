package antcolopt;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.omg.Messaging.SyncScopeHelper;

public class Population {

	public int getIterationsanzahl() {
		return iterationsanzahl;
	}

	public void setIterationsanzahl(int iterationsanzahl) {
		this.iterationsanzahl = iterationsanzahl;
	}

	int alterAeltesteLoesung = 1000000;
	int iterationsanzahl = 0;
	int anzahlLoesungen = 0;
	Loesung[] loesungenInPopulation = new Loesung[Problem.populationsgroesse];
	Loesung eliteloesung;
	Loesung alteEliteLoesung;
	Loesung besteLoesungIteration;
	Loesung neueEliteLoesung;
	List<Integer> list = new ArrayList<Integer>();
	List<Integer> list2 = new ArrayList<Integer>();
	int tftEliteLoesung;
	int besterTftInPopulation;
	private int anzahlJobs=0;
	private int anzahlMaschinen=0;
	//Berabeitungszeit bei Probleminstanz a auf Maschine b und Job c
	private int[][] ausfuehrungszeiten;
	private int[][][] dueDates;
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

	double[][] pheromonmatrix;

	public Population(int jobAnzahl, int maschinenAnzahl, int[][] ausfuehrungszeiten) {
		
anzahlJobs = jobAnzahl;
anzahlMaschinen = maschinenAnzahl;
this.ausfuehrungszeiten = ausfuehrungszeiten;
pheromonmatrix = new double[anzahlJobs][anzahlJobs];
dueDates = new int[9][4][anzahlJobs];
		// eliteloesung = new Loesung(0);
		for (int i = 0; i < anzahlJobs; i++) {
			for (int j = 0; j < anzahlJobs; j++) {
				pheromonmatrix[i][j] = (double) 1 / (anzahlJobs);

			}
		}
		for (int k = 0; k < anzahlJobs; k++) {
			// eliteloesung.getJobreihenfolge()[k]=k;
			list.add(k);
			list2.add(k);
		}
		java.util.Collections.shuffle(list);
		java.util.Collections.shuffle(list2);
		eliteloesung = Problem.generiereTftHeristikLoesung(anzahlJobs,anzahlMaschinen, ausfuehrungszeiten);
		Loesung[] loesungArray = new Loesung[1];
		loesungArray[0] = eliteloesung;
		wendeLokaleSucheAn(loesungArray, Problem.anzahlLokaleSuche);
		eliteloesung = loesungArray[0];
		tftEliteLoesung = eliteloesung.berechneTFT();
		for (int i = 0; i < pheromonmatrix.length; i++) {
			pheromonmatrix[i][eliteloesung.jobreihenfolge[i]] = pheromonmatrix[i][eliteloesung.jobreihenfolge[i]]
					+ (Problem.eliteUpdateGewicht / anzahlJobs);
		}
		System.out.println(toString());

	}

	public int getAnzahlJobs() {
		return anzahlJobs;
	}

	public void setAnzahlJobs(int anzahlJobs) {
		this.anzahlJobs = anzahlJobs;
	}

	public String toString() {
		String s = "";
		s += "populationsgroesse: " + anzahlLoesungen + "\n";
		s += "Pheromonmatrix: \n";
		for (int i = 0; i < anzahlJobs; i++) {
			for (int j = 0; j < anzahlJobs; j++) {
				s += pheromonmatrix[i][j] + " ";
			}

			s += "\n";
		}
		// if (loesungenInPopulation[ermittleBesteLoesung(loesungenInPopulation)] !=
		// null) {
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

	public double[][] getPheromonmatrix() {
		return pheromonmatrix;
	}

	public void setPheromonmatrix(double[][] pheromonmatrix) {
		this.pheromonmatrix = pheromonmatrix;
	}

	public void updateMatrix(Loesung neueLoesung, Loesung alteLoesung) {
		for (int i = 0; i < pheromonmatrix.length; i++) {
			pheromonmatrix[i][neueLoesung.jobreihenfolge[i]] = pheromonmatrix[i][neueLoesung.jobreihenfolge[i]]
					+ (Problem.updategewicht / anzahlJobs);
			if (anzahlLoesungen >= Problem.populationsgroesse) {
				pheromonmatrix[i][alteLoesung.jobreihenfolge[i]] = pheromonmatrix[i][alteLoesung.jobreihenfolge[i]]
						- (Problem.updategewicht / anzahlJobs);
				loesungenInPopulation[ermittleIndexAeltesteLoesung()] = neueLoesung;
			}

		}
		if (anzahlLoesungen < Problem.populationsgroesse) {
			anzahlLoesungen++;
		}
	}

	public void updateEliteMatrix(Loesung neueLoesung, Loesung alteLoesung) {
		for (int i = 0; i < pheromonmatrix.length; i++) {
			pheromonmatrix[i][neueLoesung.jobreihenfolge[i]] = pheromonmatrix[i][neueLoesung.jobreihenfolge[i]]
					+ (Problem.eliteUpdateGewicht / anzahlJobs);

			pheromonmatrix[i][alteLoesung.jobreihenfolge[i]] = pheromonmatrix[i][alteLoesung.jobreihenfolge[i]]
					- (Problem.eliteUpdateGewicht / anzahlJobs);

		}
	}

	public Loesung ermittleAeltesteLoesung() {
		Loesung aeltesteLoesung = loesungenInPopulation[0];
		alterAeltesteLoesung = loesungenInPopulation[0].getAlter();
		for (int i = 1; i < anzahlLoesungen; i++) {
			if (loesungenInPopulation[i].alter < alterAeltesteLoesung) {
				alterAeltesteLoesung = loesungenInPopulation[i].getAlter();
				aeltesteLoesung = loesungenInPopulation[i];
			}
		}
		return aeltesteLoesung;
	}

	public int ermittleIndexAeltesteLoesung() {
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

	private int ermittleBesteLoesung(Loesung[] loesungen) {
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

	public int ermittleBesteLoesunginPopulation(Loesung[] loesungen) {
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

	public Loesung getEliteloesung() {
		return eliteloesung;
	}

	public void setEliteloesung(Loesung eliteloesung) {
		this.eliteloesung = eliteloesung;
	}

	public Loesung generiereLoesung() {
		Ameise[] ameisen = new Ameise[Problem.anzahlAmeisen];
		Loesung[] loesungen = new Loesung[Problem.anzahlAmeisen];
		for (int i = 0; i < Problem.anzahlAmeisen; i++) {
			ameisen[i] = new Ameise(eliteloesung, anzahlJobs,anzahlMaschinen);
			loesungen[i] = new Loesung(iterationsanzahl,anzahlJobs,anzahlMaschinen,ausfuehrungszeiten);
		}

		for (int j = 0; j < loesungen.length; j++) {

			for (int i = 0; i < anzahlJobs; i++) {
				ameisen[j].naechsterKnoten(pheromonmatrix);
			}

			loesungen[j].jobreihenfolge = ameisen[j].getBesuchteKnoten();

		}

		if (Problem.lokaleSuche == true) {

			
			//for(int i=0;i<loesungen.length;i++) {
				//loesungen[i] = wendeLokaleSucheAn2(loesungen[i]);
				
			//}
			wendeLokaleSucheAn(loesungen, Problem.anzahlLokaleSuche);
			
		}

		int indexBesteLoesung = ermittleBesteLoesung(loesungen);
		besteLoesungIteration = loesungen[indexBesteLoesung];
		if (anzahlLoesungen < Problem.populationsgroesse) {
			loesungenInPopulation[iterationsanzahl] = loesungen[indexBesteLoesung];
		}
		/*
		 * if (iterationsanzahl == 0) { // update Matrix mit Elitelösung eliteloesung =
		 * loesungen[besteLoesung]; for (int i = 0; i < pheromonmatrix.length; i++) {
		 * pheromonmatrix[i][eliteloesung.jobreihenfolge[i]] =
		 * pheromonmatrix[i][eliteloesung.jobreihenfolge[i]] +
		 * (Problem.eliteUpdateGewicht / Problem.anzahlJobs);
		 * 
		 * } } else {
		 */
		if (besterTftInPopulation < tftEliteLoesung) {
			updateEliteMatrix(loesungen[indexBesteLoesung], eliteloesung);
			eliteloesung = loesungen[indexBesteLoesung];
			tftEliteLoesung = besterTftInPopulation;
			// }
		}

		/*
		 * alteEliteLoesung = new Loesung(iterationsanzahl);
		 * alteEliteLoesung.jobreihenfolge =
		 * Arrays.copyOf(eliteloesung.getJobreihenfolge(),
		 * eliteloesung.getJobreihenfolge().length);
		 * 
		 * 
		 * 
		 * for (int j = 0; j < Problem.anzahlJobs; j++) {
		 * 
		 * eliteloesung = lokaleSucheInsertion(eliteloesung,j);
		 * 
		 * } System.out.println(alteEliteLoesung.toString());
		 * System.out.println(eliteloesung.toString()); updateEliteMatrix(eliteloesung,
		 * alteEliteLoesung);
		 */

		updateMatrix(loesungen[indexBesteLoesung], ermittleAeltesteLoesung());
		System.out.println(toString());
		iterationsanzahl++;
		return eliteloesung;
	}

	public int getTftEliteLoesung() {
		return tftEliteLoesung;
	}

	public void setTftEliteLoesung(int tftEliteLoesung) {
		this.tftEliteLoesung = tftEliteLoesung;
	}

	private void wendeLokaleSucheAn(Loesung[] loesungen, int k) {
		for (int l = 0; l < k; l++) {
			if ((l % anzahlJobs == 0) && (l > 0)) {
				java.util.Collections.shuffle(list);
				java.util.Collections.shuffle(list2);
			}
			for (int i = 0; i < loesungen.length; i++) {

				// int zufall = (int) (Problem.anzahlJobs * Math.random());

				loesungen[i] = lokaleSucheInsertion(loesungen[i], list.get(l % anzahlJobs));
				// zufall = (int) (Problem.anzahlJobs * Math.random());
				loesungen[i] = swapSearch(loesungen[i], list2.get(l % anzahlJobs));

				// for (int j = 0; j <10; j++) {
				// int zufall = (int) (Problem.anzahlJobs * Math.random());
				// loesungen[i] = swapSearch(loesungen[i], zufall);
				// zufall = (int) (Problem.anzahlJobs * Math.random());
				// }
			}
		}
	}

	
	
	public Loesung lokaleSucheInsertion(Loesung loesung, int index) {
		int besterTft = loesung.berechneTFT();
		int tftTemp = 0;
		Loesung besteLoesung = loesung;
		
		Loesung tempLoesung = new Loesung(loesung.getAlter(),anzahlJobs,anzahlMaschinen,ausfuehrungszeiten);

		for (int i = 0; i < loesung.getJobreihenfolge().length; i++) {

			tempLoesung = insertJob(loesung, index, i);
		     tftTemp = tempLoesung.berechneTFT();
			if (tftTemp < besterTft) {
				besteLoesung = tempLoesung;
				besterTft = tftTemp;
			
			}

		}
		return besteLoesung;
	}

	/*public Loesung ibls(Loesung loesung, int index) {

		Loesung besteLoesungIbls = loesung;
		List<Integer> list = new ArrayList<Integer>();
		for (int i = 0; i < Problem.anzahlJobs; i++) {
			list.add(i);
		}

		java.util.Collections.shuffle(list);
		System.out.println(list);
		int i = 0;
		int h = 1;
		while (i < Problem.anzahlJobs) {
			int s = list.get(h - 1);
			int j = findeIndexJob(besteLoesungIbls, s);
			Loesung[] w = new Loesung[5000];
			int anzahlLoesungenIbls = -1;
			for (int k = 0; k < j - 1; k++) {
				anzahlLoesungenIbls++;
				// System.out.println(anzahlLoesungenIbls);
				w[anzahlLoesungenIbls] = new Loesung(besteLoesungIbls.getAlter());
				w[anzahlLoesungenIbls].jobreihenfolge = Arrays.copyOf(besteLoesungIbls.getJobreihenfolge(),
						besteLoesungIbls.getJobreihenfolge().length);
				for (int z = 0; z < j; z++) {
					if (z >= k) {

						// System.out.println(j);
						// System.out.println(list.get(h));
						if (z < Problem.anzahlJobs - 1) {
							w[anzahlLoesungenIbls].jobreihenfolge[z + 1] = besteLoesungIbls.jobreihenfolge[z];
						}

					}

					// System.out.println("k: " + k);
					w[anzahlLoesungenIbls].jobreihenfolge[k] = s;

				}
			}
			System.out.println("j: " + j);
			for (int k = j; k < Problem.anzahlJobs; k++) {
				anzahlLoesungenIbls++;
				// System.out.println(anzahlLoesungenIbls);
				w[anzahlLoesungenIbls] = new Loesung(besteLoesungIbls.getAlter());

				w[anzahlLoesungenIbls].jobreihenfolge = Arrays.copyOf(besteLoesungIbls.getJobreihenfolge(),
						besteLoesungIbls.getJobreihenfolge().length);

				for (int z = j; z < Problem.anzahlJobs - 1; z++) {
					if (z <= k) {
						w[anzahlLoesungenIbls].jobreihenfolge[z] = besteLoesungIbls.jobreihenfolge[z + 1];
					}
				}
				w[anzahlLoesungenIbls].jobreihenfolge[k] = s;
			}

			int besterTftNeuerLoesungen = w[0].berechneTFT();
			Loesung besteInsertionLoesung = new Loesung(loesung.getAlter());
			for (int y = 1; y < anzahlLoesungenIbls; y++) {
				// System.out.println(y);
				// System.out.println(anzahlLoesungenIbls);
				int loesungsGuete = w[y].berechneTFT();
				if (loesungsGuete < besterTftNeuerLoesungen) {
					besteInsertionLoesung = w[y];
					besterTftNeuerLoesungen = loesungsGuete;

				}
			}
			System.out.println("besterTFTNeuerLoesungen :" + besterTftNeuerLoesungen);
			System.out.println("besterTFT: " + besteLoesungIbls.berechneTFT());
			if (besterTftNeuerLoesungen < besteLoesungIbls.berechneTFT()) {
				besteLoesungIbls = besteInsertionLoesung;
				System.out.println("NeuerBesterTFT: " + besteLoesungIbls.berechneTFT());
				System.out.println("neueLoesung:");
				for (int f = 0; f < Problem.anzahlJobs; f++) {

					System.out.println(besteLoesungIbls.jobreihenfolge[f]);
				}
				i = 1;
			} else {
				i = i + 1;
			}
			h = (h + 1) % Problem.anzahlJobs;
		}

		return besteLoesungIbls;
	}*/

	public int findeIndexJob(Loesung loesung, int job) {
		for (int i = 0; i < anzahlJobs; i++) {
			if (loesung.jobreihenfolge[i] == job) {
				return i;
			}
		}
		return 999;
	}

	public Loesung swapSearch(Loesung loesung, int index) {

		int besterTft = loesung.berechneTFT();
		int tftTemp = 0;
		Loesung besteLoesung = loesung;
		
		Loesung tempLoesung = new Loesung(loesung.getAlter(),anzahlJobs,anzahlMaschinen,ausfuehrungszeiten);

		for (int i = 0; i < loesung.getJobreihenfolge().length; i++) {

			tempLoesung = swapJob(loesung, index, i);
		     tftTemp = tempLoesung.berechneTFT();
			if (tftTemp < besterTft) {
				besteLoesung = tempLoesung;
				besterTft = tftTemp;
			
			}

		}
	
		return besteLoesung;
	}

	public Loesung insertJob(Loesung loesung, int jobPosition, int insertPosition) {
		Loesung loesung2 = new Loesung(loesung.getAlter(),anzahlJobs,anzahlMaschinen,ausfuehrungszeiten);
		loesung2.jobreihenfolge = Arrays.copyOf(loesung.getJobreihenfolge(),
				loesung.getJobreihenfolge().length);
		int job = loesung2.getJobreihenfolge()[jobPosition];
		if (jobPosition < insertPosition) {
			for (int i = jobPosition; i < insertPosition ; i++) {
				loesung2.getJobreihenfolge()[i] = loesung2.getJobreihenfolge()[i + 1];
			}
		}
		if (jobPosition > insertPosition) {
			for (int i = jobPosition; i > insertPosition ; i--) {
				loesung2.getJobreihenfolge()[i] = loesung2.getJobreihenfolge()[i-1];
			}
			
		}
		loesung2.getJobreihenfolge()[insertPosition] = job;
		return loesung2;
	}
	
	public Loesung swapJob (Loesung loesung, int swapPosition1, int swapPosition2) {
		Loesung loesung2 = new Loesung(loesung.getAlter(),anzahlJobs,anzahlMaschinen,ausfuehrungszeiten);
		loesung2.jobreihenfolge = Arrays.copyOf(loesung.getJobreihenfolge(),
				loesung.getJobreihenfolge().length);
		int job = loesung2.getJobreihenfolge()[swapPosition1];
		loesung2.getJobreihenfolge()[swapPosition1] = loesung2.getJobreihenfolge()[swapPosition2];
				loesung2.getJobreihenfolge()[swapPosition2] = job;
	return loesung2;
	}
		
		
		
		
		
	public Loesung wendeLokaleSucheAn2 (Loesung loesung) {
	//	int jobIndexInit = (int) (Math.random() * Problem.anzahlJobs);
		//int insertIndexInit = (int) (Math.random() * Problem.anzahlJobs);
		Loesung veraenderteLoesung = new Loesung(loesung.getAlter(),anzahlJobs,anzahlMaschinen,ausfuehrungszeiten);
		//veraenderteLoesung = insertJob(loesung, jobIndexInit, insertIndexInit);
		int loesungsguete = loesung.berechneTFT();
		for(int i = 0; i<Problem.anzahlLokaleSuche;i++)
		{
			int jobIndex = (int) (Math.random() * anzahlJobs);
			int insertIndex = (int) (Math.random() * anzahlJobs);
			
			if ( (i & 1) == 0 ) {
			veraenderteLoesung = insertJob(loesung, jobIndex, insertIndex);}
			else {veraenderteLoesung = swapJob(loesung, jobIndex, insertIndex);}
			int loesungsguete2 = veraenderteLoesung.berechneTFT();
			if(loesungsguete2 < loesungsguete) {
				loesungsguete = loesungsguete2;
				loesung = veraenderteLoesung;
			}
		}
		return loesung;
	}
	
}