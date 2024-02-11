package com.denisreshetov;

import java.util.Map;

public class Scoring {
    public static Double getRetractedFraction(Map<Integer, StateOfLegs> states, boolean isLeft){
        int countTrue = 0;
        int countFalse = 0;
        int countNA = 0;
        for(StateOfLegs state: states.values()){
            if(state != null){
                Boolean legState = state.getRightOK();
                if(isLeft){
                    legState = state.getLeftOK();
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
            } else {
                countNA++;
            }
        }
        if(countNA >= countTrue && countNA >= countFalse){
            return null;
        }
        return (double)countFalse/(double)(countTrue + countFalse);
    }
    public static boolean isScore3(Map<Integer, StateOfLegs> states){
        for(StateOfLegs state: states.values()){
            if(state != null){
                if(state.getScore3() != null && state.getScore3()){
                    return true;
                }
            }
        }
        return false;
    }
    public static Integer getScore(Map<Integer, StateOfLegs> states){
        if(states == null){
            return null;
        }
        boolean isScore3 = isScore3(states);
        if(isScore3){
            return 3;
        }
        Double leftRetractedFraction = getRetractedFraction(states, true);
        Double rightRetractedFraction = getRetractedFraction(states, false);
        if(leftRetractedFraction == null || rightRetractedFraction == null){
            return null;
        }
        Double mean = (leftRetractedFraction + rightRetractedFraction)/2;
        if(mean == 0){
            return 0;
        } else if (mean < 0.5) {
            return 1;
        } else {
            return 2;
        }
    }
}
