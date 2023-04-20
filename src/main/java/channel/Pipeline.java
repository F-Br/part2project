package channel;

import channel.operationblocks.OperationBlock;
import clock.StepClock;
import packet.AbstractPacket;
import packet.PacketReleaseStepPair;
import packet.PacketReleaseStepPairComparator;

import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

public class Pipeline {
    private List<OperationBlock> operationBlockList;
    private Comparator<PacketReleaseStepPair> packetPairComparator = new PacketReleaseStepPairComparator();
    private PriorityQueue<PacketReleaseStepPair> pipelineQueue = new PriorityQueue<PacketReleaseStepPair>(packetPairComparator);

    public Pipeline(List<OperationBlock> operations) {
        this.operationBlockList = operations;
    }

    private PacketReleaseStepPair performAllOperations(AbstractPacket packet) {
        PacketReleaseStepPair packetPair = new PacketReleaseStepPair(packet, null);
        for (OperationBlock operation : operationBlockList) {
            packetPair = operation.performOperation(packetPair);
        }
        return packetPair;
    }

    public void insertPacket(AbstractPacket newPacket) {
        PacketReleaseStepPair packetPair = performAllOperations(newPacket);
        if (packetPair.packet == null) {
            return;
        }
        if (packetPair.releaseStep == null) {
            throw new IllegalStateException("releaseStep has not been set. Ensure operationBlockList includes an " +
                    "operation to set releaseState to a value");
        }
        //System.out.println("packet added to queue");
        pipelineQueue.add(packetPair);
    }

    public void stepUpdate() {
        if (pipelineQueue.isEmpty()) {
            return;
        }
        while (pipelineQueue.peek().releaseStep <= StepClock.getCurrentStepCount()) {
            pipelineQueue.poll().packet.sendPacketToDestination();
            if (pipelineQueue.isEmpty()) {
                break;
            }
        }
    }
}
