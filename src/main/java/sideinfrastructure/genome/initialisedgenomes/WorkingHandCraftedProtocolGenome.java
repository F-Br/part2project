package sideinfrastructure.genome.initialisedgenomes;

import fraglet.instructions.DataInstruction;
import fraglet.instructions.Instruction;
import fraglet.instructions.InstructionTag;
import sideinfrastructure.SideIdentifier;
import sideinfrastructure.genome.*;



import java.util.ArrayList;
import java.util.BitSet;

public class WorkingHandCraftedProtocolGenome extends Genome {

    static BitSet payload = BitSet.valueOf(new long[] {10005});
    static BitSet ExtractorID = BitSet.valueOf(new long[] {110});
    static BitSet ErrorID = BitSet.valueOf(new long[] {220});

    static BitSet senderInitialChromPID = BitSet.valueOf(new long[] {1000});
    static BitSet receiverInitialChromPID = BitSet.valueOf(new long[] {1100});
    static BitSet senderExtraChromPID = BitSet.valueOf(new long[] {1200});
    static BitSet receiverExtraChromPID = BitSet.valueOf(new long[] {1300});
    static BitSet nextPacket1 = BitSet.valueOf(new long[] {700000000});
    static BitSet nextPacket2 = BitSet.valueOf(new long[] {60000000});
    static BitSet nextPacket3 = BitSet.valueOf(new long[] {5000000});
    static BitSet finishCheck1 = BitSet.valueOf(new long[] {400000000});
    static BitSet finishCheck2 = BitSet.valueOf(new long[] {3000000});
    static BitSet finishCheck3 = BitSet.valueOf(new long[] {20000000});
    static BitSet finishCheck4 = BitSet.valueOf(new long[] {10000000});

    static  BitSet AACPayload = BitSet.valueOf(new long[] {10050000});
    static BitSet goodPacket = BitSet.valueOf(new long[] {1010000});
    static BitSet decodedMessage = BitSet.valueOf(new long[] {11000000});

    private static ArrayList<Chromosome> createChromosomeList() {
        Chromosome C0 = createChrom0();
        Chromosome C1 = createChrom1();
        Chromosome C2 = createChrom2();
        Chromosome C3 = createChrom3();

        ArrayList<Chromosome> chromosomes = new ArrayList<>();
        chromosomes.add(C0);
        chromosomes.add(C1);
        chromosomes.add(C2);
        chromosomes.add(C3);
        return chromosomes;
    }

    public WorkingHandCraftedProtocolGenome(SideIdentifier side) {
        super(side, createChromosomeList());
    }

    private static Chromosome createChrom0() {
        ArrayList<Codon> codonListC1 = new ArrayList<>();

        codonListC1.add(new Codon(CodonType.BLOCKING_PROMOTER, 100)); // P
        codonListC1.add(createCodonInstruction(InstructionTag.PROMOTE));
        codonListC1.add(createCodonDataInstruction(BitSet.valueOf(new long[] {105})));
        codonListC1.add(createCodonInstruction(InstructionTag.PROMOTE));
        codonListC1.add(createCodonDataInstruction(ExtractorID));
        codonListC1.add(createCodonInstruction(InstructionTag.PROMOTE));
        codonListC1.add(createCodonDataInstruction(BitSet.valueOf(new long[] {205})));
        codonListC1.add(createCodonInstruction(InstructionTag.PROMOTE));
        codonListC1.add(createCodonDataInstruction(ErrorID));
        codonListC1.add(createCodonInstruction(InstructionTag.PROMOTE));
        codonListC1.add(createCodonDataInstruction(BitSet.valueOf(new long[] {205})));
        codonListC1.add(createCodonInstruction(InstructionTag.PERMANENT_REPRESS)); // reprPerm
        codonListC1.add(createCodonDataInstruction(receiverInitialChromPID));
        codonListC1.add(createCodonInstruction(InstructionTag.PERMANENT_REPRESS)); // reprPerm
        codonListC1.add(createCodonDataInstruction(receiverExtraChromPID));

        codonListC1.add(new Codon(CodonType.BLOCKING_PROMOTER, 105)); // P
        codonListC1.add(createCodonInstruction(InstructionTag.MATCH_P)); // matchp
        codonListC1.add(createCodonDataInstruction(payload)); // payload
        codonListC1.add(createCodonInstruction(InstructionTag.CRC_T)); // crct
        codonListC1.add(createCodonInstruction(InstructionTag.SEND)); // send
        codonListC1.add(createCodonDataInstruction(AACPayload)); // AACPayload

        codonListC1.add(new Codon(CodonType.BLOCKING_PROMOTER, (int) ExtractorID.toLongArray()[0])); // P
        codonListC1.add(createCodonInstruction(InstructionTag.EXTRACT)); // extract
        codonListC1.add(createCodonInstruction(InstructionTag.REPRESS)); // repr
        codonListC1.add(new Codon(CodonType.VAR)); // var
        codonListC1.add(createCodonDataInstruction(ErrorID)); // SID
        codonListC1.add(createCodonDataInstruction(payload)); // payload
        codonListC1.add(createCodonInstruction(InstructionTag.STAR)); // star
        codonListC1.add(new Codon(CodonType.VAR)); // var

        codonListC1.add(new Codon(CodonType.CONTINUING_PROMOTER, 115));// C
        codonListC1.add(createCodonInstruction(InstructionTag.DELAY)); // delay
        codonListC1.add(createCodonDataInstruction(BitSet.valueOf(new long[] {10}))); // 10
        codonListC1.add(createCodonInstruction(InstructionTag.IF_MATCH)); // ifMatch
        codonListC1.add(new Codon(CodonType.VAR)); // var
        codonListC1.add(createCodonDataInstruction(nextPacket1)); // nextPacket1
        codonListC1.add(createCodonInstruction(InstructionTag.PROMOTE)); // prom
        codonListC1.add(createCodonDataInstruction(ExtractorID)); // ID
        codonListC1.add(new Codon(CodonType.VAR)); // var

        Chromosome C1 = new Chromosome(1000, codonListC1);

        return C1;
    }

