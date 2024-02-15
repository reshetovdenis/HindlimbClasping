package com.denisreshetov;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

public class ScoringTest {

    @Test
    public void testGetMeanAngleWithEmptyInput() {
        Map<Integer, StateOfLegs> states = new HashMap<>();
        Assert.assertNull(Scoring.getMeanAngle(states), "Mean angle should be null for empty input.");
    }

    @Test
    public void testGetMeanAngleWithAllNullStates() {
        Map<Integer, StateOfLegs> states = new HashMap<>();
        states.put(1, null);
        states.put(2, null);
        Assert.assertNull(Scoring.getMeanAngle(states), "Mean angle should be null when all states are null.");
    }

    @Test
    public void testGetMeanAngleWithNotEnoughValidAngles() {
        Map<Integer, StateOfLegs> states = new HashMap<>();
        for (int i = 1; i <= Scoring.MIN_FRAMES_SCORED - 1; i++) {
            states.put(i, new StateOfLegs(i * 10));
        }
        Assert.assertNull(Scoring.getMeanAngle(states), "Mean angle should be null with not enough valid angles.");
    }

    @Test
    public void testGetMedianAngleWithEmptyInput() {
        Map<Integer, StateOfLegs> states = new HashMap<>();
        Assert.assertNull(Scoring.getMedianAngle(states), "Median angle should be null for empty input.");
    }

    @Test
    public void testGetMedianAngleWithOddNumberOfElements() {
        Map<Integer, StateOfLegs> states = new HashMap<>();
        for (int i = 1; i <= Scoring.MIN_FRAMES_SCORED; i++) {
            states.put(i, new StateOfLegs(i * 10));
        }
        Double result = Scoring.getMedianAngle(states);
        Assert.assertNotNull(result, "Median angle should not be null with an odd number of elements.");
        Assert.assertEquals(result, 40.0, "Median calculation is incorrect for odd number of elements.");
    }

    @Test
    public void testGetMedianAngleWithEvenNumberOfElements() {
        Map<Integer, StateOfLegs> states = new HashMap<>();
        for (int i = 1; i <= Scoring.MIN_FRAMES_SCORED + 1; i++) {
            states.put(i, new StateOfLegs(i * 10));
        }
        Double result = Scoring.getMedianAngle(states);
        Assert.assertNotNull(result, "Median angle should not be null with an even number of elements.");
        Assert.assertEquals(result, 45.0, "Median calculation is incorrect for even number of elements.");
    }
}