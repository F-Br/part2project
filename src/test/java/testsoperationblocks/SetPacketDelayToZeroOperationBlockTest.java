package testsoperationblocks;

import channel.operationblocks.OperationBlock;
import channel.operationblocks.SetPacketDelayToZeroOperationBlock;
import clock.StepClock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import packet.BasicDataPacket;
import packet.PacketReleaseStepPair;

import java.util.ArrayDeque;

import static org.junit.jupiter.api.Assertions.*;

class SetPacketDelayToZeroOperationBlockTest {

    static PacketReleaseStepPair testPacket;

    @BeforeEach
    void setTestPacket() {
        BasicDataPacket dummyPacket = new BasicDataPacket("data", new ArrayDeque());
        testPacket = new PacketReleaseStepPair(dummyPacket, 100L);
    }

    @Test
    void CheckPacketDelayAlwaysSameAsClock() {
        StepClock clock = new StepClock();
        OperationBlock operationBlock = new SetPacketDelayToZeroOperationBlock(clock);
        assertEquals(1L, clock.getCurrentStepCount());
        for (int i = 1; i <= 10; i++) {
            testPacket = operationBlock.performOperation(testPacket);
            assertEquals(clock.getCurrentStepCount(), testPacket.releaseStep);
            clock.incrementStepCount();
        }
    }


}