package antcolopt;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Reader {

	private static final int ANZAHL_DER_VERSCHIEDENNEN_WARTEKAPAZITAETEN = 4;
	private static final int ANZAHL_DER_INSTANZEN_PRO_PROBLEMKLASSE = 10;
	private int[] jobZeiten;
	private int anzahlJobs;
	private int anzahlMaschinen;
	private int[][][] ausfuehrungszeiten;

	/**
	 * 
	 * @param anzahlJobs      Anzahl der zu optimierenden Jobs
	 * @param anzahlMaschinen Anzahl der zu optimierenden Maschinen
	 */
	public Reader(int anzahlJobs, int anzahlMaschinen) {
		super();

		this.anzahlJobs = anzahlJobs;
		this.anzahlMaschinen = anzahlMaschinen;
		this.ausfuehrungszeiten = new int[10][anzahlMaschinen][anzahlJobs];
		this.jobZeiten = new int[anzahlJobs];
	}

	/**
	 * Lädt die zur Zeit besten Total FLow Times aus der Datei ins Programm
	 * 
	 * @param dateipfadBestwerteTFT Pfad zur Datei mit den den besten Total Flow
	 *                              Zeiten, die bisher bekannt sind
	 */
	public void ladeBestwerteTFT(String dateipfadBestwerteTFT) {
		int problemklasse = 0;

		File file = new File(dateipfadBestwerteTFT);

		if (!file.canRead() || !file.isFile())
			System.exit(0);

		BufferedReader in = null;
		try {
			in = new BufferedReader(new FileReader(dateipfadBestwerteTFT));
			String zeile = null;
			while ((zeile = in.readLine()) != null) {

				if (zeile.contains("Ta")) {
					for (int i = 0; i < ANZAHL_DER_INSTANZEN_PRO_PROBLEMKLASSE; i++) {

						zeile = in.readLine();
						String[] splitted = zeile.split(",");

						for (int b = 0; b < ANZAHL_DER_VERSCHIEDENNEN_WARTEKAPAZITAETEN; b++) {
							KonstantenUndHelper.bestWerteTft[problemklasse][b] = Integer.parseInt(splitted[b].trim());
						}
						problemklasse++;
					}
					zeile = in.readLine();
				}
			}
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

	/**
	 * Lädt die Probleminstanzen aus der Datei ins Programm
	 * 
	 * @param datName Pfad zur Datei mit den Laufzeiten der Jobs pro Maschine
	 * @return Gibt die Ausführungszeiten pro Probleminstanz pro Maschine pro Job
	 *         zurück
	 */
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
						String[] splitted = zeile.split("\\s+");
						addAsInt(splitted, anzahlJobs);
						for (int b = 0; b < jobZeiten.length; b++) {
							ausfuehrungszeiten[counterInstanz][counterZeile][b] = jobZeiten[b];
						}
						counterZeile++;
					}
					counterInstanz++;
					counterZeile = 0;
				}
			}
			return ausfuehrungszeiten;
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

	/**
	 * Liest den gesplitteten String als ints in das jobZeiten-Array
	 * 
	 * @param elements   der gesplittete String aus der ausgelesenen Datei
	 * @param anzahlJobs Anzahl der zu optimierenden Jobs
	 */
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

	public int[][][] getAusfuehrungszeiten() {
		return ausfuehrungszeiten;
	}

	public void setAusfuehrungszeiten(int[][][] ausfuehrungszeiten) {
		this.ausfuehrungszeiten = ausfuehrungszeiten;
	}
}
