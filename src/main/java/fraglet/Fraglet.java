package fraglet;

import fraglet.instructions.Instruction;

import java.util.LinkedList;

public class Fraglet {
    private LinkedList<Instruction> instructionList;

    public Fraglet(LinkedList<Instruction> instructionList) { // TODO: overload this to accept a fraglet
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

    public Instruction pollHeadInstruction() {
        return instructionList.pollFirst();
    }

    public void consumeNOPHeadInstructions() { // TODO: this is potentially very bad, as how would it deal with an empty fraglet?????? Probably best to remove.
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

    public void addFirst(Instruction instruction) {
        instructionList.addFirst(instruction);
    }

    public int size() {
        return instructionList.size();
    }

    public Instruction get(int index) {
        return instructionList.get(index);
    }

    public void remove(int index) {
        instructionList.remove(index);
    }

    public void addAll(LinkedList<Instruction> seq) { // TODO: overload this to accept a fraglet
        instructionList.addAll(seq);
    }

    public void addLast(Instruction instruction) {
        instructionList.addLast(instruction);
    }
}
