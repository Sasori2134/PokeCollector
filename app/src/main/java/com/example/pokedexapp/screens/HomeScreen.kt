package com.example.pokedexapp.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.pokedexapp.components.PokemonCard
import com.example.pokedexapp.navigation.Screen

@Composable
fun HomeScreen(navController: NavController, viewModel: HomeViewModel = viewModel()) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFE6F9F9))
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Pokecollector",
                color = Color(0xFF117A71),
                fontWeight = FontWeight.Bold,
                fontSize = 30.sp,
                modifier = Modifier.padding(top = 20.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatBox(
                    label = "Pokemons discovered",
                    value = viewModel.collectedCount.toString(),
                    modifier = Modifier.weight(1f)
                )
                StatBox(
                    label = "Favorite pokemon count",
                    value = viewModel.favoriteCount.toString(),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Recently Caught",
                color = Color(0xFF117A71),
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (viewModel.recentPokemons.isEmpty()) {
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    Text(text = "Start exploring to catch Pokemons!", color = Color.Gray)
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(viewModel.recentPokemons) { pokemon ->
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

        FloatingActionButton(
            onClick = { navController.navigate(Screen.Camera.route) },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 130.dp, end = 24.dp),
            containerColor = Color(0xFF117A71),
            contentColor = Color.White,
            shape = CircleShape
        ) {
            Icon(
                imageVector = Icons.Default.CameraAlt,
                contentDescription = "Open Camera",
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun StatBox(label: String, value: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .border(2.dp, Color.Black, RoundedCornerShape(10.dp))
            .background(Color.White, RoundedCornerShape(10.dp))
            .padding(vertical = 20.dp, horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
            lineHeight = 16.sp,
            minLines = 2
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = Color(0xFF117A71)
        )
    }
}
