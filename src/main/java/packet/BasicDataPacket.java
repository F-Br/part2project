package packet;

import java.util.Queue;

public class BasicDataPacket extends AbstractPacket {
    private String data;

    public BasicDataPacket(String data, Queue destinationBufferQueue) {
        super(destinationBufferQueue);
        this.data = data;
    }

    public String getPacketData() {
        return data;
    }

    public void sendPacketToDestination() {
        super.sendPacketToDestination();
        System.out.println("packet delivered");
    }
}
