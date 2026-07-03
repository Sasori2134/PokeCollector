package com.example.pokedexapp.api

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.example.pokedexapp.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class GeminiService {
    private val apiKey = BuildConfig.GEMINI_API_KEY
    
    private val model = GenerativeModel(
        modelName = "gemini-2.5-flash",
        apiKey = apiKey
    )

    suspend fun identifyPokemon(bitmap: Bitmap): String? = withContext(Dispatchers.IO) {
        try {
            val inputContent = content {
                image(bitmap)
                text("Identify the Pokemon in this image. If a Pokemon is found, return ONLY its name in lowercase. If no Pokemon is found or the image is too blurry/unclear, start your response with 'Error:' followed by a brief explanation of what went wrong.")
            }
            
            val response = model.generateContent(inputContent)
            response.text?.trim()?.lowercase()
        } catch (e: Exception) {
            Log.e("GeminiService", "API Error: ${e.message}", e)
            val errorType = e.javaClass.simpleName
            val errorMessage = e.message ?: "No detail message available"
            "error:[$errorType] $errorMessage"
        }
    }

    suspend fun identifyFromPath(path: String): String? = withContext(Dispatchers.IO) {
        val file = File(path)
        if (!file.exists()) return@withContext null
        
        val bitmap = BitmapFactory.decodeFile(path) ?: return@withContext null
        identifyPokemon(bitmap)
    }
}
