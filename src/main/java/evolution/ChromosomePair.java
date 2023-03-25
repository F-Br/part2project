package evolution;

import sideinfrastructure.genome.Chromosome;

public class ChromosomePair {

    private Chromosome chromosome1;
    private Chromosome chromosome2;

    ChromosomePair(Chromosome chromosome1, Chromosome chromosome2) {
        this.chromosome1 = chromosome1;
        this.chromosome2 = chromosome2;
    }

    public Chromosome getChromosome1() {
        return chromosome1;
    }

    public Chromosome getChromosome2() {
        return chromosome2;
    }
}
