package channel;

import packet.AbstractPacket;

import java.util.Queue;

public class Endpoint {
    private Queue<AbstractPacket> inputBufferQueue;
    private Pipeline outputPipeline;

    public Endpoint(Queue inputBufferQueue, Pipeline outputPipeline) {
        this.inputBufferQueue = inputBufferQueue;
        this.outputPipeline = outputPipeline;
    }

    public void sendPacket(AbstractPacket outputPacket) { // TODO: will likely need to make a custom packet for fraglet
        outputPipeline.insertPacket(outputPacket); // TODO: need to check if this works, pipeline class seems fine :), but packet objects and sender + receiver classes seem a bit odd
    }

    // TODO: add receive packet

    public Queue getInputBufferQueue() {
        return inputBufferQueue;
    }

    public void stepUpdate() {
        outputPipeline.stepUpdate();
    }
}
