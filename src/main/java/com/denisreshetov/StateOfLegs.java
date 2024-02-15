package com.denisreshetov;

public class StateOfLegs {
    private Integer angle;
    public StateOfLegs(int angle) {
        this.angle = angle;
    }
    public StateOfLegs() {
    }
    public Integer getAngle() {
        return angle;
    }
    public void setAngle(Integer angle) {
        this.angle = angle;
    }
    public static String text(StateOfLegs state){
        if(state == null){
            return "NA";
        } else {
            return state.getAngle().toString();
        }
    }
    public static StateOfLegs fromText(String state) {
        try {
            Integer angleFromText = Integer.parseInt(state);
            return new StateOfLegs(angleFromText);
        } catch (Exception e){
            return null;
        }
    }
    @Override
    public String toString(){
        if(angle != null){
            return angle.toString();
        }
        return "NA";
    }
    private String stateToSymbol(Boolean state) {
        if (state == null) {
            return "-";
        } else if (state == true) {
            return "O";
        }
        return "R";
    }
}
