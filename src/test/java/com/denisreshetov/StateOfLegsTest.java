package com.denisreshetov;

import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class StateOfLegsTest {
    @Test
    public void testConstructor() {
        StateOfLegs state = new StateOfLegs(true, false);
        assertEquals(state.getLeftOK(), Boolean.TRUE);
        assertEquals(state.getRightOK(), Boolean.FALSE);
    }

    @Test
    public void testDefaultConstructor() {
        StateOfLegs state = new StateOfLegs();
        assertNull(state.getLeftOK());
        assertNull(state.getRightOK());
    }

    @Test
    public void testFromTextValidInput() {
        String[] types = {"3", "-|-", "O|-", "R|-", "-|O","-|R", "O|O", "O|R", "R|R", "R|O", "O|O" };
        for(String type: types){
            StateOfLegs state = StateOfLegs.fromText(type);
            assertEquals(state.toString(), type);
        }
    }

    @Test
    public void testFromTextInvalidInput() {
        StateOfLegs state = StateOfLegs.fromText("Invalid");
        assertNull(state);
    }

    @Test
    public void testToString() {
        StateOfLegs state = new StateOfLegs(true, false);
        assertEquals(state.toString(), "O|R");
    }

    @Test
    public void testTextMethodWithNull() {
        assertEquals(StateOfLegs.text(null), "NA");
    }

    @Test
    public void testTextMethodWithScore3() {
        StateOfLegs state = new StateOfLegs();
        state.setScore3(true);
        assertEquals(state.toString(), "3");
    }

    @Test
    public void testTextMethodWithNonNull() {
        StateOfLegs state = new StateOfLegs(true, false);
        assertEquals(StateOfLegs.text(state), "O|R");
    }
}