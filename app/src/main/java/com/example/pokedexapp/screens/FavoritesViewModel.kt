package com.example.pokedexapp.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class FavoritesViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val userId = auth.currentUser?.uid

    var favoritesList by mutableStateOf<List<CollectedPokemon>>(emptyList())
    var isLoading by mutableStateOf(true)

    init {
        loadData()
    }

    private fun loadData() {
        if (userId == null) {
            isLoading = false
            return
        }

        db.collection("users").document(userId)
            .collection("favorites")
            .orderBy("addedAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    favoritesList = snapshot.toObjects(CollectedPokemon::class.java)
                }
                isLoading = false
            }
    }

    fun removeFavorite(name: String) {
        if (userId == null) return
        db.collection("users").document(userId)
            .collection("favorites").document(name).delete()
    }
}
