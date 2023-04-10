package evolution;

import org.apache.commons.math3.util.Pair;
import sideinfrastructure.genome.Codon;

import java.util.List;

public class EvolutionaryGenome {
    private List<Pair<List<Codon>, List<Codon>>> innerGenome;
    private final int length;
    public EvolutionaryGenome(List<Pair<List <Codon>, List<Codon>>> tempGenome) {
        this.innerGenome = tempGenome;
        this.length = tempGenome.size();
    }

    public Pair<List<Codon>, List<Codon>> getChromosomePair(int index) {
        return innerGenome.get(index);
    }

    public int getLength() {
        return length;
    }
}
