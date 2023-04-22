package sideinfrastructure.genome;

import clock.StepClock;
import fraglet.Fraglet;
import fraglet.instructions.DataInstruction;
import fraglet.instructions.Instruction;
import sideinfrastructure.FragletVat;
import sideinfrastructure.SideIdentifier;

import java.util.*;

import static java.util.Collections.binarySearch;

public class Genome {

    private final SideIdentifier side;

    // map CID -> chromosome pointer
    // sorted list of promoter ID
    // map PID -> CID
    // map CID -> (#prom, #repr)
    HashMap<Integer, HashSet<Chromosome>> chromPIDToChromosomeMap = new HashMap<>();

    public ArrayList<Integer> getSortedPIDList() {
        return sortedPIDList;
    }

    ArrayList<Integer> sortedPIDList = new ArrayList<>(); // TODO: should this be an array set?
    ArrayList<Integer> sortedChromPIDList = new ArrayList<>(); // TODO: should this be an array set?
    HashMap<Integer, HashSet<Integer>> PIDToChromPIDLocationMap = new HashMap<>();
    public FragletVat fragletVat;
    HashMap<Chromosome, Integer> chromosomeTotalScoreMap = new HashMap<>();
    HashMap<Integer, Integer> chromPIDToRepressorCountMap = new HashMap<>();


    private BitSet DEFAULT_VAR_VALUE = BitSet.valueOf(new long[] {0});

    private Random rnd = new Random();


    Queue<GeneExpressionDetails> geneExpressionDetailsQueue = new LinkedList<>();


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

            // initialise chromosomeTotalScoreMap
            chromosomeTotalScoreMap.put(chromosome, chromosome.getTotalScore());

            // initialise all chromPID repress count to 0
            chromPIDToRepressorCountMap.put(chromPID, 0);


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
                addPromoter(geneExpressionDetails.getPID());
                break;
            case REPRESSOR:
                addRepressor(geneExpressionDetails.getPID());
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

            /*
            Check if queue is empty. If so return 0

            While peeking on queue, the next element has a release time less than the current time:
                if the next element is repressor:
                pop it
                call removeRepressor()
                continue to next loop

                if the next element is a promoter:
                check if promote score is zero, if so pop promoter and call removePromoter()

                if promote score is > 0, call parseGenome and break (parse genome will find the geneExpressionDetail, and take any parameters and then remove it and call removePromoter() on it

             */

            GeneExpressionDetails oldGeneExpressionDetails = geneExpressionDetailsQueue.peek();

            old_gene_expression_final_logic:
            switch (oldGeneExpressionDetails.getGeneExpressionType()) {
                case REPRESSOR:
                    geneExpressionDetailsQueue.poll();
                    removeRepressor(oldGeneExpressionDetails.getPID());
                    break old_gene_expression_final_logic;

                case PROMOTER:
                    int validPID = oldGeneExpressionDetails.getPID();

                    HashSet<Integer> chromPIDs;
                    if (sortedChromPIDList.contains(validPID)) { // if validPID is a chromPID
                        chromPIDs = new HashSet<>();
                        chromPIDs.add(validPID);
                    }
                    else {
                        chromPIDs = PIDToChromPIDLocationMap.get(validPID);
                    }

                    for (int chromPID : chromPIDs) {
                        if (chromPIDToRepressorCountMap.get(chromPID) > 0) { // ignore any chromPIDs which are repressed
                            continue;
                        }
                        for (Chromosome chromosome : chromPIDToChromosomeMap.get(chromPID)) {
                            if (chromosome.getSortedPIDList().contains(validPID)) { // if a pid at a chromosome has a positive promote score, then parse the genome and exit
                                if (getPromoteScoreOfPromoter(chromosome, validPID) > 0) {
                                    // logic for a parsing happening:
                                    numberPromotersActivated += 1;
                                    parseGenome(validPID);
                                    break old_gene_expression_final_logic;
                                }
                            }
                        }
                    }
                    // impossible to promote:
                    geneExpressionDetailsQueue.poll();
                    removePromoter(validPID);
                    break old_gene_expression_final_logic;

                default:
                    throw new IllegalArgumentException("Have not implemented case for " + oldGeneExpressionDetails.getGeneExpressionType().name());

            }

