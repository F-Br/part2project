package fraglet;

import fraglet.instructions.Instruction;

import java.util.LinkedList;

public class Fraglet {
    private LinkedList<Instruction> instructionList;

    public Fraglet(LinkedList<Instruction> instructionList) {
        this.instructionList = instructionList;
    }

    public LinkedList<Instruction> getInstructionList() {
        return instructionList;
    }

    public Instruction peekHeadInstruction() {
        return instructionList.peekFirst();
    }

    public boolean isEmpty() {
        return instructionList.isEmpty();
    }

    public void consumeNOPHeadInstructions() {
        while (instructionList.peekFirst().getInstructionTag().isNopWhenAtHead()) {
            instructionList.removeFirst();
        }
    }
}
