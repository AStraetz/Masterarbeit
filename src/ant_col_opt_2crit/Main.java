package ant_col_opt_2crit;

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
			FileWriter fw3 = new FileWriter("D:\\QuickB3n\\logs\\endergebnis_grafik.txt");
			BufferedWriter bw3 = new BufferedWriter(fw3);
			int[] besteTfts = new int[10];
			int[] besteTards = new int[10];
			final int MITELLUNGSANZAHL = 5;
			for (int y = 6; y <7; y++) {
				System.out.println("starte neue Problemklasse");
				Problem.Problemklasse = y;
				Problem.anzahlMaschinen = Problem.anzahlMaschinenArray[y];
				Problem.anzahlJobs = Problem.anzahlJobsArray[y];
				Problem.ausfuehrungszeiten0 = new int[10][Problem.anzahlMaschinen][Problem.anzahlJobs];
				Problem.anzahlLokaleSuche = Problem.anzahlJobs*1000;
				String dateiName = Problem.dateiname[y];
				Reader reader = new Reader();
				reader.ladeProbleminstanzen(dateiName);
				reader.ladeBestwerteTFT(Problem.dateiBewerteTft);
				Problem.berechneGesamtBearbeitungsZeitJobs();
				
				for (int z = 0; z < 1; z++) {
					Problem.probleminstanz = z;
					for (int j = 2; j < 3; j++) {

						Problem.wartekapazitaet = j;
						Problem.berechneDueDates();
						for(int e = 0; e<4;e++) {
							if(e==0) {Problem.zufPop=false;Problem.populationsgroesse=1;}
							if(e==1) {Problem.zufPop=false;Problem.populationsgroesse=3;}
							if(e==2) {Problem.zufPop=false;Problem.populationsgroesse=5;}
							if(e==3) {Problem.zufPop=true;}
				//	for(int f = 3; f<4;f++) {
						
						//	if(e==1){Problem.nachbarschaftsregel=true;}
						//if(f==0){Problem.PHEROMON_MAX = 1.0;}
						//if(f==1){Problem.PHEROMON_MAX = 5.0;}
						//if(f==2){Problem.PHEROMON_MAX = 20.0;}
						//if(f==3){Problem.PHEROMON_MAX = 100.0;}
						//if(f==4){Problem.PHEROMON_MAX = 1000.0;}
						Problem.PHEROMON_MAX = 10.0;
						
						//if(f==1) {Problem.nachbarschaftsregel=true;
					//	Problem.PHEROMON_MAX = 20.0;
					//	}
						Problem.PHEROMON_UPDATE_MENGE = 
								(Problem.PHEROMON_MAX - Problem.pheromon_inital)
								/ (double) Problem.populationsgroesse;
						//Problem.nachbarschaftsGroesse = f;
						//Problem.eliteUpdateGewicht =  f;
						//for(int o = 7; o<10;o++) {
							//	Problem.anzahlLokaleSuche = o *Problem.anzahlJobs/2;
						for (int l = 0; l < MITELLUNGSANZAHL; l++) {
							//Problem.generiereTftHeristikLoesung();
							Population population = new Population();
							final long timeStart = System.currentTimeMillis();
							long timeEnd = System.currentTimeMillis();
							for(int r = 0; r < 10000000; r++){
								population.generiereLoesung();
								timeEnd = System.currentTimeMillis();
								//System.out.println(population.getLoesungenInSuperPopulation().size());
								bw.write(population.getIterationsanzahl() + "; " + (timeEnd - timeStart) + "; " + population.getLoesungenInSuperPopulation().size()+ "; " );
								for(Loesung loesung: population.getLoesungenInSuperPopulation()) {         
									bw.write(loesung.berechneTFT() + "; " + loesung.getMeanTardiness() + "; ");
								}
								bw.write("\n");
								
								if ((timeEnd - timeStart) > Problem.berechnungszeit[y]) {
									String s = "";
									for (int k = 0; k < Problem.anzahlJobs; k++) {
										s += population.getEliteloesung().getJobreihenfolge()[k] + ", ";
									}
									bw.write(s);
									besteTfts[l] = population.getLoesungenInSuperPopulation().get(0).berechneTFT();
									besteTards[l] = population.getLoesungenInSuperPopulation().get(population.getLoesungenInSuperPopulation().size()-1).getMeanTardiness();
									bw.write("\n");
									for(Loesung loesung: population.getLoesungenInSuperPopulation()) {  
										bw3.write(loesung.berechneTFT() + "; " + loesung.getMeanTardiness() + "; ");
										bw3.write("\n");
									}
									bw3.write("\n");
									break;
								}
							}
						}
						int summeTft = 0;
						int summeTard = 0;
						for (int tft : besteTfts) {
							summeTft = summeTft + tft;
						}
						for (int tard : besteTards) {
							summeTard = summeTard + tard;
						}
						
						double gemitteltertft = (summeTft) / MITELLUNGSANZAHL;
						double gemittelteTard = (summeTard) / MITELLUNGSANZAHL;
						double besterBekannterTft = Problem.bestWerteTft[y*10+z][j-1];
						double rpd = ((gemitteltertft- besterBekannterTft)/besterBekannterTft) * 100;
						bw2.write(Problem.PHEROMON_MAX+"; "+Problem.populationsgroesse+ "; " + Problem.anzahlLokaleSuche +"; "+ Problem.anzahlJobs +"; "
								+ Problem.anzahlMaschinen +"; " + (z+1) +"; " + j + ": "
								+ gemitteltertft +"; "+ gemittelteTard+"; " + rpd);
						bw2.write("\n");
					
					}
					}
						bw.write("\n");
						
						}
						}
				//	}
				//}
			//}
			bw.close();

			bw2.close();
			bw3.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

}
