package com.denisreshetov;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class Scoring {
    public static Double getMeanAngle(Map<Integer, StateOfLegs> states){
        int count = 0;
        int sum = 0;
        if(states != null){
            for(StateOfLegs state: states.values()){
                if(state != null && state.getAngle() != null){
                    count++;
                    sum += state.getAngle();
                }
            }
        }
        if(count < MIN_FRAMES_SCORED){
            return null;
        }
        return (double)sum/(double)(count);
    }
    public static Double getMedianAngle(Map<Integer, StateOfLegs> states){
        List<Integer> angles = new ArrayList<>();

        if(states != null){
            for (StateOfLegs state : states.values()) {
                if (state != null) {
                    angles.add(state.getAngle());
                }
            }
        }

        if(angles.size() < MIN_FRAMES_SCORED){
            return null;
        }

        Collections.sort(angles);

        // Calculate median
        int size = angles.size();
        if (size % 2 == 1) {
            // Odd number of elements
            return (double)angles.get(size / 2);
        } else {
            // Even number of elements, average the two middle elements
            return (double)(angles.get(size / 2 - 1) + angles.get(size / 2)) / 2.0;
        }
    }

    protected static int MIN_FRAMES_SCORED = 7;
}
