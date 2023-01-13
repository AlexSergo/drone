package com.example.dronevision.utils

enum class MapType(val value: Int) {
    OSM(1),
    SCHEME_MAP(2),
    GOOGLE_SAT(3),
    GOOGLE_HYB(4),
    OFFLINE(5)
}