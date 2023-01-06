package sideinfrastructure.genome;

import fraglet.instructions.DataInstruction;
import fraglet.instructions.Instruction;
import fraglet.instructions.InstructionTag;

public class Codon {
    private CodonType codonType;
    private Instruction instruction;
    private Integer PID;

    public CodonType getCodonType() {
        return codonType;
    }

    public Instruction getInstruction() {
        return instruction;
    }

    public Integer getPID() {
        return PID;
    }

    public Codon(CodonType codonType) {
        if (codonType == CodonType.VAR) {
            this.codonType = CodonType.VAR;
        }
        else {
            throw new IllegalArgumentException("Codon constructor with just codonType argument may only be CodonType.VAR, but was instead given " + codonType.name());
        }
    }

    public Codon(CodonType codonType, Integer PID) {
        if ((codonType == CodonType.BLOCKING_PROMOTER) || (codonType == CodonType.CONTINUING_PROMOTER)) {
            this.codonType = codonType;
            this.PID = PID;
        }
        else {
            throw new IllegalArgumentException("Codon constructor with codonType and integer arguments may only be CodonType.BLOCKING_PROMOTER or CodonType.CONTINUING_PROMOTER, but was instead given " + codonType.name());
        }
    }

    public Codon(CodonType codonType, Instruction instruction) {
        if (codonType == CodonType.INSTRUCTION) {
            if (instruction.getInstructionTag() == InstructionTag.DATA) {
                if !(instruction instanceof DataInstruction) {
                    throw new IllegalArgumentException("Data instruction must be constructed from DataInstruction class");
                }
            }
            this.codonType = codonType;
            this.instruction = instruction;
        }
        else {
            throw new IllegalArgumentException("Codon constructor with codonType and instruction arguments may only be CodonType.INSTRUCTION, but was instead given " + codonType.name());
        }
    }
}
