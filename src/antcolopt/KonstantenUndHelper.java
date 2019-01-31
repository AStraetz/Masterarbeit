package antcolopt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class KonstantenUndHelper {


	public static final int ANZAHL_AMEISEN = 5;
	public static final boolean VERWENDE_LOKALESUCHE = true;
	public static double[] gesamtBearbeitungsZeitJobs;
	public static final double ALPHA = 1.0;
	public static final double BETA = 0;
	public static final boolean VERWENDE_NACHBARSCHAFTSREGEL = true;
	public static final String[] DATEIPFAD_PROBLEMKLASSEN = { "D:\\tailard\\tai20_5.txt", "D:\\tailard\\tai20_10.txt",
			"D:\\tailard\\tai20_20.txt", "D:\\tailard\\tai50_5.txt", "D:\\tailard\\tai50_10.txt",
			"D:\\tailard\\tai50_20.txt", "D:\\tailard\\tai100_5.txt", "D:\\tailard\\tai100_10.txt",
			"D:\\tailard\\tai100_20.txt" };
	public static final double q0 = 0.90;
	public static final int POPULATIONSGROESSE = 3;
	public static final boolean VERWENDE_SUMMENREGEL = false;
	public static final boolean BENUTZE_q0_REGEL = true;
	public static final int[] BERECHNUNGSZEITEN_PROBLEMKLASSEN = { 3000, 6000, 12000, 37500, 75000, 150000, 150000,
			300000, 600000 };
	public static final int[] ANZAHL_JOBS_PROBLEMKLASSEN = { 20, 20, 20, 50, 50, 50, 100, 100, 100 };
	public static final int[] ANZAHL_MASCHINEN_PROBLEMKLASSEN = { 5, 10, 20, 5, 10, 20, 5, 10, 20 };
	public static final String DATEIPFAD_BESTWERTE_TFT = "D:\\tailard\\Bestwerte_TFT.csv";
	public static final int NACHBARSCHAFT_GROESSE = 5;
	public static final int SPALTENANZAHL_AUS_BESTWERTE_TFT = 4;
	// Konstanten, die sich nur ändern, wenn mehrere Problemklassen gleichzeitig
	// getestet werden
	static double updategewicht = 0.5;
	static int wartekapazitaet = 1;
	static double eliteUpdateGewicht = 1;
	static int anzahlLokaleSuche = 0;
	static double[][] bestWerteTft = new double[90][4];

	/**
	 * Berechnet die Jobreihenfolge mit Hilfe der Rajendran Heuristik
	 * 
	 * @param anzahlJobs         Anzahl der zu optimierenden Jobs
	 * @param anzahlMaschinen    Anzahl der zu optimierenden Maschinen
	 * @param ausfuehrungszeiten Bearbeitungszeiten der Jobs pro Maschine
	 * @return Loesung, die mit Hilfe der Rajendran Heuristik berechnet wurde
	 */
	public static Loesung generiereTftHeristikLoesung(int anzahlJobs, int anzahlMaschinen, int[][] ausfuehrungszeiten) {
		Loesung loesung = new Loesung(0, anzahlJobs, anzahlMaschinen, ausfuehrungszeiten);
		Job[] jobs = berechneHeuristikWerte(anzahlJobs, anzahlMaschinen, ausfuehrungszeiten);
		List<Integer> jobreihenfolge = new ArrayList<Integer>();
		jobreihenfolge.add(jobs[0].getNummer());
		for (int i = 1; i < anzahlJobs; i++) {
			int indexbesteLoesung = 0;
			int bestertft = Integer.MAX_VALUE;
			for (int k = (i + 1) / 2; k <= i + 1; k++) {
				jobreihenfolge.add(k - 1, jobs[i].getNummer());
				int[] jobreihenfolgeArray = jobreihenfolge.stream().mapToInt(z -> z).toArray();
				loesung.setJobreihenfolge(jobreihenfolgeArray);
				int tftaktuell = loesung.berechneTFT();
				if (tftaktuell < bestertft) {
					indexbesteLoesung = k - 1;
					bestertft = tftaktuell;
				}
				jobreihenfolge.remove((Integer) jobs[i].getNummer());
			}
			jobreihenfolge.add(indexbesteLoesung, jobs[i].getNummer());
		}
		int[] jobreihenfolgeArray = jobreihenfolge.stream().mapToInt(z -> z).toArray();
		loesung.setJobreihenfolge(jobreihenfolgeArray);
		return loesung;
	}

	/** Berechnet Heuristikwerte für initiale Loesung
	 * 
	 * @param anzahlJobs         Anzahl der zu optimierenden Jobs
	 * @param anzahlMaschinen    Anzahl der zu optimierenden Maschinen
	 * @param ausfuehrungszeiten Bearbeitungszeiten der Jobs pro Maschine
	 * @return Werte, die angeben, wie früh ein Job gescheduled werden sollte
	 */
	private static Job[] berechneHeuristikWerte(int anzahlJobs, int anzahlMaschinen, int[][] ausfuehrungszeiten) {
		Job[] jobs = new Job[anzahlJobs];
		for (int i = 0; i < jobs.length; i++) {
			jobs[i] = new Job(i, 0, 0);
		}
		for (int i = 0; i < jobs.length; i++) {
			jobs[i].setHeuristikwert(0);
			for (int j = 1; j < anzahlMaschinen + 1; j++) {
				jobs[i].setHeuristikwert(
						jobs[i].getHeuristikwert() + (anzahlMaschinen - j + 1) * ausfuehrungszeiten[j - 1][i]);
				jobs[i].setHeuristikwert2(jobs[i].getHeuristikwert2() + ausfuehrungszeiten[j - 1][i]);
			}
		}
		Arrays.sort(jobs);
		return jobs;
	}

	/*
	 * public static void berechneGesamtBearbeitungsZeitJobs(int anzahlJobs, int
	 * anzahlMaschinen, int[][][] ausfuehrungszeiten) { gesamtBearbeitungsZeitJobs =
	 * new double[anzahlJobs]; for (int i = 0; i > anzahlJobs; i++) {
	 * gesamtBearbeitungsZeitJobs[i] = 0; }
	 * 
	 * for (int i = 0; i < anzahlJobs; i++) { for (int j = 0; j < anzahlMaschinen;
	 * j++) { gesamtBearbeitungsZeitJobs[i] = gesamtBearbeitungsZeitJobs[i] +
	 * ausfuehrungszeiten[probleminstanz][j][i]; }
	 * 
	 * } }
	 */
}
