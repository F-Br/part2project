package sideinfrastructure;

import fraglet.FragletReleasePair;
import org.javatuples.Triplet;

import java.util.Comparator;

public class FragletReleasePairComparator  implements Comparator<FragletReleasePair> {
    @Override
    public int compare(FragletReleasePair frp1, FragletReleasePair frp2) {
        // -int : o1 < o2
        // 0 : o1 == o2
        // +int : o1 > o2
        if (frp1.getReleaseStep() < frp2.getReleaseStep()) {
            return -1;
        }
        else if (frp1.getReleaseStep() > frp2.getReleaseStep()) {
            return 1;
        }
        else {
            return 0;
        }
    }
}
