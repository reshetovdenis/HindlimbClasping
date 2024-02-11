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

    /*
   (0) If the hindlimbs are consistently splayed outward, away
from the abdomen and never retract towards the abdomen,
the mouse is assigned a score of 0 (Fig. 1A).
*/
    @Test
    public void testGetScore0() {
        Map<Integer, StateOfLegs> states = new HashMap<>();
        for (int i = 1; i <= 3; i++) {
            states.put(i, new StateOfLegs(true, true));
        }
        Integer result = Scoring.getScore(states);
        assertEquals(result, 0);
        for (int i = 4; i <= 6; i++) {
            states.put(i, new StateOfLegs(true, false));
        }
        result = Scoring.getScore(states);
        assertEquals(result, 1);
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
        for (int i = 1; i <= 3; i++) {
            states.put(i, new StateOfLegs(true, true));
        }
        Integer result = Scoring.getScore(states);
        assertEquals(result, 0);
        for (int i = 4; i <= 6; i++) {
            states.put(i, new StateOfLegs(true, false));
        }
        result = Scoring.getScore(states);
        assertEquals(result, 1);
    }

    /*
 (2) If hindlimbs individually, but not both at the same time,
 retract towards the abdomen for a cumulative time of 5 s or
 greater (or greater than 50% of the time of the test), the
 mouse is assigned a score of 2 (Fig. 1B).
     */
    @Test
    public void testGetScore2() {
        Map<Integer, StateOfLegs> states = new HashMap<>();
        for (int i = 1; i <= 3; i++) {
            states.put(i, new StateOfLegs(false, true));
        }
        for (int i = 4; i <= 6; i++) {
            states.put(i, new StateOfLegs(true, false));
        }
        Integer result = Scoring.getScore(states);
        assertEquals(result, 2);
        //
        for (int i = 7; i <= 7; i++) {
            states.put(i, new StateOfLegs(true, true));
        }
        result = Scoring.getScore(states);
        assertEquals(result, 1);
    }

    /*
(3) If both hindlimbs are retracted at the same time toward and
touch the abdomen for any amount of time, the mouse is
assigned a score of 3 (Fig. 1C).
     */
    @Test
    public void testGetScore3() {
        Map<Integer, StateOfLegs> states = new HashMap<>();
        Integer result = Scoring.getScore(states);
        assertEquals(result, null);
        //
        for (int i = 1; i <= 3; i++) {
            states.put(i, new StateOfLegs(false, false));
        }
        result = Scoring.getScore(states);
        assertEquals(result, 2);
        //
        StateOfLegs state3 = new StateOfLegs();
        state3.setScore3(true);
        states.put(4, state3);
        result = Scoring.getScore(states);
        assertEquals(result, 3);
    }
}