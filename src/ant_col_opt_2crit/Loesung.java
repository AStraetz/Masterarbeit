package ant_col_opt_2crit;

import java.util.ArrayList;
import java.util.Arrays;

public class Loesung {

	public Loesung(int alter) {
		jobreihenfolge = new int[Problem.anzahlJobs];
		this.alter = alter;
	}

	public int jobreihenfolge[];
	int makespan = 0;
	private int tft = 0;
	int alter = 0;
	public int getRangTft() {
		return rangTft;
	}

	public void setRangTft(int rangTft) {
		this.rangTft = rangTft;
	}

	public int getRangTardiness() {
		return rangTardiness;
	}

	public void setRangTardiness(int rangTardiness) {
		this.rangTardiness = rangTardiness;
	}

	public int getTft() {
		return tft;
	}

	public void setTft(int tft) {
		this.tft = tft;
	}

	int[] completionTimes = new int[Problem.anzahlJobs];
	private int meanTardiness = 0;
	private int rangTft = 0;
	private int rangTardiness = 0;

	public int getMeanTardiness() {
		return meanTardiness;
	}

	public void setMeanTardiness(int meanTardiness) {
		this.meanTardiness = meanTardiness;
	}

	public int berechneTFT() {
		meanTardiness = 0;
		int tft = 0;
		Problem problem = new Problem();
		int[][] ausfuehrungszeiten = problem.getAusfuehrungszeiten();
		int[][] leavingTime = new int[Problem.anzahlMaschinen][jobreihenfolge.length];
		leavingTime[0][0] = ausfuehrungszeiten[0][jobreihenfolge[0]];
		berechneLeavingTimesfuerJob1undMaschine1(ausfuehrungszeiten, leavingTime);
		berechneRestlicheLeavingTimes(ausfuehrungszeiten, leavingTime);
		berechneLeavingTimesMitWartekapazität(ausfuehrungszeiten, leavingTime);
		berechneLeavingTimesLetzteMaschine(ausfuehrungszeiten, leavingTime);

		for (int i = 0; i < leavingTime[0].length; i++) {
			tft = tft + leavingTime[leavingTime.length - 1][i];
			completionTimes[i] = leavingTime[leavingTime.length - 1][i];
			meanTardiness = meanTardiness + Math.max(0, completionTimes[i]
					- Problem.dueDates[Problem.Problemklasse][Problem.probleminstanz][Problem.wartekapazitaet - 1][i]);
		}
		this.tft = tft;
		return tft;
	}

	private void berechneLeavingTimesLetzteMaschine(int[][] ausfuehrungszeiten, int[][] leavingTime) {
		// leavingTimes letzte Maschine
		for (int j = 1; j < leavingTime[0].length; j++) {

			leavingTime[leavingTime.length - 1][j] = Math.max(leavingTime[leavingTime.length - 1][j - 1],
					leavingTime[leavingTime.length - 2][j])
					+ ausfuehrungszeiten[leavingTime.length - 1][jobreihenfolge[j]];

			// System.out.println(leavingTime[0][j]);
		}
	}

	private void berechneLeavingTimesMitWartekapazität(int[][] ausfuehrungszeiten, int[][] leavingTime) {
		// berücksischtige Wartekapazität
		for (int j = Problem.wartekapazitaet + 1; j < leavingTime[0].length; j++) {

			leavingTime[0][j] = Math.max(leavingTime[0][j - 1] + ausfuehrungszeiten[0][jobreihenfolge[j]],
					leavingTime[1][j - Problem.wartekapazitaet - 1]);
			// System.out.println(leavingTime[0][j - 1] +
			// ausfuehrungszeiten[0][jobreihenfolge[j]] + " oder " + leavingTime[1][j -
			// Problem.wartekapazitaet - 1]);
			// System.out.println(leavingTime[0][j]);

// berücksischtige Wartekapazität
			for (int i = 1; i < leavingTime.length - 1; i++) {

				leavingTime[i][j] = Math.max(
						Math.max(leavingTime[i][j - 1], leavingTime[i - 1][j])
								+ ausfuehrungszeiten[i][jobreihenfolge[j]],
						leavingTime[i + 1][j - Problem.wartekapazitaet - 1]);

			}

		}
	}

	private void berechneRestlicheLeavingTimes(int[][] ausfuehrungszeiten, int[][] leavingTime) {
		for (int i = 1; i < leavingTime.length - 1; i++) {

			for (int j = 1; j < Math.min(Problem.wartekapazitaet + 1, jobreihenfolge.length); j++) {

				leavingTime[i][j] = Math.max(leavingTime[i][j - 1], leavingTime[i - 1][j])
						+ ausfuehrungszeiten[i][jobreihenfolge[j]];

			}

		}
	}

	private void berechneLeavingTimesfuerJob1undMaschine1(int[][] ausfuehrungszeiten, int[][] leavingTime) {
		// leavingTimes für Job 1
		for (int i = 1; i < leavingTime.length; i++) {

			leavingTime[i][0] = leavingTime[i - 1][0] + ausfuehrungszeiten[i][jobreihenfolge[0]];
			// System.out.println(leavingTime[i][0]);
		}

		// leavingTimes für Maschine 1
		for (int j = 1; j < Math.min(Problem.wartekapazitaet + 1, jobreihenfolge.length); j++) {

			leavingTime[0][j] = leavingTime[0][j - 1] + ausfuehrungszeiten[0][jobreihenfolge[j]];

			// System.out.println(leavingTime[0][j]);
		}
	}

	public int[] berechneCompletionTimeAllerJobs() {
		int[][] ausfuehrungszeiten = Problem.ausfuehrungszeiten0[Problem.probleminstanz];
		int[][] leavingTime = new int[Problem.anzahlMaschinen][jobreihenfolge.length];
		leavingTime[0][0] = ausfuehrungszeiten[0][jobreihenfolge[0]];
		berechneLeavingTimesfuerJob1undMaschine1(ausfuehrungszeiten, leavingTime);
		berechneRestlicheLeavingTimes(ausfuehrungszeiten, leavingTime);
		berechneLeavingTimesMitWartekapazität(ausfuehrungszeiten, leavingTime);
		berechneLeavingTimesLetzteMaschine(ausfuehrungszeiten, leavingTime);
		int[] completionTimes = new int[Problem.anzahlJobs];
		// for (int i = 0; i < leavingTime[0].length; i++) {
		// completionTimes[i] = leavingTime[leavingTime.length - 1][i];
		// }
		return leavingTime[leavingTime.length - 1];
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

	@Override
	public String toString() {
		return alter + " -> " + Arrays.toString(jobreihenfolge);
	}

}