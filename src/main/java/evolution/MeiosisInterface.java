package evolution;

import sideinfrastructure.genome.Chromosome;
import sideinfrastructure.genome.Genome;

import java.util.List;

public interface MeiosisInterface {
    Genome performAllMutations(Genome genome);

    ChromosomePair performAllCrossovers(ChromosomePair chromosomePair);

    List<Chromosome> performMeiosis(Genome genome);

    Genome fertilisation(List<Chromosome> haploidGenome1, List<Chromosome> haploidGenome2);

}
