package com.example.petmedical.result

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ResultDAO {
    @Query("SELECT * FROM resultEntity")
    fun getAll(): List<ResultEntity>

    @Insert
    fun insertResult(resultEntity: ResultEntity)

    @Query("DELETE FROM resultEntity")
    fun deleteAll()
}