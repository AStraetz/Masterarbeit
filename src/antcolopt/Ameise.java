package antcolopt;

import java.util.ArrayList;
import java.util.Collections;

public class Ameise {

	int[] besuchteKnoten = new int[Problem.anzahlJobs];
	ArrayList<Integer> erlaubteKnoten = new ArrayList<Integer>();
	int aktuellePosition;

	double[] wahrscheinlichkeiten = new double[Problem.anzahlJobs];

	public Ameise() {
		for (int i = 0; i < Problem.anzahlJobs; i++) {
			erlaubteKnoten.add(i);
		}
		aktuellePosition = 0;

	}

	public void updateWahrscheinlichkeiten(double[][] matrix) {
		

		double summeAlleKanten = 0;
		for (int i = 0; i < Problem.anzahlJobs; i++) {
			if (erlaubteKnoten.contains(i)) {
				summeAlleKanten += matrix[aktuellePosition][i]
						* Math.pow((1.0 / Problem.gesamtBearbeitungsZeitJobs[i]), Problem.beta);
			}
		}
			



		// System.out.println("Summe alle Kanten: " + summeAlleKanten);

		for (int i = 0; i < Problem.anzahlJobs; i++) {
			if (erlaubteKnoten.contains(i)) {
				wahrscheinlichkeiten[i] = Math.pow((matrix[aktuellePosition][i]), Problem.alpha)
						* Math.pow((1.0 / Problem.gesamtBearbeitungsZeitJobs[i]), Problem.beta) / summeAlleKanten;
			} else {
				wahrscheinlichkeiten[i] = 0;
			}
		}

	}

	public int naechsterKnoten(double[][] matrix) {
		Collections.sort(erlaubteKnoten);
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
					return ergebnis;
				}
			}
		}

		return erlaubteKnoten.get(erlaubteKnoten.size() - 1);

	}

	public int[] getBesuchteKnoten() {
		return besuchteKnoten;
	}

	public void setBesuchteKnoten(int[] besuchteKnoten) {
		this.besuchteKnoten = besuchteKnoten;
	}

	public ArrayList<Integer> getErlaubteKnoten() {
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
