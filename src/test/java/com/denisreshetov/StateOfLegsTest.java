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
        Assert.assertEquals(result.getLeftLeg().toString(), "0.0;0.0;1.0;1.0", "Left leg was not correctly initialized.");
        Assert.assertEquals(result.getRightLeg().toString(), "1.0;1.0;2.0;2.0", "Right leg was not correctly initialized.");
        Assert.assertEquals(result.getBetweenLegs().toString(), "0.5;0.5;1.5;1.5", "Between legs was not correctly initialized.");
        Assert.assertEquals(result.getBody().toString(), "2.0;2.0;3.0;3.0", "Body was not correctly initialized.");
    }
    @Test
    public void testFromTextInvalid() {
        String input = "Invalid input";
        StateOfLegs result = StateOfLegs.fromText(input);
        Assert.assertNull(result, "fromText should return null for invalid input.");
    }
    @Test
    public void testUpdateStretchingPercent() {
        stateOfLegs.setBetweenLegs(new Measurement(0, 0, 100, 0));
        stateOfLegs.setLeftLeg(new Measurement(0, 0, 25, 0));
        Assert.assertEquals(stateOfLegs.getStretchingPercent(), 400, "Left leg is not correctly used in calculations");
        stateOfLegs.setRightLeg(new Measurement(0, 0, 50, 0));
        Assert.assertEquals(stateOfLegs.getStretchingPercent(), 200, "Right leg is not correctly used in calculations");
        stateOfLegs.setBody(new Measurement(0, 0, 150, 0));
        Assert.assertEquals(stateOfLegs.getStretchingPercent(), 100, "Body is not correctly used in calculations");
    }
    @Test
    public void testToString() {
        stateOfLegs.setLeftLeg(new Measurement(0, 0, 0, 0));
        Assert.assertEquals(stateOfLegs.toString(), "0.0;0.0;0.0;0.0|||", "Left leg");
        stateOfLegs.setRightLeg(new Measurement(1, 0, 0, 0));
        Assert.assertEquals(stateOfLegs.toString(), "0.0;0.0;0.0;0.0|1.0;0.0;0.0;0.0||", "Right leg");
        stateOfLegs.setBetweenLegs(new Measurement(0, 1, 0, 0));
        Assert.assertEquals(stateOfLegs.toString(), "0.0;0.0;0.0;0.0|1.0;0.0;0.0;0.0|0.0;1.0;0.0;0.0|", "Between legs");
        stateOfLegs.setBody(new Measurement(0, 0, 1, 0));
        Assert.assertEquals(stateOfLegs.toString(), "0.0;0.0;0.0;0.0|1.0;0.0;0.0;0.0|0.0;1.0;0.0;0.0|0.0;0.0;1.0;0.0", "Body");
    }
}