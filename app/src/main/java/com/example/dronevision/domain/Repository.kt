package com.example.dronevision.domain

import com.example.dronevision.domain.model.TechnicEntity

interface Repository {
    suspend fun saveTechnic(technic: TechnicEntity)

    suspend fun getTechnics(): List<TechnicEntity>

    suspend fun deleteTechnic(technic: TechnicEntity)

    suspend fun  deleteAll()
}