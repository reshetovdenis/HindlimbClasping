package com.denisreshetov;

import java.util.Map;

public class Scoring {
    public static Double getRetractedFraction(Map<Integer, StateOfLegs> states, boolean isLeft){
        int countTrue = 0;
        int countFalse = 0;
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
                }
            }
        }
        if(countTrue + countFalse < MIN_FRAMES_SCORED){
            return null;
        }
        return (double)countFalse/(double)(countTrue + countFalse);
    }
    public static boolean isScore3(Map<Integer, StateOfLegs> states){
        if(states != null){
            for(StateOfLegs state: states.values()){
                if(state != null){
                    if(state.getScore3() != null && state.getScore3()){
                        return true;
                    }
                }
            }
        }
        return false;
    }

    protected static int MIN_FRAMES_SCORED = 8;
    public static Integer getScore(Map<Integer, StateOfLegs> states){
        boolean isScore3 = isScore3(states);
        if(isScore3){
            return 3;
        }
        if(states == null || states.size() < MIN_FRAMES_SCORED){
            return null;
        }

        Double leftRetractedFraction = getRetractedFraction(states, true);
        Double rightRetractedFraction = getRetractedFraction(states, false);
        if(leftRetractedFraction == null || rightRetractedFraction == null){
            return null;
        }

        if((leftRetractedFraction + rightRetractedFraction) == 0){
            return 0;
        } else if ( (leftRetractedFraction < 0.5) && (rightRetractedFraction < 0.5) ) {
            return 1;
        } else {
            return 2;
        }
    }
}
