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

    public void addPromoter() {
        promoteCount += 1;
        if (repressCount == 0) {
            recalculatePromoteScore();
        }
    }

    public void removePromoter() {
        promoteCount -= 1;
        if (repressCount == 0) {
            recalculatePromoteScore();
        }
    }

    public void addRepressor() {
        repressCount += 1;
        promoteScore = 0;
    }

    public void removeRepressor() {
        if (repressCount == 1) {
            recalculatePromoteScore();
        }
        repressCount -= 1;
    }
}
