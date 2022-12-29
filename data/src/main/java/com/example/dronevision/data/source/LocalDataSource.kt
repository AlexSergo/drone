package com.example.dronevision.data.source

import com.example.dronevision.data.source.local.dao.SessionStateDao
import com.example.dronevision.data.source.local.dao.TechnicDao
import com.example.dronevision.data.source.local.model.SessionStateEntity
import com.example.dronevision.data.source.local.model.TechnicEntity

class LocalDataSource(
    private val technicDao: TechnicDao,
    private val sessionStateDao: SessionStateDao
) {
    
    suspend fun saveTechnic(technicEntity: TechnicEntity) {
        technicDao.saveTechnic(technicEntity)
    }
    
    suspend fun getTechnics(): List<TechnicEntity> = technicDao.getTechnics()
    
    
    suspend fun deleteTechnic(technicEntity: TechnicEntity) {
        technicDao.deleteTechnic(technicEntity)
    }
    
    suspend fun deleteAll() {
        technicDao.deleteAllTechnics()
    }
    
    suspend fun saveSessionState(sessionStateEntity: SessionStateEntity) {
        sessionStateDao.saveSessionState(sessionStateEntity)
    }
    
    suspend fun getSessionState(): SessionStateEntity? = sessionStateDao.getSessionState()
    
}