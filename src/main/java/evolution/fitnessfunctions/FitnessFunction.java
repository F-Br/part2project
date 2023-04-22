package evolution.fitnessfunctions;

import sideinfrastructure.ChallengeAnswer;
import sideinfrastructure.ChallengeQuestion;
import sideinfrastructure.genome.Genome;

public abstract class FitnessFunction {

    abstract public float getFitness(ChallengeQuestion challengeQuestion, ChallengeAnswer challengeAnswer, Genome genome);
    abstract public float addNoiseToFitness(float currentFitness, float maxNoiseValue);
}
