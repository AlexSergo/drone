package com.example.dronevision.utils

class NGeoCalc {
    /* access modifiers changed from: package-private */
    fun pza(): Double {
        return 6378136.0
    }
    
    /* access modifiers changed from: package-private */
    fun pzA(): Double {
        return 0.003352803735
    }
    
    /* access modifiers changed from: package-private */
    fun wgsa(): Double {
        return 6378137.0
    }
    
    /* access modifiers changed from: package-private */
    fun wgsA(): Double {
        return 0.003352810665
    }
    
    /* access modifiers changed from: package-private */
    fun ska(): Double {
        return 6378245.0
    }
    
    /* access modifiers changed from: package-private */
    fun skA(): Double {
        return 0.003352329869
    }
    
    fun geoToGeo(
        Bb: DoubleArray,
        Lb: DoubleArray,
        Hb: DoubleArray,
        ab: Double,
        Ab: Double,
        Ba: Double,
        La: Double,
        Ha: Double,
        aa: Double,
        Aa: Double,
        dx: Double,
        dy: Double,
        dz: Double,
        wx: Double,
        wy: Double,
        wz: Double,
        m: Double
    ) {
        val dB = DoubleArray(1)
        val dL = DoubleArray(1)
        val dH = DoubleArray(1)
        geoToGeoDelta(dB, dL, dH, ab, Ab, Ba, La, Ha, aa, Aa, dx, dy, dz, wx, wy, wz, m)
        geoToGeoDelta(
            dB, dL, dH, ab, Ab, Ba + degreesToRadians(dB[0] / 3600.0) / 2.0, La + degreesToRadians(
                dL[0] / 3600.0
            ) / 2.0,
            Ha + dH[0] / 2.0, aa, Aa, dx, dy, dz, wx, wy, wz, m
        )
        Bb[0] = Ba + degreesToRadians(dB[0] / 3600.0)
        Lb[0] = La + degreesToRadians(dL[0] / 3600.0)
        Hb[0] = Ha + dH[0]
    }
    
    fun geoToGeoDelta(
        dB: DoubleArray,
        dL: DoubleArray,
        dH: DoubleArray,
        ab: Double,
        Ab: Double,
        Ba: Double,
        La: Double,
        Ha: Double,
        aa: Double,
        Aa: Double,
        dx: Double,
        dy: Double,
        dz: Double,
        wx: Double,
        wy: Double,
        wz: Double,
        m: Double
    ) {
        val da = ab - aa
        val e2b = Ab * 2.0 - Ab * Ab
        val e2a = Aa * 2.0 - Aa * Aa
        val de2 = e2b - e2a
        val a = (ab + aa) / 2.0
        val e2 = (e2b + e2a) / 2.0
        val sinB = Math.sin(Ba)
        val cosB = Math.cos(Ba)
        val cos2B = Math.cos(Ba * 2.0)
        val sinL = Math.sin(La)
        val cosL = Math.cos(La)
        val tanB = Math.tan(Ba)
        val d2 = e2b
        val M = (1.0 - e2) * a * Math.pow(1.0 - e2 * sinB * sinB, -1.5)
        val N = Math.pow(1.0 - e2 * sinB * sinB, -0.5) * a
        val d3 = e2a
        dB[0] =
            206264.806 / (M + Ha) * (N / a * e2 * sinB * cosB * da + (Math.pow(N, 2.0) / Math.pow(
                a,
                2.0
            ) + 1.0) * N * sinB * cosB * de2 / 2.0 - (dx * cosL + dy * sinL) * sinB + dz * cosB) - wx * sinL * (e2 * cos2B + 1.0) + wy * cosL * (e2 * cos2B + 1.0) - 206264.806 * m * e2 * sinB * cosB
        dL[0] =
            206264.806 / ((N + Ha) * cosB) * (-dx * sinL + dy * cosL) + (1.0 - e2) * tanB * (wx * cosL + wy * sinL) - wz
        dH[0] =
            -a / N * da + N * sinB * sinB * de2 / 2.0 + (dx * cosL + dy * sinL) * cosB + dz * sinB - N * e2 * sinB * cosB * (wx / 206264.806 * sinL - wy / 206264.806 * cosL) + (a * a / N + Ha) * m
    }
    
