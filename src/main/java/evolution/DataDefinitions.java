package evolution;

public class DataDefinitions {
    private final int counterDataGroupSize;
    private final int statusTagDataGroupSize;
    private final int internalChromosomeDataGroupSize;
    private final int chromosomeDataStartingValue;
    private final int chromosomeDataEndingValueExclusive;
    private final int statusStartingValue;
    private final int numberChromosomePairs;

    public DataDefinitions(int counterDataGroupSize, int statusStartingValue, int statusTagDataGroupSize, int internalChromosomeDataGroupSize, int numberChromosomePairs) {
        this.numberChromosomePairs = numberChromosomePairs;
        this.counterDataGroupSize = counterDataGroupSize;
        this.statusTagDataGroupSize = statusTagDataGroupSize;
        this.internalChromosomeDataGroupSize = internalChromosomeDataGroupSize;
        this.statusStartingValue = statusStartingValue;

        if (counterDataGroupSize > statusStartingValue) {
            throw new IllegalStateException("counterDataGroupSize (" + counterDataGroupSize + ") is greater than the statusTag starting value of " + statusStartingValue);
        }

        int startingChromosomeMultiple = 100;
        this.chromosomeDataStartingValue = (((statusStartingValue + statusTagDataGroupSize - 1) / startingChromosomeMultiple) + 1) * startingChromosomeMultiple;
        // e.g. ssv = 50, stdgs = 50 to 99 (50), sm = 100, then (50 + 50 - 1 / 100) = 0. (0 + 1) * 100 = 100 which is desired starting value.
        this.chromosomeDataEndingValueExclusive = chromosomeDataStartingValue + (internalChromosomeDataGroupSize * numberChromosomePairs);
    }

    public int getCounterDataGroupMin() {
        return 0;
    }

    public int getCounterDataGroupMaxExclusive() {
        return counterDataGroupSize;
    }

    public int getStatusTagDataGroupMin() {
        return statusStartingValue;
    }

    public int getStatusTagDataGroupMaxExclusive() {
        return getStatusTagDataGroupMin() + statusTagDataGroupSize;
    }

    public int getChromosomeDataStartingValue() {
        return chromosomeDataStartingValue;
    }

    public int getChromosomeDataEndingValueExclusive() {
        return chromosomeDataEndingValueExclusive;
    }

    public int getChromosomeDataStartingValueForIndex(int index) {
        if (index >= numberChromosomePairs) {
            throw new IllegalArgumentException("index (" + index + ") must be less than number of chromosomes (" + numberChromosomePairs +")");
        }
        return getChromosomeDataStartingValue() + (index * (internalChromosomeDataGroupSize + 1)) + 1; // e.g. if CID is 200, it starts at 201 and goes till 299
    }

    public int getCIDForChromosomeIndex(int index) {
        return getChromosomeDataStartingValueForIndex(index) - 1;
    }

    public int getCounterDataGroupSize() {
        return counterDataGroupSize;
    }

    public int getStatusTagDataGroupSize() {
        return statusTagDataGroupSize;
    }

    public int getInternalChromosomeDataGroupSize() {
        return internalChromosomeDataGroupSize;
    }

    public int getInternalChromosomeLengthIncludingCID() { return internalChromosomeDataGroupSize + 1;}

    public int getNumberChromosomePairs() {
        return numberChromosomePairs;
    }
}
