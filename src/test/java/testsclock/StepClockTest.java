package testsclock;

import clock.StepClock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class StepClockTest {

    static StepClock clock = new StepClock();

    @BeforeEach
    void setClock() {
        clock.resetClock();
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 5, 10, 100})
    void CheckStepCountIncrementsCorrectly(int argument) {
        int startingValue = 1;

        for (int i = 0; i < argument; i++) {
            clock.incrementStepCount();
        }
        assertEquals(argument + startingValue, clock.getCurrentStepCount());
    }
}