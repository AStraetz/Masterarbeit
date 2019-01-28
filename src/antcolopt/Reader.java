package antcolopt;

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

	private int[] jobZeiten;
	private int anzahlJobs;
	private int anzahlMaschinen;
	private int[][][] ausfuehrungszeiten;
	private int[][][] dueDates;
	
	

	public int[][][] getAusfuehrungszeiten() {
		return ausfuehrungszeiten;
	}

	public void setAusfuehrungszeiten(int[][][] ausfuehrungszeiten) {
		this.ausfuehrungszeiten = ausfuehrungszeiten;
	}

	public Reader(int anzahlJobs, int anzahlMaschinen) {
		super();
		
		this.anzahlJobs = anzahlJobs;
		this.anzahlMaschinen = anzahlMaschinen;
		this.ausfuehrungszeiten = new int[10][anzahlMaschinen][anzahlJobs];
		this.jobZeiten =  new int[anzahlJobs];
	}

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

	public int[][][] ladeProbleminstanzen(String datName) {
		int counterInstanz = 0;
		int counterZeile = 0;

		File file = new File(datName);

		if (!file.canRead() || !file.isFile())
			System.exit(0);

		BufferedReader in = null;
		try {
			
			in = new BufferedReader(new FileReader(datName));
			String zeile = null;
			while ((zeile = in.readLine()) != null) {

				if (zeile.contains("processing times")) {
					for (int i = 0; i < anzahlMaschinen; i++) {

						zeile = in.readLine();
						// System.out.println("Gelesene Zeile: " + zeile);
						String[] splitted = zeile.split("\\s+");
						// String[] splitted = zeile.split(" ");
						// for (int j = 0; j < Problem.anzahlJobs; j++) {
						// System.out.println(splitted[j]);
						// }
						addAsInt(splitted, anzahlJobs);
						for (int b = 0; b < jobZeiten.length; b++) {
							ausfuehrungszeiten[counterInstanz][counterZeile][b] = jobZeiten[b];
						}

						counterZeile++;
						// Problem.ausfuehrungszeiten0[counterInstanz][counterZeile] =

					}
					counterInstanz++;
					counterZeile = 0;
				}

			}
			return ausfuehrungszeiten;
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
		return ausfuehrungszeiten;
	}

	private void addAsInt(String[] elements, int anzahlJobs) {
		for (int i = 1; i < anzahlJobs + 1; i++) {

			jobZeiten[i - 1] = (new Integer(Integer.parseInt(elements[i])));
		}

	}

	public String toString() {
		String s = "";
		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < anzahlMaschinen; j++) {
				for (int k = 0; k < anzahlJobs; k++) {

					s += ausfuehrungszeiten[i][j][k] + " ";
				}
				s += "\n";
			}
			s += "\n";
			s += "\n";
		}

		return s;
	}
}
