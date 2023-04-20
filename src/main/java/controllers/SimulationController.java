package controllers;

import channel.Pipeline;
import channel.Receiver;
import channel.Sender;
import channel.operationblocks.OperationBlock;
import clock.StepClock;
import evolution.DataDefinitions;
import evolution.EvolutionaryGenome;
import evolution.fitnessfunctions.DefaultFitnessFunction;
import evolution.fitnessfunctions.FitnessFunction;
import org.apache.commons.math3.util.Pair;
import sideinfrastructure.ChallengeQuestion;
import sideinfrastructure.SideIdentifier;
import sideinfrastructure.genome.Chromosome;
import sideinfrastructure.genome.Codon;
import sideinfrastructure.genome.Genome;

import java.util.ArrayList;
import java.util.List;

public class SimulationController {

    private List<OperationBlock> operationBlockList;
    private DataDefinitions dataDefinitions;
    private FitnessFunction fitnessFunction;
    private int maxNumberOfSimulationSteps;

    public SimulationController(List<OperationBlock> operationBlockList, DataDefinitions dataDefinitions, int maxNumberOfSimulationSteps, FitnessFunction fitnessFunction) {
        this.operationBlockList = operationBlockList;
        this.dataDefinitions = dataDefinitions;
        this.maxNumberOfSimulationSteps = maxNumberOfSimulationSteps;
        this.fitnessFunction = fitnessFunction;
    }

    public SimulationController(List<OperationBlock> operationBlockList, DataDefinitions dataDefinitions, int maxNumberOfSimulationSteps) {
        ChallengeQuestion cQ = new ChallengeQuestion();
        FitnessFunction usedFitnessFunction = new DefaultFitnessFunction(cQ.numberOfBytesInRow, cQ.numberOfRows);
        this.operationBlockList = operationBlockList;
        this.dataDefinitions = dataDefinitions;
        this.maxNumberOfSimulationSteps = maxNumberOfSimulationSteps;
        this.fitnessFunction = usedFitnessFunction;
    }

    private Genome convertEvolutionaryGenomeToSimulationGenome(SideIdentifier sideIdentifier, EvolutionaryGenome evolutionaryGenome) {
        ArrayList<Chromosome> chromosomeList = convertEvolutionaryGenomeToChromosomeList(evolutionaryGenome);
        Genome simulationGenome = new Genome(sideIdentifier, chromosomeList);
        return simulationGenome;
    }

    private ArrayList<Chromosome> convertEvolutionaryGenomeToChromosomeList(EvolutionaryGenome evolutionaryGenome) {
        int numChromosomes = evolutionaryGenome.getLength();
        ArrayList<Chromosome> chromosomeList = new ArrayList<>(numChromosomes * 2);
        for (int i = 0; i < numChromosomes; i++) {
            int chromosomeStartingIndex = dataDefinitions.getCIDForChromosomeIndex(i);
            Pair<List<Codon>, List<Codon>> codonListChromosomePair = evolutionaryGenome.getChromosomePair(i);

            chromosomeList.add(2 * i, new Chromosome(chromosomeStartingIndex, new ArrayList<>(codonListChromosomePair.getFirst())));
            chromosomeList.add((2 * i) + 1, new Chromosome(chromosomeStartingIndex, new ArrayList<>(codonListChromosomePair.getSecond())));
        }
        return chromosomeList;
    }

    public Pair<Float, Long> simulateGenome(EvolutionaryGenome evolutionaryGenome) {
        // start timing how long it takes:
        long startTime = System.currentTimeMillis();

        //System.out.println("Start Simulation");

        // clock
        // TODO: do you need to reset this because its a static class?
        //StepClock sClock = new StepClock();
        StepClock.resetClock();

        // genomes
        Genome senderGenome = convertEvolutionaryGenomeToSimulationGenome(SideIdentifier.SENDER, evolutionaryGenome);
        Genome receiverGenome = convertEvolutionaryGenomeToSimulationGenome(SideIdentifier.RECEIVER, evolutionaryGenome);

        // pipeline, sender, and receiver
        Pipeline pipeline = new Pipeline(operationBlockList);
        Sender sender = new Sender(pipeline);
        sender.setDestinationFragletVat(receiverGenome.fragletVat);
        Receiver receiver = new Receiver(pipeline);
        receiver.setDestinationFragletVat(senderGenome.fragletVat);

        // channel controller
        ChannelStepController controller = new ChannelStepController(pipeline, sender, receiver);

        // side controllers
        SideController senderController = new SideController(SideIdentifier.SENDER, senderGenome, sender);
        SideController receiverController = new SideController(SideIdentifier.RECEIVER, receiverGenome, receiver);

        // simulation
        for (int i = 0; i < maxNumberOfSimulationSteps; i++) {
            controller.simulateChannelStep();
            senderController.simulateSideCheck();
            receiverController.simulateSideCheck();
        }

        //System.out.println("Finished Simulation");
        long endTime = System.currentTimeMillis();
        long executionTime = endTime - startTime;
        //System.out.println("Number of milliseconds to complete simulation: " + (endTime - startTime));

        // evaluate performance
        float fitnessScore = fitnessFunction.getFitness(senderController.getChallengeQuestion(), receiverController.getChallengeAnswer());

        return new Pair<>(fitnessScore, executionTime);
    }

}