package dev.trinum.app.data.di;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import dev.trinum.app.data.local.dao.HistoryEntryDao;
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
public final class DatabaseModule_Companion_ProvideHistoryEntryDaoFactory implements Factory<HistoryEntryDao> {
  private final Provider<AppDatabase> dbProvider;

  private DatabaseModule_Companion_ProvideHistoryEntryDaoFactory(Provider<AppDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public HistoryEntryDao get() {
    return provideHistoryEntryDao(dbProvider.get());
  }

  public static DatabaseModule_Companion_ProvideHistoryEntryDaoFactory create(
      Provider<AppDatabase> dbProvider) {
    return new DatabaseModule_Companion_ProvideHistoryEntryDaoFactory(dbProvider);
  }

  public static HistoryEntryDao provideHistoryEntryDao(AppDatabase db) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.Companion.provideHistoryEntryDao(db));
  }
}
