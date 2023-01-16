package packet;

import fraglet.Fraglet;
import sideinfrastructure.FragletVat;


public class AbstractPacket {
    private FragletVat destinationVat;
    private Fraglet fraglet;

    public AbstractPacket(Fraglet fraglet, FragletVat destinationVat) {
        this.destinationVat = destinationVat;
        this.fraglet = fraglet;
    }

    public void sendPacketToDestination() {
        destinationVat.addFraglet(fraglet);
    }
}
