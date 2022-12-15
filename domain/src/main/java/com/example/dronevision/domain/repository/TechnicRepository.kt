package com.example.dronevision.domain.repository

import com.example.dronevision.domain.model.TechnicDTO

interface TechnicRepository {
    suspend fun saveTechnic(technicDTO: TechnicDTO)

    suspend fun getTechnics(): List<TechnicDTO>

    suspend fun deleteTechnic(technicDTO: TechnicDTO)

    suspend fun  deleteAll()
}