package channel.operationblocks;

import packet.PacketReleaseStepPair;

import java.util.Random;

public class RandomPacketLossOperationBlock extends OperationBlock {

    private float packetLossProbability;
    Random rand = new Random();

    public RandomPacketLossOperationBlock(float packetLossProbability) {
        this.packetLossProbability = packetLossProbability;
        if (packetLossProbability > 1 || packetLossProbability < 0) {
            throw new IllegalArgumentException("packetLossProbability must be float between 0 and 1 inclusive, " +
                    "but value given was: " + packetLossProbability);
        }
    }

    public PacketReleaseStepPair performOperation(PacketReleaseStepPair packetPair) {
        if (rand.nextFloat() < packetLossProbability) {
            packetPair.packet = null;
        }
        return packetPair;
    }
}
