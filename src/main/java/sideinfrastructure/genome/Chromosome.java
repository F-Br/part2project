package sideinfrastructure.genome;

import java.util.*;

public class Chromosome {

    // map PID -> index in this chromosome
    // array list of codons
    // map PID -> (#prom, #repr)

    // some 2 sided fifo queue (time <- key, name, PID, Param)

    ArrayList<Codon> codonList = new ArrayList<>(); // TODO: will need to figure these out, they should all probably be private with getter methods, however not sure about constructors etc.
    HashMap<Integer, HashSet<Integer>> PIDToIndexMap = new HashMap<>(); // TODO: this will need to be a set of positions
    HashMap<Integer, PromoteRepressPair> PIDtoPIDExpressionMap = new HashMap<>();
    private int totalScore = 0;
    private ArrayList<Integer> sortedPIDList = new ArrayList<>(); // no duplicates in list
    private int chromPID;
    private int delegatePID;

    private Random rnd = new Random();

    public Chromosome(int chromPID, ArrayList<Codon> codonList) {
        this.chromPID = chromPID;
        this.codonList = codonList;
        initialisePIDtoIndexMapAndSortedPIDList();
    }

    private void initialisePIDtoIndexMapAndSortedPIDList() {
        int index = 0;
        for (Codon codon : codonList) {
            CodonType codonType = codon.getCodonType();
            if ((codonType == CodonType.BLOCKING_PROMOTER) || (codonType == CodonType.CONTINUING_PROMOTER)) {
                int PID = codon.getPID();
                PromoteRepressPair PIDExpression = PIDtoPIDExpressionMap.get(PID);

                // initialise promoteRepressPairs for all PIDs in chromosome
                if (PIDExpression == null) { // new PID
                    PIDExpression = new PromoteRepressPair();
                    PIDtoPIDExpressionMap.put(PID, PIDExpression);
                    sortedPIDList.add(PID);
                }
                else { // already seen PID
                    PIDExpression.addPIDCountWithinChromosome();
                }

                // initialise index map for all PIDs in chromosome
                HashSet<Integer> indexSet = PIDToIndexMap.get(PID);
                if (indexSet == null) {
                    indexSet = new HashSet<>();
                }
                indexSet.add(index);
                PIDToIndexMap.put(PID, indexSet); // TODO: this line might not be necessary (depending on referencing)
            }
            index ++;
        }
        sortedPIDList.sort(Comparator.naturalOrder());
        delegatePID = sortedPIDList.get(0);
    }

    public ArrayList<Integer> getSortedPIDList() {
        return sortedPIDList;
    }
    public int getChromPID() {
        return chromPID;
    }
    public int getDelegatePID() {
        return delegatePID;
    }

    // new promoter system:
    public int addPromoter(int validPID) {
        if (validPID == chromPID) {
            validPID = delegatePID;
        }
        if (!(sortedPIDList.contains(validPID))) { // if PID not in this chromosome:
            return totalScore;
        }
        PromoteRepressPair promoteRepressPair = PIDtoPIDExpressionMap.get(validPID);
        totalScore -= promoteRepressPair.getSumOfAllPIDInChromosomePromoteScore();
        promoteRepressPair.addPromoter();
        totalScore += promoteRepressPair.getSumOfAllPIDInChromosomePromoteScore();
        return totalScore;
    }

    public int removePromoter(int validPID) {
        if (validPID == chromPID) {
            validPID = delegatePID;
        }
        if (!(sortedPIDList.contains(validPID))) { // if PID not in this chromosome:
            return totalScore;
        }
        PromoteRepressPair promoteRepressPair = PIDtoPIDExpressionMap.get(validPID);
        totalScore -= promoteRepressPair.getSumOfAllPIDInChromosomePromoteScore();
        promoteRepressPair.removePromoter();
        totalScore += promoteRepressPair.getSumOfAllPIDInChromosomePromoteScore();
        return totalScore;
    }

    public int addRepressor(int validPID) {
        if (!(sortedPIDList.contains(validPID))) {
            return totalScore;
        }
        PromoteRepressPair promoteRepressPair = PIDtoPIDExpressionMap.get(validPID);
        totalScore -= promoteRepressPair.getSumOfAllPIDInChromosomePromoteScore();
        promoteRepressPair.addRepressor(); // TODO: check that you dont need to put this back into map
        return totalScore;
    }

    public int removeRepressor(int validPID) {
        if (!(sortedPIDList.contains(validPID))) {
            return totalScore;
        }
        PromoteRepressPair promoteRepressPair = PIDtoPIDExpressionMap.get(validPID);
        totalScore += promoteRepressPair.removeRepressor();
        return totalScore;
    }

    public int weightedRandomSelectionOfPID() {
        int cumulativeScoreTarget = rnd.nextInt(totalScore + 1);
        int currentCumulativeScore = 0;

        for (int PID : sortedPIDList) {
            int PIDScore = PIDtoPIDExpressionMap.get(PID).getIndividualPromoteScore();

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





    public int getTotalScore() {
        return totalScore;
    }

    public Codon getCodonAtLocation(int indexLocation) {
        return codonList.get(indexLocation);
    }

}
