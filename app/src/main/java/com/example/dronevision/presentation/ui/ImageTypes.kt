package com.example.dronevision.presentation.ui

import com.example.dronevision.R
import com.example.dronevision.domain.model.TechnicTypes

object ImageTypes {
    val imageMap = mapOf<TechnicTypes, Int>(
        Pair(TechnicTypes.LAUNCHER, R.drawable.ic_01),
        Pair(TechnicTypes.OVERLAND, R.drawable.ic_04),
        Pair(TechnicTypes.ARTILLERY, R.drawable.ic_08),
        Pair(TechnicTypes.REACT, R.drawable.ic_10),
        Pair(TechnicTypes.MINES, R.drawable.ic_12),
        Pair(TechnicTypes.ZUR, R.drawable.ic_14),
        Pair(TechnicTypes.RLS, R.drawable.ic_17),
        Pair(TechnicTypes.INFANTRY, R.drawable.ic_19),
        Pair(TechnicTypes.O_POINT, R.drawable.ic_20),
        Pair(TechnicTypes.KNP, R.drawable.ic_21),
        Pair(TechnicTypes.TANKS, R.drawable.ic_22),
        Pair(TechnicTypes.BTR, R.drawable.ic_23),
        Pair(TechnicTypes.BMP, R.drawable.ic_24),
        Pair(TechnicTypes.HELICOPTER, R.drawable.ic_25),
        Pair(TechnicTypes.PTRK, R.drawable.ic_27),
        Pair(TechnicTypes.KLN_PESH, R.drawable.ic_29),
        Pair(TechnicTypes.KLN_BR, R.drawable.ic_30),
        Pair(TechnicTypes.TANK, R.drawable.ic_31),
        Pair(TechnicTypes.ANOTHER, R.drawable.ic_99),
        Pair(TechnicTypes.GAP, R.drawable.ic_breach)
    )
}