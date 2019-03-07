package antcolopt;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.text.ElementIterator;

import org.omg.Messaging.SyncScopeHelper;

public class Main {

	private static final String PFAD_ERGEBNISSE_ZUSAMMENFASSUNG = "./logs/zusammenfassung_ergebnisse.txt";
	private static final String PFAD_LOGS = "./logs/logs.txt";
	private static final int MITTELUNG_ANZAHL = 10;
	// Problemklassen von 0-8
	private static final int PROBLEMKLASSEN_STARTWERT = 3;
	private static final int PROBLEMKLASSEN_ENDWERT = 6;
	// Probleminstanzen von 0-9
	private static final int PROBLEMINSTANZEN_STARTWERT =2;
	private static final int PROBLEMINSTANZEN_ENDWERT = 2;
	// Wartekapazitaeten von 1-4
	private static final int WARTEKAPAZITAETEN_STARTWERT = 2;
	private static final int WARTEKAPAZITAETEN_ENDWERT = 2;
	int zeitheuristikloesung;
	private static int iterationsanzahlMax = 0;
	private static int timeStamp;
	private static int[] gemittelteErgebnisse = new int[1010];
	private static List<Integer> gemittelteErgebnisseList = new ArrayList<Integer>();
	private static Loesung eliteLoesung;

	private static int logCounter;

