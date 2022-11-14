package channel;

import packet.AbstractPacket;

import java.util.Queue;

public class Sender extends Endpoint {
    public Sender(Queue inputBufferQueue, Pipeline outputPipeline) {
        super(inputBufferQueue, outputPipeline);
    }
}
