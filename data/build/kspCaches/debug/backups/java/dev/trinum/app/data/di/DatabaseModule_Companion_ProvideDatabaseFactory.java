package dev.trinum.app.data.di;

import android.content.Context;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import dev.trinum.app.data.local.db.AppDatabase;
import javax.annotation.processing.Generated;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
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
public final class DatabaseModule_Companion_ProvideDatabaseFactory implements Factory<AppDatabase> {
  private final Provider<Context> contextProvider;

  private DatabaseModule_Companion_ProvideDatabaseFactory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public AppDatabase get() {
    return provideDatabase(contextProvider.get());
  }

  public static DatabaseModule_Companion_ProvideDatabaseFactory create(
      Provider<Context> contextProvider) {
    return new DatabaseModule_Companion_ProvideDatabaseFactory(contextProvider);
  }

  public static AppDatabase provideDatabase(Context context) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.Companion.provideDatabase(context));
  }
}