    /* access modifiers changed from: package-private */
    fun modf(fullDouble: Double): DoubleArray {
        val intVal = fullDouble.toInt()
        val d = intVal.toDouble()
        java.lang.Double.isNaN(d)
        return doubleArrayOf(intVal.toDouble(), fullDouble - d)
    }
    
    fun geoToPlane(x: DoubleArray, y: DoubleArray, B: Double, L: Double, nzone: Int) {
        val n: Double
        val L2 = radiansToDegrees(positiveLongitude(L))
        n = if (nzone <= 0 || nzone > 60) {
            modf(L2 / 6.0 + 1.0)[0]
        } else {
            nzone.toDouble()
        }
        val l = (L2 - ((n - 1.0) * 6.0 + 3.0)) / 57.29577951
        val l2 = l * l
        val sinB = Math.sin(B)
        val sin2B = Math.sin(2.0 * B)
        val sinB2 = sinB * sinB
        val sinB4 = sinB2 * sinB2
        val sinB6 = sinB2 * sinB4
        val cosB = Math.cos(B)
        x[0] =
            6367558.4968 * B - (66.9607 * sinB2 + 16002.89 + 0.3515 * sinB4 - (5336.535 * sinB2 + 1594561.25 + 26.79 * sinB4 + 0.149 * sinB6 + (672483.4 - 811219.9 * sinB2 + 5420.0 * sinB4 - 10.6 * sinB6 + (278194.0 - 830174.0 * sinB2 + 572434.0 * sinB4 - 16010.0 * sinB6 + (109500.0 - 574700.0 * sinB2 + 863700.0 * sinB4 - 398600.0 * sinB6) * l2) * l2) * l2) * l2) * sin2B
        y[0] =
            (10.0 * n + 5.0) * 100000.0 + l * cosB * (21346.1415 * sinB2 + 6378245.0 + 107.159 * sinB4 + 0.5977 * sinB6 + (1070204.16 - 2136826.66 * sinB2 + 17.98 * sinB4 - 11.99 * sinB6 + (270806.0 - 1523417.0 * sinB2 + 1327645.0 * sinB4 - 21701.0 * sinB6 + (79690.0 - 866190.0 * sinB2 + 1730360.0 * sinB4 - 945460.0 * sinB6) * l2) * l2) * l2)
    }
    
    fun planeToGeo(B: DoubleArray, L: DoubleArray, x: Double, y: Double) {
        val Beta = x / 6367558.4968
        val sin2Beta = Math.sin(Beta * 2.0)
        val sinBeta = Math.sin(Beta)
        val sinBeta2 = sinBeta * sinBeta
        val B0 =
            (0.00252588685 - 1.49186E-5 * sinBeta2 + 1.1904E-7 * sinBeta2 * sinBeta2) * sin2Beta + Beta
        val cosB0 = Math.cos(B0)
        val sin2B0 = Math.sin(2.0 * B0)
        val sinB0 = Math.sin(B0)
        val sinB02 = sinB0 * sinB0
        val sinB04 = sinB02 * sinB02
        val sinB06 = sinB04 * sinB02
        val d = Beta
        val d2 = sin2Beta
        val n = modf(y / 1000000.0)[0]
        val z0 = (y - (10.0 * n + 5.0) * 100000.0) / (6378245.0 * cosB0)
        val z02 = z0 * z0
        B[0] =
            B0 + -z02 * sin2B0 * (0.251684631 - 0.003369263 * sinB02 + 1.1276E-5 * sinB04 - (0.10500614 - 0.04559916 * sinB02 + 0.00228901 * sinB04 - 2.987E-5 * sinB06 - (0.042858 - 0.025318 * sinB02 * 0.014346 * sinB04 - 0.001264 * sinB06 - (0.01672 - 0.0063 * sinB02 + 0.01188 * sinB04 - 0.00328 * sinB06) * z02) * z02) * z02)
        L[0] =
            (n - 0.5) * 6.0 / 57.29577951 + (1.0 - 0.0033467108 * sinB02 - 5.6002E-6 * sinB04 - 1.87E-8 * sinB06 - (0.16273586 * sinB02 + 0.16778975 - 5.249E-4 * sinB04 - 8.46E-6 * sinB06 - (0.1487407 * sinB02 + 0.0420025 + 0.005942 * sinB04 - 1.5E-5 * sinB06 - (0.09477 * sinB02 + 0.01225 + 0.03282 * sinB04 - 3.4E-4 * sinB06 - (0.0524 * sinB02 + 0.0038 + 0.0482 * sinB04 + 0.0032 * sinB06) * z02) * z02) * z02) * z02) * z0
    }
    
