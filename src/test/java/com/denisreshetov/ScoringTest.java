package com.denisreshetov;

import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static org.testng.Assert.*;

public class ScoringTest {

    @Test
    public void testGetConsensusLegState() {
        Map<Integer, StateOfLegs> states = new HashMap<>();
        Boolean result = Scoring.getConsensusLegState(states, true);
        assertEquals(result, null);
        result = Scoring.getConsensusLegState(states, false);
        assertEquals(result, null);
        //
        for(int i = 1; i <= 3; i++){
            states.put(i, new StateOfLegs(false, false));
        }
        result = Scoring.getConsensusLegState(states, true);
        assertEquals(result, false);
        result = Scoring.getConsensusLegState(states, false);
        assertEquals(result, false);
        //
        for(int i = 4; i <= 6; i++){
            states.put(i, new StateOfLegs(true, false));
        }
        result = Scoring.getConsensusLegState(states, true);
        assertEquals(result, false);
        result = Scoring.getConsensusLegState(states, false);
        assertEquals(result, false);
        //
        for(int i = 7; i <= 7; i++){
            states.put(i, new StateOfLegs(true, false));
        }
        result = Scoring.getConsensusLegState(states, true);
        assertEquals(result, true);
        result = Scoring.getConsensusLegState(states, false);
        assertEquals(result, false);
        //
        for(int i = 8; i <= 14; i++){
            states.put(i, new StateOfLegs(true, true));
        }
        result = Scoring.getConsensusLegState(states, false);
        assertEquals(result, false);
        for(int i = 15; i <= 15; i++){
            states.put(i, new StateOfLegs(true, true));
        }
        result = Scoring.getConsensusLegState(states, false);
        assertEquals(result, true);
    }

    @Test
    public void testGetScore() {
        Map<Integer, StateOfLegs> states = new HashMap<>();
        Integer result = Scoring.getScore(states);
        assertEquals(result, null);
        //
        for(int i = 1; i <= 3; i++){
            states.put(i, new StateOfLegs(false, false));
        }
        result = Scoring.getScore(states);
        assertEquals(result, 2);
        //
        for(int i = 4; i <= 6; i++){
            states.put(i, new StateOfLegs(true, false));
        }
        result = Scoring.getScore(states);
        assertEquals(result, 2);
        //
        for(int i = 7; i <= 7; i++){
            states.put(i, new StateOfLegs(true, false));
        }
        result = Scoring.getScore(states);
        assertEquals(result, 1);
        //
        for(int i = 8; i <= 14; i++){
            states.put(i, new StateOfLegs(true, true));
        }
        result = Scoring.getScore(states);
        assertEquals(result, 1);
        for(int i = 15; i <= 15; i++){
            states.put(i, new StateOfLegs(true, true));
        }
        result = Scoring.getScore(states);
        assertEquals(result, 0);
    }
}