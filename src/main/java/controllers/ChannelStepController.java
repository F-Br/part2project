package controllers;

import channel.Pipeline;
import channel.Receiver;
import channel.Sender;
import clock.StepClock;

public class ChannelStepController {
    private Pipeline pipeline;
    private Sender sender;
    private Receiver receiver;

    public ChannelStepController(Pipeline pipeline, Sender sender, Receiver receiver) {
        this.pipeline = pipeline;
        this.sender = sender;
        this.receiver = receiver;
    }

    public void simulateChannelStep() {
        sender.stepUpdate();
        receiver.stepUpdate();
        StepClock.incrementStepCount();
    }
}
