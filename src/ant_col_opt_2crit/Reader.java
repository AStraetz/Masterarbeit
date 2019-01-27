package ant_col_opt_2crit;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Reader {

	public int[] jobZeiten = new int[Problem.anzahlJobs];

	
	public void ladeBestwerteTFT(String datName) {
		int problemklasse = 0;

		File file = new File(datName);

		if (!file.canRead() || !file.isFile())
			System.exit(0);

		BufferedReader in = null;
		try {
			in = new BufferedReader(new FileReader(datName));
			String zeile = null;
			while ((zeile = in.readLine()) != null) {

				if (zeile.contains("Ta")) {
					for (int i = 0; i < 10; i++) {

						zeile = in.readLine();
						String[] splitted = zeile.split(",");
					
						for (int b = 0; b < 4; b++) {
							Problem.bestWerteTft[problemklasse][b] = Integer.parseInt(splitted[b].trim());
							
						}

						problemklasse++;
						// Problem.ausfuehrungszeiten0[counterInstanz][counterZeile] =

					}
					zeile = in.readLine();
					
				}

			}
			// System.out.println(toString());
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (in != null)
				try {
					in.close();
				} catch (IOException e) {
				}
		}
	}

	
	
	public void ladeProbleminstanzen(String datName) {
		int counterInstanz = 0;
		int counterZeile = 0;
		int counterMakespan = 0;

		File file = new File(datName);

		if (!file.canRead() || !file.isFile())
			System.exit(0);

		BufferedReader in = null;
		try {
			in = new BufferedReader(new FileReader(datName));
			String zeile = null;
			while ((zeile = in.readLine()) != null) {
				if(zeile.contains("number")) {
					zeile = in.readLine();
					String[] splitted = zeile.split("\\s+");
					Problem.makespanBestwerte[Problem.Problemklasse][counterMakespan] = (new Integer(Integer.parseInt(splitted[4])));
					counterMakespan++;
				}
				if (zeile.contains("processing times")) {
					for (int i = 0; i < Problem.anzahlMaschinen; i++) {

						zeile = in.readLine();
						// System.out.println("Gelesene Zeile: " + zeile);
						String[] splitted = zeile.split("\\s+");
						 //String[] splitted = zeile.split(" ");
						 for (int j = 1; j < Problem.anzahlJobs+1; j++) {
						 }
						addAsInt(splitted);
						for (int b = 0; b < jobZeiten.length; b++) {
							Problem.ausfuehrungszeiten0[counterInstanz][counterZeile][b] = jobZeiten[b];
						}

						counterZeile++;
						// Problem.ausfuehrungszeiten0[counterInstanz][counterZeile] =

					}
					counterInstanz++;
					counterZeile = 0;
				}

			}
			// System.out.println(toString());
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (in != null)
				try {
					in.close();
				} catch (IOException e) {
				}
		}
	}

	private void addAsInt(String[] elements) {
		for (int i = 1; i < Problem.anzahlJobs + 1; i++) {

			jobZeiten[i - 1] = (new Integer(Integer.parseInt(elements[i])));
		}

	}

	public String toString() {
		String s = "";
		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < Problem.anzahlMaschinen; j++) {
				for (int k = 0; k < Problem.anzahlJobs; k++) {

					s += Problem.ausfuehrungszeiten0[i][j][k] + " ";
				}
				s += "\n";
			}
			s += "\n";
			s += "\n";
		}

		return s;
	}
}
