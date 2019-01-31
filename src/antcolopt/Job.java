package antcolopt;

public class Job implements Comparable<Job> {

	private int nummer;
	private int heuristikwert;
	private int heuristikwert2;

	public Job(int nummer, int heuristikwert, int heuristikwert2) {
		super();
		this.nummer = nummer;
		this.heuristikwert = heuristikwert;
		this.heuristikwert2 = heuristikwert2;
	}

	/**
	 * Vergleicht Jobs anhand ihrer Rajedran Heuristikwerte
	 */
	public int compareTo(Job job) {
		if (job.heuristikwert < this.heuristikwert) {
			return 1;
		}
		if (job.heuristikwert > this.heuristikwert) {
			return -1;
		}
		if (job.heuristikwert2 < this.heuristikwert2) {
			return 1;
		} else {
			return -1;
		}
	}

	public int getNummer() {
		return nummer;
	}

	public void setNummer(int nummer) {
		this.nummer = nummer;
	}

	public int getHeuristikwert() {
		return heuristikwert;
	}

	public void setHeuristikwert(int heuristikwert) {
		this.heuristikwert = heuristikwert;
	}

	public int getHeuristikwert2() {
		return heuristikwert;
	}

	public void setHeuristikwert2(int heuristikwert2) {
		this.heuristikwert2 = heuristikwert2;
	}

}
