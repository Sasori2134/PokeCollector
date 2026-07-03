package com.example.pokedexapp.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pokedexapp.components.PokemonCard

data class CollectedPokemon(
    val name: String = "",
    val pokedexId: Int = 0,
    val imageUrl: String? = null
)

@Composable
fun CollectedScreen(viewModel: CollectedViewModel = viewModel()) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE6F9F9))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Collected",
            color = Color(0xFF117A71),
            fontWeight = FontWeight.Bold,
            fontSize = 30.sp,
            modifier = Modifier.padding(top = 20.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (viewModel.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFF117A71))
            }
        } else if (viewModel.collectedList.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "No Pokemon collected yet!", color = Color.Gray)
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                items(viewModel.collectedList) { pokemon ->
                    PokemonCard(
                        name = pokemon.name,
                        imageUrl = pokemon.imageUrl,
                        id = pokemon.pokedexId,
                        isFavorite = viewModel.favoritesSet.contains(pokemon.name),
                        onFavoriteClick = { viewModel.toggleFavorite(pokemon) }
                    )
                }
            }
        }
    }
}
