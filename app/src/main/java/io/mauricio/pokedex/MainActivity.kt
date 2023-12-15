package io.mauricio.pokedex

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import dagger.hilt.android.AndroidEntryPoint
import io.mauricio.pokedex.pokemondetail.PokemonDetailScreen
import io.mauricio.pokedex.pokemonlist.PokemonListScreen
import io.mauricio.pokedex.repository.PokemonRepository
import io.mauricio.pokedex.ui.theme.PokedexTheme
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var repository: PokemonRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            PokedexTheme {

                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "pokemon_list_screen") {
                    composable(route = "pokemon_list_screen") {
                        PokemonListScreen(navController = navController)
                    }
                    composable(route = "pokemon_detail_screen/{dominantColor}/{pokemonName}", arguments = listOf(
                        navArgument("dominantColor") {
                            type = NavType.IntType
                        },
                        navArgument("pokemonName") {
                            type = NavType.StringType
                        }
                    )) {

                        val dominantColor = remember {
                            val color = it.arguments?.getInt("dominantColor")
                            color?.let { Color(it) } ?: Color.White
                        }

                        val pokemonName = remember {
                            it.arguments?.getString("pokemonName")
                        }

                        PokemonDetailScreen(dominantColor= dominantColor, pokemonName= pokemonName ?: "", navController= navController)
                    }
                }

            }
        }
    }
}