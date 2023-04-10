package evolution;


import org.apache.commons.math3.util.Pair;

import java.util.Comparator;

public class FitnessListComparator implements Comparator<Pair<Float, EvolutionaryGenome>> {

    @Override
    public int compare(Pair<Float, EvolutionaryGenome> o1, Pair<Float, EvolutionaryGenome> o2) {
        // -int : o1 < o2
        // 0 : o1 == o2
        // +int : o1 > o2
        if (o1.getFirst() < o2.getFirst()) {
            return -1;
        }
        else if (o1.getFirst() > o2.getFirst()) {
            return 1;
        }
        else {
            return 0;
        }
    }
}
