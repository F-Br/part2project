package fraglet;

import channel.Endpoint;
import clock.StepClock;
import fraglet.instructions.DataInstruction;
import fraglet.instructions.Instruction;
import fraglet.instructions.InstructionTag;
import sideinfrastructure.ChallengeAnswer;
import sideinfrastructure.ChallengeQuestion;
import sideinfrastructure.FragletVat;
import sideinfrastructure.SideIdentifier;
import sideinfrastructure.genome.GeneExpressionDetails;
import sideinfrastructure.genome.GeneExpressionType;
import sideinfrastructure.genome.Genome;

import java.util.BitSet;
import java.util.LinkedList;

public class FragletParser {
    // inputs:
    // sending channel
    // reaction vat
    // matching vat
    // expression count and queue
    // output side boolean (maybe)
    // question buffer or answer buffer

    private Endpoint currentEndpoint;
    private ChallengeAnswer challengeAnswer;
    private ChallengeQuestion challengeQuestion;
    private SideIdentifier side;
    private FragletVat fragletVat;
    private Genome genome;
    private long PROMOTER_LIFE_TIME = 5L;
    private long REPRESSOR_LIFE_TIME = 5L;
    private boolean submissionMade = false;

    public boolean hasSubmissionBeenMade() {
        return submissionMade;
    }


    public FragletParser(ChallengeAnswer challengeAnswer, Genome genome, Endpoint currentEndpoint) {
        this.challengeAnswer = challengeAnswer;
        this.side = SideIdentifier.RECEIVER;
        this.fragletVat = genome.fragletVat;
        this.genome = genome;
        this.currentEndpoint = currentEndpoint;
        if (currentEndpoint.destinationFragletVat == null) {
            throw new IllegalStateException("currentEndpoint should have its destination set before being used as arguments");
        }
    }

    public FragletParser(ChallengeQuestion challengeQuestion, Genome genome, Endpoint currentEndpoint) {
        this.challengeQuestion = challengeQuestion;
        this.side = SideIdentifier.SENDER;
        this.fragletVat = genome.fragletVat;
        this.genome = genome;
        this.currentEndpoint = currentEndpoint;
        if (currentEndpoint.destinationFragletVat == null) {
            throw new IllegalStateException("currentEndpoint should have its destination set before being used as arguments");
        }
    }

