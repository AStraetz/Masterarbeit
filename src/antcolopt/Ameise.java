package antcolopt;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.text.ElementIterator;

public class Ameise {

	
	List<Integer> erlaubteKnoten = new ArrayList<Integer>();
	int aktuellePosition;
	private List<Integer> nachbarschaft = new ArrayList<Integer>();
	private int nachbarschaftsgroesse = Problem.nachbarschaftsGroesse;
	private Loesung eliteLoesung;
	private int anzahlJobs;
	private int anzahlMaschinen;
	int[] besuchteKnoten = new int[anzahlJobs];

	double[] wahrscheinlichkeiten = new double[anzahlJobs];

	public Ameise(Loesung eliteloesung, int jobAnzahl, int maschinenAnzahl) {
		anzahlJobs = jobAnzahl;
		anzahlMaschinen = maschinenAnzahl;
		for (int i = 0; i < nachbarschaftsgroesse; i++) {
			nachbarschaft.add(eliteloesung.getJobreihenfolge()[i]);
		}
		eliteLoesung = eliteloesung;
		for (int i = 0; i < anzahlJobs; i++) {
			erlaubteKnoten.add(i);
		}
		aktuellePosition = 0;

	}

	public void updateWahrscheinlichkeiten(double[][] matrix) {

		if (Problem.nachbarschaftsregel == true) {

			double summeAlleKanten = 0;
			for (Integer knotenInNachbarschaft : nachbarschaft) {
				summeAlleKanten += matrix[aktuellePosition][knotenInNachbarschaft]
						* Math.pow((1.0 / Problem.gesamtBearbeitungsZeitJobs[knotenInNachbarschaft]), Problem.beta);
			}
			for (Integer knotenInNachbarschaft : nachbarschaft) {

				wahrscheinlichkeiten[knotenInNachbarschaft] = Math
						.pow((matrix[aktuellePosition][knotenInNachbarschaft]), Problem.alpha)
						* Math.pow((1.0 / Problem.gesamtBearbeitungsZeitJobs[knotenInNachbarschaft]), Problem.beta)
						/ summeAlleKanten;

			}

		}

		else {

			double summeAlleKanten = 0;
			for (int i = 0; i < anzahlJobs; i++) {
				if (erlaubteKnoten.contains(i)) {
					summeAlleKanten += matrix[aktuellePosition][i]
							* Math.pow((1.0 / Problem.gesamtBearbeitungsZeitJobs[i]), Problem.beta);
				}
			}

			// System.out.println("Summe alle Kanten: " + summeAlleKanten);

			for (int i = 0; i < anzahlJobs; i++) {
				if (erlaubteKnoten.contains(i)) {
					wahrscheinlichkeiten[i] = Math.pow((matrix[aktuellePosition][i]), Problem.alpha)
							* Math.pow((1.0 / Problem.gesamtBearbeitungsZeitJobs[i]), Problem.beta) / summeAlleKanten;
				} else {
					wahrscheinlichkeiten[i] = 0;
				}
			}

		}
	}

	public void bestimmeNachbarschaft() {
		nachbarschaft = new ArrayList<Integer>();
		for (int job : eliteLoesung.getJobreihenfolge()) {
			if (erlaubteKnoten.contains(job)) {
				nachbarschaft.add(job);
				if (nachbarschaft.size() >= nachbarschaftsgroesse) {
					break;
				}
			}
		}
	}

	public void naechsterKnoten(double[][] matrix) {

		if (Problem.benutzeq0 == true) {

			double random = Math.random();
			if (random < Problem.q0) {
				double maximalerPheromonwert = 0;
				int knotenMitMaximalemPheromonwert = -99;
				for (int knoten : nachbarschaft) {
					if (matrix[aktuellePosition][knoten] > maximalerPheromonwert) {
						maximalerPheromonwert = matrix[aktuellePosition][knoten];
						knotenMitMaximalemPheromonwert = knoten;
					}
				}

				int ergebnis = knotenMitMaximalemPheromonwert;
				erlaubteKnoten.remove((Integer) ergebnis);
				besuchteKnoten[aktuellePosition] = ergebnis;
				nachbarschaft.remove((Integer) ergebnis);
				if (aktuellePosition < (anzahlJobs - nachbarschaftsgroesse)) {
					nachbarschaft.add(eliteLoesung.getJobreihenfolge()[aktuellePosition + nachbarschaftsgroesse]);
				}
				aktuellePosition++;
				return;

			}
		}

		if (Problem.nachbarschaftsregel == true) {
			// System.out.println(knoten);
			updateWahrscheinlichkeiten(matrix);
			double summe = 0;
			double zufallszahl = Math.random();
			// System.out.println("zufallszahl: " + zufallszahl);
			for (Integer knotenInNachbarschaft : nachbarschaft) {
				// System.out.println("wahrscheinlichkeit: " +
				// wahrscheinlichkeiten[erlaubteKnoten.get(i)]);
				summe += wahrscheinlichkeiten[knotenInNachbarschaft];
				if (zufallszahl < summe) {
					int ergebnis = knotenInNachbarschaft;
					erlaubteKnoten.remove((Integer) knotenInNachbarschaft);
					besuchteKnoten[aktuellePosition] = ergebnis;
					nachbarschaft.remove((Integer) knotenInNachbarschaft);
					if (aktuellePosition < (anzahlJobs - nachbarschaftsgroesse)) {
						nachbarschaft.add(eliteLoesung.getJobreihenfolge()[aktuellePosition + nachbarschaftsgroesse]);
					}
					aktuellePosition++;
					return;
				}
			}
		} else {

			// Collections.sort(erlaubteKnoten);
			updateWahrscheinlichkeiten(matrix);
			// System.out.println(toString());
			for (Object knoten : erlaubteKnoten) {
				// System.out.println(knoten);
				double summe = 0;
				double zufallszahl = Math.random();
				// System.out.println("zufallszahl: " + zufallszahl);
				for (int i = 0; i < erlaubteKnoten.size(); i++) {
					// System.out.println("wahrscheinlichkeit: " +
					// wahrscheinlichkeiten[erlaubteKnoten.get(i)]);
					summe += wahrscheinlichkeiten[erlaubteKnoten.get(i)];
					if (zufallszahl < summe) {
						int ergebnis = erlaubteKnoten.get(i);
						erlaubteKnoten.remove(i);
						besuchteKnoten[aktuellePosition] = ergebnis;

						aktuellePosition++;
						return;
					}
				}
			}
		}

	}

	public int[] getBesuchteKnoten() {
		return besuchteKnoten;
	}

	public void setBesuchteKnoten(int[] besuchteKnoten) {
		this.besuchteKnoten = besuchteKnoten;
	}

	public List<Integer> getErlaubteKnoten() {
		return erlaubteKnoten;
	}

	public void setErlaubteKnoten(ArrayList<Integer> erlaubteKnoten) {
		this.erlaubteKnoten = erlaubteKnoten;
	}

	public int getAktuellePosition() {
		return aktuellePosition;
	}

	public void setAktuellePosition(int aktuellePosition) {
		this.aktuellePosition = aktuellePosition;
	}

	public String toString() {
		String s = "";
		s += "Besuchte Knoten: ";
		for (Object knoten : besuchteKnoten) {
			s += knoten + " ";
		}
		s += "\n Wahrscheinlichkeiten:";
		for (int i = 0; i < wahrscheinlichkeiten.length; i++) {
			s += wahrscheinlichkeiten[i] + " ";
		}
		return s;
	}

}
