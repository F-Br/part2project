package genome;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

public class GeneExpressionCounter {
    private int totalScore;
    private ArrayList<Integer> sortedPIDList;
    private HashMap<Integer, PromoteRepressPair> geneExpressionPairMap;

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

}
