package com.example.wastemanagement.di

import com.example.wastemanagement.data.EventRepository
import com.example.wastemanagement.data.EventRepositoryImpl
import com.example.wastemanagement.data.IncidentRepository
import com.example.wastemanagement.data.IncidentRepositoryImpl
import com.example.wastemanagement.data.PlantationRepository
import com.example.wastemanagement.data.PlantationRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindIncidentRepository(incidentRepositoryImpl: IncidentRepositoryImpl): IncidentRepository

    @Binds
    @Singleton
    abstract fun bindEventRepository(eventRepositoryImpl: EventRepositoryImpl): EventRepository

    @Binds
    @Singleton
    abstract fun bindPlantationRepository(plantationRepositoryImpl: PlantationRepositoryImpl): PlantationRepository

}
