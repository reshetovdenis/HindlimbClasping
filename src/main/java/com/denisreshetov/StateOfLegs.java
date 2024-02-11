package com.denisreshetov;

public class StateOfLegs {
    private Boolean leftOK = null;
    private Boolean rightOK = null;

    private Boolean isScore3 = null;

    public StateOfLegs(Boolean leftOK, Boolean rightOK) {
        this.leftOK = leftOK;
        this.rightOK = rightOK;
    }

    public StateOfLegs() {
    }

    public Boolean getLeftOK() {
        return leftOK;
    }

    public void setLeftOK(Boolean leftOK) {
        this.leftOK = leftOK;
        setScore3(null);
    }

    public Boolean getRightOK() {
        return rightOK;
    }

    public void setRightOK(Boolean rightOK) {
        this.rightOK = rightOK;
        setScore3(null);
    }

    public Boolean getScore3() {
        return isScore3;
    }

    public void setScore3(Boolean score3) {
        isScore3 = score3;
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
        } else if (state.equals("3")) {
            StateOfLegs stateOfLegs = new StateOfLegs();
            stateOfLegs.setScore3(true);
            return stateOfLegs;
        }
        return null;
    }

    @Override
    public String toString(){
        if(isScore3 != null && isScore3 == true){
            return "3";
        }
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
