package com.example.petmedical.result

import android.graphics.Bitmap
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity
data class ResultEntity(
    @PrimaryKey(autoGenerate = true) val id: Int? = null,
    @ColumnInfo(name = "img") val image: Bitmap? = null,
    @ColumnInfo(name = "date") val date: LocalDateTime = LocalDateTime.now(),
    @ColumnInfo(name = "disease") val disease: String? = null
)
