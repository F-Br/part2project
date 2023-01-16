package sideinfrastructure;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Random;

public class ChallengeAnswer {
    private int numberOfRows = 32; // TODO: rather than hardwire this, could have it randomly generated each new challenge and send the result over to receiver side
    private int numberOfBytesInRow = 4;
    private int numberOfBitsInRow = 8 * numberOfBytesInRow;
    private ArrayList<BitSet> totalAnswer;

    public ChallengeAnswer() {
        resetChallengeAnswer();
    }

    public void resetChallengeAnswer() {
        totalAnswer = new ArrayList<>(numberOfRows);
        for (int i = 0; i < numberOfRows; i++) {
            totalAnswer.add(new BitSet(numberOfBytesInRow));
        }
    }

    public void setRow(long longIndex, BitSet bitsetAnswer) {
        if (longIndex >= numberOfRows) {
            return;
        }

        int index = (int) longIndex;
        int answerSize = bitsetAnswer.size();
        totalAnswer.get(index).clear();
        if (answerSize <= numberOfBitsInRow) {
            totalAnswer.get(index).or(bitsetAnswer);
        } else {
            totalAnswer.get(index).or(bitsetAnswer.get(0, numberOfBitsInRow));
        }
    }

    public BitSet getRow(int index) {
        return totalAnswer.get(index);
    }

}
