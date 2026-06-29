package dev.trinum.app.data.local.entity

import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "table_cells",
    foreignKeys = [
        ForeignKey(
            entity = SavedTableEntity::class,
            parentColumns = ["id"],
            childColumns = ["table_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("table_id")]
)
@Keep
data class TableCellEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "table_id") val tableId: Long,
    val row: Int,
    val column: Int,
    val content: String,
    @ColumnInfo(name = "is_formula") val isFormula: Boolean
)
