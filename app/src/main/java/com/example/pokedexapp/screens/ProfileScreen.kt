package com.example.pokedexapp.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.pokedexapp.navigation.Screen
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun ProfileScreen(navController: NavController) {
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val context = LocalContext.current
    
    var username by remember { mutableStateOf("Loading...") }
    var email by remember { mutableStateOf(auth.currentUser?.email ?: "No Email") }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            db.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    username = document.getString("username") ?: "Unknown User"
                    isLoading = false
                }
                .addOnFailureListener {
                    username = "Error loading"
                    isLoading = false
                }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE6F9F9))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Profile",
            color = Color(0xFF117A71),
            fontWeight = FontWeight.Bold,
            fontSize = 30.sp,
            modifier = Modifier.padding(top = 20.dp)
        )

        Spacer(modifier = Modifier.height(40.dp))

        Box(
            modifier = Modifier
                .size(120.dp)
                .background(Color.White, CircleShape)
                .clip(CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = Color(0xFF117A71)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(15.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                InfoRow(label = "Username", value = username)
                HorizontalDivider(color = Color(0xFFF2F2F2))
                InfoRow(label = "Email", value = email)
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {
                auth.signOut()
                navController.navigate(Screen.Login.route) {
                    popUpTo(0) { inclusive = true }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF117A71)),
            shape = RoundedCornerShape(15.dp)
        ) {
            Icon(Icons.Default.Logout, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Logout", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
        
        Spacer(modifier = Modifier.height(140.dp))
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Column {
        Text(text = label, color = Color.Gray, fontSize = 14.sp)
        Text(text = value, fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = Color.Black)
    }
}
