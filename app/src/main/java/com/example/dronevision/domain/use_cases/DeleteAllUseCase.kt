package com.example.dronevision.domain.use_cases

import com.example.dronevision.domain.Repository

class DeleteAllUseCase(private val repository: Repository) {

    suspend fun execute(){
        repository.deleteAll()
    }
}