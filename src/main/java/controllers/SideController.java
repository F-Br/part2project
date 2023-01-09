package controllers;

import fraglet.FragletParser;
import sideinfrastructure.ChallengeAnswer;
import sideinfrastructure.ChallengeQuestion;
import sideinfrastructure.FragletVat;
import sideinfrastructure.SideIdentifier;
import sideinfrastructure.genome.Genome;

public class SideController {
    // will handle genome, vat, parser, side identifier
    // will initialise it all as empty

    private final int NUMBER_GENES_PROMOTED_PER_STEP = 1;
    private final int NUMBER_FRAGLETS_PARSED_PER_STEP = 2;

    private SideIdentifier side;
    private FragletVat fragletVat;
    private FragletParser fragletParser;
    private Genome genome;

    private ChallengeAnswer challengeAnswer;
    private ChallengeQuestion challengeQuestion;

    // constructor
    public SideController(SideIdentifier side, Genome genome) {
        if (genome.getSide() != side) {
            throw new IllegalArgumentException("genome must have same side as side identifier supplied. Genome side: " + genome.getSide().name() + ", side identifier given: " + side.name());
        }

        this.genome = genome;
        this.side = side;
        this.fragletVat = new FragletVat(side);

        switch (side) {
            case SENDER:
                challengeQuestion.createNewChallengeQuestion();
                fragletParser = new FragletParser(challengeQuestion, fragletVat, genome);
                break;
            case RECEIVER:
                challengeAnswer.resetChallengeAnswer();
                fragletParser = new FragletParser(challengeAnswer, fragletVat, genome);
                break;
            default:
                throw new IllegalArgumentException("side enum not recognised as SENDER or RECEIVER, was given as: " + side.name());
        }
    }

    public boolean simulateSideCheck() { // returns true if submitted

        // order is going to be:
        // - go through old genome queue
        // - if not enough promoted, then do random promotion
        // - match fraglets resolve
        // - delayed fraglets resolve
        // - random fraglet selection if still not enough
        // - return if submitted

        // go through old genome queue and activate where possible
        int maxNumberPromotersLeftToActivate = NUMBER_GENES_PROMOTED_PER_STEP;
        maxNumberPromotersLeftToActivate -= genome.removeOldGeneExpressionDetails();

        // random promotions for those left
        Integer selectedPID;
        while (maxNumberPromotersLeftToActivate > 0) {
            selectedPID = genome.weightedRandomSelectionOfPID();
            if (selectedPID == null) { // no PID's available as all repressed
                break;
            }
            genome.parseGenome((int) selectedPID);
            maxNumberPromotersLeftToActivate--;
        }

        // fraglet matches resolve
        int maxNumberOfFragletsToParse = NUMBER_FRAGLETS_PARSED_PER_STEP;
        maxNumberOfFragletsToParse -= fragletVat.resolveMatches();;

        // fraglet delay queue resolve
        if (maxNumberOfFragletsToParse > 0) {
            maxNumberOfFragletsToParse -= fragletVat.processFragletDelayQueue(); // TODO: does this remove the matched fraglet? check and decide and then clarify in notes
        }

        // random fraglet attempts for those left
        if (maxNumberOfFragletsToParse > 0) {
            fragletVat.shuffleFragletList();
            while (maxNumberOfFragletsToParse > 0) {
                fragletParser.parseFraglet(fragletVat.removeFirstFragletInFragletList());
                maxNumberOfFragletsToParse--;
            }
        }

        // return true if submit instruction has been called
        if (side == SideIdentifier.RECEIVER) {
            return fragletParser.hasSubmissionBeenMade(); // TODO: i know this isnt called as soon as submission is made, but surely that should be fine? Within 1 step should be alright
        }
        return false;

    }


}
