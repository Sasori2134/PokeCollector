package com.example.pokedexapp.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class HomeViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val userId = auth.currentUser?.uid

    var collectedCount by mutableIntStateOf(0)
    var favoriteCount by mutableIntStateOf(0)
    var recentPokemons by mutableStateOf<List<CollectedPokemon>>(emptyList())
    var favoritesSet by mutableStateOf<Set<String>>(emptySet())

    init {
        loadData()
    }

    private fun loadData() {
        if (userId == null) return

        db.collection("users").document(userId)
            .collection("collected")
            .orderBy("capturedAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    collectedCount = snapshot.size()
                    recentPokemons = snapshot.toObjects(CollectedPokemon::class.java).take(4)
                }
            }

        db.collection("users").document(userId)
            .collection("favorites")
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    favoriteCount = snapshot.size()
                    favoritesSet = snapshot.documents.map { it.id }.toSet()
                }
            }
    }

    fun toggleFavorite(pokemon: CollectedPokemon) {
        if (userId == null) return
        
        val docRef = db.collection("users").document(userId)
            .collection("favorites").document(pokemon.name)
        
        if (favoritesSet.contains(pokemon.name)) {
            docRef.delete()
        } else {
            val data = hashMapOf(
                "name" to pokemon.name,
                "pokedexId" to pokemon.pokedexId,
                "imageUrl" to pokemon.imageUrl,
                "addedAt" to System.currentTimeMillis()
            )
            docRef.set(data)
        }
    }
}
