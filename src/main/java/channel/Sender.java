package channel;

import packet.AbstractPacket;
import sideinfrastructure.SideIdentifier;

import java.util.Queue;

public class Sender extends Endpoint {
    public Sender(Pipeline outputPipeline) {
        super(outputPipeline);
        super.side = SideIdentifier.SENDER;
    }
}
