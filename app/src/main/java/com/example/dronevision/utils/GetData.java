package com.example.dronevision.utils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.dronevision.presentation.model.bluetooth.Entity;

public class GetData {

    private DroneData droneData = new DroneData();
    private Target droneTarget = new Target();

    private final Thread aimUpdater = new AimUpdater();

    public void setDroneData(Entity data, Double asim){
        if (asim == null)
            asim = 0.0;
        droneData.lat = data.getLat();
        droneData.lon = data.getLon();
        droneData.alt = data.getAlt();
        droneData.gimbalYawRelative = data.getCam_deflect();
        droneData.gimbalPitch = data.getCam_angle();
        droneData.heading = data.getAsim() + asim;
    }

    public void updater() {
        if (!aimUpdater.isAlive())
            aimUpdater.start();
    }

    public DroneData getDrone() {
        return droneData;
    }

    public Target getTarget() {
        return droneTarget;
    }

    private class AimUpdater extends Thread {
        private NGeoCalc geoCalc = new NGeoCalc();
        @Override
        public void run() {
            while (true) {
                //  Переменные для работы geoCalc
                double[] droneX = new double[1];
                double[] droneY = new double[1];
                double[] droneH = new double[1];

                double[] target_X_wgs = new double[1];
                double[] target_Y_wgs = new double[1];
                double[] target_H_wgs = new double[1];


                try {
                    droneTarget.Nz = (int) (droneData.lon / 6 + 1);
                    droneTarget.L0 = 6 * droneTarget.Nz - 3;
                    droneTarget.Y = (droneData.lon - droneTarget.L0) * Math.sin(Math.toRadians(droneData.lat));
                    droneTarget.Da = (float) (droneData.heading + droneData.devation + droneData.gimbalYawRelative - droneTarget.Y);
                    if (droneData.gimbalPitch >= 0)
                        droneTarget.cameraTargetDistance = 0;
                    else
                        droneTarget.cameraTargetDistance = droneData.alt * Math.tan(Math.toRadians(90 + droneData.gimbalPitch));

                    geoCalc.wgs84ToPlane(droneX, droneY, droneH,
                            NGeoCalc.degreesToRadians(droneData.lat),
                            NGeoCalc.degreesToRadians(droneData.lon),
                            0, -1);
                    droneTarget.targetX = droneX[0] + Math.cos(Math.toRadians(droneTarget.Da)) * droneTarget.cameraTargetDistance;
                    droneTarget.targetY = droneY[0] + Math.sin(Math.toRadians(droneTarget.Da)) * droneTarget.cameraTargetDistance;
                    if (droneTarget.fire_target_x > 0 && droneTarget.fire_target_y > 0) {
                        droneTarget.targetX += droneTarget.targetX - droneTarget.fire_target_x;
                        droneTarget.targetY += droneTarget.targetY - droneTarget.fire_target_y;
                    }
                    geoCalc.planeToWgs84(target_X_wgs, target_Y_wgs, target_H_wgs, droneTarget.targetX, droneTarget.targetY, 0);

                    droneTarget.lat = NGeoCalc.radiansToDegrees(target_X_wgs[0]);
                    droneTarget.lon = NGeoCalc.radiansToDegrees(target_Y_wgs[0]);
                } catch (Exception ignored) {}


                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}


