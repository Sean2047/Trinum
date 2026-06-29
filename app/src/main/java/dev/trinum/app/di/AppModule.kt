package dev.trinum.app.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    // Top-level bindings — use-case and feature-level bindings added by TASK-002, TASK-003, TASK-004
}
