package io.mauricio.pokedex.repository

import androidx.compose.ui.text.toLowerCase
import dagger.hilt.android.scopes.ActivityScoped
import io.mauricio.pokedex.data.responses.Pokemon
import io.mauricio.pokedex.data.responses.PokemonList
import io.mauricio.pokedex.network.RetrofitApiService
import io.mauricio.pokedex.util.Resource
import java.util.Locale
import javax.inject.Inject

@ActivityScoped
class PokemonRepository @Inject constructor(private val apiService: RetrofitApiService)  {

    suspend fun getPokemonList(limit: Int, offset: Int): Resource<PokemonList> {
        val response = try {
            apiService.getPokemons(limit, offset)
        } catch (e: Exception) {
            return Resource.Error("An unknown error occured.")
        }
        return Resource.Success(response)
    }

    suspend fun getPokemonInfo(name: String): Resource<Pokemon> {
        val response = try {
            apiService.getPokemonInfo(name.lowercase(Locale.getDefault()))
        } catch (e: Exception) {
            return Resource.Error("An unknown error occured.")
        }
        return Resource.Success(response)
    }
}