    /* access modifiers changed from: package-private */
    fun dxSk42ToPz9002(): Double {
        return 23.93
    }
    
    /* access modifiers changed from: package-private */
    fun dySk42ToPz9002(): Double {
        return -141.03
    }
    
    /* access modifiers changed from: package-private */
    fun dzSk42ToPz9002(): Double {
        return -79.98
    }
    
    /* access modifiers changed from: package-private */
    fun wxGeoSk42ToPz9002(): Double {
        return 0.0
    }
    
    /* access modifiers changed from: package-private */
    fun wyGeoSk42ToPz9002(): Double {
        return -0.35
    }
    
    /* access modifiers changed from: package-private */
    fun wzGeoSk42ToPz9002(): Double {
        return -0.79
    }
    
    /* access modifiers changed from: package-private */
    fun mSk42ToPz9002(): Double {
        return -2.2E-7
    }
    
    /* access modifiers changed from: package-private */
    fun dxPz9002ToWgs84(): Double {
        return -0.36
    }
    
    /* access modifiers changed from: package-private */
    fun dyPz9002ToWgs84(): Double {
        return 0.08
    }
    
    /* access modifiers changed from: package-private */
    fun dzPz9002ToWgs84(): Double {
        return 0.18
    }
    
    /* access modifiers changed from: package-private */
    fun wxGeoPz9002ToWgs84(): Double {
        return 0.0
    }
    
    /* access modifiers changed from: package-private */
    fun wyGeoPz9002ToWgs84(): Double {
        return 0.0
    }
    
    /* access modifiers changed from: package-private */
    fun wzGeoPz9002ToWgs84(): Double {
        return 0.0
    }
    
    /* access modifiers changed from: package-private */
    fun mPz9002ToWgs84(): Double {
        return 0.0
    }
    
    /* access modifiers changed from: package-private */
    fun fmod(x: Double, y: Double): Double {
        val quot = x / y
        return x - (if (quot < 0.0) Math.ceil(quot) else Math.floor(quot)) * y
    }
    
    /* access modifiers changed from: package-private */
    fun positiveLongitude(value: Double): Double {
        val result = fmod(value, 6.283185307179586)
        return if (result < 0.0) {
            result + 6.283185307179586
        } else result
    }
    
    @JvmOverloads
    fun wgs84ToPlane(
        x: DoubleArray,
        y: DoubleArray,
        h: DoubleArray,
        B: Double,
        L: Double,
        H: Double,
        nzone: Int = 0
    ) {
        val geoCalc = NGeoCalc()
        val Bpz9002 = DoubleArray(1)
        val Lpz9002 = DoubleArray(1)
        val Hpz9002 = DoubleArray(1)
        val Bsk42 = DoubleArray(1)
        val Lsk42 = DoubleArray(1)
        val Hsk42 = DoubleArray(1)
        geoCalc.geoToGeo(
            Bpz9002,
            Lpz9002,
            Hpz9002,
            geoCalc.pza(),
            geoCalc.pzA(),
            B,
            L,
            H,
            geoCalc.wgsa(),
            geoCalc.wgsA(),
            -geoCalc.dxPz9002ToWgs84(),
            -geoCalc.dyPz9002ToWgs84(),
            -geoCalc.dzPz9002ToWgs84(),
            -geoCalc.wxGeoPz9002ToWgs84(),
            -geoCalc.wyGeoPz9002ToWgs84(),
            -geoCalc.wzGeoPz9002ToWgs84(),
            -geoCalc.mPz9002ToWgs84()
        )
        geoCalc.geoToGeo(
            Bsk42,
            Lsk42,
            Hsk42,
            geoCalc.ska(),
            geoCalc.skA(),
            Bpz9002[0],
            Lpz9002[0],
            Hpz9002[0],
            geoCalc.pza(),
            geoCalc.pzA(),
            -geoCalc.dxSk42ToPz9002(),
            -geoCalc.dySk42ToPz9002(),
            -geoCalc.dzSk42ToPz9002(),
            -geoCalc.wxGeoSk42ToPz9002(),
            -geoCalc.wyGeoSk42ToPz9002(),
            -geoCalc.wzGeoSk42ToPz9002(),
            -geoCalc.mSk42ToPz9002()
        )
        geoCalc.geoToPlane(x, y, Bsk42[0], Lsk42[0], nzone)
        h[0] = Hsk42[0]
    }
    
