package com.example.dronevision.data.repository

import com.example.dronevision.data.local.Dao
import com.example.dronevision.data.mapper.TechnicMapperDTO
import com.example.dronevision.domain.Repository
import com.example.dronevision.domain.model.TechnicDTO

class RepositoryImpl(private val dao: Dao): Repository {

    override suspend fun saveTechnic(technicDTO: TechnicDTO) {
        dao.saveTechnic(TechnicMapperDTO.mapTechnicDTOtoEntity(technicDTO))
    }

    override suspend fun getTechnics(): List<TechnicDTO> {
       return TechnicMapperDTO.mapEntitiesToTechnicsDTO(dao.getTechnics())
    }

    override suspend fun deleteTechnic(technicDTO: TechnicDTO) {
        dao.deleteTechnic(TechnicMapperDTO.mapTechnicDTOtoEntity(technicDTO))
    }

    override suspend fun deleteAll() {
        dao.deleteAll()
    }
}