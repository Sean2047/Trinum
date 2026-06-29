package dev.trinum.app.data.repository;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import dev.trinum.app.data.local.dao.HistoryEntryDao;
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
public final class HistoryRepositoryImpl_Factory implements Factory<HistoryRepositoryImpl> {
  private final Provider<HistoryEntryDao> daoProvider;

  private HistoryRepositoryImpl_Factory(Provider<HistoryEntryDao> daoProvider) {
    this.daoProvider = daoProvider;
  }

  @Override
  public HistoryRepositoryImpl get() {
    return newInstance(daoProvider.get());
  }

  public static HistoryRepositoryImpl_Factory create(Provider<HistoryEntryDao> daoProvider) {
    return new HistoryRepositoryImpl_Factory(daoProvider);
  }

  public static HistoryRepositoryImpl newInstance(HistoryEntryDao dao) {
    return new HistoryRepositoryImpl(dao);
  }
}
