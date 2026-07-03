package com.example.pokedexapp.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

data class PokemonDetails(
    val name: String,
    val id: Int,
    val height: Int,
    val weight: Int,
    val sprites: Sprites
)

data class Sprites(
    val front_default: String?
)

interface PokeApiService {
    @GET("pokemon/{name}")
    suspend fun getPokemonDetails(@Path("name") name: String): PokemonDetails

    companion object {
        private const val BASE_URL = "https://pokeapi.co/api/v2/"

        fun create(): PokeApiService {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(PokeApiService::class.java)
        }
    }
}
