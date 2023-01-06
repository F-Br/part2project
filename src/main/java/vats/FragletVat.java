package vats;

import clock.StepClock;
import fraglet.Fraglet;
import fraglet.FragletReleasePair;
import fraglet.instructions.DataInstruction;
import fraglet.instructions.Instruction;
import fraglet.instructions.InstructionTag;

import java.util.*;

public class FragletVat {
    // this will store seperately the match instructions and the "free fraglets"
    // will need to keep track of the heads of all the existing fraglets to do matches quickly

    LinkedList<Fraglet> fragletList = new LinkedList<>();
    LinkedList<Fraglet> matchFragletList = new LinkedList<>();
    HashMap<InstructionTag, Integer> headInstructionPresentMap = new HashMap<>(); // TODO: consider, might have been better using a list pointing to the actual fraglets.
    HashMap<BitSet, Integer> dataHeadInstuctionPresentMap = new HashMap<>(); // TODO: STILL... need to decide details of this, is it going to be a bitset or an integer?
    PriorityQueue<FragletReleasePair> delayedFragletQueue = new PriorityQueue<>();
    // TODO: may want to be able to do flushes to kill off very old and stagnant fraglets.

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




    // TODO NEXT: add check if any matches function
    //          : add random selection function (must include changing head values) <- can just getFirst after a shuffle
    //          : add a delayFragletList and create functions for dealing with this (may need to modify addFraglet)

    public void resolveMatches() { // TODO: could speed this up using dirty bits/newly added list.
        shuffleMatchList();

        for (Fraglet matchFraglet : matchFragletList) {
            Instruction searchInstruction = matchFraglet.peekSecondInstruction();
            if (searchInstruction == null) {
                // TODO: should probably delete this match instruction then.
                continue;
            }
            else {
                Fraglet matchedFraglet = findFragletMatch(matchFraglet);
                if (matchedFraglet == null) { // if no match found
                    continue;
                }
                else {
                    fragletParse(matchFraglet, matchedFraglet);
                    break;
                }
            }
        }
    }


    private boolean fragletMatchFound(Instruction matchInstruction) {
        // check head tag match
        if (headInstructionPresentMap.get(matchInstruction.getInstructionTag()) > 0) {
            // if DATA, then check if match
            if (matchInstruction instanceof DataInstruction) {
                if (dataHeadInstuctionPresentMap.get(((DataInstruction) matchInstruction).getData()) > 0) {
                    return true;
                }
                else {
                    return false;
                }
            }
            else {
                return true;
            }
        }
        else {
            return false;
        }
    }


    private Fraglet findFragletMatch(Fraglet matchFraglet) { // TODO: note the input of this must be an instruction which starts with match (maybe should add check for this?)
        // removes the free fraglet if a match is found, however does not remove the match fraglet.

        Instruction instructionToMatch = matchFraglet.peekSecondInstruction();

        if (fragletMatchFound(instructionToMatch)) {

            if (instructionToMatch instanceof DataInstruction) {
                for (Fraglet freeFraglet : fragletList) {
                    if (freeFraglet.peekHeadInstruction() instanceof DataInstruction) {
                        if (((DataInstruction) freeFraglet.peekHeadInstruction()).getData() == ((DataInstruction) instructionToMatch).getData()) {
                            removeFraglet(matchFraglet);
                            removeFraglet(freeFraglet);
                            return freeFraglet;
                        }
                    }
                }
            }
            else {
                for (Fraglet freeFraglet : fragletList) {
                    if (freeFraglet.peekHeadInstruction().getInstructionTag() == instructionToMatch.getInstructionTag()) {
                        removeFraglet(matchFraglet);
                        removeFraglet(freeFraglet);
                        return freeFraglet;
                    }
                }
            }
            throw new IllegalStateException("fraglet match apparently found, but the actual fraglet couldn't be located");
        }

        return null;
    }

    private void removeFraglet(Fraglet fragletToRemove) {
        // need to remove from lists
        // need to remove present head checks

        if (fragletToRemove.peekHeadInstruction().getInstructionTag().isMatchInstruction()) {
            matchFragletList.remove(fragletToRemove); // TODO: this is quite inneficient as takes O(n)
        }
        else { // if not match instruction
            fragletList.remove(fragletToRemove); // TODO: this is quite inneficient as takes O(n)
            InstructionTag headInstructionTag = fragletToRemove.peekHeadInstruction().getInstructionTag();
            int newInstructionCount = headInstructionPresentMap.get(headInstructionTag) - 1;
            headInstructionPresentMap.put(headInstructionTag, newInstructionCount);

            if (headInstructionTag == InstructionTag.DATA) {
                BitSet data = ((DataInstruction) fragletToRemove.peekHeadInstruction()).getData(); // TODO: check correct data type
                int newDataCount = dataHeadInstuctionPresentMap.get(data) - 1;
                dataHeadInstuctionPresentMap.put(data, newDataCount);
            }
        }
    }


    public void addToDelayFragletQueue(Fraglet postDelayFraglet, int delay) {
        delayedFragletQueue.add(new FragletReleasePair(postDelayFraglet, StepClock.getCurrentStepCount() + delay)); // TODO: just double check the implicit int to long casting has worked here.
    }


    private void processFragletDelayQueue() {
        if (delayedFragletQueue.isEmpty()) {
            return;
        }

        long currentStep = StepClock.getCurrentStepCount();

        while (delayedFragletQueue.peek().getReleaseStep() <= currentStep) {
            fragletParse(delayedFragletQueue.poll().getFraglet());
            if (delayedFragletQueue.isEmpty()) {
                break;
            }
        }
    }


    public void shuffleFragletList() {
        Collections.shuffle(fragletList);
    }


    public void shuffleMatchList() {
        Collections.shuffle(matchFragletList);
    }



}
