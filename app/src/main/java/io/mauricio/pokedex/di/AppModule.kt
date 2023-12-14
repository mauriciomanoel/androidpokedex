package io.mauricio.pokedex.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.mauricio.pokedex.network.RetrofitApiService
import io.mauricio.pokedex.repository.PokemonRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun providePokemonRepository(apiService: RetrofitApiService) = PokemonRepository(apiService)

}