package sideinfrastructure.genome;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Random;

import static java.util.Collections.binarySearch;

public class GeneExpressionCounter {
    private int totalScore;
    private ArrayList<Integer> sortedPIDList;
    private ArrayList<Integer> sortedChromPIDList;
    private HashMap<Integer, PromoteRepressPair> geneExpressionPairMap = new HashMap<>();

    private Random rnd = new Random();

    public GeneExpressionCounter(ArrayList<Integer> PIDList, ArrayList<Integer> chromPIDList) {
        sortedPIDList = PIDList;
        sortedPIDList.sort(Comparator.naturalOrder());
        for (Integer PID : sortedPIDList) {
            geneExpressionPairMap.put(PID, new PromoteRepressPair());
        }
        totalScore = sortedPIDList.size();

        sortedChromPIDList = chromPIDList;
        sortedChromPIDList.sort(Comparator.naturalOrder());
        for (Integer chromPID : sortedChromPIDList) {
            geneExpressionPairMap.put(chromPID, new PromoteRepressPair());
        }
    }

    public int findClosestPID(int attemptedPID) {
        int closestPromPID = calculateClosestValue(sortedPIDList, attemptedPID);
        int closestChromPID = calculateClosestValue(sortedChromPIDList, attemptedPID);

        int diffPromPID = Math.abs(attemptedPID - closestPromPID);
        int diffChromPID = Math.abs(attemptedPID - closestChromPID);

        if (diffChromPID < attemptedPID) {
            return closestChromPID;
        }
        else {
            return closestPromPID;
        }




        // TODO: need to check the neighbours from the index given (with edge cases of 0 and length) and find the closest value to the attemptedPID
        // TODO: need to then return the closest value (so long as it is within a certain distance of the attemptedPID)
        // TODO: then need to add this into the getPromoteScorePID so that it handles unfamiliar PIDs
        // TODO: can also getPromoteScorePID instead of rewriting the same code over and over again in the addPromoters, repressPromoters, etc. code below

    }

    private int calculateClosestValue(ArrayList<Integer> list, int attemptedValue) {
        int attemptedIndex = binarySearch(list, attemptedValue);
        if (attemptedIndex >= 0) { // matches a value
            return attemptedValue;
        }
        else {
            attemptedIndex = -(attemptedIndex) - 1;
        }

        if (attemptedIndex == 0) { // smaller than all values
            return list.get(0);
        }
        if (attemptedIndex == list.size()) { // greater than all values
            return list.get(list.size() - 1);
        }

        int upper = list.get(attemptedIndex);
        int lower = list.get(attemptedIndex - 1);

        int upperDifference = upper - attemptedValue;
        int lowerDifference = attemptedValue - lower;

        if (upperDifference > lowerDifference) {
            return lower;
        }
        else {
            return upper;
        }


    }


    public int getPromoteScorePID(Integer validPID) {
        return geneExpressionPairMap.get(validPID).getPromoteScore();
    }

    public void addPromoter(int PID) {
        PID = findClosestPID(PID);
        int oldScore = getPromoteScorePID(PID);
        totalScore -= oldScore;
        int newScore = geneExpressionPairMap.get(PID).addPromoter();
        totalScore += newScore;
    }

    public void removePromoter(int PID) {
        PID = findClosestPID(PID);
        int oldScore = getPromoteScorePID(PID);
        totalScore -= oldScore;
        int newScore = geneExpressionPairMap.get(PID).removePromoter();
        totalScore += newScore;
    }

    public void addRepressor(int PID) {
        PID = findClosestPID(PID);
        int oldScore = getPromoteScorePID(PID);
        totalScore -= oldScore;
        int newScore = geneExpressionPairMap.get(PID).addRepressor();
        totalScore += newScore;
    }

    public void removeRepressor(int PID) {
        PID = findClosestPID(PID);
        int oldScore = getPromoteScorePID(PID);
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
