package fraglet;

public class FragletReleasePair {
    private Fraglet fraglet;
    private long releaseStep;
    public FragletReleasePair(Fraglet fraglet, Long releaseStep) {
        this.fraglet = fraglet;
        this.releaseStep = releaseStep;
    }

    public long getReleaseStep() {
        return releaseStep;
    }

    public Fraglet getFraglet() {
        return fraglet;
    }

}
