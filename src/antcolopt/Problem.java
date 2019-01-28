package antcolopt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class Problem {

	
	static int anzahlAmeisen = 5;
	static int populationsgroesse = 3;
	static double updategewicht = 0.5;
	public static double[] gesamtBearbeitungsZeitJobs;
	static int wartekapazitaet = 1;
	static double eliteUpdateGewicht = 1;
	static double alpha = 1.0;
	static double beta = 0;
	static ArrayList<int[][]> problemliste = new ArrayList<int[][]>();
	static int probleminstanz = 0;
	static boolean lokaleSuche = true;
	static String[] dateiname = {"D:\\tailard\\tai20_5.txt","D:\\tailard\\tai20_10.txt","D:\\tailard\\tai20_20.txt","D:\\tailard\\tai50_5.txt","D:\\tailard\\tai50_10.txt","D:\\tailard\\tai50_20.txt","D:\\tailard\\tai100_5.txt","D:\\tailard\\tai100_10.txt","D:\\tailard\\tai100_20.txt"};
	static boolean summenregel = false;
	static boolean nachbarschaftsregel = true;
	static double q0 = 0.90;
	static boolean benutzeq0= true;
	static int[] berechnungszeit = {3000,6000,12000,37500,75000,150000,150000,300000,600000};
	static int[] anzahlJobsArray = {20,20,20,50,50,50,100,100,100};
	static int[] anzahlMaschinenArray = {5,10,20,5,10,20,5,10,20};
	static int anzahlLokaleSuche = 0;
	static double[][] bestWerteTft = new double[90][4];
	static String dateiBewerteTft = "D:\\tailard\\Bestwerte_TFT.csv";
	static int nachbarschaftsGroesse = 5;
	
	
	

	public static double getBeta() {
		return beta;
	}

	public static void setBeta(double beta) {
		Problem.beta = beta;
	}

	public static void berechneGesamtBearbeitungsZeitJobs(int anzahlJobs, int anzahlMaschinen, int[][][] ausfuehrungszeiten) {
		gesamtBearbeitungsZeitJobs = new double[anzahlJobs];
		for (int i = 0; i > anzahlJobs; i++) {
			gesamtBearbeitungsZeitJobs[i] = 0;
		}

		for (int i = 0; i < anzahlJobs; i++) {
			for (int j = 0; j < anzahlMaschinen; j++) {
				gesamtBearbeitungsZeitJobs[i] = gesamtBearbeitungsZeitJobs[i]
						+ ausfuehrungszeiten[probleminstanz][j][i];
			}

		}
	}

	public static Loesung generiereTftHeristikLoesung(int anzahlJobs,int anzahlMaschinen, int[][] ausfuehrungszeiten) {
		Loesung loesung = new Loesung(0,anzahlJobs,anzahlMaschinen,ausfuehrungszeiten);
		// List<Job> jobliste = new ArrayList<Job>();
		Job[] jobs = berechneHeuristikWerte(anzahlJobs,anzahlMaschinen,ausfuehrungszeiten);
		List<Integer> jobreihenfolge = new ArrayList<Integer>();
		jobreihenfolge.add(jobs[0].getNummer());

		for(int i = 1; i<anzahlJobs;i++) {
			int indexbesteLoesung = 0;
			int bestertft = Integer.MAX_VALUE;
			for(int k = (i+1)/2;k<=i+1;k++) {
				
				
				jobreihenfolge.add(k-1, jobs[i].getNummer());
			    int[] jobreihenfolgeArray = jobreihenfolge.stream().mapToInt(z->z).toArray();
				loesung.setJobreihenfolge(jobreihenfolgeArray);
				int tftaktuell = loesung.berechneTFT();
				if(tftaktuell  < bestertft)
				{indexbesteLoesung = k-1;
				bestertft = tftaktuell ;
				}
				jobreihenfolge.remove((Integer) jobs[i].getNummer());
				
			}
			jobreihenfolge.add(indexbesteLoesung, jobs[i].getNummer());
		}
			 int[] jobreihenfolgeArray = jobreihenfolge.stream().mapToInt(z->z).toArray();
			 loesung.setJobreihenfolge(jobreihenfolgeArray);
		
			
			
		
		return loesung;
	}

	private static Job[] berechneHeuristikWerte(int anzahlJobs, int anzahlMaschinen, int[][] ausfuehrungszeiten) {
		Job[] jobs = new Job[anzahlJobs];
		for (int i = 0; i < jobs.length; i++) {
			jobs[i] = new Job(i, 0, 0);
		}
		for (int i = 0; i < jobs.length; i++) {
			jobs[i].setHeuristikwert(0);
			for (int j = 1; j < anzahlMaschinen + 1; j++) {
				jobs[i].setHeuristikwert(jobs[i].getHeuristikwert()
						+ (anzahlMaschinen - j + 1) * ausfuehrungszeiten[j - 1][i]);
				jobs[i].setHeuristikwert2(jobs[i].getHeuristikwert2() + ausfuehrungszeiten[j - 1][i]);
			}
		}
		Arrays.sort(jobs);
		return jobs;
	}

	public static final int SPALTENANZAHL_AUS_BESTWERTE_TFT = 4;
	
	

}