    private static Chromosome createChrom1() {
        ArrayList<Codon> codonListC1 = new ArrayList<>();

        codonListC1.add(new Codon(CodonType.BLOCKING_PROMOTER, 200)); // P
        codonListC1.add(createCodonInstruction(InstructionTag.PROMOTE));
        codonListC1.add(createCodonDataInstruction(BitSet.valueOf(new long[] {305})));
        codonListC1.add(createCodonInstruction(InstructionTag.PERMANENT_REPRESS)); // reprPerm
        codonListC1.add(createCodonDataInstruction(senderInitialChromPID));
        codonListC1.add(createCodonInstruction(InstructionTag.PERMANENT_REPRESS)); // reprPerm
        codonListC1.add(createCodonDataInstruction(senderExtraChromPID));

        Chromosome C1 = new Chromosome(1100, codonListC1);

        return C1;
    }

    private static Chromosome createChrom2() {
        ArrayList<Codon> codonListC2 = new ArrayList<>();

        codonListC2.add(new Codon(CodonType.BLOCKING_PROMOTER, 205));  // P
        codonListC2.add(createCodonInstruction(InstructionTag.MATCH_P));     // matchp
        codonListC2.add(createCodonDataInstruction(nextPacket1)); // nextPacket1
        codonListC2.add(createCodonInstruction(InstructionTag.REMOVE)); // rm
        codonListC2.add(createCodonDataInstruction(nextPacket2)); // nextPacket2

        codonListC2.add(new Codon(CodonType.CONTINUING_PROMOTER, 210)); // C
        codonListC2.add(createCodonInstruction(InstructionTag.MATCH_P)); // matchp
        codonListC2.add(createCodonDataInstruction(nextPacket2)); // nextPacket2
        codonListC2.add(createCodonInstruction(InstructionTag.REMOVE)); // rm
        codonListC2.add(createCodonDataInstruction(nextPacket3)); // nextPacket3

        codonListC2.add(new Codon(CodonType.CONTINUING_PROMOTER, 215)); // C
        codonListC2.add(createCodonInstruction(InstructionTag.MATCH_P)); // matchp
        codonListC2.add(createCodonDataInstruction(nextPacket3)); // nextPacket3
        codonListC2.add(createCodonInstruction(InstructionTag.PROMOTE)); // prom
        codonListC2.add(createCodonDataInstruction(ExtractorID)); // ID
        codonListC2.add(createCodonInstruction(InstructionTag.SUM)); // sum
        codonListC2.add(createCodonInstruction(InstructionTag.NOP)); // nop
        codonListC2.add(createCodonDataInstruction(BitSet.valueOf(new long[] {1}))); // 1

        codonListC2.add(new Codon(CodonType.BLOCKING_PROMOTER, (int) ErrorID.toLongArray()[0])); // P
        codonListC2.add(createCodonInstruction(InstructionTag.MATCH_P)); // matchp
        codonListC2.add(createCodonInstruction(InstructionTag.ERROR)); // ERROR
        codonListC2.add(createCodonInstruction(InstructionTag.SPLIT)); // split
        codonListC2.add(createCodonInstruction(InstructionTag.SEND)); // send
        codonListC2.add(createCodonDataInstruction(finishCheck1)); // finishCheck1
        codonListC2.add(createCodonInstruction(InstructionTag.STAR)); // star
        codonListC2.add(createCodonInstruction(InstructionTag.SPLIT)); // split
        codonListC2.add(createCodonInstruction(InstructionTag.PROMOTE)); // prom
        codonListC2.add(createCodonDataInstruction(ExtractorID)); // ID
        codonListC2.add(createCodonInstruction(InstructionTag.SUM)); // sum
        codonListC2.add(createCodonInstruction(InstructionTag.NOP)); // nop
        codonListC2.add(createCodonDataInstruction(BitSet.valueOf(new long[] {1}))); // 1

        codonListC2.add(new Codon(CodonType.BLOCKING_PROMOTER, 225)); // P
        codonListC2.add(createCodonInstruction(InstructionTag.MATCH_P)); // matchp
        codonListC2.add(createCodonDataInstruction(finishCheck4)); // finishCheck4
        codonListC2.add(createCodonInstruction(InstructionTag.SPLIT)); // split
        codonListC2.add(createCodonInstruction(InstructionTag.SEND)); // send
        codonListC2.add(createCodonInstruction(InstructionTag.SUBMIT)); // submit
        codonListC2.add(createCodonInstruction(InstructionTag.NUL)); // nul
        codonListC2.add(createCodonInstruction(InstructionTag.STAR)); // star
        codonListC2.add(createCodonDataInstruction(finishCheck4)); // finishCheck4
        codonListC2.add(createCodonInstruction(InstructionTag.NUL)); // nul

        codonListC2.add(new Codon(CodonType.CONTINUING_PROMOTER, 230)); // C
        codonListC2.add(createCodonInstruction(InstructionTag.MATCH)); // match
        codonListC2.add(createCodonDataInstruction(finishCheck1)); // finishCheck1
        codonListC2.add(createCodonInstruction(InstructionTag.MATCH)); // match
        codonListC2.add(createCodonDataInstruction(finishCheck3)); // finishCheck3
        codonListC2.add(createCodonDataInstruction(finishCheck4)); // finishCheck4

        codonListC2.add(new Codon(CodonType.CONTINUING_PROMOTER, 235)); // C
        codonListC2.add(createCodonInstruction(InstructionTag.MATCH)); // match
        codonListC2.add(createCodonDataInstruction(finishCheck1)); // finishCheck1
        codonListC2.add(createCodonInstruction(InstructionTag.MATCH)); // match
        codonListC2.add(createCodonDataInstruction(finishCheck2)); // finishCheck2
        codonListC2.add(createCodonDataInstruction(finishCheck3)); // finishCheck3

        codonListC2.add(new Codon(CodonType.CONTINUING_PROMOTER, 240)); // C
        codonListC2.add(createCodonInstruction(InstructionTag.MATCH)); // match
        codonListC2.add(createCodonDataInstruction(finishCheck1)); // finishCheck1
        codonListC2.add(createCodonDataInstruction(finishCheck2)); // finishCheck2

        Chromosome C2 = new Chromosome(1200, codonListC2);

        return C2;
    }

