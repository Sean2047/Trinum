package dev.trinum.app.data.repository;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import dev.trinum.app.data.local.dao.SavedTableDao;
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
public final class TableRepositoryImpl_Factory implements Factory<TableRepositoryImpl> {
  private final Provider<SavedTableDao> daoProvider;

  private TableRepositoryImpl_Factory(Provider<SavedTableDao> daoProvider) {
    this.daoProvider = daoProvider;
  }

  @Override
  public TableRepositoryImpl get() {
    return newInstance(daoProvider.get());
  }

  public static TableRepositoryImpl_Factory create(Provider<SavedTableDao> daoProvider) {
    return new TableRepositoryImpl_Factory(daoProvider);
  }

  public static TableRepositoryImpl newInstance(SavedTableDao dao) {
    return new TableRepositoryImpl(dao);
  }
}
