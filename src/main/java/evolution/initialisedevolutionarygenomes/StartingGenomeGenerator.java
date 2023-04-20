package evolution.initialisedevolutionarygenomes;

import evolution.EvolutionaryGenome;
import fraglet.instructions.DataInstruction;
import fraglet.instructions.Instruction;
import fraglet.instructions.InstructionTag;
import org.apache.commons.math3.util.Pair;
import sideinfrastructure.genome.Codon;
import sideinfrastructure.genome.CodonType;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.LinkedList;
import java.util.List;

public class StartingGenomeGenerator {

    private final int numChromosomePairs = 6;
    private final int chromStartingValue;
    private final int dataTagStartingValue;
    private final int chromosomeLength;

    public StartingGenomeGenerator(int dataTagStartingValue, int chromStartingValue, int chromosomeLength) {
        this.dataTagStartingValue = dataTagStartingValue;
        this.chromStartingValue = chromStartingValue;
        this.chromosomeLength = chromosomeLength;
    }

    public EvolutionaryGenome generateStartingGenome() {
        List<Pair<List<Codon>, List<Codon>>> startingChromosomePairList = new ArrayList<>(numChromosomePairs);

        // --------------------
        // chromosome 1
        int currentStartingValue = chromStartingValue + chromosomeLength * 0;
        List<Codon> chromosome1A = new LinkedList<>();
        List<Codon> chromosome1B = new LinkedList<>();

        // sender starter
        chromosome1A.add(new Codon(CodonType.BLOCKING_PROMOTER, currentStartingValue + 1));
        chromosome1B.add(new Codon(CodonType.BLOCKING_PROMOTER, currentStartingValue + 1));

        chromosome1A.add(new Codon(CodonType.INSTRUCTION, new Instruction(InstructionTag.PERMANENT_REPRESS)));
        chromosome1A.add(new Codon(CodonType.INSTRUCTION, new DataInstruction(BitSet.valueOf(new long[] {chromStartingValue + chromosomeLength * 0}))));
        chromosome1A.add(new Codon(CodonType.INSTRUCTION, new Instruction(InstructionTag.PERMANENT_REPRESS)));
        chromosome1A.add(new Codon(CodonType.INSTRUCTION, new DataInstruction(BitSet.valueOf(new long[] {chromStartingValue + chromosomeLength * 3}))));
        chromosome1A.add(new Codon(CodonType.INSTRUCTION, new Instruction(InstructionTag.PERMANENT_REPRESS)));
        chromosome1A.add(new Codon(CodonType.INSTRUCTION, new DataInstruction(BitSet.valueOf(new long[] {chromStartingValue + chromosomeLength * 4}))));
        chromosome1A.add(new Codon(CodonType.INSTRUCTION, new Instruction(InstructionTag.PERMANENT_REPRESS)));
        chromosome1A.add(new Codon(CodonType.INSTRUCTION, new DataInstruction(BitSet.valueOf(new long[] {chromStartingValue + chromosomeLength * 5}))));
        chromosome1A.add(new Codon(CodonType.INSTRUCTION, new Instruction(InstructionTag.PROMOTE)));
        chromosome1A.add(new Codon(CodonType.INSTRUCTION, new DataInstruction(BitSet.valueOf(new long[] {chromStartingValue + chromosomeLength * 1 + 1}))));

        chromosome1B.add(new Codon(CodonType.INSTRUCTION, new Instruction(InstructionTag.PERMANENT_REPRESS)));
        chromosome1B.add(new Codon(CodonType.INSTRUCTION, new DataInstruction(BitSet.valueOf(new long[] {chromStartingValue + chromosomeLength * 0}))));
        chromosome1B.add(new Codon(CodonType.INSTRUCTION, new Instruction(InstructionTag.PERMANENT_REPRESS)));
        chromosome1B.add(new Codon(CodonType.INSTRUCTION, new DataInstruction(BitSet.valueOf(new long[] {chromStartingValue + chromosomeLength * 3}))));
        chromosome1B.add(new Codon(CodonType.INSTRUCTION, new Instruction(InstructionTag.PERMANENT_REPRESS)));
        chromosome1B.add(new Codon(CodonType.INSTRUCTION, new DataInstruction(BitSet.valueOf(new long[] {chromStartingValue + chromosomeLength * 4}))));
        chromosome1B.add(new Codon(CodonType.INSTRUCTION, new Instruction(InstructionTag.PERMANENT_REPRESS)));
        chromosome1B.add(new Codon(CodonType.INSTRUCTION, new DataInstruction(BitSet.valueOf(new long[] {chromStartingValue + chromosomeLength * 5}))));
        chromosome1B.add(new Codon(CodonType.INSTRUCTION, new Instruction(InstructionTag.PROMOTE)));
        chromosome1B.add(new Codon(CodonType.INSTRUCTION, new DataInstruction(BitSet.valueOf(new long[] {chromStartingValue + chromosomeLength * 1 + 1}))));

        // empty
        chromosome1A.add(new Codon(CodonType.BLOCKING_PROMOTER, currentStartingValue + 30));
        chromosome1B.add(new Codon(CodonType.BLOCKING_PROMOTER, currentStartingValue + 30));

        chromosome1A.add(new Codon(CodonType.INSTRUCTION, new Instruction(InstructionTag.NOP)));
        chromosome1A.add(new Codon(CodonType.INSTRUCTION, new Instruction(InstructionTag.NOP)));
        chromosome1A.add(new Codon(CodonType.INSTRUCTION, new Instruction(InstructionTag.NOP)));
        chromosome1B.add(new Codon(CodonType.INSTRUCTION, new Instruction(InstructionTag.NOP)));
        chromosome1B.add(new Codon(CodonType.INSTRUCTION, new Instruction(InstructionTag.NOP)));
        chromosome1B.add(new Codon(CodonType.INSTRUCTION, new Instruction(InstructionTag.NOP)));

        // empty
        chromosome1A.add(new Codon(CodonType.BLOCKING_PROMOTER, currentStartingValue + 60));
        chromosome1B.add(new Codon(CodonType.BLOCKING_PROMOTER, currentStartingValue + 60));

        chromosome1A.add(new Codon(CodonType.INSTRUCTION, new Instruction(InstructionTag.NOP)));
        chromosome1A.add(new Codon(CodonType.INSTRUCTION, new Instruction(InstructionTag.NOP)));
        chromosome1A.add(new Codon(CodonType.INSTRUCTION, new Instruction(InstructionTag.NOP)));
        chromosome1B.add(new Codon(CodonType.INSTRUCTION, new Instruction(InstructionTag.NOP)));
        chromosome1B.add(new Codon(CodonType.INSTRUCTION, new Instruction(InstructionTag.NOP)));
        chromosome1B.add(new Codon(CodonType.INSTRUCTION, new Instruction(InstructionTag.NOP)));


        startingChromosomePairList.add(0, new Pair<>(chromosome1A, chromosome1B));


        // --------------------
        // chromosome 2
        currentStartingValue = chromStartingValue + chromosomeLength * 1;
        List<Codon> chromosome2A = new LinkedList<>();
        List<Codon> chromosome2B = new LinkedList<>();


        // extract
        chromosome2A.add(new Codon(CodonType.BLOCKING_PROMOTER, currentStartingValue + 1));
        chromosome2B.add(new Codon(CodonType.BLOCKING_PROMOTER, currentStartingValue + 1));

        chromosome2A.add(new Codon(CodonType.INSTRUCTION, new Instruction(InstructionTag.EXTRACT)));
        chromosome2A.add(new Codon(CodonType.INSTRUCTION, new DataInstruction(BitSet.valueOf(new long[] {dataTagStartingValue}))));
        chromosome2A.add(new Codon(CodonType.INSTRUCTION, new DataInstruction(BitSet.valueOf(new long[] {0}))));

        chromosome2B.add(new Codon(CodonType.INSTRUCTION, new Instruction(InstructionTag.EXTRACT)));
        chromosome2B.add(new Codon(CodonType.INSTRUCTION, new DataInstruction(BitSet.valueOf(new long[] {dataTagStartingValue}))));
        chromosome2B.add(new Codon(CodonType.INSTRUCTION, new DataInstruction(BitSet.valueOf(new long[] {0}))));


        // send
        chromosome2A.add(new Codon(CodonType.CONTINUING_PROMOTER, currentStartingValue + 30));
        chromosome2B.add(new Codon(CodonType.CONTINUING_PROMOTER, currentStartingValue + 30));

        chromosome2A.add(new Codon(CodonType.INSTRUCTION, new Instruction(InstructionTag.MATCH)));
        chromosome2A.add(new Codon(CodonType.INSTRUCTION, new DataInstruction(BitSet.valueOf(new long[] {dataTagStartingValue}))));
        chromosome2A.add(new Codon(CodonType.INSTRUCTION, new Instruction(InstructionTag.SEND)));
        chromosome2A.add(new Codon(CodonType.INSTRUCTION, new DataInstruction(BitSet.valueOf(new long[] {dataTagStartingValue + 4}))));

        chromosome2B.add(new Codon(CodonType.INSTRUCTION, new Instruction(InstructionTag.MATCH)));
        chromosome2B.add(new Codon(CodonType.INSTRUCTION, new DataInstruction(BitSet.valueOf(new long[] {dataTagStartingValue}))));
        chromosome2B.add(new Codon(CodonType.INSTRUCTION, new Instruction(InstructionTag.SEND)));
        chromosome2B.add(new Codon(CodonType.INSTRUCTION, new DataInstruction(BitSet.valueOf(new long[] {dataTagStartingValue + 4}))));


        // empty
        chromosome2A.add(new Codon(CodonType.BLOCKING_PROMOTER, currentStartingValue + 60));
        chromosome2B.add(new Codon(CodonType.BLOCKING_PROMOTER, currentStartingValue + 60));

        chromosome2A.add(new Codon(CodonType.INSTRUCTION, new Instruction(InstructionTag.NOP)));
        chromosome2A.add(new Codon(CodonType.INSTRUCTION, new Instruction(InstructionTag.NOP)));
        chromosome2A.add(new Codon(CodonType.INSTRUCTION, new Instruction(InstructionTag.NOP)));
        chromosome2B.add(new Codon(CodonType.INSTRUCTION, new Instruction(InstructionTag.NOP)));
        chromosome2B.add(new Codon(CodonType.INSTRUCTION, new Instruction(InstructionTag.NOP)));
        chromosome2B.add(new Codon(CodonType.INSTRUCTION, new Instruction(InstructionTag.NOP)));

        startingChromosomePairList.add(1, new Pair<>(chromosome2A, chromosome2B));

        // --------------------
        // chromosome 3
        currentStartingValue = chromStartingValue + chromosomeLength * 2;
        List<Codon> chromosome3A = new LinkedList<>();
        List<Codon> chromosome3B = new LinkedList<>();


        // empty
        chromosome3A.add(new Codon(CodonType.BLOCKING_PROMOTER, currentStartingValue + 1));
        chromosome3B.add(new Codon(CodonType.BLOCKING_PROMOTER, currentStartingValue + 1));

        chromosome3A.add(new Codon(CodonType.INSTRUCTION, new Instruction(InstructionTag.NOP)));
        chromosome3A.add(new Codon(CodonType.INSTRUCTION, new Instruction(InstructionTag.NOP)));
        chromosome3A.add(new Codon(CodonType.INSTRUCTION, new Instruction(InstructionTag.NOP)));
        chromosome3B.add(new Codon(CodonType.INSTRUCTION, new Instruction(InstructionTag.NOP)));
        chromosome3B.add(new Codon(CodonType.INSTRUCTION, new Instruction(InstructionTag.NOP)));
        chromosome3B.add(new Codon(CodonType.INSTRUCTION, new Instruction(InstructionTag.NOP)));

        // empty
        chromosome3A.add(new Codon(CodonType.BLOCKING_PROMOTER, currentStartingValue + 30));
        chromosome3B.add(new Codon(CodonType.BLOCKING_PROMOTER, currentStartingValue + 30));

        chromosome3A.add(new Codon(CodonType.INSTRUCTION, new Instruction(InstructionTag.NOP)));
        chromosome3A.add(new Codon(CodonType.INSTRUCTION, new Instruction(InstructionTag.NOP)));
        chromosome3A.add(new Codon(CodonType.INSTRUCTION, new Instruction(InstructionTag.NOP)));
        chromosome3B.add(new Codon(CodonType.INSTRUCTION, new Instruction(InstructionTag.NOP)));
        chromosome3B.add(new Codon(CodonType.INSTRUCTION, new Instruction(InstructionTag.NOP)));
        chromosome3B.add(new Codon(CodonType.INSTRUCTION, new Instruction(InstructionTag.NOP)));

        // empty
        chromosome3A.add(new Codon(CodonType.BLOCKING_PROMOTER, currentStartingValue + 60));
        chromosome3B.add(new Codon(CodonType.BLOCKING_PROMOTER, currentStartingValue + 60));

        chromosome3A.add(new Codon(CodonType.INSTRUCTION, new Instruction(InstructionTag.NOP)));
        chromosome3A.add(new Codon(CodonType.INSTRUCTION, new Instruction(InstructionTag.NOP)));
        chromosome3A.add(new Codon(CodonType.INSTRUCTION, new Instruction(InstructionTag.NOP)));
        chromosome3B.add(new Codon(CodonType.INSTRUCTION, new Instruction(InstructionTag.NOP)));
        chromosome3B.add(new Codon(CodonType.INSTRUCTION, new Instruction(InstructionTag.NOP)));
        chromosome3B.add(new Codon(CodonType.INSTRUCTION, new Instruction(InstructionTag.NOP)));


        startingChromosomePairList.add(2, new Pair<>(chromosome3A, chromosome3B));


        // --------------------
        // chromosome 4
        currentStartingValue = chromStartingValue + chromosomeLength * 3;
        List<Codon> chromosome4A = new LinkedList<>();
        List<Codon> chromosome4B = new LinkedList<>();


        // reciever starter
        chromosome4A.add(new Codon(CodonType.BLOCKING_PROMOTER, currentStartingValue + 1));
        chromosome4B.add(new Codon(CodonType.BLOCKING_PROMOTER, currentStartingValue + 1));

        chromosome4A.add(new Codon(CodonType.INSTRUCTION, new Instruction(InstructionTag.PERMANENT_REPRESS)));
        chromosome4A.add(new Codon(CodonType.INSTRUCTION, new DataInstruction(BitSet.valueOf(new long[] {chromStartingValue + chromosomeLength * 3}))));
        chromosome4A.add(new Codon(CodonType.INSTRUCTION, new Instruction(InstructionTag.PERMANENT_REPRESS)));
        chromosome4A.add(new Codon(CodonType.INSTRUCTION, new DataInstruction(BitSet.valueOf(new long[] {chromStartingValue + chromosomeLength * 0}))));
        chromosome4A.add(new Codon(CodonType.INSTRUCTION, new Instruction(InstructionTag.PERMANENT_REPRESS)));
        chromosome4A.add(new Codon(CodonType.INSTRUCTION, new DataInstruction(BitSet.valueOf(new long[] {chromStartingValue + chromosomeLength * 1}))));
        chromosome4A.add(new Codon(CodonType.INSTRUCTION, new Instruction(InstructionTag.PERMANENT_REPRESS)));
        chromosome4A.add(new Codon(CodonType.INSTRUCTION, new DataInstruction(BitSet.valueOf(new long[] {chromStartingValue + chromosomeLength * 2}))));
        chromosome4A.add(new Codon(CodonType.INSTRUCTION, new Instruction(InstructionTag.PROMOTE)));
        chromosome4A.add(new Codon(CodonType.INSTRUCTION, new DataInstruction(BitSet.valueOf(new long[] {chromStartingValue + chromosomeLength * 4 + 1}))));

        chromosome4B.add(new Codon(CodonType.INSTRUCTION, new Instruction(InstructionTag.PERMANENT_REPRESS)));
        chromosome4B.add(new Codon(CodonType.INSTRUCTION, new DataInstruction(BitSet.valueOf(new long[] {chromStartingValue + chromosomeLength * 3}))));
        chromosome4B.add(new Codon(CodonType.INSTRUCTION, new Instruction(InstructionTag.PERMANENT_REPRESS)));
        chromosome4B.add(new Codon(CodonType.INSTRUCTION, new DataInstruction(BitSet.valueOf(new long[] {chromStartingValue + chromosomeLength * 0}))));
        chromosome4B.add(new Codon(CodonType.INSTRUCTION, new Instruction(InstructionTag.PERMANENT_REPRESS)));
        chromosome4B.add(new Codon(CodonType.INSTRUCTION, new DataInstruction(BitSet.valueOf(new long[] {chromStartingValue + chromosomeLength * 1}))));
        chromosome4B.add(new Codon(CodonType.INSTRUCTION, new Instruction(InstructionTag.PERMANENT_REPRESS)));
        chromosome4B.add(new Codon(CodonType.INSTRUCTION, new DataInstruction(BitSet.valueOf(new long[] {chromStartingValue + chromosomeLength * 2}))));
        chromosome4B.add(new Codon(CodonType.INSTRUCTION, new Instruction(InstructionTag.PROMOTE)));
        chromosome4B.add(new Codon(CodonType.INSTRUCTION, new DataInstruction(BitSet.valueOf(new long[] {chromStartingValue + chromosomeLength * 4 + 1}))));


        // empty
        chromosome4A.add(new Codon(CodonType.BLOCKING_PROMOTER, currentStartingValue + 30));
        chromosome4B.add(new Codon(CodonType.BLOCKING_PROMOTER, currentStartingValue + 30));

        chromosome4A.add(new Codon(CodonType.INSTRUCTION, new Instruction(InstructionTag.NOP)));
        chromosome4A.add(new Codon(CodonType.INSTRUCTION, new Instruction(InstructionTag.NOP)));
        chromosome4A.add(new Codon(CodonType.INSTRUCTION, new Instruction(InstructionTag.NOP)));
        chromosome4B.add(new Codon(CodonType.INSTRUCTION, new Instruction(InstructionTag.NOP)));
        chromosome4B.add(new Codon(CodonType.INSTRUCTION, new Instruction(InstructionTag.NOP)));
        chromosome4B.add(new Codon(CodonType.INSTRUCTION, new Instruction(InstructionTag.NOP)));

        // empty
        chromosome4A.add(new Codon(CodonType.BLOCKING_PROMOTER, currentStartingValue + 60));
        chromosome4B.add(new Codon(CodonType.BLOCKING_PROMOTER, currentStartingValue + 60));

        chromosome4A.add(new Codon(CodonType.INSTRUCTION, new Instruction(InstructionTag.NOP)));
        chromosome4A.add(new Codon(CodonType.INSTRUCTION, new Instruction(InstructionTag.NOP)));
        chromosome4A.add(new Codon(CodonType.INSTRUCTION, new Instruction(InstructionTag.NOP)));
        chromosome4B.add(new Codon(CodonType.INSTRUCTION, new Instruction(InstructionTag.NOP)));
        chromosome4B.add(new Codon(CodonType.INSTRUCTION, new Instruction(InstructionTag.NOP)));
        chromosome4B.add(new Codon(CodonType.INSTRUCTION, new Instruction(InstructionTag.NOP)));


        startingChromosomePairList.add(3, new Pair<>(chromosome4A, chromosome4B));


        // --------------------
        // chromosome 5
        currentStartingValue = chromStartingValue + chromosomeLength * 4;
        List<Codon> chromosome5A = new LinkedList<>();
        List<Codon> chromosome5B = new LinkedList<>();


        // recieve
        chromosome5A.add(new Codon(CodonType.BLOCKING_PROMOTER, currentStartingValue + 1));
        chromosome5B.add(new Codon(CodonType.BLOCKING_PROMOTER, currentStartingValue + 1));

        chromosome5A.add(new Codon(CodonType.INSTRUCTION, new Instruction(InstructionTag.MATCH)));
        chromosome5A.add(new Codon(CodonType.INSTRUCTION, new Instruction(InstructionTag.NOP)));
        chromosome5A.add(new Codon(CodonType.INSTRUCTION, new Instruction(InstructionTag.NOP)));

        chromosome5B.add(new Codon(CodonType.INSTRUCTION, new Instruction(InstructionTag.MATCH)));
        chromosome5B.add(new Codon(CodonType.INSTRUCTION, new Instruction(InstructionTag.NOP)));
        chromosome5B.add(new Codon(CodonType.INSTRUCTION, new Instruction(InstructionTag.NOP)));

        // empty
        chromosome5A.add(new Codon(CodonType.BLOCKING_PROMOTER, currentStartingValue + 30));
        chromosome5B.add(new Codon(CodonType.BLOCKING_PROMOTER, currentStartingValue + 30));

        chromosome5A.add(new Codon(CodonType.INSTRUCTION, new Instruction(InstructionTag.NOP)));
        chromosome5A.add(new Codon(CodonType.INSTRUCTION, new Instruction(InstructionTag.NOP)));
        chromosome5A.add(new Codon(CodonType.INSTRUCTION, new Instruction(InstructionTag.NOP)));
        chromosome5B.add(new Codon(CodonType.INSTRUCTION, new Instruction(InstructionTag.NOP)));
        chromosome5B.add(new Codon(CodonType.INSTRUCTION, new Instruction(InstructionTag.NOP)));
        chromosome5B.add(new Codon(CodonType.INSTRUCTION, new Instruction(InstructionTag.NOP)));

        // empty
        chromosome5A.add(new Codon(CodonType.BLOCKING_PROMOTER, currentStartingValue + 60));
        chromosome5B.add(new Codon(CodonType.BLOCKING_PROMOTER, currentStartingValue + 60));

        chromosome5A.add(new Codon(CodonType.INSTRUCTION, new Instruction(InstructionTag.NOP)));
        chromosome5A.add(new Codon(CodonType.INSTRUCTION, new Instruction(InstructionTag.NOP)));
        chromosome5A.add(new Codon(CodonType.INSTRUCTION, new Instruction(InstructionTag.NOP)));
        chromosome5B.add(new Codon(CodonType.INSTRUCTION, new Instruction(InstructionTag.NOP)));
        chromosome5B.add(new Codon(CodonType.INSTRUCTION, new Instruction(InstructionTag.NOP)));
        chromosome5B.add(new Codon(CodonType.INSTRUCTION, new Instruction(InstructionTag.NOP)));


        startingChromosomePairList.add(4, new Pair<>(chromosome5A, chromosome5B));

        // --------------------
        // chromosome 6
        currentStartingValue = chromStartingValue + chromosomeLength * 5;
        List<Codon> chromosome6A = new LinkedList<>();
        List<Codon> chromosome6B = new LinkedList<>();

        // empty
        chromosome6A.add(new Codon(CodonType.BLOCKING_PROMOTER, currentStartingValue + 1));
        chromosome6B.add(new Codon(CodonType.BLOCKING_PROMOTER, currentStartingValue + 1));

        chromosome6A.add(new Codon(CodonType.INSTRUCTION, new Instruction(InstructionTag.NOP)));
        chromosome6A.add(new Codon(CodonType.INSTRUCTION, new Instruction(InstructionTag.NOP)));
        chromosome6A.add(new Codon(CodonType.INSTRUCTION, new Instruction(InstructionTag.NOP)));
        chromosome6B.add(new Codon(CodonType.INSTRUCTION, new Instruction(InstructionTag.NOP)));
        chromosome6B.add(new Codon(CodonType.INSTRUCTION, new Instruction(InstructionTag.NOP)));
        chromosome6B.add(new Codon(CodonType.INSTRUCTION, new Instruction(InstructionTag.NOP)));

        // empty
        chromosome6A.add(new Codon(CodonType.BLOCKING_PROMOTER, currentStartingValue + 30));
        chromosome6B.add(new Codon(CodonType.BLOCKING_PROMOTER, currentStartingValue + 30));

        chromosome6A.add(new Codon(CodonType.INSTRUCTION, new Instruction(InstructionTag.NOP)));
        chromosome6A.add(new Codon(CodonType.INSTRUCTION, new Instruction(InstructionTag.NOP)));
        chromosome6A.add(new Codon(CodonType.INSTRUCTION, new Instruction(InstructionTag.NOP)));
        chromosome6B.add(new Codon(CodonType.INSTRUCTION, new Instruction(InstructionTag.NOP)));
        chromosome6B.add(new Codon(CodonType.INSTRUCTION, new Instruction(InstructionTag.NOP)));
        chromosome6B.add(new Codon(CodonType.INSTRUCTION, new Instruction(InstructionTag.NOP)));

        // empty
        chromosome6A.add(new Codon(CodonType.BLOCKING_PROMOTER, currentStartingValue + 60));
        chromosome6B.add(new Codon(CodonType.BLOCKING_PROMOTER, currentStartingValue + 60));

        chromosome6A.add(new Codon(CodonType.INSTRUCTION, new Instruction(InstructionTag.NOP)));
        chromosome6A.add(new Codon(CodonType.INSTRUCTION, new Instruction(InstructionTag.NOP)));
        chromosome6A.add(new Codon(CodonType.INSTRUCTION, new Instruction(InstructionTag.NOP)));
        chromosome6B.add(new Codon(CodonType.INSTRUCTION, new Instruction(InstructionTag.NOP)));
        chromosome6B.add(new Codon(CodonType.INSTRUCTION, new Instruction(InstructionTag.NOP)));
        chromosome6B.add(new Codon(CodonType.INSTRUCTION, new Instruction(InstructionTag.NOP)));


        startingChromosomePairList.add(5, new Pair<>(chromosome6A, chromosome6B));


        return new EvolutionaryGenome(startingChromosomePairList);
    }
}
