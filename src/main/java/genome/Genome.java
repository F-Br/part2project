package genome;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

public class Genome {
    // map CID -> chromosome pointer
    // sorted list of promoter ID
    // map PID -> CID
    // map CID -> (#prom, #repr)
    HashMap<Integer, LinkedList<Chromosome>> CIDToChromosomeMap;
    ArrayList<Integer> sortedPIDList;
    HashMap<Integer, LinkedList<Integer>> PIDToCIDLocationMap;
    HashMap<Integer, PromoteRepressPair> chromosomeRegulatoryMap;

    // TODO: this is likely temporary and will rethink this when other components built out or moving onto evolutionary operators and systems
    public Genome(HashMap<Integer, LinkedList<Chromosome>> CIDToChromosomeMap, ArrayList<Integer> sortedPIDList, HashMap<Integer, LinkedList<Integer>> PIDToCIDLocationMap, HashMap<Integer, PromoteRepressPair> chromosomeRegulatoryMap) {
        this.CIDToChromosomeMap = CIDToChromosomeMap;
        this.sortedPIDList = sortedPIDList;
        this.PIDToCIDLocationMap = PIDToCIDLocationMap;
        this.chromosomeRegulatoryMap = chromosomeRegulatoryMap;
    }


    // going to include the expression controller in the genome and not give it a class

    // EC
    // way to select what to produce (master map of all PIDs to PRPairs)
    // map of all PIDs to counts (might want to create a new class here which updates the count when PRPair updates its own values.
    // queue of promoters and repressors still present in area

    Queue<GeneExpressionDetails> geneExpressionDetailsQueue = new LinkedList<>();
    // TODO: 4/1/23 pick off from here

}
