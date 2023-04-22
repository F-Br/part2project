package evolution.fitnessfunctions;

import sideinfrastructure.ChallengeAnswer;
import sideinfrastructure.ChallengeQuestion;
import sideinfrastructure.genome.Genome;

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

    private int scoreComparedRows(BitSet questionRow, BitSet answerRow) {
        // will score by checking at least 7/8 of bits are correct, if so they are awarded 1, for every additional correct bit, they are awarded 1
        // if 32 bit row, then this is 0.000965% just by chance
        // Can determine matches through XOR, then negation, then length of list

        BitSet workingBitSet = questionRow.get(0, numBitsInRow);
        workingBitSet.xor(answerRow.get(0, numBitsInRow));
        workingBitSet.flip(0, numBitsInRow); // number of 1s now correspond to the matches
        int numBitsOverThreshold = workingBitSet.cardinality() - (bitMatchesTargetThreshold-1);

        return Math.max(0, numBitsOverThreshold);
    }

    @Override
    public float getFitness(ChallengeQuestion challengeQuestion, ChallengeAnswer challengeAnswer, Genome senderGenome) {
        float score = 0;
        if (challengeQuestion.isExtractedAtleastOnce()) {
            score++;
            if (challengeQuestion.isSentAfterExtraction()) {
                score++;

                for (long i = 0; i < numRows; i++) {
                    score += scoreComparedRows(challengeQuestion.fetchRowForFitness(i), challengeAnswer.fetchRowForFitness(i));
                }
            }
        }
        return score;
    }

    @Override
    public float addNoiseToFitness(float currentFitness, float maxNoiseValue) {
        return currentFitness;
    }
}