	public static void main(String[] args) {

		try {
			FileWriter fw = new FileWriter(PFAD_LOGS);
			BufferedWriter logWriter = new BufferedWriter(fw);
			FileWriter fw2 = new FileWriter(PFAD_ERGEBNISSE_ZUSAMMENFASSUNG);
			BufferedWriter ergebnisWriter = new BufferedWriter(fw2);
			schreibeKopfzeileFuerLogs(logWriter, ergebnisWriter);
			int[] besteTfts = new int[10];
			for (int problemklasse = PROBLEMKLASSEN_STARTWERT; problemklasse < PROBLEMKLASSEN_ENDWERT
					+ 1; problemklasse++) {

				// Ausnahme

				Reader reader = aktualisiereProblemklassenSpezifischeKonstanten(problemklasse);
				System.out.println("Starte Berechnung");
				for (int probleminstanz = PROBLEMINSTANZEN_STARTWERT; probleminstanz < PROBLEMINSTANZEN_ENDWERT
						+ 1; probleminstanz++) {
					for (int wartekapazitaet = WARTEKAPAZITAETEN_STARTWERT; wartekapazitaet < WARTEKAPAZITAETEN_ENDWERT
							+ 1; wartekapazitaet++) {
						KonstantenUndHelper.wartekapazitaet = wartekapazitaet;
						for (int h = 1; h < 2; h++) {
							for(int p = 4; p < 7; p++) {

							KonstantenUndHelper.anzahlLokaleSuche = 5
									* KonstantenUndHelper.ANZAHL_JOBS_PROBLEMKLASSEN[problemklasse];

						//	KonstantenUndHelper.wartekapazitaet = ((problemklasse + 1) % 4) + 1;
							Loesung heuristikLoesung = KonstantenUndHelper.generiereTftHeristikLoesung(
									KonstantenUndHelper.ANZAHL_JOBS_PROBLEMKLASSEN[problemklasse],
									KonstantenUndHelper.ANZAHL_MASCHINEN_PROBLEMKLASSEN[problemklasse],
									reader.getAusfuehrungszeiten()[probleminstanz]);

							// KonstantenUndHelper.wartekapazitaet = wartekapazitaet;
						
							KonstantenUndHelper.pheromon_inital = 1.0
									/ (double) KonstantenUndHelper.ANZAHL_JOBS_PROBLEMKLASSEN[problemklasse];
							KonstantenUndHelper.PHEROMON_MAX = 0.3 + p * 0.4;
							KonstantenUndHelper.ELITE_ANTEIL = 0.25 + h * 0.25;

							// logWriter.write("\n");
							
								
							KonstantenUndHelper.PHEROMON_UPDATE_MENGE = (1 - KonstantenUndHelper.ELITE_ANTEIL)
									* (KonstantenUndHelper.PHEROMON_MAX - KonstantenUndHelper.pheromon_inital)
									/ (double) KonstantenUndHelper.POPULATIONSGROESSE;
							KonstantenUndHelper.elite_pheromonzuwachs = KonstantenUndHelper.ELITE_ANTEIL
									* (KonstantenUndHelper.PHEROMON_MAX - KonstantenUndHelper.pheromon_inital);
							for (int mittelungsInstanz = 0; mittelungsInstanz < MITTELUNG_ANZAHL; mittelungsInstanz++) {
								logCounter = 0;
								timeStamp = 0;
								// logWriter.write(KonstantenUndHelper.ANZAHL_JOBS_PROBLEMKLASSEN[problemklasse]
								// + " Jobs; " +
								// KonstantenUndHelper.ANZAHL_MASCHINEN_PROBLEMKLASSEN[problemklasse]
								// + " Maschinen; " + mittelungsInstanz + " MittelungsInstanz");
								// logWriter.write("\n");
								berechneLoesung(logWriter, besteTfts, problemklasse, reader, probleminstanz,
										heuristikLoesung, mittelungsInstanz);

							}
							// logWriter.write("\n");
							int summe = 0;
							for (int tft : besteTfts) {
								summe = summe + tft;
							}
							berechneRPDundSchreibeErgebnisse(ergebnisWriter, problemklasse, probleminstanz,
									KonstantenUndHelper.wartekapazitaet, summe);
						}
						}
					}
				}
			}
			logWriter.close();
			ergebnisWriter.close();
			System.out.println("Berechnung beendet");
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	private static void berechneLoesung(BufferedWriter logWriter, int[] besteBekannteTFTs, int problemklasse,
			Reader reader, int probleminstanz, Loesung heuristikLoesung, int mittelungsInstanz) throws IOException {
		Population population = new Population(KonstantenUndHelper.ANZAHL_JOBS_PROBLEMKLASSEN[problemklasse],
				KonstantenUndHelper.ANZAHL_MASCHINEN_PROBLEMKLASSEN[problemklasse],
				reader.getAusfuehrungszeiten()[probleminstanz], heuristikLoesung);
		eliteLoesung = population.getEliteloesung();
		boolean weiterRechnen = true;
		final long timeStart = System.currentTimeMillis();
		while (weiterRechnen) {
			weiterRechnen = berechneIterationUndMesseZeit(logWriter, problemklasse, population, timeStart);
		}
		iterationsanzahlMax = population.getIterationsanzahl();
		// String s = "";
		// for (int k = 0; k <
		// KonstantenUndHelper.ANZAHL_JOBS_PROBLEMKLASSEN[problemklasse]; k++) {
		// s += population.getEliteloesung().getJobreihenfolge()[k] + "; ";
		// }
		// logWriter.write(s);
		besteBekannteTFTs[mittelungsInstanz] = population.getTftEliteLoesung();
	}

	private static boolean berechneIterationUndMesseZeit(BufferedWriter logWriter, int problemklasse,
			Population population, final long timeStart) throws IOException {
		boolean weiterRechnen = true;
		population.berechneNeueIteration();
		final long timeEnd = System.currentTimeMillis();
		long rechenZeit = timeEnd - timeStart;
		// logWriter.write(population.getIterationsanzahl() + "; " + (timeEnd -
		// timeStart) + "; "
		// + population.getEliteloesung().berechneTFT());
		// logWriter.write("\n");
		if ((rechenZeit > timeStamp)
				&& (rechenZeit < KonstantenUndHelper.BERECHNUNGSZEITEN_PROBLEMKLASSEN[problemklasse])) {
			if (gemittelteErgebnisseList.size() <= logCounter) {
				// im ersten Lauf sind noch keinen Werte enthalten daher erst einmal Lösung
				// setzen
				gemittelteErgebnisseList.add(population.getTftEliteLoesung());
			} else {
				// danach sind Werte enthalten also aufaddieren
				gemittelteErgebnisseList.set(logCounter,
						population.getTftEliteLoesung() + gemittelteErgebnisseList.get(logCounter));
			}

			logCounter++;

			// logWriter.write(population.getIterationsanzahl() + "; " + (timeEnd -
			// timeStart) + "; "
			// + population.getTftEliteLoesung());
			// logWriter.write("\n");
			timeStamp = timeStamp + KonstantenUndHelper.BERECHNUNGSZEITEN_PROBLEMKLASSEN[problemklasse] / 100;
		}
		if (rechenZeit > KonstantenUndHelper.BERECHNUNGSZEITEN_PROBLEMKLASSEN[problemklasse]) {

			weiterRechnen = false;
			if (population.getEliteloesung().berechneTFT() < eliteLoesung.berechneTFT()) {
				eliteLoesung = population.getEliteloesung();
			}

		}
		return weiterRechnen;
	}

	private static void berechneRPDundSchreibeErgebnisse(BufferedWriter ergebnisWriter, int problemklasse,
			int probleminstanz, int wartekapazitaet, int aufsummierteTFTs) throws IOException {
		try {
			FileWriter fw2 = new FileWriter("./logs/" + KonstantenUndHelper.ANZAHL_JOBS_PROBLEMKLASSEN[problemklasse]
					+ "_" + KonstantenUndHelper.ANZAHL_MASCHINEN_PROBLEMKLASSEN[problemklasse] + "_" + probleminstanz
					+ "_" + KonstantenUndHelper.wartekapazitaet + "_" + KonstantenUndHelper.ELITE_ANTEIL + "_" + KonstantenUndHelper.PHEROMON_MAX + "_"
					+ KonstantenUndHelper.anzahlLokaleSuche + "_" + "logs.txt");
			BufferedWriter logWriter2 = new BufferedWriter(fw2);
			for (int i = 0; i < gemittelteErgebnisseList.size(); i++) {
				logWriter2.write("" + (gemittelteErgebnisseList.get(i) / 10));
				logWriter2.newLine();

			}
			String s = "";
			for (int i = 0; i < eliteLoesung.getJobreihenfolge().length; i++) {
				s += eliteLoesung.getJobreihenfolge()[i] + ", ";
			}
			
			logWriter2.write(s);
			logWriter2.close();
			// Arrays.fill(gemittelteErgebnisse, 0);
			gemittelteErgebnisseList.clear();
		} catch (IOException e) {
			e.printStackTrace();
		}
		double gemitteltertft = (aufsummierteTFTs / MITTELUNG_ANZAHL);
		double besterBekannterTft = KonstantenUndHelper.bestWerteTft[problemklasse * 10
				+ probleminstanz][wartekapazitaet - 1];
		// Berechnung des relative percentage deviation (RPD)
		double rpd = ((gemitteltertft - besterBekannterTft) / besterBekannterTft) * 100;
		ergebnisWriter.write(KonstantenUndHelper.anzahlLokaleSuche + ";" + KonstantenUndHelper.ELITE_ANTEIL + ";" + KonstantenUndHelper.PHEROMON_MAX + ";"
				+ KonstantenUndHelper.ANZAHL_JOBS_PROBLEMKLASSEN[problemklasse] + ";"
				+ KonstantenUndHelper.ANZAHL_MASCHINEN_PROBLEMKLASSEN[problemklasse] + ";" + (probleminstanz + 1) + ";"
				+ wartekapazitaet + ";" + iterationsanzahlMax + ";" + rpd);
		ergebnisWriter.write("\n");
	}

	private static void schreibeKopfzeileFuerLogs(BufferedWriter logWriter, BufferedWriter ergebnisWriter)
			throws IOException {
		ergebnisWriter.write("LokaleSuche;EliteAnteil;Pheromon_Max;Jobs;Maschinen;Instanz;Wartekapazitaet;Iterationen;RPD");
		ergebnisWriter.write("\n");
		// logWriter.write("Iteration;Zeit;besterTFT");
		// logWriter.write("\n");
	}

	/**
	 * Aktualisiert Parameter je nach Job und Maschinenanzahl
	 * 
	 * @param problemklasse Problemklasse
	 * @return Schreibobjekt
	 */
	private static Reader aktualisiereProblemklassenSpezifischeKonstanten(int problemklasse) {
		KonstantenUndHelper.eliteUpdateGewicht = KonstantenUndHelper.ANZAHL_JOBS_PROBLEMKLASSEN[problemklasse] / 20.0;
		KonstantenUndHelper.anzahlLokaleSuche = KonstantenUndHelper.ANZAHL_JOBS_PROBLEMKLASSEN[problemklasse] * 4;
		String dateiName = KonstantenUndHelper.DATEIPFAD_PROBLEMKLASSEN[problemklasse];
		Reader reader = new Reader(KonstantenUndHelper.ANZAHL_JOBS_PROBLEMKLASSEN[problemklasse],
				KonstantenUndHelper.ANZAHL_MASCHINEN_PROBLEMKLASSEN[problemklasse]);
		reader.ladeProbleminstanzen(dateiName);
		reader.ladeBestwerteTFT(KonstantenUndHelper.DATEIPFAD_BESTWERTE_TFT);
		return reader;
	}

}
