package testsoperationblocks;

import channel.operationblocks.AddConstantPacketDelayOperationBlock;
import channel.operationblocks.OperationBlock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import packet.BasicDataPacket;
import packet.PacketReleaseStepPair;

import java.util.ArrayDeque;

import static org.junit.jupiter.api.Assertions.*;

class AddConstantPacketDelayOperationBlockTest {

    static PacketReleaseStepPair testPacket1;
    static PacketReleaseStepPair testPacket2;
    static PacketReleaseStepPair testPacket3;
    static PacketReleaseStepPair testPacket4;

    @BeforeEach
    void setTestPackets() {
        BasicDataPacket dummyPacket = new BasicDataPacket("data", new ArrayDeque());

        testPacket1 = new PacketReleaseStepPair(dummyPacket, 0L);
        testPacket2 = new PacketReleaseStepPair(dummyPacket, 1L);
        testPacket3 = new PacketReleaseStepPair(dummyPacket, 10L);
        testPacket4 = new PacketReleaseStepPair(dummyPacket, 1000L);
    }

    @ParameterizedTest
    @ValueSource(longs = {0L, 1L, 10L, 1000L, 1000000000L})
    void CorrectResultsOnStandardDelays(Long argument) {
        OperationBlock operationBlock = new AddConstantPacketDelayOperationBlock(argument);
        assertEquals(testPacket1.releaseStep + argument, operationBlock.performOperation(testPacket1).releaseStep);
        assertEquals(testPacket2.releaseStep + argument, operationBlock.performOperation(testPacket2).releaseStep);
        assertEquals(testPacket3.releaseStep + argument, operationBlock.performOperation(testPacket3).releaseStep);
        assertEquals(testPacket4.releaseStep + argument, operationBlock.performOperation(testPacket4).releaseStep);
    }

    @Test
    void AvoidsSettingNegativeDelay() {
        assertThrows(IllegalArgumentException.class, () -> new AddConstantPacketDelayOperationBlock(-1L));
    }
}