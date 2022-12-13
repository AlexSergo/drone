package com.example.dronevision.data.repository

import android.content.Context
import com.example.dronevision.data.local.Dao
import com.example.dronevision.data.local.Database

object RepositoryInitializer {

    private var dao: Dao? = null
    private lateinit var repository: RepositoryImpl

    fun getRepository(context: Context): RepositoryImpl {
        if (dao == null)
            dao = Database.getInstance(context)?.dao()
        dao?.let {
            repository = RepositoryImpl(it)
        }
        return repository
    }
}