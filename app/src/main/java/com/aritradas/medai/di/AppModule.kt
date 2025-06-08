package com.aritradas.medai.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.aritradas.medai.data.datastore.DataStoreUtil
import com.aritradas.medai.data.repository.PrescriptionRepositoryImpl
import com.aritradas.medai.domain.repository.PrescriptionRepository
import com.aritradas.medai.utils.AppBioMetricManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun providePrescriptionRepository(
        prescriptionRepositoryImpl: PrescriptionRepositoryImpl
    ): PrescriptionRepository = prescriptionRepositoryImpl

    @Provides
    fun provideDataStoreUtil(@ApplicationContext context: Context): DataStoreUtil =
        DataStoreUtil(context)

    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext appContext: Context): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create(
            produceFile = { appContext.preferencesDataStoreFile("user_preferences") }
        )
    }

    @Provides
    fun provideAppBioMetricManager(context: Context): AppBioMetricManager {
        return AppBioMetricManager(context)
    }
}
