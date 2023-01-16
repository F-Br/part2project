package sideinfrastructure.genome;

import clock.StepClock;
import fraglet.Fraglet;
import fraglet.instructions.DataInstruction;
import fraglet.instructions.Instruction;
import sideinfrastructure.FragletVat;
import sideinfrastructure.SideIdentifier;

import java.util.*;

public class Genome {

    private final SideIdentifier side;

    // map CID -> chromosome pointer
    // sorted list of promoter ID
    // map PID -> CID
    // map CID -> (#prom, #repr)
    HashMap<Integer, HashSet<Chromosome>> chromPIDToChromosomeMap = new HashMap<>(); // TODO: data structure of this should probably be hashset
    ArrayList<Integer> sortedPIDList = new ArrayList<>();
    ArrayList<Integer> sortedChromPIDList = new ArrayList<>();
    HashMap<Integer, HashSet<Integer>> PIDToChromPIDLocationMap = new HashMap<>();
    public FragletVat fragletVat;

    private BitSet DEFAULT_VAR_VALUE = BitSet.valueOf(new long[] {0});


    Queue<GeneExpressionDetails> geneExpressionDetailsQueue = new LinkedList<>();
    GeneExpressionCounter geneExpressionCounter;

    // new public constructor
    public Genome(SideIdentifier side, ArrayList<Chromosome> chromosomes) {
        this.side = side;
        this.fragletVat = new FragletVat(side);

        for (Chromosome chromosome : chromosomes) {
            int chromPID = chromosome.getChromPID();

            // add map from chromPID to set of references to chromosomes
            HashSet<Chromosome> currentChromosomes = chromPIDToChromosomeMap.get(chromPID);
            if (currentChromosomes == null) {
                currentChromosomes = new HashSet<>();
            }
            currentChromosomes.add(chromosome);
            chromPIDToChromosomeMap.put(chromPID, currentChromosomes);

            // add all PIDs to list
            ArrayList<Integer> PIDsInChromosome = chromosome.getSortedPIDList();
            sortedChromPIDList.add(chromPID);
            sortedPIDList.addAll(PIDsInChromosome);

            // add all chromosome PIDs to PIDToChromPIDLocationMap
            for (int PID : PIDsInChromosome) {
                HashSet<Integer> setChromPIDs = PIDToChromPIDLocationMap.get(PID);
                if (setChromPIDs == null) {
                    setChromPIDs = new HashSet<>();
                }
                setChromPIDs.add(chromPID);
                PIDToChromPIDLocationMap.put(PID, setChromPIDs);
            }

            geneExpressionCounter = new GeneExpressionCounter(sortedPIDList, sortedChromPIDList);

        }

        sortedPIDList.sort(Comparator.naturalOrder());

    }

    public SideIdentifier getSide() {
        return side;
    }

    // going to include the expression controller in the sideinfrastructure.genome and not give it a class

    // EC
    // way to select what to produce (master map of all PIDs to PRPairs)
    // map of all PIDs to counts (might want to create a new class here which updates the count when PRPair updates its own values.
    // queue of promoters and repressors still present in area



    public void addGeneExpressionDetail(GeneExpressionDetails geneExpressionDetails) {
        switch (geneExpressionDetails.getGeneExpressionType()) {
            case PROMOTER:
                geneExpressionCounter.addPromoter(geneExpressionDetails.getPID());
                break;
            case REPRESSOR:
                geneExpressionCounter.addRepressor(geneExpressionDetails.getPID());
                break;
            default:
                throw new IllegalArgumentException("Have not implemented case for " + geneExpressionDetails.getGeneExpressionType().name());
        }

        geneExpressionDetailsQueue.add(geneExpressionDetails);
    }