    public void parseFraglet(Fraglet fraglet) { // doesnt need to be boolean if makes no sense
        // pre parse checks if necessary
        if (fraglet == null) {
            return;
        }

        // fraglet parsing loop
        fraglet_parsing_loop:
        while (true) {

            switch_statement:
            switch (fraglet.peekHeadInstruction().getInstructionTag()) {
                case DATA: {
                    break fraglet_parsing_loop;
                }

                case CRC_T: {
                    if (fraglet.size() < 3) {
                        break fraglet_parsing_loop;
                    }

                    fraglet.pollHeadInstruction();
                    Instruction instr = fraglet.pollHeadInstruction();
                    DataInstruction crcResult = crcCalculation(fraglet);
                    fraglet.addFirst(crcResult);
                    fraglet.addFirst(instr);
                    break;
                }

                case CRC_T_CHECK: {
                    if (fraglet.size() < 4) {
                        break fraglet_parsing_loop;
                    }
                    if (!(fraglet.get(2) instanceof DataInstruction)) {
                        break fraglet_parsing_loop;
                    }

                    fraglet.pollHeadInstruction();
                    Instruction instr = fraglet.pollHeadInstruction();
                    Instruction crcResult = fraglet.pollHeadInstruction();
                    if (crcTCheckMatch(fraglet, (DataInstruction) crcResult)) {
                        fraglet.addFirst(instr);
                    } else {
                        fraglet.addFirst(instr);
                        fraglet.addFirst(new Instruction(InstructionTag.NUL));
                    }
                    break;
                }

                case ERROR: {
                    break fraglet_parsing_loop;
                }

                case EXTRACT: {
                    if (side == SideIdentifier.RECEIVER) {
                        break fraglet_parsing_loop;
                    }
                    if (fraglet.size() < 3) {
                        break fraglet_parsing_loop;
                    }
                    if (!(fraglet.get(2) instanceof DataInstruction)) {
                        break fraglet_parsing_loop;
                    }

                    fraglet.pollHeadInstruction();
                    DataInstruction integerIndex = (DataInstruction) fraglet.get(1);
                    fraglet.remove(1);
                    BitSet data = challengeQuestion.getRow(integerIndex.getLongData());
                    if (data == null) {
                        fraglet.addFirst(new Instruction(InstructionTag.ERROR));
                        break fraglet_parsing_loop;
                    } else {
                        DataInstruction message = new DataInstruction(data);
                        fraglet.addLast(message);
                        break;
                    }
                }

                case INSERT: {
                    if (side == SideIdentifier.SENDER) { // TODO: consecutive if statements vs multiple conditioned if statement?
                        break fraglet_parsing_loop;
                    }
                    if (fraglet.size() < 3) {
                        break fraglet_parsing_loop;
                    }
                    if (!(fraglet.get(1) instanceof DataInstruction)) {
                        break fraglet_parsing_loop;
                    }
                    if (!(fraglet.get(2) instanceof DataInstruction)) {
                        break fraglet_parsing_loop;
                    }

                    fraglet.pollHeadInstruction();
                    long longIndex = ((DataInstruction) fraglet.pollHeadInstruction()).getLongData();
                    BitSet data = ((DataInstruction) fraglet.pollHeadInstruction()).getData();

                    challengeAnswer.setRow(longIndex, data);
                    break;
                }

                case SUBMIT: {
                    submissionMade = true;
                    break fraglet_parsing_loop;
                }


                case DELAY: {
                    if (fraglet.size() < 3) {
                        break fraglet_parsing_loop;
                    }
                    if (!(fraglet.get(1) instanceof DataInstruction)) {
                        break fraglet_parsing_loop;
                    }

                    fraglet.pollHeadInstruction();
                    long delay = ((DataInstruction) fraglet.pollHeadInstruction()).getLongData();
                    fragletVat.addToDelayFragletQueue(fraglet, delay);
                    //break fraglet_parsing_loop;
                    return;
                }

                case IF_MATCH: { // TODO: still a lot to do for this one
                    if (fraglet.size() < 3) { // TODO: how are we going to deal with nops? add custom size to fraglet class maybe?
                        //Todo: If so, then we will need to change the instanceof checks as well as they could be pointing to the wrong directions (override get maybe?) What if nop is intentionally instruction tag?
                        break fraglet_parsing_loop;
                    }

                    fraglet.pollHeadInstruction();
                    Instruction matchInstruction = fraglet.pollHeadInstruction();
                    Fraglet matchedFraglet = fragletVat.processIfMatchRequest(matchInstruction);
                    if (matchedFraglet == null) {
                        fraglet.pollHeadInstruction();
                        break;
                    } else {
                        matchedFraglet.pollHeadInstruction(); // TODO: depends on if already done
                        fraglet.addAll(matchedFraglet);
                        break;
                    }
                }

                case REMOVE: {
                    if (fraglet.size() < 3) {
                        break fraglet_parsing_loop;
                    }

                    fraglet.pollHeadInstruction();
                    fraglet.remove(1);
                    break;
                }

                case PROMOTE: { // TODO: Check it is sorted if a nonsense PID is given, needs to have logic to just reject this/assign it to closest PID (probably this assign)
                    // TODO: again, need to check about nops and when they are ok and when they are not
                    if (fraglet.size() < 2) {
                        break fraglet_parsing_loop;
                    }
                    if (!(fraglet.get(1) instanceof DataInstruction)) {
                        break fraglet_parsing_loop;
                    }

                    fraglet.pollHeadInstruction();
                    long longPID = ((DataInstruction) fraglet.pollHeadInstruction()).getLongData();
                    int validPID = genome.findClosestPID((int) longPID);
                    Instruction param;

                    if (fraglet.size() > 0) {
                        param = fraglet.get(0);
                    }
                    else {
                        genome.addGeneExpressionDetail(new GeneExpressionDetails(PROMOTER_LIFE_TIME + StepClock.getCurrentStepCount(), GeneExpressionType.PROMOTER, validPID));
                        break;
                    }

                    if (!(param instanceof DataInstruction)) { // check if not data instruction
                        genome.addGeneExpressionDetail(new GeneExpressionDetails(PROMOTER_LIFE_TIME + StepClock.getCurrentStepCount(), GeneExpressionType.PROMOTER, validPID));
                        break;
                    } else {
                        genome.addGeneExpressionDetail(new GeneExpressionDetails(PROMOTER_LIFE_TIME + StepClock.getCurrentStepCount(), GeneExpressionType.PROMOTER, validPID, ((DataInstruction) param).getData()));
                        fraglet.pollHeadInstruction();
                        break;
                    }
                }


                case REPRESS: {
                    if (fraglet.size() < 2) {
                        break fraglet_parsing_loop;
                    }
                    if (!(fraglet.get(1) instanceof DataInstruction)) {
                        break fraglet_parsing_loop;
                    }

                    fraglet.pollHeadInstruction();
                    long longPID = ((DataInstruction) fraglet.pollHeadInstruction()).getLongData();
                    int validPID = genome.findClosestPID((int) longPID);
                    genome.addGeneExpressionDetail(new GeneExpressionDetails(REPRESSOR_LIFE_TIME + StepClock.getCurrentStepCount(), GeneExpressionType.REPRESSOR, validPID));
                    break;
                }

                case PERMANENT_REPRESS: {
                    if (fraglet.size() < 2) {
                        break fraglet_parsing_loop;
                    }
                    if (!(fraglet.get(1) instanceof DataInstruction)) {
                        break fraglet_parsing_loop;
                    }

                    fraglet.pollHeadInstruction();
                    long longPID = ((DataInstruction) fraglet.pollHeadInstruction()).getLongData();
                    int validPID = genome.findClosestPID((int) longPID);
                    genome.addRepressor(validPID);
                    break;
                }


                case MATCH: // both matches should have same behaviour (when found within an executing fraglet)
                case MATCH_P: {
                    // TODO: follow same as above
                    if (fraglet.size() < 2) { // starts with a match instruction and has no argument to match with
                        return;
                    }

                    break fraglet_parsing_loop;
                }


                case SEND: {
                    if (fraglet.size() < 2) { // needs to send something
                        break fraglet_parsing_loop;
                    }

                    fraglet.pollHeadInstruction();
                    currentEndpoint.sendFraglet(fraglet); // TODO: need to fix packet problems
                    if (side == SideIdentifier.SENDER) {
                        challengeQuestion.checkIfSentAfterExtraction();
                    }
                    return; // TODO: check this is correct, next line should be deleted i think
                    // break fraglet_parsing_loop; // TODO: may need to check that logic which follows after loop doesn't affect this fraglet
                }

                case SPLIT: {
                    if (fraglet.size() < 2) { // needs to have something to search through
                        break fraglet_parsing_loop;
                    }

                    fraglet.pollHeadInstruction();
                    LinkedList<Instruction> seq1 = new LinkedList<>();

                    star_search:
                    while (true) {
                        if (fraglet.peekHeadInstruction().getInstructionTag() == InstructionTag.STAR) {
                            fraglet.pollHeadInstruction();
                            if (fraglet.isEmpty()) { // star at end
                                fraglet.addAll(seq1);
                                break star_search;
                            }
                            if (seq1.isEmpty()) { // star at start
                                break star_search;
                            }
                            Fraglet seq2 = new Fraglet(fraglet); // TODO: when code compiles, should really experiment with deep copies to be certain (reading not enough)
                            fraglet = new Fraglet(seq1);
                            fragletVat.addFraglet(seq2); // star in between, seq 1 added to vat, seq2 continues execution
                            //TODO: ^ maybe want a deep copy of this?
                            break switch_statement; // TODO: want to break out of switch statement but how?
                        } else {
                            seq1.addLast(fraglet.pollHeadInstruction());
                            //fraglet.remove(0);
                        }

                        if (fraglet.isEmpty()) { // no star
                            fraglet = new Fraglet(seq1);
                            break star_search;
                        }
                    }
                    break;
                }


                case STAR: {
                    fraglet.pollHeadInstruction();
                }


                case SUM: { // TODO: had weird rules with nops which I'd need to figure out... this applies to many instructions which you need to figure out
                    if (fraglet.size() < 4) {
                        break fraglet_parsing_loop;
                    }
                    if (!(fraglet.get(2) instanceof DataInstruction)) {
                        break fraglet_parsing_loop;
                    }
                    if (!(fraglet.get(3) instanceof DataInstruction)) {
                        break fraglet_parsing_loop;
                    }

                    fraglet.pollHeadInstruction();
                    Instruction instr = fraglet.pollHeadInstruction();
                    long valueA = ((DataInstruction) fraglet.pollHeadInstruction()).getLongData();
                    long valueB = ((DataInstruction) fraglet.pollHeadInstruction()).getLongData();

                    DataInstruction sumResult = new DataInstruction(BitSet.valueOf(new long[]{valueA + valueB}));
                    fraglet.addFirst(sumResult);
                    fraglet.addFirst(instr);
                    break;
                }


                case NOP: {
                    // TODO: check that happy with this... a lot of uncertainty around nop
                    fraglet.pollHeadInstruction();
                    break;
                }


                case NUL: {
                    fraglet = new Fraglet(new LinkedList<>());
                    break;
                }


                case FORK: {
                    if (fraglet.size() < 3) {
                        break fraglet_parsing_loop;
                    }

                    // TODO: ideally would take a deep copy of tail and make that a new fraglet
                    fraglet.pollHeadInstruction();
                    Instruction instrA = fraglet.pollHeadInstruction();
                    Instruction instrB = fraglet.pollHeadInstruction();

                    Fraglet fraglet2 = new Fraglet(fraglet);
                    fraglet.addFirst(instrA); // TODO: CHECK THIS DOESNT MODIFY fraglet2 <<<<<<<<<<<<<<<<<<<
                    fraglet2.addFirst(instrB);
                    fragletVat.addFraglet(fraglet2);
                    break;
                }


                default:
                    throw new IllegalStateException("fraglet head instruction had an unrecognised tag of: " + fraglet.peekHeadInstruction().getInstructionTag().name());

            }



            if (fraglet.isEmpty()) { // fully used fraglet should not be added back to fragletVat
                return;
            }

        }
        // ending conditions check
        // If empty, destroy
        if (fraglet.isEmpty()) {
            return;
        }

        // TODO: nop check here if desired (but also maybe not neccessary as addFraglet() may also remove nops

        // add fraglet back to vat (will auto update counts)
        fragletVat.addFraglet(fraglet);

    }


