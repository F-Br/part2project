package clock;

public final class StepClock {
    private static final Long STARTING_STEP_COUNT = 1L;
    private static Long currentStepCount = STARTING_STEP_COUNT;

    public static void incrementStepCount() throws ArithmeticException {
        currentStepCount++;
        if (currentStepCount == 0L) {
            throw new ArithmeticException("currentStepCount is of value " + currentStepCount + ". Potentially an overflow");
        }
    }

    public static Long getCurrentStepCount() {
        return currentStepCount;
    }

    public static void resetClock() {
        currentStepCount = STARTING_STEP_COUNT;
    }
}
