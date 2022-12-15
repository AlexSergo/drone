package com.example.dronevision.data.source

import com.example.dronevision.data.source.local.TechnicDao
import com.example.dronevision.data.source.local.model.TechnicEntity

class LocalDataSource(private val technicDao: TechnicDao) {
    
    suspend fun saveTechnic(technicEntity: TechnicEntity) {
        technicDao.saveTechnic(technicEntity)
    }
    
    suspend fun getTechnics(): List<TechnicEntity> {
        return technicDao.getTechnics()
    }
    
    suspend fun deleteTechnic(technicEntity: TechnicEntity) {
        technicDao.deleteTechnic(technicEntity)
    }
    
    suspend fun deleteAll() {
        technicDao.deleteAll()
    }
}