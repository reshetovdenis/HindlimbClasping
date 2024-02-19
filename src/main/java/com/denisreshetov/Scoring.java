package com.denisreshetov;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class Scoring {
    protected static int MIN_FRAMES_SCORED = 7;
    public static Double getMedianStretching(Map<Integer, StateOfLegs> states){
        List<Double> stretches = new ArrayList<>();
        if(states != null){
            for (StateOfLegs state : states.values()) {

                if (state != null) {
                    Double stretching = state.getStretchingPercent();
                    if(stretching != null){
                        stretches.add(state.getStretchingPercent());
                    }
                }
            }
        }
        if(stretches.size() < MIN_FRAMES_SCORED){
            return null;
        }
        Collections.sort(stretches);
        // Calculate median
        int size = stretches.size();
        if (size % 2 == 1) {
            // Odd number of elements
            return (double)stretches.get(size / 2);
        } else {
            // Even number of elements, average the two middle elements
            return (double)(stretches.get(size / 2 - 1) + stretches.get(size / 2)) / 2.0;
        }
    }
}
