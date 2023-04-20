package controllers;

import evolution.EvolutionaryGenome;
import evolution.MeiosisInterface;
import evolution.MeiosisOperators;
import org.apache.commons.math3.util.Pair;
import org.javatuples.Triplet;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class GenerationController {

    private List<EvolutionaryGenome> currentGeneration;
    private MeiosisOperators meiosisOperators;
    private SimulationController simulationController;
    private int numElite;
    private List<Triplet<Float, EvolutionaryGenome, Long>> fitnessTripletGeneration;


    public GenerationController(List<EvolutionaryGenome> startingGeneration, MeiosisOperators meiosisOperators, SimulationController simulationController, int numElite) {
        this.currentGeneration = startingGeneration;
        this.meiosisOperators = meiosisOperators;
        this.simulationController = simulationController;
        this.numElite = numElite;
    }

    public void simulateGeneration() {
        int generationSize = currentGeneration.size();
        fitnessTripletGeneration = new ArrayList<>(generationSize);
        for (int i = 0; i < generationSize; i++) {
            EvolutionaryGenome currentGenome = currentGeneration.get(i);
            Triplet<Float, EvolutionaryGenome, Long> fitnessGenomeTimeTriplet = simulateGenome(currentGenome);
            fitnessTripletGeneration.add(i, fitnessGenomeTimeTriplet);
        }
    }

    public void createNextGeneration() {
        currentGeneration = meiosisOperators.fullSelectionOfNextGeneration(fitnessTripletGeneration, numElite);
    }

    private Triplet<Float, EvolutionaryGenome, Long> simulateGenome(EvolutionaryGenome evolutionaryGenome) {
        Pair<Float, Long> fitnessAndTimePair = simulationController.simulateGenome(evolutionaryGenome);
        float fitnessScore = fitnessAndTimePair.getFirst();

        // TODO: add timer metrics here maybe

        return new Triplet<>(fitnessScore, evolutionaryGenome, fitnessAndTimePair.getSecond());
    }

}
