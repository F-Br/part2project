package testschannel;

import channel.Pipeline;
import channel.operationblocks.AddConstantPacketDelayOperationBlock;
import channel.operationblocks.OperationBlock;
import channel.operationblocks.RandomPacketLossOperationBlock;
import channel.operationblocks.SetPacketDelayToZeroOperationBlock;
import clock.StepClock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import packet.BasicDataPacket;
import packet.PacketReleaseStepPair;
import packet.PacketReleaseStepPairComparator;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class PipelineTest {

    static StepClock clock = new StepClock();
    static Queue<BasicDataPacket> destinationQueue;
    static BasicDataPacket testPacket;

    @BeforeEach
    void setTestPacket() {
        clock.resetClock();

        destinationQueue = new LinkedList<BasicDataPacket>();
        testPacket = new BasicDataPacket("data", destinationQueue);
    }


    @Test
    void AvoidsInvalidPipelineOperationBlocks() {
        OperationBlock operationBlock = new RandomPacketLossOperationBlock(0);
        List<OperationBlock> operationBlockList = new ArrayList<>();
        operationBlockList.add(operationBlock);

        Pipeline pipeline = new Pipeline(operationBlockList);
        assertThrows(IllegalStateException.class, () -> pipeline.insertPacket(testPacket));
    }

    @ParameterizedTest
    @ValueSource(longs = {0L, 1L, 5L, 10L, 50L})
    void CheckPipelineUpdatesAPacketToItsDestination(Long argument) {
        OperationBlock operationBlock1 = new SetPacketDelayToZeroOperationBlock(clock);
        OperationBlock operationBlock2 = new AddConstantPacketDelayOperationBlock(argument);
        List<OperationBlock> operationBlockList = new ArrayList<>();
        operationBlockList.add(operationBlock1);
        operationBlockList.add(operationBlock2);

        Pipeline pipeline = new Pipeline(operationBlockList);
        pipeline.insertPacket(testPacket);
        assertTrue(destinationQueue.isEmpty());

        for (int i = 0; i < argument - 1; i++) {
            clock.incrementStepCount();
            pipeline.stepUpdate();
            assertTrue(destinationQueue.isEmpty());
        }
        clock.incrementStepCount();
        pipeline.stepUpdate();
        assertNotNull(destinationQueue.poll());
    }

    @ParameterizedTest
    @ValueSource(longs = {0L, 1L, 5L, 10L, 50L})
    void CheckPipeSendsPacketsEvenIfLate(Long argument) {
        OperationBlock operationBlock1 = new SetPacketDelayToZeroOperationBlock(clock);
        OperationBlock operationBlock2 = new AddConstantPacketDelayOperationBlock(5L);
        List<OperationBlock> operationBlockList = new ArrayList<>();
        operationBlockList.add(operationBlock1);
        operationBlockList.add(operationBlock2);

        Pipeline pipeline = new Pipeline(operationBlockList);
        pipeline.insertPacket(testPacket);
        assertTrue(destinationQueue.isEmpty());

        for (int i = 0; i < 5L + argument - 1; i++) {
            clock.incrementStepCount();
            assertTrue(destinationQueue.isEmpty());
        }
        clock.incrementStepCount();
        pipeline.stepUpdate();
        assertNotNull(destinationQueue.poll());
    }
}