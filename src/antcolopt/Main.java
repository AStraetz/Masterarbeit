package antcolopt;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Main {

	private static final int MAXIMALE_ANZAHL_ITERATIONEN = 10000;
	private static final int MITTELUNG_ANZAHL = 10;
	private static final int WARTEKAPAZITAETEN_ENDWERT = 3;
	private static final int WARTEKAPAZITAETEN_STARTWERT = 2;
	private static final int PROBLEMINSTANZEN_ENDWERT = 10;
	private static final int PROBLEMINSTANZEN_STARTWERT = 5;
	private static final int PROBLEMKLASSEN_ENDWERT = 5;
	private static final int PROBLEMKLASSEN_STARTWERT = 0;

	public static void main(String[] args) {
		


		try {
			FileWriter fw = new FileWriter("D:\\QuickB3n\\logs\\ausgabe.txt");
			BufferedWriter bw = new BufferedWriter(fw);
			FileWriter fw2 = new FileWriter("D:\\QuickB3n\\logs\\ausgabe_ergebnisse.txt");
			BufferedWriter bw2 = new BufferedWriter(fw2);
			int[] besteTfts = new int[10];
			// Problemklassen von 0-8
			for (int y = PROBLEMKLASSEN_STARTWERT; y < PROBLEMKLASSEN_ENDWERT; y++) {
				Problem.ausfuehrungszeiten0 = new int[10][Problem.anzahlMaschinenArray[y]][Problem.anzahlJobsArray[y]];
				Problem.eliteUpdateGewicht=Problem.anzahlJobsArray[y]/20.0;
				Problem.anzahlLokaleSuche = Problem.anzahlJobsArray[y]*8;
				String dateiName = Problem.dateiname[y];
				Reader reader = new Reader();
				reader.ladeProbleminstanzen(dateiName);
				reader.ladeBestwerteTFT(Problem.dateiBewerteTft);
				Problem.berechneGesamtBearbeitungsZeitJobs();
				// Probleminstanzen von 0-9
				for (int z = PROBLEMINSTANZEN_STARTWERT; z < PROBLEMINSTANZEN_ENDWERT; z++) {
					Problem.probleminstanz = z;
					// Wartekapazitaeten von 1-4
					for (int j = WARTEKAPAZITAETEN_STARTWERT; j < WARTEKAPAZITAETEN_ENDWERT; j++) {

						Problem.wartekapazitaet = j;
					//for(int f = 4; f<7;f++) {
						
						//Problem.nachbarschaftsGroesse = f;
						//Problem.eliteUpdateGewicht =  f;
						//for(int o = 7; o<10;o++) {
							//	Problem.anzahlLokaleSuche = o *Problem.anzahlJobs/2;
						for (int l = 0; l < MITTELUNG_ANZAHL; l++) {
							Problem.generiereTftHeristikLoesung();
							Population population = new Population(Problem.anzahlJobsArray[y], Problem.anzahlMaschinenArray[y]);
							final long timeStart = System.currentTimeMillis();
							for (int i = 0; i < MAXIMALE_ANZAHL_ITERATIONEN; i++) {
								population.generiereLoesung();
								final long timeEnd = System.currentTimeMillis();
								bw.write(population.getIterationsanzahl() + "; " + (timeEnd - timeStart) + "; "
										+ population.getEliteloesung().berechneTFT());
								bw.write("\n");
								if ((timeEnd - timeStart) > Problem.berechnungszeit[y]) {
									String s = "";
									for (int k = 0; k < Problem.anzahlJobsArray[y]; k++) {
										s += population.getEliteloesung().getJobreihenfolge()[k] + ", ";
									}
									bw.write(s);
									besteTfts[l] = population.getTftEliteLoesung();
									break;
								}
							}
						}
						
						bw.write("\n");
						int summe = 0;
						for (int tft : besteTfts) {
							summe = summe + tft;
						}
						
						double gemitteltertft = (summe / MITTELUNG_ANZAHL);
						double besterBekannterTft = Problem.bestWerteTft[y*10+z][j-1];
						// Berechnung des relative percentage deviation (RPD)
						double rpd = ((gemitteltertft- besterBekannterTft)/besterBekannterTft) * 100;
						bw2.write("LokaleSuche: " + Problem.anzahlLokaleSuche + " EliteGewicht: " + Problem.eliteUpdateGewicht + "gewichteter bester TFT bei Jobanzahl: " + Problem.anzahlJobsArray[y] + " Maschinenanzahl: "
								+ Problem.anzahlMaschinenArray[y] + " Instanz " + (z+1) + ", Wartekapazitaet " + j + ": "
								+ (summe / 10) + " RPD:" + rpd +" "+ besterBekannterTft);
						bw2.write("\n");
						}
						}
					}
				//}
			//}
			bw.close();

			bw2.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

}