    // TODO: need to do a match instruction
    public void parseFragletMatch(Fraglet matchFraglet, Fraglet matchedFraglet) { // doesnt need to be boolean if makes no sense
        // pre parse checks if necessary

        // fraglet parsing loop
        switch (matchFraglet.peekHeadInstruction().getInstructionTag()) {
            case MATCH: // assuming match holds
                performDefaultMatch(matchFraglet, matchedFraglet);
                return;


            case MATCH_P: // assuming match holds
                Fraglet fragletMatchPCopy = new Fraglet(matchFraglet); // TODO: definitely deep copy?
                fragletVat.addFraglet(fragletMatchPCopy);

                performDefaultMatch(matchFraglet, matchedFraglet);
                return;


            default:
                throw new IllegalArgumentException("match fraglet given as argument here should have head instruction with tag MATCH or MATCH_P, instead got " + matchFraglet.peekHeadInstruction().getInstructionTag().name());
        }
    }


    private void performDefaultMatch(Fraglet matchFraglet, Fraglet matchedFraglet) {
        matchFraglet.remove(0);
        matchFraglet.remove(0);
        matchedFraglet.remove(0);

        matchFraglet.addAll(matchedFraglet); // TODO: what happens to matchedFraglet, does it terminate immediately? when does it die?

        if (matchFraglet.isEmpty()) {
            return;
        }

        if ((matchFraglet.peekHeadInstruction().getInstructionTag().isMatchInstruction()) && (matchFraglet.size() < 2)) { // starts with a match instruction and has no argument to match with
            return;
        }

        parseFraglet(matchFraglet);
    }

    private DataInstruction crcCalculation(Fraglet fraglet) { // TODO: could change this to a sequence of recursive hashfunction calls into the instructions
        long runningSum = 0;
        for (Instruction instr : fraglet.getInstructionList()) {
            if (instr instanceof DataInstruction) {
                runningSum += ((DataInstruction) instr).getLongData();
            }
        }

        return new DataInstruction(BitSet.valueOf(new long[] {runningSum}));
    }

    private boolean crcTCheckMatch(Fraglet fraglet, DataInstruction crcOldResult) {
        DataInstruction crcNewResult = crcCalculation(fraglet);
        if (crcNewResult.getData() == crcOldResult.getData()) {
            return true;
        }
        else {
            return false;
        }
    }

}
