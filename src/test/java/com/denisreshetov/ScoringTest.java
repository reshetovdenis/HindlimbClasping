package com.denisreshetov;

import com.denisreshetov.Measurement;
import com.denisreshetov.Scoring;
import com.denisreshetov.StateOfLegs;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

public class ScoringTest {

    @Test
    public void testGetMedianStretchingWithNullStates() {
        Double median = Scoring.getMedianStretching(null);
        Assert.assertNull(median, "Median should be null when states map is null.");
    }

    @Test
    public void testGetMedianStretchingWithEmptyStates() {
        Map<Integer, StateOfLegs> states = new HashMap<>();
        Double median = Scoring.getMedianStretching(states);
        Assert.assertNull(median, "Median should be null when states map is empty.");
    }

    @Test
    public void testGetMedianStretchingWithInsufficientFrames() {
        Map<Integer, StateOfLegs> states = new HashMap<>();
        for (int i = 0; i < Scoring.MIN_FRAMES_SCORED - 1; i++) {
            states.put(i, createStateOfLegsWithMeasurements(1, 1, 1, 1));
        }
        Double median = Scoring.getMedianStretching(states);
        Assert.assertNull(median, "Median should be null when there are fewer states than the minimum required.");
    }

    @Test
    public void testGetMedianStretchingWithOddNumberOfFrames() {
        Map<Integer, StateOfLegs> states = createStatesMapWithMeasurements(Scoring.MIN_FRAMES_SCORED); // Ensure odd number of frames
        Double median = Scoring.getMedianStretching(states);
        Assert.assertNotNull(median, "Median should not be null for an odd number of valid states.");
    }

    @Test
    public void testGetMedianStretchingWithEvenNumberOfFrames() {
        Map<Integer, StateOfLegs> states = createStatesMapWithMeasurements(Scoring.MIN_FRAMES_SCORED + 1); // Ensure even number of frames
        Double median = Scoring.getMedianStretching(states);
        Assert.assertNotNull(median, "Median should not be null for an even number of valid states.");
    }

    private StateOfLegs createStateOfLegsWithMeasurements(double leftLeg, double rightLeg, double betweenLegs, double body) {
        StateOfLegs state = new StateOfLegs();
        state.setLeftLeg(new Measurement(0, 0, leftLeg, leftLeg));
        state.setRightLeg(new Measurement(0, 0, rightLeg, rightLeg));
        state.setBetweenLegs(new Measurement(0, 0, betweenLegs, betweenLegs));
        state.setBody(new Measurement(0, 0, body, body));
        return state;
    }

    private Map<Integer, StateOfLegs> createStatesMapWithMeasurements(int numberOfFrames) {
        Map<Integer, StateOfLegs> states = new HashMap<>();
        for (int i = 0; i < numberOfFrames; i++) {
            // Simulate different measurements to influence the stretching percent
            // Adjust the parameters as needed to reflect realistic measurement scenarios
            states.put(i, createStateOfLegsWithMeasurements(i + 1, i + 1.5, i + 2, i + 2.5));
        }
        return states;
    }
}