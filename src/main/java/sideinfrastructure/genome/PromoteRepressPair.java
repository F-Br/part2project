package sideinfrastructure.genome;

public class PromoteRepressPair {
    private int promoteCount;
    private int repressCount;
    private int promoteScore;
    private int numberOfThisPIDWithinChromosome;

    final private int PROMOTER_MULTIPLIER = 10;

    public PromoteRepressPair() {
        this.promoteCount = 0;
        this.repressCount = 0;
        this.promoteScore = 0;
        this.numberOfThisPIDWithinChromosome = 1;
    }
    public PromoteRepressPair(boolean isChromosome) { // TODO: is this used???
        this.promoteCount = 0;
        this.repressCount = 0;
        if (isChromosome) {
            this.promoteScore = 1;
        }
        else {
            this.promoteScore = 0;
        }
    }

    public void addPIDCountWithinChromosome() {
        numberOfThisPIDWithinChromosome++;
    }

    public int getIndividualPromoteScore() {
        return promoteScore;
    }

    public int getSumOfAllPIDInChromosomePromoteScore() {
        return promoteScore * numberOfThisPIDWithinChromosome;
    }

    private int recalculatePromoteScore() {
        if (repressCount > 0) {
            promoteScore = 0;
            return 0;
        }
        promoteScore = promoteCount * PROMOTER_MULTIPLIER;
        return promoteScore;
    }

    public int addPromoter() {
        promoteCount += 1;
        recalculatePromoteScore();

        return promoteScore;
    }

    public int removePromoter() {
        promoteCount -= 1;
        recalculatePromoteScore();

        return promoteScore;
    }

    public int addRepressor() {
        repressCount += 1;
        promoteScore = 0;

        return 0;
    }

    public int removeRepressor() {
        if (repressCount == 1) {
            repressCount -= 1;
            recalculatePromoteScore();
        }
        else {
            repressCount -= 1;
        }

        return promoteScore;
    }
}
