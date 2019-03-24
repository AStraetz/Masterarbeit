package antcolopt;

import java.util.Arrays;

public class Loesung {

	public int jobreihenfolge[];
	private int anzahlMaschinen;
	int[][] ausfuehrungszeiten;
	int makespan = 0;
	int alter = 0;
	private int letztesTft;

	public Loesung(int alter, int anzahlJobs, int anzahlMaschinen, int[][] ausfuehrungszeiten) {
		jobreihenfolge = new int[anzahlJobs];
		this.alter = alter;
		this.anzahlMaschinen = anzahlMaschinen;
		this.ausfuehrungszeiten = ausfuehrungszeiten;
	}

	/**
	 * Berechnet die Total Flow Time der Loesung
	 * 
	 * @return Total FLow Time
	 */
	public int berechneTFT() {
		int tft = 0;
		int[][] leavingTime = new int[anzahlMaschinen][jobreihenfolge.length];
		leavingTime[0][0] = ausfuehrungszeiten[0][jobreihenfolge[0]];
		berechneLeavingTimesJob1Maschine1(leavingTime);
		berechneLeavingTimesSonstKeineWartezeiten(leavingTime);
		berechneLEavingTimesMitWartezeiten(leavingTime);
		berechneCompletionTimesDerJobs(leavingTime);

		for (int i = 0; i < leavingTime[0].length; i++) {
			tft = tft + leavingTime[leavingTime.length - 1][i];
		}
		this.letztesTft = tft;
		return tft;
	}

	/**
	 * Berechnet den Zeitpunkt, an dem die Jobs die letzte Maschine verlassen
	 * 
	 * @param leavingTime Zeitpunkte, an denen die einzelne Jobs fertiggestellt
	 *                    wurden
	 */
	private void berechneCompletionTimesDerJobs(int[][] leavingTime) {
		for (int j = 1; j < leavingTime[0].length; j++) {

			leavingTime[leavingTime.length - 1][j] = Math.max(leavingTime[leavingTime.length - 1][j - 1],
					leavingTime[leavingTime.length - 2][j])
					+ ausfuehrungszeiten[leavingTime.length - 1][jobreihenfolge[j]];
		}
	}

	/**
	 * Leaving times werden berechnet, bei denen eventuelle Wartezeiten zusätzlich
	 * berücksichtigt werden muessen
	 * 
	 * @param leavingTime Zeitpunkte, an denen die Jobs die Maschine verlassen
	 */
	private void berechneLEavingTimesMitWartezeiten(int[][] leavingTime) {
		for (int j = KonstantenUndHelper.wartekapazitaet + 1; j < leavingTime[0].length; j++) {
			leavingTime[0][j] = Math.max(leavingTime[0][j - 1] + ausfuehrungszeiten[0][jobreihenfolge[j]],
					leavingTime[1][j - KonstantenUndHelper.wartekapazitaet - 1]);
			for (int i = 1; i < leavingTime.length - 1; i++) {
				leavingTime[i][j] = Math.max(
						Math.max(leavingTime[i][j - 1], leavingTime[i - 1][j])
								+ ausfuehrungszeiten[i][jobreihenfolge[j]],
						leavingTime[i + 1][j - KonstantenUndHelper.wartekapazitaet - 1]);
			}
		}
	}

	/**
	 * Leavingt imes werden berechnet, bei denen Wartezeiten noch nicht
	 * beruecksichtigt werden muessen
	 * 
	 * @param leavingTime Zeitpunkte, an denen die Jobs die Maschine verlassen
	 */
	private void berechneLeavingTimesSonstKeineWartezeiten(int[][] leavingTime) {
		for (int i = 1; i < leavingTime.length - 1; i++) {
			for (int j = 1; j < Math.min(KonstantenUndHelper.wartekapazitaet + 1, jobreihenfolge.length); j++) {
				leavingTime[i][j] = Math.max(leavingTime[i][j - 1], leavingTime[i - 1][j])
						+ ausfuehrungszeiten[i][jobreihenfolge[j]];
			}
		}
	}

	/**
	 * Nur leaving times für ersten Job und erste Maschine werden berechnet
	 * 
	 * @param leavingTime Zeitpunkte, an denen die Jobs die Maschine verlassen
	 */
	private void berechneLeavingTimesJob1Maschine1(int[][] leavingTime) {
		for (int i = 1; i < leavingTime.length; i++) {
			leavingTime[i][0] = leavingTime[i - 1][0] + ausfuehrungszeiten[i][jobreihenfolge[0]];
		}

		for (int j = 1; j < Math.min(KonstantenUndHelper.wartekapazitaet + 1, jobreihenfolge.length); j++) {
			leavingTime[0][j] = leavingTime[0][j - 1] + ausfuehrungszeiten[0][jobreihenfolge[j]];
		}
	}

	public int[] getJobreihenfolge() {
		return jobreihenfolge;
	}

	public void setJobreihenfolge(int[] jobreihenfolge) {
		this.jobreihenfolge = jobreihenfolge;
	}

	public int getAlter() {
		return alter;
	}

	public void setAlter(int alter) {
		this.alter = alter;
	}

	public int getLetztesTft() {
		return letztesTft;
	}

	@Override
	public String toString() {
		return alter + " -> " + Arrays.toString(jobreihenfolge);
	}

}