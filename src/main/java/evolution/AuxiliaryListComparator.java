package evolution;

import org.javatuples.Triplet;

import java.util.Comparator;

public class AuxiliaryListComparator implements Comparator<Triplet<Integer, Boolean, Integer>> {

    @Override
    public int compare(Triplet<Integer, Boolean, Integer> o1, Triplet<Integer, Boolean, Integer> o2) {
        // -int : o1 < o2
        // 0 : o1 == o2
        // +int : o1 > o2
        if (o1.getValue0() < o2.getValue0()) {
            return -1;
        }
        else if (o1.getValue0() > o2.getValue0()) {
            return 1;
        }
        else {
            return 0;
        }
    }
}
