package com.denisreshetov;

import java.util.Arrays;
import java.util.stream.Collectors;

public class Measurement {
    private double startX;
    private double startY;
    private double endX;
    private double endY;

    public Measurement(double startX, double startY, double endX, double endY) {
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
    }

    @Override
    public String toString(){
        return Arrays.stream(new double[]{startX, startY, endX, endY})
                .mapToObj(Double::toString)
                .collect(Collectors.joining(";"));
    }
    public static Measurement fromText(String text) {
        if(text != null){
            String[] values = text.split(";");
            if(values.length == 4){
                try {
                    double startX = Double.parseDouble(values[0]);
                    double startY = Double.parseDouble(values[1]);
                    double endX = Double.parseDouble(values[2]);
                    double endY = Double.parseDouble(values[3]);
                    Measurement measurement = new Measurement(startX, startY, endX, endY);
                    return measurement;
                } catch (Exception e){
                }
            }
        }
        return null;
    }

    public double getStartX() {
        return startX;
    }

    public void setStartX(double startX) {
        this.startX = startX;
    }

    public double getStartY() {
        return startY;
    }

    public void setStartY(double startY) {
        this.startY = startY;
    }

    public double getEndX() {
        return endX;
    }

    public void setEndX(double endX) {
        this.endX = endX;
    }

    public double getEndY() {
        return endY;
    }

    public void setEndY(double endY) {
        this.endY = endY;
    }
}
