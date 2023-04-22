package evolution.fitnessfunctions;

import sideinfrastructure.ChallengeAnswer;
import sideinfrastructure.ChallengeQuestion;
import sideinfrastructure.genome.Genome;

import java.util.Random;

public class FitnessFunctionPenaliseLength extends DefaultFitnessFunction{

    private final int recommendedLimiterForPIDs = 100;
    private Random rnd = new Random();

    public FitnessFunctionPenaliseLength(int numBytesInRow, int numRows) {
        super(numBytesInRow, numRows);
    }

    @Override
    public float addNoiseToFitness(float currentFitness, float noisePercentage) {
        float noiseRange = currentFitness * noisePercentage;
        if (rnd.nextBoolean()) {
            currentFitness += (rnd.nextFloat() * noiseRange);
        }
        else {
            currentFitness -= (rnd.nextFloat() * noiseRange);
        }
        return currentFitness;
    }

    @Override
    public float getFitness(ChallengeQuestion challengeQuestion, ChallengeAnswer challengeAnswer, Genome genome) {
        float currentFitness = super.getFitness(challengeQuestion, challengeAnswer, genome);

        int numberOfPIDsInGenome = genome.getSortedPIDList().size();
        if (numberOfPIDsInGenome > recommendedLimiterForPIDs) {
            currentFitness -= numberOfPIDsInGenome - recommendedLimiterForPIDs;
        }

        return currentFitness;
    }
}
