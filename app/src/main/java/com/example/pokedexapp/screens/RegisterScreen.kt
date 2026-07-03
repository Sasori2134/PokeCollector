package com.example.pokedexapp.screens

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.pokedexapp.navigation.Screen
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun RegisterScreen(navController: NavController) {
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Create Account",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF117A71)
        )
        
        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        )

        Spacer(modifier = Modifier.height(32.dp))

        if (isLoading) {
            CircularProgressIndicator(color = Color(0xFF117A71))
        } else {
            Button(
                onClick = {
                    if (email.isNotEmpty() && password.isNotEmpty() && username.isNotEmpty()) {
                        isLoading = true
                        auth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    val userId = auth.currentUser?.uid
                                    val user = hashMapOf(
                                        "username" to username,
                                        "email" to email
                                    )
                                    if (userId != null) {
                                        db.collection("users").document(userId)
                                            .set(user)
                                            .addOnSuccessListener {
                                                isLoading = false
                                                navController.navigate(Screen.Home.route) {
                                                    popUpTo(Screen.Login.route) { inclusive = true }
                                                }
                                            }
                                            .addOnFailureListener { e ->
                                                isLoading = false
                                                Toast.makeText(context, "Error saving profile: ${e.message}", Toast.LENGTH_SHORT).show()
                                            }
                                    }
                                } else {
                                    isLoading = false
                                    Toast.makeText(context, "Registration failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                                }
                            }
                    } else {
                        Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Register")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Already have an account? Login",
            modifier = Modifier.clickable {
                navController.popBackStack()
            },
            color = Color(0xFF117A71)
        )
    }
}
