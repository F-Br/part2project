package genome;

import java.util.ArrayList;
import java.util.HashMap;

public class Chromosome {

    // map PID -> index in this chromosome
    // array list of codons
    // map PID -> (#prom, #repr)

    // some 2 sided fifo queue (time <- key, name, PID, Param)

    ArrayList<Codon> codonList = new ArrayList<>();
    HashMap<Integer, Integer> promoterIDToIndexMap = new HashMap<>();
    HashMap<Integer, PromoteRepressPair> codonRegulatoryMap = new HashMap<>();

}
