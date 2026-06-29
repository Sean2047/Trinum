package dev.trinum.app.`data`.local.dao

import androidx.collection.LongSparseArray
import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.appendPlaceholders
import androidx.room.util.getColumnIndex
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.room.util.recursiveFetchLongSparseArray
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.SQLiteStatement
import dev.trinum.app.`data`.local.entity.SavedTableEntity
import dev.trinum.app.`data`.local.entity.TableCellEntity
import dev.trinum.app.`data`.local.relation.SavedTableWithCells
import javax.`annotation`.processing.Generated
import kotlin.Boolean
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.Suppress
import kotlin.Unit
import kotlin.collections.List
import kotlin.collections.MutableList
import kotlin.collections.mutableListOf
import kotlin.reflect.KClass
import kotlin.text.StringBuilder
import kotlinx.coroutines.flow.Flow

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class SavedTableDao_Impl(
  __db: RoomDatabase,
) : SavedTableDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfSavedTableEntity: EntityInsertAdapter<SavedTableEntity>

  private val __insertAdapterOfTableCellEntity: EntityInsertAdapter<TableCellEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfSavedTableEntity = object : EntityInsertAdapter<SavedTableEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `saved_tables` (`id`,`name`,`rows`,`columns`,`created_at`,`updated_at`) VALUES (nullif(?, 0),?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: SavedTableEntity) {
        statement.bindLong(1, entity.id)
        statement.bindText(2, entity.name)
        statement.bindLong(3, entity.rows.toLong())
        statement.bindLong(4, entity.columns.toLong())
        statement.bindLong(5, entity.createdAt)
        statement.bindLong(6, entity.updatedAt)
      }
    }
    this.__insertAdapterOfTableCellEntity = object : EntityInsertAdapter<TableCellEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `table_cells` (`id`,`table_id`,`row`,`column`,`content`,`is_formula`) VALUES (nullif(?, 0),?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: TableCellEntity) {
        statement.bindLong(1, entity.id)
        statement.bindLong(2, entity.tableId)
        statement.bindLong(3, entity.row.toLong())
        statement.bindLong(4, entity.column.toLong())
        statement.bindText(5, entity.content)
        val _tmp: Int = if (entity.isFormula) 1 else 0
        statement.bindLong(6, _tmp.toLong())
      }
    }
  }

  public override suspend fun insertTable(table: SavedTableEntity): Long = performSuspending(__db,
      false, true) { _connection ->
    val _result: Long = __insertAdapterOfSavedTableEntity.insertAndReturnId(_connection, table)
    _result
  }

  public override suspend fun insertCells(cells: List<TableCellEntity>): Unit =
      performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfTableCellEntity.insert(_connection, cells)
  }

  public override fun observeAll(): Flow<List<SavedTableEntity>> {
    val _sql: String = "SELECT * FROM saved_tables ORDER BY updated_at DESC"
    return createFlow(__db, false, arrayOf("saved_tables")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfRows: Int = getColumnIndexOrThrow(_stmt, "rows")
        val _columnIndexOfColumns: Int = getColumnIndexOrThrow(_stmt, "columns")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "created_at")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updated_at")
        val _result: MutableList<SavedTableEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: SavedTableEntity
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpRows: Int
          _tmpRows = _stmt.getLong(_columnIndexOfRows).toInt()
          val _tmpColumns: Int
          _tmpColumns = _stmt.getLong(_columnIndexOfColumns).toInt()
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          _item = SavedTableEntity(_tmpId,_tmpName,_tmpRows,_tmpColumns,_tmpCreatedAt,_tmpUpdatedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getWithCells(tableId: Long): SavedTableWithCells? {
    val _sql: String = "SELECT * FROM saved_tables WHERE id = ?"
    return performSuspending(__db, true, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, tableId)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfRows: Int = getColumnIndexOrThrow(_stmt, "rows")
        val _columnIndexOfColumns: Int = getColumnIndexOrThrow(_stmt, "columns")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "created_at")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updated_at")
        val _collectionCells: LongSparseArray<MutableList<TableCellEntity>> =
            LongSparseArray<MutableList<TableCellEntity>>()
        while (_stmt.step()) {
          val _tmpKey: Long
          _tmpKey = _stmt.getLong(_columnIndexOfId)
          if (!_collectionCells.containsKey(_tmpKey)) {
            _collectionCells.put(_tmpKey, mutableListOf())
          }
        }
        _stmt.reset()
        __fetchRelationshiptableCellsAsdevTrinumAppDataLocalEntityTableCellEntity(_connection,
            _collectionCells)
        val _result: SavedTableWithCells?
        if (_stmt.step()) {
          val _tmpTable: SavedTableEntity
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpRows: Int
          _tmpRows = _stmt.getLong(_columnIndexOfRows).toInt()
          val _tmpColumns: Int
          _tmpColumns = _stmt.getLong(_columnIndexOfColumns).toInt()
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          _tmpTable =
              SavedTableEntity(_tmpId,_tmpName,_tmpRows,_tmpColumns,_tmpCreatedAt,_tmpUpdatedAt)
          val _tmpCellsCollection: MutableList<TableCellEntity>
          val _tmpKey_1: Long
          _tmpKey_1 = _stmt.getLong(_columnIndexOfId)
          _tmpCellsCollection = checkNotNull(_collectionCells.get(_tmpKey_1))
          _result = SavedTableWithCells(_tmpTable,_tmpCellsCollection)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun deleteCellsForTable(tableId: Long) {
    val _sql: String = "DELETE FROM table_cells WHERE table_id = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, tableId)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun deleteTable(tableId: Long) {
    val _sql: String = "DELETE FROM saved_tables WHERE id = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, tableId)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  private
      fun __fetchRelationshiptableCellsAsdevTrinumAppDataLocalEntityTableCellEntity(_connection: SQLiteConnection,
      _map: LongSparseArray<MutableList<TableCellEntity>>) {
    if (_map.isEmpty()) {
      return
    }
    if (_map.size() > 999) {
      recursiveFetchLongSparseArray(_map, true) { _tmpMap ->
        __fetchRelationshiptableCellsAsdevTrinumAppDataLocalEntityTableCellEntity(_connection,
            _tmpMap)
      }
      return
    }
    val _stringBuilder: StringBuilder = StringBuilder()
    _stringBuilder.append("SELECT `id`,`table_id`,`row`,`column`,`content`,`is_formula` FROM `table_cells` WHERE `table_id` IN (")
    val _inputSize: Int = _map.size()
    appendPlaceholders(_stringBuilder, _inputSize)
    _stringBuilder.append(")")
    val _sql: String = _stringBuilder.toString()
    val _stmt: SQLiteStatement = _connection.prepare(_sql)
    var _argIndex: Int = 1
    for (i in 0 until _map.size()) {
      val _item: Long = _map.keyAt(i)
      _stmt.bindLong(_argIndex, _item)
      _argIndex++
    }
    try {
      val _itemKeyIndex: Int = getColumnIndex(_stmt, "table_id")
      if (_itemKeyIndex == -1) {
        return
      }
      val _columnIndexOfId: Int = 0
      val _columnIndexOfTableId: Int = 1
      val _columnIndexOfRow: Int = 2
      val _columnIndexOfColumn: Int = 3
      val _columnIndexOfContent: Int = 4
      val _columnIndexOfIsFormula: Int = 5
      while (_stmt.step()) {
        val _tmpKey: Long
        _tmpKey = _stmt.getLong(_itemKeyIndex)
        val _tmpRelation: MutableList<TableCellEntity>? = _map.get(_tmpKey)
        if (_tmpRelation != null) {
          val _item_1: TableCellEntity
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpTableId: Long
          _tmpTableId = _stmt.getLong(_columnIndexOfTableId)
          val _tmpRow: Int
          _tmpRow = _stmt.getLong(_columnIndexOfRow).toInt()
          val _tmpColumn: Int
          _tmpColumn = _stmt.getLong(_columnIndexOfColumn).toInt()
          val _tmpContent: String
          _tmpContent = _stmt.getText(_columnIndexOfContent)
          val _tmpIsFormula: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsFormula).toInt()
          _tmpIsFormula = _tmp != 0
          _item_1 = TableCellEntity(_tmpId,_tmpTableId,_tmpRow,_tmpColumn,_tmpContent,_tmpIsFormula)
          _tmpRelation.add(_item_1)
        }
      }
    } finally {
      _stmt.close()
    }
  }

  public companion object {
    public fun getRequiredConverters(): List<KClass<*>> = emptyList()
  }
}
