package evolution;

import fraglet.instructions.DataInstruction;
import fraglet.instructions.Instruction;
import fraglet.instructions.InstructionTag;
import org.apache.commons.math3.distribution.EnumeratedDistribution;

import org.apache.commons.math3.util.Pair;
import sideinfrastructure.genome.Chromosome;
import sideinfrastructure.genome.Codon;
import sideinfrastructure.genome.CodonType;
import sideinfrastructure.genome.Genome;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Random;

public class MeiosisOperators implements MeiosisInterface {

    private final int NUM_CHROMOSOME_PAIRS;
    private final DataDefinitions dataDefinitions;
    private final int DATA_CHROMOSOME_GROUP_MUTATION_MAGNITUDE = 10;
    private final double PROB_DATA_CODON_TO_RANDOM_DATA_CODON = 0.1;
    private final double PROB_PROMOTER_TO_PROMOTER_PID_MODIFICATION = 0.8;
    Random rand = new Random();


    private EnumeratedDistribution codonDefaultDistribution;

    public MeiosisOperators(DataDefinitions dataDefinitions) {
        this.dataDefinitions = dataDefinitions;
        this.NUM_CHROMOSOME_PAIRS = dataDefinitions.getNumberChromosomePairs();
        if (DATA_CHROMOSOME_GROUP_MUTATION_MAGNITUDE > dataDefinitions.getChromosomeDataStartingValue()) {
            throw new IllegalStateException("DATA_CHROMOSOME_GROUP_MUTATION_MAGNITUDE should be less than the index value at which the chromosome group starts (" + dataDefinitions.getChromosomeDataStartingValue() + ")");
        }

        List<Pair<String, Double>> defaultList = new ArrayList<Pair<String, Double>>(); // TODO: STILL NEED TO CHANGE THESE SO THAT THEY ARE MORE PROPORTIONAL TO THE NUMBER OF FORMS THEY CAN TAKE

        defaultList.add(new Pair("VAR", 1.0));
        defaultList.add(new Pair("BLOCKING_PROMOTER", 1.0));
        defaultList.add(new Pair("CONTINUING_PROMOTER", 1.0));
        defaultList.add(new Pair("data INSTRUCTION", 1.0));
        defaultList.add(new Pair("operator INSTRUCTION", 1.0));

        this.codonDefaultDistribution = new EnumeratedDistribution(defaultList);
    }

    private List<Codon> deleteCodon(List<Codon> codonList, int index) {
        codonList.remove(index);
        return codonList;
    }

    private List<Codon> replaceCodonAtIndex(List<Codon> codonList, int replacementIndex, Codon replacementCodon) {
        // delete codon and then insert replacement at that location
        codonList = deleteCodon(codonList, replacementIndex);
        codonList = insertCodonAtIndex(codonList, replacementIndex, replacementCodon);
        return codonList;
    }

    private List<Codon> insertCodonAtIndex(List<Codon> codonList, int insertionIndex, Codon insertedCodon) {
        codonList.add(insertionIndex, insertedCodon);
        return codonList;
    }

    private List<Codon> deletionDefault(List<Codon> codonList, int index) {
        return deleteCodon(codonList, index);
    }

    private List<Codon> insertionDefault(List<Codon> codonList, int codonIndexCallingInsert, int chromosomeIndex) {
        // update index to be after calling index
        codonIndexCallingInsert++;

        // insert placeholder codon
        if (codonIndexCallingInsert >= codonList.size()) { // at end of codonList
            codonList.add(new Codon(CodonType.VAR));
        }
        else { // inside of codonList
            codonList = insertCodonAtIndex(codonList, codonIndexCallingInsert, new Codon(CodonType.VAR));
        }

        // replace placeholder codon
        String sample = codonDefaultDistribution.sample().toString();
        switch (sample) {
            case "VAR":
                codonList = varDefault(codonList, codonIndexCallingInsert);
                break;

            case "BLOCKING_PROMOTER":
                codonList = blockingPromoterDefault(codonList, codonIndexCallingInsert, chromosomeIndex);
                break;

            case "CONTINUING_PROMOTER":
                codonList = continuingPromoterDefault(codonList, codonIndexCallingInsert, chromosomeIndex);
                break;

            case "data INSTRUCTION":
                codonList = dataInstructionDefault(codonList, codonIndexCallingInsert, chromosomeIndex);
                break;

            case "operator INSTRUCTION":
                codonList = operatorInstructionDefault(codonList, codonIndexCallingInsert);
                break;

            default:
                throw new IllegalStateException("Unexpected value: " + sample);
        }

        return codonList;
    }

