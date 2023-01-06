package genome;

public class PromoteRepressPair {
    private int promoteCount;
    private int repressCount;
    private int promoteScore;

    final private int PROMOTER_MULTIPLIER = 10;

    public PromoteRepressPair() {
        this.promoteCount = 0;
        this.repressCount = 0;
        this.promoteScore = 1;
    }

    public int getPromoteScore() {
        return promoteScore;
    }

    private int recalculatePromoteScore() {
        promoteScore = promoteCount * PROMOTER_MULTIPLIER;
        return promoteScore;
    }

    public int addPromoter() {
        promoteCount += 1;
        if (repressCount == 0) {
            recalculatePromoteScore();
        }

        return promoteScore;
    }

    public int removePromoter() {
        promoteCount -= 1;
        if (repressCount == 0) {
            recalculatePromoteScore();
        }

        return promoteScore;
    }

    public int addRepressor() {
        repressCount += 1;
        promoteScore = 0;

        return promoteScore;
    }

    public int removeRepressor() {
        if (repressCount == 1) {
            recalculatePromoteScore();
        }
        repressCount -= 1;

        return promoteScore;
    }
}
