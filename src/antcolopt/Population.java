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
	double[][] pheromonmatrix = new double[Problem.anzahlJobs][Problem.anzahlJobs];
	Loesung eliteloesung;
	Loesung alteEliteLoesung;
	Loesung besteLoesungIteration;
	Loesung neueEliteLoesung;
	List<Integer> list = new ArrayList<Integer>();
	List<Integer> list2 = new ArrayList<Integer>();
	

	public Population() {
		
		//eliteloesung = new Loesung(0);
		for (int i = 0; i < Problem.anzahlJobs; i++) {
			for (int j = 0; j < Problem.anzahlJobs; j++) {
				pheromonmatrix[i][j] = (double) 1 / (Problem.anzahlJobs);
				
				
			}
		}
			for (int k = 0; k < Problem.anzahlJobs; k++) {
			//	eliteloesung.getJobreihenfolge()[k]=k;
				list.add(k);
				list2.add(k);
			}
			java.util.Collections.shuffle(list);			
			java.util.Collections.shuffle(list2);
			eliteloesung = Problem.generiereTftHeristikLoesung();
			Loesung[] loesungArray = new Loesung[1];
			loesungArray[0] = eliteloesung;
			wendeLokaleSucheAn(loesungArray, 3);
			eliteloesung = loesungArray[0];
			System.out.println(toString());
		
	}

	public String toString() {
		String s = "";
		s += "populationsgroesse: " + anzahlLoesungen + "\n";
		s += "Pheromonmatrix: \n";
		for (int i = 0; i < Problem.anzahlJobs; i++) {
			for (int j = 0; j < Problem.anzahlJobs; j++) {
				s += pheromonmatrix[i][j] + " ";
			}

			s += "\n";
		}
		// if (loesungenInPopulation[ermittleBesteLoesung(loesungenInPopulation)] !=
		// null) {
		if (loesungenInPopulation[ermittleBesteLoesung(loesungenInPopulation, true)] != null) {
			s += "beste Lösung der aktuellen Iteration: " + besteLoesungIteration.berechneTFT();
			s += "\n";
			for (int i = 0; i < besteLoesungIteration.getJobreihenfolge().length; i++) {
				s += besteLoesungIteration.getJobreihenfolge()[i] + ", ";
			}}
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
					+ (Problem.updategewicht / Problem.anzahlJobs);
			if (anzahlLoesungen >= Problem.populationsgroesse) {
				pheromonmatrix[i][alteLoesung.jobreihenfolge[i]] = pheromonmatrix[i][alteLoesung.jobreihenfolge[i]]
						- (Problem.updategewicht / Problem.anzahlJobs);
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
					+ (Problem.eliteUpdateGewicht / Problem.anzahlJobs);

			pheromonmatrix[i][alteLoesung.jobreihenfolge[i]] = pheromonmatrix[i][alteLoesung.jobreihenfolge[i]]
					- (Problem.eliteUpdateGewicht / Problem.anzahlJobs);

		}
	}

	public Loesung ermittleAeltesteLoesung() {
		Loesung aeltesteLoesung = loesungenInPopulation[0];
		for (int i = 0; i < anzahlLoesungen; i++) {
			if (loesungenInPopulation[i].alter <= alterAeltesteLoesung) {
				alterAeltesteLoesung = loesungenInPopulation[i].alter;
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

	public int ermittleBesteLoesung(Loesung[] loesungen) {
		int tft = 999999;
		if (anzahlLoesungen > 0) {
			tft = loesungen[0].berechneTFT();
		}
		int besteLoesung = 0;
		for (int i = 1; i < loesungen.length; i++) {
			if (loesungen[i].berechneTFT() < tft) {
				tft = loesungen[i].berechneTFT();
				besteLoesung = i;
			}
		}
		return besteLoesung;
	}

	public int ermittleBesteLoesung(Loesung[] loesungen, Boolean b) {
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
			ameisen[i] = new Ameise(eliteloesung);
			loesungen[i] = new Loesung(iterationsanzahl);
		}

		for (int j = 0; j < loesungen.length; j++) {

			for (int i = 0; i < Problem.anzahlJobs; i++) {
				ameisen[j].naechsterKnoten(pheromonmatrix);
			}

			loesungen[j].jobreihenfolge = ameisen[j].getBesuchteKnoten();
			
			

		}

		if(Problem.lokaleSuche == true) {
			
			
			wendeLokaleSucheAn(loesungen,3);
		}

		int besteLoesung = ermittleBesteLoesung(loesungen);
		besteLoesungIteration = loesungen[besteLoesung];
		if (iterationsanzahl < Problem.populationsgroesse) {
			loesungenInPopulation[iterationsanzahl] = loesungen[besteLoesung];
		}
		if (iterationsanzahl == 0) {
			// update Matrix mit Elitelösung
			eliteloesung = loesungen[besteLoesung];
			for (int i = 0; i < pheromonmatrix.length; i++) {
				pheromonmatrix[i][eliteloesung.jobreihenfolge[i]] = pheromonmatrix[i][eliteloesung.jobreihenfolge[i]]
						+ (Problem.eliteUpdateGewicht / Problem.anzahlJobs);

			}
		} else {
			if (loesungen[besteLoesung].berechneTFT() < eliteloesung.berechneTFT()) {
				updateEliteMatrix(loesungen[besteLoesung], eliteloesung);
				eliteloesung = loesungen[besteLoesung];
			}
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

		updateMatrix(loesungen[besteLoesung], ermittleAeltesteLoesung());
		System.out.println(toString());
		iterationsanzahl++;
		return eliteloesung;
	}

	private void wendeLokaleSucheAn(Loesung[] loesungen, int k) {
		for(int l=0;l<k;l++ ) {
		for (int i = 0; i < loesungen.length; i++) {
			for (int j = 0; j <Problem.anzahlJobs; j++) {
				//int zufall = (int) (Problem.anzahlJobs * Math.random());
				
			 loesungen[i] = lokaleSucheInsertion(loesungen[i], list.get(j));
			// zufall = (int) (Problem.anzahlJobs * Math.random());
			 loesungen[i] = swapSearch(loesungen[i], list2.get(j));
			}
			java.util.Collections.shuffle(list);
			java.util.Collections.shuffle(list2);
		//	for (int j = 0; j <10; j++) {
			//	int zufall = (int) (Problem.anzahlJobs * Math.random());
		//	loesungen[i] = swapSearch(loesungen[i], zufall);
		//	zufall = (int) (Problem.anzahlJobs * Math.random());
		//	}
		}
}
	}

	public Loesung lokaleSucheInsertion(Loesung loesung, int index) {
		int tft = loesung.berechneTFT();
		int tftTemp = 0;
		int indexwert = loesung.getJobreihenfolge()[index];
		Loesung neueLoesung = new Loesung(loesung.getAlter());
		Loesung besteLoesung = loesung;
		if (index == Problem.anzahlJobs - 1) {
			indexwert = loesung.getJobreihenfolge()[Problem.anzahlJobs - 1];
		}
		for (int i = 0; i < neueLoesung.getJobreihenfolge().length - 1; i++) {
			if (i < index) {
				neueLoesung.getJobreihenfolge()[i] = loesung.getJobreihenfolge()[i];
			}
			if (i >= index) {
				neueLoesung.getJobreihenfolge()[i] = loesung.getJobreihenfolge()[i + 1];
			}
		}

		Loesung tempLoesung = new Loesung(loesung.getAlter());

		for (int i = 0; i < loesung.getJobreihenfolge().length; i++) {

			tempLoesung.jobreihenfolge = Arrays.copyOf(neueLoesung.getJobreihenfolge(),
					neueLoesung.getJobreihenfolge().length);

			for (int j = loesung.getJobreihenfolge().length - 1; j > i; j--) {
				tempLoesung.getJobreihenfolge()[j] = neueLoesung.getJobreihenfolge()[j - 1];

			}
			tempLoesung.getJobreihenfolge()[i] = indexwert;
			tftTemp = tempLoesung.berechneTFT();
			if (tftTemp < tft) {
				besteLoesung.jobreihenfolge = Arrays.copyOf(tempLoesung.getJobreihenfolge(),
						tempLoesung.getJobreihenfolge().length);
				tft = tftTemp;
			}

		}
		return besteLoesung;
	}

	public Loesung ibls(Loesung loesung, int index) {
		
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
			int s = list.get(h-1);
			int j = findeIndexJob(besteLoesungIbls, s);
			Loesung[] w = new Loesung[5000];
			int anzahlLoesungenIbls = -1;
			for (int k = 0; k < j-1; k++) {
				anzahlLoesungenIbls++;
				//System.out.println(anzahlLoesungenIbls);
				w[anzahlLoesungenIbls] = new Loesung(besteLoesungIbls.getAlter());				
				w[anzahlLoesungenIbls].jobreihenfolge = Arrays.copyOf(besteLoesungIbls.getJobreihenfolge(),
						besteLoesungIbls.getJobreihenfolge().length);
				for (int z = 0; z < j; z++) {
					if (z >= k) {
						
					//	System.out.println(j);
						//System.out.println(list.get(h));
						if (z<Problem.anzahlJobs-1) {
						w[anzahlLoesungenIbls].jobreihenfolge[z + 1] = besteLoesungIbls.jobreihenfolge[z];}
					
				}
			
				//System.out.println("k: " + k);
				w[anzahlLoesungenIbls].jobreihenfolge[k] = s;

			}
			}
			System.out.println("j: " + j);
			for (int k = j ; k < Problem.anzahlJobs; k++) {
				anzahlLoesungenIbls++;
				//System.out.println(anzahlLoesungenIbls);
				w[anzahlLoesungenIbls] = new Loesung(besteLoesungIbls.getAlter());
				
				w[anzahlLoesungenIbls].jobreihenfolge = Arrays.copyOf(besteLoesungIbls.getJobreihenfolge(),
						besteLoesungIbls.getJobreihenfolge().length);

				for (int z = j; z < Problem.anzahlJobs-1; z++) {
					if (z <= k) {
						w[anzahlLoesungenIbls].jobreihenfolge[z] = besteLoesungIbls.jobreihenfolge[z + 1];
					} 
				}
				w[anzahlLoesungenIbls].jobreihenfolge[k] = s;
			}

			int besterTftNeuerLoesungen = w[0].berechneTFT();
			Loesung besteInsertionLoesung = new Loesung(loesung.getAlter());
			for (int y = 1; y < anzahlLoesungenIbls; y++) {
				//System.out.println(y);
				//System.out.println(anzahlLoesungenIbls);
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
				for (int f = 0; f<Problem.anzahlJobs;f++) {
					
					System.out.println(besteLoesungIbls.jobreihenfolge[f]);
				}
				i = 1;
			} else {
				i = i + 1;
			}
			h = (h + 1) % Problem.anzahlJobs;
		}

		return besteLoesungIbls;
	}

	public int findeIndexJob(Loesung loesung, int job) {
		System.out.println(job);
		for (int i = 0; i < Problem.anzahlJobs; i++) {
			if (loesung.jobreihenfolge[i] == job) {
				return i;
			}
		}
		return 999;
	}
	
	public Loesung swapSearch(Loesung loesung, int index) {
		
		int tft = loesung.berechneTFT();
		
	
		Loesung besteLoesung = new Loesung(loesung.getAlter());
		besteLoesung = loesung;
		for(int i = 0; i<loesung.getJobreihenfolge().length;i++) {
			
			Loesung bessereLoesung = new Loesung(loesung.getAlter());
			bessereLoesung.jobreihenfolge = Arrays.copyOf(loesung.getJobreihenfolge(),
					loesung.getJobreihenfolge().length);
			int temp = bessereLoesung.getJobreihenfolge()[i];
			bessereLoesung.getJobreihenfolge()[i] = loesung.getJobreihenfolge()[index];
			bessereLoesung.getJobreihenfolge()[index] = temp;
			int tempTft = bessereLoesung.berechneTFT();
			
			if(tempTft < tft) {
				
				besteLoesung = bessereLoesung;
				tft = tempTft;
			}
	}
		
		
		return besteLoesung;
	}
}