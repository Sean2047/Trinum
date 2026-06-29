package dev.trinum.app.data.local.entity

import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_tables")
@Keep
data class SavedTableEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val rows: Int,
    val columns: Int,
    @ColumnInfo(name = "created_at") val createdAt: Long,
    @ColumnInfo(name = "updated_at") val updatedAt: Long
)
