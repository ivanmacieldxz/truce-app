package com.ivanmacieldxz.truce.di

import com.ivanmacieldxz.truce.data.repository.AuthRepositoryImpl
import com.ivanmacieldxz.truce.domain.repository.AuthRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindFriendshipsRepository(
        friendshipsRepositoryImpl: com.ivanmacieldxz.truce.data.repository.FriendshipsRepositoryImpl
    ): com.ivanmacieldxz.truce.domain.repository.FriendshipsRepository

    @Binds
    @Singleton
    abstract fun bindUserRepository(
        userRepositoryImpl: com.ivanmacieldxz.truce.data.repository.UserRepositoryImpl
    ): com.ivanmacieldxz.truce.domain.repository.UserRepository
}
