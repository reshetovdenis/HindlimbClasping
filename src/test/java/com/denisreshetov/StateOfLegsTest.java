package com.denisreshetov;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class StateOfLegsTest {
    private StateOfLegs stateOfLegs;

    @BeforeMethod
    public void setUp() {
        stateOfLegs = new StateOfLegs();
    }

    @Test
    public void testFromTextValid() {
        String input = "0.0;0.0;1.0;1.0|1.0;1.0;2.0;2.0|0.5;0.5;1.5;1.5|2.0;2.0;3.0;3.0";
        StateOfLegs result = StateOfLegs.fromText(input);
        Assert.assertNotNull(result, "fromText should not return null for valid input.");
        // Assertions to check if the measurements were set correctly would go here
    }

    @Test
    public void testFromTextInvalid() {
        String input = "Invalid input";
        StateOfLegs result = StateOfLegs.fromText(input);
        Assert.assertNull(result, "fromText should return null for invalid input.");
    }

    @Test
    public void testUpdateStretchingPercent() {
        stateOfLegs.setBetweenLegs(new Measurement(0, 0, 1, 1));
        stateOfLegs.setLeftLeg(new Measurement(0, 0, 2, 2));
        stateOfLegs.setRightLeg(new Measurement(0, 0, 3, 3));
        stateOfLegs.setBody(new Measurement(0, 0, 4, 4));
        // This test assumes specific logic inside updateStretchingPercent
        // For example, checking if the stretching percent is correctly updated would be:
        Assert.assertNotNull(stateOfLegs.getStretchingPercent(), "Stretching percent should be updated.");
        // Exact value check would depend on the implementation details of updateStretchingPercent
    }

    @Test
    public void testToString() {
        stateOfLegs.setBetweenLegs(new Measurement(0, 0, 1, 1));
        String result = stateOfLegs.toString();
        // This test checks if the toString method returns the expected format
        // The exact expected string will depend on the Measurement toString implementation
        Assert.assertNotEquals(result, "NA", "toString should not return 'NA' when measurements are set.");
    }
}