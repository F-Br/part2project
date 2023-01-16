package sideinfrastructure.genome;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;

public class Chromosome {

    // map PID -> index in this chromosome
    // array list of codons
    // map PID -> (#prom, #repr)

    // some 2 sided fifo queue (time <- key, name, PID, Param)

    ArrayList<Codon> codonList = new ArrayList<>(); // TODO: will need to figure these out, they should all probably be private with getter methods, however not sure about constructors etc.
    HashMap<Integer, HashSet<Integer>> PIDToIndexMap = new HashMap<>(); // TODO: this will need to be a set of positions
    HashMap<Integer, PromoteRepressPair> codonExpressionMap = new HashMap<>();
    private ArrayList<Integer> sortedPIDList = new ArrayList<>();
    private int chromPID;

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

                sortedPIDList.add(PID);
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
    }

    public ArrayList<Integer> getSortedPIDList() {
        return sortedPIDList;
    }
    public int getChromPID() {
        return chromPID;
    }
}
