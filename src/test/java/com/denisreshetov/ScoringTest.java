package com.denisreshetov;

import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static org.testng.Assert.*;

public class ScoringTest {

    @Test
    public void testGetConsensusLegState() {
        Map<Integer, StateOfLegs> states = new HashMap<>();
        Double result = Scoring.getRetractedFraction(states, true);
        assertEquals(result, null);
        result = Scoring.getRetractedFraction(states, false);
        assertEquals(result, null);
        //
        for (int i = 1; i <= 3; i++) {
            states.put(i, new StateOfLegs(false, false));
        }
        result = Scoring.getRetractedFraction(states, true);
        assertEquals(result, 1);
        result = Scoring.getRetractedFraction(states, false);
        assertEquals(result, 1);
        //
        for (int i = 4; i <= 6; i++) {
            states.put(i, new StateOfLegs(true, false));
        }
        result = Scoring.getRetractedFraction(states, true);
        assertEquals(result, 0.5);
        result = Scoring.getRetractedFraction(states, false);
        assertEquals(result, 1);
        //
        for (int i = 7; i <= 12; i++) {
            states.put(i, new StateOfLegs(true, true));
        }
        result = Scoring.getRetractedFraction(states, false);
        assertEquals(result, 0.5);
    }
    @Test
    public void testGetScoreNull() {
        Map<Integer, StateOfLegs> states = new HashMap<>();
        Integer result = Scoring.getScore(states);
        assertEquals(result, null);
    }

    @Test
    public void testMinFramesScored() {
        Map<Integer, StateOfLegs> states = new HashMap<>();
        for (int i = 0; i < (Scoring.MIN_FRAMES_SCORED - 1); i++) {
            states.put(i, new StateOfLegs(true, true));
        }
        states.put(Scoring.MIN_FRAMES_SCORED - 1, new StateOfLegs(true, true));
        assertEquals(Scoring.getScore(states), 0);
    }

    /*
   (0) If the hindlimbs are consistently splayed outward, away
from the abdomen and never retract towards the abdomen,
the mouse is assigned a score of 0 (Fig. 1A).
*/
    @Test
    public void testGetScore0() {
        Map<Integer, StateOfLegs> states = new HashMap<>();
        for (int i = 0; i < Scoring.MIN_FRAMES_SCORED; i++) {
            states.put(i, new StateOfLegs(true, true));
        }
        assertEquals(Scoring.getScore(states), 0);
        states.put(Scoring.MIN_FRAMES_SCORED, new StateOfLegs(true, false));
        assertEquals(Scoring.getScore(states), 1);
    }

    /*
    (1) If hindlimbs individually, but not both at the same time,
 retract towards the abdomen for a cumulative time between
 1 and 4 s (or less than 50% of the time of the test), the mouse
 is assigned a score of 1 (Fig. 1B).
     */
    @Test
    public void testGetScore1() {
        Map<Integer, StateOfLegs> states = new HashMap<>();
        for (int i = 0; i < Scoring.MIN_FRAMES_SCORED; i++) {
            states.put(i, new StateOfLegs(true, true));
        }
        states.put(Scoring.MIN_FRAMES_SCORED, new StateOfLegs(true, false));
        assertEquals(Scoring.getScore(states), 1);
        for (int i = Scoring.MIN_FRAMES_SCORED+1; i < 17; i++) {
            states.put(i, new StateOfLegs(true, false));
        }
        assertEquals(Scoring.getScore(states), 2);
    }

    /*
 (2) If hindlimbs individually, but not both at the same time,
 retract towards the abdomen for a cumulative time of 5 s or
 greater (or greater than 50% of the time of the test), the
 mouse is assigned a score of 2 (Fig. 1B).
     */
    @Test
    public void testGetScore2IfCumulativeRetractionIsHalfAndAboveTime() {
        Map<Integer, StateOfLegs> states = new HashMap<>();
        states.put(0, null);
        for (int i = 0; i < 4; i++) {
            states.put(i, new StateOfLegs(false, true));
        }
        for (int i = 4; i < Scoring.MIN_FRAMES_SCORED; i++) {
            states.put(i, new StateOfLegs(true, false));
        }
        assertEquals(Scoring.getScore(states), 2);
        states.put(Scoring.MIN_FRAMES_SCORED, new StateOfLegs(true, true));
        assertEquals(Scoring.getScore(states), 1);
    }

    @Test
    public void testGetScore2IfRightLegIsConstantlyRetracted() {
        Map<Integer, StateOfLegs> states = new HashMap<>();
        for (int i = 0; i <= 4; i++) {
            states.put(i, new StateOfLegs(true, true));
        }
        for (int i = 5; i <= 9; i++) {
            states.put(i, new StateOfLegs(true, false));
        }
        states.put(10, null);
        assertEquals(Scoring.getScore(states), 2);
    }

    @Test
    public void testGetScore2IfLeftLegIsConstantlyRetracted() {
        Map<Integer, StateOfLegs> states = new HashMap<>();
        for (int i = 0; i <= 4; i++) {
            states.put(i, new StateOfLegs(true, true));
        }
        for (int i = 5; i <= 9; i++) {
            states.put(i, new StateOfLegs(false, true));
        }
        states.put(10, null);
        assertEquals(Scoring.getScore(states), 2);
    }



    /*
(3) If both hindlimbs are retracted at the same time toward and
touch the abdomen for any amount of time, the mouse is
assigned a score of 3 (Fig. 1C).
     */
    @Test
    public void testGetScore3() {
        Map<Integer, StateOfLegs> states = new HashMap<>();
        assertEquals(Scoring.getScore(states), null);
        //
        for (int i = 0; i < Scoring.MIN_FRAMES_SCORED; i++) {
            states.put(i, new StateOfLegs(true, true));
        }
        assertEquals(Scoring.getScore(states), 0);
        //
        StateOfLegs state3 = new StateOfLegs();
        state3.setScore3(true);
        states.put(Scoring.MIN_FRAMES_SCORED, state3);
        assertEquals(Scoring.getScore(states), 3);
    }
    @Test
    public void testGetScore3IfOnly1FrameScored() {
        Map<Integer, StateOfLegs> states = new HashMap<>();
        StateOfLegs state3 = new StateOfLegs();
        state3.setScore3(true);
        states.put(0, state3);
        assertEquals(Scoring.getScore(states), 3);
    }
}