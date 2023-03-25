package fraglet.instructions;

public class Instruction {
    private InstructionTag instructionTag;
    public Instruction(InstructionTag instructionTag) {
        this.instructionTag = instructionTag;
    }

    public InstructionTag getInstructionTag() {
        return instructionTag;
    }

    // TODO: Should have some structural mechanism so that a DATA instruction just cannot be created here, and must be done through the DataInstruction class.


    @Override
    public String toString() {
        return instructionTag.name();
    }
}