            if (geneExpressionDetailsQueue.isEmpty()) { // if queue now empty, then break
                break;
            }
        }

        return numberPromotersActivated;

    }


    // TODO: probably good to remove this function
    public void parseGenome(GeneExpressionDetails geneExpressionDetails) { // TODO: fundamentally this is a BUG, geneExpressionDetail can be for a repressor and therefore you shouldn't parse this...
        if (geneExpressionDetails.getOwnVarSupplied()) {
            parseGenome(geneExpressionDetails.getPID(), geneExpressionDetails.getOptionalVarValue());
        }
        else {
            parseGenome(geneExpressionDetails.getPID(), DEFAULT_VAR_VALUE);
        }
    }

    // TODO: probably good to remove this function, and have the one below take the same arguments as this but with a default var value all the time
    public void parseGenome(Integer validPID) {
        parseGenome(validPID, DEFAULT_VAR_VALUE);
    }

    public void parseGenome(Integer validPID, BitSet VARValue) { // TODO: what if everything is repressed?
        // check if it is a chromosome, and if so replace with its delegated promotion site
        if (sortedChromPIDList.contains(validPID)) {
            for (Chromosome chromosome : chromPIDToChromosomeMap.get(validPID)) { // TODO: if just did the first chromosome in this set then would only create 2 promotions and not potentially 4. Might want to change this
                parseGenome(chromosome.getDelegatePID(), VARValue);
            }
            return;
        }

        boolean usedGeneExpressionDetailFromQueue = false;
        // Check geneExpressionQueue for a promoter parameter, and if so record it and remove from the queue
        if (!(geneExpressionDetailsQueue.isEmpty())) {
            for (GeneExpressionDetails geneExpressionDetails : geneExpressionDetailsQueue) {
                if (validPID == geneExpressionDetails.getPID()) { // if match in PID with a geneExpressionDetail
                    if (geneExpressionDetails.getGeneExpressionType() == GeneExpressionType.PROMOTER) {
                        if (geneExpressionDetails.getOwnVarSupplied()) {
                            VARValue = geneExpressionDetails.getOptionalVarValue();
                        }
                        geneExpressionDetailsQueue.remove(geneExpressionDetails);
                        usedGeneExpressionDetailFromQueue = true;
                        break;
                    }
                }
            }
        }

        HashSet<Integer> chromPIDToParse = PIDToChromPIDLocationMap.get(validPID);
        for (int chromPID : chromPIDToParse) {
            if (chromPIDToRepressorCountMap.get(chromPID) > 0) { // ignore any chromPIDs which are repressed
                continue;
            }
            for (Chromosome validChromosome : chromPIDToChromosomeMap.get(chromPID)) {
                if (validChromosome.PIDToIndexMap.get(validPID) != null) { // if the PID is in the chromosome
                    parseChromosome(validChromosome, validPID, VARValue);
                }
            }
        }

        // If a promoter taken from geneExpression, then promoter will have been removed from the queue, therefore now need to remove promote score from all with the same PIDs
        if (usedGeneExpressionDetailFromQueue) {
            removePromoter(validPID);
        }
    }


    private void parseChromosome(Chromosome validChromosome, Integer validPID, BitSet VARValue) {

        for (int index : validChromosome.PIDToIndexMap.get(validPID)) {
            // TODO: finished 22/2/23 - to start off tomorrow here, have a look at 261 and go from there
            ArrayList<Codon> codonList = validChromosome.codonList;
            Codon currentCodon = codonList.get(index);
            LinkedList<Instruction> workingFragletContents = new LinkedList<>();

            if (validChromosome.PIDtoPIDExpressionMap.get(currentCodon.getPID()).getIndividualPromoteScore() > 0) { // check starting promoter has positive promotion score
                index++;

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
                                fragletVat.addFraglet(new Fraglet(workingFragletContents));
                            }
                            workingFragletContents = new LinkedList<>();

                            if (validChromosome.PIDtoPIDExpressionMap.get(currentCodon.getPID()).getIndividualPromoteScore() == 0) {
                                break codon_parsing_loop;
                            }
                            break;

                        case BLOCKING_PROMOTER:
                            if (!workingFragletContents.isEmpty()) {
                                fragletVat.addFraglet(new Fraglet(workingFragletContents));
                            }
                            workingFragletContents = new LinkedList<>();
                            break codon_parsing_loop;

                        default:
                            throw new IllegalStateException("This codon has not been accounted for when parsing (" + currentCodon.getCodonType().name() + ")");
                    }
                    index++;
                }

                // for if end of codon list
                if (!workingFragletContents.isEmpty()) {
                    fragletVat.addFraglet(new Fraglet(workingFragletContents));
                }
            }
        }
    }



    public void forceInitialParseGenome(Chromosome validChromosome, int delegateValidPID) {

        ArrayList<Codon> codonList = validChromosome.codonList;
        HashSet<Integer> indexesToSynthesiseFrom = validChromosome.PIDToIndexMap.get(delegateValidPID);

        // for each index synthesise from the codon list from that index
        for (int index : indexesToSynthesiseFrom) {

            LinkedList<Instruction> workingFragletContents = new LinkedList<>();
            index++;
            Codon currentCodon;

            codon_parsing_loop:
            while (index < codonList.size()) {

                currentCodon = codonList.get(index);

                switch (currentCodon.getCodonType()) {
                    case INSTRUCTION:
                        workingFragletContents.addLast(currentCodon.getInstruction());
                        break;

                    case VAR:
                        workingFragletContents.addLast(new DataInstruction(DEFAULT_VAR_VALUE));
                        break;

                    case CONTINUING_PROMOTER:
                        if (!workingFragletContents.isEmpty()) {
                            fragletVat.addFraglet(new Fraglet(workingFragletContents));
                        }
                        workingFragletContents = new LinkedList<>();

                        if (validChromosome.PIDtoPIDExpressionMap.get(currentCodon.getPID()).getIndividualPromoteScore() == 0) {
                            break codon_parsing_loop;
                        }
                        break;

                    case BLOCKING_PROMOTER:
                        if (!workingFragletContents.isEmpty()) {
                            fragletVat.addFraglet(new Fraglet(workingFragletContents));
                        }
                        workingFragletContents = new LinkedList<>();
                        break codon_parsing_loop;

                    default:
                        throw new IllegalStateException("This codon has not been accounted for when parsing (" + currentCodon.getCodonType().name() + ")");
                }
                index++;
            }

            // for if end of codon list
            if (!workingFragletContents.isEmpty()) {
                fragletVat.addFraglet(new Fraglet(workingFragletContents));
            }
        }
    }



    public void addPromoter(int validPID) {
        // get chromosomes for this PID and then call addPromoters on these chromosomes references
        HashSet<Integer> chromPIDs = new HashSet<>();
        if (sortedChromPIDList.contains(validPID)) { // if validPID is a chromPID
            chromPIDs.add(validPID);
        }
        else {
            chromPIDs = PIDToChromPIDLocationMap.get(validPID);
        }
        for (int chromPID : chromPIDs) {
            HashSet<Chromosome> chromosomes = chromPIDToChromosomeMap.get(chromPID);
            for (Chromosome chromosome : chromosomes) { // update chromosome score count
                chromosomeTotalScoreMap.put(chromosome, chromosome.addPromoter(validPID));
                // TODO: may also want to add argument expression pairs here aswell... not sure
            }
        }
    }

    public void removePromoter(int validPID) {
        HashSet<Integer> chromPIDs = new HashSet<>();
        if (sortedChromPIDList.contains(validPID)) { // if validPID is a chromPID
            chromPIDs.add(validPID);
        }
        else {
            chromPIDs = PIDToChromPIDLocationMap.get(validPID);
        }
        for (int chromPID : chromPIDs) {
            HashSet<Chromosome> chromosomes = chromPIDToChromosomeMap.get(chromPID);
            for (Chromosome chromosome : chromosomes) { // update chromosome score count
                chromosomeTotalScoreMap.put(chromosome, chromosome.removePromoter(validPID));
                // TODO: may also want to add argument expression pairs here aswell... not sure
            }
        }
    }

    public void addRepressor(int validPID) {
        // check if chromosome in which case you should change current mapping
        if (sortedChromPIDList.contains(validPID)) { // if is chromPID
            chromPIDToRepressorCountMap.put(validPID, chromPIDToRepressorCountMap.get(validPID) + 1);
            return;
        }

        HashSet<Integer> chromPIDs = PIDToChromPIDLocationMap.get(validPID);
        for (int chromPID : chromPIDs) {
            HashSet<Chromosome> chromosomes = chromPIDToChromosomeMap.get(chromPID);
            for (Chromosome chromosome : chromosomes) {
                chromosomeTotalScoreMap.put(chromosome, chromosome.addRepressor(validPID));
                // TODO: may also want to add argument expression pairs here aswell... not sure
            }
        }
    }

    public void removeRepressor(int validPID) {
        if (sortedChromPIDList.contains(validPID)) {
            chromPIDToRepressorCountMap.put(validPID, chromPIDToRepressorCountMap.get(validPID) - 1);
            return;
        }

        HashSet<Integer> chromPIDs = PIDToChromPIDLocationMap.get(validPID);
        for (int chromPID : chromPIDs) {
            HashSet<Chromosome> chromosomes = chromPIDToChromosomeMap.get(chromPID);
            for (Chromosome chromosome : chromosomes) {
                chromosomeTotalScoreMap.put(chromosome, chromosome.removeRepressor(validPID));
                // TODO: may also want to add argument expression pairs here aswell... not sure
            }
        }
    }


    private int calculateTotalScore() {
        int totalScore = 0;
        for (int chromPID : sortedChromPIDList) {
            if (chromPIDToRepressorCountMap.get(chromPID) > 0) {
                continue;
            }
            HashSet<Chromosome> chromosomes = chromPIDToChromosomeMap.get(chromPID);

            for (Chromosome chromosome : chromosomes) {
                totalScore += chromosome.getTotalScore();
            }
        }

        return totalScore;
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

    public int findClosestPID(int attemptedPID) { // can return chromPID or promPID
        int closestPromPID = calculateClosestValue(sortedPIDList, attemptedPID);
        int closestChromPID = calculateClosestValue(sortedChromPIDList, attemptedPID);

        int diffPromPID = Math.abs(attemptedPID - closestPromPID);
        int diffChromPID = Math.abs(attemptedPID - closestChromPID);

        if (diffChromPID < diffPromPID) {
            return closestChromPID;
        } else {
            return closestPromPID;
        }
    }

    // CHANGE TO weightedSelectionOfChromosomes
    public Chromosome weightedRandomSelectionOfChromosomes() { // TODO: check that totalScore is not 0, if so return null (?)
        int totalScore = calculateTotalScore();
        if (totalScore == 0) {
            return null;
        }

        int cumulativeScoreTarget = rnd.nextInt(totalScore + 1);
        int currentCumulativeScore = 0;

        for (int chromPID : sortedChromPIDList) {
            if (chromPIDToRepressorCountMap.get(chromPID) > 0) {
                continue;
            }
            for (Chromosome chromosome : chromPIDToChromosomeMap.get(chromPID)) {
                int chromosomeTotalScore = chromosome.getTotalScore();
                if (chromosomeTotalScore == 0) {
                    continue;
                }
                currentCumulativeScore += chromosomeTotalScore;


                if (cumulativeScoreTarget <= currentCumulativeScore) {
                    return chromosome;
                }
            }
        }

        throw new IllegalStateException("No PID found, should have reached a valid PID or returned null");
    }

    private int getPromoteScoreOfPromoter(Chromosome validChromosome, int validPID) { // requires the pic be in the chromosome
        if (chromPIDToRepressorCountMap.get(validChromosome.getChromPID()) > 0) { // if chromosome promote score is zero
            return 0;
        }
        return validChromosome.PIDtoPIDExpressionMap.get(validPID).getSumOfAllPIDInChromosomePromoteScore();
    }


    public Integer weightedRandomSelectionOfPID() {
        Chromosome selectedChromosome = weightedRandomSelectionOfChromosomes();
        if (selectedChromosome == null) {
            return null;
        }
        return selectedChromosome.weightedRandomSelectionOfPID();
    }



    public ArrayList<Integer> getSortedChromPIDList() {
        return sortedChromPIDList;
    }

    public HashSet<Chromosome> getChromosomesFromChromPID(int validChromPID) {
        return chromPIDToChromosomeMap.get(validChromPID);
    }

}