    public int removeOldGeneExpressionDetails() {
        if (geneExpressionDetailsQueue.isEmpty()) {
            return 0;
        }
        int numberPromotersActivated = 0;
        Long currentTime = StepClock.getCurrentStepCount();
        while (geneExpressionDetailsQueue.peek().getReleaseTime() <= currentTime) {
            GeneExpressionDetails oldGeneExpressionDetails = geneExpressionDetailsQueue.poll();

            // try and use if now possible
            if (geneExpressionCounter.getPromoteScorePID(oldGeneExpressionDetails.getPID()) > 0) {
                parseGenome(oldGeneExpressionDetails);
                numberPromotersActivated += 1;
            }

            // Update counter
            switch (oldGeneExpressionDetails.getGeneExpressionType()) {
                case PROMOTER:
                    geneExpressionCounter.removePromoter(oldGeneExpressionDetails.getPID());
                    break;
                case REPRESSOR:
                    geneExpressionCounter.removeRepressor(oldGeneExpressionDetails.getPID());
                    break;
                default:
                    throw new IllegalArgumentException("Have not implemented case for " + oldGeneExpressionDetails.getGeneExpressionType().name());
            }

            // break if queue now empty
            if (geneExpressionDetailsQueue.isEmpty()) {
                break;
            }
        }
        return numberPromotersActivated;
    }


    public void parseGenome(GeneExpressionDetails geneExpressionDetails) {
        if (geneExpressionDetails.getOwnVarSupplied()) {
            parseGenome(geneExpressionDetails.getPID(), geneExpressionDetails.getOptionalVarValue());
        }
        else {
            parseGenome(geneExpressionDetails.getPID(), DEFAULT_VAR_VALUE);
        }
    }

    public void parseGenome(Integer PID) {
        parseGenome(PID, DEFAULT_VAR_VALUE);
    }

    public void parseGenome(Integer PID, BitSet VARValue) { // TODO: what if everything is repressed?
        // find which chromosomes and if they are repressed
        HashSet<Integer> chromPIDToParse = PIDToChromPIDLocationMap.get(PID);
        for (Integer chromPID : chromPIDToParse) {
            if (geneExpressionCounter.getPromoteScorePID(chromPID) == 0) {
                chromPIDToParse.remove(chromPID);
            }
        }

        // go through each chromsome which shares the chromPID
        for (Integer chromPID : chromPIDToParse) {
            HashSet<Chromosome> setChromosomes = chromPIDToChromosomeMap.get(chromPID);
            for (Chromosome currentChromosome : setChromosomes) {
                // for those which arent, find which indexes correspond to the PID
                HashSet<Integer> indexesToSynthesiseFrom = currentChromosome.PIDToIndexMap.get(PID);
                // if this chromsome doesnt have the PID, then continue to next
                if (indexesToSynthesiseFrom == null) {
                    continue;
                }

                ArrayList<Codon> codonList = currentChromosome.codonList;

                // for each index synthesise from the codon list from that index
                for (Integer index : indexesToSynthesiseFrom) {

                    Codon currentCodon = codonList.get(index);
                    LinkedList<Instruction> workingFragletContents = new LinkedList<>();

                    if (geneExpressionCounter.getPromoteScorePID(currentCodon.getPID()) != 0) {
                        index++;
                        currentCodon = codonList.get(index);

                        codon_parsing_loop:
                        while (index < codonList.size()) {

                            currentCodon = codonList.get(index);

                            switch (currentCodon.getCodonType()) {
                                case INSTRUCTION:
                                    workingFragletContents.addLast(currentCodon.getInstruction());
                                    break;

                                case VAR:
                                    workingFragletContents.addLast(new DataInstruction(VARValue));
                                    break;

                                case CONTINUING_PROMOTER:
                                    if (!workingFragletContents.isEmpty()) {
                                        fragletVat.addFraglet(new Fraglet(workingFragletContents)); // TODO: add vat class and function
                                    }
                                    workingFragletContents = new LinkedList<>();

                                    if (geneExpressionCounter.getPromoteScorePID(currentCodon.getPID()) == 0) {
                                        break codon_parsing_loop;
                                    }
                                    break;

                                case BLOCKING_PROMOTER:
                                    if (!workingFragletContents.isEmpty()) {
                                        fragletVat.addFraglet(new Fraglet(workingFragletContents)); // TODO: add vat class and function
                                    }
                                    break codon_parsing_loop;

                                default:
                                    throw new IllegalStateException("This codon has not been accounted for when parsing (" + currentCodon.getCodonType().name() + ")");
                            }


                            index++;
                        }
                    }


                }
            }
        }

    }

    public Integer weightedRandomSelectionOfPID() {
        return geneExpressionCounter.weightedRandomSelectionOfPID();
    }

    public int findClosestPID(int attemptedPID) {
        return geneExpressionCounter.findClosestPID(attemptedPID);
    }



}
