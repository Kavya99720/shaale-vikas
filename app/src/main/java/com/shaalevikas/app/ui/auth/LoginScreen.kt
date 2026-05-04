package com.shaalevikas.app.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shaalevikas.app.ui.theme.Green700
import com.shaalevikas.app.viewmodel.AuthViewModel

@Composable
fun LoginScreen(
    authViewModel: AuthViewModel,
    onLoginSuccess: (String) -> Unit,
    onNavigateToRegister: () -> Unit,
    onNavigateToForgotPassword: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    val isLoading by authViewModel.isLoading.collectAsState()
    val error by authViewModel.error.collectAsState()

    LaunchedEffect(error) {
        // error is shown inline
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .background(Green700),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("🏫", fontSize = 56.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Shaale-Vikas", color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.Bold)
                Text("Welcome Back", color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
            }
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("Sign In", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Green700)

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it; authViewModel.clearError() },
                    label = { Text("Email") },
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it; authViewModel.clearError() },
                    label = { Text("Password") },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility, contentDescription = null)
                        }
                    },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                error?.let {
                    Text(it, color = MaterialTheme.colorScheme.error, fontSize = 13.sp)
                }

                TextButton(onClick = onNavigateToForgotPassword, modifier = Modifier.align(Alignment.End)) {
                    Text("Forgot Password?", color = Green700)
                }

                Button(
                    onClick = {
                        authViewModel.login(email.trim(), password) { role -> onLoginSuccess(role) }
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    enabled = !isLoading && email.isNotBlank() && password.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(containerColor = Green700)
                ) {
                    if (isLoading) CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                    else Text("Sign In", fontWeight = FontWeight.Bold)
                }

                Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
                    Text("Don't have an account? ", color = Color.Gray)
                    TextButton(onClick = onNavigateToRegister, contentPadding = PaddingValues(0.dp)) {
                        Text("Register", color = Green700, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
