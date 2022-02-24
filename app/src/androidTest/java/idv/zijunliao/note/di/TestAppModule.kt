package idv.zijunliao.note.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import idv.zijunliao.note.data.room.NoteDao
import idv.zijunliao.note.data.room.NoteDatabase
import idv.zijunliao.note.data.room.NoteRepository
import idv.zijunliao.note.data.store.DataStoreRepository
import idv.zijunliao.note.data.use_case.NoteUseCase
import idv.zijunliao.note.dataStore
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class TestAppModule {

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext applicationContext: Context): NoteDatabase =
        Room.inMemoryDatabaseBuilder(
            applicationContext,
            NoteDatabase::class.java
        ).build()

    @Provides
    fun provideDao(database: NoteDatabase): NoteDao = database.dao()

    @Singleton
    @Provides
    fun provideRepository(dao: NoteDao): NoteRepository = NoteRepository(dao)

    @Singleton
    @Provides
    fun provideNoteUseCase(repository: NoteRepository): NoteUseCase = NoteUseCase(repository)

    @Singleton
    @Provides
    fun provideDataStoreRepository(@ApplicationContext applicationContext: Context) = DataStoreRepository(applicationContext.dataStore)
}