package channel.operationblocks;

import clock.StepClock;
import packet.PacketReleaseStepPair;

public class SetPacketDelayToZeroOperationBlock extends OperationBlock {

    private StepClock stepClock;

    public SetPacketDelayToZeroOperationBlock(StepClock stepClock) {
        this.stepClock = stepClock;
    }

    public PacketReleaseStepPair performOperation(PacketReleaseStepPair packetPair) {
        packetPair.releaseStep = stepClock.getCurrentStepCount();
        return packetPair;
    }
}