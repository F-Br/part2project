package fraglet.instructions;

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


}