    private List<Codon> varDefault(List<Codon> codonList, int codonIndex) {
        return replaceCodonAtIndex(codonList, codonIndex, new Codon(CodonType.VAR));
    }


    private List<Codon> blockingPromoterDefault(List<Codon> codonList, int codonIndex, int chromosomeIndex) {
        return blockingPromoterDefault(codonList, codonIndex, 1d, chromosomeIndex);
    }

    private List<Codon> continuingPromoterDefault(List<Codon> codonList, int codonIndex, int chromosomeIndex) {
        return continuingPromoterDefault(codonList, codonIndex, 1d, chromosomeIndex);
    }

    private int determinePIDGivenProbInternal(double probDefinitelyInternal, int chromosomeIndex) {
        if (probDefinitelyInternal > rand.nextDouble()) {
            // create internal blocking promoter
            return dataDefinitions.getChromosomeDataStartingValueForIndex(chromosomeIndex) + rand.nextInt(dataDefinitions.getInternalChromosomeDataGroupSize());
        }
        else {
            // create blocking promoter with PID across all viable range
            return dataDefinitions.getChromosomeDataStartingValue() + rand.nextInt(dataDefinitions.getChromosomeDataEndingValueExclusive() - dataDefinitions.getChromosomeDataStartingValue());
        }
    }

    private List<Codon> blockingPromoterDefault(List<Codon> codonList, int codonIndex, double probDefinitelyInternal, int chromosomeIndex) {
        int blockingPromoterPID = determinePIDGivenProbInternal(probDefinitelyInternal, chromosomeIndex);
        Codon replacementCodon = new Codon(CodonType.BLOCKING_PROMOTER, blockingPromoterPID);
        return replaceCodonAtIndex(codonList, codonIndex, replacementCodon);
    }

    private List<Codon> continuingPromoterDefault(List<Codon> codonList, int codonIndex, double probDefinitelyInternal, int chromosomeIndex) {
        int continuingPromoterPID = determinePIDGivenProbInternal(probDefinitelyInternal, chromosomeIndex);
        Codon replacementCodon = new Codon(CodonType.CONTINUING_PROMOTER, continuingPromoterPID);
        return replaceCodonAtIndex(codonList, codonIndex, replacementCodon);
    }

    private List<Codon> dataInstructionDefault(List<Codon> codonList, int codonIndex, int chromosomeIndex) {
        return dataInstructionDefault(codonList, codonIndex, 1d/3, 1d/3, (1 - (2d/3)), 0.5, chromosomeIndex);
    }

    private int getExponentialDistributedInteger(double lambda, int maxIntExclusive) {
        // v = 1 - e^(-lambda x)  =>  e^(-lambda x) = 1 - v  =>  -lambda x = log(1 - v)  =>  x = -1/lambda log(1-v)
        int distributionGeneratedInteger = (int) ((-1/lambda) * Math.log(1 - rand.nextDouble()));
        if (distributionGeneratedInteger >= maxIntExclusive) {
            distributionGeneratedInteger = maxIntExclusive - 1;
        }
        return distributionGeneratedInteger;
    }

    private List<Codon> dataInstructionDefault(List<Codon> codonList, int codonIndex, double probCounter, double probStatusCode, double probChromosomePID, double probDefinitelyInternal, int chromosomeIndex) {
        if (probCounter + probStatusCode + probChromosomePID != 1d) {
            throw new IllegalArgumentException("Probabilities must sum to 1, instead they sum to: " + (probCounter + probStatusCode + probChromosomePID));
        }

        double randomProb = rand.nextDouble();
        int dataValue;

        if (randomProb < probCounter) {
            // using CDF of exponential distribution with lambda = 0.3 ( p(0) = 0.26, p(9) = 0.7 )
            dataValue = dataDefinitions.getCounterDataGroupMin() + getExponentialDistributedInteger(0.3, dataDefinitions.getCounterDataGroupMaxExclusive());
        }
        else if (randomProb < probCounter + probStatusCode) {
            // uniformly distributed
            dataValue = dataDefinitions.getStatusTagDataGroupMin() + rand.nextInt(dataDefinitions.getStatusTagDataGroupSize());
        }
        else {
            dataValue = determinePIDGivenProbInternal(probDefinitelyInternal, chromosomeIndex);
        }
        DataInstruction dataInstruction = new DataInstruction(BitSet.valueOf(new long[] {dataValue}));
        return replaceCodonAtIndex(codonList, codonIndex, new Codon(CodonType.INSTRUCTION, dataInstruction));
    }

