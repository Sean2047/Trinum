package dev.trinum.app.data.di

import android.content.Context
import androidx.room.Room
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.trinum.app.data.local.dao.HistoryEntryDao
import dev.trinum.app.data.local.dao.SavedTableDao
import dev.trinum.app.data.local.db.AppDatabase
import dev.trinum.app.data.repository.HistoryRepositoryImpl
import dev.trinum.app.data.repository.TableRepositoryImpl
import dev.trinum.app.domain.repository.HistoryRepository
import dev.trinum.app.domain.repository.TableRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DatabaseModule {

    @Binds
    abstract fun bindHistoryRepository(impl: HistoryRepositoryImpl): HistoryRepository

    @Binds
    abstract fun bindTableRepository(impl: TableRepositoryImpl): TableRepository

    companion object {

        @Provides
        @Singleton
        fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
            Room.databaseBuilder(context, AppDatabase::class.java, "trinum.db").build()

        @Provides
        fun provideHistoryEntryDao(db: AppDatabase): HistoryEntryDao = db.historyEntryDao()

        @Provides
        fun provideSavedTableDao(db: AppDatabase): SavedTableDao = db.savedTableDao()
    }
}
