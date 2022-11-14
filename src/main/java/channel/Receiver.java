package channel;

import java.util.Queue;

public class Receiver extends Endpoint {
    public Receiver(Queue inputBufferQueue, Pipeline outputPipeline) {
        super(inputBufferQueue, outputPipeline);
    }
}
