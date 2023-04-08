package com.indexdev.partnerin.di

import com.indexdev.partnerin.data.Repository
import com.indexdev.partnerin.data.api.ApiHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
object RepositoryModule {
    @ViewModelScoped
    @Provides
    fun provideRepository(
        apiHelper: ApiHelper
    ) = Repository(apiHelper)
}