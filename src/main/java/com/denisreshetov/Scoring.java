package com.denisreshetov;

import java.util.Map;

public class Scoring {
    public static Boolean getConsensusLegState(Map<Integer, StateOfLegs> states, boolean isLeft){
        int countTrue = 0;
        int countFalse = 0;
        int countNA = 0;
        for(StateOfLegs state: states.values()){
            Boolean legState = state.rightOK;
            if(isLeft){
                legState = state.leftOK;
            }
            if(legState != null){
                if(legState){
                    countTrue++;
                } else {
                    countFalse++;
                }
            } else {
                countNA++;
            }
        }
        if(countNA >= countTrue && countNA >= countFalse){
            return null;
        }
        if(countTrue > countFalse){
            return true;
        }
        return false;
    }
    public static Integer getScore(Map<Integer, StateOfLegs> states){
        Boolean leftConsensus = getConsensusLegState(states, true);
        Boolean rightConsensus = getConsensusLegState(states, false);
        if(leftConsensus == null || rightConsensus == null){
            return null;
        }
        if(leftConsensus && rightConsensus){
            return 0;
        } else if (!leftConsensus && !rightConsensus) {
            return 2;
        } else {
            return 1;
        }
    }
}