    private List<Codon> operatorInstructionDefault(List<Codon> codonList, int codonIndex) {
        InstructionTag operatorInstructionTag = InstructionTag.getRandomOperatorInstructionTag();
        Instruction operatorInstruction = new Instruction(operatorInstructionTag);
        return replaceCodonAtIndex(codonList, codonIndex, new Codon(CodonType.INSTRUCTION, operatorInstruction));
    }

    private List<Codon> promoterToOtherPromoterMutation(List<Codon> codonList, int codonIndex, int chromosomeIndex) {
        // sort out PID
        int currentCodonPID = codonList.get(codonIndex).getPID();
        if (PROB_PROMOTER_TO_PROMOTER_PID_MODIFICATION > rand.nextDouble()) {
            currentCodonPID = calculateModifiedPromoterPID(currentCodonPID, chromosomeIndex);
        }
        // sort out CodonType switch
        if (codonList.get(codonIndex).getCodonType() == CodonType.BLOCKING_PROMOTER) {
            return replaceCodonAtIndex(codonList, codonIndex, new Codon(CodonType.CONTINUING_PROMOTER, currentCodonPID));
        }
        else { // was previously continuing promoter
            return replaceCodonAtIndex(codonList, codonIndex, new Codon(CodonType.BLOCKING_PROMOTER, currentCodonPID));
        }
    }

    private List<Codon> promoterToSamePromoterMutation(List<Codon> codonList, int codonIndex, int chromosomeIndex) {
        int currentCodonPID = codonList.get(codonIndex).getPID();
        currentCodonPID = calculateModifiedPromoterPID(currentCodonPID, chromosomeIndex);

        return replaceCodonAtIndex(codonList, codonIndex, new Codon(codonList.get(codonIndex).getCodonType(), currentCodonPID));
    }

    private int calculateModifiedPromoterPID(int currentCodonPID, int chromosomeIndex) {
        if (rand.nextInt(2) == 0) { // +
            currentCodonPID -= (rand.nextInt(DATA_CHROMOSOME_GROUP_MUTATION_MAGNITUDE) + 1);
            if (currentCodonPID < dataDefinitions.getChromosomeDataStartingValueForIndex(chromosomeIndex)) { // check hasn't fallen out of chromosome
                currentCodonPID = dataDefinitions.getChromosomeDataStartingValueForIndex(chromosomeIndex);
            }
        } else { // -
            currentCodonPID += (rand.nextInt(DATA_CHROMOSOME_GROUP_MUTATION_MAGNITUDE) + 1);
            if (currentCodonPID >= (dataDefinitions.getChromosomeDataStartingValueForIndex(chromosomeIndex) + dataDefinitions.getInternalChromosomeDataGroupSize())) { // check hasn't fallen out of chromosome
                currentCodonPID = (dataDefinitions.getChromosomeDataStartingValueForIndex(chromosomeIndex) + dataDefinitions.getInternalChromosomeDataGroupSize() - 1);
            }
        }

        return currentCodonPID;
    }

