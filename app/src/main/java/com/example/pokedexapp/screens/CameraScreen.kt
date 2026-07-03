package com.example.pokedexapp.screens

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.pokedexapp.api.GeminiService
import com.example.pokedexapp.api.PokeApiService
import com.example.pokedexapp.api.PokemonDetails
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.Executor

class CameraViewModel : ViewModel() {
    private val geminiService = GeminiService()
    private val pokeApiService = PokeApiService.create()

    var scanResult by mutableStateOf<String?>(null)
    var pokemonDetails by mutableStateOf<PokemonDetails?>(null)
    var isProcessing by mutableStateOf(false)

    fun saveToPokedex(onSuccess: () -> Unit) {
        val details = pokemonDetails ?: return
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val db = FirebaseFirestore.getInstance()

        val pokemonData = hashMapOf(
            "name" to details.name,
            "pokedexId" to details.id,
            "height" to details.height,
            "weight" to details.weight,
            "imageUrl" to details.sprites.front_default,
            "capturedAt" to System.currentTimeMillis()
        )

        db.collection("users").document(userId)
            .collection("collected")
            .document(details.name)
            .set(pokemonData)
            .addOnSuccessListener {
                onSuccess()
            }
    }

    fun processCapturedPhoto(path: String) {
        viewModelScope.launch {
            isProcessing = true
            val result = geminiService.identifyFromPath(path)
            
            if (result != null && !result.startsWith("error:")) {
                scanResult = result
                try {
                    pokemonDetails = pokeApiService.getPokemonDetails(result)
                } catch (e: Exception) {
                    Log.e("CameraViewModel", "Error fetching details for $result", e)
                }
            } else {
                // If it starts with error:, we display the explanation
                scanResult = result ?: "error:Something went wrong with the identification process."
            }
            
            val file = File(path)
            if (file.exists()) {
                file.delete()
            }

            isProcessing = false
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraScreen(navController: NavController, viewModel: CameraViewModel = viewModel()) {
    val cameraPermissionState = rememberPermissionState(android.Manifest.permission.CAMERA)

    Box(modifier = Modifier.fillMaxSize()) {
        if (cameraPermissionState.status.isGranted) {
            CameraPreview(navController = navController, onPhotoCaptured = { path ->
                viewModel.processCapturedPhoto(path)
            })
        } else {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Button(onClick = { cameraPermissionState.launchPermissionRequest() }) {
                    Text("Grant Camera Permission")
                }
            }
        }

        if (viewModel.isProcessing) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.7f)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = Color.White)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Identifying Pokemon...", color = Color.White)
                }
            }
        }

        viewModel.scanResult?.let { result ->
            val isError = result.startsWith("error:")
            val displayMessage = if (isError) {
                result.removePrefix("error:").replaceFirstChar { it.uppercase() }
            } else {
                "It's a ${result.replaceFirstChar { it.uppercase() }}!"
            }

            AlertDialog(
                onDismissRequest = { viewModel.scanResult = null },
                title = { Text(text = if (!isError) "Pokemon Identified!" else "Scanning Failed") },
                text = {
                    Column {
                        Text(text = displayMessage)
                        if (!isError) {
                            viewModel.pokemonDetails?.let { details ->
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(text = "Pokedex ID: #${details.id}")
                                Text(text = "Height: ${details.height / 10.0} m")
                                Text(text = "Weight: ${details.weight / 10.0} kg")
                            }
                        }
                    }
                },
                confirmButton = {
                    if (!isError) {
                        Button(onClick = {
                            viewModel.saveToPokedex {
                                viewModel.scanResult = null
                                navController.popBackStack()
                            }
                        }) {
                            Text("Add to Pokedex")
                        }
                    } else {
                        Button(onClick = { viewModel.scanResult = null }) {
                            Text("Try Again")
                        }
                    }
                },
                dismissButton = {
                    if (!isError) {
                        TextButton(onClick = { viewModel.scanResult = null }) {
                            Text("Retake")
                        }
                    }
                }
            )
        }
    }
}

@Composable
fun CameraPreview(navController: NavController, onPhotoCaptured: (String) -> Unit) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    
    val previewView = remember { PreviewView(context) }
    val imageCapture = remember { ImageCapture.Builder().build() }
    val mainExecutor = remember { ContextCompat.getMainExecutor(context) }

    LaunchedEffect(cameraProviderFuture) {
        val cameraProvider = cameraProviderFuture.get()
        val preview = Preview.Builder().build().also {
            it.setSurfaceProvider(previewView.surfaceProvider)
        }

        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

        try {
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                preview,
                imageCapture
            )
        } catch (e: Exception) {
            Log.e("CameraPreview", "Use case binding failed", e)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = { previewView },
            modifier = Modifier.fillMaxSize()
        )
        
        Text(
            text = "Scan a Pokemon",
            color = Color.White,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 40.dp)
        )

        IconButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 40.dp, start = 16.dp)
                .background(Color.Black.copy(alpha = 0.5f), CircleShape)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Exit Camera",
                tint = Color.White
            )
        }

        IconButton(
            onClick = {
                takePhoto(
                    imageCapture = imageCapture,
                    context = context,
                    executor = mainExecutor,
                    onPhotoSaved = onPhotoCaptured
                )
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 50.dp)
                .size(80.dp)
                .background(Color.White.copy(alpha = 0.5f), CircleShape)
        ) {
            Icon(
                imageVector = Icons.Default.Camera,
                contentDescription = "Take Photo",
                modifier = Modifier.size(50.dp),
                tint = Color.White
            )
        }
    }
}

private fun takePhoto(
    imageCapture: ImageCapture,
    context: Context,
    executor: Executor,
    onPhotoSaved: (String) -> Unit
) {
    val outputDirectory = getOutputDirectory(context)
    val photoFile = File(
        outputDirectory,
        SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS", Locale.US).format(System.currentTimeMillis()) + ".jpg"
    )

    val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

    imageCapture.takePicture(
        outputOptions,
        executor,
        object : ImageCapture.OnImageSavedCallback {
            override fun onError(exception: ImageCaptureException) {
                Log.e("CameraPreview", "Photo capture failed: ${exception.message}", exception)
                Toast.makeText(context, "Capture Failed", Toast.LENGTH_SHORT).show()
            }

            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                executor.execute {
                    onPhotoSaved(photoFile.absolutePath)
                }
            }
        }
    )
}

private fun getOutputDirectory(context: Context): File {
    val mediaDir = context.externalMediaDirs.firstOrNull()?.let {
        File(it, context.resources.getString(com.example.pokedexapp.R.string.app_name)).apply { mkdirs() }
    }
    return if (mediaDir != null && mediaDir.exists()) mediaDir else context.filesDir
}
