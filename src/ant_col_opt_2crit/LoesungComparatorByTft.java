package ant_col_opt_2crit;

import java.util.Comparator;

public class LoesungComparatorByTft implements Comparator<Loesung>{

    @Override
    public int compare(Loesung first, Loesung second) {
    	Integer a = new Integer(first.getTft());
    	Integer b = new Integer(second.getTft());
        return a.compareTo(b);
    }
}