package io.mauricio.pokedex.pokemonlist

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.capitalize
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.palette.graphics.Palette
import dagger.hilt.android.lifecycle.HiltViewModel
import io.mauricio.pokedex.data.models.PokemonListEntry
import io.mauricio.pokedex.repository.PokemonRepository
import io.mauricio.pokedex.util.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class PokemonListViewModel @Inject constructor(
    private val repository: PokemonRepository
): ViewModel() {

    private var PAGE_SIZE = 20
    private var currentPage = 0
    private var isSearchStarting = true

    var pokemonList = mutableStateOf<List<PokemonListEntry>>(listOf())
    private var cachedPokemonList = listOf<PokemonListEntry>()

    var loadError = mutableStateOf("")
    var isLoading = mutableStateOf(false)
    var endReached = mutableStateOf(false)
    var isSearching = mutableStateOf(false)

    init {
        loadPokemonPaginaed()
    }

    fun loadPokemonPaginaed() {
        viewModelScope.launch {
            isLoading.value = true
            var result = repository.getPokemonList(PAGE_SIZE, currentPage * PAGE_SIZE)
            when(result) {
                is Resource.Success -> {
                    endReached.value = currentPage * PAGE_SIZE >= result.data!!.count

                    val pokemonEntries = result.data!!.results!!.mapIndexed { index, entry ->
                        val number = if (entry.url.endsWith("/")) {
                            entry.url.dropLast(1).takeLastWhile { it.isDigit() }
                        } else {
                            entry.url.takeLastWhile { it.isDigit() }
                        }
                        val url = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/${number}.png"
                        PokemonListEntry(entry.name.replaceFirstChar {
                            if (it.isLowerCase()) it.titlecase(
                                Locale.ROOT
                            ) else it.toString()
                        }, url, number.toInt())
                    }
                    currentPage++

                    loadError.value = ""
                    isLoading.value = false
                    pokemonList.value += pokemonEntries
                }
                is Resource.Loading -> {
                    isLoading.value = true
                }
                is Resource.Error -> {
                    loadError.value = result.message!!
                    isLoading.value = false
                }
            }
        }

    }

    fun seachPokemonList(query: String) {
        val listToSearch = if (isSearchStarting) {
            pokemonList.value
        } else {
            cachedPokemonList
        }

        viewModelScope.launch(Dispatchers.Default) {
            if (query.isEmpty()) {
                pokemonList.value = cachedPokemonList
                isSearching.value = false
                isSearchStarting = true
                return@launch
            }

            val results = listToSearch.filter {
                it.pokemonName.contains(query.trim(), ignoreCase = true) || it.number.toString() == query.trim()
            }

            if (isSearchStarting) {
                cachedPokemonList = pokemonList.value
                isSearchStarting = false
            }
            pokemonList.value = results
            isSearching.value = true
        }
    }
    fun calcDominantColor(drawable: Drawable, onFinish: (Color) -> Unit) {
        val bmp = (drawable as BitmapDrawable).bitmap.copy(Bitmap.Config.ARGB_8888, true)
        Palette.from(bmp).generate { palette ->
            palette?.dominantSwatch?.rgb?.let { colorValue ->
                onFinish(Color(colorValue))
            }
        }
    }
}