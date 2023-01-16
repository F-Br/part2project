package channel;

import sideinfrastructure.SideIdentifier;

import java.util.Queue;

public class Receiver extends Endpoint {
    public Receiver(Pipeline outputPipeline) {
        super(outputPipeline);

        super.side = SideIdentifier.RECEIVER;
    }
}
