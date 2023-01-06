package genome;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class Chromosome {

    // map PID -> index in this chromosome
    // array list of codons
    // map PID -> (#prom, #repr)

    // some 2 sided fifo queue (time <- key, name, PID, Param)

    ArrayList<Codon> codonList = new ArrayList<>(); // TODO: will need to figure these out, they should all probably be private with getter methods, however not sure about constructors etc.
    HashMap<Integer, HashSet<Integer>> PIDToIndexMap = new HashMap<>(); // TODO: this will need to be a set of positions
    HashMap<Integer, PromoteRepressPair> codonRegulatoryMap = new HashMap<>();

}
