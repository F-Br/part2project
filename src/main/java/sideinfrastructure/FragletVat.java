package sideinfrastructure;

import clock.StepClock;
import fraglet.Fraglet;
import fraglet.FragletParser;
import fraglet.FragletReleasePair;
import fraglet.instructions.DataInstruction;
import fraglet.instructions.Instruction;
import fraglet.instructions.InstructionTag;

import java.util.*;

public class FragletVat {
    // this will store seperately the match instructions and the "free fraglets"
    // will need to keep track of the heads of all the existing fraglets to do matches quickly

    private final SideIdentifier side;

    LinkedList<Fraglet> fragletList = new LinkedList<>();
    LinkedList<Fraglet> matchFragletList = new LinkedList<>();
    HashMap<InstructionTag, Integer> headInstructionPresentMap = new HashMap<>(); // TODO: consider, might have been better using a list pointing to the actual fraglets.
    HashMap<BitSet, Integer> dataHeadInstuctionPresentMap = new HashMap<>(); // TODO: STILL... need to decide details of this, is it going to be a bitset or an integer?
    PriorityQueue<FragletReleasePair> delayedFragletQueue = new PriorityQueue<>();

    private FragletParser fragletParser;

    public FragletVat(SideIdentifier side) { // TODO: might also need to develop this to work with other datastructures (maybe?)
        this.side = side;
    }

    public void setFragletParser(FragletParser fragletParser) {
        this.fragletParser = fragletParser;
    }

    public SideIdentifier getSide() {
        return side;
    }

    // TODO: may want to be able to do flushes to kill off very old and stagnant fraglets.

    public void addFraglet(Fraglet fraglet) {
        if (fraglet.isEmpty()) {
            return;
        }

        fraglet.consumeNOPHeadInstructions(); // TODO: maybe remove? See note in the method in fraglet. should almost definitely rework/delete

        if (fraglet.peekHeadInstruction().getInstructionTag().isMatchInstruction()) {
            matchFragletList.add(fraglet);
        }
        else {
            int newCount = headInstructionPresentMap.getOrDefault(fraglet.peekHeadInstruction().getInstructionTag(), 0) + 1;
            headInstructionPresentMap.put(fraglet.peekHeadInstruction().getInstructionTag(), newCount);
            if (fraglet.peekHeadInstruction() instanceof DataInstruction) {
                BitSet data = ((DataInstruction) fraglet.peekHeadInstruction()).getData();
                int newDataCount = dataHeadInstuctionPresentMap.getOrDefault(data, 0) + 1;
                dataHeadInstuctionPresentMap.put(data, newDataCount);
            }
            fragletList.add(fraglet);
        }
    }




    // TODO NEXT: add check if any matches function
    //          : add random selection function (must include changing head values) <- can just getFirst after a shuffle
    //          : add a delayFragletList and create functions for dealing with this (may need to modify addFraglet)

