package io.mauricio.pokedex.pokemondetail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import io.mauricio.pokedex.data.responses.Pokemon
import io.mauricio.pokedex.ui.theme.PokedexTheme
import io.mauricio.pokedex.util.Resource

@Composable
fun PokemonDetailScreen(
    dominantColor: Color,
    pokemonName: String,
    navController: NavController,
    tapPadding: Dp = 20.dp,
    pokemonImageSize: Dp = 240.dp,
    viewModel: PokemonDetailViewModel = hiltViewModel()
    ) {

    val pokemonInfo = produceState<Resource<Pokemon>>(initialValue = Resource.Loading()) {
        value = viewModel.getPokemonInfo(pokemonName)
    }

    Box(modifier = Modifier
        .fillMaxSize()
        .background(dominantColor)
        .padding(bottom = 16.dp)) {

        PokemonDetailTopSection(
            navController,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.2f)
                .align(Alignment.TopStart))

        PokemonDetailStateWrapper(
            pokemonInfo = pokemonInfo.value,
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = tapPadding + pokemonImageSize / 2f,
                    start = 16.dp,
                    end = 16.dp,
                    bottom = 16.dp
                )
                .shadow(10.dp, RoundedCornerShape(10.dp))
                .clip(RoundedCornerShape(10.dp))
                .padding(16.dp)
                .align(Alignment.BottomCenter)
        )
        Box(contentAlignment = Alignment.TopCenter,
            modifier = Modifier
                .fillMaxSize()) {

            if (pokemonInfo.value is Resource.Success) {

                pokemonInfo.value.data?.sprites?.let {
                    SubcomposeAsyncImage(
                        loading = {
                            CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.scale(0.5f)
                            )
                        },
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(it.front_default)
                            .crossfade(true)
                            .build(),
                        modifier = Modifier
                            .size(pokemonImageSize)
                            .offset(y= tapPadding),
                        contentDescription = pokemonName
                    )
                }
            }
        }
    }
}

@Composable
fun PokemonDetailTopSection(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.TopStart,
        modifier = modifier
            .background(
                Brush.verticalGradient(
                    listOf(Color.Black, Color.Transparent)
                )
            )
    ) {

        Icon(
            imageVector = Icons.Default.ArrowBack,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier
                .size(36.dp)
                .offset(16.dp, 16.dp)
                .clickable {
                    navController.popBackStack()
                }
            )
    }
}

@Preview
@Composable
fun PreviewPokemonDetailTopSection() {
    val navController = NavController(LocalContext.current)
    PokedexTheme {
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 16.dp)
            )
        {

            PokemonDetailTopSection(
                navController,
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.2f)
                    .align(Alignment.TopCenter)
            )
        }
    }



}

@Composable
fun PokemonDetailStateWrapper(
    pokemonInfo: Resource<Pokemon>,
    modifier: Modifier = Modifier,
    loadingModifier: Modifier = Modifier) {

    when(pokemonInfo) {
        is Resource.Success -> {

        }
        is Resource.Error -> {
            Text(
                text= pokemonInfo.message!!,
                color = Color.Red,
                modifier = modifier
            )
        }
        is Resource.Loading -> {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.primary,
                modifier = loadingModifier
            )
        }
    }
}