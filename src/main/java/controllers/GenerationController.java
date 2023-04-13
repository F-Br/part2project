package controllers;

import evolution.EvolutionaryGenome;
import evolution.MeiosisInterface;
import evolution.MeiosisOperators;
import org.apache.commons.math3.util.Pair;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class GenerationController {

    private List<EvolutionaryGenome> currentGeneration;
    private MeiosisOperators meiosisOperators;
    private SimulationController simulationController;
    private int numElite;
    private List<Pair<Float, EvolutionaryGenome>> fitnessPairGeneration;


    public GenerationController(List<EvolutionaryGenome> startingGeneration, MeiosisOperators meiosisOperators, SimulationController simulationController, int numElite) {
        this.currentGeneration = startingGeneration;
        this.meiosisOperators = meiosisOperators;
        this.simulationController = simulationController;
        this.numElite = numElite;
    }

    public void simulateGeneration() {
        int generationSize = currentGeneration.size();
        fitnessPairGeneration = new ArrayList<>(generationSize);
        for (int i = 0; i < generationSize; i++) {
            System.out.println("Genome: " + i);
            EvolutionaryGenome currentGenome = currentGeneration.get(i);
            Pair<Float, EvolutionaryGenome> fitnessGenomePair = simulateGenome(currentGenome);
            fitnessPairGeneration.add(i, fitnessGenomePair);
        }
    }

    public void createNextGeneration() {
        currentGeneration = meiosisOperators.fullSelectionOfNextGeneration(fitnessPairGeneration, numElite);
    }

    private Pair<Float, EvolutionaryGenome> simulateGenome(EvolutionaryGenome evolutionaryGenome) {
        Pair<Float, Long> fitnessAndTimePair = simulationController.simulateGenome(evolutionaryGenome);
        float fitnessScore = fitnessAndTimePair.getFirst();

        // TODO: add timer metrics here maybe

        return new Pair<>(fitnessScore, evolutionaryGenome);
    }

}
