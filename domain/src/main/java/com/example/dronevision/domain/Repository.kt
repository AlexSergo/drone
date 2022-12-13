package com.example.dronevision.domain

import com.example.dronevision.domain.model.TechnicDTO

interface Repository {
    suspend fun saveTechnic(technicDTO: TechnicDTO)

    suspend fun getTechnics(): List<TechnicDTO>

    suspend fun deleteTechnic(technicDTO: TechnicDTO)

    suspend fun  deleteAll()
}