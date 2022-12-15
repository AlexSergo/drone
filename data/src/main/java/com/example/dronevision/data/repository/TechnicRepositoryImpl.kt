package com.example.dronevision.data.repository

import com.example.dronevision.data.mapper.TechnicMapperDTO
import com.example.dronevision.data.source.LocalDataSource
import com.example.dronevision.domain.model.TechnicDTO
import com.example.dronevision.domain.repository.TechnicRepository

class TechnicRepositoryImpl(private val localDataSource: LocalDataSource) : TechnicRepository {
    
    override suspend fun saveTechnic(technicDTO: TechnicDTO) {
        localDataSource.saveTechnic(TechnicMapperDTO.mapTechnicDTOtoEntity(technicDTO))
    }
    
    override suspend fun getTechnics(): List<TechnicDTO> {
        return TechnicMapperDTO.mapEntitiesToTechnicsDTO(localDataSource.getTechnics())
    }
    
    override suspend fun deleteTechnic(technicDTO: TechnicDTO) {
        localDataSource.deleteTechnic(TechnicMapperDTO.mapTechnicDTOtoEntity(technicDTO))
    }

    override suspend fun deleteAll() {
        localDataSource.deleteAll()
    }
}