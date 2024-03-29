package evolution;

import org.apache.commons.math3.util.Pair;
import sideinfrastructure.genome.Chromosome;
import sideinfrastructure.genome.Codon;
import sideinfrastructure.genome.Genome;

import java.util.List;

public interface MeiosisInterface {
    List<Codon> performAllMutations(List<Codon> codonList, int chromosomeIndex);

    Pair<List<Codon>, List<Codon>> performAllCrossovers(Pair<List<Codon>, List<Codon>> pairCodonLists, int chromosomeIndex);

    List<Codon> performMeiosisAndMutationForChromosomePair(Pair<List<Codon>, List<Codon>> pairCodonLists, int chromosomeNumber);

    EvolutionaryGenome fertilisation(EvolutionaryGenome diploidGenome1, EvolutionaryGenome diploidGenome2);

}
