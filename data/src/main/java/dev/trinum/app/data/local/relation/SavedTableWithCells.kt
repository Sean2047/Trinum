package dev.trinum.app.data.local.relation

import androidx.room.Embedded
import androidx.room.Relation
import dev.trinum.app.data.local.entity.SavedTableEntity
import dev.trinum.app.data.local.entity.TableCellEntity

data class SavedTableWithCells(
    @Embedded val table: SavedTableEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "table_id"
    )
    val cells: List<TableCellEntity>
)