    // TODO: change this back to a private method
    public List<Codon> applySpecificMutation(List<Codon> codonList, int index, int chromosomeIndex) {

        // check which bucket codon falls into and produce that result:
        // DATA INSTRUCTION
        // OPERATOR INSTRUCTION
        // VAR
        // BLOCKING_PROMOTER
        // CONTINUING_PROMOTER

        // for each consider:
        //      deletion
        //      insertion (always after)
        //      VAR
        //      BLOCKING_PROMOTER
        //      CONTINUING_PROMOTER
        //      data instruction
        //      operator instruction


        switch (codonList.get(index).getCodonType()) {

            // Specialised: BLOCKING_PROMOTER, CONTINUING_PROMOTER
            case BLOCKING_PROMOTER: {
                List<Pair<String, Double>> list = new ArrayList<Pair<String, Double>>();
                list.add(new Pair("deletion", 1.0));
                list.add(new Pair("insertion", 1.0));
                list.add(new Pair("VAR", 1.0));
                list.add(new Pair("BLOCKING_PROMOTER", 1.0));
                list.add(new Pair("CONTINUING_PROMOTER", 1.0));
                list.add(new Pair("data INSTRUCTION", 1.0));
                list.add(new Pair("operator INSTRUCTION", 1.0));
                EnumeratedDistribution e = new EnumeratedDistribution(list);

                String sample = e.sample().toString();
                switch (sample) {
                    case "deletion":
                        codonList = deletionDefault(codonList, index);
                        break;

                    case "insertion":
                        codonList = insertionDefault(codonList, index, chromosomeIndex);
                        break;

                    case "VAR":
                        codonList = varDefault(codonList, index);
                        break;

                    case "BLOCKING_PROMOTER":
                        codonList = promoterToSamePromoterMutation(codonList, index, chromosomeIndex);
                        break;

                    case "CONTINUING_PROMOTER":
                        codonList = promoterToOtherPromoterMutation(codonList, index, chromosomeIndex);
                        break;

                    case "data INSTRUCTION":
                        codonList = dataInstructionDefault(codonList, index, chromosomeIndex);
                        break;

                    case "operator INSTRUCTION":
                        codonList = operatorInstructionDefault(codonList, index);
                        break;

                    default:
                        throw new IllegalStateException("Unexpected value: " + sample);
                }
                return codonList;
            }

            // Specialised: BLOCKING_PROMOTER, CONTINUING_PROMOTER
            case CONTINUING_PROMOTER: {
                List<Pair<String, Double>> list = new ArrayList<Pair<String, Double>>();
                list.add(new Pair("deletion", 1.0));
                list.add(new Pair("insertion", 1.0));
                list.add(new Pair("VAR", 1.0));
                list.add(new Pair("BLOCKING_PROMOTER", 1.0));
                list.add(new Pair("CONTINUING_PROMOTER", 1.0));
                list.add(new Pair("data INSTRUCTION", 1.0));
                list.add(new Pair("operator INSTRUCTION", 1.0));
                EnumeratedDistribution e = new EnumeratedDistribution(list);

                String sample = e.sample().toString();
                switch (sample) {
                    case "deletion":
                        codonList = deletionDefault(codonList, index);
                        break;

                    case "insertion":
                        codonList = insertionDefault(codonList, index, chromosomeIndex);
                        break;

                    case "VAR":
                        codonList = varDefault(codonList, index);
                        break;

                    case "BLOCKING_PROMOTER":
                        codonList = promoterToOtherPromoterMutation(codonList, index, chromosomeIndex);
                        break;

                    case "CONTINUING_PROMOTER":
                        codonList = promoterToSamePromoterMutation(codonList, index, chromosomeIndex);
                        break;

                    case "data INSTRUCTION":
                        codonList = dataInstructionDefault(codonList, index, chromosomeIndex);
                        break;

                    case "operator INSTRUCTION":
                        codonList = operatorInstructionDefault(codonList, index);
                        break;

                    default:
                        throw new IllegalStateException("Unexpected value: " + sample);
                }
                return codonList;
            }

            // Specialised: data INSTRUCTION (data instruction more likely to go to different data instruction or VAR) - therefore need check if data instruction
            case INSTRUCTION: {
                List<Pair<String, Double>> list = new ArrayList<Pair<String, Double>>();
                list.add(new Pair("deletion", 1.0));
                list.add(new Pair("insertion", 1.0));
                list.add(new Pair("VAR", 1.0));
                list.add(new Pair("BLOCKING_PROMOTER", 1.0));
                list.add(new Pair("CONTINUING_PROMOTER", 1.0));
                list.add(new Pair("data INSTRUCTION", 1.0));
                list.add(new Pair("operator INSTRUCTION", 1.0));
                EnumeratedDistribution e = new EnumeratedDistribution(list);

                String sample = e.sample().toString();
                switch (sample) {
                    case "deletion":
                        codonList = deletionDefault(codonList, index);
                        break;

                    case "insertion":
                        codonList = insertionDefault(codonList, index, chromosomeIndex);
                        break;

                    case "VAR":
                        codonList = varDefault(codonList, index);
                        break;

                    case "BLOCKING_PROMOTER":
                        codonList = blockingPromoterDefault(codonList, index, chromosomeIndex);
                        break;

                    case "CONTINUING_PROMOTER":
                        codonList = continuingPromoterDefault(codonList, index, chromosomeIndex);
                        break;

                    case "data INSTRUCTION":
                        // check if current codon is data instruction, in which case need specialised implementation
                        if (codonList.get(index).getInstruction() instanceof DataInstruction) { // is data instruction codon
                            if (rand.nextDouble() < PROB_DATA_CODON_TO_RANDOM_DATA_CODON) { // data codon to random data codon possibility
                                codonList = dataInstructionDefault(codonList, index, chromosomeIndex);
                            }
                            else { // usual behaviour for a data codon mutation
                                int currentValue = (int) ((DataInstruction) codonList.get(index).getInstruction()).getLongData();
                                if (currentValue >= dataDefinitions.getChromosomeDataStartingValue()) { // in chromosome group
                                    if (rand.nextInt(2) == 0) {
                                        currentValue -= (rand.nextInt(DATA_CHROMOSOME_GROUP_MUTATION_MAGNITUDE) + 1);
                                    } else {
                                        currentValue += (rand.nextInt(DATA_CHROMOSOME_GROUP_MUTATION_MAGNITUDE) + 1);
                                    }
                                } else { // in other groups/possibilities
                                    if (rand.nextInt(2) == 0) {
                                        currentValue--;
                                    } else {
                                        currentValue++;
                                    }

                                    if (currentValue < 0) {
                                        currentValue = 0;
                                    }
                                }
                                DataInstruction dataInstruction = new DataInstruction(BitSet.valueOf(new long[]{currentValue}));
                                codonList = replaceCodonAtIndex(codonList, index, new Codon(CodonType.INSTRUCTION, dataInstruction));
                            }
                        }
                        else {
                            codonList = dataInstructionDefault(codonList, index, chromosomeIndex);
                        }
                        break;

                    case "operator INSTRUCTION":
                        codonList = operatorInstructionDefault(codonList, index);
                        break;

                    default:
                        throw new IllegalStateException("Unexpected value: " + sample);
                }
                return codonList;
            }

            // Specialised: N/A
            case VAR: {
                List<Pair<String, Double>> list = new ArrayList<Pair<String, Double>>();
                list.add(new Pair("deletion", 1.0));
                list.add(new Pair("insertion", 1.0));
                list.add(new Pair("VAR", 1.0));
                list.add(new Pair("BLOCKING_PROMOTER", 1.0));
                list.add(new Pair("CONTINUING_PROMOTER", 1.0));
                list.add(new Pair("data INSTRUCTION", 1.0));
                list.add(new Pair("operator INSTRUCTION", 1.0));
                EnumeratedDistribution e = new EnumeratedDistribution(list);

                String sample = e.sample().toString();
                switch (sample) {
                    case "deletion":
                        codonList = deletionDefault(codonList, index);
                        break;

                    case "insertion":
                        codonList = insertionDefault(codonList, index, chromosomeIndex);
                        break;

                    case "VAR":
                        codonList = varDefault(codonList, index);
                        break;

                    case "BLOCKING_PROMOTER":
                        codonList = blockingPromoterDefault(codonList, index, chromosomeIndex);
                        break;

                    case "CONTINUING_PROMOTER":
                        codonList = continuingPromoterDefault(codonList, index, chromosomeIndex);
                        break;

                    case "data INSTRUCTION":
                        codonList = dataInstructionDefault(codonList, index, chromosomeIndex);
                        break;

                    case "operator INSTRUCTION":
                        codonList = operatorInstructionDefault(codonList, index);
                        break;

                    default:
                        throw new IllegalStateException("Unexpected value: " + sample);
                }
                return codonList;
            }


            default:
                throw new IllegalStateException("Unexpected value: " + codonList.get(index).getCodonType());
        }

    }

    @Override
    public Genome performAllMutations(Genome genome) {
        return null;
    }

    private ChromosomePair applySpecificCrossover(ChromosomePair chromosomePair, int crossoverIndex) { // TODO: a single index or more?
        return null;
    }

    @Override
    public ChromosomePair performAllCrossovers(ChromosomePair chromosomePair) {
        return null;
    }

    @Override
    public List<Chromosome> performMeiosis(Genome genome) {
        return null;
    }

    @Override
    public Genome fertilisation(List<Chromosome> haploidGenome1, List<Chromosome> haploidGenome2) {
        return null;
    }
}
