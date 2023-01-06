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

    // TODO: need to think about how to evaluate non head positions which returnValue == true. These should have a limited processing
    public Instruction peekSecondInstruction() {
        try {
             return instructionList.get(1);
        }
        catch (IndexOutOfBoundsException e) {
            return null;
        }
    }
}
