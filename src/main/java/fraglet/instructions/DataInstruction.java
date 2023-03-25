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
    } // NOTE: the returned data is a reference and so should only ever be used for reads and never writes to it

    public long getLongData() {
        if (data.length() == 0) { // handle empty bitset
            return 0L;
        }
        return data.toLongArray()[0];
    }


    @Override
    public String toString() {
        return String.valueOf(getLongData());
        // if want bitset do: return data;
    }
}
