package idv.zijunliao.note.data.room

import kotlinx.coroutines.flow.Flow

/**
 * A simple generic interface for repository.
 * Just define some common database CRUD functions.
 */
interface IRepository<T> {
    fun selectAll(): Flow<List<T>>

    fun query(keyword: String): Flow<List<T>>

    suspend fun selectById(id: Int): T?

    suspend fun insert(t: T)

    suspend fun delete(t: T)

    suspend fun deleteAll()
}