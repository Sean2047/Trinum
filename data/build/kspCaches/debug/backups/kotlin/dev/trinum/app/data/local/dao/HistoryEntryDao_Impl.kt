package dev.trinum.app.`data`.local.dao

import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import dev.trinum.app.`data`.local.entity.HistoryEntryEntity
import javax.`annotation`.processing.Generated
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.Suppress
import kotlin.Unit
import kotlin.collections.List
import kotlin.collections.MutableList
import kotlin.collections.mutableListOf
import kotlin.reflect.KClass
import kotlinx.coroutines.flow.Flow

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class HistoryEntryDao_Impl(
  __db: RoomDatabase,
) : HistoryEntryDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfHistoryEntryEntity: EntityInsertAdapter<HistoryEntryEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfHistoryEntryEntity = object : EntityInsertAdapter<HistoryEntryEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `history_entries` (`id`,`expression`,`result`,`timestamp`) VALUES (nullif(?, 0),?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: HistoryEntryEntity) {
        statement.bindLong(1, entity.id)
        statement.bindText(2, entity.expression)
        statement.bindText(3, entity.result)
        statement.bindLong(4, entity.timestamp)
      }
    }
  }

  public override suspend fun insert(entry: HistoryEntryEntity): Unit = performSuspending(__db,
      false, true) { _connection ->
    __insertAdapterOfHistoryEntryEntity.insert(_connection, entry)
  }

  public override fun observeRecent(): Flow<List<HistoryEntryEntity>> {
    val _sql: String = "SELECT * FROM history_entries ORDER BY timestamp DESC LIMIT 50"
    return createFlow(__db, false, arrayOf("history_entries")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfExpression: Int = getColumnIndexOrThrow(_stmt, "expression")
        val _columnIndexOfResult: Int = getColumnIndexOrThrow(_stmt, "result")
        val _columnIndexOfTimestamp: Int = getColumnIndexOrThrow(_stmt, "timestamp")
        val _result: MutableList<HistoryEntryEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: HistoryEntryEntity
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpExpression: String
          _tmpExpression = _stmt.getText(_columnIndexOfExpression)
          val _tmpResult: String
          _tmpResult = _stmt.getText(_columnIndexOfResult)
          val _tmpTimestamp: Long
          _tmpTimestamp = _stmt.getLong(_columnIndexOfTimestamp)
          _item = HistoryEntryEntity(_tmpId,_tmpExpression,_tmpResult,_tmpTimestamp)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun deleteById(id: Long) {
    val _sql: String = "DELETE FROM history_entries WHERE id = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, id)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun clearAll() {
    val _sql: String = "DELETE FROM history_entries"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public companion object {
    public fun getRequiredConverters(): List<KClass<*>> = emptyList()
  }
}
