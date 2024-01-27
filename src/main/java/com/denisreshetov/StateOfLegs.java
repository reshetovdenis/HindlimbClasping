package com.denisreshetov;

public class StateOfLegs {
    public Boolean leftOK = null;
    public Boolean rightOK = null;

    public static String text(StateOfLegs state){
        if(state == null){
            return "NA";
        } else {
            return state.toString();
        }
    }
    @Override
    public String toString(){
        return stateToSymbol(leftOK) + "|" + stateToSymbol(rightOK);
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
