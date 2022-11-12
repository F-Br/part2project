package clock;

public class StepClock {
    private Long currentStepCount = 1L;

    public void incrementStepCount() throws ArithmeticException {
        currentStepCount++;
        if (currentStepCount == 0L) {
            throw new ArithmeticException("currentStepCount is of value " + currentStepCount + ". Potentially an overflow");
        }
    }

    public Long getCurrentStepCount() {
        return currentStepCount;
    }
}
