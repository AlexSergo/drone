package com.example.dronevision.utils;

class GuideVector {
    public double x;
    public double y;
    GuideVector(double asim) {
        x = Math.sin(Math.toRadians(asim));
        y = Math.cos(Math.toRadians(asim));
    }
}
