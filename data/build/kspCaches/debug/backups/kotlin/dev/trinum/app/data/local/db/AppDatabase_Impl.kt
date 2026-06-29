package dev.trinum.app.`data`.local.db

import androidx.room.InvalidationTracker
import androidx.room.RoomOpenDelegate
import androidx.room.migration.AutoMigrationSpec
import androidx.room.migration.Migration
import androidx.room.util.TableInfo
import androidx.room.util.TableInfo.Companion.read
import androidx.room.util.dropFtsSyncTriggers
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL
import dev.trinum.app.`data`.local.dao.HistoryEntryDao
import dev.trinum.app.`data`.local.dao.HistoryEntryDao_Impl
import dev.trinum.app.`data`.local.dao.SavedTableDao
import dev.trinum.app.`data`.local.dao.SavedTableDao_Impl
import javax.`annotation`.processing.Generated
import kotlin.Lazy
import kotlin.String
import kotlin.Suppress
import kotlin.collections.List
import kotlin.collections.Map
import kotlin.collections.MutableList
import kotlin.collections.MutableMap
import kotlin.collections.MutableSet
import kotlin.collections.Set
import kotlin.collections.mutableListOf
import kotlin.collections.mutableMapOf
import kotlin.collections.mutableSetOf
import kotlin.reflect.KClass

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class AppDatabase_Impl : AppDatabase() {
  private val _historyEntryDao: Lazy<HistoryEntryDao> = lazy {
    HistoryEntryDao_Impl(this)
  }

  private val _savedTableDao: Lazy<SavedTableDao> = lazy {
    SavedTableDao_Impl(this)
  }

  protected override fun createOpenDelegate(): RoomOpenDelegate {
    val _openDelegate: RoomOpenDelegate = object : RoomOpenDelegate(1,
        "6de51c37cff156e844023e3ae1e74686", "6c64ae3f4df7a12d959750ce6001bc31") {
      public override fun createAllTables(connection: SQLiteConnection) {
        connection.execSQL("CREATE TABLE IF NOT EXISTS `history_entries` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `expression` TEXT NOT NULL, `result` TEXT NOT NULL, `timestamp` INTEGER NOT NULL)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `saved_tables` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `rows` INTEGER NOT NULL, `columns` INTEGER NOT NULL, `created_at` INTEGER NOT NULL, `updated_at` INTEGER NOT NULL)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `table_cells` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `table_id` INTEGER NOT NULL, `row` INTEGER NOT NULL, `column` INTEGER NOT NULL, `content` TEXT NOT NULL, `is_formula` INTEGER NOT NULL, FOREIGN KEY(`table_id`) REFERENCES `saved_tables`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_table_cells_table_id` ON `table_cells` (`table_id`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)")
        connection.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '6de51c37cff156e844023e3ae1e74686')")
      }

      public override fun dropAllTables(connection: SQLiteConnection) {
        connection.execSQL("DROP TABLE IF EXISTS `history_entries`")
        connection.execSQL("DROP TABLE IF EXISTS `saved_tables`")
        connection.execSQL("DROP TABLE IF EXISTS `table_cells`")
      }

      public override fun onCreate(connection: SQLiteConnection) {
      }

      public override fun onOpen(connection: SQLiteConnection) {
        connection.execSQL("PRAGMA foreign_keys = ON")
        internalInitInvalidationTracker(connection)
      }

      public override fun onPreMigrate(connection: SQLiteConnection) {
        dropFtsSyncTriggers(connection)
      }

      public override fun onPostMigrate(connection: SQLiteConnection) {
      }

      public override fun onValidateSchema(connection: SQLiteConnection):
          RoomOpenDelegate.ValidationResult {
        val _columnsHistoryEntries: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsHistoryEntries.put("id", TableInfo.Column("id", "INTEGER", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsHistoryEntries.put("expression", TableInfo.Column("expression", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsHistoryEntries.put("result", TableInfo.Column("result", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsHistoryEntries.put("timestamp", TableInfo.Column("timestamp", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysHistoryEntries: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesHistoryEntries: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoHistoryEntries: TableInfo = TableInfo("history_entries", _columnsHistoryEntries,
            _foreignKeysHistoryEntries, _indicesHistoryEntries)
        val _existingHistoryEntries: TableInfo = read(connection, "history_entries")
        if (!_infoHistoryEntries.equals(_existingHistoryEntries)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |history_entries(dev.trinum.app.data.local.entity.HistoryEntryEntity).
              | Expected:
              |""".trimMargin() + _infoHistoryEntries + """
              |
              | Found:
              |""".trimMargin() + _existingHistoryEntries)
        }
        val _columnsSavedTables: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsSavedTables.put("id", TableInfo.Column("id", "INTEGER", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsSavedTables.put("name", TableInfo.Column("name", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsSavedTables.put("rows", TableInfo.Column("rows", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsSavedTables.put("columns", TableInfo.Column("columns", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsSavedTables.put("created_at", TableInfo.Column("created_at", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsSavedTables.put("updated_at", TableInfo.Column("updated_at", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysSavedTables: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesSavedTables: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoSavedTables: TableInfo = TableInfo("saved_tables", _columnsSavedTables,
            _foreignKeysSavedTables, _indicesSavedTables)
        val _existingSavedTables: TableInfo = read(connection, "saved_tables")
        if (!_infoSavedTables.equals(_existingSavedTables)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |saved_tables(dev.trinum.app.data.local.entity.SavedTableEntity).
              | Expected:
              |""".trimMargin() + _infoSavedTables + """
              |
              | Found:
              |""".trimMargin() + _existingSavedTables)
        }
        val _columnsTableCells: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsTableCells.put("id", TableInfo.Column("id", "INTEGER", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsTableCells.put("table_id", TableInfo.Column("table_id", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsTableCells.put("row", TableInfo.Column("row", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsTableCells.put("column", TableInfo.Column("column", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsTableCells.put("content", TableInfo.Column("content", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsTableCells.put("is_formula", TableInfo.Column("is_formula", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysTableCells: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        _foreignKeysTableCells.add(TableInfo.ForeignKey("saved_tables", "CASCADE", "NO ACTION",
            listOf("table_id"), listOf("id")))
        val _indicesTableCells: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesTableCells.add(TableInfo.Index("index_table_cells_table_id", false,
            listOf("table_id"), listOf("ASC")))
        val _infoTableCells: TableInfo = TableInfo("table_cells", _columnsTableCells,
            _foreignKeysTableCells, _indicesTableCells)
        val _existingTableCells: TableInfo = read(connection, "table_cells")
        if (!_infoTableCells.equals(_existingTableCells)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |table_cells(dev.trinum.app.data.local.entity.TableCellEntity).
              | Expected:
              |""".trimMargin() + _infoTableCells + """
              |
              | Found:
              |""".trimMargin() + _existingTableCells)
        }
        return RoomOpenDelegate.ValidationResult(true, null)
      }
    }
    return _openDelegate
  }

  protected override fun createInvalidationTracker(): InvalidationTracker {
    val _shadowTablesMap: MutableMap<String, String> = mutableMapOf()
    val _viewTables: MutableMap<String, Set<String>> = mutableMapOf()
    return InvalidationTracker(this, _shadowTablesMap, _viewTables, "history_entries",
        "saved_tables", "table_cells")
  }

  public override fun clearAllTables() {
    super.performClear(true, "history_entries", "saved_tables", "table_cells")
  }

  protected override fun getRequiredTypeConverterClasses(): Map<KClass<*>, List<KClass<*>>> {
    val _typeConvertersMap: MutableMap<KClass<*>, List<KClass<*>>> = mutableMapOf()
    _typeConvertersMap.put(HistoryEntryDao::class, HistoryEntryDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(SavedTableDao::class, SavedTableDao_Impl.getRequiredConverters())
    return _typeConvertersMap
  }

  public override fun getRequiredAutoMigrationSpecClasses(): Set<KClass<out AutoMigrationSpec>> {
    val _autoMigrationSpecsSet: MutableSet<KClass<out AutoMigrationSpec>> = mutableSetOf()
    return _autoMigrationSpecsSet
  }

  public override
      fun createAutoMigrations(autoMigrationSpecs: Map<KClass<out AutoMigrationSpec>, AutoMigrationSpec>):
      List<Migration> {
    val _autoMigrations: MutableList<Migration> = mutableListOf()
    return _autoMigrations
  }

  public override fun historyEntryDao(): HistoryEntryDao = _historyEntryDao.value

  public override fun savedTableDao(): SavedTableDao = _savedTableDao.value
}
