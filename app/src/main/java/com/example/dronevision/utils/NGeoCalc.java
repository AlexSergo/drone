package com.example.dronevision.utils;

public class NGeoCalc {
    final double pza()  { return 6378136.0; }
    final double pzA()  { return 0.003352803735; } // 1.0 / 298.25784
    final double wgsa() { return 6378137.0; }
    final double wgsA() { return 0.003352810665; } // 1.0 / 298.257223563
    final double ska()  { return 6378245.0; }
    final double skA()  { return 0.003352329869; } // 1.0 / 298.3
    //
    public void geoToGeo(double[] Bb, double[] Lb, double[] Hb, double ab, double Ab,
                         double  Ba, double  La, double  Ha, double aa, double Aa,
                         double dx, double dy, double dz, double wx, double wy, double wz, double m) {
        double[] dB = new double[1];
        double[] dL = new double[1];
        double[] dH = new double[1];
        double B, L, H;
        geoToGeoDelta(dB, dL, dH, ab, Ab,
                Ba, La, Ha, aa, Aa,
                dx, dy, dz, wx, wy, wz, m);
        B = Ba + degreesToRadians(dB[0] / 3600) / 2;
        L = La + degreesToRadians(dL[0] / 3600) / 2;
        H = Ha + dH[0] / 2;
        geoToGeoDelta(dB, dL, dH, ab, Ab,
                B, L, H, aa, Aa,
                dx, dy, dz, wx, wy, wz, m);
        Bb[0] = Ba + degreesToRadians(dB[0] / 3600);
        Lb[0] = La + degreesToRadians(dL[0] / 3600);
        Hb[0] = Ha + dH[0];
    }
    //
    public void geoToGeoDelta(double[] dB, double[] dL, double[] dH, double ab, double Ab,
                              double  Ba, double  La, double  Ha, double aa, double Aa,
                              double dx, double dy, double dz, double wx, double wy, double wz, double m) {
        double da = ab - aa;
        double e2b = 2.0 * Ab - Ab * Ab;
        double e2a = 2.0 * Aa - Aa * Aa;
        double de2 = e2b - e2a;
        double a = (ab + aa) / 2.0;
        double e2 = (e2b + e2a) / 2.0;
        double sinB = Math.sin(Ba);
        double cosB = Math.cos(Ba);
        double cos2B = Math.cos(2 * Ba);
        double sinL = Math.sin(La);
        double cosL = Math.cos(La);
        double tanB = Math.tan(Ba);
        double M = a * (1 - e2) * Math.pow(1 - e2 * sinB * sinB, - 1.5);
        double N = a * Math.pow(1 - e2 * sinB * sinB, -0.5);
        double ro = 206264.806;
        dB[0] = ro / (M + Ha) *
                (
                        N / a * e2 * sinB * cosB * da
                                + (Math.pow(N, 2) / Math.pow(a, 2) + 1) * N * sinB * cosB * de2 / 2.0
                                - (dx * cosL + dy * sinL) * sinB + dz * cosB
                )
                - wx * sinL * (1 + e2 * cos2B)
                + wy * cosL * (1 + e2 * cos2B)
                - ro * m * e2 * sinB * cosB;
        dL[0] = ro / ((N + Ha) * cosB) * (-dx * sinL + dy * cosL)
                + tanB * (1 - e2) * (wx * cosL + wy * sinL)
                - wz;
        dH[0] = - a / N * da + N * sinB * sinB * de2 / 2
                + (dx * cosL + dy * sinL) * cosB
                + dz * sinB
                - N * e2 * sinB * cosB * (wx / ro * sinL - wy / ro * cosL)
                + (a * a / N + Ha) * m;

    }
    //
    double[] modf(double fullDouble) {
        int intVal = (int)fullDouble;
        double remainder = fullDouble - intVal;

        double[] retVal = new double[2];
        retVal[0] = intVal;
        retVal[1] = remainder;

        return retVal;
    }


