package fraglet;

import fraglet.instructions.Instruction;

import java.util.LinkedList;
import java.util.Objects;

public class Fraglet {
    private LinkedList<Instruction> instructionList;

    public Fraglet(LinkedList<Instruction> instructionList) { // TODO: overload this to accept a fraglet
        this.instructionList = instructionList;
    }

    public Fraglet(Fraglet fraglet) {
        this.instructionList = new LinkedList<>(fraglet.getInstructionList()); // TODO: might not be ok, need a deep copy
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

    public void addAll(Fraglet fraglet) {
        for (Instruction instruction : fraglet.getInstructionList()) { // TODO: deep copy?
            addLast(instruction);
        }
    }

    public void addLast(Instruction instruction) {
        instructionList.addLast(instruction);
    }

    @Override
    public String toString() {
        return "Fraglet{" +
                "instructionList=" + instructionList +
                '}';
    }
}
