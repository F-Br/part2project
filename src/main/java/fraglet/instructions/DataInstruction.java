package fraglet.instructions;

import java.util.BitSet;

public class DataInstruction extends Instruction{
    private BitSet data;
    public DataInstruction(BitSet data){
        super(InstructionTag.DATA);
        this.data = data;
    }

    public BitSet getData() {
        return data;
    }

    public long getLongData() {
        if (data.length() == 0) { // handle empty bitset
            return 0L;
        }
        return data.toLongArray()[0];
    }

}
