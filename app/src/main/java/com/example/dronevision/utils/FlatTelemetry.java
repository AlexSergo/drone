package com.example.dronevision.utils;

public class FlatTelemetry {
    private final double x;
    private final double y;
    private final GuideVector guideVector;

    public FlatTelemetry(double x, double y, double asim) {
        this.x = x;
        this.y = y;
        guideVector = new GuideVector(asim);
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public GuideVector getGuideVector() {
        return guideVector;
    }
}
