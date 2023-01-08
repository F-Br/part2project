package sideinfrastructure;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Random;

public class ChallengeQuestion {
    private int numberOfRows = 32; // TODO: rather than hardwire this, could have it randomly generated each new challenge and send the result over to receiver side
    private int numberOfBytesInRow = 4;
    private ArrayList<BitSet> totalChallenge = new ArrayList<>(numberOfRows);
    private Random rnd = new Random();

    public ChallengeQuestion() {
        createNewChallengeQuestion();
    }

    public void createNewChallengeQuestion() {
        for (int i = 0; i < numberOfRows; i++) {
            byte[] randomBytes = new byte[numberOfBytesInRow];
            rnd.nextBytes(randomBytes);
            totalChallenge.set(i, BitSet.valueOf(randomBytes));
        }
    }

    public BitSet getRow(long index) {
        if (index >= numberOfRows) {
            return null;
        }
        return totalChallenge.get((int) index);
    }

}


