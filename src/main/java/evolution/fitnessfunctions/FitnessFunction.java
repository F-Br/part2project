package evolution.fitnessfunctions;

import sideinfrastructure.ChallengeAnswer;
import sideinfrastructure.ChallengeQuestion;

public abstract class FitnessFunction {

    abstract public float getFitness(ChallengeQuestion challengeQuestion, ChallengeAnswer challengeAnswer);
}
