package channel.operationblocks;

import packet.PacketReleaseStepPair;

public class AddConstantPacketDelayOperationBlock extends OperationBlock {
    private long packetDelay;

    public AddConstantPacketDelayOperationBlock(Long packetDelay) throws IllegalArgumentException {
        if (packetDelay < 0) {
            throw new IllegalArgumentException("must provide a natural number for delay");
        }

        this.packetDelay = packetDelay;
    }
    public PacketReleaseStepPair performOperation(PacketReleaseStepPair packetPair) {
        // TODO: decide whether to add null check to this? Technically just need one check so constantly checking seems like a waste
        packetPair.releaseStep += packetDelay;
        return packetPair;
    }
}
