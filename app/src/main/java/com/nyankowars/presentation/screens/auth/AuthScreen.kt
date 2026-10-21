package com.nyankowars.presentation.screens.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.nyankowars.domain.models.Resource
import com.nyankowars.presentation.viewmodels.AuthViewModel

@Composable
fun AuthScreen(viewModel: AuthViewModel) {
    var registerMode by remember { mutableStateOf(false) }
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val loginState by viewModel.loginState.collectAsState()
    val registerState by viewModel.registerState.collectAsState()
    val state = if (registerMode) registerState else loginState

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(if (registerMode) "NyankoWars 新規登録" else "NyankoWars ログイン")
        if (registerMode) {
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("ユーザー名") },
                singleLine = true
            )
        }
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("メール") },
            singleLine = true
        )
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("パスワード") },
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true
        )

        when (val current = state) {
            Resource.Loading -> CircularProgressIndicator()
            is Resource.Error -> Text(current.message)
            else -> Unit
        }

        Button(
            onClick = {
                if (registerMode) viewModel.register(username, email, password)
                else viewModel.login(email, password)
            },
            enabled = state !is Resource.Loading && email.isNotBlank() && password.isNotBlank() && (!registerMode || username.isNotBlank()),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (registerMode) "登録" else "ログイン")
        }
        TextButton(
            onClick = {
                registerMode = !registerMode
                viewModel.clearStates()
            }
        ) {
            Text(if (registerMode) "ログインへ" else "新規登録")
        }
    }
}
