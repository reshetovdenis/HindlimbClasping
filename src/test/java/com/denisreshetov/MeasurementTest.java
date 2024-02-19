package com.denisreshetov;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class MeasurementTest {
    @Test(dataProvider = "provideDataForGetLength")
    public void testGetLength(double startX, double startY, double endX, double endY, double expectedLength) {
        Measurement measurement = new Measurement(startX, startY, endX, endY);
        double actualLength = measurement.getLength();
        Assert.assertEquals(actualLength, expectedLength, 0.001, "The getLength method did not calculate the correct length.");
    }

    @DataProvider
    public Object[][] provideDataForGetLength() {
        return new Object[][]{
                {0.0, 0.0, 3.0, 4.0, 5.0}, // A 3-4-5 right triangle
                {0.0, 0.0, 1.0, 1.0, Math.sqrt(2)}, // A 45-degree right triangle, should be sqrt(2)
                {-1.0, -1.0, 1.0, 1.0, Math.sqrt(8)} // Two points distance, should be sqrt(8) or 2*sqrt(2)
        };
    }

    @Test(dataProvider = "provideMeasurementData")
    public void testToString(double startX, double startY, double endX, double endY, String expected) {
        Measurement measurement = new Measurement(startX, startY, endX, endY);
        String actual = measurement.toString();
        Assert.assertEquals(actual, expected, "The toString method did not generate the expected string.");
    }

    @DataProvider
    public Object[][] provideMeasurementData() {
        return new Object[][]{
                {0.0, 0.0, 1.0, 1.0, "0.0;0.0;1.0;1.0"},
                {-1.0, -2.0, 2.0, 3.0, "-1.0;-2.0;2.0;3.0"},
                {5.5, 6.6, 7.7, 8.8, "5.5;6.6;7.7;8.8"}
        };
    }

    @Test(dataProvider = "provideTextForFromTextMethod")
    public void testFromText(String text, Measurement expected) {
        Measurement actual = Measurement.fromText(text);
        Assert.assertNotNull(actual, "The fromText method returned null.");
        Assert.assertEquals(actual.getStartX(), expected.getStartX(), "StartX does not match.");
        Assert.assertEquals(actual.getStartY(), expected.getStartY(), "StartY does not match.");
        Assert.assertEquals(actual.getEndX(), expected.getEndX(), "EndX does not match.");
        Assert.assertEquals(actual.getEndY(), expected.getEndY(), "EndY does not match.");
    }

    @DataProvider
    public Object[][] provideTextForFromTextMethod() {
        return new Object[][]{
                {"0.0;0.0;1.0;1.0", new Measurement(0.0, 0.0, 1.0, 1.0)},
                {"-1.0;-2.0;2.0;3.0", new Measurement(-1.0, -2.0, 2.0, 3.0)},
                {"5.5;6.6;7.7;8.8", new Measurement(5.5, 6.6, 7.7, 8.8)}
        };
    }

    @Test
    public void testFromTextWithInvalidData() {
        Assert.assertNull(Measurement.fromText(null), "fromText should return null for null input.");
        Assert.assertNull(Measurement.fromText(""), "fromText should return null for empty string.");
        Assert.assertNull(Measurement.fromText("invalid;data"), "fromText should return null for invalid data.");
    }
}

