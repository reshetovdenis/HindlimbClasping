package com.denisreshetov;

import java.util.ArrayList;
import java.util.List;

public class StateOfLegs {

    private Measurement leftLeg;
    private Measurement rightLeg;
    private Measurement betweenLegs;
    private Measurement body;

    private Double stretchingPercent;
    public StateOfLegs() {
    }

    public Measurement getLeftLeg() {
        return leftLeg;
    }

    public void setLeftLeg(Measurement leftLeg) {
        this.leftLeg = leftLeg;
        updateStretchingPercent();
    }

    private void updateStretchingPercent() {

    }

    public Measurement getRightLeg() {
        return rightLeg;
    }

    public void setRightLeg(Measurement rightLeg) {
        this.rightLeg = rightLeg;
        updateStretchingPercent();
    }

    public Measurement getBetweenLegs() {
        return betweenLegs;
    }

    public void setBetweenLegs(Measurement betweenLegs) {
        this.betweenLegs = betweenLegs;
        updateStretchingPercent();
    }

    public Measurement getBody() {
        return body;
    }

    public void setBody(Measurement body) {
        this.body = body;
        updateStretchingPercent();
    }

    public Double getStretchingPercent() {
        return stretchingPercent;
    }

    public static String text(StateOfLegs state){
        if(state == null){
            return "NA";
        } else {
            return state.toString();
        }
    }
    public static StateOfLegs fromText(String text) {
        if(text != null){
            String[] measurementInfo = text.split("|");
            if(measurementInfo.length == 4){
                StateOfLegs state = new StateOfLegs();
                state.setLeftLeg(Measurement.fromText(measurementInfo[0]));
                state.setRightLeg(Measurement.fromText(measurementInfo[1]));
                state.setBetweenLegs(Measurement.fromText(measurementInfo[2]));
                state.setBody(Measurement.fromText(measurementInfo[3]));
                return state;
            }
        }
        return null;
    }
    @Override
    public String toString(){
        if(getStretchingPercent() != null){
            List<String> result = new ArrayList<>();
            Measurement[] measurements = {leftLeg, rightLeg, betweenLegs, body};//Don't change the order
            for(Measurement m : measurements){
                if(m != null){
                    result.add(m.toString());
                } else {
                    result.add("");
                }
            }
            return String.join("|", result);
        }
        return "NA";
    }
}
