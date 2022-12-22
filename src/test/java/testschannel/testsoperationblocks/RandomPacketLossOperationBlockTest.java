package testschannel.testsoperationblocks;

import channel.operationblocks.OperationBlock;
import channel.operationblocks.RandomPacketLossOperationBlock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import packet.BasicDataPacket;
import packet.PacketReleaseStepPair;

import java.util.ArrayDeque;

import static org.junit.jupiter.api.Assertions.*;

class RandomPacketLossOperationBlockTest {

    static PacketReleaseStepPair testPacket;

    @BeforeEach
    void setTestPacket() {
        BasicDataPacket dummyPacket = new BasicDataPacket("data", new ArrayDeque());
        testPacket = new PacketReleaseStepPair(dummyPacket, 5L);
    }

    @Test
    void LeavesPacketDueToRemoveProbabilityZero() {
        OperationBlock operationBlock = new RandomPacketLossOperationBlock(0);
        for (int i = 0; i < 30; i++) {
            testPacket = operationBlock.performOperation(testPacket);
        }
        assertNotNull(testPacket.packet);
    }

    @Test
    void RemovesPacketWithProbabilityOne() {
        OperationBlock opertaionBlock = new RandomPacketLossOperationBlock(1);
        testPacket = opertaionBlock.performOperation(testPacket);
        assertNull(testPacket.packet);
    }

    @Test
    void AvoidsInvalidProbabilities() {
        assertThrows(IllegalArgumentException.class, () -> new RandomPacketLossOperationBlock(2));
        assertThrows(IllegalArgumentException.class, () -> new RandomPacketLossOperationBlock(-0.01F));
    }

}