    fun planeToWgs84(
        B: DoubleArray,
        L: DoubleArray,
        H: DoubleArray,
        x: Double,
        y: Double,
        h: Double
    ) {
        val geoCalc = NGeoCalc()
        val Bsk42 = DoubleArray(1)
        val Lsk42 = DoubleArray(1)
        val Bpz9002 = DoubleArray(1)
        val Lpz9002 = DoubleArray(1)
        val Hpz9002 = DoubleArray(1)
        geoCalc.planeToGeo(Bsk42, Lsk42, x, y)
        val dArr = Bsk42
        val dArr2 = Lsk42
        geoCalc.geoToGeo(
            Bpz9002,
            Lpz9002,
            Hpz9002,
            geoCalc.pza(),
            geoCalc.pzA(),
            Bsk42[0],
            Lsk42[0],
            h,
            geoCalc.ska(),
            geoCalc.skA(),
            geoCalc.dxSk42ToPz9002(),
            geoCalc.dySk42ToPz9002(),
            geoCalc.dzSk42ToPz9002(),
            geoCalc.wxGeoSk42ToPz9002(),
            geoCalc.wyGeoSk42ToPz9002(),
            geoCalc.wzGeoSk42ToPz9002(),
            geoCalc.mSk42ToPz9002()
        )
        geoCalc.geoToGeo(
            B,
            L,
            H,
            geoCalc.wgsa(),
            geoCalc.wgsA(),
            Bpz9002[0],
            Lpz9002[0],
            Hpz9002[0],
            geoCalc.pza(),
            geoCalc.pzA(),
            geoCalc.dxPz9002ToWgs84(),
            geoCalc.dyPz9002ToWgs84(),
            geoCalc.dzPz9002ToWgs84(),
            geoCalc.wxGeoPz9002ToWgs84(),
            geoCalc.wyGeoPz9002ToWgs84(),
            geoCalc.wzGeoPz9002ToWgs84(),
            geoCalc.mPz9002ToWgs84()
        )
    }
    
    fun planeToPlaneByZone(
        xbuf: DoubleArray,
        ybuf: DoubleArray,
        hbuf: DoubleArray,
        x: Double,
        y: Double,
        h: Double,
        nzone: Int
    ) {
        val b = doubleArrayOf(0.0)
        val l = doubleArrayOf(0.0)
        val h2 = doubleArrayOf(0.0)
        planeToWgs84(b, l, h2, x, y, h)
        wgs84ToPlane(xbuf, ybuf, hbuf, b[0], l[0], h2[0], nzone)
    }
    
    companion object {
        fun degreesToRadians(degrees: Double): Double {
            return 3.141592653589793 * degrees / 180.0
        }
        
        fun radiansToDegrees(radians: Double): Double {
            return 180.0 * radians / 3.141592653589793
        }
        
        @JvmStatic
        fun main(argv: Array<String>) {
            val geoCalc = NGeoCalc()
            val x = DoubleArray(1)
            val y = DoubleArray(1)
            val h = DoubleArray(1)
            geoCalc.wgs84ToPlane(
                x,
                y,
                h,
                degreesToRadians(58.2342),
                degreesToRadians(38.9109),
                135.0,
                -1
            )
            val printStream = System.out
            printStream.println(
                x[0].toInt().toString() + " " + y[0].toInt() + " " + h[0].toInt()
            )
            val b = DoubleArray(1)
            val l = DoubleArray(1)
            geoCalc.planeToWgs84(b, l, h, x[0], y[0], h[0])
            val printStream2 = System.out
            printStream2.println(
                radiansToDegrees(b[0])
                    .toString() + " " + radiansToDegrees(
                    l[0]
                ) + " " + h[0]
            )
            val str = " "
            geoCalc.planeToPlaneByZone(x, y, h, 6457472.0, 7494882.0, 135.0, 8)
            val printStream3 = System.out
            printStream3.println("To zone 8: " + x[0].toInt() + str + y[0].toInt() + str + h[0].toInt())
            geoCalc.planeToPlaneByZone(x, y, h, 6457472.0, 7494882.0, 135.0, 6)
            val printStream4 = System.out
            printStream4.println("To zone 6: " + x[0].toInt() + str + y[0].toInt() + str + h[0].toInt())
        }
    }
}
