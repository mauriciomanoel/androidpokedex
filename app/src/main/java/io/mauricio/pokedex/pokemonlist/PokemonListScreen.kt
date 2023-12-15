package io.mauricio.pokedex.pokemonlist

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.FocusInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import io.mauricio.pokedex.R
import io.mauricio.pokedex.ui.theme.PokedexTheme

@Composable
fun PokemonListScreen(
    navController: NavController,
    viewModel: PokemonListViewModel = hiltViewModel()
) {
    Surface(
        color = MaterialTheme.colorScheme.background,
        modifier = Modifier.fillMaxSize()
    ) {
        Column {
            Spacer(Modifier.height(20.dp))
            Image(
                painter = painterResource(R.drawable.ic_international_pok_mon_logo)
            , contentDescription = "Pokemon"
            , modifier = Modifier
                .fillMaxWidth()
                .align(CenterHorizontally)
            )
            SearchBar(modifier = Modifier.fillMaxWidth().padding(10.dp), "Search...") {
                viewModel.seachPokemonList(it)
            }
            PokemonList(navController)
        }
    }
}

@Composable
fun SearchBar(
    modifier: Modifier,
    hint: String = "",
    onSearch: (String) -> Unit = {}
) {
    var text by rememberSaveable { mutableStateOf("") }
    var isHintDisplayed by rememberSaveable { mutableStateOf(hint != "") }

    Box(modifier = modifier) {
        BasicTextField(
            value = text,
            onValueChange = {
                text = it
                onSearch(it)
            },
            maxLines = 1,
            singleLine = true,
            textStyle = TextStyle(color = Color.Black),
            modifier = Modifier
                .fillMaxWidth()
                .shadow(5.dp, CircleShape)
                .background(Color.White, CircleShape)
                .padding(horizontal = 20.dp, vertical = 12.dp)
                .onFocusChanged {
                    isHintDisplayed = !it.isFocused
                }
        )

        if (!text.isNullOrEmpty()) {
            isHintDisplayed = false
        }

        if(isHintDisplayed) {
            Text(
                text = hint,
                color = Color.LightGray,
                modifier = Modifier
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            )
        }
    }
}

@Composable
fun PokemonList (
    navController: NavController,
    viewModel: PokemonListViewModel = hiltViewModel()
) {

    val pokemonList by remember { viewModel.pokemonList }
    val endReached by remember { viewModel.endReached }
    val loadError by remember { viewModel.loadError }
    val isLoading by remember { viewModel.isLoading }
    val isSearching by remember { viewModel.isSearching }

    LazyColumn(
        contentPadding = PaddingValues(10.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        val itemCount = if (pokemonList.size % 2 == 0) {
            pokemonList.size / 2
        } else {
            pokemonList.size / 2 + 1
        }
        items(itemCount) {
            if (it >= itemCount - 1 && !endReached && !isLoading && !isSearching) {
                viewModel.loadPokemonPaginaed()
            }
            PokemonRow(rowIndex = it, entries = pokemonList, navController = navController)
        }
    }

    Box(contentAlignment = Center,
        modifier = Modifier.fillMaxSize()
    ) {

        if (isLoading) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        }

        if (loadError.isNotEmpty()) {
            RetrySection(error = loadError) {
                viewModel.loadPokemonPaginaed()
            }
        }

    }

}

@Composable
fun RetrySection(
    error: String,
    onRetry: () -> Unit = {}
) {
    Column {
        Text(error, color= Color.Red, fontSize = 18.sp)

        Spacer(Modifier.height(10.dp))
        
        Button(
            onClick = { onRetry() },
            modifier = Modifier.align(CenterHorizontally)
        ) {
            Text(text = "Retry")
        }
    }
}


