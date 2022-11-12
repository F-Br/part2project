package channel.operationblocks;

import packet.PacketReleaseStepPair;

public abstract class OperationBlock {
    abstract public PacketReleaseStepPair performOperation(PacketReleaseStepPair packetPair);
}
