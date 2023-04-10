package sideinfrastructure;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Random;

public class ChallengeQuestion {
    public final int numberOfRows = 32; // TODO: rather than hardwire this, could have it randomly generated each new challenge and send the result over to receiver side
    public final int numberOfBytesInRow = 4;
    private ArrayList<BitSet> totalChallenge;
    private Random rnd = new Random();
    private boolean extractedAtleastOnce = false;
    private boolean sentAfterExtraction = false;

    public ChallengeQuestion() {
        createNewChallengeQuestion();
    }

    public void createNewChallengeQuestion() {
        totalChallenge = new ArrayList<>(numberOfRows);
        for (int i = 0; i < numberOfRows; i++) {
            byte[] randomBytes = new byte[numberOfBytesInRow];
            rnd.nextBytes(randomBytes);
            totalChallenge.add(BitSet.valueOf(randomBytes));
        }
    }

    public BitSet getRow(long index) {
        if (index >= numberOfRows) {
            return null;
        }
        extractedAtleastOnce = true;
        return totalChallenge.get((int) index);
    }

    public BitSet fetchRowForFitness(long index) {
        if (index >= numberOfRows) {
            return null;
        }
        return totalChallenge.get((int) index);
    }

    public void checkIfSentAfterExtraction() {
        if ((!sentAfterExtraction) && extractedAtleastOnce) {
            sentAfterExtraction = true;
        }
    }

    public boolean isExtractedAtleastOnce() {
        return extractedAtleastOnce;
    }

    public boolean isSentAfterExtraction() {
        return sentAfterExtraction;
    }
}


