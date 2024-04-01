package com.engineerfred.kotlin.next.presentation.screens.authGraphScreens.loginScreen

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Face
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.engineerfred.kotlin.next.core.SetSystemBarColor
import com.engineerfred.kotlin.next.presentation.screens.authGraphScreens.loginScreen.components.LoginContainer
import com.engineerfred.kotlin.next.presentation.theme.CrimsonRed
import com.engineerfred.kotlin.next.presentation.theme.WhiteSmoke

@Composable
fun LoginScreen(
    onCreateAccount: () -> Unit,
    viewModel: LoginViewModel,
    isDarkTheme: Boolean
) {

    val uiState = viewModel.uiState.collectAsState().value
    val context = LocalContext.current
    val boxBg = if (isDarkTheme) MaterialTheme.colorScheme.background else Color.White

    SetSystemBarColor(color = MaterialTheme.colorScheme.surface, isDarkTheme)

    when {
        uiState.isInitializing && uiState.initError.isNullOrEmpty()-> {
            Box(modifier = Modifier
                .fillMaxSize()
                .background(boxBg), contentAlignment = Alignment.Center){
                if ( !isDarkTheme ) {
                    val view = LocalView.current
                    LaunchedEffect(key1 = Unit) {
                        val window = (view.context as Activity).window
                        window.statusBarColor = Color.White.toArgb()
                        window.navigationBarColor = Color.White.toArgb()
                    }
                }
                Text(text = "Just a moment...", fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = if ( isDarkTheme ) WhiteSmoke else CrimsonRed)
            }
        }

        !uiState.isInitializing && !uiState.initError.isNullOrEmpty() -> {
            Box(modifier = Modifier
                .fillMaxSize()
                .background(boxBg), contentAlignment = Alignment.Center){
                if ( !isDarkTheme ) {
                    val view = LocalView.current
                    LaunchedEffect(key1 = Unit) {
                        val window = (view.context as Activity).window
                        window.statusBarColor = Color.White.toArgb()
                        window.navigationBarColor = Color.White.toArgb()
                    }
                }
                Text(text = uiState.initError, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = Color.Red)
            }
        }

        else -> {

        Column(modifier = Modifier
            .fillMaxSize()
            .background(boxBg)
            .padding(14.dp)
            .verticalScroll(rememberScrollState())) {
            Spacer(modifier = Modifier.size(40.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ){
                Icon(
                    imageVector = Icons.Rounded.Face,
                    contentDescription = null,
                    Modifier
                        .size(111.dp),
                    tint = if ( !isDarkTheme ) CrimsonRed else Color.White
                )
            }
            Spacer(modifier = Modifier.size(55.dp))
            LoginContainer(
                emailValue = { uiState.emailTextValue },
                passwordValue = { uiState.passwordTextValue },
                onLogin = { viewModel.onEvent( LoginUiEvents.LoginButtonClicked ) },
                onForgotPassword = {
                    Toast.makeText(context, "This feature is not yet implemented!", Toast.LENGTH_SHORT ).show()
                },
                loggingInProgress = { uiState.isLoggingIn },
                onEmailValueChanged = { viewModel.onEvent( LoginUiEvents.EmailChanged(it) ) },
                onPasswordValueChanged = { viewModel.onEvent( LoginUiEvents.PasswordChanged(it) ) },
                isDarkTheme = isDarkTheme
            )

            if ( !uiState.loginError.isNullOrEmpty()) {
                Spacer(modifier = Modifier.size(55.dp))
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center){
                    Text(text = uiState.loginError, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = Color.Red)
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            OutlinedButton(
                onClick = { onCreateAccount() },
                enabled = !uiState.isLoggingIn,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(text = "Create new account", fontWeight = FontWeight.SemiBold, color = if ( isDarkTheme ) Color.White else MaterialTheme.colorScheme.primary)
            }
        }
        }
    }

}