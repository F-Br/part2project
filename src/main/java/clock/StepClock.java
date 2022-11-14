package clock;

public final class StepClock {
    private static Long currentStepCount = 1L;

    public static void incrementStepCount() throws ArithmeticException {
        currentStepCount++;
        if (currentStepCount == 0L) {
            throw new ArithmeticException("currentStepCount is of value " + currentStepCount + ". Potentially an overflow");
        }
    }

    public static Long getCurrentStepCount() {
        return currentStepCount;
    }
}
