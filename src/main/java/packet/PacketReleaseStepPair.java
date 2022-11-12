package packet;

public class PacketReleaseStepPair {
    public AbstractPacket packet;
    public Long releaseStep;

    public PacketReleaseStepPair(AbstractPacket initPacket, Long initReleaseStep) {
        this.packet = initPacket;
        this.releaseStep = initReleaseStep;
    }
}
