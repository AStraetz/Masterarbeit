package antcolopt;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class Main {

	private static final String PFAD_ERGEBNISSE_ZUSAMMENFASSUNG = "./logs/zusammenfassung_ergebnisse.txt";
	private static final String PFAD_LOGS = "./logs/logs.txt";
	private static final int MITTELUNG_ANZAHL = 10;
	// Problemklassen von 0-8
	private static final int PROBLEMKLASSEN_STARTWERT = 0;
	private static final int PROBLEMKLASSEN_ENDWERT = 9;
	// Probleminstanzen von 0-9
	private static final int PROBLEMINSTANZEN_STARTWERT = 0;
	private static final int PROBLEMINSTANZEN_ENDWERT = 1;
	// Wartekapazitaeten von 1-4
	private static final int WARTEKAPAZITAETEN_STARTWERT = 3;
	private static final int WARTEKAPAZITAETEN_ENDWERT = 4;

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
				Reader reader = aktualisiereProblemklassenSpezifischeKonstanten(problemklasse);
				for (int probleminstanz = PROBLEMINSTANZEN_STARTWERT; probleminstanz < PROBLEMINSTANZEN_ENDWERT
						+ 1; probleminstanz++) {
					for (int wartekapazitaet = WARTEKAPAZITAETEN_STARTWERT; wartekapazitaet < WARTEKAPAZITAETEN_ENDWERT
							+ 1; wartekapazitaet++) {
						Loesung heuristikLoesung = KonstantenUndHelper.generiereTftHeristikLoesung(
								KonstantenUndHelper.ANZAHL_JOBS_PROBLEMKLASSEN[problemklasse],
								KonstantenUndHelper.ANZAHL_MASCHINEN_PROBLEMKLASSEN[problemklasse],
								reader.getAusfuehrungszeiten()[probleminstanz]);
						KonstantenUndHelper.wartekapazitaet = wartekapazitaet;
						for (int l = 0; l < MITTELUNG_ANZAHL; l++) {
							berechneLoesung(logWriter, besteTfts, problemklasse, reader, probleminstanz, heuristikLoesung, l);
						}
						logWriter.write("\n");
						int summe = 0;
						for (int tft : besteTfts) {
							summe = summe + tft;
						}
                    berechneRPDundSchreibeErgebnisse(ergebnisWriter, problemklasse, probleminstanz, wartekapazitaet, summe);
	}
				}
			}
			logWriter.close();
			ergebnisWriter.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	private static void berechneLoesung(BufferedWriter logWriter, int[] besteBekannteTFTs, int problemklasse, Reader reader, int probleminstanz,
			Loesung heuristikLoesung, int mittelungsInstanz) throws IOException {
		Population population = new Population(KonstantenUndHelper.ANZAHL_JOBS_PROBLEMKLASSEN[problemklasse],
				KonstantenUndHelper.ANZAHL_MASCHINEN_PROBLEMKLASSEN[problemklasse], reader.getAusfuehrungszeiten()[probleminstanz],
				heuristikLoesung);
		final long timeStart = System.currentTimeMillis();
		boolean weiterRechnen = true;
		while (weiterRechnen) {
			weiterRechnen = berechneIterationUndMesseZeit(logWriter, problemklasse, population, timeStart);
		}
		String s = "";
		for (int k = 0; k < KonstantenUndHelper.ANZAHL_JOBS_PROBLEMKLASSEN[problemklasse]; k++) {
			s += population.getEliteloesung().getJobreihenfolge()[k] + ", ";
		}
		logWriter.write(s);
		besteBekannteTFTs[mittelungsInstanz] = population.getTftEliteLoesung();
	}

	private static boolean berechneIterationUndMesseZeit(BufferedWriter logWriter, int problemklasse, Population population,
			final long timeStart) throws IOException {
		boolean weiterRechnen = true;
		population.berechneNeueIteration();
		final long timeEnd = System.currentTimeMillis();
		logWriter.write(population.getIterationsanzahl() + "; " + (timeEnd - timeStart) + "; "
				+ population.getEliteloesung().berechneTFT());
		logWriter.write("\n");
		if ((timeEnd - timeStart) > KonstantenUndHelper.BERECHNUNGSZEITEN_PROBLEMKLASSEN[problemklasse]) {
			weiterRechnen = false;
		}
		return weiterRechnen;
	}

	private static void berechneRPDundSchreibeErgebnisse(BufferedWriter ergebnisWriter, int problemklasse, int probleminstanz, int wartekapazitaet, int aufsummierteTFTs)
			throws IOException {
		double gemitteltertft = (aufsummierteTFTs / MITTELUNG_ANZAHL);
		double besterBekannterTft = KonstantenUndHelper.bestWerteTft[problemklasse * 10 + probleminstanz][wartekapazitaet - 1];
		// Berechnung des relative percentage deviation (RPD)
		double rpd = ((gemitteltertft - besterBekannterTft) / besterBekannterTft) * 100;
		ergebnisWriter.write(KonstantenUndHelper.anzahlLokaleSuche + ";" + KonstantenUndHelper.eliteUpdateGewicht + ";"
				+ KonstantenUndHelper.ANZAHL_JOBS_PROBLEMKLASSEN[problemklasse] + ";"
				+ KonstantenUndHelper.ANZAHL_MASCHINEN_PROBLEMKLASSEN[problemklasse] + ";" + (probleminstanz + 1) + ";" + wartekapazitaet + ";" + rpd);
		ergebnisWriter.write("\n");
	}

	private static void schreibeKopfzeileFuerLogs(BufferedWriter logWriter, BufferedWriter ergebnisWriter) throws IOException {
		ergebnisWriter.write("LokaleSuche;Elitegewicht;Jobs;Maschinen;Instanz;Wartekapazitaet;RPD");
		ergebnisWriter.write("\n");
		logWriter.write("Iteration;Zeit;besterTFT");
		logWriter.write("\n");
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
