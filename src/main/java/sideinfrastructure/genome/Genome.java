package sideinfrastructure.genome;

import clock.StepClock;
import fraglet.Fraglet;
import fraglet.instructions.DataInstruction;
import fraglet.instructions.Instruction;
import sideinfrastructure.SideIdentifier;

import java.util.*;

public class Genome {

    private final SideIdentifier side;

    // map CID -> chromosome pointer
    // sorted list of promoter ID
    // map PID -> CID
    // map CID -> (#prom, #repr)
    HashMap<Integer, Chromosome> chromPIDToChromosomeMap;
    ArrayList<Integer> sortedPIDList;
    HashMap<Integer, HashSet<Integer>> PIDToChromPIDLocationMap;
    HashMap<Integer, PromoteRepressPair> chromosomeRegulatoryMap;

    private int DEFAULT_VAR_VALUE = 0; // TODO: should this not be a BitSet of zero?

    // TODO: this is likely temporary and will rethink this when other components built out or moving onto evolutionary operators and systems
    public Genome(SideIdentifier side, HashMap<Integer, LinkedList<Chromosome>> CIDToChromosomeMap, ArrayList<Integer> sortedPIDList, HashMap<Integer, LinkedList<Integer>> PIDToCIDLocationMap, HashMap<Integer, PromoteRepressPair> chromosomeRegulatoryMap) {
        this.side = side;

        this.CIDToChromosomeMap = CIDToChromosomeMap;
        this.sortedPIDList = sortedPIDList;
        this.PIDToCIDLocationMap = PIDToCIDLocationMap;
        this.chromosomeRegulatoryMap = chromosomeRegulatoryMap;
    }

    public SideIdentifier getSide() {
        return side;
    }

    // going to include the expression controller in the sideinfrastructure.genome and not give it a class

    // EC
    // way to select what to produce (master map of all PIDs to PRPairs)
    // map of all PIDs to counts (might want to create a new class here which updates the count when PRPair updates its own values.
    // queue of promoters and repressors still present in area

    Queue<GeneExpressionDetails> geneExpressionDetailsQueue = new LinkedList<>();
    GeneExpressionCounter geneExpressionCounter = new GeneExpressionCounter(sortedPIDList);

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

    public void removeOldGeneExpressionDetails() {
        Long currentTime = StepClock.getCurrentStepCount();
        while (geneExpressionDetailsQueue.peek().getReleaseTime() <= currentTime) {
            GeneExpressionDetails oldGeneExpressionDetails = geneExpressionDetailsQueue.poll();

            // try and use if now possible
            if (geneExpressionCounter.getPromoteScorePID(oldGeneExpressionDetails.getPID()) > 0) {
                parseGenome(oldGeneExpressionDetails);
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

    public void parseGenome(Integer PID, BitSet VARValue) {
        // find which chromosomes and if they are repressed
        HashSet<Integer> chromPIDToParse = PIDToChromPIDLocationMap.get(PID);
        for (Integer chromPID : chromPIDToParse) {
            if (geneExpressionCounter.getPromoteScorePID(chromPID) == 0) {
                chromPIDToParse.remove(chromPID);
            }
        }

        // for those which arent, find which indexes correspond to the PID
        for (Integer chromPID : chromPIDToParse) {
            Chromosome currentChromosome = chromPIDToChromosomeMap.get(chromPID);
            HashSet<Integer> indexesToSynthesiseFrom = currentChromosome.PIDToIndexMap.get(PID);
            ArrayList<Codon> codonList = currentChromosome.codonList;

            // for each index synthesise from the codon list from that index
            for (Integer index : indexesToSynthesiseFrom) {

                Codon currentCodon = codonList.get(index);
                LinkedList<Instruction> workingFragletContents = new LinkedList<>();

                if (geneExpressionCounter.getPromoteScorePID(currentCodon.getPID()) != 0) {
                    index++;
                    currentCodon = codonList.get(index);

                    codon_parsing_loop:
                    while (currentCodon.getCodonType() != CodonType.BLOCKING_PROMOTER) {
                        switch (currentCodon.getCodonType()) {
                            case CodonType.INSTRUCTION:
                                workingFragletContents.addLast(currentCodon.getInstruction());
                                break;

                            case CodonType.VAR:
                                workingFragletContents.addLast(new DataInstruction(VARValue));
                                break;

                            case CodonType.CONTINUING_PROMOTER:
                                if (!workingFragletContents.isEmpty()) {
                                    vat.addFraglet(new Fraglet(workingFragletContents)); // TODO: add vat class and function
                                }
                                workingFragletContents = new LinkedList<>();

                                if (geneExpressionCounter.getPromoteScorePID(currentCodon.getPID()) == 0) {
                                    break codon_parsing_loop;
                                }
                                break;

                            default:
                                throw new IllegalStateException("This codon has not been accounted for when parsing (" + currentCodon.getCodonType().name() + ")");
                        }


                        index++;
                        if (index >= codonList.size()) {
                            break codon_parsing_loop;
                        }
                        currentCodon = codonList.get(index);
                    }
                }


            }
        }

    }





}
