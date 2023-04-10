package evolution.fitnessfunctions;

import sideinfrastructure.ChallengeAnswer;
import sideinfrastructure.ChallengeQuestion;

import java.util.BitSet;

public class DefaultFitnessFunction extends FitnessFunction {
    private final int numBitsInRow;
    private final int numRows;
    private final int bitMatchesTargetThreshold;

    public DefaultFitnessFunction(int numBytesInRow, int numRows) {
        this.numBitsInRow = numBytesInRow * 8;
        this.numRows = numRows;
        this.bitMatchesTargetThreshold = (numBitsInRow * 7) / 8;
    }

    public int scoreComparedRows(BitSet questionRow, BitSet answerRow) {
        // will score by checking at least 7/8 of bits are correct, if so they are awarded 1, for every additional correct bit, they are awarded 1
        // if 32 bit row, then this is 0.000965% just by chance
        // Can determine matches through XOR, then negation, then length of list

        BitSet workingBitSet = questionRow.get(0, numBitsInRow);
        workingBitSet.xor(answerRow.get(0, numBitsInRow));
        System.out.println(workingBitSet.size());
        workingBitSet.flip(0, numBitsInRow);
        int numBitsOverThreshold = workingBitSet.cardinality() - (bitMatchesTargetThreshold-1);

        return Math.max(0, numBitsOverThreshold);
    }

    @Override
    public float getFitness(ChallengeQuestion challengeQuestion, ChallengeAnswer challengeAnswer) {
        float score = 0;
        if (challengeQuestion.isExtractedAtleastOnce()) {
            score++;
            if (challengeQuestion.isSentAfterExtraction()) {
                score++;

                for (long i = 0; i < numRows; i++) {
                    score += scoreComparedRows(challengeQuestion.getRow(i), challengeAnswer.getRow(i));
                }
            }
        }
        return score;
    }
}
