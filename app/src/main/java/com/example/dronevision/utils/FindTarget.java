package com.example.dronevision.utils;

public class FindTarget {
    public double Drone_alt;
    public double Drone_lat;
    public double Drone_lon;
    public double Drone_course;
    public double Gimbal_pitch;
    private double Camera_target_distance;
    private final NGeoCalc geoCalc = new NGeoCalc();
    private final HeightFinder heightFinder = new HeightFinder();
    private final double[] target_x_plane = new double[1];
    private final double[] target_y_plane = new double[1];
    private final double[] target_h_plane = new double[1];
    public double lat, lon, alt;

    public FindTarget(double drone_alt, double drone_lat, double drone_lon, double drone_course, double gimbal_pitch) {
        this.Drone_alt = drone_alt;
        this.Drone_lat = drone_lat;
        this.Drone_lon = drone_lon;
        this.Drone_course = drone_course;
        this.Gimbal_pitch = gimbal_pitch;
    }

    public void find_target_coordinates() {
        double geo_alt = heightFinder.FindH(Drone_lat, Drone_lon);
        Drone_alt += geo_alt;
        int nz = (int) (Drone_lon / 6 + 1);
        double l0 = 6 * nz - 3;
        double y = (Drone_lon - l0) * Math.sin(Math.toRadians(Drone_lat));
        double da = (float) Drone_course - y;
        //  Певая инициализация возможных значений цели
        double current_target_height = Drone_alt;
        geoCalc.wgs84ToPlane(target_x_plane, target_y_plane, target_h_plane, NGeoCalc.degreesToRadians(Drone_lat), NGeoCalc.degreesToRadians(Drone_lon), Drone_alt, -1);
        if (Gimbal_pitch >= 0) Camera_target_distance = 0;
        else {
            while (is_on_air(current_target_height, target_x_plane[0], target_y_plane[0])) {
                Camera_target_distance += 5; // Нужно потом будет золотое сечение сюда вставить
                target_x_plane[0] = target_x_plane[0] + Math.cos(Math.toRadians(da)) * Camera_target_distance;
                target_y_plane[0] = target_y_plane[0] + Math.sin(Math.toRadians(da)) * Camera_target_distance;
                current_target_height -= Math.cos(Math.toRadians(90 + Gimbal_pitch)) * Camera_target_distance;
            }
            // Пересчет координат с СК в wgs
            double[] target_lat = new double[1];
            double[] target_lon = new double[1];
            double[] target_alt = new double[1];
            geoCalc.planeToWgs84(target_lat, target_lon, target_alt, target_x_plane[0], target_y_plane[0], 0);
            target_lat[0] = NGeoCalc.radiansToDegrees(target_lat[0]);
            target_lon[0] = NGeoCalc.radiansToDegrees(target_lon[0]);
            lat = target_lat[0];
            lon = target_lon[0];
            alt = current_target_height;
        }
    }

    private boolean is_on_air(double height, double x, double y) {
        double[] x_wgs = new double[1], y_wgs = new double[1], point_h = new double[1];
        double ground;
        geoCalc.planeToWgs84(x_wgs, y_wgs, point_h, x, y, 0);
        x_wgs[0] = NGeoCalc.radiansToDegrees(x_wgs[0]);
        y_wgs[0] = NGeoCalc.radiansToDegrees(y_wgs[0]);
        ground = heightFinder.FindH(x_wgs[0], y_wgs[0]);
        return ground < height;
    }
}