    //
    public void geoToPlane(double[] x, double[] y, double B, double L, int nzone) {
        L = radiansToDegrees(positiveLongitude(L));
        double n;
        if (nzone > 0 && nzone <= 60) {
            n = nzone;
        } else {
            double[] ptr = modf(1.0 + L / 6.0);
            n = ptr[0];
        }
        double l = (L - (3.0 + 6.0 * (n - 1.0))) / 57.29577951;
        double l2 = l * l;
        double sinB = Math.sin(B);
        double sin2B = Math.sin(2 * B);
        double sinB2 = sinB * sinB;
        double sinB4 = sinB2 * sinB2;
        double sinB6 = sinB2 * sinB4;
        double cosB = Math.cos(B);
        x[0] = 6367558.4968 * B - sin2B * (16002.89 + 66.9607 * sinB2 + 0.3515 * sinB4 -
                l2 * (1594561.25 + 5336.535 * sinB2 + 26.79 * sinB4 + 0.149 * sinB6 +
                        l2 * (672483.4 - 811219.9 * sinB2 + 5420 * sinB4 - 10.6 * sinB6 +
                                l2 * (278194 - 830174 * sinB2 + 572434 * sinB4 - 16010 * sinB6 +
                                        l2 * (109500 - 574700 * sinB2 + 863700 * sinB4 - 398600 * sinB6)))));
        y[0] = (5 + 10 * n) * 100000 +
                l * cosB * (6378245 + 21346.1415 * sinB2 + 107.159 * sinB4 + 0.5977 * sinB6 +
                        l2 * (1070204.16 - 2136826.66 * sinB2 + 17.98 * sinB4 - 11.99 * sinB6 +
                                l2 * (270806 - 1523417 * sinB2 + 1327645 * sinB4 - 21701 * sinB6 +
                                        l2 * (79690 - 866190 * sinB2 + 1730360 * sinB4 - 945460 * sinB6))));
    }
    //
    public void planeToGeo(double[] B, double[] L, double x, double y) {
        double Beta = x / 6367558.4968;
        double sin2Beta = Math.sin(2 * Beta);
        double sinBeta = Math.sin(Beta);
        double sinBeta2 = sinBeta * sinBeta;
        double sinBeta4 = sinBeta2 * sinBeta2;
        double B0 = Beta + sin2Beta * (0.00252588685 - 0.0000149186 * sinBeta2 + 0.00000011904 * sinBeta4);
        double cosB0 = Math.cos(B0);
        double sin2B0 = Math.sin(2 * B0);
        double sinB0 = Math.sin(B0);
        double sinB02 = sinB0 * sinB0;
        double sinB04 = sinB02 * sinB02;
        double sinB06 = sinB04 * sinB02;
        double[] ptr = modf(y / 1000000.0);
        double n = ptr[0];
        double z0 = (y - (10.0 * n + 5.0) * 100000.0) / (6378245.0 * cosB0);
        double z02 = z0 * z0;
        double dB = - z02 * sin2B0 * (0.251684631 - 0.003369263 * sinB02 + 0.000011276 * sinB04
                - z02 * (0.10500614 - 0.04559916 * sinB02 + 0.00228901 * sinB04 - 0.00002987 * sinB06
                - z02 * (0.042858 - 0.025318 * sinB02 * 0.014346 * sinB04 - 0.001264 * sinB06
                - z02 * (0.01672 - 0.0063 * sinB02 + 0.01188 * sinB04 - 0.00328 * sinB06))));
        double l = z0 * (1 - 0.0033467108 * sinB02 - 0.0000056002 * sinB04 - 0.0000000187 * sinB06 -
                z02 * (0.16778975 + 0.16273586 * sinB02 - 0.0005249 * sinB04 - 0.00000846 * sinB06 -
                        z02 * (0.0420025 + 0.1487407 * sinB02 + 0.005942 * sinB04 - 0.000015 * sinB06 -
                                z02 * (0.01225 + 0.09477 * sinB02 + 0.03282 * sinB04 - 0.00034 * sinB06 -
                                        z02 * (0.0038 + 0.0524 * sinB02 + 0.0482 * sinB04 + 0.0032 * sinB06)))));
        B[0] = B0 + dB;
        L[0] = 6.0 * (n - 0.5) / 57.29577951 + l;
    }
    //
    final double dxSk42ToPz9002()              { return 23.93; }
    final double dySk42ToPz9002()              { return -141.03; }
    final double dzSk42ToPz9002()              { return -79.98; }
    final double wxGeoSk42ToPz9002()           { return 0.0; }
    final double wyGeoSk42ToPz9002()           { return -0.35; }
    final double wzGeoSk42ToPz9002()           { return -0.79; }
    final double wxSpatialSk42ToPz9002()       { return 0.0; }
    final double wySpatialSk42ToPz9002()       { return -0.0000016968; }
    final double wzSpatialSk42ToPz9002()       { return -0.00000383; }
    final double mSk42ToPz9002()               { return -0.00000022; }
    //
    final double dxPz9002ToWgs84()              { return -0.36; }
    final double dyPz9002ToWgs84()              { return 0.08; }
    final double dzPz9002ToWgs84()              { return 0.18; }
    final double wxGeoPz9002ToWgs84()           { return 0.0; }
    final double wyGeoPz9002ToWgs84()           { return 0.0; }
    final double wzGeoPz9002ToWgs84()           { return 0.0; }
    final double wxSpatialPz9002ToWgs84()       { return 0.0; }
    final double wySpatialPz9002ToWgs84()       { return 0.0; }
    final double wzSpatialPz9002ToWgs84()       { return 0.0; }
    final double mPz9002ToWgs84()               { return 0.0; }
    //
    double fmod(double x, double y) {
        double quot = x/y;
        return x - (quot < 0.0 ? Math.ceil(quot) : Math.floor(quot)) * y;
    }
    //
    double positiveLongitude(double value) {
        double pi2 = Math.PI * 2.0;
        double result = fmod(value, pi2);
        if (result < 0.0) {
            result = pi2 + result;
        }
        return result;
    }
    public static double degreesToRadians(double degrees) { return (Math.PI * degrees / 180.0); }
    public static double radiansToDegrees(double radians) { return (radians * 180.0 / Math.PI); }
    //
    public void wgs84ToPlane(double[] x, double[] y, double[] h, double B, double L, double H) {
        wgs84ToPlane(x, y, h, B, L, H, 0);
    }
    //
    public void wgs84ToPlane(double[] x, double[] y, double[] h, double B, double L, double H, int nzone) {
        NGeoCalc geoCalc = new NGeoCalc();
        double[] Bpz9002 = new double[1];
        double[] Lpz9002 = new double[1];
        double[] Hpz9002 = new double[1];
        double[] Bsk42   = new double[1];
        double[] Lsk42   = new double[1];
        double[] Hsk42   = new double[1];
        geoCalc.geoToGeo(Bpz9002, Lpz9002, Hpz9002, geoCalc.pza(), geoCalc.pzA(),
                B, L, H, geoCalc.wgsa(), geoCalc.wgsA(),
                -geoCalc.dxPz9002ToWgs84(), -geoCalc.dyPz9002ToWgs84(), -geoCalc.dzPz9002ToWgs84(),
                -geoCalc.wxGeoPz9002ToWgs84(), -geoCalc.wyGeoPz9002ToWgs84(), -geoCalc.wzGeoPz9002ToWgs84(),
                -geoCalc.mPz9002ToWgs84());
        geoCalc.geoToGeo(Bsk42, Lsk42, Hsk42, geoCalc.ska(), geoCalc.skA(),
                Bpz9002[0], Lpz9002[0], Hpz9002[0], geoCalc.pza(), geoCalc.pzA(),
                -geoCalc.dxSk42ToPz9002(), -geoCalc.dySk42ToPz9002(), -geoCalc.dzSk42ToPz9002(),
                -geoCalc.wxGeoSk42ToPz9002(), -geoCalc.wyGeoSk42ToPz9002(), -geoCalc.wzGeoSk42ToPz9002(),
                -geoCalc.mSk42ToPz9002());
        geoCalc.geoToPlane(x, y, Bsk42[0], Lsk42[0], nzone);
        h[0] = Hsk42[0];
    }
    //
    public void planeToWgs84(double[] B, double[] L, double[] H, double x, double y, double h) {
        NGeoCalc geoCalc = new NGeoCalc();
        double[] Bsk42   = new double[1];
        double[] Lsk42   = new double[1];
        double[] Bpz9002 = new double[1];
        double[] Lpz9002 = new double[1];
        double[] Hpz9002 = new double[1];
        //
        geoCalc.planeToGeo(Bsk42, Lsk42, x, y);
        geoCalc.geoToGeo(Bpz9002, Lpz9002, Hpz9002, geoCalc.pza(), geoCalc.pzA(),
                Bsk42[0], Lsk42[0], h, geoCalc.ska(), geoCalc.skA(),
                geoCalc.dxSk42ToPz9002(), geoCalc.dySk42ToPz9002(), geoCalc.dzSk42ToPz9002(),
                geoCalc.wxGeoSk42ToPz9002(), geoCalc.wyGeoSk42ToPz9002(), geoCalc.wzGeoSk42ToPz9002(),
                geoCalc.mSk42ToPz9002());
        geoCalc.geoToGeo(B, L, H, geoCalc.wgsa(), geoCalc.wgsA(),
                Bpz9002[0], Lpz9002[0], Hpz9002[0], geoCalc.pza(), geoCalc.pzA(),
                geoCalc.dxPz9002ToWgs84(), geoCalc.dyPz9002ToWgs84(), geoCalc.dzPz9002ToWgs84(),
                geoCalc.wxGeoPz9002ToWgs84(), geoCalc.wyGeoPz9002ToWgs84(), geoCalc.wzGeoPz9002ToWgs84(),
                geoCalc.mPz9002ToWgs84());
    }
    //
    public void planeToPlaneByZone(double[] xbuf, double[] ybuf, double[] hbuf,
                                   double x, double y, double h, int nzone) {
        double[] b = { 0 }, l = { 0 }, h2 = { 0 };
        planeToWgs84(b, l, h2, x, y, h);
        wgs84ToPlane(xbuf, ybuf, hbuf, b[0], l[0], h2[0], nzone);
    }
    //

}