    private static Chromosome createChrom3() {
        ArrayList<Codon> codonListC3 = new ArrayList<>();

        codonListC3.add(new Codon(CodonType.BLOCKING_PROMOTER, 305)); // P
        codonListC3.add(createCodonInstruction(InstructionTag.MATCH_P)); // matchp
        codonListC3.add(createCodonDataInstruction(AACPayload)); // AACPayload
        codonListC3.add(createCodonInstruction(InstructionTag.CRC_T_CHECK)); // crcCheck
        codonListC3.add(createCodonDataInstruction(goodPacket)); // goodPacket
        codonListC3.add(createCodonDataInstruction(AACPayload)); // AACPayload

        codonListC3.add(new Codon(CodonType.CONTINUING_PROMOTER, 310)); // C
        codonListC3.add(createCodonInstruction(InstructionTag.MATCH_P)); // matchp
        codonListC3.add(createCodonDataInstruction(goodPacket)); // goodPacket
        codonListC3.add(createCodonInstruction(InstructionTag.REMOVE)); // rm
        codonListC3.add(createCodonDataInstruction(decodedMessage)); // decodedMessage

        codonListC3.add(new Codon(CodonType.CONTINUING_PROMOTER, 315)); // C
        codonListC3.add(createCodonInstruction(InstructionTag.MATCH_P)); // matchp
        codonListC3.add(createCodonDataInstruction(decodedMessage)); // goodPacket
        codonListC3.add(createCodonInstruction(InstructionTag.FORK)); // fork
        codonListC3.add(createCodonInstruction(InstructionTag.INSERT)); // insert
        codonListC3.add(createCodonInstruction(InstructionTag.SEND)); // send

        Chromosome C3 = new Chromosome(1300, codonListC3);

        return C3;
    }

    private static Codon createCodonInstruction(InstructionTag instructionTag) {
        return new Codon(CodonType.INSTRUCTION, new Instruction(instructionTag));
    }

    private static Codon createCodonDataInstruction(BitSet data) {
        return new Codon(CodonType.INSTRUCTION, new DataInstruction(data));
    }
}
