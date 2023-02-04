package com.example.dronevision.utils;

import java.util.ArrayList;

public class Points {
    private ArrayList<Double> xs;
    private ArrayList<Double> ys;
    private Line a1;
    private Line a2;
    public Points(ArrayList<FlatTelemetry> measurements) {
        a1 = new Line(measurements.get(0));
        a2 = new Line(measurements.get(1));
    }


    private Point getPoint(Line line1, Line line2) {
        Point point = new Point(1, 1);
        return point;
    }
}

class Line {
    public Point p1;
    public Point p2;
    Line(FlatTelemetry telemetry) {
        p1 = new Point(telemetry.getX(), telemetry.getY());
        GuideVector guideVector = telemetry.getGuideVector();
        double point_x = p1.getX() + guideVector.x * 1e7;
        double point_y = p1.getY() + guideVector.y * 1e7;
        p2 = new Point(point_x, point_y);
    }
}
