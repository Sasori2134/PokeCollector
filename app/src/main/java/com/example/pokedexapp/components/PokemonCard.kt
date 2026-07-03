package com.example.pokedexapp.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.outlined.CatchingPokemon
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import coil.compose.AsyncImage

@Composable
fun PokemonCard(
    name: String,
    imageUrl: String?,
    id: Int? = null,
    isFavorite: Boolean = false,
    onFavoriteClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(0.9f),
        shape = RoundedCornerShape(15.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            IconButton(
                onClick = onFavoriteClick,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .zIndex(1f)
                    .padding(4.dp)
            ) {
                Icon(
                    imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Favorite",
                    tint = if (isFavorite) Color(0xFFFF69B4) else Color.Gray
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .background(Color(0xFFF2F2F2), RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    if (imageUrl != null) {
                        AsyncImage(
                            model = imageUrl,
                            contentDescription = name,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(8.dp),
                            contentScale = ContentScale.Fit
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Outlined.CatchingPokemon,
                            contentDescription = null,
                            modifier = Modifier.size(60.dp),
                            tint = Color(0xFF117A71)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = name.replaceFirstChar { it.uppercase() },
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color.Black
                )

                if (id != null) {
                    Text(
                        text = "#${id.toString().padStart(3, '0')}",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}
