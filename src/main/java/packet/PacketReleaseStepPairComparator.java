package packet;

import java.util.Comparator;

public class PacketReleaseStepPairComparator implements Comparator<PacketReleaseStepPair> {

    public int compare(PacketReleaseStepPair p1, PacketReleaseStepPair p2) {
        // -int : p1 < p2
        // 0 : p1 == p2
        // +int : p1 > p2
        if (p1.releaseStep < p2.releaseStep) {
            return -1;
        }
        else if (p1.releaseStep > p2.releaseStep) {
            return 1;
        }
        else {
            return 0;
        }
    }
}
