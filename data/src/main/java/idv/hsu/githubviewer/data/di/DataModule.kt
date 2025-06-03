package idv.hsu.githubviewer.data.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import idv.hsu.githubviewer.data.repository.GitHubRepositoryImpl
import idv.hsu.githubviewer.data.source.GitHubRemoteDataSource
import idv.hsu.githubviewer.data.source.remote.GitHubRemoteDataSourceImpl
import idv.hsu.githubviewer.domain.repository.GitHubRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Singleton
    @Binds
    abstract fun bindGitHubRepository(
        githubRepositoryImpl: GitHubRepositoryImpl
    ): GitHubRepository

    @Singleton
    @Binds
    abstract fun bindGitHubRemoteDataSource(
        userDataSourceImpl: GitHubRemoteDataSourceImpl
    ): GitHubRemoteDataSource
}