package com.example.petmedical.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ImageDao {
    @Insert
    suspend fun insertImage(image: com.example.petmedical.data.Image)

    @Query("SELECT * FROM images")
    suspend fun getAllImages(): List<com.example.petmedical.data.Image>

    @Query("DELETE FROM images")
    suspend fun deleteAllImages()
}
