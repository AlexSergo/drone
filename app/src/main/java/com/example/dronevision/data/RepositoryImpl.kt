package com.example.dronevision.data

import com.example.dronevision.data.local.Dao
import com.example.dronevision.domain.model.TechnicEntity

class RepositoryImpl(private val dao: Dao): com.example.dronevision.domain.Repository {

    override suspend fun saveTechnic(technic: TechnicEntity) {
        dao.saveTechnic(technic)
    }

    override suspend fun getTechnics(): List<TechnicEntity> {
       return dao.getTechnics()
    }

    override suspend fun deleteTechnic(technic: TechnicEntity) {
        dao.deleteTechnic(technic)
    }

    override suspend fun deleteAll() {
        dao.deleteAll()
    }
}