    public int resolveMatches() { // TODO: could speed this up using dirty bits/newly added list.
        int numberOfMatches = 0;
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
                else { // match found
                    numberOfMatches++;
                    fragletParser.parseFragletMatch(matchFraglet, matchedFraglet);
                    break;
                }
            }
        }
        return numberOfMatches;
    }


    private boolean fragletMatchFound(Instruction matchInstruction) {
        // check head tag match
        if (headInstructionPresentMap.getOrDefault(matchInstruction.getInstructionTag(), 0) > 0) {
            // if DATA, then check if match
            if (matchInstruction instanceof DataInstruction) {
                if (dataHeadInstuctionPresentMap.getOrDefault(((DataInstruction) matchInstruction).getData(), 0) > 0) {
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
                            removeSpecificFraglet(matchFraglet);
                            removeSpecificFraglet(freeFraglet);
                            return freeFraglet;
                        }
                    }
                }
            }
            else {
                for (Fraglet freeFraglet : fragletList) {
                    if (freeFraglet.peekHeadInstruction().getInstructionTag() == instructionToMatch.getInstructionTag()) {
                        removeSpecificFraglet(matchFraglet);
                        removeSpecificFraglet(freeFraglet);
                        return freeFraglet;
                    }
                }
            }
            throw new IllegalStateException("fraglet match apparently found, but the actual fraglet couldn't be located"); // TODO: could be activated if fraglet is "match match" - need to ensure match match doesn't get added on counts
        }

        return null;
    }


    public Fraglet processIfMatchRequest(Instruction instructionToMatch) {
        if (fragletMatchFound(instructionToMatch)) {
            if (instructionToMatch instanceof DataInstruction) {
                for (Fraglet freeFraglet : fragletList) {
                    if (freeFraglet.peekHeadInstruction() instanceof DataInstruction) {
                        if (((DataInstruction) freeFraglet.peekHeadInstruction()).getData() == ((DataInstruction) instructionToMatch).getData()) {
                            removeSpecificFraglet(freeFraglet);
                            return freeFraglet;
                        }
                    }
                }
            }
            else {
                for (Fraglet freeFraglet : fragletList) {
                    if (freeFraglet.peekHeadInstruction().getInstructionTag() == instructionToMatch.getInstructionTag()) {
                        removeSpecificFraglet(freeFraglet);
                        return freeFraglet;
                    }
                }
            }
            throw new IllegalStateException("fraglet match apparently found, but the actual fraglet couldn't be located"); // TODO: could be activated if fraglet is "match match" - need to ensure match match doesn't get added on counts
        }
        return null;
    }


    private void removeSpecificFraglet(Fraglet fragletToRemove) {
        // need to remove from lists
        // need to remove present head checks

        if (fragletToRemove.peekHeadInstruction().getInstructionTag().isMatchInstruction()) {
            matchFragletList.remove(fragletToRemove); // TODO: this is quite inneficient as takes O(n)
        }
        else { // if not match instruction
            // TODO: check if good implementation, gives extra layer of security (returns false if not in the list), but is an extra check whenever removing
            if (fragletList.remove(fragletToRemove)) { // TODO: this is quite inneficient as takes O(n)
                processRemovedFraglet(fragletToRemove);
            }
            else { // TODO: is this necessary? see earlier todo about checking implementation
                throw new IllegalArgumentException("fragletToRemove given doesn't exist in the fragletList");
            }
        }
    }

    private void processRemovedFraglet(Fraglet fragletToRemove) {
        if (fragletToRemove.peekHeadInstruction().getInstructionTag().isMatchInstruction()) { // if match
            return;
        }
        else { // if not match instruction
            InstructionTag headInstructionTag = fragletToRemove.peekHeadInstruction().getInstructionTag();
            int newInstructionCount = headInstructionPresentMap.getOrDefault(headInstructionTag, 0) - 1;
            headInstructionPresentMap.put(headInstructionTag, newInstructionCount);

            if (headInstructionTag == InstructionTag.DATA) {
                BitSet data = ((DataInstruction) fragletToRemove.peekHeadInstruction()).getData(); // TODO: check correct data type
                int newDataCount = dataHeadInstuctionPresentMap.getOrDefault(data, 0) - 1;
                dataHeadInstuctionPresentMap.put(data, newDataCount);
            }
        }
    }


    public void addToDelayFragletQueue(Fraglet postDelayFraglet, long delay) {
        delayedFragletQueue.add(new FragletReleasePair(postDelayFraglet, StepClock.getCurrentStepCount() + delay));
    }


    public int processFragletDelayQueue() {
        if (delayedFragletQueue.isEmpty()) {
            return 0;
        }

        long currentStep = StepClock.getCurrentStepCount();
        int numberOfFragletsRemovedFromDelayQueue = 0;

        while (delayedFragletQueue.peek().getReleaseStep() <= currentStep) {
            fragletParser.parseFraglet(delayedFragletQueue.poll().getFraglet());
            numberOfFragletsRemovedFromDelayQueue++;
            if (delayedFragletQueue.isEmpty()) {
                break;
            }
        }

        return numberOfFragletsRemovedFromDelayQueue;
    }

    public Fraglet removeFirstFragletInFragletList() {
        if (fragletList.isEmpty()) {
            return null;
        }

        Fraglet removedFraglet = fragletList.removeFirst();
        processRemovedFraglet(removedFraglet);
        return removedFraglet;
    }


    public void shuffleFragletList() {
        Collections.shuffle(fragletList);
    }


    public void shuffleMatchList() {
        Collections.shuffle(matchFragletList);
    }



}
