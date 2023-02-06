package com.example.dronevision.utils

import org.osmdroid.util.GeoPoint
import kotlin.math.abs
import kotlin.math.sqrt

data class Point(val x: Double, val y: Double)

class PointCalibration: Thread() {

    private val geoPoints = mutableMapOf<Point, Double>()
    private var points = mutableListOf<Point>()
    private val lines = mutableListOf<Line>()
    public var resultPoint: GeoPoint? = null

    fun rememberPoint(startPoint: GeoPoint, asim: Double){
        var x = doubleArrayOf(0.0)
        var y = doubleArrayOf(0.0)
        NGeoCalc().wgs84ToPlane(x, y,  doubleArrayOf(0.0),
            NGeoCalc.degreesToRadians(startPoint.latitude),
            NGeoCalc.degreesToRadians(startPoint.longitude), 0.0)

        val p = Point(x[0], y[0])
        geoPoints[p] = asim
        points.add(p)
    }

    fun reset(){
        geoPoints.clear()
        points.clear()
    }

    override fun run() {
        resultPoint = getAveragePoint()
    }

    fun getAveragePoint(): GeoPoint?{
        points.forEach {
            val flatTelemetry = FlatTelemetry(it.x, it.y, geoPoints[it]!!)
            lines.add(Line(flatTelemetry))
        }
        var intersections = findAllIntersections()
        while (intersections.size > 2) {
            val midPoint = middlePoint(intersections)
            val distances = distancesFromMiddlePoint(intersections)
            val averageDistance = distances.average()
            intersections = intersections.filter { point -> distance(midPoint, point) <= averageDistance }
                .toMutableList()
        }
        val midPoint = middlePoint(intersections)
        var x = doubleArrayOf(0.0)
        var y = doubleArrayOf(0.0)
        NGeoCalc().planeToWgs84(x, y, doubleArrayOf(0.0), midPoint.x, midPoint.y,
            0.0)
        if (x[0].isNaN() || y[0].isNaN())
            return null
        return GeoPoint(Math.toDegrees(x[0]), Math.toDegrees(y[0]))
    }

    private fun findAllIntersections(): List<Point> {
        val intersections = mutableListOf<Point>()
        for (i in 0 until lines.size) {
            for (j in i + 1 until lines.size) {
                val intersection = intersect(lines[i], lines[j])
                if (intersection != null) {
                    intersections.add(intersection)
                }
            }
        }
        return intersections
    }

    private fun intersect(l1: Line, l2: Line): Point? {
        val (x1, y1) = Pair(l1.p1.x, l1.p1.y)
        val (x2, y2) = Pair(l1.p2.x, l1.p2.y)
        val (x3, y3) = Pair(l2.p1.x, l2.p1.y)
        val (x4, y4) = Pair(l2.p2.x, l2.p2.y)
        val denom = (y4 - y3) * (x2 - x1) - (x4 - x3) * (y2 - y1)
        if (abs(denom) < 0.01) return null

        val ua = ((x4 - x3) * (y1 - y3) - (y4 - y3) * (x1 - x3)) / denom
        val ub = ((x2 - x1) * (y1 - y3) - (y2 - y1) * (x1 - x3)) / denom

        //if (ua < 0 || ua > 1 || ub < 0 || ub > 1) return null

        return Point(x1 + ua * (x2 - x1), y1 + ua * (y2 - y1))
    }

    private fun distance(p1: Point, p2: Point): Double {
        val dx = p1.x - p2.x
        val dy = p1.y - p2.y
        return sqrt(dx * dx + dy * dy)
    }

    private fun distancesFromMiddlePoint(points: List<Point>): List<Double> {
        val middle = middlePoint(points)
        return points.map { point -> distance(middle, point) }
    }

    private fun middlePoint(points: List<Point>): Point {
        val x = points.map { it.x }.average()
        val y = points.map { it.y }.average()
        return Point(x, y)
    }
}