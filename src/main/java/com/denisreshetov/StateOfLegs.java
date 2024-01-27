package com.denisreshetov;

public class StateOfLegs {
    public Boolean leftOK = null;
    public Boolean rightOK = null;

    public StateOfLegs(Boolean leftOK, Boolean rightOK) {
        this.leftOK = leftOK;
        this.rightOK = rightOK;
    }

    public StateOfLegs() {
    }

    public static String text(StateOfLegs state){
        if(state == null){
            return "NA";
        } else {
            return state.toString();
        }
    }

    public static StateOfLegs fromText(String state) {
        if(state.matches(".\\|.")){
            String[] leftRight = state.split("\\|");
            if(leftRight.length == 2){
                StateOfLegs stateOfLegs = new StateOfLegs();
                if(leftRight[0].equals("O")){
                    stateOfLegs.leftOK = true;
                } else if (leftRight[0].equals("R")) {
                    stateOfLegs.leftOK = false;
                }
                if(leftRight[1].equals("O")){
                    stateOfLegs.rightOK = true;
                } else if(leftRight[1].equals("R")){
                    stateOfLegs.rightOK = false;
                }
                return stateOfLegs;
            }
        }
        return null;
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
