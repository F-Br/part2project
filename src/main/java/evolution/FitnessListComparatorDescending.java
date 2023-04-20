package evolution;


import org.javatuples.Triplet;

import java.util.Comparator;

public class FitnessListComparatorDescending implements Comparator<Triplet<Float, EvolutionaryGenome, Long>> {

    @Override
    public int compare(Triplet<Float, EvolutionaryGenome, Long> o1, Triplet<Float, EvolutionaryGenome, Long> o2) {
        // -int : o1 < o2
        // 0 : o1 == o2
        // +int : o1 > o2
        if (o1.getValue0() < o2.getValue0()) {
            return 1;
        }
        else if (o1.getValue0() > o2.getValue0()) {
            return -1;
        }
        else {
            return 0;
        }
    }
}
