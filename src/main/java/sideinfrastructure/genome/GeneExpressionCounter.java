package sideinfrastructure.genome;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Random;

public class GeneExpressionCounter {
    private int totalScore;
    private ArrayList<Integer> sortedPIDList;
    private HashMap<Integer, PromoteRepressPair> geneExpressionPairMap;

    private Random rnd = new Random();

    public GeneExpressionCounter(ArrayList<Integer> PIDList) {
        sortedPIDList = PIDList;
        sortedPIDList.sort(Comparator.naturalOrder());
        for (Integer PID : sortedPIDList) {
            geneExpressionPairMap.put(PID, new PromoteRepressPair());
        }
        totalScore = sortedPIDList.size();
    }



    public int getPromoteScorePID(Integer PID) {
        return geneExpressionPairMap.get(PID).getPromoteScore();
    }

    public void addPromoter(Integer PID) {
        int oldScore = geneExpressionPairMap.get(PID).getPromoteScore();
        totalScore -= oldScore;
        int newScore = geneExpressionPairMap.get(PID).addPromoter();
        totalScore += newScore;
    }

    public void removePromoter(Integer PID) {
        int oldScore = geneExpressionPairMap.get(PID).getPromoteScore();
        totalScore -= oldScore;
        int newScore = geneExpressionPairMap.get(PID).removePromoter();
        totalScore += newScore;
    }

    public void addRepressor(Integer PID) {
        int oldScore = geneExpressionPairMap.get(PID).getPromoteScore();
        totalScore -= oldScore;
        int newScore = geneExpressionPairMap.get(PID).addRepressor();
        totalScore += newScore;
    }

    public void removeRepressor(Integer PID) {
        int oldScore = geneExpressionPairMap.get(PID).getPromoteScore();
        totalScore -= oldScore;
        int newScore = geneExpressionPairMap.get(PID).removeRepressor();
        totalScore += newScore;
    }

    public Integer weightedRandomSelectionOfPID() { // TODO: check that totalScore is not 0, if so return null (?)
        if (totalScore == 0) {
            return null;
        }

        int cumulativeScoreTarget = rnd.nextInt(totalScore + 1);
        int currentCumulativeScore = 0;
        for (int PID : sortedPIDList) {
            int PIDScore = geneExpressionPairMap.get(PID).getPromoteScore();

            if (PIDScore == 0) {
                continue;
            }

            currentCumulativeScore += PIDScore;
            if (cumulativeScoreTarget <= currentCumulativeScore) {
                return PID;
            }
        }

        throw new IllegalStateException("No PID found, should have reached a valid PID or returned null");
    }

}
