package ant_col_opt_2crit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;




public class Problem {

	static int anzahlJobs = 20;
	static int anzahlMaschinen = 5;
	static int anzahlAmeisen = 1;
	static int populationsgroesse = 3;
	static double updategewicht = 1;
	public static double[] gesamtBearbeitungsZeitJobs;
	static int wartekapazitaet = 1;
	static double eliteUpdateGewicht = 1;
	static double alpha = 1.0;
	static double beta = 0;
	public static int[][][] ausfuehrungszeiten0 = new int[10][anzahlMaschinen][anzahlJobs];
	static ArrayList<int[][]> problemliste = new ArrayList<int[][]>();
	static int probleminstanz = 0;
	static boolean lokaleSuche = true;
	static String[] dateiname = {"D:\\tailard\\tai20_5.txt","D:\\tailard\\tai20_10.txt","D:\\tailard\\tai20_20.txt","D:\\tailard\\tai50_5.txt","D:\\tailard\\tai50_10.txt","D:\\tailard\\tai50_20.txt","D:\\tailard\\tai100_5.txt","D:\\tailard\\tai100_10.txt","D:\\tailard\\tai100_20.txt"};
	static boolean summenregel = false;
	static boolean nachbarschaftsregel = false;
	static double q0 = 0.9;
	static boolean benutzeq0= false;
	static int[] berechnungszeit = {6000,12000,24000,37500,75000,150000,150000,300000,600000};
	static int[] anzahlJobsArray = {20,20,20,50,50,50,100,100,100};
	static int[] anzahlMaschinenArray = {5,10,20,5,10,20,5,10,20};
	static int anzahlLokaleSuche = 10000;
	static double[][] bestWerteTft = new double[90][4];
	static String dateiBewerteTft = "D:\\tailard\\Bestwerte_TFT.csv";
	static int nachbarschaftsGroesse = 5;
	static int[][][][] dueDates = new int[9][10][4][100];
	static int Problemklasse = 0;
	static int[][] makespanBestwerte = new int[9][10];
	static boolean nachTftOderTardinessSortieren = true;
	public static double PHEROMON_MAX = 20.0;
	public static double ELITE_ANTEIL = 0.25;
	public static double elite_pheromonzuwachs;
	public static double pheromon_inital = 1.0/anzahlJobs;
	static double PHEROMON_UPDATE_MENGE;
	

	public static double getBeta() {
		return beta;
	}

	public static void setBeta(double beta) {
		Problem.beta = beta;
	}

	public static void berechneGesamtBearbeitungsZeitJobs() {
		gesamtBearbeitungsZeitJobs = new double[Problem.anzahlJobs];
		for (int i = 0; i > Problem.anzahlJobs; i++) {
			gesamtBearbeitungsZeitJobs[i] = 0;
		}

		for (int i = 0; i < anzahlJobs; i++) {
			for (int j = 0; j < anzahlMaschinen; j++) {
				gesamtBearbeitungsZeitJobs[i] = gesamtBearbeitungsZeitJobs[i]
						+ ausfuehrungszeiten0[probleminstanz][j][i];
			}

		}
	}

	public static Loesung generiereTftHeristikLoesung() {
		Loesung loesung = new Loesung(0);
		// List<Job> jobliste = new ArrayList<Job>();
		Job[] jobs = berechneHeuristikWerte();
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

	private static Job[] berechneHeuristikWerte() {
		Job[] jobs = new Job[anzahlJobs];
		for (int i = 0; i < jobs.length; i++) {
			jobs[i] = new Job(i, 0, 0);
		}
		for (int i = 0; i < jobs.length; i++) {
			jobs[i].setHeuristikwert(0);
			for (int j = 1; j < anzahlMaschinen + 1; j++) {
				jobs[i].setHeuristikwert(jobs[i].getHeuristikwert()
						+ (anzahlMaschinen - j + 1) * ausfuehrungszeiten0[probleminstanz][j - 1][i]);
				jobs[i].setHeuristikwert2(jobs[i].getHeuristikwert2() + ausfuehrungszeiten0[probleminstanz][j - 1][i]);
			}
		}
		Arrays.sort(jobs);
		return jobs;
	}
	
	static void shuffleArray(int[] ar)
	  {

	    Random rnd = ThreadLocalRandom.current();
	    for (int i = ar.length - 1; i > 0; i--)
	    {
	      int index = rnd.nextInt(i + 1);
	      int a = ar[index];
	      ar[index] = ar[i];
	      ar[i] = a;
	    }
	  }
	
	static Loesung generiereZufaelligeLoesung(int jobanzahl) {
		Loesung zufaelligeLoesung = new Loesung(0);
		for(int i = 0;i<jobanzahl;i++) {
			zufaelligeLoesung.jobreihenfolge[i] = i;		
		}
		shuffleArray(zufaelligeLoesung.jobreihenfolge);
		return zufaelligeLoesung;
	}
	

	
	static void berechneDueDates() {
		Loesung loesung = generiereZufaelligeLoesung(Problem.anzahlJobs);
		int[] completionTimes = loesung.berechneCompletionTimeAllerJobs();
		//dueDates[Problemklasse][probleminstanz][wartekapazitaet-1] = loesung.berechneCompletionTimeAllerJobs();
		for(int i = 0;i<Problem.anzahlJobs;i++) {
			Random r = new Random();
			int zufaelligerVersatz = r.nextInt(makespanBestwerte[Problemklasse][probleminstanz]*2/10+1) - makespanBestwerte[Problemklasse][probleminstanz]/10;
			//System.out.println("makespan: " + makespanBestwerte[Problemklasse][probleminstanz]);
			//System.out.println("versatz: " + zufaelligerVersatz);
			dueDates[Problemklasse][probleminstanz][wartekapazitaet-1][loesung.getJobreihenfolge()[i]] = (int) (completionTimes[i] + zufaelligerVersatz ) ;
		//System.out.println(dueDatesAllerProblemklassen[Problemklasse][probleminstanz][wartekapazitaet-1][i] + ", ");
		}
		//System.out.println("");
	}

	public int getAnzahlJobs() {
		return anzahlJobs;
	}

	public void setAnzahlJobs(int anzahlJobs) {
		this.anzahlJobs = anzahlJobs;
	}

	public int getAnzahlMaschinen() {
		return anzahlMaschinen;
	}

	public void setAnzahlMaschinen(int anzahlMaschinen) {
		this.anzahlMaschinen = anzahlMaschinen;
	}

	public int[][] getAusfuehrungszeiten() {
		return ausfuehrungszeiten0[probleminstanz];
	}

	public void setAusfuehrungszeiten(int[][] ausfuehrungszeiten) {
		this.ausfuehrungszeiten0[probleminstanz] = ausfuehrungszeiten;
	}

}
