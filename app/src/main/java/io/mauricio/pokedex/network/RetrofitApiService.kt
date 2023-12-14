package io.mauricio.pokedex.network


import io.mauricio.pokedex.data.responses.Pokemon
import io.mauricio.pokedex.data.responses.PokemonList
import retrofit2.http.*

interface RetrofitApiService {

    @GET("pokemon")
    suspend fun getPokemons(@Query("limit") limit: Int,
                    @Query("offset") offset: Int): PokemonList

    @GET("pokemon/{name}")
    suspend fun getPokemonInfo(@Path("name") name: String): Pokemon

}