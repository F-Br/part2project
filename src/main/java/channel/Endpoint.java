package channel;

import fraglet.Fraglet;
import packet.AbstractPacket;
import sideinfrastructure.FragletVat;
import sideinfrastructure.SideIdentifier;

import java.util.Queue;

public class Endpoint {
    private Pipeline outputPipeline;
    public FragletVat destinationFragletVat;
    protected SideIdentifier side;

    public Endpoint(Pipeline outputPipeline) {
        this.outputPipeline = outputPipeline;
    }

    public void setDestinationFragletVat(FragletVat destinationFragletVat) {
        this.destinationFragletVat = destinationFragletVat;
    }

    public void sendPacket(AbstractPacket outputPacket) { // TODO: will likely need to make a custom packet for fraglet
        outputPipeline.insertPacket(outputPacket); // TODO: need to check if this works, pipeline class seems fine :), but packet objects and sender + receiver classes seem a bit odd
    }

    public void sendFraglet(Fraglet fraglet) {
        outputPipeline.insertPacket(new AbstractPacket(fraglet, destinationFragletVat));
    }

    public SideIdentifier getSide() {
        return side;
    }

    // TODO: add receive packet


    public void stepUpdate() {
        outputPipeline.stepUpdate();
    }
}
