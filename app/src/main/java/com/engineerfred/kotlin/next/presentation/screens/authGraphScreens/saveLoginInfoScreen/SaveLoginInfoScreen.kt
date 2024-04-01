package com.engineerfred.kotlin.next.presentation.screens.authGraphScreens.saveLoginInfoScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.engineerfred.kotlin.next.core.SetSystemBarColor
import com.engineerfred.kotlin.next.presentation.common.CommonViewModel
import com.engineerfred.kotlin.next.presentation.theme.Charcoal
import com.engineerfred.kotlin.next.presentation.theme.CrimsonRed

@Composable
fun SaveLoginInfoScreen(
    lastName: String,
    firstName: String,
    email: String,
    password: String,
    onBackClicked: () -> Unit,
    onAlreadyHaveAccountClicked: () -> Unit,
    viewModel: SaveInfoViewModel = hiltViewModel(),
    commonViewModel: CommonViewModel,
    onRegistrationSuccessful: () -> Unit,
    isDarkTheme: Boolean
) {

    val bgColor = if (isDarkTheme) MaterialTheme.colorScheme.background else Color.White
    val textColor = if (isDarkTheme) Color.White else Color.Black
    val cursorColor = if ( isDarkTheme ) Color.White else CrimsonRed

    SetSystemBarColor(color = MaterialTheme.colorScheme.surface, isDarkTheme)

    val uiState = viewModel.uiState.collectAsState().value

    if ( uiState.user != null ) {
        commonViewModel.addUser( user = uiState.user )
        onRegistrationSuccessful.invoke()
    }

    when{
        uiState.isAddingUserInDatabase -> {
            Box(modifier = Modifier.fillMaxSize().background(bgColor), contentAlignment = Alignment.Center){
                Text(text = "Please wait...", fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = textColor)
            }
        }
        else -> {
            Column (
                Modifier
                    .fillMaxSize()
                    .background(bgColor)
                    .verticalScroll(rememberScrollState())
                    .padding(10.dp)
            ){

                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.TopStart) {
                    IconButton(onClick = { onBackClicked.invoke() }) {
                        Icon(imageVector = Icons.Rounded.ArrowBack, contentDescription = null, tint = textColor)
                    }
                }
                Spacer(modifier = Modifier.size(11.dp))
                Text(text = "Save your login info?", fontWeight = FontWeight.ExtraBold, fontSize = 22.sp, color = textColor)
                Text(text = "We'll save the login info for $firstName $lastName, so you won't need to enter it next time you log in.", color = textColor)
                Spacer(modifier = Modifier.size(11.dp))
                Button(
                    onClick = {
                        viewModel.onEvent( SaveInfoUiEvents.SaveClicked )
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    enabled = email.isNotEmpty() && lastName.isNotEmpty() && firstName.isNotEmpty() && password.isNotEmpty() && !uiState.isRegisteringUser && !uiState.isSavingInfoAndRegisteringUser,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isDarkTheme) Charcoal else CrimsonRed,
                        disabledContentColor = Color.White,
                        contentColor = Color.White
                    )
                ) {
                    if ( uiState.isSavingInfoAndRegisteringUser ) {
                        CircularProgressIndicator( color = Color.White, modifier = Modifier.size(22.dp) )
                    }else {
                        Text(text = "Save", fontWeight = FontWeight.SemiBold)
                    }
                }
                Spacer(modifier = Modifier.size(11.dp))
                OutlinedButton(
                    onClick = { viewModel.onEvent( SaveInfoUiEvents.NotNowClicked ) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = email.isNotEmpty() && lastName.isNotEmpty() && firstName.isNotEmpty() && password.isNotEmpty() && !uiState.isRegisteringUser && !uiState.isSavingInfoAndRegisteringUser
                ) {
                    if ( uiState.isRegisteringUser ) {
                        Text(text = "...", fontWeight = FontWeight.SemiBold, color = cursorColor)
                    }else {
                        Text(text = "Not now", fontWeight = FontWeight.SemiBold, color = cursorColor)
                    }
                }

                if ( !uiState.error.isNullOrEmpty() ) {
                    Spacer(modifier = Modifier.size(30.dp))
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center){
                        Text(text = uiState.error, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = Color.Red)
                    }
                }

                Spacer(modifier = Modifier.weight(1f))
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text(
                        modifier = Modifier.clickable {
                            if ( !uiState.isRegisteringUser && !uiState.isSavingInfoAndRegisteringUser ) {
                                onAlreadyHaveAccountClicked.invoke()
                            }
                        },
                        text = "Already have an account?.",
                        color = textColor,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}