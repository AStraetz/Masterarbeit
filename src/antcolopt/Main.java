package antcolopt;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Main {

	public static void main(String[] args) {

		String dateiName = Problem.dateiname;		
		Reader reader = new Reader();
		reader.ladeDatei(dateiName);

		Problem.berechneGesamtBearbeitungsZeitJobs();
		for (int i = 0; i < Problem.gesamtBearbeitungsZeitJobs.length; i++) {
		}

		
		try {
			FileWriter fw = new FileWriter("D:\\QuickB3n\\logs\\ausgabe.txt" );
			BufferedWriter bw = new BufferedWriter(fw);
			for(int l =0;l<10;l++) {
				Population population = new Population();
				final long timeStart = System.currentTimeMillis();
			for (int i = 0; i < 300; i++) {
				
				population.generiereLoesung();
				final long timeEnd = System.currentTimeMillis();
				bw.write(population.getIterationsanzahl() + "; " + (timeEnd - timeStart) + "; "
						+ population.getEliteloesung().berechneTFT());
				bw.write("\n");
				if((timeEnd - timeStart) > 600000) {
					String s = "";
					for (int k = 0; k < Problem.anzahlJobs; k++) {
						s += population.getEliteloesung().getJobreihenfolge()[k] + ", ";
					}
					bw.write(s);
					break;}
			}
			bw.write("\n");
			
			/*
			 * Loesung loesung = population.generiereLoesung(); for (int i = 0; i < 10; i++)
			 * {
			 * 
			 * for (int j = 0; j < Problem.anzahlJobs; j++) {
			 * 
			 * loesung = population.lokaleSucheInsertion(loesung, j);
			 * 
			 * } } System.out.println(loesung.berechneTFT());
			 */
			}
			bw.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

}
