package ant_col_opt_2crit;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

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
	ArrayList<Loesung> loesungenInSuperPopulation = new ArrayList<Loesung>();
	double[][] pheromonmatrix = new double[Problem.anzahlJobs][Problem.anzahlJobs];
	Loesung eliteloesung;
	Loesung alteEliteLoesung;
	Loesung besteLoesungIteration;
	Loesung neueEliteLoesung;
	List<Integer> list = new ArrayList<Integer>();
	List<Integer> list2 = new ArrayList<Integer>();
	int tftEliteLoesung;
	int besterTftInPopulation;
	private Loesung aktuellAusgewaehlteLoesung;
	private boolean bessereLoesungGefunden = false;

	public Loesung getAktuellAusgewaehlteLoesung() {
		return aktuellAusgewaehlteLoesung;
	}

	public void setAktuellAusgewaehlteLoesung(Loesung aktuellAusgewaehlteLoesung) {
		this.aktuellAusgewaehlteLoesung = aktuellAusgewaehlteLoesung;
	}

	public Population() {

		// eliteloesung = new Loesung(0);
		for (int i = 0; i < Problem.anzahlJobs; i++) {
			for (int j = 0; j < Problem.anzahlJobs; j++) {
				pheromonmatrix[i][j] = (double) 1 / (Problem.anzahlJobs);

			}
		}
		for (int k = 0; k < Problem.anzahlJobs; k++) {
			// eliteloesung.getJobreihenfolge()[k]=k;
			list.add(k);
			list2.add(k);
		}
		java.util.Collections.shuffle(list);
		java.util.Collections.shuffle(list2);
		// eliteloesung = Problem.generiereTftHeristikLoesung();
		eliteloesung = Problem.generiereZufaelligeLoesung(Problem.anzahlJobs);
		Loesung[] loesungArray = new Loesung[1];
		loesungArray[0] = eliteloesung;
	//	wendeLokaleSucheAn(loesungArray, Problem.anzahlLokaleSuche);
		eliteloesung = loesungArray[0];
		tftEliteLoesung = eliteloesung.berechneTFT();
		loesungenInSuperPopulation.add(eliteloesung);
		
		//System.out.println(toString());

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
	//	if (loesungenInSuperPopulation[ermittleBesteLoesunginPopulation(loesungenInSuperPopulation)] != null) {
			//s += "beste Lösung der aktuellen Iteration: " + besteLoesungIteration.berechneTFT();
		//	s += "\n";
		//	for (int i = 0; i < besteLoesungIteration.getJobreihenfolge().length; i++) {
			//	s += besteLoesungIteration.getJobreihenfolge()[i] + ", ";
		//	}
		//}
		s += "\n";
		for(Loesung loesung:loesungenInSuperPopulation) {
		s += "TFT: " + loesung.berechneTFT() + "  Tardniness: " + loesung.getMeanTardiness() + "  Alter: " + loesung.getAlter();
		s += "\n";
		}
		if(aktuellAusgewaehlteLoesung != null) {
		for (int i = 0; i < aktuellAusgewaehlteLoesung.getJobreihenfolge().length; i++) {
			s += aktuellAusgewaehlteLoesung.getJobreihenfolge()[i] + ", ";
		}
		}
		s += "\n Anzahl Loesungen: " + loesungenInSuperPopulation.size();
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
	
	public void testeLoesungFuerSuperpopulation(Loesung loesung) {
		//for(int i = 0; i<loesungenInSuperPopulation.size();i++) {
		for (Iterator<Loesung> iterator = this.loesungenInSuperPopulation.iterator(); iterator.hasNext();) {
			Loesung loesungInSP =  iterator.next();
			if(testeAufDominierung(loesung, loesungInSP) == 1) 
			{iterator.remove();
			anzahlLoesungen--;}
			else {if((testeAufDominierung(loesung, loesungInSP) == -1) || (Arrays.equals(loesung.getJobreihenfolge(),loesungInSP.getJobreihenfolge())))
			{return;}}
		}
		loesungenInSuperPopulation.add(loesung);
		anzahlLoesungen++;
	}
	public void berechneMatrix(Loesung[] loesungen) {
		
		for(int j = 0; j<loesungen.length;j++) {
		for (int i = 0; i < pheromonmatrix.length; i++) {
			pheromonmatrix[i][loesungen[j].jobreihenfolge[i]] = pheromonmatrix[i][loesungen[j].jobreihenfolge[i]]
					+ Problem.PHEROMON_UPDATE_MENGE;
		}
		}
		}
	
	public void setzeMatrixZurueck(Loesung[] loesungen) {
		for(int j = 0; j<loesungen.length;j++) {
			for (int i = 0; i < pheromonmatrix.length; i++) {
				pheromonmatrix[i][loesungen[j].jobreihenfolge[i]] = pheromonmatrix[i][loesungen[j].jobreihenfolge[i]]
						- Problem.PHEROMON_UPDATE_MENGE;
			}
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

	

	

	public int ermittleBesteLoesung(Loesung[] loesungen) {
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
		Collections.sort(loesungenInSuperPopulation, new LoesungComparatorByTft());
		
		Loesung[] loesungenFuerMatrixBerechnung;
		if(loesungenInSuperPopulation.size()<=Problem.populationsgroesse) {
			aktuellAusgewaehlteLoesung = loesungenInSuperPopulation.get(loesungenInSuperPopulation.size()/2);
			loesungenFuerMatrixBerechnung = new Loesung[loesungenInSuperPopulation.size()];
			for(int i =0; i<loesungenInSuperPopulation.size();i++) {
				loesungenFuerMatrixBerechnung[i]= loesungenInSuperPopulation.get(i);
			}
			
		}
		else {
			Random r = new Random();
			loesungenFuerMatrixBerechnung = new Loesung[Problem.populationsgroesse];
			int high = loesungenInSuperPopulation.size() - ((Problem.populationsgroesse-1)/2);
			int low = (Problem.populationsgroesse-1)/2;
		int random =  r.nextInt(high-low) + low;  
		aktuellAusgewaehlteLoesung = loesungenInSuperPopulation.get(random);
		int index = 0;
		for(int i = random-(Problem.populationsgroesse-1)/2;i< (random+(Problem.populationsgroesse-1)/2)+1;i++) {
			loesungenFuerMatrixBerechnung[index] = loesungenInSuperPopulation.get(i);
			index++;
		}
		}
		berechneMatrix(loesungenFuerMatrixBerechnung);
		Ameise[] ameisen = new Ameise[Problem.anzahlAmeisen];
		Loesung[] loesungen = new Loesung[Problem.anzahlAmeisen];
		for (int i = 0; i < Problem.anzahlAmeisen; i++) {
			ameisen[i] = new Ameise(aktuellAusgewaehlteLoesung);
			loesungen[i] = new Loesung(iterationsanzahl);
		}

		for (int j = 0; j < loesungen.length; j++) {

			for (int i = 0; i < Problem.anzahlJobs; i++) {
				ameisen[j].naechsterKnoten(pheromonmatrix);
			}

			loesungen[j].jobreihenfolge = ameisen[j].getBesuchteKnoten();
		}
			
			if (Problem.lokaleSuche) {
				for (int j = 0; j < loesungen.length; j++) {
					loesungen[j]=lokaleSucheBestChangeFound(loesungen[j], Problem.anzahlLokaleSuche);}
			}
			
			
			
		
		for (int i = 0; i < loesungen.length; i++) {
			testeLoesungFuerSuperpopulation(loesungen[i]);
		}

		
		

			
			



		System.out.println(toString());
		setzeMatrixZurueck(loesungenFuerMatrixBerechnung);
		iterationsanzahl++;
		return aktuellAusgewaehlteLoesung;
	}

	public ArrayList<Loesung> getLoesungenInSuperPopulation() {
		return loesungenInSuperPopulation;
	}

	public void setLoesungenInSuperPopulation(ArrayList<Loesung> loesungenInSuperPopulation) {
		this.loesungenInSuperPopulation = loesungenInSuperPopulation;
	}

	public int testeAufDominierung(Loesung loesung1, Loesung loesung2) {
		int tft1 = loesung1.berechneTFT();
		int tardiness1 = loesung1.getMeanTardiness();
		int tft2 = loesung2.berechneTFT();
		int tardiness2 = loesung2.getMeanTardiness();
		if ((tft1 < tft2) && (tardiness1 <= tardiness2) || (tft1 <= tft2) && (tardiness1 < tardiness2)) {
			return 1;
		}
		if ((tft2 < tft1) && (tardiness2 <= tardiness1) || (tft1 <= tft2) && (tardiness1 < tardiness2) ) {
			return -1;
		}
		return 0;
	}
	
	
	
	
	
	

	public int getTftEliteLoesung() {
		return tftEliteLoesung;
	}

	public void setTftEliteLoesung(int tftEliteLoesung) {
		this.tftEliteLoesung = tftEliteLoesung;
	}

	/*private void wendeLokaleSucheAn(Loesung[] loesungen, int k) {
		for (int l = 0; l < k; l++) {
			if ((l % Problem.anzahlJobs == 0) && (l > 0)) {
				java.util.Collections.shuffle(list);
				java.util.Collections.shuffle(list2);
			}
			//for (int i = 0; i < loesungen.length; i++) {

				// int zufall = (int) (Problem.anzahlJobs * Math.random());

			//	loesungen[i] = lokaleSucheInsertion(loesungen[i], list.get(l % Problem.anzahlJobs));
				// zufall = (int) (Problem.anzahlJobs * Math.random());
			//	loesungen[i] = swapSearch(loesungen[i], list2.get(l % Problem.anzahlJobs));

				// for (int j = 0; j <10; j++) {
				// int zufall = (int) (Problem.anzahlJobs * Math.random());
				// loesungen[i] = swapSearch(loesungen[i], zufall);
				// zufall = (int) (Problem.anzahlJobs * Math.random());
				// }
			//}
			
			for (int a = 0; a < loesungen.length; a++) {
				loesungen[a] = lokaleSucheBestChangeFound(loesungen[a], Problem.anzahlLokaleSuche);
			}
		}
	}
	*/
	private Loesung lokaleSucheBestChangeFound(Loesung loesung, int k) {
			for (int l = 0; l < k; l++) {
				if (l % Problem.anzahlJobs == 0) {
					if ((bessereLoesungGefunden == false) && (l > 1)) {
						return loesung;
					}
					java.util.Collections.shuffle(list);
					java.util.Collections.shuffle(list2);
					bessereLoesungGefunden = false;
				}

				loesung = lokaleSucheInsertion(loesung, list.get(l % Problem.anzahlJobs));
				loesung = swapSearch(loesung, list2.get(l % Problem.anzahlJobs));
			}
		return loesung;
		}
	
	private Loesung lokaleSucheInsertion(Loesung loesung, int index) {
		int besterTft = loesung.berechneTFT();
		int tftTemp = 0;
		int besterTardiness = loesung.getMeanTardiness();
		int TardinessTemp = 0;
		Loesung besteLoesung = loesung;
        Loesung tempLoesung = new Loesung(loesung.getAlter());

		for (int i = 0; i < loesung.getJobreihenfolge().length; i++) {

			tempLoesung = insertJob(loesung, index, i);
			tftTemp = tempLoesung.berechneTFT();
			TardinessTemp = tempLoesung.getMeanTardiness();
			if (((tftTemp < besterTft) && (TardinessTemp <= besterTardiness)) || ((tftTemp <= besterTft) && (TardinessTemp < besterTardiness))) {
			//if (tftTemp < besterTft){
				bessereLoesungGefunden = true;
				besteLoesung = tempLoesung;
				besterTft = tftTemp;

			}

		}
		return besteLoesung;
	}
	
	private Loesung swapSearch(Loesung loesung, int index) {

		int besterTft = loesung.berechneTFT();
		int tftTemp = 0;
		int besterTardiness = loesung.getMeanTardiness();
		int TardinessTemp = 0;
		Loesung besteLoesung = loesung;

		Loesung tempLoesung = new Loesung(loesung.getAlter());

		for (int i = 0; i < loesung.getJobreihenfolge().length; i++) {

			tempLoesung = swapJob(loesung, index, i);
			tftTemp = tempLoesung.berechneTFT();
			TardinessTemp = tempLoesung.getMeanTardiness();
			if (((tftTemp < besterTft) && (TardinessTemp <= besterTardiness)) || ((tftTemp <= besterTft) && (TardinessTemp < besterTardiness))) {
			//if (tftTemp < besterTft){
				bessereLoesungGefunden = true;
				besteLoesung = tempLoesung;
				besterTft = tftTemp;

			}

		}

		return besteLoesung;
	}

	
	

	/*public Loesung lokaleSucheInsertion(Loesung loesung, int index) {
		int besterTft = loesung.berechneTFT();
		int besteTardiness = loesung.getMeanTardiness();
		int tftTemp = 0;
		int tardinessTemp = 0;
		Loesung besteLoesung = loesung;

		Loesung tempLoesung = new Loesung(loesung.getAlter());

		for (int i = 0; i < loesung.getJobreihenfolge().length; i++) {

			tempLoesung = insertJob(loesung, index, i);
			tftTemp = tempLoesung.berechneTFT();
			tardinessTemp = tempLoesung.getMeanTardiness();
			if ((tftTemp < besterTft) && (tardinessTemp < besteTardiness)) {
				besteLoesung = tempLoesung;
				besterTft = tftTemp;
				besteTardiness = tardinessTemp;

			}

		}
		return besteLoesung;
	}
	*/

	public int findeIndexJob(Loesung loesung, int job) {
		for (int i = 0; i < Problem.anzahlJobs; i++) {
			if (loesung.jobreihenfolge[i] == job) {
				return i;
			}
		}
		return 999;
	}

	/*public Loesung swapSearch(Loesung loesung, int index) {

		int besterTft = loesung.berechneTFT();
		int besteTardiness = loesung.getMeanTardiness();
		int tftTemp = 0;
		int tardinessTemp = 0;
		Loesung besteLoesung = loesung;

		Loesung tempLoesung = new Loesung(loesung.getAlter());

		for (int i = 0; i < loesung.getJobreihenfolge().length; i++) {

			tempLoesung = swapJob(loesung, index, i);
			tftTemp = tempLoesung.berechneTFT();
			if ((tftTemp < besterTft) && (tardinessTemp < besteTardiness)) {
				besteLoesung = tempLoesung;
				besterTft = tftTemp;
				besteTardiness = tardinessTemp;

			}

		}

		return besteLoesung;
	}
*/
	public Loesung insertJob(Loesung loesung, int jobPosition, int insertPosition) {
		Loesung loesung2 = new Loesung(loesung.getAlter());
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

	public Loesung swapJob(Loesung loesung, int swapPosition1, int swapPosition2) {
		Loesung loesung2 = new Loesung(loesung.getAlter());
		loesung2.jobreihenfolge = Arrays.copyOf(loesung.getJobreihenfolge(), loesung.getJobreihenfolge().length);
		int job = loesung2.getJobreihenfolge()[swapPosition1];
		loesung2.getJobreihenfolge()[swapPosition1] = loesung2.getJobreihenfolge()[swapPosition2];
		loesung2.getJobreihenfolge()[swapPosition2] = job;
		return loesung2;
	}

	public Loesung wendeLokaleSucheAn2(Loesung loesung) {
		// int jobIndexInit = (int) (Math.random() * Problem.anzahlJobs);
		// int insertIndexInit = (int) (Math.random() * Problem.anzahlJobs);
		Loesung veraenderteLoesung = new Loesung(loesung.getAlter());
		// veraenderteLoesung = insertJob(loesung, jobIndexInit, insertIndexInit);
		int loesungsguete = loesung.berechneTFT();
		for (int i = 0; i < Problem.anzahlLokaleSuche; i++) {
			int jobIndex = (int) (Math.random() * Problem.anzahlJobs);
			int insertIndex = (int) (Math.random() * Problem.anzahlJobs);

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

}