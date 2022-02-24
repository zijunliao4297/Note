package idv.zijunliao.note.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import idv.zijunliao.note.data.use_case.NoteUseCase
import idv.zijunliao.note.data.room.NoteDao
import idv.zijunliao.note.data.room.NoteDatabase
import idv.zijunliao.note.data.room.NoteRepository
import idv.zijunliao.note.data.store.DataStoreRepository
import idv.zijunliao.note.dataStore
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideNoteDatabase(@ApplicationContext applicationContext: Context): NoteDatabase =
        Room.databaseBuilder(
            applicationContext,
            NoteDatabase::class.java,
            NoteDatabase::class.java.simpleName
        ).build()

    @Provides
    fun provideNoteDao(database: NoteDatabase): NoteDao = database.dao()

    @Singleton
    @Provides
    fun provideNoteRepository(dao: NoteDao): NoteRepository = NoteRepository(dao)

    @Singleton
    @Provides
    fun provideNoteUseCase(repository: NoteRepository): NoteUseCase = NoteUseCase(repository)

    @Singleton
    @Provides
    fun provideDataStoreRepository(@ApplicationContext applicationContext: Context) = DataStoreRepository(applicationContext.dataStore)
}