package vats;

import fraglet.Fraglet;
import fraglet.instructions.DataInstruction;
import fraglet.instructions.Instruction;
import fraglet.instructions.InstructionTag;

import java.util.BitSet;
import java.util.HashMap;
import java.util.LinkedList;

public class FragletVat {
    // this will store seperately the match instructions and the "free fraglets"
    // will need to keep track of the heads of all the existing fraglets to do matches quickly

    LinkedList<Fraglet> fragletList = new LinkedList<>();
    LinkedList<Fraglet> matchFragletList = new LinkedList<>();
    HashMap<InstructionTag, Integer> headInstructionPresentMap = new HashMap<>();
    HashMap<BitSet, Integer> dataHeadInstuctionPresentMap = new HashMap<>(); // TODO: STILL... need to decide details of this, is it going to be a bitset or an integer?

    public void addFraglet(Fraglet fraglet) {
        if (fraglet.isEmpty()) {
            return;
        }

        fraglet.consumeNOPHeadInstructions();

        if (fraglet.peekHeadInstruction().getInstructionTag().isMatchInstruction()) {
            matchFragletList.add(fraglet);
        }
        else {
            int newCount = headInstructionPresentMap.get(fraglet.peekHeadInstruction().getInstructionTag()) + 1;
            headInstructionPresentMap.put(fraglet.peekHeadInstruction().getInstructionTag(), newCount);
            if (fraglet.peekHeadInstruction() instanceof DataInstruction) {
                BitSet data = ((DataInstruction) fraglet.peekHeadInstruction()).getData();
                int newDataCount = dataHeadInstuctionPresentMap.get(data) + 1;
                dataHeadInstuctionPresentMap.put(data, newDataCount);
            }
            fragletList.add(fraglet);
        }
    }







}
