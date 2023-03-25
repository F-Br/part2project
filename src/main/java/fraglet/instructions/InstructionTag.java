package fraglet.instructions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public enum InstructionTag {
    DATA(false, true, false),

    CRC_T(false, true, false),
    CRC_T_CHECK,
    ERROR,
    EXTRACT,
    INSERT,
    SUBMIT,
    DELAY,
    IF_MATCH,
    REMOVE, // maybe returns value?
    PROMOTE,
    REPRESS,
    PERMANENT_REPRESS,

    MATCH(false, false, true),
    MATCH_P(false, false, true),
    SEND,
    SPLIT,
    STAR(true, false, false),
    SUM(false, true, false),
    NOP(true, false, false),
    NUL,
    FORK;



    static Random rand = new Random();


    private boolean isNopWhenAtHead = false;
    private boolean returnsValue = false;
    private boolean isMatchInstruction = false;

    private InstructionTag() {
    }

    private InstructionTag(boolean isNopWhenAtHead, boolean returnsValue, boolean isMatchInstruction) {
        this.isNopWhenAtHead = isNopWhenAtHead;
        this.returnsValue = returnsValue;
        this.isMatchInstruction = isMatchInstruction;
    }

    public boolean isNopWhenAtHead() {
        return isNopWhenAtHead;
    }

    public boolean isReturnsValue() {
        return returnsValue;
    }

    public boolean isMatchInstruction() {
        return isMatchInstruction;
    }

    public static InstructionTag getRandomOperatorInstructionTag() {
        List<InstructionTag> validOperatorInstructions = new ArrayList<>(Arrays.asList(InstructionTag.values()));
        validOperatorInstructions.remove(InstructionTag.DATA);
        return validOperatorInstructions.get(rand.nextInt(validOperatorInstructions.size()));
    }


}
