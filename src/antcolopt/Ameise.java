package antcolopt;

import java.util.ArrayList;
import java.util.List;

public class Ameise {

	private List<Integer> erlaubteKnoten = new ArrayList<Integer>();
	private int aktuellePosition;
	private List<Integer> nachbarschaft = new ArrayList<Integer>();
	private int nachbarschaftsgroesse = KonstantenUndHelper.NACHBARSCHAFT_GROESSE;
	private Loesung eliteLoesung;
	private int anzahlJobs;
	private int[] besuchteKnoten;
	private double[] wahrscheinlichkeiten;

	/** Nachbarschaft wird initialisiert
	 * 
	 * @param eliteloesung    beste bisher gefundene Loesung
	 * @param jobAnzahl       Anzahl der zu optimierenden Jobs
	 * @param maschinenAnzahl Anzahl der zu opotimierenden Maschinen
	 */
	public Ameise(Loesung eliteloesung, int jobAnzahl, int maschinenAnzahl) {
		anzahlJobs = jobAnzahl;
		for (int i = 0; i < nachbarschaftsgroesse; i++) {
			nachbarschaft.add(eliteloesung.getJobreihenfolge()[i]);
		}
		eliteLoesung = eliteloesung;
		for (int i = 0; i < anzahlJobs; i++) {
			erlaubteKnoten.add(i);
		}
		aktuellePosition = 0;
		besuchteKnoten = new int[anzahlJobs];
		wahrscheinlichkeiten = new double[anzahlJobs];
	}

	/**
	 * Aktualisiert die Wahrscheinlichekitsmatrix nachdem ein Job gewählt wurde
	 * 
	 * @param wahrscheinlichkeitsMatrix Matrix mit den Wahrscheinlichekiten Job i
	 *                                  aud Platz j zu setzen
	 */
	private void updateWahrscheinlichkeiten(double[][] wahrscheinlichkeitsMatrix) {

		if (KonstantenUndHelper.VERWENDE_NACHBARSCHAFTSREGEL) {

			berechneWahrscheinlichkeitenInNachbarschaft(wahrscheinlichkeitsMatrix);
		}

		else {
			berechneAlleWahrscheinlichkeiten(wahrscheinlichkeitsMatrix);

		}
	}

	/**
	 * Berechnet die Wahrscheinlichkeiten traditionell
	 * 
	 * @param matrix Matrix mit den Wahrscheinlichekiten Job i aud Platz j zu setzen
	 */
	private void berechneAlleWahrscheinlichkeiten(double[][] matrix) {
		double summeAlleKanten = 0;
		for (int i = 0; i < anzahlJobs; i++) {
			if (erlaubteKnoten.contains(i)) {
				summeAlleKanten += matrix[aktuellePosition][i];
			}
		}
		for (int i = 0; i < anzahlJobs; i++) {
			if (erlaubteKnoten.contains(i)) {
				wahrscheinlichkeiten[i] = Math.pow((matrix[aktuellePosition][i]), KonstantenUndHelper.ALPHA)
						/ summeAlleKanten;
			} else {
				wahrscheinlichkeiten[i] = 0;
			}
		}
	}

	/**
	 * Berechnet die Wahrscheinlichkeiten mit der Nachbarschaftsregel
	 * 
	 * @param matrix Matrix mit den Wahrscheinlichekiten Job i aud Platz j zu setzen
	 */
	private void berechneWahrscheinlichkeitenInNachbarschaft(double[][] matrix) {
		double summeAlleKanten = 0;
		for (Integer knotenInNachbarschaft : nachbarschaft) {
			summeAlleKanten += matrix[aktuellePosition][knotenInNachbarschaft];
		}
		for (Integer knotenInNachbarschaft : nachbarschaft) {
			wahrscheinlichkeiten[knotenInNachbarschaft] = Math.pow((matrix[aktuellePosition][knotenInNachbarschaft]),
					KonstantenUndHelper.ALPHA) / summeAlleKanten;
		}
	}

	/**
	 * Bestimmt die Nachbarschaft: die ersten [nachbarschaftsgroesse] ungesetzen
	 * Jobs der Eliteloesung
	 */
	/*private void bestimmeNachbarschaft() {
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
*/
	/**
	 * Bestimmt den naechsten Job in der Bearbeitungsreihenfolge
	 * 
	 * @param matrix Matrix mit den Wahrscheinlichekiten Job i aud Platz j zu setzen
	 */
	public void bestimmteNaechstenJob(double[][] matrix) {
		double random = Math.random();
		if (KonstantenUndHelper.BENUTZE_q0_REGEL && (random < KonstantenUndHelper.q0)) {

			berechneNaechstenKnotenNachq0Regel(matrix);

		} else {

			if (KonstantenUndHelper.VERWENDE_NACHBARSCHAFTSREGEL) {
				berechneNaechstenKnotenNachNachbarschaftsRegel(matrix);
			} else {
				berechneNaechstenKnotenKonventionell(matrix);
			}

		}
	}

	/**
	 * Berechnung des naechsten Jobs ohne Nachbarschaftsregel, bzw q0-Regel
	 * 
	 * @param matrix Matrix mit den Wahrscheinlichekiten Job i aud Platz j zu setzen
	 */
	private void berechneNaechstenKnotenKonventionell(double[][] matrix) {
		updateWahrscheinlichkeiten(matrix);
		// for (Object knoten : erlaubteKnoten) {
		double summe = 0;
		double zufallszahl = Math.random();
		for (int i = 0; i < erlaubteKnoten.size(); i++) {
			summe += wahrscheinlichkeiten[erlaubteKnoten.get(i)];
			if (zufallszahl < summe) {
				int ergebnis = erlaubteKnoten.get(i);
				erlaubteKnoten.remove(i);
				besuchteKnoten[aktuellePosition] = ergebnis;
				aktuellePosition++;
				return;
			}
			// }
		}
	}

	/**
	 * Berechnung des naechsten Jobs mit Nachbarschaftsregel
	 * 
	 * @param matrix Matrix mit den Wahrscheinlichekiten Job i aud Platz j zu setzen
	 */
	private void berechneNaechstenKnotenNachNachbarschaftsRegel(double[][] matrix) {
		updateWahrscheinlichkeiten(matrix);
		double summe = 0;
		double zufallszahl = Math.random();
		int ergebnis = 0;
		for (Integer knotenInNachbarschaft : nachbarschaft) {
			summe += wahrscheinlichkeiten[knotenInNachbarschaft];
			if (zufallszahl < summe) {
				ergebnis = knotenInNachbarschaft;
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
	}

	/**
	 * Berechnung des naechsten Jobs mit q0-Regell
	 * 
	 * @param matrix Matrix mit den Wahrscheinlichekiten Job i aud Platz j zu setzen
	 */
	private void berechneNaechstenKnotenNachq0Regel(double[][] matrix) {
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
