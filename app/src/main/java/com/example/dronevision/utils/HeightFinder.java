package com.example.dronevision.utils;

import java.io.File;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;
import java.nio.channels.FileChannel;

public class HeightFinder {

    private static final int SECONDS_PER_MINUTE = 60;

    // alter these values for different SRTM resolutions
    public static final int HGT_RES = 1; // resolution in arc seconds
    public static final int HGT_ROW_LENGTH = 3601; // number of elevation values per line

    public double FindH(double coorLat, double coorLon) {
        String filePath = "/data/data/com.example.dronevision/files/";
        String fileName = getHgtFileName(coorLat, coorLon);

        String htgFile = filePath + fileName;

//  System.out.println(htgFile);


        ShortBuffer data = null;
        try {
            data = readHgtFile(htgFile);
        } catch (Exception ignored){}

        double fLat = frac(Math.abs(coorLat)) * SECONDS_PER_MINUTE;
        double fLon = frac(Math.abs(coorLon)) * SECONDS_PER_MINUTE;

        int row = (int) Math.round(fLat * SECONDS_PER_MINUTE / HGT_RES);
        int col = (int) Math.round(fLon * SECONDS_PER_MINUTE / HGT_RES);


        row = HGT_ROW_LENGTH - row;
        int cell = (HGT_ROW_LENGTH * (row - 1)) + col;


        if (data == null) {
            return -200;
        }
        // valid position in buffer?
        if (cell < data.limit()) {
            return data.get(cell);
        } else {
            return -100;
        }
    }

    private static ShortBuffer readHgtFile(String file) throws Exception {

        FileChannel fc = null;
        ShortBuffer sb;
        try {
            // "/data/data/com.dji.ux.sample/files/maps/n55e037.hgt"
            File dir = new File(file); //path указывает на директорию
            File[] files = dir.listFiles();
            // Eclipse complains here about resource leak on 'fc' - even with 'finally' clause???
            fc = new FileInputStream(file).getChannel();

            if (null != fc) {
                // choose the right endianness

                ByteBuffer bb = ByteBuffer.allocateDirect((int) fc.size());
                while (bb.remaining() > 0) fc.read(bb);

                bb.flip();
                sb = bb.order(ByteOrder.BIG_ENDIAN).asShortBuffer();
            }
            else {
                sb = null;
            }
        } finally {
            if (fc != null) fc.close();
        }
        return sb;
    }

    public static String getHgtFileName(double latD, double lonD) {
        int lat = (int) latD;
        int lon = (int) lonD;
        String latPref = "n";
        if (lat < 0) latPref = "s";

        String lonPref = "e";
        if (lon < 0) {
            lonPref = "w";
        }
        String lonT = String.valueOf(lon).replace("-", "");
        if (lonT.length() == 2) {
            lonT = "0" + lonT;
        }
        String ret = latPref + lat + lonPref + lonT + ".hgt";
        return ret.replace("-", "");
    }

    public static double frac(double d) {
        long iPart;
        double fPart;
        iPart = (long) d;
        fPart = d - iPart;
        return fPart;
    }
}