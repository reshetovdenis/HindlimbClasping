package com.denisreshetov;

import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class StateOfLegsTest {
    @Test
    public void testFromTextValidInput() {
        String[] types = {"3", "40" };
        for(String type: types){
            StateOfLegs state = StateOfLegs.fromText(type);
            assertEquals(state.toString(), type);
        }
    }
    @Test
    public void testFromTextNA() {
        StateOfLegs state = StateOfLegs.fromText("NA");
        assertNull(state);
    }
    @Test
    public void testFromTextInvalidInput() {
        StateOfLegs state = StateOfLegs.fromText("Invalid");
        assertNull(state);
    }

    @Test
    public void testToString() {
        StateOfLegs state = new StateOfLegs(30);
        assertEquals(state.toString(), "30");
    }

    @Test
    public void testTextMethodWithNull() {
        assertEquals(StateOfLegs.text(null), "NA");
    }

}