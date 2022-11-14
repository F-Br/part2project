package packet;

import java.util.Queue;

public abstract class AbstractPacket {
    final private Queue destinationBufferQueue;

    public AbstractPacket(Queue destinationBufferQueue) {
        this.destinationBufferQueue = destinationBufferQueue;
    }

    public void sendPacketToDestination() {
        destinationBufferQueue.add(this);
    }
}
