package dev.trinum.app.data.di;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import dev.trinum.app.data.local.dao.SavedTableDao;
import dev.trinum.app.data.local.db.AppDatabase;
import javax.annotation.processing.Generated;

@ScopeMetadata
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast",
    "deprecation",
    "nullness:initialization.field.uninitialized"
})
public final class DatabaseModule_Companion_ProvideSavedTableDaoFactory implements Factory<SavedTableDao> {
  private final Provider<AppDatabase> dbProvider;

  private DatabaseModule_Companion_ProvideSavedTableDaoFactory(Provider<AppDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public SavedTableDao get() {
    return provideSavedTableDao(dbProvider.get());
  }

  public static DatabaseModule_Companion_ProvideSavedTableDaoFactory create(
      Provider<AppDatabase> dbProvider) {
    return new DatabaseModule_Companion_ProvideSavedTableDaoFactory(dbProvider);
  }

  public static SavedTableDao provideSavedTableDao(AppDatabase db) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.Companion.provideSavedTableDao(db));
  }
}
