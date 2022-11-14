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

    public void sendPacket(AbstractPacket outputPacket) {
        outputPipeline.insertPacket(outputPacket);
    }

    public Queue getInputBufferQueue() {
        return inputBufferQueue;
    }

    public void stepUpdate() {
        outputPipeline.stepUpdate();
    }
}
