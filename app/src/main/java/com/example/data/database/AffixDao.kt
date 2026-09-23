package com.example.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.AffixEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AffixDao {
    @Query("SELECT * FROM affixes ORDER BY affix ASC")
    fun getAllAffixes(): Flow<List<AffixEntity>>

    @Query("SELECT * FROM affixes WHERE type = :type ORDER BY affix ASC")
    fun getAffixesByType(type: String): Flow<List<AffixEntity>>

    @Query("SELECT * FROM affixes WHERE type = :type AND (affix LIKE '%' || :query || '%' OR meaning LIKE '%' || :query || '%' OR examples LIKE '%' || :query || '%') ORDER BY affix ASC")
    fun searchAffixes(query: String, type: String): Flow<List<AffixEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(affix: AffixEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(affixes: List<AffixEntity>)

    @Update
    suspend fun update(affix: AffixEntity)

    @Delete
    suspend fun delete(affix: AffixEntity)

    @Query("DELETE FROM affixes WHERE type = :type")
    suspend fun deleteByType(type: String)

    @Query("DELETE FROM affixes")
    suspend fun deleteAll()

    @Query("SELECT COUNT(*) FROM affixes")
    suspend fun getCount(): Int
}
