package antcolopt;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Main {

	public static void main(String[] args) {
		


		try {
			FileWriter fw = new FileWriter("D:\\QuickB3n\\logs\\ausgabe.txt");
			BufferedWriter bw = new BufferedWriter(fw);
			FileWriter fw2 = new FileWriter("D:\\QuickB3n\\logs\\ausgabe_ergebnisse.txt");
			BufferedWriter bw2 = new BufferedWriter(fw2);
			int[] besteTfts = new int[10];
			for (int y = 4; y < 5; y++) {
				Problem.anzahlMaschinen = Problem.anzahlMaschinenArray[y];
				System.out.println(Problem.anzahlMaschinen);
				Problem.anzahlJobs = Problem.anzahlJobsArray[y];
				Problem.ausfuehrungszeiten0 = new int[10][Problem.anzahlMaschinen][Problem.anzahlJobs];
				Problem.eliteUpdateGewicht=Problem.anzahlJobs/20.0;
				Problem.anzahlLokaleSuche = Problem.anzahlJobs*8;
				String dateiName = Problem.dateiname[y];
				Reader reader = new Reader();
				reader.ladeProbleminstanzen(dateiName);
				reader.ladeBestwerteTFT(Problem.dateiBewerteTft);
				Problem.berechneGesamtBearbeitungsZeitJobs();
				for (int z = 5; z < 10; z++) {
					Problem.probleminstanz = z;
					for (int j = 2; j < 3; j++) {

						Problem.wartekapazitaet = j;
					//for(int f = 4; f<7;f++) {
						
						//Problem.nachbarschaftsGroesse = f;
						//Problem.eliteUpdateGewicht =  f;
						//for(int o = 7; o<10;o++) {
							//	Problem.anzahlLokaleSuche = o *Problem.anzahlJobs/2;
						for (int l = 0; l < 10; l++) {
							Problem.generiereTftHeristikLoesung();
							Population population = new Population();
							final long timeStart = System.currentTimeMillis();
							for (int i = 0; i < 10000; i++) {
								population.generiereLoesung();
								final long timeEnd = System.currentTimeMillis();
								bw.write(population.getIterationsanzahl() + "; " + (timeEnd - timeStart) + "; "
										+ population.getEliteloesung().berechneTFT());
								bw.write("\n");
								if ((timeEnd - timeStart) > Problem.berechnungszeit[y]) {
									String s = "";
									for (int k = 0; k < Problem.anzahlJobs; k++) {
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
						
						double gemitteltertft = (summe / 10);
						double besterBekannterTft = Problem.bestWerteTft[y*10+z][j-1];
						double rpd = ((gemitteltertft- besterBekannterTft)/besterBekannterTft) * 100;
						bw2.write("LokaleSuche: " + Problem.anzahlLokaleSuche + " EliteGewicht: " + Problem.eliteUpdateGewicht + "gewichteter bester TFT bei Jobanzahl: " + Problem.anzahlJobs + " Maschinenanzahl: "
								+ Problem.anzahlMaschinen + " Instanz " + (z+1) + ", Wartekapazitaet " + j + ": "
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
