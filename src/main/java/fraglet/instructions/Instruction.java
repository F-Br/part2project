package fraglet.instructions;

public class Instruction {
    InstructionTag instructionTag;
    public Instruction(InstructionTag instructionTag) {
        this.instructionTag = instructionTag;
    }

    // TODO: Should have some structural mechanism so that a DATA instruction just cannot be created here, and must be done through the DataInstruction class.
}
