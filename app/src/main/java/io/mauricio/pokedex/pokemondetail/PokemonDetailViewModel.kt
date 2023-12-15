package io.mauricio.pokedex.pokemondetail

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.mauricio.pokedex.data.responses.Pokemon
import io.mauricio.pokedex.repository.PokemonRepository
import io.mauricio.pokedex.util.Resource
import javax.inject.Inject

@HiltViewModel
class PokemonDetailViewModel @Inject constructor(
    private val repository: PokemonRepository
): ViewModel() {

    suspend fun getPokemonInfo(namePokemon: String): Resource<Pokemon> {
        return repository.getPokemonInfo(namePokemon)
    }
}